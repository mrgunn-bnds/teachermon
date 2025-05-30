package com.gunn.handlers;

import com.gunn.FilePaths;
import com.gunn.Teacher;
import com.gunn.Templates;
import com.gunn.models.Battle;
import com.gunn.models.User;
import com.j256.ormlite.dao.Dao;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

import static com.gunn.HttpUtils.showError;

/**
 * Handles the main battle logic for the TeacherMon game
 *
 * This class is responsible for responding to GET  requests at the {@link com.gunn.Routes#BATTLE} endpoint.
 *
 * Each player can only participate in one battle at a time.
 * Battle states are stored in the persistant database.
 *
 * This class uses the authenticated user identity (from Basic Auth) to lookup and process battles.
 *
 * @see com.sun.net.httpserver.HttpHandler
 * @see com.sun.net.httpserver.BasicAuthenticator
 */
public class BattleHandler implements HttpHandler {
    private final Dao<User,Integer> userDao;
    private final Dao<com.gunn.models.Battle,Integer> battleDao;
    private final Teacher[] teachers;

    /**
     * Initialize the BattleHandler object.
     *
     * @param userDao the DAO of the user
     * @param battleDao the DAO of the battle
     *
     * @see Dao
     */
    public BattleHandler(Dao<User,Integer> userDao, Dao<com.gunn.models.Battle,Integer> battleDao, Teacher[] teachers) {
        this.userDao = userDao;
        this.battleDao = battleDao;
        this.teachers = teachers;
    }

    /**
     * Handles the HTTP GET request to display the current state of the ongoing battle.
     *
     * This will use the template file {@link FilePaths#BATTLE}
     *
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws IOException
     *
     * @see Templates
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Part of HTTP we can have a username.. using this to get the User
        String username = exchange.getPrincipal().getUsername();
        User user;
        com.gunn.models.Battle battle;

        try {
            List<User> users = userDao.queryForEq(User.USERNAME_FIELD, username);
            // TODO: enable assertions and assert theres only one
            user = users.getFirst();

            // Create the battle object if it doesnt exist! (starting the battle)
            List<com.gunn.models.Battle> battles = battleDao.queryForEq(Battle.USERID_FIELD, user.getId());
            battle = battles.getFirst();

            // Start preparing the HTML page for display
            InputStream in = getClass().getResourceAsStream(FilePaths.BATTLE);
            String text = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            // show the battle log
            text = text.replace(Templates.BATTLE_LOG, battle.getBattleLog());
            text = text.replace(Templates.ENEMY_HP, "" + battle.getEnemyHP());
            text = text.replace(Templates.PLAYER_HP, "" + battle.getPlayerHP());
            text = text.replace(Templates.BATTLE_ID, "" + battle.getId());
            text = text.replace(Templates.PLAYER_IMG, teachers[battle.getPlayerID()].getImg());
            text = text.replace(Templates.PLAYER_NAME, teachers[battle.getPlayerID()].getName());
            text = text.replace(Templates.ENEMY_IMG, teachers[battle.getEnemyID()].getImg());
            text = text.replace(Templates.ENEMY_NAME, teachers[battle.getEnemyID()].getName());

            // show the username
            text = text.replace(Templates.USERNAME, username);
            // save results to database
            this.userDao.update(user);

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
