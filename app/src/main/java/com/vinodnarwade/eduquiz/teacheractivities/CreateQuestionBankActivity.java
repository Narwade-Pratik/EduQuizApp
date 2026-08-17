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

public class CreateQuestionBankActivity extends AppCompatActivity {

    private EditText etClassName;
    private EditText etSubject;
    private EditText etChapter;
    private EditText etTopic;

    private AppCompatButton btnCreateQuestionBank;

    private String userId;

    private DatabaseReference questionBankRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Create Question Bank");

        setContentView(R.layout.activity_create_question_bank);

        // Get teacher/user ID
        userId = getIntent().getStringExtra("userId");

        if (userId == null || userId.isEmpty()) {

            Toast.makeText(
                    this,
                    "User information not found",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        initializeViews();

        createFirebaseReference();

        btnCreateQuestionBank.setOnClickListener(
                v -> createQuestionBank()
        );
    }

    private void initializeViews() {

        etClassName =
                findViewById(R.id.etCreateQBClass);

        etSubject =
                findViewById(R.id.etCreateQBSubject);

        etChapter =
                findViewById(R.id.etCreateQBChapter);

        etTopic =
                findViewById(R.id.etCreateQBTopic);

        btnCreateQuestionBank =
                findViewById(R.id.btnCreateQuestionBank);
    }

    private void createFirebaseReference() {

        questionBankRef = FirebaseDatabase
                .getInstance()
                .getReference("QuestionBank")
                .child(userId);
    }

    private void createQuestionBank() {

        String className =
                "Class: ".concat(etClassName.getText()
                        .toString()
                        .trim());

        String subject =
                etSubject.getText()
                        .toString()
                        .trim();

        String chapter =
                etChapter.getText()
                        .toString()
                        .trim();

        String topic =
                etTopic.getText()
                        .toString()
                        .trim();

        // Validate fields
        if (className.isEmpty()
                || subject.isEmpty()
                || chapter.isEmpty()
                || topic.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        /*
         * Create the Question Bank hierarchy:
         *
         * QuestionBank
         *    userId
         *       class
         *          subject
         *             chapter
         *                topic
         */

        DatabaseReference topicRef =
                questionBankRef
                        .child(className)
                        .child(subject)
                        .child(chapter)
                        .child(topic);

        // Create empty hierarchy marker
        topicRef.child("created").setValue(true)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Question Bank created successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                    openQuestionBank(
                            className,
                            subject,
                            chapter,
                            topic
                    );
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to create Question Bank: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void openQuestionBank(
            String className,
            String subject,
            String chapter,
            String topic) {

        Intent intent =
                new Intent(
                        CreateQuestionBankActivity.this,
                        QuestionBankActivity.class
                );

        intent.putExtra("userId", userId);
        intent.putExtra("className", className);
        intent.putExtra("subject", subject);
        intent.putExtra("chapter", chapter);
        intent.putExtra("topic", topic);

        startActivity(intent);

        finish();
    }
}