package com.vinodnarwade.eduquiz.studentactivities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vinodnarwade.eduquiz.R;

import java.util.ArrayList;
import java.util.List;

public class PreviousStudentQuizzesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<StudentAttemptedQuizModel> quizList;
    private AttemptedQuizAdapter adapter;
    private DatabaseReference usersRef;
    private String currentStudentId;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_student_quizzes);

        recyclerView = findViewById(R.id.rvAttemptedQuizzes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        quizList = new ArrayList<>();
        adapter = new AttemptedQuizAdapter(quizList, this);
        recyclerView.setAdapter(adapter);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentStudentId = sharedPreferences.getString("userId","");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        loadAttemptedQuizzes();
    }

    private void loadAttemptedQuizzes() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                quizList.clear();

                for (DataSnapshot teacherSnap : snapshot.getChildren()) {
                    String teacherId = teacherSnap.getKey();

                    // ✅ Check if 'Quizzes' node exists under teacher
                    if (!teacherSnap.hasChild("Quizzes")) {
                        continue;
                    }

                    DataSnapshot quizzesSnap = teacherSnap.child("Quizzes");

                    for (DataSnapshot quizSnap : quizzesSnap.getChildren()) {
                        String quizId = quizSnap.getKey();

                        if (quizSnap.child("AttemptedBy").hasChild(currentStudentId)) {
                            String title = quizSnap.child("title").getValue(String.class);
                            String subject = quizSnap.child("subject").getValue(String.class);
                            int score = quizSnap.child("AttemptedBy").child(currentStudentId).child("score").getValue(Integer.class);
                            int total = quizSnap.child("AttemptedBy").child(currentStudentId).child("numberOfQuestions").getValue(Integer.class);

                            StudentAttemptedQuizModel model = new StudentAttemptedQuizModel(
                                    quizId, title, teacherId, score, total ,subject
                            );

                            quizList.add(model);
                        }
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PreviousStudentQuizzesActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
