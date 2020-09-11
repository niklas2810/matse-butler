package com.niklasarndt.matsebutler.modules.timetable.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Lecturer {

    @JsonProperty
    private String name;

    @JsonProperty
    private String email;

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
