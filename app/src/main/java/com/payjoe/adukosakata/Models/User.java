package com.payjoe.adukosakata.Models;

/**
 * Created by Payjoe on 1/3/2018.
 */

public class User {
    public String username;
    public String email;
    public int matches;
    public int win;
    public int loss;
    public int rank;
    public int draw;

    public User(){

    }

    public User(String username, String email, int matches, int win,
                int loss, int rank, int draw){
        this.username = username;
        this.email = email;
        this.matches = matches;
        this.win = win;
        this.loss = loss;
        this.rank = rank;
        this.draw = draw;
    }

}
