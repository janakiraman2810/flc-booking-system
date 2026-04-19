package com.flc;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        FLCSystem system = new FLCSystem();
        initializeData(system);
        
        System.out.println(system.generateAttendanceReport());
        System.out.println(system.generateHighestIncomeReport());
    }
    
    private static void initializeData(FLCSystem system) {
        // 1. Create 4 exercise types
        Exercise yoga = new Exercise("Yoga", 15.0);
        Exercise zumba = new Exercise("Zumba", 12.0);
        Exercise aquacise = new Exercise("Aquacise", 14.5);
        Exercise boxFit = new AssessmentExercise("Box Fit", 18.0); // Simple instantiation
        
        Exercise[] exercises = {yoga, zumba, aquacise, new Exercise("Box Fit", 18.0)};
        
        // 2. Create 10 Members
        Member[] members = new Member[10];
        for (int i = 0; i < 10; i++) {
            members[i] = new Member("M00" + (i + 1), "Member " + (i + 1));
            system.addMember(members[i]);
        }
        
        // 3. Create 8 Weekends (Saturday and Sunday, 3 lessons each = 48 lessons in total)
        DayOfWeek[] days = {DayOfWeek.SATURDAY, DayOfWeek.SUNDAY};
        TimeSlot[] times = {TimeSlot.MORNING, TimeSlot.AFTERNOON, TimeSlot.EVENING};
        
        int lessonCounter = 1;
        Random random = new Random(42); // Seed for reproducible dummy data
        
        for (int w = 1; w <= 8; w++) {
            for (DayOfWeek day : days) {
                for (TimeSlot time : times) {
                    Exercise ex = exercises[random.nextInt(exercises.length)];
                    Lesson l = new Lesson("L" + lessonCounter++, w, day, time, ex);
                    system.addLesson(l);
                }
            }
        }
        
        // 4. Book members to lessons 
        for (Lesson l : system.getTimetableByDay(DayOfWeek.SATURDAY)) {
            int bookingsToMake = random.nextInt(3) + 1; // 1 to 3 bookings
            for(int i=0; i<bookingsToMake; i++) {
                system.bookLesson(members[random.nextInt(10)], l);
            }
        }
        for (Lesson l : system.getTimetableByDay(DayOfWeek.SUNDAY)) {
            int bookingsToMake = random.nextInt(4) + 1; // 1 to 4 bookings
            for(int i=0; i<bookingsToMake; i++) {
                system.bookLesson(members[random.nextInt(10)], l);
            }
        }

        // 5. Demonstrate the Booking Lifecycle (Booked -> Changed / Cancelled / Attended)
        for (int i = 0; i < system.getBookings().size(); i++) {
            Booking b = system.getBookings().get(i);
            
            if (b.getStatus() == BookingStatus.BOOKED) {
                int lifecycleAction = random.nextInt(10);
                
                if (lifecycleAction == 0) {
                    // Demonstrate Cancellation
                    system.cancelBooking(b);
                } else if (lifecycleAction == 1) {
                    // Demonstrate Changing Lesson
                    Lesson newL = system.getTimetableByDay(DayOfWeek.SUNDAY).get(random.nextInt(10)); // pseudo random picking
                    system.changeBooking(b, newL);
                } else {
                    // Demonstrate Attending (which is the only way to leave a review)
                    system.attendLesson(b);
                }
            }
        }
        
        // 6. Generate exactly 20 reviews for valid ATTENDED bookings
        int reviewCount = 0;
        for (Booking b : system.getBookings()) {
            if (reviewCount >= 20) break;
            
            if (b.getStatus() == BookingStatus.ATTENDED) {
                int rating = random.nextInt(5) + 1; // 1 to 5 stars
                system.addReview(b.getMember(), b.getLesson(), rating, "Great class!");
                reviewCount++;
            }
        }
    }
}

class AssessmentExercise extends Exercise {
    public AssessmentExercise(String n, double p) { super(n, p); }
}
