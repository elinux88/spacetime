package site.elioplasma.ecook.spacetimeeventreminder;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by eli on 3/9/16.
 */
public class Reminder {

    private UUID mId;
    private int mAmount;
    private int mType;

    public Reminder() {
        mId = UUID.randomUUID();
    }

    public long getAmountInMillis() {
        long millis = 0;

        if (mType == 0) {
            // Minutes
            millis = TimeUnit.MINUTES.toMillis(mAmount);
        } else if (mType == 1) {
            // Hours
            millis = TimeUnit.HOURS.toMillis(mAmount);
        } else if (mType == 2) {
            // Days
            millis = TimeUnit.DAYS.toMillis(mAmount);
        }
        return millis;
    }

    public UUID getId() {
        return mId;
    }

    public int getAmount() {
        return mAmount;
    }

    public void setAmount(int amount) {
        mAmount = amount;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }
}
