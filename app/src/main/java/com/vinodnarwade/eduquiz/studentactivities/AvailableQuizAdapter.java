package com.vinodnarwade.eduquiz.studentactivities;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vinodnarwade.eduquiz.R;
import com.vinodnarwade.eduquiz.teacheractivities.QuizModel;

import java.util.List;

public class AvailableQuizAdapter extends RecyclerView.Adapter<AvailableQuizAdapter.AvailableQuizViewHolder> {

    private Context context;
    private List<QuizModel> quizList;

    public AvailableQuizAdapter(Context context, List<QuizModel> quizList) {
        this.context = context;
        this.quizList = quizList;
    }

    @NonNull
    @Override
    public AvailableQuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quiz_item_available, parent, false);
        return new AvailableQuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvailableQuizViewHolder holder, int position) {
        QuizModel quiz = quizList.get(position);

        holder.titleTextView.setText(quiz.getTitle());
        holder.subjectTextView.setText("Subject: " + quiz.getSubject());
        holder.questionCountTextView.setText("Questions: " + quiz.getNumberOfQuestions());

        holder.startButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, AttemptQuizActivity.class);
            intent.putExtra("quizId", quiz.getQuizID());
            intent.putExtra("teacherId", quiz.getCreatedBy());  // Important to fetch from correct teacher node
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public static class AvailableQuizViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, subjectTextView, questionCountTextView;
        Button startButton;

        public AvailableQuizViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tvItemAvailableQuizzesTitle);
            subjectTextView = itemView.findViewById(R.id.tvItemAvailableQuizzesQuizSubject);
            questionCountTextView = itemView.findViewById(R.id.tvItemAvailableQuizzesTotalQuestions);
            startButton = itemView.findViewById(R.id.btnItemAvailableQuizzesAttemptQuiz);
        }
    }
}
