package L04;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HTTPClient {
    public HTTPClient() {
        System.out.println("HTTP Client Started");
        try {
            InetAddress serverInetAddress = InetAddress.getLoopbackAddress();
            Socket connection = new Socket(serverInetAddress, 8080);
            
            try(OutputStream out = connection.getOutputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()))) {
                // send new diary message
                sendPost(out);
                System.out.println(getResponse(in));
            }
            
            // reopen connection for GET
            connection = new Socket(serverInetAddress, 8080);
            try(OutputStream out = connection.getOutputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()))) {
                // display all diary messages
                sendGet(out);
                System.out.println(getResponse(in));
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(HTTPClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HTTPClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) {
        new HTTPClient();
    }

    private String getResponse(BufferedReader in) {
        try {
            String inputLine;
            StringBuilder response = new StringBuilder();
            while((inputLine = in.readLine()) != null) {
                response.append(inputLine).append("\n");
            }
            return response.toString();
        } catch (IOException ex) {
            Logger.getLogger(HTTPClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    private void sendGet(OutputStream out) {
        try {
            out.write("GET ./diary.txt\r\n".getBytes());
            out.write("User-Agent: Mozilla/5.0\r\n".getBytes());
        } catch (IOException ex) {
            Logger.getLogger(HTTPClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void sendPost(OutputStream out) {
        // get diary entry
        Scanner in = new Scanner(System.in);
        System.out.println("Please enter a diary entry:");
        String entry = in.nextLine();
        
        try {
            out.write("POST ./diary.txt\r\n".getBytes());
            out.write("User-Agent: Mozilla/5.0\r\n".getBytes());
            out.write("Content-Type: text/plain\r\n".getBytes());
            out.write(("Content-Length: " + entry.length() + "\r\n").getBytes());
            out.write("\r\n".getBytes());
            
            // send diary entry
            out.write((entry + "\r\n").getBytes());
        } catch (IOException ex) {
            Logger.getLogger(HTTPClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
