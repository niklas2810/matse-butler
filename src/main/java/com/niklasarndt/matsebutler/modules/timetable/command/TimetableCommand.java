package com.niklasarndt.matsebutler.modules.timetable.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.niklasarndt.matsebutler.modules.ButlerCommand;
import com.niklasarndt.matsebutler.modules.ButlerContext;
import com.niklasarndt.matsebutler.modules.timetable.DateUtils;
import com.niklasarndt.matsebutler.modules.timetable.model.TimetableEntry;

import java.io.IOException;
import java.net.URL;

public class TimetableCommand extends ButlerCommand {

    public TimetableCommand() {
        super("timetable", "Updates the timetable.");
    }

    @Override
    public void execute(ButlerContext context) {
        ObjectMapper mapper = new ObjectMapper();

        TimetableEntry[] entries = new TimetableEntry[0];
        try {
            entries = mapper.readValue(new URL(DateUtils.getDayRequest()), TimetableEntry[].class);
        } catch (IOException e) {
            logger.error("Can not parse timetable url", e);
        }


        StringBuilder b = new StringBuilder();
        for (TimetableEntry entry : entries) {
            b.append(entry.getTitle()).append(": ").append(entry.getStartParsed()).append("\n");
        }

        context.resultBuilder().success(b.toString());
    }
}
