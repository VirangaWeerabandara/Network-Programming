package Server;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import VersioningSystem.CommitHandler;

public class VCSServer {
    private static final int PORT = 8080;
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(5);

    public static void main(String[] args) {
        CommitHandler.initializeRepo();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("VCS Server running on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
