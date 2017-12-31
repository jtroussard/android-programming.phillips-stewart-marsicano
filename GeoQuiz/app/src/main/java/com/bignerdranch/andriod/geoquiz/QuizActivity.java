package com.bignerdranch.andriod.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String KEY_ANSWERED = "answered";
    private static final String KEY_SCORE = "player_score";
    private static final int REQUEST_CODE_CHEAT = 0;

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mPrevButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;
    private TextView mScoreboard;
    private boolean mIsCheater;

    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true)
    };
    private boolean[] mAnswered = new boolean[mQuestionBank.length]; // length property represents allocated size
    private boolean[] mCheatedOn = new boolean[mQuestionBank.length]; // length property represents allocated size

    private int mCurrentIndex = 0;
    private int mScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        Log.i(TAG, mAnswered.toString());
        setContentView(R.layout.activity_quiz);

        mTrueButton = (Button) findViewById(R.id.true_button);
        mFalseButton = (Button) findViewById(R.id.false_button);

        // Retrieve data from saved state if present
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mAnswered = savedInstanceState.getBooleanArray(KEY_ANSWERED);
            mScore = savedInstanceState.getInt(KEY_SCORE, 0);
        }

        // Text views
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
        mScoreboard = (TextView) findViewById(R.id.score_view);
        mScoreboard.setText(calculateScore(mScore, mQuestionBank.length));
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        // Next button
        mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length; // Mod allows for loop
                updateQuestion();
                mIsCheater = false;
            }
        });

        // Prev button
        mPrevButton = (Button) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentIndex > 0) {
                    mCurrentIndex = (mCurrentIndex - 1);
                } else {
                    mCurrentIndex = mQuestionBank.length-1;
                }
                updateQuestion();
            }
        });

        // Cheat button
        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });
        updateQuestion();

        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
                deactivateButtons();
                updateScoreboard();
            }
        });
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
                deactivateButtons();
                updateScoreboard();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) { // user cancelled activity
            Log.i(TAG, "User cancelled activity (I hope)");
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT) { // child is cheatactivity
            if (data == null) {
                Log.i(TAG, "User did NOT press show answer.");
                return;
            }
            Log.i(TAG, "CHEATER!!!");
            mIsCheater = CheatActivity.wasAnsweredShown(data);
            mCheatedOn[mCurrentIndex] = true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState() called");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putBooleanArray(KEY_ANSWERED, mAnswered);
        savedInstanceState.putInt(KEY_SCORE, mScore);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestory() called");
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
        if (mAnswered[mCurrentIndex] == true) {
            mTrueButton.setEnabled(false);
            mFalseButton.setEnabled(false);
        } else {
            mTrueButton.setEnabled(true);
            mFalseButton.setEnabled(true);
        }
    }

    private void updateScoreboard() {
        String newScore = calculateScore(mScore, mQuestionBank.length);
        mScoreboard.setText(newScore);
    }

    private void checkAnswer(boolean userPressedTrue) {
        mAnswered[mCurrentIndex] = true; // register answered

        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = 0;

        if (mIsCheater) {
            messageResId = R.string.judgment_toast;
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                mScore++;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
        // check if the quiz is over
        if (isQuizFinished(mAnswered)) {
            String s1 = "Your Score is ... ".concat(calculateScore(mScore, mQuestionBank.length));
            Toast t = Toast.makeText(this, s1, Toast.LENGTH_SHORT);
            t.setGravity(Gravity.TOP,0,0);
            t.show();
        }
    }

    private void deactivateButtons() {
        mTrueButton.setEnabled(false);
        mFalseButton.setEnabled(false);
    }

    private String calculateScore(int score, int numOfQuestions) {
        double raw = Double.valueOf(score) / numOfQuestions;
        double exp = raw * 100;
        String result = String.valueOf(Math.round(exp));
        return (result.concat("%"));
    }

    private boolean isQuizFinished(boolean[] answered) {
        for (boolean v : answered) {
            if (v == false) {
                return false;
            }
        }
        return true;
    }

}
