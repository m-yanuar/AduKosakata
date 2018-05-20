package com.payjoe.adukosakata.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Payjoe on 2/5/2018.
 */

public class UserAnswersModels implements Parcelable {

    // Variable yang dibutuhkan
    public String questionForAnswers;
    public String userAnswers;
    public boolean resultAnswers;

    public UserAnswersModels(){

    }

    public UserAnswersModels(String questionForAnswers, String userAnswers, boolean resultAnswers) {
        this.questionForAnswers = questionForAnswers;
        this.userAnswers = userAnswers;
        this.resultAnswers = resultAnswers;
    }
    public String getQuestionForAnswers() {
        return questionForAnswers;
    }

    public void setQuestionForAnswers(String questionForAnswers) {
        this.questionForAnswers = questionForAnswers;
    }

    public String getUserAnswers() {
        return userAnswers;
    }

    public void setUserAnswers(String userAnswers) {
        this.userAnswers = userAnswers;
    }

    public boolean isResultAnswers() {
        return resultAnswers;
    }

    public void setResultAnswers(boolean resultAnswers) {
        this.resultAnswers = resultAnswers;
    }

    public boolean getResultAnswers(){ return resultAnswers;}


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.questionForAnswers);
        dest.writeString(this.userAnswers);
        dest.writeByte(this.resultAnswers ? (byte) 1 : (byte) 0);
    }

    protected UserAnswersModels(Parcel in) {
        this.questionForAnswers = in.readString();
        this.userAnswers = in.readString();
        this.resultAnswers = in.readByte() != 0;
    }

    public static final Creator<UserAnswersModels> CREATOR = new Creator<UserAnswersModels>() {
        @Override
        public UserAnswersModels createFromParcel(Parcel source) {
            return new UserAnswersModels(source);
        }

        @Override
        public UserAnswersModels[] newArray(int size) {
            return new UserAnswersModels[size];
        }
    };
}
