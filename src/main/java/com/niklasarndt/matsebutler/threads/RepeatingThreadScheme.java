package com.niklasarndt.matsebutler.threads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Niklas on 2020/09/13.
 */
public abstract class RepeatingThreadScheme implements Runnable {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private final AtomicLong lastExecution = new AtomicLong();
    private final AtomicLong lastThreadStartup = new AtomicLong();
    private final AtomicLong initialDelay = new AtomicLong(10000);
    private final AtomicLong sleepTime = new AtomicLong(10000);

    private final String name;
    private Thread thread;


    public RepeatingThreadScheme(String name) {
        this.name = name;
    }

    public final long lastExecutionTimestamp() {
        return lastExecution.get();
    }

    public final long getSleepTime() {
        return sleepTime.get();
    }

    public final void setSleepTime(long durationInMs) {
        sleepTime.set(durationInMs);
    }

    public final long getInitialDelay() {
        return initialDelay.get();
    }

    public final void setInitialDelay(long durationInMs) {
        initialDelay.set(durationInMs);
    }

    public final boolean isRunning() {
        return thread != null && thread.getState() != Thread.State.TERMINATED;
    }

    public void start() {
        thread = new Thread(null, this::threadScheme,
                String.format("%s-%02d", name, (int) (Math.random() * 1000)),
                0);
        thread.start();
    }

    public void stop() {
        lastThreadStartup.set(0);
        try {
            thread.interrupt();
        } catch (Exception exception) {
            logger.error("Can not interrupt thread {}", name, exception);
            exception.printStackTrace();
        }
    }

    private void threadScheme() {
        long startup = System.currentTimeMillis();
        lastThreadStartup.set(startup);

        logger.info("Thread startup in {}ms!", initialDelay.get());
        try {
            Thread.sleep(initialDelay.get());
        } catch (InterruptedException e) {
            logger.warn("Thread has been interrupted while waiting for first iteration!");
        }

        while (true) {
            if (lastThreadStartup.get() != startup) { //Another thread has been started
                logger.info("Shutting down duplicate scheduler thread");
                break;
            }

            logger.debug("Starting thread iteration");
            lastExecution.set(System.currentTimeMillis());

            try {
                this.run();
            } catch (Exception ex) {
                logger.error("Could not run thread iteration", ex);
            }

            try {
                Thread.sleep(sleepTime.get());
            } catch (InterruptedException e) {
                logger.warn("Thread has been interrupted!");
            }
        }
    }
}
