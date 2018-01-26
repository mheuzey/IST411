
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleEchoServer {
    public static void main(String[] args) {
        System.out.println("Simple echo server");
        
        try (ServerSocket serverSocket = new ServerSocket(6000)) {
            System.out.println("Waiting for connection......");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Connected to client");
            
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                        clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(
                        clientSocket.getOutputStream(), true)) {
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    System.out.println("Server: " + inputLine);
                    out.println(inputLine);
                }
            } catch (IOException ex) {
                Logger.getLogger(SimpleEchoServer.class.getName()).log(Level.SEVERE, null, ex);
            } 
        } catch (IOException ex) {
            Logger.getLogger(SimpleEchoServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
