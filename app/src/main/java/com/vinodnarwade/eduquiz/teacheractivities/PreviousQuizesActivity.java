package com.vinodnarwade.eduquiz.teacheractivities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
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

public class PreviousQuizesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    QuizAdapter adapter;
    ArrayList<QuizModel> quizList;
    DatabaseReference quizRef;
    String userId; // pass this via Intent
    SharedPreferences sharedPreferences;
    ProgressBar progressBar;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_quizes);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        userId = sharedPreferences.getString("userId", "").trim();
        recyclerView = findViewById(R.id.rvPreviousQuizzes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = findViewById(R.id.progressBarPreviousQuizesActivity);

        quizList = new ArrayList<>();
        adapter = new QuizAdapter(this, quizList);
        recyclerView.setAdapter(adapter);

        quizRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userId)
                .child("Quizzes");

        fetchQuizzes();
    }

    private void fetchQuizzes() {
        progressBar.setVisibility(View.VISIBLE);
        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                quizList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    QuizModel model = snap.getValue(QuizModel.class);
                    if (model != null) {
                        quizList.add(model);
                    }
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);  // Hide progress bar

                // Show "No quizzes found" if list is empty
                TextView noQuizText = findViewById(R.id.noQuizPreviousQuizesActivityQuizText);
                if (quizList.isEmpty()) {
                    noQuizText.setVisibility(View.VISIBLE);
                } else {
                    noQuizText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(PreviousQuizesActivity.this, "Failed to load quizzes", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
