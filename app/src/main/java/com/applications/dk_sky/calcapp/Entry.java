package com.applications.dk_sky.calcapp;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


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

    Entry(String userName, int buttonsPressed, String expression, double result) {
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

    String getUserName() {
        return userName;
    }

    private void setUserName(String userName) {
        this.userName = userName;
    }

    int getButtonsPressed() {
        return buttonsPressed;
    }

    private void setButtonsPressed(int buttonsPressed) {
        this.buttonsPressed = buttonsPressed;
    }

    String getExpression() {
        return expression;
    }

    private void setExpression(String expression) {
        this.expression = expression;
    }

    double getResult() {
        return result;
    }

    private void setResult(double result) {
        this.result = result;
    }
}
