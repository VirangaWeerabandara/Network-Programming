package Server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    public ClientHandler(Socket socket) {
        this.socket = socket;
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
            output.println("Welcome to Java VCS!");
            String command;
            while ((command = input.readLine()) != null) {
                if (command.startsWith("CREATE_REPO")) {
                    String repoName = command.split(" ")[1];
                    boolean success = RepositoryManager.createRepository(repoName);
                    output.println(success ? "Repository Created" : "Failed to Create Repository");
                } else if (command.startsWith("COMMIT")) {
                    String[] parts = command.split(" ", 3);
                    String repoName = parts[1];
                    String commitMessage = parts[2];
                    boolean success = CommitManager.commit(repoName, commitMessage);
                    output.println(success ? "Commit Successful" : "Commit Failed");
                } else if (command.equals("EXIT")) {
                    break;
                } else {
                    output.println("Unknown Command!");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
