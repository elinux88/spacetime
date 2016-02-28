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
}
