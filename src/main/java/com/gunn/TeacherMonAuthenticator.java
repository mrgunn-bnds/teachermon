package com.gunn;

import com.sun.net.httpserver.BasicAuthenticator;

public class TeacherMonAuthenticator extends BasicAuthenticator {
    public TeacherMonAuthenticator() {
        super("TeacherMon");
    }

    @Override
    public boolean checkCredentials(String username, String password) {
        //TODO: this should verify with the db
        return username.equals("mg");
    }
}
