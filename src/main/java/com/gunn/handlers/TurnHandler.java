package com.gunn.handlers;

import com.gunn.*;
import com.gunn.models.Battle;
import com.gunn.models.Teacher;
import com.gunn.models.User;
import com.j256.ormlite.dao.Dao;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 *
 */
public class TurnHandler implements HttpHandler {
    private final Dao<User,Integer> userDao;
    private final Dao<Battle, Integer> battleDao;
    private final TeacherRepository teacherRepo;

    /**
     *
     * @param userDao
     * @param battleDao
     */
    public TurnHandler(Dao<User,Integer> userDao, Dao<Battle, Integer> battleDao, TeacherRepository teacherRepo) {
        this.userDao = userDao;
        this.battleDao = battleDao;
        this.teacherRepo = teacherRepo;
    }



    @Override
    public void handle(HttpExchange exchange) throws IOException {

        // called everytime a move in a battle is called!

        // figure out which user is logged in.. and find his battle
        String username = exchange.getPrincipal().getUsername();
        User user;
        Battle b;
        try {
            // Get the user (logged in), and the associated battle
            List<User> users = userDao.queryForEq(User.USERNAME_FIELD, username);
            user = users.getFirst();

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
            Teacher player = teacherRepo.getById(b.getPlayerID());
            Teacher enemy = teacherRepo.getById(b.getEnemyID());

            String log = "<p>{{PLAYER_NAME}} hit {{ENEMY_NAME}} for {{ENEMY_DMG}} points of damage!</p>";

            boolean battleOver = false;
            // Did you win?
            if (b.getEnemyHP() <= 0) {
                log += "<p>{{PLAYER_NAME}} defeated {{ENEMY_NAME}}!</p>" +
                        "<p>After some time..{{ENEMY_NAME}} awakes refreshed!</p>";
                user.addWin();
                battleOver = true;

            } else {
                log += "<p>{{ENEMY_NAME}} hit {{PLAYER_NAME}} for {{PLAYER_DMG}} points of damage!</p>";
                // you lost
                if (b.getPlayerHP() <= 0) {
                    log += "<p>{{ENEMY_NAME}} defeated {{PLAYER_NAME}}!</p>" +
                            "<p>After some time..{{PLAYER_NAME}} awakes refreshed!</p>";
                    user.addLoss();
                    battleOver = true;
                }
            }

            // replace templates with real values
            log = log.replace(Templates.PLAYER_NAME, player.getName())
                    .replace(Templates.PLAYER_DMG, "" + playerDmg)
                    .replace(Templates.ENEMY_NAME, enemy.getName())
                    .replace(Templates.ENEMY_DMG, "" + enemyDmg);

            if (battleOver) {
                // save the results of the fight
                userDao.update(user);

                // Randomize player & enemy
                Teacher newPlayer = teacherRepo.getRandomTeacher();
                Teacher newEnemy = teacherRepo.getRandomOpponent(newPlayer);
                b.setPlayerID(newPlayer.getId());
                b.setEnemyID(newEnemy.getId());
                b.setEnemyHP(100);
                b.setPlayerHP(100);

                log += "<p>A wild {{ENEMY_NAME}} arrives. {{PLAYER_NAME}} will fight!</p>"
                        .replace(Templates.PLAYER_NAME, newPlayer.getName())
                        .replace(Templates.ENEMY_NAME, newEnemy.getName());
            }
            b.setBattleLog(log);

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
