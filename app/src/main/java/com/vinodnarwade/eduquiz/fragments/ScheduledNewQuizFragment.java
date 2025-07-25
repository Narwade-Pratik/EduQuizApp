package com.vinodnarwade.eduquiz.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vinodnarwade.eduquiz.R;

public class ScheduledNewQuizFragment extends Fragment
{


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scheduled_new_quiz, container, false);

    }
}