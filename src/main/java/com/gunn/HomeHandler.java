package com.gunn;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class HomeHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        //System.out.println(exchange.getRequestURI().getPath());
        String filePath = "www/index.html";
        File file = new File(filePath);
        String text = Files.readString(file.toPath());
        if (Math.random() > 0.5) {
            text = text.replace("{{RESULT}}", "You lose!");
        } else {
            text = text.replace("{{RESULT}}", "You win!");
        }
        byte[] contents = text.getBytes();

        exchange.sendResponseHeaders(200, contents.length);
        OutputStream os = exchange.getResponseBody();
        os.write(contents);
        os.close();
    }
}
