package com.vinodnarwade.eduquiz.teacheractivities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.vinodnarwade.eduquiz.R;
import com.vinodnarwade.eduquiz.fragments.ScheduledQuizFragment;
import com.vinodnarwade.eduquiz.fragments.ScheduledNewQuizFragment;

public class QuizSchedulerActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Schedule Quizzes");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_scheduler);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        adapter = new ViewPageAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Scheduled Quizzes");
                            break;
                        case 1:
                            tab.setText("Schedule New Quiz");
                            break;
                    }
                }).attach();
    }
}
