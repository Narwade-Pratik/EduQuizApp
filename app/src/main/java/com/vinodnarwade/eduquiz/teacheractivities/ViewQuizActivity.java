package com.vinodnarwade.eduquiz.teacheractivities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vinodnarwade.eduquiz.R;

import java.util.ArrayList;

public class ViewQuizActivity extends AppCompatActivity {

    EditText etQuestionTopic, etQuestion, etOptionA, etOptionB, etOptionC, etOptionD, etCorrectOption, etMarks, etDifficulty;
    AppCompatButton btnNext, btnPrevious;
    ArrayList<QuestionModel> questionList = new ArrayList<>();
    int currentIndex = 0;

    String quizId, userId;
    DatabaseReference questionRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_quiz);

        etQuestionTopic = findViewById(R.id.etviewquestionquestiontopic);
        etQuestion = findViewById(R.id.etviewquestionquestion);
        etOptionA = findViewById(R.id.etviewquestionoptiona);
        etOptionB = findViewById(R.id.etviewquestionoptionb);
        etOptionC = findViewById(R.id.etviewquestionoptionc);
        etOptionD = findViewById(R.id.etviewquestionoptiond);
        etCorrectOption = findViewById(R.id.etviewquestioncorrectoption);
        etMarks = findViewById(R.id.etviewquestionmarks);
        etDifficulty = findViewById(R.id.etviewquestiondifficulty);

        btnNext = findViewById(R.id.btnviewquestionnext);
        btnPrevious = findViewById(R.id.btnviewquestionprevious);

        disableEditing();

        quizId = getIntent().getStringExtra("quizId");
        userId = getIntent().getStringExtra("userId");

        questionRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userId)
                .child("Quizzes")
                .child(quizId)
                .child("Questions");

        questionRef.get().addOnSuccessListener(snapshot -> {
            for (DataSnapshot ds : snapshot.getChildren()) {
                QuestionModel model = ds.getValue(QuestionModel.class);
                questionList.add(model);
            }

            if (!questionList.isEmpty()) {
                showQuestion(currentIndex);
            } else {
                Toast.makeText(this, "No questions found", Toast.LENGTH_SHORT).show();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentIndex < questionList.size() - 1) {
                currentIndex++;
                showQuestion(currentIndex);
            } else {
                Toast.makeText(this, "Last Question", Toast.LENGTH_SHORT).show();
            }
        });

        btnPrevious.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                showQuestion(currentIndex);
            } else {
                Toast.makeText(this, "First Question", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showQuestion(int index) {
        QuestionModel model = questionList.get(index);
        etQuestionTopic.setText(model.getQuestionTopic());
        etQuestion.setText(model.getQuestion());
        etOptionA.setText(model.getOptionA());
        etOptionB.setText(model.getOptionB());
        etOptionC.setText(model.getOptionC());
        etOptionD.setText(model.getOptionD());
        etCorrectOption.setText(model.getCorrectOption());
        etMarks.setText(String.valueOf(model.getMarks()));
        etDifficulty.setText(model.getDifficulty());
    }

    private void disableEditing() {
        etQuestionTopic.setEnabled(false);
        etQuestion.setEnabled(false);
        etOptionA.setEnabled(false);
        etOptionB.setEnabled(false);
        etOptionC.setEnabled(false);
        etOptionD.setEnabled(false);
        etCorrectOption.setEnabled(false);
        etMarks.setEnabled(false);
        etDifficulty.setEnabled(false);
    }
}