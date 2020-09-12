package com.niklasarndt.matsebutler.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class Configuration {

    @JsonProperty
    private List<Long> admins;

    @JsonProperty
    private Long guild;

    @JsonProperty
    private String token;

    @JsonProperty(value = "daily")
    private String dailyChannel;

    @JsonProperty(value = "weekly")
    private String weeklyChannel;

    public List<Long> getAdmins() {
        return admins;
    }

    public boolean isAdmin(long id) {
        return admins != null && admins.contains(id);
    }

    public void addAdmin(long id) {
        if (admins == null) admins = new ArrayList<>();
        admins.add(id);
    }

    public Long getGuild() {
        return guild;
    }

    public String getToken() {
        return token;
    }

    public String getDailyChannel() {
        return dailyChannel == null ? "stundenplan-tag" : dailyChannel;
    }

    public String getWeeklyChannel() {
        return weeklyChannel == null ? "stundenplan-woche" : weeklyChannel;
    }
}
