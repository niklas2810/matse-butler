package com.niklasarndt.matsebutler;

import com.niklasarndt.matsebutler.modules.ButlerCommand;
import com.niklasarndt.matsebutler.modules.ButlerContext;
import com.niklasarndt.matsebutler.modules.ButlerModule;
import com.niklasarndt.matsebutler.util.ResultBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * Created by Niklas on 2020/07/25.
 */
public class ModuleManager {

    private static final Logger logger = LoggerFactory.getLogger(ModuleManager.class);

    private final Butler instance;
    private final List<ButlerModule> modules = new ArrayList<>();
    private String mostRecentMessage;

    public ModuleManager(Butler instance) {
        this.instance = instance;
    }

    public void loadAll() {
        unloadAll();
        final Set<Class<? extends ButlerModule>> classes =
                new Reflections("com.niklasarndt.matsebutler.modules")
                        .getSubTypesOf(ButlerModule.class);

        classes.forEach(item -> {
            try {
                final ButlerModule module = item.getDeclaredConstructor().newInstance();
                registerModule(module);
            } catch (Exception e) {
                logger.error("Unable to register module \"{}\"", item.getSimpleName(), e);
            }
        });
        logger.info("{} modules loaded.", modules.size());
        logger.info("{} commands registered.", getCommandCount());
    }

    public void unloadAll() {
        modules.forEach(mod -> unregisterModule(mod, false));
        modules.clear();
        logger.info("Unloaded all modules.");
    }

    public void registerModule(ButlerModule module) {
        if (hasModule(module.info().getName())) {
            throw new IllegalStateException(String.format("There's already a module " +
                    "claiming the name \"%s\".", module.info().getName()));
        }
        modules.add(module);
        module.onStartup();
    }

    public void unregisterModule(String name) {
        unregisterModule(modules.stream()
                .filter(item -> item.info().getName().equals(name)).findFirst().orElse(null));
    }

    public void unregisterModule(ButlerModule module) {
        unregisterModule(module, true);
    }

    public void unregisterModule(ButlerModule module, boolean removeFromList) {
        if (module == null || !hasModule(module)) return;

        module.onShutdown();
        if (removeFromList) modules.remove(module);
    }

    private boolean hasModule(String name) {
        return modules.stream().anyMatch(item -> item.info().getName().equals(name));
    }

    private boolean hasModule(ButlerModule module) {
        return modules.contains(module);
    }

    public ResultBuilder execute(Message message) {
        return execute(message.getContentRaw(), message);
    }

    public ResultBuilder execute(String content, Message origin) {
        String[] parts = content.split(" ");
        String name = parts[0].toLowerCase();
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);


        Optional<ButlerCommand> command = findCommand(name);

        if (command.isPresent()) {
            ButlerCommand cmd = command.get();

            ButlerContext context = new ButlerContext(instance, origin, name, args,
                    new ResultBuilder(command.get().module().info()));

            if (args.length < cmd.info().getArgsMin() || args.length > cmd.info().getArgsMax()) {

                context.resultBuilder().invalidArgsLength(
                        cmd.info().getArgsMin(), cmd.info().getArgsMax(), args.length);
            } else if (origin != null
                    && cmd.info().requiresPrivileges()
                    && !instance.isAdmin(origin.getAuthor().getIdLong())) {

                context.resultBuilder().unauthorized("You are not permitted " +
                        "to execute this command.");
            } else {
                try {
                    cmd.execute(context);
                } catch (Exception e) {
                    logger.error("Could not execute command '{}' (module: {})",
                            name, cmd.module().info().getName(), e);
                    context.resultBuilder().error(e);
                }
            }

            return context.resultBuilder();
        } else return ResultBuilder.NOT_FOUND;
    }

    public List<ButlerModule> getModules() {
        return Collections.unmodifiableList(modules);
    }

    public Optional<ButlerCommand> findCommand(String name) {
        return modules.stream()
                .map(m -> m.getCommand(name)).flatMap(Optional::stream)
                .findFirst();
    }

    private int getCommandCount() {
        return modules.stream().mapToInt(ButlerModule::getCommandCount).sum();
    }

    public Optional<ButlerModule> getModule(String name) {
        return modules.stream().filter(m -> m.info().getName().equals(name)).findFirst();
    }

    public String getMostRecentMessage() {
        return mostRecentMessage;
    }
}
