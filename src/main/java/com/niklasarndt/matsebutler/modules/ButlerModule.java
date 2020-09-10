package com.niklasarndt.matsebutler.modules;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Niklas on 2020/07/25.
 */
public abstract class ButlerModule {

    private static final Logger logger = LoggerFactory.getLogger(ButlerModule.class);

    private final ButlerModuleInformation info;
    private final List<ButlerCommand> commands = new ArrayList<>();

    public ButlerModule(String emoji, String name, String displayName,
                        String description, String version) {
        this.info = new ButlerModuleInformation(emoji, name, displayName, description, version);
    }

    public void onStartup() {
        loadCommandsFromDefaultPackage(this);
    }

    public void onShutdown() {
    }

    public final void loadCommands(ButlerCommand... commands) {
        this.commands.addAll(Arrays.asList(commands));
    }

    public final void loadCommandsFromDefaultPackage(ButlerModule instance) {
        loadCommandsFromPackage(instance.getClass().getPackageName() + ".command");
    }

    public final void loadCommandsFromPackage(String packagePath) {
        final Set<Class<? extends ButlerCommand>> classes = new Reflections(packagePath)
                .getSubTypesOf(ButlerCommand.class);

        classes.forEach(item -> {
            try {
                final ButlerCommand cmd = item.getDeclaredConstructor().newInstance();
                cmd.setModule(this);
                this.commands.add(cmd);
            } catch (Exception e) {
                logger.error("Unable to register module \"{}\"", item.getSimpleName(), e);
            }
        });
    }

    public final List<ButlerCommand> getCommands() {
        return commands;
    }

    public final int getCommandCount() {
        return commands.size();
    }

    public final ButlerModuleInformation info() {
        return info;
    }

    public final Optional<ButlerCommand> getCommand(String name) {
        return getCommands().stream().filter(
                cmd -> cmd.info().getName().equals(name) || cmd.info().hasAlias(name)).findFirst();
    }
}
