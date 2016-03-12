package site.elioplasma.ecook.spacetimeeventreminder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
    private Spinner mTimeAmountSpinner;
    private Spinner mTimeTypeSpinner;

    public static Intent newIntent(Context context) {
        return new Intent(context, EventFragment.class);
    }

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

        mDateTextView = (TextView)v.findViewById(R.id.event_detail_date);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        mDateTextView.setText(sdf.format(mEvent.getDate()));

        mDescriptionTextView = (TextView)v.findViewById(R.id.event_detail_description);
        mDescriptionTextView.setText(mEvent.getDescription());

        mTimeAmountSpinner = (Spinner)v.findViewById(R.id.event_detail_time_amount_spinner);
        ArrayAdapter<CharSequence> amountAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.time_amount_array, android.R.layout.simple_spinner_item);
        amountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTimeAmountSpinner.setAdapter(amountAdapter);

        String reminderAmount = Integer.toString(mEvent.getReminder().getAmount());
        int amountSpinnerPosition = amountAdapter.getPosition(reminderAmount);
        mTimeAmountSpinner.setSelection(amountSpinnerPosition);

        mTimeAmountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int amount = Integer.parseInt(parent.getItemAtPosition(position).toString());
                Reminder reminder = mEvent.getReminder();
                reminder.setAmount(amount);
                mEvent.setReminder(reminder);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // not used
            }
        });

        mTimeTypeSpinner = (Spinner)v.findViewById(R.id.event_detail_time_type_spinner);
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.time_type_array, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTimeTypeSpinner.setAdapter(typeAdapter);

        mTimeTypeSpinner.setSelection(mEvent.getReminder().getType());

        mTimeTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Reminder reminder = mEvent.getReminder();
                reminder.setType(position);
                mEvent.setReminder(reminder);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // not used
            }
        });

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_event, menu);

        MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_reminder);
        if (PollService.isServiceAlarmOn(getActivity())) {
            toggleItem.setTitle(R.string.stop_reminder);
        } else {
            toggleItem.setTitle(R.string.start_reminder);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_item_edit_event:
                //mTitleTextView.setKeyListener((KeyListener) mTitleTextView.getTag());
                //mTitleTextView.setCursorVisible(true);
                return true;
            case R.id.menu_item_toggle_reminder:
                boolean shouldStartAlarm = !PollService.isServiceAlarmOn(getActivity());
                PollService.setServiceAlarm(getActivity(), shouldStartAlarm);
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
