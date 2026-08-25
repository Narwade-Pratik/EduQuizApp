package com.vinodnarwade.eduquiz.teacheractivities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vinodnarwade.eduquiz.R;

import java.util.List;
import java.util.Set;

public class WeakAreaStudentAdapter
        extends RecyclerView.Adapter<WeakAreaStudentAdapter.ViewHolder> {

    public interface OnStudentClickListener {
        void onStudentClick(StudentModel student);
    }

    private final List<StudentModel> studentList;
    private final Set<String> selectedIds;
    private final OnStudentClickListener listener;

    public WeakAreaStudentAdapter(
            List<StudentModel> studentList,
            Set<String> selectedIds,
            OnStudentClickListener listener) {

        this.studentList = studentList;
        this.selectedIds = selectedIds;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_weak_area_student, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        StudentModel student = studentList.get(position);

        holder.tvName.setText(student.getName());

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(selectedIds.contains(student.getStudentId()));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                selectedIds.add(student.getStudentId());
            } else {
                selectedIds.remove(student.getStudentId());
            }
        });

        holder.tvName.setOnClickListener(v -> listener.onStudentClick(student));
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        CheckBox checkBox;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemWeakAreaStudentName);
            checkBox = itemView.findViewById(R.id.cbItemWeakAreaStudent);
        }
    }
}