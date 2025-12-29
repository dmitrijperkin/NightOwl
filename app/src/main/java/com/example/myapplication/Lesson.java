package com.example.myapplication;

public class Lesson {
    public String time, subject, teacher, room, type;
    public int color;

    public Lesson(String time, String subject, String teacher, String room, String type, int color) {
        this.time = time;
        this.subject = subject;
        this.teacher = teacher;
        this.room = room;
        this.type = type;
        this.color = color;
    }
}