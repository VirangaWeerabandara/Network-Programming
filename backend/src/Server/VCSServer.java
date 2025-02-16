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

public class VCSServer {
    private static final int PORT = 5000;
    private static final int THREAD_POOL_SIZE = 10;
    
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
     public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try {
            // Bind to all network interfaces
            ServerSocket serverSocket = new ServerSocket(PORT);
            
            // Get all IP addresses
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

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("\nNew Client Connection:");
                System.out.println("Client IP: " + clientSocket.getInetAddress().getHostAddress());
                executor.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }
}