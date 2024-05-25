package com.lahiruj;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringTokenizer;

public class SimpleHttpServer {
    public static void main(String[] args) {
        //Define the port to run the server
        int port = 8080;

        //Initialises a server socket listening to a port
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (true) {
                try {
                    //Wait for a client to connect
                    //The Socket object is a fundamental part of network programming. It represents a single connection between a client and a server.
                    //ServerSocket.accept(): This method waits (blocks) until a client connects to the server on the specified port. When a connection is made, it returns a Socket object that represents the client connection.
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected");

                    // Handle the client connection in a separate method
                    handleClientSocket(clientSocket);

                } catch (IOException e) {
                    System.err.println("Error handling client connection: " + e.getMessage());
                }
            }


        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }

    }

    private static void handleClientSocket(Socket clientSocket) {
        //The request is read from the input stream of the socket
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream())) {

            //Read request line
            String requestLine = in.readLine();

            if (requestLine == null && requestLine.isEmpty()) {
                return;
            }

            // Log the request line
            System.out.println("Request: " + requestLine);

            //Parse the request line
            StringTokenizer tokenizer = new StringTokenizer(requestLine);
            String method = tokenizer.nextToken();
            String resource = tokenizer.nextToken();

            switch (method) {
                case "GET" -> handleGetRequest(resource, out);
                default -> sendResponse(out,"HTTP/1.1 405 Method Not Allowed", "text/plain", "Method Not Allowed");
            }

            out.flush();
            clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void handleGetRequest(String resource, PrintWriter out) {
        if (resource.equals("/")) {
            resource = "/index.html";
        }

        Path filePath = Paths.get("webapp", resource);
        if (Files.exists(filePath)) {
            try {
                byte[] fileBytes = Files.readAllBytes(filePath);
                String mimeType = Files.probeContentType(filePath);
                sendResponse(out, "HTTP/1.1 200 OK", mimeType, new String(fileBytes));
            } catch (IOException e) {
                e.printStackTrace();
                sendResponse(out, "HTTP/1.1 500 Internal Server Error", "text/plain", "Internal Server Error");
            }

        } else {
            sendResponse(out, "HTTP/1.1 404 Not Found", "text/plain", "Not Found");
        }

    }

    private static void sendResponse(PrintWriter out, String status, String contentType, String content) {
        out.println(status);
        out.println("Content-Type: " + contentType);
        out.println("Content-Length: " + content.length());
        out.println();
        out.println(content);

    }
}