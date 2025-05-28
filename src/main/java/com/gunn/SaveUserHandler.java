package com.gunn;

import com.j256.ormlite.dao.Dao;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.file.Files;

public class SaveUserHandler implements HttpHandler {
    private final Dao<User,Integer> userDao;
    private final Dao<Battle, Integer> battleDao; // every user has 1 battle

    public SaveUserHandler(Dao<User, Integer> userDao, Dao<Battle, Integer> battleDao) {

        this.userDao = userDao;
        this.battleDao = battleDao;
    }

    private void showError(HttpExchange exchange, String message) throws IOException {
        File file = new File("www/error.html");
        String text = Files.readString(file.toPath());
        text = text.replace("{{MESSAGE}}", message);
        text = text.replace("{{OK_URL}}", "/create-user");
        byte[] contents = text.getBytes();

        exchange.sendResponseHeaders(200, contents.length);
        OutputStream os = exchange.getResponseBody();
        os.write(contents);
        os.close();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            User u = getUser(exchange); // converts the HTML form into a User model
            this.userDao.create(u);
            // Every user starts in battle
            Battle b = new Battle(u.getId());
            this.battleDao.create(b);

            // redirect the user to battle page
            exchange.getResponseHeaders().add("Location","/battle");
            exchange.sendResponseHeaders(302, -1);
        } catch (Exception e) {
            e.printStackTrace();
            showError(exchange, e.getMessage());
        }
    }

    private static User getUser(HttpExchange exchange) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        // username=abc&password=def
        User newUser = new User();
        String query = br.readLine();
        String[] vars = query.split("&");
        for (String var : vars) {
            String[] keyvalue = var.split("=");
            if (keyvalue[0].equalsIgnoreCase("username")) {
                newUser.setUsername(keyvalue[1]);
            } else if (keyvalue[0].equalsIgnoreCase("password")) {
                newUser.setPassword(keyvalue[1]);
            } else {
                throw new IOException("unexpected keyvalue " + keyvalue[0]);
            }
        }
        return newUser;
    }
}
