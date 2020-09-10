package com.niklasarndt.matsebutler.scheduler;

import com.niklasarndt.matsebutler.util.ButlerUtils;

/**
 * Created by Niklas on 2020/08/02.
 */
public class ScheduledTask {

    private final long start = System.currentTimeMillis();

    private final int id;
    private final String name;
    private final Runnable runnable;
    private final long waitTimeInMs;
    private boolean executed = false;
    private long executionTimestamp;
    private boolean success = true;


    protected ScheduledTask(int id, Runnable runnable, long waitTimeInMs) {
        this(id, "Unnamed Task", runnable, waitTimeInMs);
    }

    protected ScheduledTask(int id, String name, Runnable runnable, long waitTimeInMs) {
        this.id = id;
        this.name = name;
        this.runnable = runnable;
        this.waitTimeInMs = waitTimeInMs;
    }

    public void execute() {
        executed = true;
        executionTimestamp = System.currentTimeMillis();
        try {
            runnable.run();
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }

    public int getId() {
        return id;
    }

    public String getFancyIndex() {
        return String.format("%02d", getId());
    }

    public String getName() {
        return name;
    }

    public long getWaitTimeInMs() {
        return waitTimeInMs;
    }

    public boolean wasExecuted() {
        return executed;
    }

    public boolean wasSuccessfullyExecuted() {
        return wasExecuted() && success;
    }

    public boolean hasFailed() {
        return !success;
    }

    public boolean shouldBeExecuted() {
        return getDueTimestamp() < System.currentTimeMillis(); //Should already have been executed
    }

    public long getDueTimestamp() {
        return start + waitTimeInMs;
    }

    public long getExecutionTimestamp() {
        return wasExecuted() ? executionTimestamp : -1;
    }

    public long getTimeUntilExecution() {
        return getDueTimestamp() - System.currentTimeMillis();
    }

    public long getTimeSinceExecution() {
        return wasExecuted() ? System.currentTimeMillis() - executionTimestamp : 0;
    }

    public String getFancyTimeUntilExecution() {
        return wasExecuted() ? "Executed" : ButlerUtils.prettyPrintTime(getTimeUntilExecution());
    }

    public String getFancyTimeSinceExecution() {
        return wasExecuted() ? ButlerUtils.prettyPrintTime(getTimeSinceExecution()) :
                "Not executed yet";
    }
}
