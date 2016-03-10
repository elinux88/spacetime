package site.elioplasma.ecook.spacetimeeventreminder;

import java.util.Date;
import java.util.UUID;

/**
 * Created by eli on 3/9/16.
 */
public class Reminder {

    private UUID mId;
    private Date mDate;

    public Reminder() {
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }
}
