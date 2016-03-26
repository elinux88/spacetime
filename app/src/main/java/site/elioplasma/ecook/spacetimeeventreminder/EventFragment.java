package site.elioplasma.ecook.spacetimeeventreminder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String KEY_REMINDER = "reminder";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;

    private Event mEvent;
    private TextView mTitleTextView;
    private TextView mDateTextView;
    private TextView mDescriptionTextView;
    private EditText mTitleEditText;
    private Button mDateButton;
    private Button mTimeButton;
    private EditText mDescriptionEditText;
    private Spinner mReminderAmountSpinner;
    private Spinner mReminderTypeSpinner;
    private Button mReminderButton;

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
            mTitleEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // not used
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mEvent.setTitle(mTitleEditText.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // not used
                }
            });
        } else {
            mTitleTextView = (TextView) v.findViewById(R.id.event_detail_title);
            mTitleTextView.setText(mEvent.getTitle());
        }



        if (mEvent.isCustom()) {
            mDateButton = (Button) v.findViewById(R.id.event_detail_custom_date_button);
            mDateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager manager = getFragmentManager();
                    DatePickerFragment dialog = DatePickerFragment
                            .newInstance(mEvent.getDate());
                    dialog.setTargetFragment(EventFragment.this, REQUEST_DATE);
                    dialog.show(manager, DIALOG_DATE);
                }
            });

            mTimeButton = (Button) v.findViewById(R.id.event_detail_custom_time_button);
            mTimeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager manager = getFragmentManager();
                    TimePickerFragment dialog = TimePickerFragment
                            .newInstance(mEvent.getDate());
                    dialog.setTargetFragment(EventFragment.this, REQUEST_TIME);
                    dialog.show(manager, DIALOG_TIME);
                }
            });

            updateDateTimeButton();

        } else {
            mDateTextView = (TextView) v.findViewById(R.id.event_detail_date);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy - h:mm a");
            mDateTextView.setText(sdf.format(mEvent.getDate()));
        }

        if (mEvent.isCustom()) {
            mDescriptionEditText = (EditText) v.findViewById(R.id.event_detail_custom_description);
            mDescriptionEditText.setText(mEvent.getDescription());
            mDescriptionEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // not used
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mEvent.setDescription(mDescriptionEditText.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // not used
                }
            });
        } else {
            mDescriptionTextView = (TextView) v.findViewById(R.id.event_detail_description);
            mDescriptionTextView.setText(mEvent.getDescription());
        }

        if (mEvent.isCustom()) {
            mReminderAmountSpinner = (Spinner) v.findViewById(R.id.event_detail_custom_time_amount_spinner);
        } else {
            mReminderAmountSpinner = (Spinner) v.findViewById(R.id.event_detail_time_amount_spinner);
        }
        List<String> amounts = new ArrayList<String>();
        for (int i = 0; i < 60; i++) {
            amounts.add(Integer.toString(i));
        }
        ArrayAdapter<String> amountAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, amounts);
        amountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mReminderAmountSpinner.setAdapter(amountAdapter);

        String reminderAmount = Integer.toString(mEvent.getReminder().getAmount());
        int amountSpinnerPosition = amountAdapter.getPosition(reminderAmount);
        mReminderAmountSpinner.setSelection(amountSpinnerPosition);

        mReminderAmountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int amount = Integer.parseInt(parent.getItemAtPosition(position).toString());
                Reminder reminder = mEvent.getReminder();
                reminder.setAmount(amount);
                mEvent.setReminder(reminder);
                if (mEvent.isReminderOn()) {
                    AlarmService.setAlarmById(getActivity(), true, mEvent.getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // not used
            }
        });

        if (mEvent.isCustom()) {
            mReminderTypeSpinner = (Spinner) v.findViewById(R.id.event_detail_custom_time_type_spinner);
        } else {
            mReminderTypeSpinner = (Spinner) v.findViewById(R.id.event_detail_time_type_spinner);
        }
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.time_type_array, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mReminderTypeSpinner.setAdapter(typeAdapter);

        mReminderTypeSpinner.setSelection(mEvent.getReminder().getType());

        mReminderTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Reminder reminder = mEvent.getReminder();
                reminder.setType(position);
                mEvent.setReminder(reminder);
                if (mEvent.isReminderOn()) {
                    AlarmService.setAlarmById(getActivity(), true, mEvent.getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // not used
            }
        });

        if (mEvent.isCustom()) {
            mReminderButton = (Button) v.findViewById(R.id.event_detail_custom_reminder_button);
        } else {
            mReminderButton = (Button) v.findViewById(R.id.event_detail_reminder_button);
        }
        updateReminderButton();
        mReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean toggle = !mEvent.isReminderOn();
                AlarmService.setAlarmById(getActivity(), toggle, mEvent.getId());
                mEvent.setReminderOn(toggle);
                updateReminderButton();
            }
        });

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_event, menu);

        if (!mEvent.isCustom()) {
            MenuItem deleteItem = menu.findItem(R.id.menu_item_delete_event);
            deleteItem.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_item_delete_event:
                if (mEvent.isCustom()) {
                    AlarmService.setAlarmById(getActivity(), false, mEvent.getId());
                    if (EventData.get(getActivity()).deleteEvent(mEvent.getId())) {
                        getActivity().finish();
                    } else {
                        return false;
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateReminderButton();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mEvent.setDate(date);
            updateDateTimeButton();
            if (mEvent.isReminderOn()) {
                AlarmService.setAlarmById(getActivity(), true, mEvent.getId());
            }
        } else if (requestCode == REQUEST_TIME) {
            Date date = (Date) data
                    .getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mEvent.setDate(date);
            updateDateTimeButton();
            if (mEvent.isReminderOn()) {
                AlarmService.setAlarmById(getActivity(), true, mEvent.getId());
            }
        }
    }

    private void updateDateTimeButton() {
        Date date = mEvent.getDate();

        SimpleDateFormat sdfDate = new SimpleDateFormat("dd MMM yyyy");
        mDateButton.setText(sdfDate.format(date));

        SimpleDateFormat sdfTime = new SimpleDateFormat("h:mm a");
        mTimeButton.setText(sdfTime.format(date));
    }

    private void updateReminderButton() {
        if (mEvent.isReminderOn()) {
            mReminderButton.setText(R.string.reminder_on);
            mReminderButton.setBackgroundResource(R.drawable.apptheme_btn_default_pressed_holo_light);
        } else {
            mReminderButton.setText(R.string.reminder_off);
            mReminderButton.setBackgroundResource(R.drawable.apptheme_btn_default_normal_holo_light);
        }
    }
}
