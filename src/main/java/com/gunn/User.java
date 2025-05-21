package com.gunn;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "user")
public class User {
    @DatabaseField(canBeNull = false)
    public int wins;
    @DatabaseField(canBeNull = false)
    public int losses;

    @DatabaseField(id = true)
    public int id; // for now just use id 1

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
