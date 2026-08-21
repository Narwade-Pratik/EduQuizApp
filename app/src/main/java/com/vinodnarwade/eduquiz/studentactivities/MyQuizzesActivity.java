package com.vinodnarwade.eduquiz.studentactivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vinodnarwade.eduquiz.R;

import java.util.ArrayList;

public class MyQuizzesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMyQuizzes;

    private TextView tvNoQuizzes;

    private MyQuizAdapter adapter;

    private final ArrayList<MyQuizModel> quizList =
            new ArrayList<>();

    private String userId;

    private DatabaseReference myQuizzesRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("My Quizzes");

        setContentView(
                R.layout.activity_my_quizzes
        );


        // =====================================================
        // INITIALIZE VIEWS
        // =====================================================

        recyclerViewMyQuizzes =
                findViewById(
                        R.id.recyclerViewMyQuizzes
                );

        tvNoQuizzes =
                findViewById(
                        R.id.tvNoQuizzes
                );


        // =====================================================
        // GET STUDENT ID
        // =====================================================

        SharedPreferences preferences =
                PreferenceManager
                        .getDefaultSharedPreferences(this);

        userId =
                preferences.getString(
                        "userId",
                        ""
                );


        if (userId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Student ID not found. Please login again.",
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return;
        }


        // =====================================================
        // RECYCLER VIEW
        // =====================================================

        recyclerViewMyQuizzes.setLayoutManager(
                new LinearLayoutManager(this)
        );


        adapter =
                new MyQuizAdapter(
                        this,
                        quizList,
                        quiz -> {

                            Intent intent =
                                    new Intent(
                                            MyQuizzesActivity.this,
                                            DetailedReportActivity.class
                                    );

                            intent.putExtra(
                                    "isCustomizedQuiz",
                                    true
                            );

                            intent.putExtra(
                                    "customQuizId",
                                    quiz.getCustomQuizId()
                            );

                            intent.putExtra(
                                    "customSubject",
                                    quiz.getSubject()
                            );

                            intent.putExtra(
                                    "customChapter",
                                    quiz.getChapter()
                            );

                            intent.putExtra(
                                    "customTopic",
                                    quiz.getTopic()
                            );

                            intent.putExtra(
                                    "customDifficulty",
                                    quiz.getDifficulty()
                            );

                            intent.putExtra(
                                    "score",
                                    quiz.getScore()
                            );

                            intent.putExtra(
                                    "numberOfQuestions",
                                    quiz.getNumberOfQuestions()
                            );

                            intent.putExtra(
                                    "timeTakenMillis",
                                    quiz.getTimeTakenMillis()
                            );

                            intent.putExtra(
                                    "selectedAnswers",
                                    quiz.getAnswers()
                            );

                            intent.putExtra(
                                    "questionIds",
                                    quiz.getQuestionIds()
                            );

                            startActivity(intent);
                        }
                );


        recyclerViewMyQuizzes.setAdapter(
                adapter
        );


        // =====================================================
        // FIREBASE REFERENCE
        // =====================================================

        myQuizzesRef =
                FirebaseDatabase.getInstance()
                        .getReference("Users")
                        .child(userId)
                        .child("MyQuizzes");


        // =====================================================
        // LOAD QUIZZES
        // =====================================================

        loadMyQuizzes();
    }


    // =========================================================
    // LOAD MY QUIZZES
    // =========================================================

    private void loadMyQuizzes() {

        myQuizzesRef.addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {

                        quizList.clear();


                        for (DataSnapshot quizSnapshot :
                                snapshot.getChildren()) {

                            MyQuizModel quiz =
                                    quizSnapshot.getValue(
                                            MyQuizModel.class
                                    );


                            if (quiz != null) {

                                quizList.add(quiz);
                            }
                        }


                        // =====================================
                        // EMPTY STATE
                        // =====================================

                        if (quizList.isEmpty()) {

                            tvNoQuizzes.setVisibility(
                                    View.VISIBLE
                            );

                            recyclerViewMyQuizzes
                                    .setVisibility(
                                            View.GONE
                                    );

                        } else {

                            tvNoQuizzes.setVisibility(
                                    View.GONE
                            );

                            recyclerViewMyQuizzes
                                    .setVisibility(
                                            View.VISIBLE
                                    );
                        }


                        adapter.notifyDataSetChanged();
                    }


                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {

                        Toast.makeText(
                                MyQuizzesActivity.this,
                                "Failed to load quizzes: "
                                        + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }
}