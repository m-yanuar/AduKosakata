package com.payjoe.adukosakata.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Payjoe on 3/1/2018.
 */

public class MyMatchesFragmentModels implements Parcelable {
    public String matchID;
    public String enemyUserID;
    public String enemyUsername;
    public int enemyScore;
    public String userUserID;
    public String userUsername;
    public int userScore;

    public MyMatchesFragmentModels(){

    }

    public MyMatchesFragmentModels(String matchID, String enemyUserID, String enemyUsername, int enemyScore, String userUserID, String userUsername, int userScore) {
        this.matchID = matchID;
        this.enemyUserID = enemyUserID;
        this.enemyUsername = enemyUsername;
        this.enemyScore = enemyScore;
        this.userUserID = userUserID;
        this.userUsername = userUsername;
        this.userScore = userScore;
    }

    public String getMatchID() {
        return matchID;
    }

    public void setMatchID(String matchID) {
        this.matchID = matchID;
    }

    public String getEnemyUserID() {
        return enemyUserID;
    }

    public void setEnemyUserID(String enemyUserID) {
        this.enemyUserID = enemyUserID;
    }

    public String getEnemyUsername() {
        return enemyUsername;
    }

    public void setEnemyUsername(String enemyUsername) {
        this.enemyUsername = enemyUsername;
    }

    public int getEnemyScore() {
        return enemyScore;
    }

    public void setEnemyScore(int enemyScore) {
        this.enemyScore = enemyScore;
    }

    public String getUserUserID() {
        return userUserID;
    }

    public void setUserUserID(String userUserID) {
        this.userUserID = userUserID;
    }

    public String getUserUsername() {
        return userUsername;
    }

    public void setUserUsername(String userUsername) {
        this.userUsername = userUsername;
    }

    public int getUserScore() {
        return userScore;
    }

    public void setUserScore(int userScore) {
        this.userScore = userScore;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.matchID);
        dest.writeString(this.enemyUserID);
        dest.writeString(this.enemyUsername);
        dest.writeInt(this.enemyScore);
        dest.writeString(this.userUserID);
        dest.writeString(this.userUsername);
        dest.writeInt(this.userScore);
    }

    protected MyMatchesFragmentModels(Parcel in) {
        this.matchID = in.readString();
        this.enemyUserID = in.readString();
        this.enemyUsername = in.readString();
        this.enemyScore = in.readInt();
        this.userUserID = in.readString();
        this.userUsername = in.readString();
        this.userScore = in.readInt();
    }

    public static final Parcelable.Creator<MyMatchesFragmentModels> CREATOR = new Parcelable.Creator<MyMatchesFragmentModels>() {
        @Override
        public MyMatchesFragmentModels createFromParcel(Parcel source) {
            return new MyMatchesFragmentModels(source);
        }

        @Override
        public MyMatchesFragmentModels[] newArray(int size) {
            return new MyMatchesFragmentModels[size];
        }
    };
}
