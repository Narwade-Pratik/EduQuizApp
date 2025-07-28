package com.vinodnarwade.eduquiz.teacheractivities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vinodnarwade.eduquiz.R;
import com.vinodnarwade.eduquiz.teacheractivities.StudentResultModel;

import java.util.List;

public class StudentResultAdapter extends RecyclerView.Adapter<StudentResultAdapter.ViewHolder> {

    private Context context;
    private List<StudentResultModel> studentList;

    public StudentResultAdapter(Context context, List<StudentResultModel> studentList) {
        this.context = context;
        this.studentList = studentList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentId, tvScore, tvTime;

        public ViewHolder(View itemView) {
            super(itemView);
            tvStudentId = itemView.findViewById(R.id.tvStudentId);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }

    @Override
    public StudentResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_quiz_result_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StudentResultAdapter.ViewHolder holder, int position) {
        StudentResultModel student = studentList.get(position);

        holder.tvStudentId.setText("ID: " + student.getStudentId());
        holder.tvScore.setText("Score: " + student.getScore());

        long timeMillis = student.getTimeTakenMillis();
        long seconds = timeMillis / 1000;
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        holder.tvTime.setText("Time Taken: " + minutes + " min " + remainingSeconds + " sec");
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }
}
