package com.niklasarndt.matsebutler.modules.fun.command;

import com.niklasarndt.matsebutler.modules.ButlerCommand;
import com.niklasarndt.matsebutler.modules.ButlerContext;
import com.niklasarndt.matsebutler.util.ButlerUtils;
import java.security.SecureRandom;

/**
 * Created by Niklas on 2020/07/26.
 */
public class DiceCommand extends ButlerCommand {

    public DiceCommand() {
        super("dice", 0, 1,
                "Rolls a coin. 1-6 by default, use the first parameter to specify the face count.",
                "roll");
    }

    @Override
    public void execute(ButlerContext context) {

        int faceCount = context.args().length == 0 ? 6 :
                ButlerUtils.parseInt(context.args()[0], -1);

        if (faceCount <= 0) {
            context.resultBuilder().error("Please make sure that you entered a positive number.");
            return;
        }

        int result = new SecureRandom().nextInt(faceCount) + 1;
        context.resultBuilder().success("Result: **%d** (1-%d).", result, faceCount);
    }
}
