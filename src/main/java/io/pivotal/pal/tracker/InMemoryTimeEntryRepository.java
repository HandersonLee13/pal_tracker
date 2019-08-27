package io.pivotal.pal.tracker;

import org.springframework.context.annotation.Bean;

import java.sql.Time;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {

    private Map<Long, TimeEntry> timeEntryHashMap = new ConcurrentHashMap<>();
    private AtomicLong currentId = new AtomicLong(1);

    public TimeEntry create(TimeEntry timeEntry) {
        timeEntry.setId(currentId.getAndIncrement());
        timeEntryHashMap.put(timeEntry.getId(), timeEntry);

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
