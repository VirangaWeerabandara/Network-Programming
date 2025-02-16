package Server;

import Service.VersionControlService;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private VersionControlService versionControlService;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.versionControlService = new VersionControlService();
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String line;
            StringBuilder requestBuilder = new StringBuilder();
            String method = null;
            String path = null;

            // Read the HTTP request headers
            while ((line = input.readLine()) != null && !line.isEmpty()) {
                if (method == null) {
                    String[] parts = line.split(" ");
                    method = parts[0];
                    path = parts[1];
                }
                requestBuilder.append(line).append("\n");
            }

            // Handle CORS preflight request
            if ("OPTIONS".equals(method)) {
                sendCORSHeaders();
                return;
            }

            // Read the request body for POST requests
            if ("POST".equals(method)) {
                int contentLength = 0;
                String[] headers = requestBuilder.toString().split("\n");
                for (String header : headers) {
                    if (header.startsWith("Content-Length: ")) {
                        contentLength = Integer.parseInt(header.substring(16));
                        break;
                    }
                }

                char[] body = new char[contentLength];
                input.read(body, 0, contentLength);
                String jsonBody = new String(body);

                handleRequest(jsonBody);
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + socket.getInetAddress());
        } finally {
            closeConnections();
        }
    }
    
private void sendCORSHeaders() {
    output.println("HTTP/1.1 200 OK");
    output.println("Access-Control-Allow-Origin: http://localhost:3000");  // Single origin
    output.println("Access-Control-Allow-Methods: POST, GET, OPTIONS");
    output.println("Access-Control-Allow-Headers: Content-Type");
    output.println("Access-Control-Max-Age: 86400");
    output.println();
    output.flush();
}
    
private void handleRequest(String jsonBody) {
    try {
        JSONObject jsonCommand = new JSONObject(jsonBody);
        String type = jsonCommand.getString("type");
        
        output.println("HTTP/1.1 200 OK");
        output.println("Access-Control-Allow-Origin: http://localhost:3000");
        output.println("Content-Type: application/json");
        output.println();

        JSONObject response = new JSONObject();
        
        switch (type) {
            case "CREATE":
                String repoName = jsonCommand.getString("repoName");
                boolean created = versionControlService.createRepository(repoName);
                response.put("success", created)
                    .put("message", created ? "Repository created" : "Failed to create repository");
                break;
                
            case "COMMIT":
                String content = jsonCommand.getString("content");
                String message = jsonCommand.getString("message");
                String commitRepoName = jsonCommand.getString("repoName"); // Get repo name from request
                boolean committed = versionControlService.commitChanges(commitRepoName, message, content);
                response.put("success", committed)
                    .put("message", committed ? "Changes committed" : "Failed to commit");
                break;
                
            case "PULL":
                String pullRepoName = jsonCommand.getString("repoName"); // Get repo name from request
                String currentContent = versionControlService.pullChanges(pullRepoName);
                response.put("success", true)
                    .put("content", currentContent);
                break;

            case "HISTORY":
                String historyRepoName = jsonCommand.getString("repoName"); // Get repo name from request
                List<Map<String, String>> history = versionControlService.getCommitHistory(historyRepoName);
                response.put("success", true)
                    .put("history", history);
                break;

            case "REVERT":
                String revertRepoName = jsonCommand.getString("repoName"); // Get repo name from request
                String hash = jsonCommand.getString("hash");
                String revertedContent = versionControlService.revertToCommit(revertRepoName, hash);
                response.put("success", revertedContent != null)
                    .put("content", revertedContent);
                break;

            case "GET_REPOS":
                List<String> repos = versionControlService.getAllRepositories();
                response.put("success", true)
                    .put("repositories", repos);
                break;
        }
        
        output.println(response.toString());
        output.flush();
    } catch (JSONException e) {
        System.err.println("Invalid JSON received: " + jsonBody);
        sendErrorResponse("Invalid JSON format");
    }
}
    private void sendErrorResponse(String message) {
        output.println("HTTP/1.1 400 Bad Request");
        output.println("Access-Control-Allow-Origin: http://localhost:3000, http://localhost:3001, http://localhost:3002, http://localhost:3003, http://localhost:3004, http://localhost:3005");
        output.println("Content-Type: application/json");
        output.println();
            
        JSONObject errorResponse = new JSONObject()
            .put("success", false)
            .put("message", message);
        output.println(errorResponse.toString());
        output.flush();
    }

    private void closeConnections() {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}