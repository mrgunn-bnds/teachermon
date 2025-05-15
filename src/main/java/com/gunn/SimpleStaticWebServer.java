package com.gunn;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

public class SimpleStaticWebServer {
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(80); // Tells OS I am a Webserver, at port 80
            System.out.println("Waiting for client..");
            while (true) {
                Socket client = server.accept();
                System.out.println("Someone connected!!");

                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                // GET /index.html HTTP/1.1
                // if
                String line = in.readLine();
                if (line != null && line.startsWith("GET")) {

                    // Parse requested file path
                    String[] parts = line.split(" ");
                    String filePath = "www" + parts[1]; // all html files have to be in this folder
                    System.out.println("file name: " + filePath);
                    String fileType = "text/html";
                    if (filePath.endsWith(".ico")) {
                        fileType = "image/x-icon";
                    } else if (filePath.endsWith(".png")) {
                        fileType = "image/png";
                    } else if (filePath.equals("www/")) {
                        filePath = "www/index.html";
                    }

                    File file = new File(filePath);
                    byte[] contents = Files.readAllBytes(file.toPath());

                    OutputStream out = client.getOutputStream();
                    PrintWriter headerOut = new PrintWriter(out, true);
                    headerOut.println("HTTP/1.1 200 OK");
                    headerOut.println("Content-type:" + fileType);
                    headerOut.println("Content-Length:" + contents.length); // fix some bugs I found with icons and images
                    headerOut.println();

                    out.write(contents);
                }

                client.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
