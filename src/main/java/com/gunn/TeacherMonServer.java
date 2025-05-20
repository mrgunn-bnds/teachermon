package com.gunn;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class TeacherMonServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(80),0);
        server.createContext("/", new HomeHandler());
        server.createContext("/favicon.ico", new StaticFileHandler());
        server.createContext("/gunn.png", new StaticFileHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }
}
