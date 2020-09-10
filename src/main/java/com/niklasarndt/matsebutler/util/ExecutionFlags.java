package com.niklasarndt.matsebutler.util;

/**
 * Created by Niklas on 2020/07/27.
 */
public enum ExecutionFlags {

    NONE, NO_API_CONNECTION, NO_MODULE_MANAGER, NO_SCHEDULE_MANAGER;

    public static ExecutionFlags getFlagById(int id) {
        return id < 0 || id > ExecutionFlags.values().length ? null : ExecutionFlags.values()[id];
    }

    public static ExecutionFlags[] getFlagsById(Integer[] flags) {
        ExecutionFlags[] result = new ExecutionFlags[flags.length];

        for (int i = 0; i < flags.length; i++) {
            result[i] = getFlagById(flags[i]);
        }
        return result;
    }

    public static String prettyPrint(Integer[] flags) {
        return prettyPrint(getFlagsById(flags));
    }

    public static String prettyPrint(ExecutionFlags[] flags) {
        if (flags.length == 0) return "None"; //None = Result, NONE = ExecutionFlags.NONE!
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < flags.length; i++) {
            builder.append(flags[i].name());
            if (i != flags.length - 1) builder.append(", ");
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return name();
    }
}
