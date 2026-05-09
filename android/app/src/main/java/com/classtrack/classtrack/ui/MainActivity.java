package com.classtrack.classtrack.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import android.view.View;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.classtrack.classtrack.R;
import com.classtrack.classtrack.models.ClassModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private ClassViewModel classViewModel;
    private FloatingActionButton fabAddClass;
    private int currentNavIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fabAddClass = findViewById(R.id.fabAddClass);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            String title = "ClassTrack";
            boolean showFab = false;
            int newIndex = 0;

            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
                title = getHomeTitle();
                showFab = true;
                newIndex = 0;
            } else if (itemId == R.id.nav_stats) {
                selectedFragment = new StatsFragment();
                title = "Performance Report";
                newIndex = 1;
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
                title = "My Account";
                newIndex = 2;
            }

            if (selectedFragment != null) {
                androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                
                // 3D Animation logic based on direction
                if (newIndex > currentNavIndex) {
                    transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                } else if (newIndex < currentNavIndex) {
                    transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                }
                
                currentNavIndex = newIndex;
                
                transaction.replace(R.id.fragment_container, selectedFragment).commit();
                updateTitle(title);
                fabAddClass.setVisibility(showFab ? View.VISIBLE : View.GONE);
            }
            return true;
        });

        // Set default fragment and title
        bottomNav.setSelectedItemId(R.id.nav_home);

        fabAddClass.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddClassActivity.class);
            startActivity(intent);
        });
    }

    public String getHomeTitle() {
        android.content.SharedPreferences prefs = getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE);
        String name = prefs.getString("user_name", "User");
        return "Hello, " + name;
    }

    public void updateTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }
}

