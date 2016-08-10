package site.elioplasma.ecook.spacetimeeventreminder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A fragment containing a RecyclerView.
 */
public class EventListFragment extends Fragment {

    private RecyclerView mEventRecyclerView;
    private EventAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        long lastDateUpdated = QueryPreferences.getStoredDateUpdated(getActivity());
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -1);
        long currentTimeYesterday = cal.getTimeInMillis();

        if (lastDateUpdated < currentTimeYesterday) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signInAnonymously()
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            EventData.get(getActivity()).updateEventList();
                            Toast.makeText(getActivity(), R.string.updating_event_list, Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

            EventData.get(getActivity()).deleteOldEvents();
            QueryPreferences.setStoredDateUpdated(getActivity(), new Date().getTime());
        }

        AlarmService.populateAlarms(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        mEventRecyclerView = (RecyclerView) v
                .findViewById(R.id.event_recycler_view);
        mEventRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (QueryPreferences.getStoredNightMode(getActivity())) {
            v.findViewById(R.id.event_recycler_view)
                    .setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorNightPrimary));
                    //.setBackgroundColor(getResources().getColor(R.color.colorNightPrimary));
        } else {
            v.findViewById(R.id.event_recycler_view)
                    .setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
                    //.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        updateUI();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_event_list, menu);

        MenuItem itemToggleAllReminders = menu.findItem(R.id.menu_item_toggle_all_reminders);
        if (QueryPreferences.getStoredRemindersEnabled(getActivity())) {
            itemToggleAllReminders.setTitle(R.string.pause_all_reminders);
        } else {
            itemToggleAllReminders.setTitle(R.string.resume_all_reminders);
        }

        MenuItem itemFilterByReminder = menu.findItem(R.id.menu_item_filter_by_reminder);
        if (QueryPreferences.getStoredFilterByReminders(getActivity())) {
            itemFilterByReminder.setTitle(R.string.show_without_reminders);
        } else {
            itemFilterByReminder.setTitle(R.string.hide_without_reminders);
        }

        MenuItem itemFilterByCustom = menu.findItem(R.id.menu_item_filter_by_custom);
        if (QueryPreferences.getStoredFilterByCustom(getActivity())) {
            itemFilterByCustom.setTitle(R.string.show_not_custom);
        } else {
            itemFilterByCustom.setTitle(R.string.hide_not_custom);
        }

        MenuItem itemNightMode = menu.findItem(R.id.menu_item_night_mode);
        if (QueryPreferences.getStoredNightMode(getActivity())) {
            itemNightMode.setTitle(R.string.turn_off_night_mode);
        } else {
            itemNightMode.setTitle(R.string.turn_on_night_mode);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_event:
                Event event = new Event();
                event.setCustom(true);
                EventData.get(getActivity()).addEvent(event);
                Intent intent = EventActivity
                        .newIntent(getActivity(), event.getId());
                startActivity(intent);
                return true;
            case R.id.menu_item_toggle_all_reminders:
                boolean alarmsAreEnabled = QueryPreferences.getStoredRemindersEnabled(getActivity());
                if (alarmsAreEnabled) {
                    QueryPreferences.setStoredRemindersEnabled(getActivity(), false);
                    AlarmService.setAllAlarms(getActivity(), false);
                } else {
                    QueryPreferences.setStoredRemindersEnabled(getActivity(), true);
                    AlarmService.setAllAlarms(getActivity(), true);
                }
                getActivity().invalidateOptionsMenu();
                return true;
            case R.id.menu_item_filter_by_reminder:
                boolean filterByReminder = QueryPreferences.getStoredFilterByReminders(getActivity());
                if (filterByReminder) {
                    QueryPreferences.setStoredFilterByReminders(getActivity(), false);
                } else {
                    QueryPreferences.setStoredFilterByReminders(getActivity(), true);
                }
                getActivity().invalidateOptionsMenu();
                updateUI();
                return true;
            case R.id.menu_item_filter_by_custom:
                boolean filterByCustom = QueryPreferences.getStoredFilterByCustom(getActivity());
                if (filterByCustom) {
                    QueryPreferences.setStoredFilterByCustom(getActivity(), false);
                } else {
                    QueryPreferences.setStoredFilterByCustom(getActivity(), true);
                }
                getActivity().invalidateOptionsMenu();
                updateUI();
                return true;
            case R.id.menu_item_night_mode:
                boolean nightMode = QueryPreferences.getStoredNightMode(getActivity());
                if (nightMode) {
                    QueryPreferences.setStoredNightMode(getActivity(), false);
                } else {
                    QueryPreferences.setStoredNightMode(getActivity(), true);
                }
                getActivity().recreate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateUI() {
        EventData eventData = EventData.get(getActivity());
        List<Event> events = eventData.getEvents(getActivity());

        if (mAdapter == null) {
            mAdapter = new EventAdapter(events);
            mEventRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setEvents(events);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class EventHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Event mEvent;

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private TextView mReminderOnTextView;

        public EventHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView)
                    itemView.findViewById(R.id.list_item_event_title_text_view);
            mDateTextView = (TextView)
                    itemView.findViewById(R.id.list_item_event_date_text_view);
            mReminderOnTextView = (TextView)
                    itemView.findViewById(R.id.list_item_event_reminder_on_text_view);

            if (QueryPreferences.getStoredNightMode(getActivity())) {
                itemView.findViewById(R.id.list_item_layout)
                        .setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.black));
                        //.setBackgroundColor(getResources().getColor(android.R.color.black));
                mTitleTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorNightPrimaryDark));
                //mTitleTextView.setTextColor(getResources().getColor(R.color.colorNightPrimaryDark));
                mDateTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorNightPrimaryDark));
                //mDateTextView.setTextColor(getResources().getColor(R.color.colorNightPrimaryDark));
            }
        }

        public void bindEvent(Event event) {
            mEvent = event;
            mTitleTextView.setText(mEvent.getTitle());
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy - h:mm a", Locale.US);
            mDateTextView.setText(sdf.format(mEvent.getDate()));
            if (mEvent.isReminderOn()) {
                if (QueryPreferences.getStoredNightMode(getActivity())) {
                    mReminderOnTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            R.drawable.ic_reminder_night, 0);
                }
                mReminderOnTextView.setVisibility(View.VISIBLE);
            } else {
                mReminderOnTextView.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            Intent intent = EventActivity.newIntent(getActivity(), mEvent.getId());
            startActivity(intent);
        }
    }

    private class EventAdapter extends RecyclerView.Adapter<EventHolder> {

        private List<Event> mEvents;

        public EventAdapter(List<Event> events) {
            mEvents = events;
        }

        @Override
        public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.list_item_event, parent, false);
            return new EventHolder(view);
        }

        @Override
        public void onBindViewHolder(EventHolder holder, int position) {
            Event event = mEvents.get(position);
            holder.bindEvent(event);
        }

        @Override
        public int getItemCount() {
            return mEvents.size();
        }

        public void setEvents(List<Event> events) {
            mEvents = events;
        }
    }
}
