package com.niklasarndt.matsebutler;

import com.niklasarndt.matsebutler.config.Configuration;
import com.niklasarndt.matsebutler.config.ConfigurationManager;
import com.niklasarndt.matsebutler.listener.ApiListener;
import com.niklasarndt.matsebutler.listener.MessageListener;
import com.niklasarndt.matsebutler.listener.ReactionListener;
import com.niklasarndt.matsebutler.scheduler.ScheduleManager;
import com.niklasarndt.matsebutler.util.ButlerUtils;
import com.niklasarndt.matsebutler.util.ExecutionFlags;
import io.sentry.Sentry;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.security.auth.login.LoginException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by Niklas on 2020/07/24.
 */
public class Butler {

    private static final Logger logger = LoggerFactory.getLogger(Butler.class);
    private final long startupTimestamp = System.currentTimeMillis();
    private final List<ExecutionFlags> flags;
    private JDA jda;
    private ModuleManager moduleManager;
    private ScheduleManager scheduleManager;
    private final ConfigurationManager configManager;

    protected Butler() throws LoginException {
        this(ExecutionFlags.NONE);
    }

    protected Butler(String... flags) throws LoginException {
        this(Arrays.stream(flags)
                .map(el -> ButlerUtils.parseInt(el, 0))
                .toArray(Integer[]::new));
    }

    protected Butler(Integer... flags) throws LoginException {
        this(ExecutionFlags.getFlagsById(flags));
    }

    protected Butler(ExecutionFlags... flags) throws LoginException {
        logger.info("Startup is in progress");
        this.flags = List.of(flags);
        this.configManager = new ConfigurationManager();

        if (configManager.getConfig().getSentryKey() != null) {
            logger.info("Using sentry key specified in the configuration file");

            Sentry.init(configManager.getConfig().getSentryKey());
        }

        if (hasFlag(ExecutionFlags.NO_API_CONNECTION)) {
            logger.warn("NO_API_CONNECTION: App will not be kept alive by daemon.");
        } else {
            jda = setUpJda();
            logger.info("JDA has been set up!");
        }

        if (!hasFlag(ExecutionFlags.NO_MODULE_MANAGER)) {
            moduleManager = new ModuleManager(this);
            moduleManager.loadAll();
            logger.info("Module manager has been set up!");
        }

        if (!hasFlag(ExecutionFlags.NO_SCHEDULE_MANAGER)) {
            scheduleManager = new ScheduleManager(this);
            logger.info("Schedule manager has been set up!");
        }

        logger.info("SUCCESS: SETUP COMPLETE");
        logger.info("EXEC FLAGS: {}", ExecutionFlags.prettyPrint(
                this.flags.toArray(new ExecutionFlags[0])));
    }

    public static void main(String[] args) {
        if (System.getenv("SENTRY_DSN") != null || System.getProperty("sentry.dsn") != null) {

            Sentry.init();
            logger.info("Has Sentry been initialized correctly? {}", Sentry.isInitialized());
        }

        try {
            new Butler(buildEnvironmentFlags(args));
        } catch (Exception e) {
            logger.error("Encountered uncaught exception", e);
            logger.error("This fatal exception will lead to an application shutdown.");
            System.exit(1);
        }
    }

    private static String[] buildEnvironmentFlags(String[] args) {
        String[] envs = Optional.ofNullable(System.getenv("EXECUTION_FLAGS"))
                .orElse("").split(",");
        boolean oneOnly = envs.length == 0 || args.length == 0;

        String[] combined;
        if (oneOnly) combined = envs.length != 0 ? envs : args;
        else combined = new String[envs.length + args.length];

        if (!oneOnly) {
            System.arraycopy(envs, 0, combined, 0, envs.length);
            System.arraycopy(args, 0, combined, envs.length, args.length);
        }
        return combined;
    }

    /**
     * Sets up the JDA instance.
     *
     * @return The completely initialized JDA instance.
     * @throws LoginException Will cause a shutdown + sentry log.
     */
    private JDA setUpJda() throws LoginException {
        final JDABuilder builder = JDABuilder.create(getToken(),
                GatewayIntent.DIRECT_MESSAGES, GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS);


        builder.addEventListeners(new ApiListener(this),
                new MessageListener(this), new ReactionListener(this));
        builder.disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE,
                CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS);
        return builder.build();
    }

    public JDA getJda() {
        return jda;
    }

    public long getStartupTimestamp() {
        return startupTimestamp;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public ScheduleManager getScheduleManager() {
        return scheduleManager;
    }

    public ConfigurationManager getConfigManager() {
        return configManager;
    }

    public Configuration getConfig() {
        return configManager.getConfig();
    }

    public long getGuild() {
        return getConfig().getGuild();
    }

    public String getToken() {
        return getConfig().getToken();
    }

    public boolean isAdmin(long id) {
        return getConfig().isAdmin(id);
    }

    public List<ExecutionFlags> getFlags() {
        return flags;
    }

    public boolean hasFlag(int flagId) {
        return hasFlag(ExecutionFlags.getFlagById(flagId));
    }

    public boolean hasFlag(ExecutionFlags flag) {
        return flags.contains(flag);
    }

    public void shutdown(int exitCode) {
        new Thread(null, () -> {
            logger.info("Shutdown in 5 seconds!");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info("Initiating shutdown...");

            moduleManager.unloadAll();
            jda.shutdown();
            logger.info("The connection to the Discord API was shut down. Goodbye!");
            System.exit(exitCode);
        }, "terminator").start();
    }
}
