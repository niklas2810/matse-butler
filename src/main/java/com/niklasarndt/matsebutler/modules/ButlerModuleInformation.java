package com.niklasarndt.matsebutler.modules;

import com.niklasarndt.matsebutler.util.Emojis;
import java.util.Objects;

/**
 * Created by Niklas on 2020/07/25.
 */
public class ButlerModuleInformation {

    protected static final String DEFAULT_EMOJI = Emojis.QUESTION_MARK;

    private final String emoji;
    private final String name;
    private final String displayName;
    private final String description;
    private final String version;

    protected ButlerModuleInformation(String name) {
        this(name, null);
    }

    protected ButlerModuleInformation(String name, String description) {
        this(name, description, null);
    }

    protected ButlerModuleInformation(String name, String description, String version) {
        this(null, name, null, description, version);
    }

    protected ButlerModuleInformation(String emoji, String name, String displayName,
                                      String description, String version) {
        Objects.requireNonNull(name);

        this.emoji = emoji != null ? emoji : DEFAULT_EMOJI;
        this.name = name;
        this.displayName = displayName != null ? displayName : name;
        this.description = description != null ? description : "n/a";
        this.version = version != null ? version : "n/a";

        applyLimits();
    }

    private void applyLimits() {
        if (this.name.length() == 0 || this.displayName.length() == 0) {
            throw new IllegalArgumentException("Both module name and display name must " +
                    "not be null.");
        }
        if (this.description.length() == 0) {
            throw new IllegalArgumentException("The module description must not be null.");
        }
        if (!Emojis.isOnlyEmojis(this.emoji)) {
            throw new IllegalArgumentException(String.format(
                    "The emoji for the module '%s' is invalid.", name));
        }
        checkLengths();
    }

    private void checkLengths() {
        if (this.emoji.length() > 4) {
            throw new IllegalArgumentException(String.format(
                    "The emoji unicode for the module '%s' is too long " +
                            "(max: 4 characters, given: %d characters).",
                    name, this.emoji.length()));
        }
        if (this.name.length() > 16) {
            throw new IllegalArgumentException(String.format(
                    "The module name '%s' is too long " +
                            "(max: 16 characters, given: %d characters).",
                    name, this.name.length()));
        }
        if (this.displayName.length() > 32) {
            throw new IllegalArgumentException(String.format(
                    "The display name for the module '%s' is too long " +
                            "(max: 32 characters, given: %d characters).",
                    name, this.displayName.length()));
        }
        if (this.description.length() > 128) {
            throw new IllegalArgumentException(String.format(
                    "The description for the module '%s' is too long " +
                            "(max: 128 characters, given: %d characters).",
                    name, this.description.length()));
        }
        if (this.version.length() > 8) {
            throw new IllegalArgumentException(String.format(
                    "The version code for the module '%s' is too long " +
                            "(max: 8 characters, given: %d characters).",
                    name, this.version.length()));
        }
    }

    public String getEmoji() {
        return emoji;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return version;
    }

    public String generateTitle() {
        return String.format("%s **%s** (_%s_)", getEmoji(), getDisplayName(), getName());
    }
}
