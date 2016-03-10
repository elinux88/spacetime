package site.elioplasma.ecook.spacetimeeventreminder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.UUID;

/**
 * Created by eli on 2/29/16.
 */
public class EventFragment extends Fragment {

    private static final String ARG_EVENT_ID = "event_id";

    private Event mEvent;
    private TextView mTitleTextView;
    private TextView mDateTextView;
    private TextView mDescriptionTextView;
    private TextView mReminderTextView;

    public static EventFragment newInstance(UUID eventId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_EVENT_ID, eventId);

        EventFragment fragment = new EventFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        UUID eventId = (UUID) getArguments().getSerializable(ARG_EVENT_ID);

        mEvent = EventData.get(getActivity()).getEvent(eventId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event, container, false);

        mTitleTextView = (TextView)v.findViewById(R.id.event_detail_title);
        mTitleTextView.setText(mEvent.getTitle());
        /*
        mTitleTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEvent.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // not used
            }
        });
        mTitleTextView.setTag(mTitleTextView.getKeyListener());
        mTitleTextView.setKeyListener(null);
        */

        mDateTextView = (TextView)v.findViewById(R.id.event_detail_date);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        mDateTextView.setText(sdf.format(mEvent.getDate()));

        mDescriptionTextView = (TextView)v.findViewById(R.id.event_detail_description);
        mDescriptionTextView.setText(mEvent.getDescription());

        mReminderTextView = (TextView)v.findViewById(R.id.event_detail_reminder);
        mReminderTextView.setText("... before");

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_event, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_item_edit_event:
                //mTitleTextView.setKeyListener((KeyListener) mTitleTextView.getTag());
                //mTitleTextView.setCursorVisible(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
