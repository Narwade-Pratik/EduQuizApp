package com.vinodnarwade.eduquiz.teacheractivities;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.vinodnarwade.eduquiz.fragments.ScheduledNewQuizFragment;
import com.vinodnarwade.eduquiz.fragments.ScheduledQuizFragment;

public class ViewPageAdapter extends FragmentStateAdapter
{
    public ViewPageAdapter(@NonNull FragmentActivity fa)
    {
        super(fa);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position)
    {
        switch(position)
        {
            case 0 : return new ScheduledQuizFragment();

            case 1 : return new ScheduledNewQuizFragment();

            default : return new ScheduledQuizFragment();
        }
    }

    @Override
    public int getItemCount()
    {
        return 2;
    }
}
