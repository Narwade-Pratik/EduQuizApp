package com.vinodnarwade.eduquiz.teacheractivities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;

import com.vinodnarwade.eduquiz.R;

public class CreateQuizActivity extends AppCompatActivity {

    EditText quizTitle,noOfQue,subjectName,durationHours,durationMinutes,etclassname;
    AppCompatButton createQuiz,btnScheduleFirstDate,btnScheduleSecondDate;
    FirebaseDatabase database;
    FirebaseAuth auth;
    DatabaseReference quizRef;
    SharedPreferences sharedPreferences;
    String userId,userName;
    TextView displayFirstDate,displaySecondDate;
    String scheduledFirstDate = "",scheduledSecondDate = "";
    long scheduledTimestampFirst = 0,scheduledTimestampSecond = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);
        quizTitle = findViewById(R.id.etcreatequizquiztitile);
        noOfQue = findViewById(R.id.etcreatequiznoofquestions);
        subjectName = findViewById(R.id.etcreatequizsubjectname);
        etclassname = findViewById(R.id.etcreatequizclassname);
        createQuiz = findViewById(R.id.btncreatequizcreatequiz);
        durationHours = findViewById(R.id.etCreateQuizDurationHours);
        durationMinutes = findViewById(R.id.etCreateQuizDurationMinutes);
        btnScheduleFirstDate = findViewById(R.id.btncreatequizschedulefirsttimeanddate);
        displayFirstDate = findViewById(R.id.tvcreatequizdiplayscheduledfirsttimeanddate);
        btnScheduleSecondDate = findViewById(R.id.btncreatequizschedulesecondtimeanddate);
        displaySecondDate = findViewById(R.id.tvcreatequizdiplayscheduledsecondtimeanddate);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        userId = sharedPreferences.getString("userId", null);
        userName = sharedPreferences.getString("userName", null);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        quizRef = quizRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userId)
                .child("Quizzes");



        createQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = quizTitle.getText().toString();
                char[] digitsArray = noOfQue.getText().toString().toCharArray();
                String subjectNameIs = subjectName.getText().toString();
                String classNameIs = etclassname.getText().toString().trim();

                String hoursStr = durationHours.getText().toString().trim();
                String minutesStr = durationMinutes.getText().toString().trim();

                int durationInMinutes = 0;
                if (!hoursStr.isEmpty()) {
                    durationInMinutes += Integer.parseInt(hoursStr) * 60;
                }
                if (!minutesStr.isEmpty()) {
                    durationInMinutes += Integer.parseInt(minutesStr);
                }

                if (title.isEmpty()) {
                    quizTitle.setError("Please enter a quiz title");
                } else if (noOfQue.getText().toString().isEmpty() || !checkIsAllDigit(digitsArray)) {
                    noOfQue.setError("Enter valid Number");
                } else if (subjectNameIs.isEmpty()) {
                    subjectName.setError("Enter Subject Name");
                } else if (scheduledFirstDate.isEmpty() || scheduledTimestampFirst == 0) {
                    Toast.makeText(CreateQuizActivity.this, "Please select scheduled date & time", Toast.LENGTH_SHORT).show();
                } else if (classNameIs.isEmpty()) {
                    etclassname.setError("Enter Class Name");
                } else if (scheduledSecondDate.isEmpty() || scheduledTimestampSecond == 0) {
                    Toast.makeText(CreateQuizActivity.this, "Please select scheduled date & time", Toast.LENGTH_SHORT).show();
                } else if (durationInMinutes <= 0) {
                    durationHours.setError("Enter valid duration");
                    durationMinutes.setError("Enter valid duration");
                } else {
                    String quizId = quizRef.push().getKey();
                    int noOfQ = Integer.parseInt(noOfQue.getText().toString().trim());

                    if (quizId == null) {
                        Toast.makeText(CreateQuizActivity.this, "Missing quiz ID!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (userId == null) {
                        Toast.makeText(CreateQuizActivity.this, "Missing user ID!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (noOfQ <= 0) {
                        Toast.makeText(CreateQuizActivity.this, "Missing number of questions!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // ✅ UPDATED QUIZ MODEL CONSTRUCTOR TO INCLUDE DURATION
                    QuizModel quiz = new QuizModel(
                            quizId,
                            title,
                            subjectNameIs,
                            noOfQ,
                            userId,
                            scheduledFirstDate,
                            scheduledTimestampFirst,
                            scheduledSecondDate,
                            scheduledTimestampSecond,
                            durationInMinutes,
                            classNameIs   // <-- naya
                    );

                    quizRef.child(quizId).setValue(quiz).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(CreateQuizActivity.this, AddQuestionActivity.class);
                            intent.putExtra("quizId", quizId);
                            intent.putExtra("noOfQ", noOfQ);
                            intent.putExtra("userId", userId);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(CreateQuizActivity.this, "Failed to create quiz", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        btnScheduleFirstDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, y, m, d) -> {

                TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view1, hour, minute) -> {
                    calendar.set(y, m, d, hour, minute);
                    scheduledTimestampFirst = calendar.getTimeInMillis();

                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
                    scheduledFirstDate = sdf.format(calendar.getTime());

                    displayFirstDate.setText("Scheduled for: " + scheduledFirstDate);
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

                timePickerDialog.show();

            }, year, month, day);

            datePickerDialog.show();
        });

        btnScheduleSecondDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, y, m, d) -> {

                TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view1, hour, minute) -> {
                    calendar.set(y, m, d, hour, minute);
                    scheduledTimestampSecond = calendar.getTimeInMillis();

                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
                    scheduledSecondDate = sdf.format(calendar.getTime());

                    displaySecondDate.setText("Scheduled for: " + scheduledSecondDate);
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

                timePickerDialog.show();

            }, year, month, day);

            datePickerDialog.show();
        });

    }

    public boolean checkIsAllDigit(char[] arr){
        for(int i=0;i<arr.length;i++){
            if(!Character.isDigit(arr[i])){
                return false;
            }
        }
        return true;
    }
}