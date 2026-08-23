package com.vinodnarwade.eduquiz.teacheractivities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vinodnarwade.eduquiz.R;

import java.util.ArrayList;

public class AddQuestionActivity extends AppCompatActivity {

    EditText etQuestionTopic, etQuestion, etOptionA, etOptionB,
            etOptionC, etOptionD, etCorrectOption, etMarks, etDifficulty;

    AppCompatButton btnNextQuestion;

    DatabaseReference questionRef;

    int questionCount;
    int count = 0;

    String quizId;
    String userId;

    ArrayList<QuestionModel> questionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        // Get data from previous Activity
        quizId = getIntent().getStringExtra("quizId");
        userId = getIntent().getStringExtra("userId");
        questionCount = getIntent().getIntExtra("noOfQ", 0);

        // Check required data
        if (quizId == null || userId == null || questionCount <= 0) {
            Toast.makeText(
                    this,
                    "Invalid quiz information",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        // Firebase reference
        questionRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userId)
                .child("Quizzes")
                .child(quizId)
                .child("Questions");

        // Initialize views
        etQuestionTopic = findViewById(R.id.etaddquestionquestiontopic);
        etQuestion = findViewById(R.id.etaddquestionquestion);
        etOptionA = findViewById(R.id.etaddquestionoptiona);
        etOptionB = findViewById(R.id.etaddquestionoptionb);
        etOptionC = findViewById(R.id.etaddquestionoptionc);
        etOptionD = findViewById(R.id.etaddquestionoptiond);
        etCorrectOption = findViewById(R.id.etaddquestioncorrectoption);
        etMarks = findViewById(R.id.etaddquestionmarks);
        etDifficulty = findViewById(R.id.etaddquestiondifficulty);

        btnNextQuestion = findViewById(R.id.btnaddquestionadd);

        // Add question button
        btnNextQuestion.setOnClickListener(v -> addQuestion());
    }

    private void addQuestion() {

        String questionTopic = etQuestionTopic.getText().toString().trim();
        String question = etQuestion.getText().toString().trim();

        String optionA = etOptionA.getText().toString().trim();
        String optionB = etOptionB.getText().toString().trim();
        String optionC = etOptionC.getText().toString().trim();
        String optionD = etOptionD.getText().toString().trim();
        String difficulty = etDifficulty.getText().toString().trim();
        String correctOption = etCorrectOption.getText().toString().trim();
        String marksText = etMarks.getText().toString().trim();

        // Validate empty fields
        if (questionTopic.isEmpty()
                || question.isEmpty()
                || optionA.isEmpty()
                || optionB.isEmpty()
                || optionC.isEmpty()
                || optionD.isEmpty()
                || correctOption.isEmpty()
                || marksText.isEmpty()
                || difficulty.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Validate correct option
        correctOption = correctOption.toUpperCase();

        if (!correctOption.equals("A")
                && !correctOption.equals("B")
                && !correctOption.equals("C")
                && !correctOption.equals("D")) {

            Toast.makeText(
                    this,
                    "Correct option must be A, B, C or D",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Convert marks to integer
        int marks;

        try {
            marks = Integer.parseInt(marksText);

            if (marks <= 0) {
                Toast.makeText(
                        this,
                        "Marks must be greater than 0",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Please enter valid marks",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Generate Firebase question ID
        String questionId = questionRef.push().getKey();

        if (questionId == null) {
            Toast.makeText(
                    this,
                    "Failed to generate question ID",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Create QuestionModel
        QuestionModel model = new QuestionModel(
                questionId,
                quizId,
                questionTopic,
                question,
                optionA,
                optionB,
                optionC,
                optionD,
                correctOption,
                marks,
                difficulty
        );

        // Save question to Firebase
        questionRef.child(questionId)
                .setValue(model)
                .addOnSuccessListener(unused -> {

                    // Keep question locally for ReviewQuizActivity
                    questionList.add(model);
                    count++;

                    Toast.makeText(
                            AddQuestionActivity.this,
                            "Question " + count + " added successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    clearFields();

                    // If all questions are added
                    if (count == questionCount) {

                        Intent intent = new Intent(
                                AddQuestionActivity.this,
                                ReviewQuizActivity.class
                        );

                        intent.putParcelableArrayListExtra(
                                "questionList",
                                questionList
                        );

                        intent.putExtra("quizId", quizId);
                        intent.putExtra("userId", userId);

                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            AddQuestionActivity.this,
                            "Failed to add question: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void clearFields() {

        etQuestionTopic.setText("");
        etQuestion.setText("");
        etOptionA.setText("");
        etOptionB.setText("");
        etOptionC.setText("");
        etOptionD.setText("");
        etCorrectOption.setText("");
        etMarks.setText("");
        etDifficulty.setText("");

        etQuestionTopic.requestFocus();
    }
}