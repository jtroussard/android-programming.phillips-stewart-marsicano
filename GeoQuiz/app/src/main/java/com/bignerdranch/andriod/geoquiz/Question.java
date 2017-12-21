package com.bignerdranch.andriod.geoquiz;

/**
 * Created by sparrow on 12/20/17.
 */

public class Question {

    private int mTextResId;
    private boolean mAnswerTrue;
    private boolean mAnswered;

    public Question (int textResId, boolean answerTrue) {
        mTextResId = textResId;
        mAnswerTrue = answerTrue;
        mAnswered = false;
    }

    public boolean getAnswered() {
        return this.mAnswered;
    }

    public void setAnswered(boolean v) {
        mAnswered = v;
    }

    public int getTextResId() {
        return mTextResId;
    }

    public void setTextResId(int textResId) {
        mTextResId = textResId;
    }

    public boolean isAnswerTrue() {
        return mAnswerTrue;
    }

    public void setAnswerTrue(boolean answerTrue) {
        mAnswerTrue = answerTrue;
    }

}
