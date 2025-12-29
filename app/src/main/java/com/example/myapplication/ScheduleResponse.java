package com.example.myapplication;
import java.util.List;

public class ScheduleResponse {
    public String group;
    public String date;
    public List<LessonModel> schedule;
}

class LessonModel {
    public String time;
    public String type;
    public String subject;
    public String room;
    public String teacher;
    public String period;
}