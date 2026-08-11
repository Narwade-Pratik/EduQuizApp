package com.vinodnarwade.eduquiz.teacheractivities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.vinodnarwade.eduquiz.R;

public class QuizSchedulerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Schedule Quizzes");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_scheduler);


    }
}
