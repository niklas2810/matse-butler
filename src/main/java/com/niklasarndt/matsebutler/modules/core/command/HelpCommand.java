package com.niklasarndt.matsebutler.modules.core.command;

import com.niklasarndt.matsebutler.modules.ButlerCommand;
import com.niklasarndt.matsebutler.modules.ButlerCommandInformation;
import com.niklasarndt.matsebutler.modules.ButlerContext;
import com.niklasarndt.matsebutler.modules.ButlerModule;
import com.niklasarndt.matsebutler.util.ButlerUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.List;
import java.util.Optional;

/**
 * Created by Niklas on 2020/07/26.
 */
public class HelpCommand extends ButlerCommand {

    public HelpCommand() {
        super("help", 0, 1,
                "Provides a command overview. Use \"help help\" for more details.",
                false, "commands", "cmds");
    }

    @Override
    public void execute(ButlerContext context) {
        if (context.args().length == 0) {
            buildOverview(context);
        } else {
            buildDetails(context);
        }
    }

    private void buildDetails(ButlerContext context) {
        Optional<ButlerCommand> command = context.instance()
                .getModuleManager().findCommand(context.args()[0]);

        if (command.isEmpty()) {
            context.resultBuilder().notFound("Could not find a command matching `%s`.",
                    context.args()[0]);
            return;
        }

        EmbedBuilder embed = context.resultBuilder().useEmbed();
        buildEmbedInfo(embed, command.get());
    }

    private void buildEmbedInfo(EmbedBuilder embed, ButlerCommand command) {
        ButlerCommandInformation info = command.info();

        embed.setTitle("Overview for command `" + info.getName() + "`");
        embed.addField("Module", String.format("%s %s", command.module().info().getEmoji(),
                command.module().info().getDisplayName()), false);
        embed.addField("Description", info.getDescription(), false);

        embed.addField("Aliases", info.getAliases().length == 0
                ? "None" : String.join(", ", info.getAliases()), true);

        String args;
        if (info.getArgsMin() != info.getArgsMax()) { //Parameter range
            args = info.getArgsMin() + "-" + info.getArgsMax();
        } else { //Fixed parameter count
            args = info.getArgsMin() == 0 ? "None" : info.getArgsMin() + "";
        }
        embed.addField("Parameters", args, true);
    }

    private void buildOverview(ButlerContext context) {
        List<ButlerModule> modules = context.instance().getModuleManager().getModules();

        StringBuilder builder = new StringBuilder();
        modules.forEach(module -> {
            builder.append(String.format("%s\n\n",
                    module.info().generateTitle()));

            if (module.getCommandCount() == 0) {
                builder.append("This module does not contain any commands.\n");
            } else module.getCommands().forEach(cmd -> {
                ButlerCommandInformation cmdInfo = cmd.info();
                builder.append(ButlerUtils.buildShortCommandInfo(cmdInfo)).append("\n");
            });

            builder.append("\n");
        });
        context.resultBuilder().success(builder.toString().trim());
    }
}
