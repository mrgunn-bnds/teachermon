package com.gunn;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import org.h2.tools.Server;

import java.net.InetSocketAddress;

public class TeacherMonServer {
    public static void main(String[] args) throws Exception {
        // set up the db connection
        // this comes from: https://ormlite.com/
        // this uses h2, but you can change it to match your database
        String databaseUrl = "jdbc:h2:file:./database/teachermon.db";

        Server.createWebServer("-trace").start();

        // create a connection source to our database
        ConnectionSource connectionSource =
                new JdbcConnectionSource(databaseUrl);

        // instantiate the DAO to handle User with integer id
        Dao<User,Integer> userDao = DaoManager.createDao(connectionSource, User.class);

        // if you need to create the 'user' table make this call
        TableUtils.createTableIfNotExists(connectionSource, User.class);

        // start the server
        HttpServer server = HttpServer.create(new InetSocketAddress(80),0);
        HttpContext battleCtx = server.createContext("/battle", new BattleHandler(userDao));
        battleCtx.setAuthenticator(new TeacherMonAuthenticator());
        server.createContext("/",new StaticFileHandler("www/index.html"));
        server.createContext("/new-user", new NewUserHandler(userDao));
        server.createContext("/create-user", new StaticFileHandler("www/create-user.html"));
        server.createContext("/favicon.ico", new StaticFileHandler("www/favicon.ico"));
        server.createContext("/gunn.png", new StaticFileHandler("www/gunn.png"));
        server.setExecutor(null); // creates a default executor
        server.start();

        // close the connection source
        connectionSource.close();
    }
}
