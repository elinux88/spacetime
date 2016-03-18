package site.elioplasma.ecook.spacetimeeventreminder;

import java.util.Date;
import java.util.UUID;

/**
 * Created by eli on 2/27/16.
 */
public class Event {

    private UUID mId;
    private boolean mCustom;
    private String mTitle;
    private Date mDate;
    private String mDescription;
    private Reminder mReminder;
    private boolean mReminderOn;

    public Event() {
        mId = UUID.randomUUID();
        mCustom = false;
        mDate = new Date();
        mReminder = new Reminder();
    }

    public Date getReminderDate() {
        long millis = mReminder.getAmountInMillis();
        Date reminderDate = new Date(mDate.getTime() - millis);
        return reminderDate;
    }

    public UUID getId() {
        return mId;
    }

    public boolean isCustom() {
        return mCustom;
    }

    public void setCustom(boolean custom) {
        mCustom = custom;
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

    public boolean isReminderOn() {
        return mReminderOn;
    }

    public void setReminderOn(boolean reminderOn) {
        mReminderOn = reminderOn;
    }
}
