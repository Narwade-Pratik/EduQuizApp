package com.vinodnarwade.eduquiz.studentactivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vinodnarwade.eduquiz.R;
import com.vinodnarwade.eduquiz.teacheractivities.QuestionModel;

import java.util.ArrayList;
import java.util.HashMap;

public class AttemptQuizActivity extends AppCompatActivity {

    private TextView questionText;
    private TextView timerText;

    private RadioGroup optionsGroup;

    private RadioButton option1;
    private RadioButton option2;
    private RadioButton option3;
    private RadioButton option4;

    private AppCompatButton nextBtn;
    private AppCompatButton prevBtn;
    private AppCompatButton submitBtn;

    private final ArrayList<QuestionModel> questionList =
            new ArrayList<>();

    private final HashMap<String, String> selectedAnswers =
            new HashMap<>();

    private int currentIndex = 0;

    // Normal quiz information
    private String quizId;
    private String teacherId;

    // Logged-in student
    private String userId;

    // Timer
    private long totalTimeMillis;
    private long timeTakenInMillis;
    private CountDownTimer countDownTimer;

    private SharedPreferences sharedPreferences;

    // =========================================================
    // CUSTOMIZED QUIZ VARIABLES
    // =========================================================

    private boolean isCustomizedQuiz = false;

    private ArrayList<QuestionModel> customQuestions;

    private String customQuizId;
    private String customSubject;
    private String customChapter;
    private String customTopic;
    private String customDifficulty;


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_attempt_quiz);

        // -----------------------------------------------------
        // Normal quiz information
        // -----------------------------------------------------

        quizId = getIntent().getStringExtra("quizId");

        teacherId = getIntent().getStringExtra("teacherId");

        // -----------------------------------------------------
        // Customized quiz information
        // -----------------------------------------------------

        isCustomizedQuiz =
                getIntent().getBooleanExtra(
                        "isCustomizedQuiz",
                        false
                );

        customQuizId =
                getIntent().getStringExtra(
                        "customQuizId"
                );

        customSubject =
                getIntent().getStringExtra(
                        "customSubject"
                );

        customChapter =
                getIntent().getStringExtra(
                        "customChapter"
                );

        customTopic =
                getIntent().getStringExtra(
                        "customTopic"
                );

        customDifficulty =
                getIntent().getStringExtra(
                        "customDifficulty"
                );

        // -----------------------------------------------------
        // Parcelable class loader
        // -----------------------------------------------------

        getIntent().setExtrasClassLoader(
                QuestionModel.class.getClassLoader()
        );

        // -----------------------------------------------------
        // Get customized questions
        // -----------------------------------------------------

        customQuestions =
                getIntent().getParcelableArrayListExtra(
                        "customQuestions"
                );

        // -----------------------------------------------------
        // Get logged-in student
        // -----------------------------------------------------

        sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        userId =
                sharedPreferences.getString(
                        "userId",
                        ""
                );

        // -----------------------------------------------------
        // Initialize UI
        // -----------------------------------------------------

        initViews();

        // -----------------------------------------------------
        // Decide quiz type
        // -----------------------------------------------------

        if (isCustomizedQuiz) {

            loadCustomizedQuestions();

        } else {

            loadQuizMetaAndStart();
        }
    }


    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initViews() {

        questionText =
                findViewById(
                        R.id.tvAttemptQuizQuestion
                );

        timerText =
                findViewById(
                        R.id.timerTextViewAttemptQuiz
                );

        optionsGroup =
                findViewById(
                        R.id.optionsRadioGroupAttemptQuiz
                );

        option1 =
                findViewById(
                        R.id.radioButtonAttemptQuizOptionA
                );

        option2 =
                findViewById(
                        R.id.radioButtonAttemptQuizOptionB
                );

        option3 =
                findViewById(
                        R.id.radioButtonAttemptQuizOptionC
                );

        option4 =
                findViewById(
                        R.id.radioButtonAttemptQuizOptionD
                );

        nextBtn =
                findViewById(
                        R.id.btnAttemptQuizNext
                );

        prevBtn =
                findViewById(
                        R.id.btnAttemptQuizPrevious
                );

        submitBtn =
                findViewById(
                        R.id.btnAttemptQuizSubmitButton
                );


        nextBtn.setOnClickListener(
                v -> goToNextQuestion()
        );

        prevBtn.setOnClickListener(
                v -> goToPreviousQuestion()
        );

        submitBtn.setOnClickListener(
                v -> submitQuiz()
        );
    }


    // =========================================================
    // NORMAL QUIZ
    // =========================================================

    private void loadQuizMetaAndStart() {

        if (teacherId == null || quizId == null) {

            Toast.makeText(
                    this,
                    "Quiz information not found.",
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return;
        }

        DatabaseReference quizRef =
                FirebaseDatabase.getInstance()
                        .getReference("Users")
                        .child(teacherId)
                        .child("Quizzes")
                        .child(quizId);


        quizRef.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            DataSnapshot snapshot) {

                        if (!snapshot.exists()) {

                            Toast.makeText(
                                    AttemptQuizActivity.this,
                                    "Quiz not found.",
                                    Toast.LENGTH_LONG
                            ).show();

                            finish();

                            return;
                        }


                        Long duration =
                                snapshot
                                        .child("durationInMinutes")
                                        .getValue(Long.class);


                        if (duration != null) {

                            totalTimeMillis =
                                    duration * 60 * 1000L;

                        } else {

                            totalTimeMillis =
                                    10 * 60 * 1000L;
                        }


                        loadQuestions();

                        startTimer();
                    }


                    @Override
                    public void onCancelled(
                            DatabaseError error) {

                        Toast.makeText(
                                AttemptQuizActivity.this,
                                "Failed to load quiz info: "
                                        + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }


    // =========================================================
    // LOAD NORMAL QUIZ QUESTIONS
    // =========================================================

    private void loadQuestions() {

        DatabaseReference ref =
                FirebaseDatabase.getInstance()
                        .getReference("Users")
                        .child(teacherId)
                        .child("Quizzes")
                        .child(quizId)
                        .child("Questions");


        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            DataSnapshot snapshot) {

                        questionList.clear();

                        for (DataSnapshot snap :
                                snapshot.getChildren()) {

                            QuestionModel question =
                                    snap.getValue(
                                            QuestionModel.class
                                    );

                            if (question != null) {

                                questionList.add(question);
                            }
                        }


                        if (!questionList.isEmpty()) {

                            currentIndex = 0;

                            showQuestion(currentIndex);

                        } else {

                            Toast.makeText(
                                    AttemptQuizActivity.this,
                                    "No questions found.",
                                    Toast.LENGTH_LONG
                            ).show();

                            finish();
                        }
                    }


                    @Override
                    public void onCancelled(
                            DatabaseError error) {

                        Toast.makeText(
                                AttemptQuizActivity.this,
                                "Error: "
                                        + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }


    // =========================================================
    // CUSTOMIZED QUIZ
    // =========================================================

    private void loadCustomizedQuestions() {

        if (customQuizId == null
                || customQuizId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Customized quiz ID not found.",
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return;
        }


        if (customQuestions == null
                || customQuestions.isEmpty()) {

            Toast.makeText(
                    this,
                    "No customized questions found.",
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return;
        }


        questionList.clear();

        questionList.addAll(
                customQuestions
        );


        /*
         * 1 minute per question.
         */
        totalTimeMillis =
                questionList.size()
                        * 60
                        * 1000L;


        currentIndex = 0;

        startTimer();

        showQuestion(currentIndex);
    }


    // =========================================================
    // TIMER
    // =========================================================

    private void startTimer() {

        if (totalTimeMillis <= 0) {

            totalTimeMillis =
                    10 * 60 * 1000L;
        }


        countDownTimer =
                new CountDownTimer(
                        totalTimeMillis,
                        1000
                ) {

                    @Override
                    public void onTick(
                            long millisUntilFinished) {

                        timeTakenInMillis =
                                totalTimeMillis
                                        - millisUntilFinished;


                        long minutes =
                                millisUntilFinished
                                        / 60000;

                        long seconds =
                                (millisUntilFinished
                                        % 60000)
                                        / 1000;


                        timerText.setText(
                                String.format(
                                        "%02d:%02d",
                                        minutes,
                                        seconds
                                )
                        );
                    }


                    @Override
                    public void onFinish() {

                        timeTakenInMillis =
                                totalTimeMillis;

                        submitQuiz();
                    }
                }
                        .start();
    }


    // =========================================================
    // SHOW QUESTION
    // =========================================================

    private void showQuestion(int index) {

        if (questionList.isEmpty()) {
            return;
        }


        if (index < 0
                || index >= questionList.size()) {

            return;
        }


        QuestionModel question =
                questionList.get(index);


        questionText.setText(
                question.getQuestion()
        );

        option1.setText(
                question.getOptionA()
        );

        option2.setText(
                question.getOptionB()
        );

        option3.setText(
                question.getOptionC()
        );

        option4.setText(
                question.getOptionD()
        );


        optionsGroup.clearCheck();


        String selected =
                selectedAnswers.get(
                        question.getQuestionId()
                );


        if (selected != null) {

            switch (selected) {

                case "A":
                    option1.setChecked(true);
                    break;

                case "B":
                    option2.setChecked(true);
                    break;

                case "C":
                    option3.setChecked(true);
                    break;

                case "D":
                    option4.setChecked(true);
                    break;
            }
        }


        // Previous button

        if (index == 0) {

            prevBtn.setVisibility(
                    View.GONE
            );

        } else {

            prevBtn.setVisibility(
                    View.VISIBLE
            );
        }


        // Next / Submit

        if (index ==
                questionList.size() - 1) {

            nextBtn.setVisibility(
                    View.GONE
            );

            submitBtn.setVisibility(
                    View.VISIBLE
            );

        } else {

            nextBtn.setVisibility(
                    View.VISIBLE
            );

            submitBtn.setVisibility(
                    View.GONE
            );
        }
    }


    // =========================================================
    // SAVE ANSWER
    // =========================================================

    private void saveAnswer() {

        if (questionList.isEmpty()) {
            return;
        }


        int selectedId =
                optionsGroup.getCheckedRadioButtonId();


        if (selectedId == -1) {
            return;
        }


        String selectedKey = "";


        if (selectedId == option1.getId()) {

            selectedKey = "A";

        } else if (selectedId == option2.getId()) {

            selectedKey = "B";

        } else if (selectedId == option3.getId()) {

            selectedKey = "C";

        } else if (selectedId == option4.getId()) {

            selectedKey = "D";
        }


        if (!selectedKey.isEmpty()) {

            String questionId =
                    questionList
                            .get(currentIndex)
                            .getQuestionId();

            selectedAnswers.put(
                    questionId,
                    selectedKey
            );
        }
    }


    // =========================================================
    // NEXT
    // =========================================================

    private void goToNextQuestion() {

        saveAnswer();

        if (currentIndex
                < questionList.size() - 1) {

            currentIndex++;

            showQuestion(
                    currentIndex
            );
        }
    }


    // =========================================================
    // PREVIOUS
    // =========================================================

    private void goToPreviousQuestion() {

        saveAnswer();

        if (currentIndex > 0) {

            currentIndex--;

            showQuestion(
                    currentIndex
            );
        }
    }


    // =========================================================
    // SUBMIT QUIZ
    // =========================================================

    private void submitQuiz() {

        if (questionList.isEmpty()) {
            return;
        }

        saveAnswer();

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        int score = 0;

        for (QuestionModel question : questionList) {

            String selected =
                    selectedAnswers.get(
                            question.getQuestionId()
                    );

            String correct =
                    question.getCorrectOption();

            if (selected != null
                    && correct != null
                    && selected.trim()
                    .equalsIgnoreCase(correct.trim())) {

                score += question.getMarks();
            }
        }

        // Make a final copy for use inside Firebase callback
        final int finalScore = score;


        // =====================================================
        // CUSTOMIZED QUIZ RESULT
        // =====================================================

        if (isCustomizedQuiz) {

            saveCustomizedQuizResult(finalScore);

            return;
        }


        // =====================================================
        // NORMAL QUIZ RESULT
        // =====================================================

        HashMap<String, Object> resultMap =
                new HashMap<>();

        resultMap.put(
                "score",
                finalScore
        );

        resultMap.put(
                "numberOfQuestions",
                questionList.size()
        );

        resultMap.put(
                "timeTakenMillis",
                timeTakenInMillis
        );

        resultMap.put(
                "studentId",
                userId
        );

        resultMap.put(
                "answers",
                new HashMap<>(
                        selectedAnswers
                )
        );


        DatabaseReference resultRef =
                FirebaseDatabase.getInstance()
                        .getReference("Users")
                        .child(teacherId)
                        .child("Quizzes")
                        .child(quizId)
                        .child("AttemptedBy")
                        .child(userId);


        resultRef.setValue(resultMap)
                .addOnCompleteListener(
                        task -> {

                            if (task.isSuccessful()) {

                                Toast.makeText(
                                        this,
                                        "Result saved successfully!",
                                        Toast.LENGTH_SHORT
                                ).show();

                                openResultActivity(finalScore);

                            } else {

                                Toast.makeText(
                                        this,
                                        "Failed to save result.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                );
    }


    // =========================================================
    // SAVE CUSTOMIZED QUIZ RESULT
    // =========================================================

    private void saveCustomizedQuizResult(int score) {

        HashMap<String, Object> resultMap =
                new HashMap<>();

        resultMap.put(
                "quizType",
                "customized"
        );

        resultMap.put(
                "customQuizId",
                customQuizId
        );

        resultMap.put(
                "studentId",
                userId
        );

        resultMap.put(
                "subject",
                customSubject
        );

        resultMap.put(
                "chapter",
                customChapter
        );

        resultMap.put(
                "topic",
                customTopic
        );

        resultMap.put(
                "difficulty",
                customDifficulty
        );

        resultMap.put(
                "score",
                score
        );

        resultMap.put(
                "numberOfQuestions",
                questionList.size()
        );

        resultMap.put(
                "timeTakenMillis",
                timeTakenInMillis
        );

        resultMap.put(
                "answers",
                new HashMap<>(selectedAnswers)
        );


        /*
         * Save customized quiz in student's MyQuizzes node.
         *
         * Users
         *   └── studentId
         *       └── MyQuizzes
         *           └── customQuizId
         */

        DatabaseReference resultRef =
                FirebaseDatabase.getInstance()
                        .getReference("Users")
                        .child(userId)
                        .child("MyQuizzes")
                        .child(customQuizId);


        resultRef.setValue(
                resultMap
        ).addOnCompleteListener(
                task -> {

                    if (task.isSuccessful()) {

                        openResultActivity(score);

                    } else {

                        Toast.makeText(
                                this,
                                "Failed to save quiz result.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }


    // =========================================================
    // OPEN RESULT ACTIVITY
    // =========================================================

    private void openResultActivity(int score) {

        Intent intent =
                new Intent(
                        AttemptQuizActivity.this,
                        ResultActivity.class
                );

        intent.putExtra("score", score);

        intent.putExtra(
                "numberOfQuestions",
                questionList.size()
        );

        intent.putExtra(
                "timeTakenMillis",
                timeTakenInMillis
        );

        intent.putExtra(
                "isCustomizedQuiz",
                isCustomizedQuiz
        );

        intent.putExtra(
                "customQuizId",
                customQuizId
        );

        intent.putParcelableArrayListExtra(
                "questionList",
                questionList
        );

        intent.putExtra(
                "selectedAnswers",
                selectedAnswers
        );

        intent.putExtra("customSubject", customSubject);
        intent.putExtra("customChapter", customChapter);
        intent.putExtra("customTopic", customTopic);
        intent.putExtra("customDifficulty", customDifficulty);

        startActivity(intent);

        finish();
    }


    // =========================================================
    // DESTROY
    // =========================================================

    @Override
    protected void onDestroy() {

        if (countDownTimer != null) {

            countDownTimer.cancel();
        }

        super.onDestroy();
    }
}