package com.gunn;

import com.gunn.models.User;
import com.j256.ormlite.dao.Dao;
import com.sun.net.httpserver.BasicAuthenticator;

import java.sql.SQLException;
import java.util.List;

public class TeacherMonAuthenticator extends BasicAuthenticator {
    private final Dao<User,Integer> userDao;

    public TeacherMonAuthenticator(Dao<User,Integer> userDao) {
        super("TeacherMon");
        this.userDao = userDao;
    }

    @Override
    public boolean checkCredentials(String username, String password) {
        try {
            List<User> users = userDao.queryForEq(User.USERNAME_FIELD,username);
            if (users.size() != 1) {
                System.out.println("MORE THAN ONE USER HAD THAT USERNAME!!!");
                return false;
            }
            return users.getFirst().hasPassword(password);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
