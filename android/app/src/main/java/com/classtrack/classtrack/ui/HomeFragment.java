package com.classtrack.classtrack.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.classtrack.classtrack.R;
import com.classtrack.classtrack.models.ClassModel;

import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private ClassViewModel classViewModel;
    private ClassAdapter adapter;
    private TextView textViewOverallStats;
    private TextView textViewStatusMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        textViewOverallStats = view.findViewById(R.id.textViewOverallStats);
        textViewStatusMessage = view.findViewById(R.id.textViewStatusMessage);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewClasses);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ClassAdapter();
        recyclerView.setAdapter(adapter);

        classViewModel = new ViewModelProvider(requireActivity()).get(ClassViewModel.class);
        classViewModel.getAllClasses().observe(getViewLifecycleOwner(), classes -> {
            adapter.setClasses(classes);
            updateOverallStats(classes);
        });

        adapter.setOnAttendanceClickListener((classModel, isPresent) -> {
            classModel.setTotalClasses(classModel.getTotalClasses() + 1);
            if (isPresent) {
                classModel.setAttendedClasses(classModel.getAttendedClasses() + 1);
            }
            classViewModel.update(classModel);
        });

        adapter.setOnClassActionListener(new ClassAdapter.OnClassActionListener() {
            @Override
            public void onEditClick(ClassModel classModel) {
                Intent intent = new Intent(getContext(), AddClassActivity.class);
                intent.putExtra("class_id", classModel.getId());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(ClassModel classModel) {
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Delete Class")
                        .setMessage("Are you sure you want to delete " + classModel.getName() + "?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            classViewModel.delete(classModel);
                            android.widget.Toast.makeText(getContext(), "Class deleted", android.widget.Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        return view;
    }

    private void updateOverallStats(List<ClassModel> classes) {
        int totalClasses = 0;
        int attendedClasses = 0;
        for (ClassModel c : classes) {
            totalClasses += c.getTotalClasses();
            attendedClasses += c.getAttendedClasses();
        }

        double percentage = totalClasses == 0 ? 0 : ((double) attendedClasses / totalClasses) * 100;
        
        textViewOverallStats.setText(String.format(Locale.getDefault(), "%.1f%%", percentage));
        
        if (percentage < 80 && totalClasses > 0) {
            textViewStatusMessage.setText("Warning: Below 80%! You need to attend more classes.");
            textViewStatusMessage.setTextColor(Color.RED);
        } else {
            textViewStatusMessage.setText("Great job! You are above the 80% threshold.");
            textViewStatusMessage.setTextColor(Color.parseColor("#2E7D32"));
        }
    }
}
