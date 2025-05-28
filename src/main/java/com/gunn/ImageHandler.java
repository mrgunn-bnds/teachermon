package com.gunn;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;


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
