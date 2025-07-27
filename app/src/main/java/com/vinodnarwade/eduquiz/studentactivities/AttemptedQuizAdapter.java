package com.vinodnarwade.eduquiz.studentactivities;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vinodnarwade.eduquiz.studentactivities.ShowStudentResultActivity;
import com.vinodnarwade.eduquiz.studentactivities.StudentAttemptedQuizModel;  // ✅ Use your correct package name

import com.vinodnarwade.eduquiz.R;
import java.util.List;

public class AttemptedQuizAdapter extends RecyclerView.Adapter<AttemptedQuizAdapter.QuizViewHolder> {

    private List<StudentAttemptedQuizModel> list;
    private Context context;
    private SharedPreferences sharedPreferences;

    public AttemptedQuizAdapter(List<StudentAttemptedQuizModel> list, Context context) {
        this.list = list;
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quiz_item_attempted_quiz, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        StudentAttemptedQuizModel model = list.get(position);

        holder.quizTitle.setText(model.getTitle());
        holder.quizCreatedBy.setText(model.getTeacherId());
        holder.quizSubject.setText(model.getSubject());
        holder.quizScore.setText("Score: " + model.getScore() + "/" + model.getTotalQuestions());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ShowStudentResultActivity.class);
            intent.putExtra("quizId", model.getQuizId());
            intent.putExtra("teacherId", model.getTeacherId());
            intent.putExtra("studentId", sharedPreferences.getString("userId",""));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView quizTitle, quizScore,quizSubject,quizCreatedBy;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            quizTitle = itemView.findViewById(R.id.tvItemAttemptedQuizQuizTitle);
            quizScore = itemView.findViewById(R.id.tvItemAttemptedQuizQuizScore);
            quizSubject = itemView.findViewById(R.id.tvItemAttemptedQuizQuizSubject);
            quizCreatedBy = itemView.findViewById(R.id.tvItemAttemptedQuizQuizCreatedBy);

        }
    }
}

