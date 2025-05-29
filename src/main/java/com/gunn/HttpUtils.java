package com.gunn;

import com.sun.net.httpserver.HttpExchange;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class HttpUtils {
    public static void showError(HttpExchange exchange, String message) throws IOException {
        InputStream in = HttpUtils.class.getResourceAsStream(FilePaths.ERROR);
        String text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        text = text.replace(Templates.MESSAGE, message);
        text = text.replace(Templates.OK, Routes.ROOT);
        byte[] contents = text.getBytes();

        exchange.sendResponseHeaders(200, contents.length);
        OutputStream os = exchange.getResponseBody();
        os.write(contents);
        os.close();
    }
}
