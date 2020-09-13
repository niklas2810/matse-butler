package com.niklasarndt.matsebutler.modules.core.command;

import com.niklasarndt.matsebutler.modules.ButlerCommand;
import com.niklasarndt.matsebutler.modules.ButlerContext;
import com.niklasarndt.matsebutler.util.BuildInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDAInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

/**
 * Created by Niklas on 2020/07/26.
 */
public class VersionCommand extends ButlerCommand {

    public VersionCommand() {
        super("version", 0, 0, "Displays some info about the current build.",
                false, "about", "info");
    }

    @Override
    public void execute(ButlerContext context) {
        EmbedBuilder embed = context.resultBuilder().useEmbed();
        embed.setTitle(String.format("%s v%s", BuildInfo.NAME, BuildInfo.VERSION));
        embed.addField("Build Timestamp", BuildInfo.TIMESTAMP, false);
        embed.addField("Target JDK", BuildInfo.TARGET_JDK, true);
        embed.addField("Repository URL", BuildInfo.URL, true);

        if (!context.instance().isAdmin(context.message().getAuthor().getIdLong())) return;

        MemoryUsage memory = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        String usage = String.format("%.02f / %.02f",
                memory.getUsed() / 1000000., memory.getMax() / 1000000.);

        embed.addBlankField(false);
        embed.addField("Environment Details", "", false);
        embed.addField("Memory Usage (MB)", usage, true);
        embed.addField("OS", System.getProperty("os.name"), true);
        embed.addBlankField(false);
        embed.addField("JDA Version", JDAInfo.VERSION, true);
        embed.addField("Java Version",
                System.getProperty("java.runtime.version").replace("+", "_"),
                true);

    }
}
