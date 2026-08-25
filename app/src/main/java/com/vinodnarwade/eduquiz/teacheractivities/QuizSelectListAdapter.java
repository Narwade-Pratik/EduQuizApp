package com.vinodnarwade.eduquiz.teacheractivities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vinodnarwade.eduquiz.R;

import java.util.List;

public class QuizSelectListAdapter
        extends RecyclerView.Adapter<QuizSelectListAdapter.ViewHolder> {

    public interface OnQuizClickListener {
        void onQuizClick(QuizModel quiz);
    }

    private final List<QuizModel> quizzes;
    private final OnQuizClickListener listener;

    public QuizSelectListAdapter(List<QuizModel> quizzes, OnQuizClickListener listener) {
        this.quizzes = quizzes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_simple_name, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        QuizModel quiz = quizzes.get(position);
        holder.tvName.setText(quiz.getTitle());
        holder.itemView.setOnClickListener(v -> listener.onQuizClick(quiz));
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemSimpleName);
        }
    }
}