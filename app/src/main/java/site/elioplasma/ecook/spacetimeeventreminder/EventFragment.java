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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private EditText mTitleEditText;
    private Button mDateButton;
    private DatePicker mDatePicker;
    private EditText mDescriptionEditText;
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
        View v;
        if (mEvent.isCustom()) {
            v = inflater.inflate(R.layout.fragment_event_custom, container, false);
        } else {
            v = inflater.inflate(R.layout.fragment_event, container, false);
        }

        if (mEvent.isCustom()) {
            mTitleEditText = (EditText) v.findViewById(R.id.event_detail_custom_title);
            mTitleEditText.setText(mEvent.getTitle());
        } else {
            mTitleTextView = (TextView) v.findViewById(R.id.event_detail_title);
            mTitleTextView.setText(mEvent.getTitle());
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");

        if (mEvent.isCustom()) {
            mDateButton = (Button) v.findViewById(R.id.event_detail_custom_date_button);
            mDateButton.setText(sdf.format(mEvent.getDate()));
            //mDatePicker = (DatePicker) v.findViewById(R.id.event_detail_custom_date);
        } else {
            mDateTextView = (TextView) v.findViewById(R.id.event_detail_date);
            mDateTextView.setText(sdf.format(mEvent.getDate()));
        }

        if (mEvent.isCustom()) {
            mDescriptionEditText = (EditText) v.findViewById(R.id.event_detail_custom_description);
            mDescriptionEditText.setText(mEvent.getDescription());
        } else {
            mDescriptionTextView = (TextView) v.findViewById(R.id.event_detail_description);
            mDescriptionTextView.setText(mEvent.getDescription());
        }

        if (mEvent.isCustom()) {
            mTimeAmountSpinner = (Spinner) v.findViewById(R.id.event_detail_custom_time_amount_spinner);
        } else {
            mTimeAmountSpinner = (Spinner) v.findViewById(R.id.event_detail_time_amount_spinner);
        }
        List<String> amounts = new ArrayList<String>();
        for (int i = 0; i < 60; i++) {
            amounts.add(Integer.toString(i));
        }
        ArrayAdapter<String> amountAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, amounts);
        //ArrayAdapter<CharSequence> amountAdapter = ArrayAdapter.createFromResource(getActivity(),
        //        intArray, android.R.layout.simple_spinner_item);
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

        if (mEvent.isCustom()) {
            mTimeTypeSpinner = (Spinner) v.findViewById(R.id.event_detail_custom_time_type_spinner);
        } else {
            mTimeTypeSpinner = (Spinner) v.findViewById(R.id.event_detail_time_type_spinner);
        }
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
        if (AlarmService.isServiceAlarmOn(getActivity())) {
            toggleItem.setTitle(R.string.reminder_off);
        } else {
            toggleItem.setTitle(R.string.reminder_on);
        }

        if (!mEvent.isCustom()) {
            MenuItem deleteItem = menu.findItem(R.id.menu_item_delete_event);
            deleteItem.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_item_toggle_reminder:
                boolean shouldStartAlarm = !AlarmService.isServiceAlarmOn(getActivity());
                Date date = mEvent.getReminderDate();
                AlarmService.setServiceAlarm(getActivity(), shouldStartAlarm, date);
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
