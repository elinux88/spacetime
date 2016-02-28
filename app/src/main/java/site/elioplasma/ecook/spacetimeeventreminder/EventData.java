package site.elioplasma.ecook.spacetimeeventreminder;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by eli on 2/27/16.
 */
public class EventData {
    private static EventData sEventData;

    private List<Event> mEvents;

    public static EventData get(Context context) {
        if (sEventData == null) {
            sEventData = new EventData(context);
        }
        return sEventData;
    }

    private EventData(Context context) {
        mEvents = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Event event = new Event();
            event.setTitle("Event #" + i);
            mEvents.add(event);
        }
    }

    public List<Event> getEvents() {
        return mEvents;
    }

    public Event getEvent(UUID id) {
        for (Event event : mEvents) {
            if (event.getId().equals(id)) {
                return event;
            }
        }
        return null;
    }
}
