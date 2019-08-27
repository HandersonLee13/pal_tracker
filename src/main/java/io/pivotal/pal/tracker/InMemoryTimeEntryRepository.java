package io.pivotal.pal.tracker;

import org.springframework.context.annotation.Bean;

import java.sql.Time;
import java.util.*;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {

    private HashMap<Long, TimeEntry> timeEntryHashMap;
    private long currentId = 1L;

    public InMemoryTimeEntryRepository() {
        timeEntryHashMap = new HashMap<Long, TimeEntry>();
    }

    public TimeEntry create(TimeEntry timeEntry) {
        timeEntry.setId(currentId);
        timeEntryHashMap.put(currentId, timeEntry);

        currentId++;

        return timeEntry;
    }

    public TimeEntry find(long id) {
        return timeEntryHashMap.get(id);
    }

    public List<TimeEntry> list() {
        return new ArrayList<>(timeEntryHashMap.values());
    }

    public TimeEntry update(long id, TimeEntry timeEntry) {
        if(timeEntryHashMap.get(id) == null) {
            return null;
        }

        timeEntry.setId(id);
        timeEntryHashMap.put(id, timeEntry);

        return timeEntry;
    }

    public void delete(long id) {
        timeEntryHashMap.remove(id);
    }
}
