package com.classtrack.classtrack.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "classes")
public class ClassModel {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String day;
    private String time;
    private String venue;
    private int totalClasses;
    private int attendedClasses;

    public ClassModel(String name, String day, String time, String venue, int totalClasses, int attendedClasses) {
        this.name = name;
        this.day = day;
        this.time = time;
        this.venue = venue;
        this.totalClasses = totalClasses;
        this.attendedClasses = attendedClasses;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public int getTotalClasses() { return totalClasses; }
    public void setTotalClasses(int totalClasses) { this.totalClasses = totalClasses; }

    public int getAttendedClasses() { return attendedClasses; }
    public void setAttendedClasses(int attendedClasses) { this.attendedClasses = attendedClasses; }

    public double getAttendancePercentage() {
        if (totalClasses == 0) return 0.0;
        return ((double) attendedClasses / totalClasses) * 100;
    }
}
