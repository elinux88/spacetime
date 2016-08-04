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
    private FirebaseDatabase mFirebase;
    private DatabaseReference mEventsRef;

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
        mFirebase = FirebaseDatabase.getInstance();
        mEventsRef = mFirebase.getReference("events");

        saveToFirebase();

        mEventsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                FireEvent fireEvent = dataSnapshot.getValue(FireEvent.class);
                Log.v("EventAdded", fireEvent.getTitle());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                FireEvent fireEvent = dataSnapshot.getValue(FireEvent.class);
                Log.v("EventChanged", fireEvent.getTitle());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                FireEvent fireEvent = dataSnapshot.getValue(FireEvent.class);
                Log.v("EventRemoved", fireEvent.getTitle());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // Temporary function for setting up initial data
    private void saveToFirebase() {

        // create a new FireEvent object
        FireEvent fireEvent = new FireEvent();
        fireEvent.setTitle("Moon at First Quarter");
        fireEvent.setDateLong(new GregorianCalendar(2016, 7, 10, 18, 22, 0).getTime().getTime());
        fireEvent.setDescription("The Moon will reach the midpoint between new moon and full moon.");
        fireEvent.setSearchTerm("moon");

        String eventId = "4ed1accd-1491-4562-8c6c-664c97dc6a36";

        mEventsRef.child(eventId).setValue(fireEvent);
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
                "Capricornid meteor shower",
                "Full Moon",
                "81P/Wild at perihelion",
                "α–Cygnid meteor shower",
                "Conjunction between the Moon and Uranus",
                "The Moon at perigee",
                "δ–Aquarid meteor shower",
                "Piscis Australid meteor shower",
                "Conjunction between the Moon and Saturn",
                "Perseid meteor shower",
        };
        Date[] eventDates = {
                new GregorianCalendar(2016, 6, 15).getTime(),
                new GregorianCalendar(2016, 6, 19, 22, 58, 0).getTime(),
                new GregorianCalendar(2016, 6, 20).getTime(),
                new GregorianCalendar(2016, 6, 21).getTime(),
                new GregorianCalendar(2016, 6, 26, 6, 0, 0).getTime(),
                new GregorianCalendar(2016, 6, 27, 11, 38, 0).getTime(),
                new GregorianCalendar(2016, 6, 29).getTime(),
                new GregorianCalendar(2016, 6, 31).getTime(),
                new GregorianCalendar(2016, 7, 12, 12, 50, 0).getTime(),
                new GregorianCalendar(2016, 7, 13).getTime(),
        };
        String[] eventDescriptions = {
                "The Capricornid meteor shower will reach its maximum rate of activity on 15 July 2016. Some shooting stars associated with the shower are expected to be visible each night from Jul to Aug.",
                "The Moon will reach full phase. At this time in its monthly cycle of phases, the Moon lies almost directly opposite the Sun in the sky, placing it high above the horizon for much of the night.",
                "Comet 81P/Wild will make its closest approach to the Sun, at a distance of 1.60 AU.",
                "The α–Cygnid meteor shower will reach its maximum rate of activity on 21 July 2016. Some shooting stars associated with the shower are expected to be visible each night from Jul to Aug.",
                "The Moon and Uranus will make a close approach, passing within 2°47' of each other.",
                "The Moon will reach the closest point along its orbit to the Earth, and as a result will appear slightly larger than at other times.",
                "The δ–Aquarid meteor shower will reach its maximum rate of activity on 29 July 2016. Some shooting stars associated with the shower are expected to be visible each night from 15 Jul to 20 Aug.",
                "The Piscis Australid meteor shower will reach its maximum rate of activity on 31 July 2016. Some shooting stars associated with the shower are expected to be visible each night from 15 Jul to 20 Aug.",
                "The Moon and Saturn will make a close approach, passing within 3°37' of each other.",
                "The Perseid meteor shower will reach its maximum rate of activity on 13 August 2016. Some shooting stars associated with the shower are expected to be visible each night from 23 Jul to 20 Aug.",
        };
        String[] eventSearchTerms = {
                "capricorn",
                "moon",
                "leo",
                "cygnus",
                "uranus",
                "moon",
                "aquarius",
                "piscis",
                "saturn",
                "perseus",
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
