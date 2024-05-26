package com.lahiruj.http;

import java.io.PrintWriter;
import java.net.Socket;

public interface HttpRequestHandler {
    void handleRequest(String resource, PrintWriter out, Socket clientSocket);
}
