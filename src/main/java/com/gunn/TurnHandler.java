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

public class TurnHandler implements HttpHandler {
    private Dao<User,Integer> userDao;
    private Dao<Battle, Integer> battleDao;

    public TurnHandler(Dao<User,Integer> userDao, Dao<Battle, Integer> battleDao) throws SQLException {
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

        // called everytime a move in a battle is called!

        // figure out which user is logged in.. and find his battle
        String username = exchange.getPrincipal().getUsername();
        User user;
        Battle b;
        try {
            List<User> users = userDao.queryForEq("username", username);
            // TODO: assert there is only 1

            user = users.getFirst();

            // Create the battle object if it doesnt exist! (starting the battle)
            List<Battle> battles = battleDao.queryForEq("userId", user.getId());
            b = battles.getFirst();

            // TODO: in the future, we need to determine which move (attack, run, teacherBall) was used
            // For now, assume attack

            // random damage to either player
            int playerDmg = (int) (Math.random() * 10 + 1);
            int enemyDmg = (int) (Math.random() * 10 + 1);

            b.setEnemyHP(b.getEnemyHP() - enemyDmg); // update the enemies hp
            b.setPlayerHP(b.getPlayerHP() - playerDmg);

            // update the log
            // Get player names TODO: from some db
            String player = "Mr. Merwe";
            String enemy = "Mr. Gunn";

            String log = b.getBattleLog();
            log = "<p>" + player + " hit " + enemy + " for " + enemyDmg + " points of damage!</p>";

            // Did you win?
            if (b.getEnemyHP() < 0) {
                log += "<p>" + player + " defeated " + enemy + "!</p>";
                log += "<p>Congratulations!</p>";
                log += "<p>After some time.." + enemy + " awakes refreshed!</p>";
                user.addWin();
                b.setEnemyHP(100);
                b.setPlayerHP(100);
                userDao.update(user); // save the win
            } else {
                log += "<p>" + enemy + " hit " + player + " for " + playerDmg + " points of damage!</p>";
                // you lost
                if (b.getPlayerHP() < 0) {
                    log += "<p>" + enemy + " defeated " + player + "!</p>";
                    log += "<p>Sadness..</p>";
                    log += "<p>After some time.." + player + " awakes refreshed!</p>";
                    user.addLoss();
                    b.setEnemyHP(100);
                    b.setPlayerHP(100);
                    userDao.update(user); // save the loss
                }
            }

            b.setBattleLog(log);

            // save the results to the db
            battleDao.update(b);

            // redirect the user to battle page
            exchange.getResponseHeaders().add("Location", "/battle");
            exchange.sendResponseHeaders(302, -1);
        } catch (SQLException e) {
            e.printStackTrace();
            showError(exchange, e.getMessage());
        }
    }
}
