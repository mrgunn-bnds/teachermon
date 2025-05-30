package com.gunn.handlers;

import com.gunn.Routes;
import com.gunn.Templates;
import com.gunn.models.Teacher;
import com.gunn.TeacherRepository;
import com.gunn.models.Battle;
import com.gunn.models.User;
import com.j256.ormlite.dao.Dao;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;

import static com.gunn.HttpUtils.showError;

public class SaveUserHandler implements HttpHandler {
    private final Dao<User,Integer> userDao;
    private final Dao<Battle, Integer> battleDao; // every user has 1 battle
    private final TeacherRepository teacherRepo;

    public SaveUserHandler(Dao<User, Integer> userDao, Dao<Battle, Integer> battleDao, TeacherRepository teacherRepo) {

        this.userDao = userDao;
        this.battleDao = battleDao;
        this.teacherRepo = teacherRepo;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            User u = getUser(exchange); // converts the HTML form into a User model
            this.userDao.create(u);
            // Every user starts in battle
            Battle b = new Battle(u.getId());

            // shuffle the competitors
            //TODO: this logic is copied from TurnHandler, probably should have done this better.
            Teacher newPlayer = teacherRepo.getRandomTeacher();
            Teacher newEnemy = teacherRepo.getRandomOpponent(newPlayer);
            b.setPlayerID(newPlayer.getId());
            b.setEnemyID(newEnemy.getId());
            String log = "<p>A wild {{ENEMY_NAME}} arrives. {{PLAYER_NAME}} will fight!</p>"
                    .replace(Templates.PLAYER_NAME, newPlayer.getName())
                    .replace(Templates.ENEMY_NAME, newEnemy.getName());
            b.setBattleLog(log);

            b.setEnemyHP(100);
            b.setPlayerHP(100);

            this.battleDao.create(b);

            // redirect the user to battle page
            exchange.getResponseHeaders().add("Location", Routes.BATTLE);
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
