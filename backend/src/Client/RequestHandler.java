package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class RequestHandler {
    private BufferedReader input;
    private PrintWriter output;

    public RequestHandler(BufferedReader input, PrintWriter output) {
        this.input = input;
        this.output = output;
    }

    public void sendRequest(String request) {
        output.println(request);
    }

    public String getResponse() {
        try {
            return input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error reading response";
        }
    }
}
