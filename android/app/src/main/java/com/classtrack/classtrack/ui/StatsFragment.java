package com.classtrack.classtrack.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.classtrack.classtrack.R;
import com.classtrack.classtrack.models.ClassModel;

import java.util.ArrayList;
import java.util.List;

public class StatsFragment extends Fragment {

    private ClassViewModel classViewModel;
    private ClassAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewRiskClasses);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ClassAdapter();
        adapter.setShowButtons(false); // Hide buttons in Statistics tab
        recyclerView.setAdapter(adapter);

        android.widget.TextView textViewSubtitle = view.findViewById(R.id.textViewSummarySubtitle);

        classViewModel = new ViewModelProvider(requireActivity()).get(ClassViewModel.class);
        classViewModel.getAllClasses().observe(getViewLifecycleOwner(), classes -> {
            if (classes == null || classes.isEmpty()) {
                view.findViewById(R.id.textViewNoData).setVisibility(View.VISIBLE);
                view.findViewById(R.id.cardActionPlan).setVisibility(View.GONE);
                textViewSubtitle.setText("No data yet");
                adapter.setClasses(new ArrayList<>());
            } else {
                view.findViewById(R.id.textViewNoData).setVisibility(View.GONE);
                
                int totalClasses = 0;
                int attendedClasses = 0;
                boolean hasAtRisk = false;
                
                for (ClassModel c : classes) {
                    totalClasses += c.getTotalClasses();
                    attendedClasses += c.getAttendedClasses();
                    if (c.getAttendancePercentage() < 80) {
                        hasAtRisk = true;
                    }
                }
                
                double avg = totalClasses == 0 ? 0 : ((double) attendedClasses / totalClasses) * 100;
                
                if (hasAtRisk) {
                    textViewSubtitle.setText("Academic Standing: Warning (Modules at risk)");
                    textViewSubtitle.setTextColor(android.graphics.Color.RED);
                } else if (avg > 90) {
                    textViewSubtitle.setText("Academic Standing: Honors (Excellent!)");
                    textViewSubtitle.setTextColor(android.graphics.Color.parseColor("#2E7D32"));
                } else {
                    textViewSubtitle.setText("Academic Standing: Good");
                    textViewSubtitle.setTextColor(android.graphics.Color.parseColor("#2E7D32"));
                }
                
                view.findViewById(R.id.cardActionPlan).setVisibility(hasAtRisk ? View.VISIBLE : View.GONE);
                
                // Sort classes by percentage
                java.util.Collections.sort(classes, (c1, c2) -> 
                    Double.compare(c1.getAttendancePercentage(), c2.getAttendancePercentage()));
                
                adapter.setClasses(classes);
            }
        });




        // Hide buttons in this view to make it look like a report
        adapter.setOnAttendanceClickListener(null); 

        return view;
    }
}
