package site.elioplasma.ecook.spacetimeeventreminder;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

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

        AlarmService.initAlarmService();
        if (EventData.get(getActivity()).areRemindersEnabled()) {
            AlarmService.setAlarmAll(getActivity(), false);
        } else {
            AlarmService.setAlarmAll(getActivity(), true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mEventRecyclerView = (RecyclerView) view
                .findViewById(R.id.event_recycler_view);
        mEventRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
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

        MenuItem itemToggleReminders = menu.findItem(R.id.menu_item_toggle_reminders);
        if (EventData.get(getActivity()).areRemindersEnabled()) {
            itemToggleReminders.setTitle(R.string.pause_all_reminders);
        } else {
            itemToggleReminders.setTitle(R.string.resume_all_reminders);
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
            case R.id.menu_item_toggle_reminders:
                boolean alarmsAreEnabled = EventData.get(getActivity()).areRemindersEnabled();
                if (alarmsAreEnabled) {
                    AlarmService.setAlarmAll(getActivity(), false);
                    EventData.get(getActivity()).setRemindersEnabled(false);
                } else {
                    AlarmService.setAlarmAll(getActivity(), true);
                    EventData.get(getActivity()).setRemindersEnabled(true);
                }
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateUI() {
        EventData eventData = EventData.get(getActivity());
        List<Event> events = eventData.getEvents();

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
        }

        public void bindEvent(Event event) {
            mEvent = event;
            mTitleTextView.setText(mEvent.getTitle());
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy - h:mm a");
            mDateTextView.setText(sdf.format(mEvent.getDate()));
            if (mEvent.isReminderOn()) {
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
