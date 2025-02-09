package Client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class VCSClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            RequestHandler requestHandler = new RequestHandler(input, output);

            System.out.println(requestHandler.getResponse()); // Welcome message

            while (true) {
                System.out.print("> ");
                String command = scanner.nextLine();

                requestHandler.sendRequest(command);

                if (command.equals("EXIT")) break;

                System.out.println("Server: " + requestHandler.getResponse());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
