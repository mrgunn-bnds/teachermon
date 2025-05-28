package com.gunn;

import com.j256.ormlite.dao.Dao;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

public class SaveUserHandler implements HttpHandler {
    private Dao<User,Integer> userDao;

    public SaveUserHandler(Dao<User, Integer> userDao) {
        this.userDao = userDao;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            User u = getUser(exchange); // converts the HTML form into a User model
            this.userDao.create(u);

            // redirect the user to battle page
            exchange.getResponseHeaders().add("Location","/battle");
            exchange.sendResponseHeaders(302, -1);
        } catch (SQLException e) {
            // TODO: show error to user
            throw new RuntimeException(e);
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
            }
        }
        return newUser;
    }
}
