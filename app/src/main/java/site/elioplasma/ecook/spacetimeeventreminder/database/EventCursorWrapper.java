package site.elioplasma.ecook.spacetimeeventreminder.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import site.elioplasma.ecook.spacetimeeventreminder.Event;
import site.elioplasma.ecook.spacetimeeventreminder.database.EventDbSchema.EventTable;

/**
 * Created by eli on 3/26/16.
 */
public class EventCursorWrapper extends CursorWrapper {
    public EventCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Event getEvent() {
        String uuidString = getString(getColumnIndex(EventTable.Cols.UUID));
        int isCustom = getInt(getColumnIndex(EventTable.Cols.CUSTOM));
        String title = getString(getColumnIndex(EventTable.Cols.TITLE));
        long date = getLong(getColumnIndex(EventTable.Cols.DATE));
        String description = getString(getColumnIndex(EventTable.Cols.DESCRIPTION));
        int reminderTimeAmount = getInt(getColumnIndex(EventTable.Cols.REMINDER_TIME_AMOUNT));
        int reminderTimeUnit = getInt(getColumnIndex(EventTable.Cols.REMINDER_TIME_UNIT));
        int isReminderOn = getInt(getColumnIndex(EventTable.Cols.REMINDER_ON));

        Event event = new Event(UUID.fromString(uuidString));
        event.setCustom(isCustom != 0);
        event.setTitle(title);
        event.setDate(new Date(date));
        event.setDescription(description);
        event.setReminderTimeAmount(reminderTimeAmount);
        event.setReminderTimeUnit(reminderTimeUnit);
        event.setReminderOn(isReminderOn != 0);

        return event;
    }
}
