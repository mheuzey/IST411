package L04;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {
    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("\nClientHandler Started for "  + this.socket);
        handleRequest(this.socket);
        System.out.println("ClientHandler Terminated for " + this.socket + "\n");
    }

    private void handleRequest(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
            String headerLine = in.readLine();
            StringTokenizer tokenizer = new StringTokenizer(headerLine);
            String httpMethod = tokenizer.nextToken();
            if (httpMethod.equals("GET")) {
                System.out.println("Get method processed");
                String diaryAddress = tokenizer.nextToken();
                File diary = new File(diaryAddress);
                if (!diary.exists()) {
                    String response = "Diary does not exist.";
                    sendResponse(socket, 404, response);
                }
                
                StringBuilder responseBuffer = new StringBuilder();
                Scanner scanner = new Scanner(diary);
                while (scanner.hasNext()){
                    responseBuffer.append(scanner.nextLine()).append("\n");
                }
                scanner.close();
                sendResponse(socket, 200, responseBuffer.toString());
            } else if (httpMethod.equals("POST")) {
                System.out.println("Post method processed");
                String diaryAddress = tokenizer.nextToken();
                
                // process headers
                do {
                    tokenizer = new StringTokenizer(in.readLine());
                    if (!tokenizer.hasMoreTokens()) break;  // reached end of headers
                    
                    String header = tokenizer.nextToken();
                    if (header.equals("User-Agent:")) {
                        System.out.println(header + " " + tokenizer.nextToken());
                    } else if (header.equals("Content-Type:")) {
                        System.out.println(header + " " + tokenizer.nextToken());
                    } else if (header.equals("Content-Length:")) {
                        System.out.println(header + " " + tokenizer.nextToken());
                    } else if (header.equals("\r\n")) {
                        break;
                    } else {
                        // should never get here, output data for troubleshooting
                        System.err.println("header:<" + header + ">.");
                    }
                } while (in.ready());
                
                // process message body
                File diary = new File(diaryAddress);
                int responseCode;
                StringBuilder responseBuffer = new StringBuilder();
                if (diary.exists()) {
                    responseCode = 200;
                    responseBuffer.append("Diary entry added.");
                } else {
                    responseCode = 201;
                    responseBuffer.append("Diary created.");
                }
                FileWriter fw = new FileWriter(diary, true);
                
                StringBuilder diaryEntry = new StringBuilder()
                        .append("*****" + new Date() + "*****\r\n")
                        .append(in.readLine() + "\r\n\r\n");
                fw.append(diaryEntry.toString());
                fw.close();
                
                sendResponse(socket, responseCode, responseBuffer.toString());
            } else {
                System.out.println("The HTTP method is not recognized");
                sendResponse(socket, 405, "Method Not Allowed");
            }
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendResponse(Socket socket, int statusCode, String responseString) {
        String statusLine;
        String serverHeader = "Server: Diaryserver\r\n";
        String contentTypeHeader = "Content-Type: text/plain\r\n";
        
        try(DataOutputStream out = new DataOutputStream(socket.getOutputStream());) {
            if(statusCode == 200) {
                statusLine = "HTTP/1.0 200 OK" + "\r\n";
                String contentLengthHeader = "Content-Length: "
                        + responseString.length() + "\r\n";
                out.writeBytes(statusLine);
                out.writeBytes(serverHeader);
                out.writeBytes(contentTypeHeader);
                out.writeBytes(contentLengthHeader);
                out.writeBytes("\r\n");
                out.writeBytes(responseString);
            } else if(statusCode == 201) {
                statusLine = "HTTP/1.0 201 Created" + "\r\n";
                String contentLengthHeader = "Content-Length: "
                        + responseString.length() + "\r\n";
                out.writeBytes(statusLine);
                out.writeBytes(serverHeader);
                out.writeBytes(contentTypeHeader);
                out.writeBytes(contentLengthHeader);
                out.writeBytes("\r\n");
                out.writeBytes(responseString);
            } else if (statusCode == 405) {
                statusLine = "HTTP/1.0 405 Method Not Allowed" + "\r\n";
                out.writeBytes(statusLine);
                out.writeBytes("\r\n");
            } else {
                statusLine = "HTTP/1.0 404 Not Found" + "\r\n";
                out.writeBytes(statusLine);
                out.writeBytes("\r\n"); 
            }
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
