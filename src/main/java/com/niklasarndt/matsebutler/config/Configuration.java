package com.niklasarndt.matsebutler.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Configuration {

    @JsonProperty
    private List<Long> admins;

    @JsonProperty
    private Long guild;

    @JsonProperty
    private String token;

    public List<Long> getAdmins() {
        return admins;
    }

    public boolean isAdmin(long id) {
        return admins.contains(id);
    }

    public void addAdmin(long id) {
        admins.add(id);
    }

    public Long getGuild() {
        return guild;
    }

    public String getToken() {
        return token;
    }
}
