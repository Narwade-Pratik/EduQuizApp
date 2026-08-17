package com.vinodnarwade.eduquiz.teacheractivities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vinodnarwade.eduquiz.R;

public class QuestionBankActivity extends AppCompatActivity {

    private TextView tvHierarchy;
    private AppCompatButton btnGrantQuestionBankAccess;
    private AppCompatButton btnAddEasy;
    private AppCompatButton btnManageEasy;

    private AppCompatButton btnAddMedium;
    private AppCompatButton btnManageMedium;

    private AppCompatButton btnAddHard;
    private AppCompatButton btnManageHard;

    private String userId;
    private String className;
    private String subject;
    private String chapter;
    private String topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Question Bank");

        setContentView(R.layout.activity_question_bank);

        // Get data from QuizSchedulerActivity
        userId = getIntent().getStringExtra("userId");
        className = getIntent().getStringExtra("className");
        subject = getIntent().getStringExtra("subject");
        chapter = getIntent().getStringExtra("chapter");
        topic = getIntent().getStringExtra("topic");

        // Check whether required data was received
        if (className == null
                || subject == null
                || chapter == null
                || topic == null) {

            Toast.makeText(
                    this,
                    "Question bank information not found",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        initializeViews();

        displayHierarchy();

        setupButtons();

        btnGrantQuestionBankAccess.setOnClickListener(v ->
                grantQuestionBankAccess()
        );
    }

    private void grantQuestionBankAccess() {

        DatabaseReference accessRef =
                FirebaseDatabase.getInstance()
                        .getReference("QuestionBankAccess")
                        .child(className)
                        .child(userId)
                        .child(subject)
                        .child(chapter)
                        .child(topic);

        accessRef.setValue(true)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            QuestionBankActivity.this,
                            "Question Bank is now available to "
                                    + className + " students",
                            Toast.LENGTH_LONG
                    ).show();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            QuestionBankActivity.this,
                            "Failed to grant access: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void initializeViews() {

        tvHierarchy = findViewById(R.id.tvHierarchy);

        btnAddEasy = findViewById(R.id.btnAddEasy);
        btnManageEasy = findViewById(R.id.btnManageEasy);

        btnAddMedium = findViewById(R.id.btnAddMedium);
        btnManageMedium = findViewById(R.id.btnManageMedium);

        btnAddHard = findViewById(R.id.btnAddHard);
        btnManageHard = findViewById(R.id.btnManageHard);
        btnGrantQuestionBankAccess =
                findViewById(R.id.btnGrantQuestionBankAccess);
    }

    private void displayHierarchy() {

        String hierarchy =
                className + "  >  "
                        + subject + "  >  "
                        + chapter + "  >  "
                        + topic;

        tvHierarchy.setText(hierarchy);
    }

    private void setupButtons() {

        btnAddEasy.setOnClickListener(v ->
                openAddQuestionActivity("Easy")
        );

        btnAddMedium.setOnClickListener(v ->
                openAddQuestionActivity("Medium")
        );

        btnAddHard.setOnClickListener(v ->
                openAddQuestionActivity("Hard")
        );

        btnManageEasy.setOnClickListener(v ->
                openManageQuestionsActivity("Easy")
        );

        btnManageMedium.setOnClickListener(v ->
                openManageQuestionsActivity("Medium")
        );

        btnManageHard.setOnClickListener(v ->
                openManageQuestionsActivity("Hard")
        );
    }

    private void openManageQuestionsActivity(String difficulty) {

        Intent intent = new Intent(
                QuestionBankActivity.this,
                ManageQuestionsActivity.class
        );

        intent.putExtra("userId", userId);
        intent.putExtra("className", className);
        intent.putExtra("subject", subject);
        intent.putExtra("chapter", chapter);
        intent.putExtra("topic", topic);
        intent.putExtra("difficulty", difficulty);

        startActivity(intent);
    }


    private void openAddQuestionActivity(String difficulty) {

        Intent intent = new Intent(
                QuestionBankActivity.this,
                AddQueToQBActivity.class
        );

        intent.putExtra("userId", userId);
        intent.putExtra("className", className);
        intent.putExtra("subject", subject);
        intent.putExtra("chapter", chapter);
        intent.putExtra("topic", topic);
        intent.putExtra("difficulty", difficulty);

        startActivity(intent);
    }
    private void showComingSoon(String difficulty) {

        Toast.makeText(
                this,
                difficulty + " question management will be added next",
                Toast.LENGTH_SHORT
        ).show();
    }
}