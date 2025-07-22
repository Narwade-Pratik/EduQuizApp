package com.vinodnarwade.eduquiz.teacheractivities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vinodnarwade.eduquiz.R;

import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {

    private Context context;
    private List<QuizModel> quizList;

    public QuizAdapter(Context context, List<QuizModel> quizList) {
        this.context = context;
        this.quizList = quizList;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quiz_item, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        QuizModel quiz = quizList.get(position);
        holder.titleTextView.setText(quiz.getTitle());
        holder.subjectTextView.setText(quiz.getSubject());
        holder.questionCountTextView.setText("Questions: " + quiz.getNumberOfQuestions());

        // TODO: Set onClickListeners for buttons below as needed
        holder.editButton.setOnClickListener(v -> {
            // Handle Edit
        });

        holder.deleteButton.setOnClickListener(v -> {
            // Handle Delete
        });

        holder.viewButton.setOnClickListener(v -> {
            // Handle View
        });
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, subjectTextView, questionCountTextView;
        Button editButton, deleteButton, viewButton;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tvquizitemquiztitle);
            subjectTextView = itemView.findViewById(R.id.tvquizitemquizsubject);
            questionCountTextView = itemView.findViewById(R.id.tvquizitemquizquestioncount);
            editButton = itemView.findViewById(R.id.btnquizitemedit);
            deleteButton = itemView.findViewById(R.id.btnquizitemdelete);
            viewButton = itemView.findViewById(R.id.btnquizitemview);
        }
    }
}
