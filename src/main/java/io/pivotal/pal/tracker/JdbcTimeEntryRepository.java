package io.pivotal.pal.tracker;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class JdbcTimeEntryRepository implements TimeEntryRepository {


    private JdbcTemplate template;

    public  JdbcTimeEntryRepository(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        this.template.update(connection -> {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("INSERT INTO time_entries (project_id, user_id, date, hours) VALUES (?, ?, ?, ?)", RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, timeEntry.getProjectId());
            preparedStatement.setLong(2, timeEntry.getUserId());
            preparedStatement.setDate(3, Date.valueOf(timeEntry.getDate()));
            preparedStatement.setInt(4, timeEntry.getHours());

            return preparedStatement;
        }, keyHolder);

        return find(keyHolder.getKey().longValue());
    }

    @Override
    public TimeEntry find(long timeEntryId) {
        try {
            Map<String, Object> foundEntry = template.queryForMap("SELECT * FROM time_entries WHERE id = ?", timeEntryId);

            TimeEntry timeEntry = generateTimeEntry(foundEntry);

            return timeEntry;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private TimeEntry generateTimeEntry(Map<String, Object> foundEntry) {
        TimeEntry timeEntry = new TimeEntry();
        timeEntry.setId((Long) foundEntry.get("id"));
        timeEntry.setProjectId((Long) foundEntry.get("project_id"));
        timeEntry.setUserId((Long) foundEntry.get("user_id"));
        timeEntry.setDate(((Date)foundEntry.get("date")).toLocalDate());
        timeEntry.setHours((Integer) foundEntry.get("hours"));
        return timeEntry;
    }

    @Override
    public List<TimeEntry> list() {
        List<TimeEntry> timeEntryList = new ArrayList<>();

        List<Map<String, Object>> resultsList = this.template.queryForList("SELECT * FROM time_entries");

        for (Map<String, Object> rowMap: resultsList) {
            timeEntryList.add(generateTimeEntry(rowMap));
        }

        return timeEntryList;
    }

    @Override
    public TimeEntry update(long timeEntryId, TimeEntry timeEntry) {
        this.template.update(connection -> {
           PreparedStatement preparedStatement = connection
                   .prepareStatement("UPDATE time_entries SET project_id = ?, user_id = ?, date = ?,  hours = ? WHERE id = ?");
            preparedStatement.setLong(1, timeEntry.getProjectId());
            preparedStatement.setLong(2, timeEntry.getUserId());
            preparedStatement.setDate(3, Date.valueOf(timeEntry.getDate()));
            preparedStatement.setInt(4, timeEntry.getHours());
            preparedStatement.setLong(5, timeEntryId);

           return preparedStatement;
        });

        return find(timeEntryId);
    }

    @Override
    public void delete(long timeEntryId) {
        this.template.update("DELETE FROM time_entries WHERE id = ?", timeEntryId);
    }
}
