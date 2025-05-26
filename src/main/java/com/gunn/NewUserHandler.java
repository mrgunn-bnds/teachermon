package com.gunn;

import com.j256.ormlite.dao.Dao;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class NewUserHandler implements HttpHandler {
    private Dao<User,Integer> userDao;

    public NewUserHandler(Dao<User, Integer> userDao) {
        this.userDao = userDao;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        User u = getUser(exchange);
        //TODO: we need to save this user
        //userDao.create(u);
    }

    private static User getUser(HttpExchange exchange) {
        BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        try {
            String query = br.readLine();
            // TODO: we need to save this information to the User model
            System.out.println(query);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //TODO: make a User
        return null;
    }
}
