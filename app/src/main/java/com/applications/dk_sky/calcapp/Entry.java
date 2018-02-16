package com.applications.dk_sky.calcapp;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by livingston on 2/16/18.
 */

@Entity(tableName = "entries")
public class Entry {


    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "user_name")
    private String userName;

    @ColumnInfo(name = "buttons_pressed")
    private int buttonsPressed;

    @ColumnInfo(name = "expression")
    private String expression;

    @ColumnInfo(name = "result")
    private double result;

    public Entry(String userName, int buttonsPressed, String expression, double result) {
        setUserName(userName);
        setButtonsPressed(buttonsPressed);
        setExpression(expression);
        setResult(result);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getButtonsPressed() {
        return buttonsPressed;
    }

    public void setButtonsPressed(int buttonsPressed) {
        this.buttonsPressed = buttonsPressed;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }
}
