package site.elioplasma.ecook.spacetimeeventreminder;

import java.util.UUID;

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
