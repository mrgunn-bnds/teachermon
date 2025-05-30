package com.gunn.handlers;

import com.gunn.HttpUtils;
import com.gunn.Routes;
import com.gunn.Teacher;
import com.gunn.models.Battle;
import com.gunn.models.User;
import com.j256.ormlite.dao.Dao;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;

/**
 *
 */
public class TurnHandler implements HttpHandler {
    private final Dao<User,Integer> userDao;
    private final Dao<Battle, Integer> battleDao;
    private final Teacher[] teachers;

    /**
     *
     * @param userDao
     * @param battleDao
     */
    public TurnHandler(Dao<User,Integer> userDao, Dao<Battle, Integer> battleDao, Teacher[] teachers) {
        this.userDao = userDao;
        this.battleDao = battleDao;
        this.teachers = teachers;
    }



    @Override
    public void handle(HttpExchange exchange) throws IOException {

        // called everytime a move in a battle is called!

        // figure out which user is logged in.. and find his battle
        String username = exchange.getPrincipal().getUsername();
        User user;
        Battle b;
        try {
            List<User> users = userDao.queryForEq(User.USERNAME_FIELD, username);
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
            String player = teachers[b.getPlayerID()].getName();
            String enemy = teachers[b.getEnemyID()].getName();

            String log = "<p>" + player + " hit " + enemy + " for " + enemyDmg + " points of damage!</p>";
            boolean battleOver = false;
            // Did you win?
            if (b.getEnemyHP() <= 0) {
                log += "<p>" + player + " defeated " + enemy + "!</p>";
                log += "<p>After some time.." + enemy + " awakes refreshed!</p>";
                user.addWin();
                battleOver = true;

            } else {
                log += "<p>" + enemy + " hit " + player + " for " + playerDmg + " points of damage!</p>";
                // you lost
                if (b.getPlayerHP() <= 0) {
                    log += "<p>" + enemy + " defeated " + player + "!</p>";
                    log += "<p>After some time.." + player + " awakes refreshed!</p>";
                    user.addLoss();
                    battleOver = true;
                }
            }
            b.setBattleLog(log);

            if (battleOver) {
                // Randomize player & enemy
                b.startNew();

                b.setEnemyHP(100);
                b.setPlayerHP(100);
                userDao.update(user); // save the result
            }

            // save the results to the db
            battleDao.update(b);

            // redirect the user to battle page
            exchange.getResponseHeaders().add("Location", Routes.BATTLE);
            exchange.sendResponseHeaders(302, -1);
        } catch (SQLException e) {
            e.printStackTrace();
            HttpUtils.showError(exchange, e.getMessage());
        }
    }
}
