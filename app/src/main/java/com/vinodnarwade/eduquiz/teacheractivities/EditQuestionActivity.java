package com.vinodnarwade.eduquiz.teacheractivities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vinodnarwade.eduquiz.R;

public class EditQuestionActivity extends AppCompatActivity {

    private EditText etQuestionTopic;
    private EditText etQuestion;
    private EditText etOptionA;
    private EditText etOptionB;
    private EditText etOptionC;
    private EditText etOptionD;
    private EditText etCorrectOption;
    private EditText etMarks;

    private AppCompatButton btnUpdateQuestion;

    private String userId;
    private String className;
    private String subject;
    private String chapter;
    private String topic;
    private String difficulty;
    private String questionId;

    private DatabaseReference questionRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Edit Question");

        setContentView(R.layout.activity_edit_question);

        receiveIntentData();

        if (!validateIntentData()) {
            return;
        }

        initializeViews();

        createFirebaseReference();

        loadQuestion();

        btnUpdateQuestion.setOnClickListener(v ->
                updateQuestion()
        );
    }

    private void receiveIntentData() {

        userId = getIntent().getStringExtra("userId");
        className = getIntent().getStringExtra("className");
        subject = getIntent().getStringExtra("subject");
        chapter = getIntent().getStringExtra("chapter");
        topic = getIntent().getStringExtra("topic");
        difficulty = getIntent().getStringExtra("difficulty");
        questionId = getIntent().getStringExtra("questionId");
    }

    private boolean validateIntentData() {

        if (userId == null
                || className == null
                || subject == null
                || chapter == null
                || topic == null
                || difficulty == null
                || questionId == null) {

            Toast.makeText(
                    this,
                    "Question information not found",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return false;
        }

        return true;
    }

    private void initializeViews() {

        etQuestionTopic =
                findViewById(R.id.etEditQBQuestionTopic);

        etQuestion =
                findViewById(R.id.etEditQBQuestion);

        etOptionA =
                findViewById(R.id.etEditQBOptionA);

        etOptionB =
                findViewById(R.id.etEditQBOptionB);

        etOptionC =
                findViewById(R.id.etEditQBOptionC);

        etOptionD =
                findViewById(R.id.etEditQBOptionD);

        etCorrectOption =
                findViewById(R.id.etEditQBCorrectOption);

        etMarks =
                findViewById(R.id.etEditQBMarks);

        btnUpdateQuestion =
                findViewById(R.id.btnUpdateQBQuestion);
    }

    private void createFirebaseReference() {

        questionRef = FirebaseDatabase.getInstance()
                .getReference("QuestionBank")
                .child(userId)
                .child(className)
                .child(subject)
                .child(chapter)
                .child(topic)
                .child(difficulty)
                .child(questionId);
    }

    private void loadQuestion() {

        questionRef.get()
                .addOnSuccessListener(snapshot -> {

                    QuestionBankQuestionModel question =
                            snapshot.getValue(
                                    QuestionBankQuestionModel.class
                            );

                    if (question == null) {

                        Toast.makeText(
                                this,
                                "Question not found",
                                Toast.LENGTH_LONG
                        ).show();

                        finish();
                        return;
                    }

                    etQuestionTopic.setText(
                            question.getQuestionTopic()
                    );

                    etQuestion.setText(
                            question.getQuestion()
                    );

                    etOptionA.setText(
                            question.getOptionA()
                    );

                    etOptionB.setText(
                            question.getOptionB()
                    );

                    etOptionC.setText(
                            question.getOptionC()
                    );

                    etOptionD.setText(
                            question.getOptionD()
                    );

                    etCorrectOption.setText(
                            question.getCorrectOption()
                    );

                    etMarks.setText(
                            String.valueOf(
                                    question.getMarks()
                            )
                    );
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to load question: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void updateQuestion() {

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
                etCorrectOption.getText()
                        .toString()
                        .trim()
                        .toUpperCase();

        String marksText =
                etMarks.getText().toString().trim();

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

        QuestionBankQuestionModel updatedQuestion =
                new QuestionBankQuestionModel(
                        questionId,
                        userId,
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

        questionRef
                .setValue(updatedQuestion)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Question updated successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to update question: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}