package com.niklasarndt.matsebutler.modules.fun.command;

import com.niklasarndt.matsebutler.modules.ButlerCommand;
import com.niklasarndt.matsebutler.modules.ButlerContext;
import java.util.Random;

/**
 * Created by Niklas on 2020/07/26.
 */
public class CoinCommand extends ButlerCommand {

    public CoinCommand() {
        super("coin", "Flips a coin (heads, tails).", "flip");
    }

    @Override
    public void execute(ButlerContext context) {
        context.resultBuilder().success("It's %s!",
                new Random().nextBoolean() ? "heads" : "tails");
    }
}
