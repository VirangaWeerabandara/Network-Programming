package Server;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import Service.VersionControlService;

public class VCSServer {
    private static final int PORT = 5000;
    private static final int THREAD_POOL_SIZE = 10;
    private final ExecutorService executor;
    private final VersionControlService versionControlService;
    private volatile boolean isRunning;
    private ServerSocket serverSocket;
    
    public VCSServer() {
        this.executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        this.versionControlService = new VersionControlService();
        this.isRunning = true;
    }
    
    public void shutdown() {
        try {
            isRunning = false;
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            
            executor.shutdown();
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
            
            versionControlService.shutdown();
            System.out.println("Server shutdown completed");
        } catch (IOException | InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            System.err.println("Error during shutdown: " + e.getMessage());
        }
    }

    private static String getWiFiIPAddress() throws SocketException {
        var interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            try {
                if (networkInterface.isUp() && 
                    !networkInterface.isLoopback() && 
                    networkInterface.getDisplayName().contains("Wi-Fi")) {
                    var addresses = networkInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress addr = addresses.nextElement();
                        if (addr instanceof Inet4Address) {
                            return addr.getHostAddress();
                        }
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            printNetworkInfo();

            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    if (isRunning) {
                        System.out.println("\nNew Client Connection:");
                        System.out.println("Client IP: " + clientSocket.getInetAddress().getHostAddress());
                        executor.execute(new ClientHandler(clientSocket));
                    } else {
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    if (isRunning) {
                        System.err.println("Error accepting client connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    private void printNetworkInfo() {
        try {
            System.out.println("\nAvailable Network Interfaces:");
            NetworkInterface.getNetworkInterfaces().asIterator().forEachRemaining(networkInterface -> {
                try {
                    if (networkInterface.isUp() && !networkInterface.isLoopback()) {
                        networkInterface.getInetAddresses().asIterator().forEachRemaining(addr -> {
                            if (addr instanceof Inet4Address) {
                                System.out.println(networkInterface.getDisplayName() + ": " + addr.getHostAddress());
                            }
                        });
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            });

            String wifiIP = getWiFiIPAddress();
            System.out.println("\n===========================================");
            System.out.println("VCS Server started on port " + PORT);
            System.out.println("WiFi IP: " + wifiIP);
            System.out.println("Use this IP on other laptops");
            System.out.println("===========================================\n");
        } catch (SocketException e) {
            System.err.println("Error getting network interfaces: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        VCSServer server = new VCSServer();
        
        // Add shutdown hook for graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down server...");
            server.shutdown();
        }));

        server.start();
    }
}