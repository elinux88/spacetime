package site.elioplasma.ecook.spacetimeeventreminder;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private RecyclerView mEventRecyclerView;
    private EventAdapter mAdapter;

    public MainActivityFragment() {
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

    private void updateUI() {
        EventData eventData = EventData.get(getActivity());
        List<Event> events = eventData.getEvents();

        mAdapter = new EventAdapter(events);
        mEventRecyclerView.setAdapter(mAdapter);
    }

    private class EventHolder extends RecyclerView.ViewHolder {

        public TextView mTitleTextView;

        public EventHolder(View itemView) {
            super(itemView);

            mTitleTextView = (TextView) itemView;
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
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new EventHolder(view);
        }

        @Override
        public void onBindViewHolder(EventHolder holder, int position) {
            Event event = mEvents.get(position);
            holder.mTitleTextView.setText(event.getTitle());
        }

        @Override
        public int getItemCount() {
            return mEvents.size();
        }
    }
}
