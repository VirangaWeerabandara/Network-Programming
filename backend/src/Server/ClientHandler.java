package Server;

import Service.VersionControlService;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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

            while ((line = input.readLine()) != null && !line.isEmpty()) {
                if (method == null) {
                    String[] parts = line.split(" ");
                    method = parts[0];
                }
                requestBuilder.append(line).append("\n");
            }

            if ("OPTIONS".equals(method)) {
                sendCORSHeaders();
                return;
            }

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
                handleRequest(new String(body));
            }
        } catch (IOException e) {
            System.err.println("Client disconnected: " + socket.getInetAddress());
        } finally {
            closeConnections();
        }
    }

    private void handleRequest(String jsonBody) {
        try {
            JSONObject jsonCommand = new JSONObject(jsonBody);
            String type = jsonCommand.getString("type");
            JSONObject response = new JSONObject();

            switch (type) {
                case "CREATE":
                    String repoName = jsonCommand.getString("repoName");
                    boolean created = versionControlService.createRepository(repoName);
                    response.put("success", created)
                           .put("message", created ? "Repository created successfully" : "Failed to create repository");
                    break;

                case "COMMIT":
                    try {
                        String content = jsonCommand.getString("content");
                        String message = jsonCommand.getString("message");
                        String commitRepoName = jsonCommand.getString("repoName");
                        String branchName = jsonCommand.getString("branchName");

                        boolean committed = versionControlService.commitChangesAsync(commitRepoName, branchName, message, content)
                                                              .get();
                        List<Map<String, String>> history = versionControlService.getCommitHistory(commitRepoName, branchName);

                        response.put("success", committed)
                               .put("message", committed ? "Changes committed successfully" : "Failed to commit")
                               .put("history", history);
                    } catch (InterruptedException | ExecutionException e) {
                        response.put("success", false)
                               .put("message", "Error during commit: " + e.getMessage());
                    }
                    break;

                case "CREATE_BRANCH":
                    try {
                        String createBranchRepoName = jsonCommand.getString("repoName");
                        String newBranchName = jsonCommand.getString("branchName");

                        Map<String, Object> result = versionControlService.createBranchAsync(createBranchRepoName, newBranchName)
                                                                        .get();

                        response.put("success", result.get("success"))
                               .put("message", (Boolean)result.get("success") ? 
                                    "Branch created successfully" : "Failed to create branch")
                               .put("branches", result.get("branches"))
                               .put("content", result.get("content"));
                    } catch (InterruptedException | ExecutionException e) {
                        response.put("success", false)
                               .put("message", "Error creating branch: " + e.getMessage());
                    }
                    break;

                case "PULL":
                    String pullRepoName = jsonCommand.getString("repoName");
                    String pullBranchName = jsonCommand.getString("branchName");
                    String currentContent = versionControlService.pullChanges(pullRepoName, pullBranchName);

                    response.put("success", currentContent != null)
                           .put("content", currentContent != null ? currentContent : "")
                           .put("message", currentContent != null ? 
                                "Successfully pulled changes" : "Failed to pull changes");
                    break;

                case "HISTORY":
                    String historyRepoName = jsonCommand.getString("repoName");
                    String historyBranchName = jsonCommand.getString("branchName");
                    List<Map<String, String>> history = versionControlService.getCommitHistory(historyRepoName, historyBranchName);

                    response.put("success", true)
                           .put("history", history);
                    break;

                case "GET_BRANCHES":
                    String getBranchesRepoName = jsonCommand.getString("repoName");
                    List<String> branches = versionControlService.getBranches(getBranchesRepoName);

                    response.put("success", true)
                           .put("branches", branches);
                    break;

                case "GET_REPOS":
                    List<String> repos = versionControlService.getAllRepositories();
                    response.put("success", true)
                           .put("repositories", repos);
                    break;

                case "GET_COMMIT_CONTENT":
                    String getCommitContentRepoName = jsonCommand.getString("repoName");
                    String commitHash = jsonCommand.getString("hash");
                    String commitBranch = jsonCommand.getString("branchName");
                    String commitContent = versionControlService.getCommitContent(getCommitContentRepoName, commitHash, commitBranch);

                    response.put("success", commitContent != null)
                           .put("content", commitContent != null ? commitContent : "");
                    break;

                case "REVERT":
                    String revertRepoName = jsonCommand.getString("repoName");
                    String hash = jsonCommand.getString("hash");
                    String revertBranch = jsonCommand.getString("branchName");
                    String revertedContent = versionControlService.revertToCommit(revertRepoName, hash, revertBranch);

                    response.put("success", revertedContent != null)
                           .put("content", revertedContent != null ? revertedContent : "")
                           .put("message", revertedContent != null ? 
                                "Successfully reverted to commit" : "Failed to revert to commit");
                    break;

                default:
                    response.put("success", false)
                           .put("message", "Unknown command type: " + type);
                    break;
            }

            sendResponse(response);
        } catch (JSONException e) {
            sendErrorResponse("Invalid JSON format: " + e.getMessage());
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

    private void sendErrorResponse(String message) {
        output.println("HTTP/1.1 400 Bad Request");
        output.println("Access-Control-Allow-Origin: *");
        output.println("Content-Type: application/json");
        output.println();

        JSONObject errorResponse = new JSONObject()
            .put("success", false)
            .put("message", message);
        output.println(errorResponse.toString());
        output.flush();
    }

    private void sendResponse(JSONObject response) {
        output.println("HTTP/1.1 200 OK");
        output.println("Access-Control-Allow-Origin: *");
        output.println("Content-Type: application/json");
        output.println();
        output.println(response.toString());
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