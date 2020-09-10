package com.niklasarndt.matsebutler.modules.fake.command;

import com.niklasarndt.matsebutler.modules.ButlerCommand;
import com.niklasarndt.matsebutler.modules.ButlerContext;

/**
 * Created by Niklas on 2020/07/27.
 */
public class FakeminCommand extends ButlerCommand {

    public FakeminCommand() {
        super("fakemin", 1, 2);
    }

    @Override
    public void execute(ButlerContext context) {
        context.resultBuilder().success("test");
    }
}
