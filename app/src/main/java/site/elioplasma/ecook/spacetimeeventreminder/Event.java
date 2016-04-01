package site.elioplasma.ecook.spacetimeeventreminder;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by eli on 2/27/16.
 */
public class Event {

    private static final int TIME_UNIT_MINUTE = 0;
    private static final int TIME_UNIT_HOUR = 1;
    private static final int TIME_UNIT_DAY = 2;

    private UUID mId;
    private boolean mCustom;
    private String mTitle;
    private Date mDate;
    private String mDescription;
    private int mReminderTimeAmount;
    private int mReminderTimeUnit;
    private boolean mReminderOn;

    public Event() {
        this(UUID.randomUUID());
    }

    public Event(UUID id) {
        mId = id;
        mCustom = false;
        mDate = new Date();
    }

    public Date getReminderDate() {
        long millis = 0;

        if (mReminderTimeUnit == TIME_UNIT_MINUTE) {
            millis = TimeUnit.MINUTES.toMillis(mReminderTimeAmount);
        } else if (mReminderTimeUnit == TIME_UNIT_HOUR) {
            millis = TimeUnit.HOURS.toMillis(mReminderTimeAmount);
        } else if (mReminderTimeUnit == TIME_UNIT_DAY) {
            millis = TimeUnit.DAYS.toMillis(mReminderTimeAmount);
        }

        return new Date(mDate.getTime() - millis);
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

    public int getReminderTimeAmount() {
        return mReminderTimeAmount;
    }

    public void setReminderTimeAmount(int reminderTimeAmount) {
        mReminderTimeAmount = reminderTimeAmount;
    }

    public int getReminderTimeUnit() {
        return mReminderTimeUnit;
    }

    public void setReminderTimeUnit(int reminderTimeUnit) {
        mReminderTimeUnit = reminderTimeUnit;
    }

    public boolean isReminderOn() {
        return mReminderOn;
    }

    public void setReminderOn(boolean reminderOn) {
        mReminderOn = reminderOn;
    }
}
