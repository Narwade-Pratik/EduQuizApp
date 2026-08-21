package com.vinodnarwade.eduquiz.studentactivities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vinodnarwade.eduquiz.R;

import java.util.ArrayList;

public class MyQuizAdapter
        extends RecyclerView.Adapter<MyQuizAdapter.MyQuizViewHolder> {

    private final Context context;

    private final ArrayList<MyQuizModel> quizList;

    private final OnQuizClickListener listener;

    public interface OnQuizClickListener {
        void onQuizClick(MyQuizModel quiz);
    }
    public MyQuizAdapter(
            Context context,
            ArrayList<MyQuizModel> quizList,
            OnQuizClickListener listener) {

        this.context = context;
        this.quizList = quizList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyQuizViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater.from(context)
                        .inflate(
                                R.layout.item_my_quiz,
                                parent,
                                false
                        );

        return new MyQuizViewHolder(view);
    }


    @Override
    public void onBindViewHolder(
            @NonNull MyQuizViewHolder holder,
            int position) {

        MyQuizModel quiz =
                quizList.get(position);


        holder.tvSubject.setText(
                quiz.getSubject()
        );

        holder.tvChapter.setText(
                "Chapter: "
                        + quiz.getChapter()
        );

        holder.tvTopic.setText(
                "Topic: "
                        + quiz.getTopic()
        );

        holder.tvDifficulty.setText(
                "Difficulty: "
                        + quiz.getDifficulty()
        );

        holder.tvScore.setText(
                "Score: "
                        + quiz.getScore()
                        + " / "
                        + quiz.getTotalMarks()
        );

        holder.itemView.setOnClickListener(v -> {

            listener.onQuizClick(quiz);

        });
    }


    @Override
    public int getItemCount() {

        return quizList.size();
    }


    // =========================================================
    // VIEW HOLDER
    // =========================================================

    static class MyQuizViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvSubject;
        TextView tvChapter;
        TextView tvTopic;
        TextView tvDifficulty;
        TextView tvScore;


        public MyQuizViewHolder(
                @NonNull View itemView) {

            super(itemView);


            tvSubject =
                    itemView.findViewById(
                            R.id.tvMyQuizSubject
                    );

            tvChapter =
                    itemView.findViewById(
                            R.id.tvMyQuizChapter
                    );

            tvTopic =
                    itemView.findViewById(
                            R.id.tvMyQuizTopic
                    );

            tvDifficulty =
                    itemView.findViewById(
                            R.id.tvMyQuizDifficulty
                    );

            tvScore =
                    itemView.findViewById(
                            R.id.tvMyQuizScore
                    );
        }
    }
}