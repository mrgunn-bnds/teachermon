package com.gunn.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * The User class represents a player in the TeacherMon game system.
 * It stores basic account credentials and persistant statistics such as win/loss record.
 */
@DatabaseTable(tableName = "user")
public class User {
    @DatabaseField(canBeNull = false)
    private int wins;
    @DatabaseField(canBeNull = false)
    private int losses;

    public int getId() {
        return id;
    }

    @DatabaseField(generatedId = true)
    private int id; // the db automatically manages the id's!

    @DatabaseField(unique = true)
    private String username;

    public static final String USERNAME_FIELD = "username";

    @DatabaseField(canBeNull = false)
    private String password;

    /**
     * Sets the username for this user account.
     *
     * @param username the username to be set to
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Checks if the password of this user account is the given password
     *
     * @param password the password to be checked
     * @return true iff the password is the given password
     */
    public boolean hasPassword(String password) {
        return this.password.equals(password);
    }

    /**
     * Sets the password for this user account to the given password
     *
     * @param password the password to be set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Initialize a newly created User object.
     * Note: this default constructor is required by ORMLite.
     */
    public User() {
        // ORMLite requires this default constructor
        this.wins = 0;
        this.losses = 0;
    }

    /**
     * Increments the amount of wins for this user
     */
    public void addWin() {
        this.wins++;
    }

    /**
     * Decrements the total number of wins for this user
     */
    public void addLoss() {
        this.losses++;
    }
}
