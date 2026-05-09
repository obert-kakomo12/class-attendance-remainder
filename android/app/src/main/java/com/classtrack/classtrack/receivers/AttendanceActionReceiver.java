package com.classtrack.classtrack.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.classtrack.classtrack.database.AppDatabase;
import com.classtrack.classtrack.database.ClassDao;
import com.classtrack.classtrack.models.ClassModel;

import java.util.concurrent.Executors;

public class AttendanceActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int classId = intent.getIntExtra("class_id", -1);
        boolean isPresent = intent.getBooleanExtra("is_present", false);

        if (classId != -1) {
            Executors.newSingleThreadExecutor().execute(() -> {
                ClassDao dao = AppDatabase.getInstance(context).classDao();
                ClassModel classModel = dao.getClassById(classId);
                if (classModel != null) {
                    classModel.setTotalClasses(classModel.getTotalClasses() + 1);
                    if (isPresent) {
                        classModel.setAttendedClasses(classModel.getAttendedClasses() + 1);
                    }
                    dao.update(classModel);
                    
                    // Cancel the notification after action
                    android.app.NotificationManager manager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(classId);
                }
            });
            
            String status = isPresent ? "Present" : "Absent";
            Toast.makeText(context, "Marked " + status + " for class", Toast.LENGTH_SHORT).show();
        }
    }
}
