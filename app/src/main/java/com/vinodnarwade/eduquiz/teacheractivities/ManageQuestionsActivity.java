package com.vinodnarwade.eduquiz.teacheractivities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
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

        adapter = new ManageQuestionsAdapter(
                questionList,
                new ManageQuestionsAdapter.OnQuestionActionListener() {

                    @Override
                    public void onEditQuestion(
                            QuestionBankQuestionModel model) {

                        openEditQuestion(model);
                    }

                    @Override
                    public void onDeleteQuestion(
                            QuestionBankQuestionModel model) {

                        deleteQuestion(model);
                    }
                }
        );

        recyclerQuestions.setAdapter(adapter);
    }

    private void editQuestion(
            QuestionBankQuestionModel question) {

        Toast.makeText(
                this,
                "Edit: " + question.getQuestionId(),
                Toast.LENGTH_SHORT
        ).show();
    }

    private void deleteQuestion(
            QuestionBankQuestionModel question) {

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Question")
                .setMessage("Are you sure you want to delete this question?")
                .setPositiveButton("Delete", (dialog, which) -> {

                    questionRef
                            .child(question.getQuestionId())
                            .removeValue()
                            .addOnSuccessListener(unused -> {

                                int position =
                                        questionList.indexOf(question);

                                if (position != -1) {
                                    questionList.remove(position);
                                    adapter.notifyItemRemoved(position);
                                }

                                Toast.makeText(
                                        ManageQuestionsActivity.this,
                                        "Question deleted successfully",
                                        Toast.LENGTH_SHORT
                                ).show();
                            })
                            .addOnFailureListener(e -> {

                                Toast.makeText(
                                        ManageQuestionsActivity.this,
                                        "Failed to delete question: "
                                                + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadQuestions() {

        questionRef.get()
                .addOnSuccessListener(snapshot -> {

                    questionList.clear();

                    for (DataSnapshot questionSnapshot : snapshot.getChildren()) {

                        QuestionBankQuestionModel question =
                                questionSnapshot.getValue(
                                        QuestionBankQuestionModel.class
                                );

                        if (question != null) {

                            question.setQuestionId(questionSnapshot.getKey());

                            Toast.makeText(
                                    ManageQuestionsActivity.this,
                                    "Loaded Topic: " + question.getQuestionTopic(),
                                    Toast.LENGTH_SHORT
                            ).show();

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

    private void openEditQuestion(
            QuestionBankQuestionModel model) {

        Intent intent = new Intent(
                ManageQuestionsActivity.this,
                EditQuestionActivity.class
        );

        intent.putExtra("userId", userId);
        intent.putExtra("className", className);
        intent.putExtra("subject", subject);
        intent.putExtra("chapter", chapter);
        intent.putExtra("topic", topic);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra(
                "questionId",
                model.getQuestionId()
        );

        startActivity(intent);
    }

}