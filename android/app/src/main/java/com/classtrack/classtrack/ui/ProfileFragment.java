package com.classtrack.classtrack.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.classtrack.classtrack.R;
import com.google.android.material.button.MaterialButton;

public class ProfileFragment extends Fragment {

    private TextView textViewName;
    private TextView textViewId;
    private SharedPreferences prefs;
    private com.google.android.material.imageview.ShapeableImageView ivProfile;
    private androidx.activity.result.ActivityResultLauncher<String> imagePicker;
    private androidx.activity.result.ActivityResultLauncher<Intent> soundPicker;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Image Picker
        imagePicker = registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        // Persist permission for the URI
                        try {
                            requireActivity().getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } catch (Exception e) {}
                        
                        prefs.edit().putString("profile_image", uri.toString()).apply();
                        ivProfile.setImageURI(uri);
                    }
                });

        // Sound Picker
        soundPicker = registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                        android.net.Uri uri = result.getData().getParcelableExtra(android.media.RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                        if (uri != null) {
                            prefs.edit().putString("alarm_sound_uri", uri.toString()).apply();
                            Toast.makeText(getContext(), "Alarm Sound Updated!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ivProfile = view.findViewById(R.id.ivProfilePicture);
        textViewName = view.findViewById(R.id.textViewProfileName);
        textViewId = view.findViewById(R.id.textViewProfileId);
        MaterialButton btnEdit = view.findViewById(R.id.btnEditProfile);
        MaterialButton btnLogout = view.findViewById(R.id.btnLogout);
        MaterialButton btnChangeSound = view.findViewById(R.id.btnChangeSound);

        prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        loadUserData();

        // Load profile image if exists
        String savedImage = prefs.getString("profile_image", null);
        if (savedImage != null) {
            ivProfile.setImageURI(android.net.Uri.parse(savedImage));
        }

        ivProfile.setOnClickListener(v -> imagePicker.launch("image/*"));
        
        btnChangeSound.setOnClickListener(v -> {
            Intent intent = new Intent(android.media.RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(android.media.RingtoneManager.EXTRA_RINGTONE_TYPE, android.media.RingtoneManager.TYPE_NOTIFICATION);
            intent.putExtra(android.media.RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alarm Sound");
            soundPicker.launch(intent);
        });


        android.widget.Spinner spinnerLeadTime = view.findViewById(R.id.spinnerLeadTime);
        com.google.android.material.materialswitch.MaterialSwitch switchSound = view.findViewById(R.id.switchSound);
        android.widget.SeekBar seekBarVolume = view.findViewById(R.id.seekBarVolume);
        TextView textViewVolumeLabel = view.findViewById(R.id.textViewVolumeLabel);

        int savedLeadTimePos = prefs.getInt("lead_time_pos", 1); // Default 30 mins
        boolean savedSound = prefs.getBoolean("enable_sound", true);
        int savedVolume = prefs.getInt("alarm_volume", 80);
        
        spinnerLeadTime.setSelection(savedLeadTimePos);
        switchSound.setChecked(savedSound);
        seekBarVolume.setProgress(savedVolume);
        textViewVolumeLabel.setText("Alarm Volume: " + savedVolume + "%");

        seekBarVolume.setOnSeekBarChangeListener(new android.widget.SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(android.widget.SeekBar seekBar, int progress, boolean fromUser) {
                textViewVolumeLabel.setText("Alarm Volume: " + progress + "%");
                prefs.edit().putInt("alarm_volume", progress).apply();
            }
            @Override
            public void onStartTrackingTouch(android.widget.SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(android.widget.SeekBar seekBar) {}
        });

        spinnerLeadTime.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                int minutes = 30;
                if (position == 0) minutes = 15;
                if (position == 2) minutes = 45;
                prefs.edit().putInt("lead_time_pos", position).putInt("reminder_minutes", minutes).apply();
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("enable_sound", isChecked).apply();
        });

        btnEdit.setOnClickListener(v -> showEditDialog());
        btnLogout.setOnClickListener(v -> showLogoutDialog());

        return view;

    }

    private void loadUserData() {
        String name = prefs.getString("user_name", "Obert Kakomo");
        String id = prefs.getString("user_id", "2024-00123");
        textViewName.setText(name);
        textViewId.setText("Student ID: " + id);
    }

    private void showEditDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_profile, null);
        EditText etName = dialogView.findViewById(R.id.editTextName);
        EditText etId = dialogView.findViewById(R.id.editTextId);

        etName.setText(prefs.getString("user_name", "Obert Kakomo"));
        etId.setText(prefs.getString("user_id", "2024-00123"));

        new AlertDialog.Builder(requireContext())
                .setTitle("Edit Profile")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = etName.getText().toString();
                    String newId = etId.getText().toString();
                    
                    prefs.edit()
                            .putString("user_name", newName)
                            .putString("user_id", newId)
                            .apply();
                    
                    loadUserData();
                    
                    // Update activity title if it's MainActivity
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).updateTitle("My Account");
                    }
                    
                    Toast.makeText(getContext(), "Profile Updated!", Toast.LENGTH_SHORT).show();

                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    requireActivity().finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}

