package com.niklasarndt.matsebutler.modules.fake.command;

import com.niklasarndt.matsebutler.modules.ButlerCommand;
import com.niklasarndt.matsebutler.modules.ButlerContext;

/**
 * Created by Niklas on 2020/07/27.
 */
public class FakeCommand extends ButlerCommand {

    public FakeCommand() {
        super("fake");
    }

    @Override
    public void execute(ButlerContext context) {
        context.resultBuilder().success("test");
    }
}
