package com.applications.dk_sky.calcapp;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by livingston on 2/16/18.
 */

@Entity
public class Entry {


    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_name")
    private String userName;

    @ColumnInfo(name = "buttons_pressed")
    private int buttonsPressed;

    @ColumnInfo(name = "expression")
    private String expression;

    @ColumnInfo(name = "result")
    private double result;

    public Entry(String userName, int buttonsPressed, String expression, double result) {
        this.userName = userName;
        this.buttonsPressed = buttonsPressed;
        this.expression = expression;
        this.result = result;
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
