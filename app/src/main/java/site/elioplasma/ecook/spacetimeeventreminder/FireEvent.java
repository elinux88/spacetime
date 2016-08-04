package site.elioplasma.ecook.spacetimeeventreminder;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Eli on 04-Aug-16.
 */

@IgnoreExtraProperties
public class FireEvent {

    private String mTitle;
    private long mDateLong;
    private String mDescription;
    private String mSearchTerm;

    public FireEvent() {
        // Default constructor required for calls to DataSnapshot.getValue(FireEvent.class)
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public long getDateLong() {
        return mDateLong;
    }

    public void setDateLong(long dateLong) {
        mDateLong = dateLong;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getSearchTerm() {
        return mSearchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        mSearchTerm = searchTerm;
    }
}
