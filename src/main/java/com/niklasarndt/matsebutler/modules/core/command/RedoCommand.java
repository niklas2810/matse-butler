package com.niklasarndt.matsebutler.modules.core.command;

import com.niklasarndt.matsebutler.modules.ButlerCommand;
import com.niklasarndt.matsebutler.modules.ButlerContext;

/**
 * Created by Niklas on 2020/08/02.
 */
public class RedoCommand extends ButlerCommand {

    public RedoCommand() {
        super("redo", "Executes the most recent command again.", "r");
    }

    @Override
    public void execute(ButlerContext context) {
        if (context.instance().getModuleManager().getMostRecentMessage() == null) {
            context.resultBuilder().error("No command has been executed yet.");
            return;
        }
        context.resultBuilder().output(context.instance().getModuleManager().redo());
    }
}
