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

    //private List<Event> mEvents;
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private boolean mRemindersEnabled;

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
        //mEvents = new ArrayList<>();
        String[] eventNames = {
                "Total Solar Eclipse",
                "Penumbra Lunar Eclipse",
                "Lyrid Meteor Shower",
                "Mars closest to Earth",
                "Ophiuchid Meteor Shower",
                "Special Event",
        };
        Date[] eventDates = {
                new GregorianCalendar(2016, 3, 9).getTime(),
                new GregorianCalendar(2016, 3, 23).getTime(),
                new GregorianCalendar(2016, 4, 23).getTime(),
                new GregorianCalendar(2016, 5, 30).getTime(),
                new GregorianCalendar(2016, 6, 20).getTime(),
                new GregorianCalendar(2016, 8, 18, 20, 0, 0).getTime(),
        };
        String[] eventDescriptions = {
                "Visible in Northern Pacific, Southern Asia, Northern Australia",
                "...",
                "...",
                "...",
                "...",
                "Test description",
        };
        for (int i = 0; i < eventNames.length; i++) {
            Event event = new Event();
            event.setTitle(eventNames[i]);
            event.setDate(eventDates[i]);
            event.setDescription(eventDescriptions[i]);
            addEvent(event);
        }
        mRemindersEnabled = true;
    }

    public void addEvent(Event e) {
        //mEvents.add(e);
        ContentValues values = getContentValues(e);

        mDatabase.insert(EventTable.NAME, null, values);
    }

    public boolean deleteEvent(UUID id) {
        /*
        for (Event event : mEvents) {
            if (event.getId().equals(id)) {
                mEvents.remove(event);
                return true;
            }
        }
        */
        return false;
    }

    public List<Event> getEvents() {
        List<Event> events = new ArrayList<>();

        EventCursorWrapper cursor = queryEvents(null, null);

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
                new String[] { id.toString() }
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
                new String[] { uuidString });
    }

    private static ContentValues getContentValues(Event event) {
        ContentValues values = new ContentValues();
        values.put(EventTable.Cols.UUID, event.getId().toString());
        values.put(EventTable.Cols.CUSTOM, event.isCustom() ? 1 : 0);
        values.put(EventTable.Cols.TITLE, event.getTitle());
        values.put(EventTable.Cols.DATE, event.getDate().getTime());
        values.put(EventTable.Cols.DESCRIPTION, event.getDescription());
        //values.put(EventTable.Cols.REMINDER, event.getReminder());
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
                null  // orderBy
        );

        return new EventCursorWrapper(cursor);
    }

    public boolean areRemindersEnabled() {
        return mRemindersEnabled;
    }

    public void setRemindersEnabled(boolean remindersPaused) {
        mRemindersEnabled = remindersPaused;
    }
}
