package com.flc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class FLCSystemTest {
    private FLCSystem system;
    private Member m1, m2, m3, m4, m5;
    private Exercise yoga, zumba;
    private Lesson l1, l2;

    @BeforeEach
    public void setup() {
        system = new FLCSystem();
        m1 = new Member("1", "Alice");
        m2 = new Member("2", "Bob");
        m3 = new Member("3", "Charlie");
        m4 = new Member("4", "David");
        m5 = new Member("5", "Eve");
        
        system.addMember(m1);
        system.addMember(m2);
        system.addMember(m3);
        system.addMember(m4);
        system.addMember(m5);

        yoga = new Exercise("Yoga", 15.0);
        zumba = new Exercise("Zumba", 10.0);

        l1 = new Lesson("L1", 1, DayOfWeek.SATURDAY, TimeSlot.MORNING, yoga);
        l2 = new Lesson("L2", 1, DayOfWeek.SATURDAY, TimeSlot.AFTERNOON, zumba);
        
        system.addLesson(l1);
        system.addLesson(l2);
    }

    @Test
    public void testSuccessfulBooking() {
        assertTrue(system.bookLesson(m1, l1));
        assertEquals(BookingStatus.BOOKED, system.getBookings().get(0).getStatus());
        assertEquals(1, system.getBookings().size());
    }

    @Test
    public void testTimeConflict() {
        // A user booking the same weekend, day, and time
        Lesson conflictingLesson = new Lesson("L3", 1, DayOfWeek.SATURDAY, TimeSlot.MORNING, zumba);
        system.addLesson(conflictingLesson);

        assertTrue(system.bookLesson(m1, l1));
        assertFalse(system.bookLesson(m1, conflictingLesson)); // Fails due to time conflict
    }

    @Test
    public void testCapacityLimit() {
        assertTrue(system.bookLesson(m1, l1));
        assertTrue(system.bookLesson(m2, l1));
        assertTrue(system.bookLesson(m3, l1));
        assertTrue(system.bookLesson(m4, l1));
        
        assertFalse(system.bookLesson(m5, l1)); // 5th member fails because MAX_CAPACITY = 4
    }

    @Test
    public void testChangeBooking() {
        system.bookLesson(m1, l1);
        Booking booking = system.getBookings().get(0);
        
        assertTrue(system.changeBooking(booking, l2));
        
        // Old booking is marked CHANGED, a new one is created internally
        assertEquals(BookingStatus.CHANGED, booking.getStatus());
        assertEquals(2, system.getBookings().size());
        assertEquals(BookingStatus.BOOKED, system.getBookings().get(1).getStatus());
        assertEquals(l2, system.getBookings().get(1).getLesson());
    }

    @Test
    public void testCancelBooking() {
        system.bookLesson(m1, l1);
        Booking booking = system.getBookings().get(0);
        
        assertTrue(system.cancelBooking(booking));
        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        
        // Ensure capacity is freed
        assertTrue(system.bookLesson(m2, l1));
        assertTrue(system.bookLesson(m3, l1));
        assertTrue(system.bookLesson(m4, l1));
        assertTrue(system.bookLesson(m5, l1));
    }

    @Test
    public void testReviewOnlyAllowedIfAttended() {
        system.bookLesson(m1, l1);
        Booking booking = system.getBookings().get(0);
        
        // Before attending - should fail matching assessment logic
        assertFalse(system.addReview(m1, l1, 5, "Good class"));
        
        // Attend the lesson
        system.attendLesson(booking);
        
        // Review works now
        assertTrue(system.addReview(m1, l1, 5, "Good class"));
    }

    @Test
    public void testReportCalculations() {
        system.bookLesson(m1, l1);
        system.bookLesson(m2, l1);
        
        system.attendLesson(system.getBookings().get(0)); // Only m1 attended, m2 just booked
        system.addReview(m1, l1, 4, "Nice");

        String attendanceReport = system.generateAttendanceReport();
        assertTrue(attendanceReport.contains("1 member(s) attended")); // m2 shouldn't count
        assertTrue(attendanceReport.contains("4.0/5.0"));
        
        String incomeReport = system.generateHighestIncomeReport();
        // Since m1 attended and m2 booked, revenue from both counts (15.0 * 2 = 30.0)
        assertTrue(incomeReport.contains("Yoga"));
        assertTrue(incomeReport.contains("30.00"));
    }
}
