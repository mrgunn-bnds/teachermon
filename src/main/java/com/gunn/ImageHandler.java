package com.gunn;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class ImageHandler extends StaticFileHandler {
    public ImageHandler() {
        super("unknown_image_filepath");
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        this.filePath = "www/"+ exchange.getRequestURI().getPath();
        super.handle(exchange);
    }
}
