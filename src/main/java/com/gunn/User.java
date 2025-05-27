package com.gunn;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "user")
public class User {
    @DatabaseField(canBeNull = false)
    private int wins;
    @DatabaseField(canBeNull = false)
    private int losses;

    @DatabaseField(generatedId = true)
    private int id; // for now just use id 1

    @DatabaseField(unique = true)
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @DatabaseField(canBeNull = false)
    private String password;

    public User(int id) {
        this.id = id;
        System.out.println("resetting the stats");
        this.wins = 0;
        this.losses =0;
    }

    public User() {
        // ORMLite requires this default constructor
    }

    public void addWin() {
        this.wins++;
    }

    public void addLoss() {
        this.losses++;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }
}
