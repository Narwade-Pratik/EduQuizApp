package com.vinodnarwade.eduquiz.teacheractivities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vinodnarwade.eduquiz.R;

import java.util.ArrayList;
import java.util.List;

public class ManageQuestionsActivity extends AppCompatActivity {

    private ManageQuestionsAdapter adapter;
    private TextView tvHierarchy;
    private TextView tvDifficulty;
    private RecyclerView recyclerQuestions;

    private String userId;
    private String className;
    private String subject;
    private String chapter;
    private String topic;
    private String difficulty;

    private DatabaseReference questionRef;

    private final List<QuestionBankQuestionModel> questionList =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Manage Questions");

        setContentView(R.layout.activity_manage_questions);

        // Receive data
        userId = getIntent().getStringExtra("userId");
        className = getIntent().getStringExtra("className");
        subject = getIntent().getStringExtra("subject");
        chapter = getIntent().getStringExtra("chapter");
        topic = getIntent().getStringExtra("topic");
        difficulty = getIntent().getStringExtra("difficulty");

        // Validate data
        if (userId == null
                || className == null
                || subject == null
                || chapter == null
                || topic == null
                || difficulty == null) {

            Toast.makeText(
                    this,
                    "Question bank information not found",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        initializeViews();

        displayQuestionBankInfo();

        createFirebaseReference();

        setupRecyclerView();

        loadQuestions();
    }

    private void initializeViews() {

        tvHierarchy = findViewById(R.id.tvHierarchy);
        tvDifficulty = findViewById(R.id.tvDifficulty);
        recyclerQuestions = findViewById(R.id.recyclerQuestions);
    }

    private void displayQuestionBankInfo() {

        String hierarchy =
                className + " > "
                        + subject + " > "
                        + chapter + " > "
                        + topic;

        tvHierarchy.setText(hierarchy);

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

    private void setupRecyclerView() {

        recyclerQuestions.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new ManageQuestionsAdapter(questionList);

        recyclerQuestions.setAdapter(adapter);
    }

    private void loadQuestions() {

        questionRef.get()
                .addOnSuccessListener(snapshot -> {

                    questionList.clear();

                    for (com.google.firebase.database.DataSnapshot questionSnapshot
                            : snapshot.getChildren()) {

                        QuestionBankQuestionModel question =
                                questionSnapshot.getValue(
                                        QuestionBankQuestionModel.class
                                );

                        if (question != null) {
                            questionList.add(question);
                        }
                    }

                    adapter.notifyDataSetChanged();

                    Toast.makeText(
                            ManageQuestionsActivity.this,
                            questionList.size()
                                    + " question(s) found",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            ManageQuestionsActivity.this,
                            "Failed to load questions: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}