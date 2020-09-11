package com.niklasarndt.matsebutler.modules;

import java.util.Objects;

/**
 * Created by Niklas on 2020/07/25.
 */
public class ButlerCommandInformation {

    public static final int MAX_CMD_LENGTH = 16;
    public static final int MAX_DESC_LENGTH = 512;

    private final String name;
    private final String[] aliases;
    private final int argsMin;
    private final int argsMax;
    private final String description;
    private final boolean privileged;

    protected ButlerCommandInformation(String name, String description) {
        this(name, 0, 0,
                description);
    }

    protected ButlerCommandInformation(String name, int argsMin, int argsMax,
                                       String description) {
        this(name, new String[0], argsMin, argsMax, description, false);
    }

    protected ButlerCommandInformation(String name, String[] aliases,
                                       int argsMin, int argsMax, String description,
                                       boolean privileged) {
        Objects.requireNonNull(name);

        this.name = name;
        this.aliases = aliases != null ? aliases : new String[0];
        this.argsMin = argsMin;
        this.argsMax = argsMax;
        this.description = description != null ? description : "n/a";
        this.privileged = privileged;

        applyLimits();
    }

    private void applyLimits() {
        if (this.name.length() == 0 || this.description.length() == 0) {
            throw new IllegalArgumentException("Both your command name and description " +
                    "must not be empty.");
        }
        if (this.name.length() > MAX_CMD_LENGTH) {
            throw new IllegalArgumentException(String.format(
                    "The module name '%s' is too long (max: %d characters, given: %d characters).",
                    name, MAX_CMD_LENGTH, this.name.length()));
        }
        if (argsMin < 0 || argsMax < argsMin) {
            throw new IllegalArgumentException(
                    String.format("Invalid argument limits for command \"%s\". (min: %d, max: %d)",
                            name, argsMin, argsMax));
        }
        if (this.description.length() > MAX_DESC_LENGTH) {
            throw new IllegalArgumentException(String.format(
                    "The description for the command '%s' is too long (max: %d characters, " +
                            "given: %d characters).",
                    name, MAX_DESC_LENGTH, this.description.length()));
        }
    }

    public String getName() {
        return name;
    }

    public String[] getAliases() {
        return aliases;
    }

    public int getArgsMin() {
        return argsMin;
    }

    public int getArgsMax() {
        return argsMax;
    }

    public String getDescription() {
        return description;
    }

    public boolean requiresPrivileges() {
        return privileged;
    }

    public boolean hasAlias(String name) {
        for (String al : aliases) {
            if (al.equals(name)) return true;
        }
        return false;
    }
}
