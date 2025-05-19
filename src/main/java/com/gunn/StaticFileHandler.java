package com.gunn;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class StaticFileHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        //System.out.println(exchange.getRequestURI().getPath());
        String filePath = exchange.getRequestURI().getPath();
        if (filePath.equals("/")) {
            filePath = "index.html";
        }
        filePath = "www/" + filePath;
        File file = new File(filePath);
        byte[] contents = Files.readAllBytes(file.toPath());

        exchange.sendResponseHeaders(200, contents.length);
        OutputStream os = exchange.getResponseBody();
        os.write(contents);
        os.close();
    }
}
