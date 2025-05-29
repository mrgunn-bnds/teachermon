package com.gunn.models;

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

    @DatabaseField
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
        // TODO: this should be pulled from a character class
        this.battleLog = "<p>You have encountered a wild Mr. Gunn!</p>";
    }
}
