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
            // Read the HTTP request headers
            while ((line = input.readLine()) != null && !line.isEmpty()) {
                if (method == null) {
                    String[] parts = line.split(" ");
                    method = parts[0];
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
        output.println("Access-Control-Allow-Origin: *");  
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
            output.println("Access-Control-Allow-Origin: *");  
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
                    String commitRepoName = jsonCommand.getString("repoName");
                    String branchName = jsonCommand.getString("branchName");
                    boolean committed = versionControlService.commitChanges(commitRepoName, branchName, message, content);
                    response.put("success", committed)
                        .put("message", committed ? 
                            "Changes committed to " + commitRepoName + "/" + branchName : 
                            "Failed to commit");
                    break;
                    
                case "PULL":
                    String pullRepoName = jsonCommand.getString("repoName");
                    String pullBranchName = jsonCommand.getString("branchName"); // Add this line
                    String currentContent = versionControlService.pullChanges(pullRepoName, pullBranchName); // Fix this line
                    if (currentContent != null) {
                        response.put("success", true)
                            .put("content", currentContent);
                    } else {
                        response.put("success", false)
                            .put("message", "Failed to pull from repository: " + pullRepoName + "/" + pullBranchName)
                            .put("content", "");
                    }
                    break;

                case "HISTORY":
                    String historyRepoName = jsonCommand.getString("repoName");
                    String historyBranchName = jsonCommand.getString("branchName");
                    List<Map<String, String>> history = versionControlService.getCommitHistory(historyRepoName, historyBranchName);
                    response.put("success", true)
                        .put("history", history);
                    break;

                case "REVERT":
                    String revertRepoName = jsonCommand.getString("repoName");
                    String hash = jsonCommand.getString("hash");
                    String revertBranch = jsonCommand.getString("branchName");
                    String revertedContent = versionControlService.revertToCommit(revertRepoName, hash, revertBranch);
                    response.put("success", revertedContent != null)
                        .put("content", revertedContent != null ? revertedContent : "");
                    break;
                            
                case "GET_COMMIT_CONTENT":
                    String getCommitContentRepoName = jsonCommand.getString("repoName");
                    String commitHash = jsonCommand.getString("hash");
                    String commitBranch = jsonCommand.getString("branchName");
                    String commitContent = versionControlService.getCommitContent(getCommitContentRepoName, commitHash, commitBranch);
                    response.put("success", commitContent != null)
                        .put("content", commitContent != null ? commitContent : "");
                    break;

                case "GET_REPOS":
                    List<String> repos = versionControlService.getAllRepositories();
                    response.put("success", true)
                        .put("repositories", repos);
                    break;
                
                case "CREATE_BRANCH":
                    String createBranchRepoName = jsonCommand.getString("repoName");
                    String newBranchName = jsonCommand.getString("branchName");  // Changed variable name to avoid conflict
                    boolean branchCreated = versionControlService.createBranch(createBranchRepoName, newBranchName);
                    response.put("success", branchCreated)
                        .put("message", branchCreated ? "Branch created" : "Failed to create branch");
                    break;

                case "GET_BRANCHES":
                    String getBranchesRepoName = jsonCommand.getString("repoName");
                    List<String> branches = versionControlService.getBranches(getBranchesRepoName);
                    response.put("success", true)
                        .put("branches", branches);
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