package com.vinodnarwade.eduquiz.teacheractivities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vinodnarwade.eduquiz.R;

public class AddQueToQBActivity extends AppCompatActivity {

    private TextView tvQuestionBankInfo;
    private TextView tvDifficulty;

    private EditText etQuestionTopic;
    private EditText etQuestion;
    private EditText etOptionA;
    private EditText etOptionB;
    private EditText etOptionC;
    private EditText etOptionD;
    private EditText etCorrectOption;
    private EditText etMarks;

    private AppCompatButton btnAddQuestion;

    private DatabaseReference questionRef;

    private String userId;
    private String className;
    private String subject;
    private String chapter;
    private String topic;
    private String difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Add Question");

        setContentView(R.layout.activity_add_que_to_qbactivity);

        // Get data from QuestionBankActivity
        userId = getIntent().getStringExtra("userId");
        className = getIntent().getStringExtra("className");
        subject = getIntent().getStringExtra("subject");
        chapter = getIntent().getStringExtra("chapter");
        topic = getIntent().getStringExtra("topic");
        difficulty = getIntent().getStringExtra("difficulty");

        // Validate received data
        if (userId == null
                || className == null
                || subject == null
                || chapter == null
                || topic == null
                || difficulty == null) {

            Toast.makeText(
                    this,
                    "Invalid question bank information",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        initializeViews();

        displayQuestionBankInfo();

        createFirebaseReference();

        btnAddQuestion.setOnClickListener(v -> addQuestion());
    }

    private void initializeViews() {

        tvQuestionBankInfo = findViewById(R.id.tvQuestionBankInfo);
        tvDifficulty = findViewById(R.id.tvDifficulty);

        etQuestionTopic =
                findViewById(R.id.etQBQuestionTopic);

        etQuestion =
                findViewById(R.id.etQBQuestion);

        etOptionA =
                findViewById(R.id.etQBOptionA);

        etOptionB =
                findViewById(R.id.etQBOptionB);

        etOptionC =
                findViewById(R.id.etQBOptionC);

        etOptionD =
                findViewById(R.id.etQBOptionD);

        etCorrectOption =
                findViewById(R.id.etQBCorrectOption);

        etMarks =
                findViewById(R.id.etQBMarks);

        btnAddQuestion =
                findViewById(R.id.btnAddQBQuestion);
    }

    private void displayQuestionBankInfo() {

        String information =
                className + " > "
                        + subject + " > "
                        + chapter + " > "
                        + topic;

        tvQuestionBankInfo.setText(information);

        tvDifficulty.setText(
                "Difficulty: " + difficulty
        );
    }

    private void createFirebaseReference() {

        questionRef = FirebaseDatabase.getInstance()
                .getReference("QuestionBank")
                .child(userId)
                .child(className)
                .child(subject)
                .child(chapter)
                .child(topic)
                .child(difficulty);
    }

    private void addQuestion() {

        String questionTopic =
                etQuestionTopic.getText().toString().trim();

        String question =
                etQuestion.getText().toString().trim();

        String optionA =
                etOptionA.getText().toString().trim();

        String optionB =
                etOptionB.getText().toString().trim();

        String optionC =
                etOptionC.getText().toString().trim();

        String optionD =
                etOptionD.getText().toString().trim();

        String correctOption =
                etCorrectOption.getText().toString().trim()
                        .toUpperCase();

        String marksText =
                etMarks.getText().toString().trim();

        // Validate empty fields
        if (questionTopic.isEmpty()
                || question.isEmpty()
                || optionA.isEmpty()
                || optionB.isEmpty()
                || optionC.isEmpty()
                || optionD.isEmpty()
                || correctOption.isEmpty()
                || marksText.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Validate correct option
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

        // Validate marks
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

        // Generate question ID
        String questionId = questionRef.push().getKey();

        if (questionId == null) {

            Toast.makeText(
                    this,
                    "Failed to generate question ID",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Create Question Bank question
        QuestionBankQuestionModel que =
                new QuestionBankQuestionModel(
                        questionId,
                        className,
                        subject,
                        chapter,
                        topic,
                        difficulty,
                        questionTopic,
                        question,
                        optionA,
                        optionB,
                        optionC,
                        optionD,
                        correctOption,
                        marks
                );

        // Save to Firebase
        questionRef
                .child(questionId)
                .setValue(que)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            AddQueToQBActivity.this,
                            "Question added successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    clearFields();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            AddQueToQBActivity.this,
                            "Failed to add question: "
                                    + e.getMessage(),
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

        etQuestionTopic.requestFocus();
    }
}