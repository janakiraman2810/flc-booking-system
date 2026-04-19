package com.flc;

public class Review {
    private Member member;
    private Lesson lesson;
    private int rating; // 1 to 5
    private String comment;

    public Review(Member member, Lesson lesson, int rating, String comment) {
        // Enforce 1-5 rating constraints
        if(rating < 1) rating = 1;
        if(rating > 5) rating = 5;
        this.member = member;
        this.lesson = lesson;
        this.rating = rating;
        this.comment = comment;
    }

    public Member getMember() { return member; }
    public Lesson getLesson() { return lesson; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
}
