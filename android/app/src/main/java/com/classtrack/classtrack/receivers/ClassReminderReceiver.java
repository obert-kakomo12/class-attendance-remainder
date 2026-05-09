package com.classtrack.classtrack.receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.classtrack.classtrack.R;
import com.classtrack.classtrack.ui.MainActivity;

public class ClassReminderReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "class_reminders";

    @Override
    public void onReceive(Context context, Intent intent) {
        String className = intent.getStringExtra("class_name");
        String venue = intent.getStringExtra("venue");
        int id = intent.getIntExtra("class_id", 0);

        showNotification(context, className, venue, id);
    }

    private void showNotification(Context context, String className, String venue, int id) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Class Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_IMMUTABLE);

        // Action: Present
        Intent presentIntent = new Intent(context, AttendanceActionReceiver.class);
        presentIntent.putExtra("class_id", id);
        presentIntent.putExtra("is_present", true);
        PendingIntent presentPendingIntent = PendingIntent.getBroadcast(context, id + 1000, presentIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Action: Absent
        Intent absentIntent = new Intent(context, AttendanceActionReceiver.class);
        absentIntent.putExtra("class_id", id);
        absentIntent.putExtra("is_present", false);
        PendingIntent absentPendingIntent = PendingIntent.getBroadcast(context, id + 2000, absentIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        android.content.SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        boolean enableSound = prefs.getBoolean("enable_sound", true);
        int leadTime = prefs.getInt("reminder_minutes", 30);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("⏰ CLASS STARTING SOON")
                .setContentText(className + " starts in " + leadTime + " mins at " + venue)
                .setPriority(enableSound ? NotificationCompat.PRIORITY_MAX : NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVibrate(enableSound ? new long[]{0, 500, 200, 500} : new long[]{0})
                .setDefaults(enableSound ? NotificationCompat.DEFAULT_ALL : 0)
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.checkbox_on_background, "Mark Present", presentPendingIntent)
                .addAction(android.R.drawable.ic_delete, "Mark Absent", absentPendingIntent)
                .setAutoCancel(true);
        
        if (enableSound) {
            int volumePercent = prefs.getInt("alarm_volume", 80);
            float volume = volumePercent / 100f;
            
            try {
                String savedSoundUri = prefs.getString("alarm_sound_uri", null);
                android.net.Uri soundUri = (savedSoundUri != null) ? android.net.Uri.parse(savedSoundUri) : android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION);
                
                android.media.MediaPlayer mediaPlayer = new android.media.MediaPlayer();
                mediaPlayer.setDataSource(context, soundUri);
                mediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_ALARM);
                mediaPlayer.setVolume(volume, volume);
                mediaPlayer.setOnCompletionListener(android.media.MediaPlayer::release);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        notificationManager.notify(id, builder.build());
    }
}

