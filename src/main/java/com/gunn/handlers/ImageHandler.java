package com.gunn.handlers;

import com.gunn.FilePaths;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;


public class ImageHandler extends StaticFileHandler {
    public ImageHandler() {
        super("unknown_image_filepath");
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        this.filePath = FilePaths.WWW_ROOT + exchange.getRequestURI().getPath();
        this.filePath = filePath.replace("//","/"); // quickly fix a bug where URI paths are getting a "/" prefix
        System.out.println("The image filepath is " + filePath);
        super.handle(exchange);
    }
}
