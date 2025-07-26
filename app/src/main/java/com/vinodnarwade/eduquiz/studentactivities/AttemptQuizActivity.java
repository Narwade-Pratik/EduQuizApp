package com.vinodnarwade.eduquiz.studentactivities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

    TextView questionText, timerText;
    RadioGroup optionsGroup;
    RadioButton option1, option2, option3, option4;
    Button nextBtn, prevBtn, submitBtn;


    ArrayList<QuestionModel> questionList = new ArrayList<>();
    int currentIndex = 0;

    HashMap<String, String> selectedAnswers = new HashMap<>();

    String quizId, teacherId, userId;
    long totalTimeMillis; // 10 minutes
    CountDownTimer countDownTimer;
    long timeTakenInMillis;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attempt_quiz);

        quizId = getIntent().getStringExtra("quizId");
        teacherId = getIntent().getStringExtra("teacherId");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        userId = sharedPreferences.getString("userId","");

        initViews();
        loadQuizMetaAndStart(); // ✅ New method
    }

    private void loadQuizMetaAndStart() {
        DatabaseReference quizRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(teacherId).child("Quizzes").child(quizId);

        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Long duration = snapshot.child("durationInMinutes").getValue(Long.class);
                    if (duration != null) {
                        totalTimeMillis = duration * 60 * 1000; // convert to millis
                    } else {
                        totalTimeMillis = 10 * 60 * 1000; // default 10 minutes
                    }

                    startTimer(); // ✅ Start timer after getting time
                    loadQuestions(); // ✅ Load questions
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(AttemptQuizActivity.this, "Failed to load quiz info", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        questionText = findViewById(R.id.tvAttemptQuizQuestion);
        timerText = findViewById(R.id.timerTextViewAttemptQuiz);
        optionsGroup = findViewById(R.id.optionsRadioGroupAttemptQuiz);
        option1 = findViewById(R.id.radioButtonAttemptQuizOptionA);
        option2 = findViewById(R.id.radioButtonAttemptQuizOptionB);
        option3 = findViewById(R.id.radioButtonAttemptQuizOptionC);
        option4 = findViewById(R.id.radioButtonAttemptQuizOptionD);
        nextBtn = findViewById(R.id.btnAttemptQuizNext);
        prevBtn = findViewById(R.id.btnAttemptQuizPrevious);
        submitBtn = findViewById(R.id.btnAttemptQuizSubmitButton);

        nextBtn.setOnClickListener(v -> goToNextQuestion());
        prevBtn.setOnClickListener(v -> goToPreviousQuestion());
        submitBtn.setOnClickListener(v -> submitQuiz());
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(totalTimeMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                timeTakenInMillis = totalTimeMillis - millisUntilFinished;
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                timerText.setText(String.format("%02d:%02d", minutes, seconds));
            }

            public void onFinish() {
                submitQuiz(); // auto submit when time is over
            }
        }.start();
    }

    private void loadQuestions() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(teacherId).child("Quizzes")
                .child(quizId).child("Questions");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    QuestionModel q = snap.getValue(QuestionModel.class);
                    if (q != null) {
                        questionList.add(q);
                    }
                }
                if (!questionList.isEmpty()) {
                    showQuestion(currentIndex);
                }
            }

            public void onCancelled(DatabaseError error) {
                Toast.makeText(AttemptQuizActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showQuestion(int index) {
        QuestionModel q = questionList.get(index);
        questionText.setText(q.getQuestion());
        option1.setText(q.getOptionA());
        option2.setText(q.getOptionB());
        option3.setText(q.getOptionC());
        option4.setText(q.getOptionD());

        optionsGroup.clearCheck();
        String selected = selectedAnswers.get(q.getQuestionId());
        if (selected != null) {
            switch (selected) {
                case "A": option1.setChecked(true); break;
                case "B": option2.setChecked(true); break;
                case "C": option3.setChecked(true); break;
                case "D": option4.setChecked(true); break;
            }
        }


        prevBtn.setVisibility(index == 0 ? View.GONE : View.VISIBLE);
        nextBtn.setVisibility(index == questionList.size() - 1 ? View.GONE : View.VISIBLE);
        submitBtn.setVisibility(index == questionList.size() - 1 ? View.VISIBLE : View.GONE);
    }

    private void saveAnswer() {
        int selectedId = optionsGroup.getCheckedRadioButtonId();
        if (selectedId != -1) {
            String selectedKey = "";
            if (selectedId == option1.getId()) selectedKey = "A";
            else if (selectedId == option2.getId()) selectedKey = "B";
            else if (selectedId == option3.getId()) selectedKey = "C";
            else if (selectedId == option4.getId()) selectedKey = "D";

            selectedAnswers.put(questionList.get(currentIndex).getQuestionId(), selectedKey);
        }
    }


    private void goToNextQuestion() {
        saveAnswer();
        if (currentIndex < questionList.size() - 1) {
            currentIndex++;
            showQuestion(currentIndex);
        }
    }

    private void goToPreviousQuestion() {
        saveAnswer();
        if (currentIndex > 0) {
            currentIndex--;
            showQuestion(currentIndex);
        }
    }

    private void submitQuiz() {
        saveAnswer();
        countDownTimer.cancel();

        int score = 0;
        for (QuestionModel q : questionList) {
            String selected = selectedAnswers.get(q.getQuestionId());
            String correct = q.getCorrectOption();
            if (selected != null && correct != null &&
                    selected.trim().equalsIgnoreCase(correct.trim())) {
                score += q.getMarks(); // ✅ add marks instead of just score++
            }
        }


        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("score", score);
        resultMap.put("numberOfQuestions", questionList.size());
        resultMap.put("timeTakenMillis", timeTakenInMillis);
        resultMap.put("studentId", userId);

        // Store selected answers
        HashMap<String, String> answersMap = new HashMap<>(selectedAnswers);
        resultMap.put("answers", answersMap);

        DatabaseReference resultRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(teacherId)
                .child("Quizzes")
                .child(quizId)
                .child("AttemptedBy")
                .child(userId);

        resultRef.setValue(resultMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Result saved successfully!", Toast.LENGTH_SHORT).show();
                // Optionally: Navigate to Result screen
                finish(); // or open a ResultActivity
            } else {
                Toast.makeText(this, "Failed to save result.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
