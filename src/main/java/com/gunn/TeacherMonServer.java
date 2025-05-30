package com.gunn;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gunn.handlers.*;
import com.gunn.models.User;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import org.h2.tools.Server;

import java.io.File;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;

public class TeacherMonServer {
    //TODO: how can i get this from Battle?
    public static Teacher[] teachers = null;

    public static void main(String[] args)  {
        // set up the db connection
        // this comes from: https://ormlite.com/
        // this uses h2, but you can change it to match your database
        String databaseUrl = "jdbc:h2:file:./database/teachermon.db";

        try {
            Server.createWebServer().start();

            // create a connection source to our database
            ConnectionSource connectionSource =
                    new JdbcConnectionSource(databaseUrl);

            // get the db of teachers
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = TeacherMonServer.class.getResourceAsStream("/data/teachers.json");
            teachers = mapper.readValue(is, Teacher[].class);

            // instantiate the DAO to handle Models with integer id primary keys
            Dao<User,Integer> userDao = DaoManager.createDao(connectionSource, User.class);
            Dao<com.gunn.models.Battle, Integer> battleDao = DaoManager.createDao(connectionSource, com.gunn.models.Battle.class);

            // if you need to create the model tables make this call
            TableUtils.createTableIfNotExists(connectionSource, User.class);
            TableUtils.createTableIfNotExists(connectionSource, com.gunn.models.Battle.class);



            // start the server
            HttpServer server = HttpServer.create(new InetSocketAddress(80),0);
            HttpContext battleCtx = server.createContext(Routes.BATTLE, new BattleHandler(userDao, battleDao, teachers));
            battleCtx.setAuthenticator(new TeacherMonAuthenticator(userDao));
            HttpContext turnCtx = server.createContext(Routes.TURN, new TurnHandler(userDao, battleDao, teachers));
            turnCtx.setAuthenticator(new TeacherMonAuthenticator(userDao));
            server.createContext(Routes.ROOT,new StaticFileHandler(FilePaths.INDEX));
            server.createContext(Routes.SAVE_USER, new SaveUserHandler(userDao, battleDao));
            server.createContext(Routes.CREATE_USER, new StaticFileHandler(FilePaths.CREATE_USER));
            server.createContext(Routes.FAV_ICO, new StaticFileHandler(FilePaths.FAV_ICON));
            server.createContext(Routes.IMG, new ImageHandler());
            server.setExecutor(null); // creates a default executor
            server.start();

            // close the connection source
            connectionSource.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
