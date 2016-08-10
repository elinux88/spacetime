package site.elioplasma.ecook.spacetimeeventreminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public void updateEventList() {
        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        DatabaseReference eventsRef = firebase.getReference("events");

        eventsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                UUID id = UUID.fromString(dataSnapshot.getKey());
                FireEvent fireEvent = dataSnapshot.getValue(FireEvent.class);

                Event event = makeEventFromFireEvent(id, fireEvent);

                Event existingEvent = getEvent(id);
                if (existingEvent == null) {
                    addEvent(event);
                } else {
                    // Compare new and existing events
                    if (!event.getTitle().equals(existingEvent.getTitle())
                            || !event.getDate().equals(existingEvent.getDate())
                            || !event.getDescription().equals(existingEvent.getDescription())
                            || !event.getSearchTerm().equals(existingEvent.getSearchTerm())) {
                        updateEvent(event);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                UUID id = UUID.fromString(dataSnapshot.getKey());
                FireEvent fireEvent = dataSnapshot.getValue(FireEvent.class);

                Event event = makeEventFromFireEvent(id, fireEvent);
                updateEvent(event);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                UUID id = UUID.fromString(dataSnapshot.getKey());
                deleteEvent(id);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private Event makeEventFromFireEvent(UUID id, FireEvent fireEvent) {
        Event event = new Event(id);
        event.setTitle(fireEvent.getTitle());
        event.setDate(new Date(fireEvent.getDateLong()));
        event.setDescription(fireEvent.getDescription());
        event.setSearchTerm(fireEvent.getSearchTerm());

        return event;
    }

    public void deleteOldEvents() {
        List<Event> events = getEvents(mContext);

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -1);
        long currentTimeYesterday = cal.getTimeInMillis();

        for (Event event : events) {
            if (event.getDate().getTime() < currentTimeYesterday) {
                deleteEvent(event.getId());
            }
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
        Event event = new Event();
        event.setTitle("Custom Event");
        event.setDate(new Date());
        event.setDescription("This is an example of a custom event. It can be deleted.");
        event.setSearchTerm("sun");
        event.setCustom(true);
        addEvent(event);
    }
}
