package com.classtrack.classtrack.ui;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.classtrack.classtrack.R;
import com.classtrack.classtrack.models.ClassModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

public class AddClassActivity extends AppCompatActivity {
    private TextInputEditText editTextName;
    private TextInputEditText editTextVenue;
    private Spinner spinnerDay;
    private Button buttonSelectTime;
    private String selectedTime = "";
    private ClassViewModel classViewModel;
    private int existingClassId = -1;
    private ClassModel existingClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        editTextName = findViewById(R.id.editTextName);
        editTextVenue = findViewById(R.id.editTextVenue);
        spinnerDay = findViewById(R.id.spinnerDay);
        buttonSelectTime = findViewById(R.id.buttonSelectTime);
        Button buttonSave = findViewById(R.id.buttonSave);

        classViewModel = new ViewModelProvider(this).get(ClassViewModel.class);

        buttonSelectTime.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(AddClassActivity.this, (timePicker, selectedHour, selectedMinute) -> {
                selectedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                buttonSelectTime.setText(selectedTime);
            }, hour, minute, true);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        });

        buttonSave.setOnClickListener(v -> saveClass());

        // Check if editing
        existingClassId = getIntent().getIntExtra("class_id", -1);
        if (existingClassId != -1) {
            setTitle("Edit Class");
            buttonSave.setText("Update Class");
            classViewModel.getAllClasses().observe(this, classes -> {
                for (ClassModel c : classes) {
                    if (c.getId() == existingClassId) {
                        existingClass = c;
                        populateFields();
                        break;
                    }
                }
            });
        }
    }

    private void populateFields() {
        editTextName.setText(existingClass.getName());
        editTextVenue.setText(existingClass.getVenue());
        selectedTime = existingClass.getTime();
        buttonSelectTime.setText(selectedTime);
        
        // Set spinner selection for day
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerDay.getAdapter();
        int position = adapter.getPosition(existingClass.getDay());
        spinnerDay.setSelection(position);
    }

    private void saveClass() {
        String name = editTextName.getText().toString().trim();
        String venue = editTextVenue.getText().toString().trim();
        String day = spinnerDay.getSelectedItem().toString();

        if (name.isEmpty() || venue.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (existingClassId != -1 && existingClass != null) {
            existingClass.setName(name);
            existingClass.setVenue(venue);
            existingClass.setDay(day);
            existingClass.setTime(selectedTime);
            
            classViewModel.update(existingClass);
            com.classtrack.classtrack.receivers.NotificationScheduler.cancelReminder(this, existingClassId);
            com.classtrack.classtrack.receivers.NotificationScheduler.scheduleReminder(this, existingClass);
            
            Toast.makeText(this, "Class updated", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            ClassModel newClass = new ClassModel(name, day, selectedTime, venue, 0, 0);
            classViewModel.insert(newClass, id -> {
                newClass.setId((int) id);
                com.classtrack.classtrack.receivers.NotificationScheduler.scheduleReminder(this, newClass);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Class saved and reminder set", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        }
    }
}
