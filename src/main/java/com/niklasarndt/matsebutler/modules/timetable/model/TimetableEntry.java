package com.niklasarndt.matsebutler.modules.timetable.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class TimetableEntry {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @JsonProperty
    private String title;

    @JsonProperty("start")
    private String rawStart;

    @JsonProperty("end")
    private String rawEnd;

    @JsonProperty
    private Location location;

    @JsonProperty
    private Lecturer lecturer;

    @JsonProperty
    private String information;

    @JsonProperty
    private String isHoliday;

    @JsonProperty
    private String isExercise;

    @JsonProperty
    private boolean allDay;

    @JsonProperty
    private String isLecture;

    public String getTitle() {
        return title;
    }

    public String getRawStart() {
        return rawStart;
    }

    public String getRawEnd() {
        return rawEnd;
    }

    public LocalDateTime getStartParsed() {
        //2020-09-11T08:00:00
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss");

        try {
            return LocalDateTime.parse(rawStart, format);
        } catch (Exception e) {
            logger.error("Can not parse start time {}", rawStart, e);
            return LocalDateTime.now();
        }
    }

    public LocalDateTime getEndParsed() {
        //2020-09-11T08:00:00
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss");

        try {
            return LocalDateTime.parse(rawEnd, format);
        } catch (Exception e) {
            logger.error("Can not parse end time {}", rawEnd, e);
            return LocalDateTime.now();
        }
    }

    public Location getLocation() {
        return location;
    }

    public Lecturer getLecturer() {
        return lecturer;
    }

    public String getInformation() {
        return information;
    }

    public boolean isHoliday() {
        return isHoliday.equals("1");
    }

    public boolean isExercise() {
        return isExercise.equals("1");
    }

    public boolean isAllDay() {
        return allDay;
    }

    public boolean isLecture() {
        return isLecture.equals("1");
    }
}
