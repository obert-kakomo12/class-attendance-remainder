package com.classtrack.classtrack.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.classtrack.classtrack.R;
import com.classtrack.classtrack.models.ClassModel;

import java.util.ArrayList;
import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {
    private List<ClassModel> classes = new ArrayList<>();
    private OnAttendanceClickListener listener;
    private boolean showButtons = true;

    public interface OnAttendanceClickListener {
        void onAttendanceClick(ClassModel classModel, boolean isPresent);
    }

    public interface OnClassActionListener {
        void onEditClick(ClassModel classModel);
        void onDeleteClick(ClassModel classModel);
    }

    public void setOnAttendanceClickListener(OnAttendanceClickListener listener) {
        this.listener = listener;
    }

    private OnClassActionListener actionListener;

    public void setOnClassActionListener(OnClassActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void setShowButtons(boolean showButtons) {
        this.showButtons = showButtons;
    }


    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class, parent, false);
        return new ClassViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        ClassModel currentClass = classes.get(position);
        holder.textViewName.setText(currentClass.getName());
        holder.textViewDayTime.setText(currentClass.getDay() + ", " + currentClass.getTime());
        holder.textViewVenue.setText("Venue: " + currentClass.getVenue());
        
        double percentage = currentClass.getAttendancePercentage();
        holder.attendanceProgress.setProgress((int) percentage);

        String attendanceText = String.format("Attendance: %d/%d (%.1f%%)", 
                currentClass.getAttendedClasses(), 
                currentClass.getTotalClasses(),
                percentage);
        holder.textViewAttendance.setText(attendanceText);

        String advice;
        int color;
        
        // Calculate what happens if we miss today (next class happens but attended stays same)
        double ifMissed = ((double) currentClass.getAttendedClasses() / (currentClass.getTotalClasses() + 1)) * 100;

        if (percentage < 80) {
            advice = "⚠️ AT RISK: Attendance below 80%";
            color = android.graphics.Color.RED;
            holder.attendanceProgress.setIndicatorColor(android.graphics.Color.RED);
        } else if (ifMissed < 80) {
            advice = "❗ CRITICAL: Don't skip today!";
            color = android.graphics.Color.parseColor("#E65100"); // Orange
            holder.attendanceProgress.setIndicatorColor(android.graphics.Color.parseColor("#E65100"));
        } else {
            advice = "✅ SAFE: You can skip today";
            color = android.graphics.Color.parseColor("#059669"); // Modern Emerald
            holder.attendanceProgress.setIndicatorColor(android.graphics.Color.parseColor("#059669"));
        }

        holder.textViewAttendance.setTextColor(color);
        holder.textViewAttendance.setText(attendanceText + "\n" + advice);

        if (showButtons) {
            holder.buttonPresent.setVisibility(View.VISIBLE);
            holder.buttonAbsent.setVisibility(View.VISIBLE);
        } else {
            holder.buttonPresent.setVisibility(View.GONE);
            holder.buttonAbsent.setVisibility(View.GONE);
        }

        holder.buttonPresent.setOnClickListener(v -> {
            if (listener != null) listener.onAttendanceClick(currentClass, true);
        });

        holder.buttonAbsent.setOnClickListener(v -> {
            if (listener != null) listener.onAttendanceClick(currentClass, false);
        });

        holder.buttonMore.setOnClickListener(v -> {
            android.widget.PopupMenu popup = new android.widget.PopupMenu(v.getContext(), v);
            popup.getMenu().add("Edit");
            popup.getMenu().add("Delete");
            popup.setOnMenuItemClickListener(item -> {
                if (actionListener != null) {
                    if (item.getTitle().equals("Edit")) {
                        actionListener.onEditClick(currentClass);
                    } else if (item.getTitle().equals("Delete")) {
                        actionListener.onDeleteClick(currentClass);
                    }
                }
                return true;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    public void setClasses(List<ClassModel> classes) {
        this.classes = classes;
        notifyDataSetChanged();
    }

    class ClassViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewName;
        private TextView textViewDayTime;
        private TextView textViewVenue;
        private TextView textViewAttendance;
        private Button buttonPresent;
        private Button buttonAbsent;
        private android.widget.ImageButton buttonMore;
        private com.google.android.material.progressindicator.LinearProgressIndicator attendanceProgress;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewClassName);
            textViewDayTime = itemView.findViewById(R.id.textViewDayTime);
            textViewVenue = itemView.findViewById(R.id.textViewVenue);
            textViewAttendance = itemView.findViewById(R.id.textViewAttendance);
            buttonPresent = itemView.findViewById(R.id.buttonPresent);
            buttonAbsent = itemView.findViewById(R.id.buttonAbsent);
            buttonMore = itemView.findViewById(R.id.buttonMore);
            attendanceProgress = itemView.findViewById(R.id.attendanceProgress);
        }
    }
}
