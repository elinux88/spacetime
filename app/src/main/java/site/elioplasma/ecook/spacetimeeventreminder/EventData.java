package site.elioplasma.ecook.spacetimeeventreminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import site.elioplasma.ecook.spacetimeeventreminder.database.EventBaseHelper;
import site.elioplasma.ecook.spacetimeeventreminder.database.EventCursorWrapper;
import site.elioplasma.ecook.spacetimeeventreminder.database.EventDbSchema.EventTable;

/**
 * Created by eli on 2/27/16.
 */
public class EventData {
    private static EventData sEventData;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static EventData get(Context context) {
        if (sEventData == null) {
            sEventData = new EventData(context);
        }
        return sEventData;
    }

    private EventData(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new EventBaseHelper(mContext)
                .getWritableDatabase();

        EventCursorWrapper eventCursor = queryEvents(null, null);
        try {
            if (eventCursor.getCount() == 0) {
                initEvents();
            }
        } finally {
            eventCursor.close();
        }
    }

    public void addEvent(Event e) {
        ContentValues values = getContentValues(e);

        mDatabase.insert(EventTable.NAME, null, values);
    }

    public boolean deleteEvent(UUID id) {
        mDatabase.delete(EventTable.NAME,
                EventTable.Cols.UUID + " = ?",
                new String[]{id.toString()});
        return true;
    }

    public List<Event> getEventsWithReminders() {
        List<Event> events = new ArrayList<>();

        EventCursorWrapper cursor;
        cursor = queryEventsWithInt(EventTable.Cols.REMINDER_ON + " = 1");

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                events.add(cursor.getEvent());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return events;
    }

    public List<Event> getEvents(Context context) {
        List<Event> events = new ArrayList<>();

        EventCursorWrapper cursor;

        boolean filterByReminders = QueryPreferences.getStoredFilterByReminders(context);
        boolean filterByCustom = QueryPreferences.getStoredFilterByCustom(context);

        if (filterByReminders && filterByCustom) {
            cursor = queryEventsWithInt(
                    EventTable.Cols.REMINDER_ON + " = 1 and " + EventTable.Cols.CUSTOM + " = 1");
        } else if (filterByReminders) {
            cursor = queryEventsWithInt(EventTable.Cols.REMINDER_ON + " = 1");
        } else if (filterByCustom) {
            cursor = queryEventsWithInt(EventTable.Cols.CUSTOM + " = 1");
        } else {
            cursor = queryEvents(null, null);
        }

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                events.add(cursor.getEvent());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return events;
    }

    public Event getEvent(UUID id) {
        EventCursorWrapper cursor = queryEvents(
                EventTable.Cols.UUID + " = ?",
                new String[]{id.toString()}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getEvent();
        } finally {
            cursor.close();
        }
    }

    public void updateEvent(Event event) {
        String uuidString = event.getId().toString();
        ContentValues values = getContentValues(event);

        mDatabase.update(EventTable.NAME, values,
                EventTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    private static ContentValues getContentValues(Event event) {
        ContentValues values = new ContentValues();
        values.put(EventTable.Cols.UUID, event.getId().toString());
        values.put(EventTable.Cols.CUSTOM, event.isCustom() ? 1 : 0);
        values.put(EventTable.Cols.TITLE, event.getTitle());
        values.put(EventTable.Cols.DATE, event.getDate().getTime());
        values.put(EventTable.Cols.DESCRIPTION, event.getDescription());
        values.put(EventTable.Cols.SEARCH_TERM, event.getSearchTerm());
        values.put(EventTable.Cols.REMINDER_TIME_AMOUNT, event.getReminderTimeAmount());
        values.put(EventTable.Cols.REMINDER_TIME_UNIT, event.getReminderTimeUnit());
        values.put(EventTable.Cols.REMINDER_ON, event.isReminderOn() ? 1 : 0);

        return values;
    }

    private EventCursorWrapper queryEvents(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                EventTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                EventTable.Cols.DATE  // orderBy
        );

        return new EventCursorWrapper(cursor);
    }

    private EventCursorWrapper queryEventsWithInt(String whereClause) {
        Cursor cursor = mDatabase.query(
                EventTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                null,
                null, // groupBy
                null, // having
                EventTable.Cols.DATE  // orderBy
        );

        return new EventCursorWrapper(cursor);
    }

    private void initEvents() {
        // Event data obtained from in-the-sky.org Astronomical Calendar
        String[] eventNames = {
                "η–Aquarid meteor shower",
                "Transit of Mercury",
                "α–Scorpiid meteor shower",
                "Conjunction between the Moon and Jupiter",
                "Conjunction between the Moon and Saturn",
                "Mars at perigee",
                "Conjunction between the Moon and Uranus",
                "The Moon at perigee",
                "Ophiuchid meteor shower",
                "Conjunction between the Moon and Jupiter",
                "Annular solar eclipse",
        };
        Date[] eventDates = {
                new GregorianCalendar(2016, 4, 6).getTime(),
                new GregorianCalendar(2016, 4, 9, 9, 6, 0).getTime(),
                new GregorianCalendar(2016, 4, 12).getTime(),
                new GregorianCalendar(2016, 4, 15, 2, 23, 0).getTime(),
                new GregorianCalendar(2016, 4, 22, 16, 18, 0).getTime(),
                new GregorianCalendar(2016, 4, 30, 15, 35, 0).getTime(),
                new GregorianCalendar(2016, 5, 1, 9, 27, 0).getTime(),
                new GregorianCalendar(2016, 5, 3, 4, 55, 0).getTime(),
                new GregorianCalendar(2016, 5, 10).getTime(),
                new GregorianCalendar(2016, 5, 11, 12, 45, 0).getTime(),
                new GregorianCalendar(2016, 8, 1, 3, 8, 0).getTime(),
        };
        String[] eventDescriptions = {
                "The η–Aquarid meteor shower will reach its maximum rate of activity on 6 May 2016. Some shooting stars associated with the shower are expected to be visible each night from 24 Apr to 20 May.",
                "...",
                "The α–Scorpiid meteor shower will reach its maximum rate of activity on 13 May 2016. Some shooting stars associated with the shower are expected to be visible each night from 20 Apr to 19 May.",
                "The Moon and Jupiter will make a close approach, passing within 1°54' of each other.",
                "The Moon and Saturn will make a close approach, passing within 3°11' of each other.",
                "Mars's orbit around the Sun will carry it to its closest point to the Earth.",
                "The Moon and Uranus will make a close approach, passing within 2°18' of each other.",
                "The Moon will reach the closest point along its orbit to the Earth, and as a result will appear slightly larger than at other times.",
                "The Ophiuchid meteor shower will reach its maximum rate of activity on 10 June 2016. Some shooting stars associated with the shower are expected to be visible each night from 19 May to Jul.",
                "The Moon and Jupiter will make a close approach, passing within 1°25' of each other.",
                "There will be a total solar eclipse, best seen from southern Africa.",
        };
        String[] eventSearchTerms = {
                "aquarius",
                "mercury",
                "scorpius",
                "jupiter",
                "saturn",
                "mars",
                "uranus",
                "moon",
                "sagitarrius",
                "jupiter",
                "sun",
        };
        for (int i = 0; i < eventNames.length; i++) {
            Event event = new Event();
            event.setTitle(eventNames[i]);
            event.setDate(eventDates[i]);
            event.setDescription(eventDescriptions[i]);
            event.setSearchTerm(eventSearchTerms[i]);
            addEvent(event);
        }
    }
}
