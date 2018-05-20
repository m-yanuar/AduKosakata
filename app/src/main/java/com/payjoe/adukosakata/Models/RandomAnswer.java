package com.payjoe.adukosakata.Models;

/**
 * Created by Payjoe on 2/21/2018.
 */

public class RandomAnswer {
    public String answer_1;
    public String answer_2;
    public String answer_3;
    public String answer_4;

    public RandomAnswer(){

    }

    public RandomAnswer(String answer_1, String answer_2, String answer_3, String answer_4) {
        this.answer_1 = answer_1;
        this.answer_2 = answer_2;
        this.answer_3 = answer_3;
        this.answer_4 = answer_4;
    }

    public String getAnswer_1() {
        return answer_1;
    }

    public void setAnswer_1(String answer_1) {
        this.answer_1 = answer_1;
    }

    public String getAnswer_2() {
        return answer_2;
    }

    public void setAnswer_2(String answer_2) {
        this.answer_2 = answer_2;
    }

    public String getAnswer_3() {
        return answer_3;
    }

    public void setAnswer_3(String answer_3) {
        this.answer_3 = answer_3;
    }

    public String getAnswer_4() {
        return answer_4;
    }

    public void setAnswer_4(String answer_4) {
        this.answer_4 = answer_4;
    }
}
