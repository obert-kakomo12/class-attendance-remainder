import 'dart:developer' as developer;
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:timezone/timezone.dart' as tz;
import 'package:timezone/data/latest.dart' as tz;

class NotificationService {
  static final NotificationService _notificationService = NotificationService._internal();

  factory NotificationService() {
    return _notificationService;
  }

  final FlutterLocalNotificationsPlugin flutterLocalNotificationsPlugin = FlutterLocalNotificationsPlugin();

  NotificationService._internal();

  Future<void> init() async {
    developer.log('Initializing Notification Service...');
    const AndroidInitializationSettings initializationSettingsAndroid =
        AndroidInitializationSettings('@mipmap/ic_launcher');

    const InitializationSettings initializationSettings = InitializationSettings(
      android: initializationSettingsAndroid,
    );

    tz.initializeTimeZones();

    try {
      await flutterLocalNotificationsPlugin.initialize(
        settings: initializationSettings,
        onDidReceiveNotificationResponse: (NotificationResponse response) {
          // Handle notification tap
          developer.log('Notification tapped: ${response.payload}');
        },
      );
      developer.log('Notification Service Initialized.');
    } catch (e) {
      developer.log('Notification Init Failed: $e', error: e, name: 'NotificationService');
    }
  }

  Future<void> scheduleClassReminder(int id, String title, String venue, DateTime scheduledTime) async {
    // Schedule 30 minutes before
    final reminderTime = scheduledTime.subtract(const Duration(minutes: 30));
    
    if (reminderTime.isBefore(DateTime.now())) return;

    developer.log('Scheduling notification $id for $title at $reminderTime');
    await flutterLocalNotificationsPlugin.zonedSchedule(
      id: id,
      title: 'Class Reminder: $title',
      body: 'Starts at ${scheduledTime.hour}:${scheduledTime.minute.toString().padLeft(2, '0')} in $venue',
      scheduledDate: tz.TZDateTime.from(reminderTime, tz.local),
      notificationDetails: const NotificationDetails(
        android: AndroidNotificationDetails(
          'class_reminders',
          'Class Reminders',
          channelDescription: 'Notifications for upcoming classes',
          importance: Importance.max,
          priority: Priority.high,
        ),
      ),
      androidScheduleMode: AndroidScheduleMode.exactAllowWhileIdle,
      matchDateTimeComponents: DateTimeComponents.dayOfWeekAndTime,
    );
  }

  Future<void> cancelNotification(int id) async {
    await flutterLocalNotificationsPlugin.cancel(id: id);
  }
}
