package com.classtrack.classtrack.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.classtrack.classtrack.models.ClassModel;

import java.util.Calendar;

public class NotificationScheduler {
    public static void scheduleReminder(Context context, ClassModel classModel) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ClassReminderReceiver.class);
        intent.putExtra("class_name", classModel.getName());
        intent.putExtra("venue", classModel.getVenue());
        intent.putExtra("class_id", classModel.getId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                classModel.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Parse time and day to set alarm
        String[] timeParts = classModel.getTime().split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // Adjust for day of week
        int dayOfWeek = getDayOfWeekInt(classModel.getDay());
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);

        // Fetch user preference for lead time
        android.content.SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int leadMinutes = prefs.getInt("reminder_minutes", 30);
        
        // Schedule according to preference
        calendar.add(Calendar.MINUTE, -leadMinutes);

        // If time passed, schedule for next week
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 7);
        }


        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        }
    }

    private static int getDayOfWeekInt(String day) {
        switch (day) {
            case "Monday": return Calendar.MONDAY;
            case "Tuesday": return Calendar.TUESDAY;
            case "Wednesday": return Calendar.WEDNESDAY;
            case "Thursday": return Calendar.THURSDAY;
            case "Friday": return Calendar.FRIDAY;
            case "Saturday": return Calendar.SATURDAY;
            case "Sunday": return Calendar.SUNDAY;
            default: return Calendar.MONDAY;
        }
    }
}
