package com.gunn;

import com.j256.ormlite.dao.Dao;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.sql.SQLException;

public class HomeHandler implements HttpHandler {
    private final Dao<User,Integer> userDao;
    private final User user;

    public HomeHandler(Dao<User,Integer> userDao) throws SQLException {
        this.userDao = userDao;
        this.user = userDao.queryForId(1);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        //System.out.println(exchange.getRequestURI().getPath());
        String filePath = "www/index.html";
        File file = new File(filePath);
        String text = Files.readString(file.toPath());
        if (Math.random() > 0.5 ) {
            text = text.replace("{{RESULT}}", "You lose!");
            user.addLoss();
        } else {
            text = text.replace("{{RESULT}}", "You win!");
            user.addWin();
        }
        // save results to database
        try {
            this.userDao.update(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        text = text.replace("{{STATS}}","You have won " + user.getWins() + " total times, and lost " + user.getLosses() + " times.");
        byte[] contents = text.getBytes();

        exchange.sendResponseHeaders(200, contents.length);
        OutputStream os = exchange.getResponseBody();
        os.write(contents);
        os.close();
    }
}
