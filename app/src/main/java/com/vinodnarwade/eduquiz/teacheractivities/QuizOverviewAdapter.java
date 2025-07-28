package com.vinodnarwade.eduquiz.teacheractivities;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vinodnarwade.eduquiz.R;
import com.vinodnarwade.eduquiz.teacheractivities.QuizModel;
import com.vinodnarwade.eduquiz.teacheractivities.QuizResultActivity;

import java.util.List;

public class QuizOverviewAdapter extends RecyclerView.Adapter<QuizOverviewAdapter.ViewHolder> {

    private Context context;
    private List<QuizModel> quizList;

    public QuizOverviewAdapter(Context context, List<QuizModel> quizList) {
        this.context = context;
        this.quizList = quizList;
    }

    @NonNull
    @Override
    public QuizOverviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quiz_item_quiz_overview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizOverviewAdapter.ViewHolder holder, int position) {
        QuizModel quiz = quizList.get(position);

        holder.titleText.setText(quiz.getTitle());
        holder.subjectText.setText("Subject: " + quiz.getSubject());

        // On Click: open QuizResultActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, QuizResultActivity.class);
            intent.putExtra("quizId", quiz.getQuizID());
            intent.putExtra("quizTitle", quiz.getTitle());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleText, subjectText, totalQuestionsText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.tvQuizItemQuizOverviewTitle);
            subjectText = itemView.findViewById(R.id.tvQuizItemQuizOverviewSubject);
        }
    }
}

