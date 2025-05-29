package com.gunn.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class StaticFileHandler implements HttpHandler {
    protected String filePath;

    public StaticFileHandler(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        InputStream in = getClass().getResourceAsStream(filePath);
        byte[] contents = in.readAllBytes();

        exchange.sendResponseHeaders(200, contents.length);
        OutputStream os = exchange.getResponseBody();
        os.write(contents);
        os.close();
    }
}
