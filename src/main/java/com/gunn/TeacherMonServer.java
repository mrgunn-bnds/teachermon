package com.gunn;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

public class TeacherMonServer {
    public static void main(String[] args) throws Exception {
        // set up the db connection
        // this comes from: https://ormlite.com/
        // this uses h2, but you can change it to match your database
        String databaseUrl = "jdbc:h2:file:./database/teachermon.db";

        // create a connection source to our database
        ConnectionSource connectionSource =
                new JdbcConnectionSource(databaseUrl);

        // instantiate the DAO to handle User with integer id
        Dao<User,Integer> userDao = DaoManager.createDao(connectionSource, User.class);

        // if you need to create the 'user' table make this call
        TableUtils.createTableIfNotExists(connectionSource, User.class);

        // Search for the user in the db with id 1
        User user = userDao.queryForId(1);
        if (user == null) {
            // create our one and only user
            user = new User(1);

            // persist the account object to the database
            userDao.create(user);
        }

        // start the server
        HttpServer server = HttpServer.create(new InetSocketAddress(80),0);
        server.createContext("/", new HomeHandler(userDao));
        server.createContext("/favicon.ico", new StaticFileHandler());
        server.createContext("/gunn.png", new StaticFileHandler());
        server.setExecutor(null); // creates a default executor
        server.start();

        // close the connection source
        connectionSource.close();
    }
}
