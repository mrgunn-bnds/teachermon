package com.gunn.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 *
 */
@DatabaseTable(tableName = "battle")
public class Battle {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private int userId;

    public static final String USERID_FIELD = "userId";

    @DatabaseField
    private int playerHP;  // merwe

    @DatabaseField
    private int enemyHP;  // gunn

    // which teacher is player using
    @DatabaseField
    private int playerID;

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public int getEnemyID() {
        return enemyID;
    }

    public void setEnemyID(int enemyID) {
        this.enemyID = enemyID;
    }

    // which teacher is enemy
    @DatabaseField
    private int enemyID;

    @DatabaseField(dataType = DataType.LONG_STRING)
    private String battleLog; // newline seperated list of all actions


    public Battle() {

    }

    public int getPlayerHP() {
        return playerHP;
    }

    /**
     * this method allows you to change the value of the players hitpoints.
     *
     * @param playerHP the new hitpoints of the player
     */
    public void setPlayerHP(int playerHP) {
        this.playerHP = playerHP;
    }

    public int getEnemyHP() {
        return enemyHP;
    }

    /**
     *
     * @param enemyHP
     */
    public void setEnemyHP(int enemyHP) {
        this.enemyHP = enemyHP;
    }

    public String getBattleLog() {
        return battleLog;
    }

    public void setBattleLog(String battleLog) {
        this.battleLog = battleLog;
    }

    public int getId() {
        return id;
    }

    public Battle(int userId) {
        this.userId = userId;
        // TODO: we should get this from the characters table
        this.playerHP = 100;
        this.enemyHP = 100;
        this.battleLog = "";
    }
}
