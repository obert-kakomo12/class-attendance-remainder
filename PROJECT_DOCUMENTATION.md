# ClassTrack: Intelligent Academic Attendance Manager

## 1. Project Overview
**ClassTrack** is a sophisticated Android application designed to help university students manage their academic schedules and maintain optimal attendance levels. Unlike simple timetable apps, ClassTrack integrates smart reminders and data-driven attendance analytics to ensure students never miss a class and stay above critical attendance thresholds (e.g., 80%).

## 2. Problem Statement
Students often struggle with:
*   Forgetting class times or venues across complex weekly schedules.
*   Losing track of their attendance percentage, which can lead to exam disqualification.
*   Manual calculations to determine if they can afford to miss a class for personal reasons.

ClassTrack solves these issues by automating reminders and providing real-time attendance insights.

## 3. Core Features

### 3.1. Dynamic Class Management
*   **Add/Edit/Delete**: Users can manage their subjects with full CRUD (Create, Read, Update, Delete) functionality.
*   **Contextual Details**: Each class entry includes the subject name, day of the week, exact time, and venue/room number.

### 3.2. Smart Double-Alarm System
The app features a robust notification engine that triggers at two critical points:
1.  **Lead-Time Reminder**: Triggers at a user-defined interval (e.g., 15, 30, or 45 minutes) before the class to allow for travel and preparation.
2.  **Start-Time Alarm**: Triggers exactly when the class starts to ensure the student is in their seat.

### 3.3. Attendance Analytics & Decision Support
*   **Visual Tracking**: Every class item features a Material 3 progress indicator showing current attendance levels.
*   **Predictive Logic**: The app calculates a "Safe to Skip" status based on current data. It warns the user if missing a class would drop their attendance below the 80% threshold.
*   **Color Coding**: 
    *   **Green**: Safe (above 80%).
    *   **Orange**: Critical (don't skip today).
    *   **Red**: At Risk (already below 80%).

### 3.4. Personalization & Settings
*   **Custom Sounds**: Users can select their own alarm/notification sounds from their device.
*   **Volume Control**: Manual alarm volume override independent of system settings.
*   **Profile Management**: Personalized experience with student name, ID, and profile picture.

## 4. Technical Architecture

### 4.1. Technology Stack
*   **Language**: Java (Native Android).
*   **Database**: Room Persistent Library (Abstraction over SQLite for reliable local storage).
*   **Architecture Pattern**: MVVM (Model-View-ViewModel) to ensure separation of concerns and maintainability.
*   **UI Components**: Material Design 3 (M3) for a modern, premium aesthetic.

### 4.2. Key Components
*   **`ClassRepository`**: Manages data operations and handles the synchronization between the database and the UI.
*   **`NotificationScheduler`**: Interfaces with the Android `AlarmManager` to schedule precision wakeups using `setExactAndAllowWhileIdle`.
*   **`ClassReminderReceiver`**: A BroadcastReceiver that handles alarm triggers, manages notification channels, and plays audio using `MediaPlayer`.

## 5. Design Decisions
*   **Emerald & Slate Theme**: Chosen for a "Calm yet Professional" academic vibe.
*   **Glassmorphism**: Subtle effects used in the UI to give a premium, high-end feel.
*   **Material Switch & Sliders**: Used for settings to provide tactile, intuitive user feedback.

## 6. Installation & Deployment
The project is built using Gradle. The production-ready APK is generated via the `./gradlew assembleDebug` command, ensuring all desugaring and optimization tasks are completed for compatibility across Android versions (Min SDK 21).

---
**Developer**: Obert Kakomo  
**Version**: 2.0 (Native Migration)  
**Repository**: [GitHub Link](https://github.com/obert-kakomo12/class-attendance-remainder)
