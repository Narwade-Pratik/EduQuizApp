package com.vinodnarwade.eduquiz.teacheractivities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vinodnarwade.eduquiz.R;

import java.util.ArrayList;

public class AddQuestionActivity extends AppCompatActivity {

    EditText etQuestion, etOptionA, etOptionB, etOptionC, etOptionD, etCorrectOption, etMarks;
    AppCompatButton btnNextQuestion;
    DatabaseReference questionRef;
    int questionCount;
    int count = 0;
    String quizId,userId;
    ArrayList<QuestionModel> questionList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        quizId = getIntent().getStringExtra("quizId");
        userId = getIntent().getStringExtra("userId");
        questionCount = getIntent().getIntExtra("noOfQ", 0); // 0 is default if not found
        questionRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userId)
                .child("Quizzes")
                .child(quizId)
                .child("Questions");


        etQuestion = findViewById(R.id.etaddquestionquestion);
        etOptionA = findViewById(R.id.etaddquestionoptiona);
        etOptionB = findViewById(R.id.etaddquestionoptionb);
        etOptionC = findViewById(R.id.etaddquestionoptionc);
        etOptionD = findViewById(R.id.etaddquestionoptiond);
        etCorrectOption = findViewById(R.id.etaddquestioncorrectoption);
        etMarks = findViewById(R.id.etaddquestionmarks);
        btnNextQuestion = findViewById(R.id.btnaddquestionadd);
        btnNextQuestion.setOnClickListener(v -> {
            addQuestion();
        });
    }

    private void addQuestion() {
        String question = etQuestion.getText().toString();
        String optionA = etOptionA.getText().toString();
        String optionB = etOptionB.getText().toString();
        String optionC = etOptionC.getText().toString();
        String optionD = etOptionD.getText().toString();
        String correctOption = etCorrectOption.getText().toString();
        int marks = Integer.parseInt(etMarks.getText().toString().trim());

        String questionId = questionRef.push().getKey();
        QuestionModel model = new QuestionModel(
                questionId,
                quizId,
                question,
                optionA,
                optionB,
                optionC,
                optionD,
                correctOption,
                marks
        );

        questionList.add(model);
        count++;
        clearFields();

        if(count == questionCount){
            Intent intent = new Intent(this, ReviewQuizActivity.class);
            intent.putParcelableArrayListExtra("questionList", questionList);
            intent.putExtra("quizId", quizId);
            intent.putExtra("userId",userId);
            startActivity(intent);
        }

    }

    private void clearFields() {
        etQuestion.setText("");
        etOptionA.setText("");
        etOptionB.setText("");
        etOptionC.setText("");
        etOptionD.setText("");
        etCorrectOption.setText("");
        etMarks.setText("");
    }
}
