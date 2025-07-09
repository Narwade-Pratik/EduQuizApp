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

import com.vinodnarwade.eduquiz.R;

public class QuizesFragment extends Fragment {

    ListView listView;
    public String roleIs;
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
            // Show Toast or open quiz detail
            // Toast.makeText(getContext(), "Selected: " + selected, Toast.LENGTH_SHORT).show();
        });
        return view;
    }
}