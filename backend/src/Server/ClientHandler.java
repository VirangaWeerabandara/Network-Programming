package Server;

import java.io.*;
import java.net.*;
import VersioningSystem.CommitHandler;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {

            String request = in.readLine();
            String response;

            if (request.startsWith("COMMIT")) {
                String message = request.substring(7);
                String commitFile = CommitHandler.saveCommit(message);
                response = "Commit saved: " + commitFile;
            } else if (request.equals("HISTORY")) {
                response = CommitHandler.getCommitHistory();
            } else {
                response = "Invalid command";
            }

            out.write(response + "\n");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}