package com.niklasarndt.matsebutler.modules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Niklas on 2020/07/25.
 */
public abstract class ButlerCommand {

    public static final Logger logger = LoggerFactory.getLogger(ButlerCommand.class);

    private final ButlerCommandInformation info;
    private ButlerModule module;

    public ButlerCommand(String name) {
        this(name, 0, 0);
    }

    public ButlerCommand(String name, String description, String... aliases) {
        this(name, description, false, aliases);
    }

    public ButlerCommand(String name, String description, boolean privileged, String... aliases) {
        this(name, 0, 0, description, privileged, aliases);
    }

    public ButlerCommand(String name, int argsMin, int argsMax) {
        this(name, argsMin, argsMax, null, false);
    }

    public ButlerCommand(String name, int argsMin, int argsMax,
                         String description, boolean privileged, String... aliases) {
        this.info = new ButlerCommandInformation(name, aliases, argsMin, argsMax, description, privileged);
    }

    public final ButlerCommandInformation info() {
        return info;
    }

    public abstract void execute(ButlerContext context);

    public final ButlerModule module() {
        return module;
    }

    public final void setModule(ButlerModule module) {
        if (this.module != null) {
            throw new IllegalStateException("The module has already been defined.");
        }
        this.module = module;
    }
}
