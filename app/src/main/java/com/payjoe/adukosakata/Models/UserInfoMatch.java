package com.payjoe.adukosakata.Models;

/**
 * Created by Payjoe on 1/16/2018.
 */

public class UserInfoMatch {
    public String userid;
    public String username;
    public int rank;
    public int score;
    public boolean finished;

    public UserInfoMatch(){

    }

    public UserInfoMatch(String userId, String username, int rank, int score, boolean finished) {
        this.userid = userId;
        this.username = username;
        this.rank = rank;
        this.score = score;
        this.finished = finished;
    }
}
