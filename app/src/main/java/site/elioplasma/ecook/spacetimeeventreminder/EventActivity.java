package site.elioplasma.ecook.spacetimeeventreminder;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

/**
 * Created by eli on 2/29/16.
 */
public class EventActivity extends SingleFragmentActivity {

    private static final String EXTRA_EVENT_ID =
            "site.elioplasma.ecook.spacetimeeventreminder.event_id";

    public static Intent newIntent(Context packageContext, UUID eventId) {
        Intent intent = new Intent(packageContext, EventActivity.class);
        intent.putExtra(EXTRA_EVENT_ID, eventId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        UUID eventId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_EVENT_ID);
        return EventFragment.newInstance(eventId);
    }
}
