package com.vinodnarwade.eduquiz.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.content.Intent;

import com.vinodnarwade.eduquiz.R;
import com.vinodnarwade.eduquiz.studentactivities.AvailableQuizzesActivity;
import com.vinodnarwade.eduquiz.studentactivities.CustomizedQuizActivity;
import com.vinodnarwade.eduquiz.studentactivities.LeaderboardActivity;
import com.vinodnarwade.eduquiz.studentactivities.MyQuizzesActivity;
import com.vinodnarwade.eduquiz.studentactivities.PreviousStudentQuizzesActivity;
import com.vinodnarwade.eduquiz.studentactivities.QuizStatisticsActivity;
import com.vinodnarwade.eduquiz.studentactivities.StudentsSettingsActivity;
import com.vinodnarwade.eduquiz.studentactivities.UpcomingQuizzesActivity;
import com.vinodnarwade.eduquiz.teacheractivities.ManageStudentsActivity;
import com.vinodnarwade.eduquiz.teacheractivities.SettingsActivity;
import com.vinodnarwade.eduquiz.teacheractivities.CreateQuizActivity;
import com.vinodnarwade.eduquiz.teacheractivities.PreviousQuizesActivity;
import com.vinodnarwade.eduquiz.teacheractivities.QuizOverviewActivity;
import com.vinodnarwade.eduquiz.teacheractivities.QuizSchedulerActivity;

public class QuizesFragment extends Fragment {

    ListView listView;
    public String roleIs,userId;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String[] teacherQuizTitles = {
            "Create New Quiz",
            "Previous Quizzes",
            "Quiz Scheduler",
            "View Quiz Overview",
            "Manage Students",
            "Settings"
    };

    String[] studentQuizTitles = {
            "Create Customized Quiz",   // Auto-generated quiz based on subject/topic
            "Available Quizzes",        // Assigned by teacher or public
            "Previous Quizzes",         // Attempt history
            "My Quizzes",               // Self-attempted or saved quizzes
            "Upcoming Quizzes",         // Scheduled quizzes
            "Leaderboard",              // Ranking among students
            "Quiz Statistics",          // Score trends & accuracy
            "Settings"                  // Profile, logout, etc.
    };

    String[] quizTitles;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quizes, container, false);
        listView = view.findViewById(R.id.listViewQuizzes);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = sharedPreferences.edit();

        roleIs = sharedPreferences.getString("roleIs","").trim();
        userId = sharedPreferences.getString("userId","").trim();
        //System.out.println(roleIs);
        if ("Teacher".equals(roleIs)) {
            quizTitles = teacherQuizTitles;
        } else {
            quizTitles = studentQuizTitles;
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1,
                quizTitles
        );

        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            String selected = quizTitles[position];

            if (roleIs.equals("Teacher")) {
                if (selected.equals("Create New Quiz")) {
                    startActivity(new Intent(getContext(), CreateQuizActivity.class));

                } else if (selected.equals("Previous Quizzes")) {
                    startActivity(new Intent(getContext(), PreviousQuizesActivity.class));

                } else if (selected.equals("Quiz Scheduler")) {
                    startActivity(new Intent(getContext(), QuizSchedulerActivity.class));

                } else if (selected.equals("View Quiz Overview")) {
                    startActivity(new Intent(getContext(), QuizOverviewActivity.class));

                } else if (selected.equals("Manage Students")) {
                    startActivity(new Intent(getContext(), ManageStudentsActivity.class));

                } else if (selected.equals("Settings")) {
                    startActivity(new Intent(getContext(), SettingsActivity.class));
                }

            } else {
                // Student role
                if (selected.equals("Create Customized Quiz")) {
                    startActivity(new Intent(getContext(), CustomizedQuizActivity.class));

                } else if (selected.equals("Available Quizzes")) {
                    startActivity(new Intent(getContext(), AvailableQuizzesActivity.class));

                } else if (selected.equals("Previous Quizzes")) {
                    startActivity(new Intent(getContext(), PreviousStudentQuizzesActivity.class));

                } else if (selected.equals("My Quizzes")) {
                    startActivity(new Intent(getContext(), MyQuizzesActivity.class));

                } else if (selected.equals("Upcoming Quizzes")) {
                    startActivity(new Intent(getContext(), UpcomingQuizzesActivity.class));

                } else if (selected.equals("Leaderboard")) {
                    startActivity(new Intent(getContext(), LeaderboardActivity.class));

                } else if (selected.equals("Quiz Statistics")) {
                    startActivity(new Intent(getContext(), QuizStatisticsActivity.class));

                } else if (selected.equals("Settings")) {
                    startActivity(new Intent(getContext(), StudentsSettingsActivity.class));
                }
            }
        });

        return view;
    }
}