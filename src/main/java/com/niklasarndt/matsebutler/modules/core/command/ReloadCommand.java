package com.niklasarndt.matsebutler.modules.core.command;

import com.niklasarndt.matsebutler.modules.ButlerCommand;
import com.niklasarndt.matsebutler.modules.ButlerContext;

/**
 * Created by Niklas on 2020/09/12.
 */
public class ReloadCommand extends ButlerCommand {

    public ReloadCommand() {
        super("reload", "Reloads the configuration", true);
    }

    @Override
    public void execute(ButlerContext context) {
        context.instance().getConfig().reload();
    }
}
