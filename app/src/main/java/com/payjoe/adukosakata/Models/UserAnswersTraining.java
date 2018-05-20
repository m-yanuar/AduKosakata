package com.payjoe.adukosakata.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Payjoe on 3/19/2018.
 */

public class UserAnswersTraining implements Parcelable {
    public int index;
    public String questionForAnswers;
    public String userAnswers;
    public boolean resultAnswers;

    public UserAnswersTraining(){

    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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

    public boolean getResultAnswers() {
        return resultAnswers;
    }

    public void setResultAnswers(boolean resultAnswers) {
        this.resultAnswers = resultAnswers;
    }

    public UserAnswersTraining(int index, String questionForAnswers, String userAnswers, boolean resultAnswers) {
        this.index = index;
        this.questionForAnswers = questionForAnswers;
        this.userAnswers = userAnswers;
        this.resultAnswers = resultAnswers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.index);
        dest.writeString(this.questionForAnswers);
        dest.writeString(this.userAnswers);
        dest.writeByte(this.resultAnswers ? (byte) 1 : (byte) 0);
    }

    protected UserAnswersTraining(Parcel in) {
        this.index = in.readInt();
        this.questionForAnswers = in.readString();
        this.userAnswers = in.readString();
        this.resultAnswers = in.readByte() != 0;
    }

    public static final Parcelable.Creator<UserAnswersTraining> CREATOR = new Parcelable.Creator<UserAnswersTraining>() {
        @Override
        public UserAnswersTraining createFromParcel(Parcel source) {
            return new UserAnswersTraining(source);
        }

        @Override
        public UserAnswersTraining[] newArray(int size) {
            return new UserAnswersTraining[size];
        }
    };
}
