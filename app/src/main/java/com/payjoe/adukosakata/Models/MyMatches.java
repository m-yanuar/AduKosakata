package com.payjoe.adukosakata.Models;

/**
 * Created by Payjoe on 1/30/2018.
 */

public class MyMatches {
    public String result;
    public String usernameEnemy;

    public MyMatches(){

    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getUsernameEnemy() {
        return usernameEnemy;
    }

    public void setUsernameEnemy(String usernameEnemy) {
        this.usernameEnemy = usernameEnemy;
    }

    public MyMatches(String result, String usernameEnemy) {
        this.result = result;
        this.usernameEnemy = usernameEnemy;
    }
}
