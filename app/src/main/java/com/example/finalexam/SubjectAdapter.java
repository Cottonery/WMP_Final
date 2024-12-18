package com.example.finalexam;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {

    private List<Subject> subjectList;
    private OnSubjectSelectedListener onSubjectSelectedListener;

    public interface OnSubjectSelectedListener {
        void onSubjectSelected(Subject subject, boolean isSelected);
    }

    public SubjectAdapter(List<Subject> subjectList, OnSubjectSelectedListener listener) {
        this.subjectList = subjectList;
        this.onSubjectSelectedListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subject_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Subject subject = subjectList.get(position);

        holder.subjectCheckBox.setText(subject.getName() + " (" + subject.getCredits() + " credits)");
        holder.subjectCheckBox.setOnCheckedChangeListener(null);
        holder.subjectCheckBox.setChecked(subject.isSelected());


        holder.subjectCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            subject.setSelected(isChecked);
            onSubjectSelectedListener.onSubjectSelected(subject, isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public void deselectSubject(Subject subject) {
        int position = subjectList.indexOf(subject);
        if (position != -1) {
            subject.setSelected(false);
            notifyItemChanged(position);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox subjectCheckBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectCheckBox = itemView.findViewById(R.id.subjectCheckBox);
        }
    }
}
