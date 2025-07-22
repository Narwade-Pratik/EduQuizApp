package com.vinodnarwade.eduquiz.teacheractivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vinodnarwade.eduquiz.HomeActivity;
import com.vinodnarwade.eduquiz.R;

import java.util.ArrayList;

public class ReviewQuizActivity extends AppCompatActivity {

    EditText etQuestion, etOptionA, etOptionB, etOptionC, etOptionD, etCorrectOption, etMarks;
    Button btnNext, btnPrevious, btnSubmitAll;
    ArrayList<QuestionModel> questionList;
    int currentIndex = 0;
    String quizId;
    DatabaseReference questionRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_quiz);

        etQuestion = findViewById(R.id.etreviewquestionquestion);
        etOptionA = findViewById(R.id.etreviewquestionoptiona);
        etOptionB = findViewById(R.id.etreviewquestionoptionb);
        etOptionC = findViewById(R.id.etreviewquestionoptionc);
        etOptionD = findViewById(R.id.etreviewquestionoptiond);
        etCorrectOption = findViewById(R.id.etreviewquestioncorrectoption);
        etMarks = findViewById(R.id.etreviewquestionmarks);

        btnNext = findViewById(R.id.btnreviewquestionnext);
        btnPrevious = findViewById(R.id.btnreviewquestionprevious);
        btnSubmitAll = findViewById(R.id.btnreviewsubmitquiz);

        questionList = getIntent().getParcelableArrayListExtra("questionList");
        quizId = getIntent().getStringExtra("quizId");

        questionRef = FirebaseDatabase.getInstance().getReference("Questions").child(quizId);

        // Pehla question dikhaye
        showQuestion(currentIndex);

        btnNext.setOnClickListener(v -> {
            saveCurrentQuestion();
            if (currentIndex < questionList.size() - 1) {
                currentIndex++;
                showQuestion(currentIndex);
            } else {
                Toast.makeText(this, "Last Question", Toast.LENGTH_SHORT).show();
            }
        });

        btnPrevious.setOnClickListener(v -> {
            saveCurrentQuestion();
            if (currentIndex > 0) {
                currentIndex--;
                showQuestion(currentIndex);
            } else {
                Toast.makeText(this, "First Question", Toast.LENGTH_SHORT).show();
            }
        });

        btnSubmitAll.setOnClickListener(v -> {
            saveCurrentQuestion(); // Last wale ko bhi save karo
            for (QuestionModel model : questionList) {
                if (model.getQuestionId() == null || model.getQuestionId().isEmpty()) {
                    String id = questionRef.push().getKey();
                    model.setQuestionId(id);
                }
                questionRef.child(model.getQuestionId()).setValue(model);
            }
            Toast.makeText(this, "All questions submitted!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ReviewQuizActivity.this, HomeActivity.class); // ya jo bhi home hai
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        });
    }

    private void showQuestion(int index) {
        QuestionModel model = questionList.get(index);
        etQuestion.setText(model.getQuestion());
        etOptionA.setText(model.getOptionA());
        etOptionB.setText(model.getOptionB());
        etOptionC.setText(model.getOptionC());
        etOptionD.setText(model.getOptionD());
        etCorrectOption.setText(model.getCorrectOption());
        etMarks.setText(String.valueOf(model.getMarks()));

        // Show submit button only on last question
        if (index == questionList.size() - 1) {
            btnSubmitAll.setVisibility(View.VISIBLE);
        } else {
            btnSubmitAll.setVisibility(View.GONE);
        }
    }

    private void saveCurrentQuestion() {
        QuestionModel model = questionList.get(currentIndex);
        model.setQuestion(etQuestion.getText().toString());
        model.setOptionA(etOptionA.getText().toString());
        model.setOptionB(etOptionB.getText().toString());
        model.setOptionC(etOptionC.getText().toString());
        model.setOptionD(etOptionD.getText().toString());
        model.setCorrectOption(etCorrectOption.getText().toString());
        model.setMarks(Integer.parseInt(etMarks.getText().toString().trim()));
    }
}
