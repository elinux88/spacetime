package site.elioplasma.ecook.spacetimeeventreminder;

import android.content.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
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
        String[] eventNames = {
                "Total Solar Eclipse",
                "Penumbra Lunar Eclipse",
                "Lyrid Meteor Shower",
                "Mars closest to Earth",
                "Ophiuchid Meteor Shower",
        };
        Date[] eventDates = {
                new GregorianCalendar(2016, 3, 9).getTime(),
                new GregorianCalendar(2016, 3, 23).getTime(),
                new GregorianCalendar(2016, 4, 23).getTime(),
                new GregorianCalendar(2016, 5, 30).getTime(),
                new GregorianCalendar(2016, 6, 20).getTime(),
        };
        String[] eventDescriptions = {
                "Visible in Northern Pacific, Southern Asia, Northern Australia",
                "...",
                "...",
                "...",
                "...",
        };
        for (int i = 0; i < eventNames.length; i++) {
            Event event = new Event();
            event.setTitle(eventNames[i]);
            event.setDate(eventDates[i]);
            event.setDescription(eventDescriptions[i]);
            addEvent(event);
        }
    }

    public void addEvent(Event e) {
        mEvents.add(e);
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
