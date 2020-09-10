package com.niklasarndt.matsebutler.scheduler;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;


/**
 * Created by Niklas on 2020/08/01.
 */
public class ScheduleManagerTest {

    public static boolean VISITED = false;

    @Test
    public void testScheduleManager() {
        VISITED = false;
        ScheduleManager manager = new ScheduleManager(null);

        ScheduledTask task = manager.schedule("test", () -> {
            ScheduleManagerTest.VISITED = true;
        }, 500);

        assertFalse(task.shouldBeExecuted());
        assertEquals("01 second", task.getFancyTimeUntilExecution());
        assertFalse(task.wasExecuted());
        assertFalse(task.wasSuccessfullyExecuted());
        assertFalse(task.hasFailed());
        assertEquals(1, task.getId());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(manager.getScheduledTasks().isEmpty());
        assertTrue(VISITED);
        assertTrue(task.wasSuccessfullyExecuted());
        assertTrue(task.shouldBeExecuted());

        assertTrue(manager.getFailedTasks(false).isEmpty());
    }

    @Test
    public void testFailingManager() {
        ScheduleManager manager = new ScheduleManager(null);
        ScheduledTask task = manager.schedule("Test", () -> {
            throw new NullPointerException("test exception");
        }, 500);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue(manager.hasTask(1));
        assertTrue(task.wasExecuted());
        assertTrue(task.hasFailed());
        assertFalse(manager.getFailedTasks(false).isEmpty());
        assertFalse(manager.getFailedTasks(false).isEmpty());
        assertEquals("Test", manager.getFailedTasks(false).get(0).getName());
        System.out.println(manager.getFailedTasks(false).get(0).getTimeSinceExecution());
        assertNotEquals(0, manager.getFailedTasks(true)
                .get(0).getTimeSinceExecution());
        assertTrue(manager.getFailedTasks(false).isEmpty());

        manager.shutdown();
        assertTrue(manager.isShutdown());
    }

    @Test
    public void testCancelledTask() {
        VISITED = false;

        ScheduleManager manager = new ScheduleManager(null);

        ScheduledTask task = manager.schedule("test", () -> {
            ScheduleManagerTest.VISITED = true;
        }, 500);

        assertEquals(1, manager.cancel(List.of(task.getId())));
        assertEquals(0, manager.cancel(0, 1, 2));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertFalse(task.wasExecuted());
        assertFalse(VISITED);
    }
}
