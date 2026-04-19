package com.flc;

import java.util.*;

public class FLCSystem {
    private List<Member> members = new ArrayList<>();
    private List<Lesson> timetable = new ArrayList<>();
    private List<Booking> bookings = new ArrayList<>();
    private List<Review> reviews = new ArrayList<>();

    public void addMember(Member m) { members.add(m); }
    public void addLesson(Lesson l) { timetable.add(l); }
    public List<Booking> getBookings() { return bookings; }

    public List<Lesson> getTimetableByDay(DayOfWeek day) {
        List<Lesson> result = new ArrayList<>();
        for (Lesson l : timetable) {
            if (l.getDay() == day) result.add(l);
        }
        return result;
    }

    public List<Lesson> getTimetableByExercise(String exerciseName) {
        List<Lesson> result = new ArrayList<>();
        for (Lesson l : timetable) {
            if (l.getExercise().getName().equalsIgnoreCase(exerciseName)) result.add(l);
        }
        return result;
    }

    public boolean bookLesson(Member member, Lesson lesson) {
        if (lesson.isFull(bookings)) return false;
        
        // Ensure no time conflict (cannot be active in two classes at same time)
        for (Booking b : bookings) {
            if (b.getMember().equals(member) && (b.getStatus() == BookingStatus.BOOKED || b.getStatus() == BookingStatus.ATTENDED)) {
                if (b.getLesson().getWeekend() == lesson.getWeekend() &&
                    b.getLesson().getDay() == lesson.getDay() &&
                    b.getLesson().getTime() == lesson.getTime()) {
                    return false; // Time conflict
                }
            }
        }
        
        String bId = "B" + (bookings.size() + 1);
        bookings.add(new Booking(bId, member, lesson));
        return true;
    }

    public boolean cancelBooking(Booking booking) {
        if (booking.getStatus() == BookingStatus.BOOKED) {
            booking.setStatus(BookingStatus.CANCELLED);
            return true;
        }
        return false;
    }

    public boolean attendLesson(Booking booking) {
        if (booking.getStatus() == BookingStatus.BOOKED) {
            booking.setStatus(BookingStatus.ATTENDED);
            return true;
        }
        return false;
    }

    public boolean changeBooking(Booking booking, Lesson newLesson) {
        if (booking.getStatus() != BookingStatus.BOOKED) return false;
        
        // Check if there is space in the new lesson
        if (newLesson.isFull(bookings)) return false;

        // Temporarily free up space to check conflicts properly 
        BookingStatus oldStatus = booking.getStatus();
        booking.setStatus(BookingStatus.CHANGED);
        
        boolean success = bookLesson(booking.getMember(), newLesson);
        
        if (!success) {
            // Rollback if new booking fails (e.g. time conflict)
            booking.setStatus(oldStatus);
            return false;
        }
        
        return true;
    }

    public boolean addReview(Member member, Lesson lesson, int rating, String comment) {
        // Members must have ATTENDED to leave a review 
        boolean attended = false;
        for (Booking b : bookings) {
            if (b.getMember().equals(member) && b.getLesson().equals(lesson) && b.getStatus() == BookingStatus.ATTENDED) {
                attended = true;
                break;
            }
        }
        if (!attended) return false;

        reviews.add(new Review(member, lesson, rating, comment));
        return true;
    }

    public String generateAttendanceReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Attendance and Rating Report ---\n");
        
        for (Lesson l : timetable) {
            int attendedCount = 0;
            for (Booking b : bookings) {
                // Assessment check: Only attended members counted for the attendance report
                if(b.getLesson().equals(l) && b.getStatus() == BookingStatus.ATTENDED) {
                    attendedCount++;
                }
            }
            
            double totalRating = 0;
            int reviewCount = 0;
            for(Review r : reviews) {
                if(r.getLesson().equals(l)) {
                    totalRating += r.getRating();
                    reviewCount++;
                }
            }
            double avgRating = reviewCount > 0 ? totalRating / reviewCount : 0.0;
            
            sb.append(String.format("Weekend %d %-8s %-9s - %-10s : %d member(s) attended, Average Rating: %.1f/5.0%n",
                    l.getWeekend(), l.getDay(), l.getTime(), l.getExercise().getName(), attendedCount, avgRating));
        }
        return sb.toString();
    }

    public String generateHighestIncomeReport() {
        Map<String, Double> incomeByExercise = new HashMap<>();
        
        for (Booking b : bookings) {
            // Revenue is earned from anyone who successfully booked and kept it (Booked or Attended)
            if(b.getStatus() == BookingStatus.BOOKED || b.getStatus() == BookingStatus.ATTENDED) {
                String exName = b.getLesson().getExercise().getName();
                double price = b.getLesson().getExercise().getPrice();
                incomeByExercise.put(exName, incomeByExercise.getOrDefault(exName, 0.0) + price);
            }
        }

        String highestEx = null;
        double highestInc = -1;
        for (Map.Entry<String, Double> entry : incomeByExercise.entrySet()) {
            if(entry.getValue() > highestInc) {
                highestInc = entry.getValue();
                highestEx = entry.getKey();
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("--- Highest Income Report ---\n");
        if(highestEx != null) {
            sb.append("Exercise generating the highest income: ").append(highestEx)
              .append("\nTotal Income Generated: £").append(String.format("%.2f", highestInc)).append("\n");
        } else {
            sb.append("No bookings made, no income generated yet.\n");
        }
        return sb.toString();
    }
}
