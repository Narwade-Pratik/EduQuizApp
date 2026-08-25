package com.vinodnarwade.eduquiz.teacheractivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;
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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class QuizOverviewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView noQuizText;

    private SharedPreferences sharedPreferences;
    private DatabaseReference quizRef;
    private String teacherId;

    private final ArrayList<String> classNames = new ArrayList<>();
    private SimpleNameListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_overview);

        recyclerView = findViewById(R.id.recyclerViewQuizOverview);
        progressBar = findViewById(R.id.progressBarQuizOverviewActivity);
        noQuizText = findViewById(R.id.noQuizOverviewActivityQuizText);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SimpleNameListAdapter(classNames, className -> {

            Intent intent = new Intent(this, QuizListActivity.class);
            intent.putExtra("teacherId", teacherId);
            intent.putExtra("className", className);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        teacherId = sharedPreferences.getString("userId", "").trim();

        quizRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(teacherId)
                .child("Quizzes");

        loadClasses();
    }

    private void loadClasses() {

        progressBar.setVisibility(View.VISIBLE);

        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Set<String> uniqueClasses = new LinkedHashSet<>();

                for (DataSnapshot quizSnap : snapshot.getChildren()) {

                    QuizModel quiz = quizSnap.getValue(QuizModel.class);

                    if (quiz != null
                            && quiz.getClassName() != null
                            && !quiz.getClassName().trim().isEmpty()) {

                        uniqueClasses.add(quiz.getClassName().trim());
                    }
                }

                classNames.clear();
                classNames.addAll(uniqueClasses);
                Collections.sort(classNames);

                progressBar.setVisibility(View.GONE);

                if (classNames.isEmpty()) {
                    noQuizText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    noQuizText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QuizOverviewActivity.this, "Failed to load classes", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}