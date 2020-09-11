package com.niklasarndt.matsebutler.modules.core.command;

import com.niklasarndt.matsebutler.modules.ButlerCommand;
import com.niklasarndt.matsebutler.modules.ButlerContext;
import com.niklasarndt.matsebutler.modules.ButlerModule;
import com.niklasarndt.matsebutler.modules.ButlerModuleInformation;
import com.niklasarndt.matsebutler.util.ButlerUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.List;

/**
 * Created by Niklas on 2020/07/26.
 */
public class ModulesCommand extends ButlerCommand {

    public ModulesCommand() {
        super("modules", 0, 1, "Provides a list of all modules.",
                false, "module");
    }

    @Override
    public void execute(ButlerContext context) {
        if (context.args().length == 0) {
            buildOverview(context);
        } else {
            String moduleName = context.args()[0];

            context.instance().getModuleManager().getModule(moduleName)
                    .ifPresentOrElse(module -> buildModuleOverview(context, module),
                            () -> context.resultBuilder()
                                    .notFound("Could not find module with name `%s`.",
                                            moduleName));
        }
    }

    private void buildModuleOverview(ButlerContext context, ButlerModule module) {
        ButlerModuleInformation info = module.info();
        EmbedBuilder embed = context.resultBuilder().useEmbed();
        embed.setTitle(info.generateTitle());

        embed.addField("Command(s)", module.getCommandCount() + "", true);
        embed.addField("Version", info.getVersion(), true);
        embed.addBlankField(false);

        module.getCommands().forEach(cmd ->
                embed.addField(ButlerUtils.buildEmbedCommandInfo(cmd.info())));
    }

    private void buildOverview(ButlerContext context) {
        List<ButlerModule> modules = context.instance().getModuleManager().getModules();
        EmbedBuilder embed = context.resultBuilder().useEmbed();
        embed.setFooter(modules.size() + " modules loaded");

        for (int i = 0; i < modules.size(); i++) {
            ButlerModule module = modules.get(i);
            ButlerModuleInformation info = module.info();
            embed.addField(info.generateTitle(),
                    info.getDescription(), false);
            embed.addField("Command(s)", module.getCommandCount() + "", true);
            embed.addField("Version", info.getVersion(), true);
            if (i != modules.size() - 1) embed.addBlankField(false);
        }
    }
}
