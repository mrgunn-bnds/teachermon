package com.gunn;

public class User {
    public int wins;
    public int losses;

    public int id; // for now just use id 1

    public User(int id) {
        this.id = id;
        this.wins = 0;
        this.losses =0;
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
