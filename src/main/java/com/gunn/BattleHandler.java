package com.gunn;

import com.j256.ormlite.dao.Dao;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;

public class BattleHandler implements HttpHandler {
    private final Dao<User,Integer> userDao;
    private final Dao<Battle,Integer> battleDao;

    public BattleHandler(Dao<User,Integer> userDao, Dao<Battle,Integer> battleDao) {
        this.userDao = userDao;
        this.battleDao = battleDao;
    }

    private void showError(HttpExchange exchange, String message) throws IOException {
        File file = new File("www/error.html");
        String text = Files.readString(file.toPath());
        text = text.replace("{{MESSAGE}}", message);
        text = text.replace("{{OK_URL}}", "/battle");
        byte[] contents = text.getBytes();

        exchange.sendResponseHeaders(200, contents.length);
        OutputStream os = exchange.getResponseBody();
        os.write(contents);
        os.close();
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Part of HTTP we can have a username.. using this to get the User
        String username = exchange.getPrincipal().getUsername();
        User user;
        Battle battle;

        try {
            List<User> users = userDao.queryForEq("username", username);
            // TODO: enable assertions and assert theres only one
            user = users.getFirst();

            // Create the battle object if it doesnt exist! (starting the battle)
            List<Battle> battles = battleDao.queryForEq("userId", user.getId());
            battle = battles.getFirst();

            // Start preparing the HTML page for display
            String filePath = "www/battle.html";
            File file = new File(filePath);
            String text = Files.readString(file.toPath());

            // show the battle log
            text = text.replace("{{BATTLE_LOG}}", battle.getBattleLog());
            text = text.replace("{{ENEMY_HP}}", "" + battle.getEnemyHP());
            text = text.replace("{{PLAYER_HP}}", "" + battle.getPlayerHP());
            text = text.replace("{{BATTLE_ID}}", "" + battle.getId());

            // show the username
            text = text.replace("{{USERNAME}}", username);
            // save results to database

            this.userDao.update(user);

            text = text.replace("{{STATS}}", "You have won " + user.getWins() + " total times, and lost " + user.getLosses() + " times.");
            byte[] contents = text.getBytes();

            // TODO: handle errors
            exchange.sendResponseHeaders(200, contents.length);
            OutputStream os = exchange.getResponseBody();
            os.write(contents);
            os.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showError(exchange, e.getMessage());
        }
    }
}
