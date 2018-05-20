package com.payjoe.adukosakata.Models;

/**
 * Created by Payjoe on 2/22/2018.
 */

public class UserAnswersFirebase {
    public String questionForAnswers;
    public String userAnswers;
    public boolean resultAnswers;

    public UserAnswersFirebase(){

    }

    public UserAnswersFirebase(String questionForAnswers, String userAnswers, boolean resultAnswers) {
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
}
