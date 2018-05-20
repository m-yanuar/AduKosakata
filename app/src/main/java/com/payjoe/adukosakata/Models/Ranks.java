package com.payjoe.adukosakata.Models;

/**
 * Created by Payjoe on 1/30/2018.
 */

public class Ranks {
    public String username;
    public int rank;
    public String userid;

    public Ranks(){

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Ranks(String username, int rank, String userid) {
        this.username = username;
        this.rank = rank;
        this.userid = userid;
    }


}
