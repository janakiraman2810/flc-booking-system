package com.flc;

import java.util.List;

public class Lesson {
    private String lessonId;
    private int weekend;
    private DayOfWeek day;
    private TimeSlot time;
    private Exercise exercise;
    private static final int MAX_CAPACITY = 4;

    public Lesson(String lessonId, int weekend, DayOfWeek day, TimeSlot time, Exercise exercise) {
        this.lessonId = lessonId;
        this.weekend = weekend;
        this.day = day;
        this.time = time;
        this.exercise = exercise;
    }

    public int getAvailableSpaces(List<Booking> activeBookings) {
        int count = 0;
        for (Booking b : activeBookings) {
            // Count any spaces taken by someone who requested booking or has attended
            if (b.getLesson().equals(this) && (b.getStatus() == BookingStatus.BOOKED || b.getStatus() == BookingStatus.ATTENDED)) {
                count++;
            }
        }
        return MAX_CAPACITY - count;
    }

    public boolean isFull(List<Booking> activeBookings) {
        return getAvailableSpaces(activeBookings) <= 0;
    }

    public String getLessonId() { return lessonId; }
    public int getWeekend() { return weekend; }
    public DayOfWeek getDay() { return day; }
    public TimeSlot getTime() { return time; }
    public Exercise getExercise() { return exercise; }
    public int getMaxCapacity() { return MAX_CAPACITY; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lesson lesson = (Lesson) o;
        return lessonId != null ? lessonId.equals(lesson.lessonId) : lesson.lessonId == null;
    }

    @Override
    public int hashCode() {
        return lessonId != null ? lessonId.hashCode() : 0;
    }
}
