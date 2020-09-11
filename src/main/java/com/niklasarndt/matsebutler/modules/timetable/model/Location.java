package com.niklasarndt.matsebutler.modules.timetable.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Location {

    @JsonProperty
    private String name;

    @JsonProperty
    private String street;

    @JsonProperty("nr")
    private String number;

    @JsonProperty("desc")
    private String description;


    public String getName() {
        return name;
    }

    public String getStreet() {
        return street;
    }

    public String getNumber() {
        return number;
    }

    public String getDescription() {
        return description;
    }
}
