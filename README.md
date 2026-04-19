# Furzefield Leisure Centre (FLC) Booking System

This is a complete, decoupled group exercise booking system built in Java. It dynamically handles class capacity limitations, prevents timeslot conflicts, requires explicit attendance for reviews, and generates automated income and attendance reports.

## System Design (UML)

```mermaid
classDiagram
    class FLCSystem {
        -List~Member~ members
        -List~Lesson~ timetable
        -List~Booking~ bookings
        -List~Review~ reviews
        +getTimetableByDay(DayOfWeek day) List~Lesson~
        +bookLesson(Member member, Lesson lesson) boolean
        +changeBooking(Booking booking, Lesson newLesson) boolean
        +cancelBooking(Booking booking) boolean
        +attendLesson(Booking booking) boolean
        +addReview(Member member, Lesson lesson, int rating, String reviewText) boolean
        +generateAttendanceReport() String
        +generateHighestIncomeReport() String
    }
    
    class Member {
        -String memberId
        -String name
    }
    
    class Lesson {
        -String lessonId
        -int weekend
        -DayOfWeek day
        -TimeSlot time
        -Exercise exercise
        -int MAX_CAPACITY = 4
        +getAvailableSpaces(List~Booking~ activeBookings) int
        +isFull(List~Booking~ activeBookings) boolean
    }
    
    class Exercise {
        -String name
        -double price
    }
    
    class Booking {
        -String bookingId
        -Member member
        -Lesson lesson
        -BookingStatus status
    }
    
    class Review {
        -Member member
        -Lesson lesson
        -int rating
        -String comment
    }
    
    class DayOfWeek {
        <<enumeration>>
        SATURDAY
        SUNDAY
    }
    
    class TimeSlot {
        <<enumeration>>
        MORNING
        AFTERNOON
        EVENING
    }

    class BookingStatus {
        <<enumeration>>
        BOOKED
        CHANGED
        CANCELLED
        ATTENDED
    }

    FLCSystem "1" *-- "*" Member : manages
    FLCSystem "1" *-- "*" Lesson : manages
    FLCSystem "1" *-- "*" Booking : manages
    FLCSystem "1" *-- "*" Review : stores
    
    Lesson "*" --> "1" Exercise : refers to
    Lesson "*" --> "1" DayOfWeek : scheduled on
    Lesson "*" --> "1" TimeSlot : happens at
    
    Booking "*" --> "1" Member : made by
    Booking "*" --> "1" Lesson : reserved for
    Booking "*" --> "1" BookingStatus : has state
    
    Review "*" --> "1" Member : written by
    Review "*" --> "1" Lesson : evaluates
```

## How to Compile and Run
You can quickly run the software and print out the core dynamic reports via the terminal using basic Java compilation:
```bash
mkdir -p out
javac -d out src/main/java/com/flc/*.java
java -cp out com.flc.Main
```

## How to Run Automated JUnit Tests
The logic governing Capacity limits, Time Conflicts, and Booking States is backed by an automated suite. To run it:
```bash
mvn clean test
```

## Strict Business Constraints Met
- **Booking Lifecycle:** Enforced `BookingStatus` (BOOKED, CHANGED, CANCELLED, ATTENDED).
- **Time Conflicts:** Mathematical restrictions against a user booking different lessons at the same time limit.
- **Review Integrity:** Enforced rule checking that a user has `ATTENDED` a `BOOKED` class before leaving a review.
- **Reporting Integrity:** Income counts `BOOKED / ATTENDED` users. Attendance purely counts `ATTENDED` users.
