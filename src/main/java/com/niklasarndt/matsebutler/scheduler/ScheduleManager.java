package com.niklasarndt.matsebutler.scheduler;

import com.niklasarndt.matsebutler.Butler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by Niklas on 2020/08/01.
 * <p>
 * WARNING: Automating a task without explicit user consent is considered API abuse.
 * A message should only be scheduled if the user to you to do so. (e.g. via the remind command)
 * <p>
 * Quote: "You may not post messages, trigger notifications, or play audio on behalf of a Discord
 * user except in response to such Discord user expressly opting-in to each instance of such action"
 * <p>
 * https://discord.com/developers/docs/policy (1st August 2020)
 */
public class ScheduleManager {

    public static final String MESSAGE_REMINDER_NAME = "Scheduled Message";

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ScheduledExecutorService executorService = Executors
            .newScheduledThreadPool(1, runnable -> new Thread(null, runnable,
                    "ScheduleThread-" + System.currentTimeMillis()));
    private final List<ScheduledTask> failedTasks = new ArrayList<>();
    private final AtomicInteger index = new AtomicInteger();
    private List<ScheduledTask> tasks = new ArrayList<>();

    public ScheduleManager(Butler butler) {
    }

    private ScheduledTask schedule(ScheduledTask task) {
        tasks.add(task);
        executorService.schedule(() -> {
            if (!hasTask(task.getId())) {
                logger.debug("Skipping cancelled task with id {}.", task.getId());
                return;
            }
            try {
                task.execute();
            } catch (Exception e) {
                logger.error("Failed to run scheduled task", e);
                failedTasks.add(task);
            }
        }, task.getWaitTimeInMs(), TimeUnit.MILLISECONDS);
        return task;
    }

    public ScheduledTask schedule(String name, Runnable runnable, long waitTimeInMs) {
        return schedule(new ScheduledTask(index.incrementAndGet(), name, runnable, waitTimeInMs));
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            logger.warn("Executor service did not shut down automatically, forcing shutdown", e);
            executorService.shutdownNow();
        }
    }

    public boolean isShutdown() {
        return executorService.isShutdown();
    }

    public List<ScheduledTask> getFailedTasks(boolean clearAfterwards) {
        List<ScheduledTask> result = Collections.unmodifiableList(
                clearAfterwards ? List.copyOf(failedTasks) : failedTasks);
        if (clearAfterwards) failedTasks.clear();
        return result;
    }

    public List<ScheduledTask> getScheduledTasks() {
        tasks = tasks.stream().filter(item -> !item.shouldBeExecuted())
                .collect(Collectors.toList());
        return Collections.unmodifiableList(tasks);
    }

    public boolean hasTask(int id) {
        return tasks.stream().anyMatch(i -> i.getId() == id);
    }

    public boolean cancel(int id) {
        Optional<ScheduledTask> task = tasks.stream().filter(i -> i.getId() == id).findFirst();

        task.ifPresent(i -> tasks.remove(i));

        return task.isPresent();
    }

    public int cancel(int... ids) {
        AtomicInteger result = new AtomicInteger();
        for (int id : ids) {
            if (cancel(id)) result.getAndIncrement();
        }
        return result.get();
    }

    public int cancel(List<Integer> taskIds) {
        AtomicInteger result = new AtomicInteger();
        taskIds.forEach(i -> {
            if (cancel(i)) result.getAndIncrement();
        });
        return result.get();
    }
}
