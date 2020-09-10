package com.niklasarndt.matsebutler.modules.core.command;

import com.niklasarndt.matsebutler.modules.ButlerCommand;
import com.niklasarndt.matsebutler.modules.ButlerContext;
import com.niklasarndt.matsebutler.scheduler.ScheduleManager;
import com.niklasarndt.matsebutler.scheduler.ScheduledTask;
import com.niklasarndt.matsebutler.util.ButlerUtils;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Niklas on 2020/08/01.
 */
public class RemindCommand extends ButlerCommand {

    public RemindCommand() {
        super("remind", 0, Integer.MAX_VALUE,
                "Be reminded in a certain time. Type \"remind\" to receive a list" +
                        "of pending reminders. Syntax: \n" +
                        "remind clear - Deletes all reminders.\n" +
                        "remind cancel <id> - Removes a specific reminder.\n" +
                        "remind 5d10h35m10s <message>.\n" +
                        "Leave out some values for the duration if " +
                        "these should be zero anyway (e.g. days).",
                "reminder");
    }

    @Override
    public void execute(ButlerContext context) {
        if (context.args().length == 0) {
            displayOverview(context);
        } else if (context.args().length == 1) {
            if (!context.args()[0].equals("clear")) {
                context.resultBuilder().error(
                        "Please specify what the I should remind you of.");
                return;
            }
            cancelAllReminders(context);
        } else if (context.args().length == 2 && context.args()[0].equals("cancel")) {
            cancelReminder(context);

        } else {
            createReminder(context);
        }
    }

    private void cancelReminder(ButlerContext context) {
        int id = ButlerUtils.parseInt(context.args()[1], -1);
        if (id <= 0) {
            context.resultBuilder().error("Please make sure to " +
                    "provide a valid reminder id.");
            return;
        }

        boolean removed = context.instance().getScheduleManager().cancel(id);
        if (removed) context.resultBuilder()
                .success("Reminder **#%02d** has been deleted.", id);
        else context.resultBuilder().notFound("Reminder **#%02d** was not found.", id);
    }

    private void cancelAllReminders(ButlerContext context) {
        List<Integer> taskIds = context.instance().getScheduleManager().getScheduledTasks().stream()
                .filter(i -> i.getName().equals(ScheduleManager.MESSAGE_REMINDER_NAME))
                .map(ScheduledTask::getId)
                .collect(Collectors.toList());

        context.resultBuilder().success("Cancelled %d reminders.",
                context.instance().getScheduleManager().cancel(taskIds));

    }

    private void createReminder(ButlerContext context) {
        long duration = ButlerUtils.parseTimeString(context.args()[0]);

        if (duration <= 0) {
            context.resultBuilder()
                    .error("Invalid duration: %s. Please make sure that the format " +
                            "is correct and you specified a positive duration. " +
                            "Use \"help remind\" for more details.");
            return;
        }

        String message = String.join(" ",
                Arrays.copyOfRange(context.args(), 1, context.args().length));
        context.instance().getScheduleManager().scheduleMessage(message, duration);
        context.resultBuilder().success("You will be reminded in %s.",
                ButlerUtils.prettyPrintTime(duration));
    }

    private void displayOverview(ButlerContext context) {
        StringBuilder builder = new StringBuilder("Pending reminders:\n\n");
        List<ScheduledTask> tasks = context.instance().getScheduleManager()
                .getScheduledTasks().stream()
                .filter(i -> i.getName().equals(ScheduleManager.MESSAGE_REMINDER_NAME))
                .collect(Collectors.toList());
        for (ScheduledTask task : tasks) {
            builder.append(String.format("Reminder **#%s**: In %s.\n", task.getFancyIndex(),
                    task.getFancyTimeUntilExecution()));
        }
        if (tasks.size() == 0) {
            builder.append("There are **no scheduled reminders**.\n");
        }

        List<ScheduledTask> failedTasks = context.instance().getScheduleManager()
                .getFailedTasks(true).stream()
                .filter(i -> i.getName().equals(ScheduleManager.MESSAGE_REMINDER_NAME))
                .collect(Collectors.toList());

        if (failedTasks.size() > 0) {
            builder.append("\n\nFailed reminders:");

            for (ScheduledTask task : tasks) {
                builder.append(String.format("Reminder **#%s**: %s ago.\n", task.getFancyIndex(),
                        task.getTimeSinceExecution()));
            }
        }
        context.resultBuilder().success(builder.toString().trim());
    }
}
