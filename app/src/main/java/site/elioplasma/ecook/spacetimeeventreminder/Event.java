package site.elioplasma.ecook.spacetimeeventreminder;

import java.util.Date;
import java.util.UUID;

/**
 * Created by eli on 2/27/16.
 */
public class Event {

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private String mDescription;
    private Reminder mReminder;

    public Event() {
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public Reminder getReminder() {
        return mReminder;
    }

    public void setReminder(Reminder reminder) {
        mReminder = reminder;
    }
}
