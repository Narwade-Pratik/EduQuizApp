package com.vinodnarwade.eduquiz.studentactivities;

import android.os.Bundle;
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
import com.vinodnarwade.eduquiz.teacheractivities.QuizModel;

import java.util.ArrayList;
import java.util.List;

public class AvailableQuizzesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AvailableQuizAdapter adapter;
    List<QuizModel> quizList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_quizzes);

        recyclerView = findViewById(R.id.rvAvailableQuizzes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        quizList = new ArrayList<>();
        adapter = new AvailableQuizAdapter(this, quizList);
        recyclerView.setAdapter(adapter);

        loadAllQuizzes();
    }

    private void loadAllQuizzes() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                quizList.clear();

                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    if (userSnap.hasChild("Quizzes")) {
                        for (DataSnapshot quizSnap : userSnap.child("Quizzes").getChildren()) {
                            QuizModel quiz = quizSnap.getValue(QuizModel.class);
                            quizList.add(quiz);
                        }
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AvailableQuizzesActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
