package com.niklasarndt.matsebutler.modules.timetable.command;

import com.niklasarndt.matsebutler.modules.ButlerCommand;
import com.niklasarndt.matsebutler.modules.ButlerContext;
import com.niklasarndt.matsebutler.modules.timetable.TimetableModule;
import com.niklasarndt.matsebutler.util.ButlerUtils;

public class TimetableCommand extends ButlerCommand {


    public TimetableCommand() {
        super("timetable", 0, 2, "Updates the timetable.",
                false);
    }

    @Override
    public void execute(ButlerContext context) {
        if (context.args().length == 0
                || context.args()[0].equalsIgnoreCase("status")) {
            long last = TimetableModule.instance().lastExecutionTimestamp();

            if (last == 0) {
                context.resultBuilder().success("The auto-updater did not run yet.");
            } else {
                context.resultBuilder().success("The auto-updater ran %s ago.",
                        ButlerUtils.prettyPrintTime(System.currentTimeMillis() - last));
            }

            return;
        } else if (context.args()[0].equalsIgnoreCase("start")) {
            if (!context.instance().isAdmin(context.message().getAuthor().getIdLong())) {
                context.resultBuilder().denyAccess();
                return;
            }

            TimetableModule.instance().start();
            context.resultBuilder().success("The auto-updater has just been started.");
            return;
        } else if (context.args()[0].equalsIgnoreCase("stop")) {
            if (!context.instance().isAdmin(context.message().getAuthor().getIdLong())) {
                context.resultBuilder().denyAccess();
                return;
            }
            TimetableModule.instance().stop();
            context.resultBuilder().success("The auto-updater has been stopped.");
            return;
        } else if (context.args()[0].equalsIgnoreCase("update")) {
            if (!context.instance().isAdmin(context.message().getAuthor().getIdLong())) {
                context.resultBuilder().denyAccess();
                return;
            }

            if (context.args().length == 1 ||
                    context.args()[1].equalsIgnoreCase("all")) {
                TimetableModule.instance().updateChannels(context.message().getGuild(),
                        context.resultBuilder(), true, true, true);
                return;
            } else if (context.args()[1].equalsIgnoreCase("today")) {

                TimetableModule.instance().updateChannels(context.message().getGuild(),
                        context.resultBuilder(), true, true, true);
                return;
            } else if (context.args()[1].equalsIgnoreCase("weekly")) {
                TimetableModule.instance().updateChannels(context.message().getGuild(),
                        context.resultBuilder(), true, true, true);
                return;
            }
        }


        context.resultBuilder()
                .notFound("Please use `timetable (status|start|stop)` or " +
                        "`timetable update (all|today|weekly)` (Admins only!)");
    }
}
