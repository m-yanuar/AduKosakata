package com.payjoe.adukosakata.Models;

/**
 * Created by Payjoe on 2/28/2018.
 */

public class PostMatchInfo {
    public String username;
    public String userScore;

    public PostMatchInfo(){

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserScore() {
        return userScore;
    }

    public void setUserScore(String userScore) {
        this.userScore = userScore;
    }

    public PostMatchInfo(String username, String userScore) {
        this.username = username;
        this.userScore = userScore;
    }
}
