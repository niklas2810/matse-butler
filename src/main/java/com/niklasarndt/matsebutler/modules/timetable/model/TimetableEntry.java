package com.niklasarndt.matsebutler.modules.timetable.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


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

    @JsonIgnore
    private LocalDateTime startParsed;

    @JsonIgnore
    private LocalDateTime endParsed;

    @JsonIgnore
    private String infoParsed;

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
        if (startParsed != null) return startParsed;
        //2020-09-11T08:00:00
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                .withZone(ZoneId.of("GMT"));

        try {
            startParsed = LocalDateTime.parse(rawStart, format);
        } catch (Exception e) {
            logger.error("Can not parse start time {}", rawStart, e);
            startParsed = LocalDateTime.now();
        }
        return startParsed;
    }

    public LocalDateTime getEndParsed() {
        if (endParsed != null) return endParsed;
        //2020-09-11T08:00:00
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                .withZone(ZoneId.of("GMT"));
        try {
            endParsed = LocalDateTime.parse(rawEnd, format);
        } catch (Exception e) {
            logger.error("Can not parse end time {}", rawEnd, e);
            endParsed = LocalDateTime.now();
        }
        return endParsed;
    }

    public Location getLocation() {
        return location;
    }

    public Lecturer getLecturer() {
        return lecturer;
    }

    public String getInformation() {
        if (infoParsed != null) return infoParsed;

        infoParsed = information.replace("<br />", "\n").trim();
        return infoParsed;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimetableEntry)) return false;

        TimetableEntry that = (TimetableEntry) o;

        if (!title.equals(that.title)) return false;
        if (!rawStart.equals(that.rawStart)) return false;
        if (!rawEnd.equals(that.rawEnd)) return false;
        return Objects.equals(information, that.information);
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + rawStart.hashCode();
        result = 31 * result + rawEnd.hashCode();
        result = 31 * result + (information != null ? information.hashCode() : 0);
        return result;
    }
}
