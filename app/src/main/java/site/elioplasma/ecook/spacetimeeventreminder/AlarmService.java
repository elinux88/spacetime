package site.elioplasma.ecook.spacetimeeventreminder;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by eli on 3/10/16.
 */
public class AlarmService extends IntentService {

    private static final String TAG = "AlarmService";
    private static final String EXTRA_EVENT_ID = "event_id";
    private static final String EXTRA_REMINDER_TEXT = "reminder_title";
    private static AlarmManager sAlarmManager;
    private static HashMap<UUID, PendingIntent> sUUIDPendingIntentMap;

    public static Intent newIntent(Context context) {
        return new Intent(context, AlarmService.class);
    }

    public static void populateAlarms(Context context) {
        if (sAlarmManager == null) {
            sAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }
        if (sUUIDPendingIntentMap == null) {
            sUUIDPendingIntentMap = new HashMap<>();
        }
        List<Event> reminderEvents = EventData.get(context).getEventsWithReminders();

        for (Event event : reminderEvents) {
            UUID id = event.getId();
            long millis = getReminderInMillis(event);
            PendingIntent pi = newEventPendingIntent(context, event);

            sUUIDPendingIntentMap.put(id, pi);
            sAlarmManager.set(AlarmManager.RTC_WAKEUP, millis, pi);
        }

    }

    public static void updateAlarm(Context context, UUID id) {
        PendingIntent pi = sUUIDPendingIntentMap.get(id);
        Event event = EventData.get(context).getEvent(id);
        if (event.isReminderOn()) {
            long millis = getReminderInMillis(event);
            pi = newEventPendingIntent(context, event);
            sAlarmManager.set(AlarmManager.RTC_WAKEUP, millis, pi);
        } else {
            sAlarmManager.cancel(pi);
            if (pi != null) {
                pi.cancel();
            }
            sUUIDPendingIntentMap.remove(id);
        }
    }

    private static PendingIntent newEventPendingIntent(Context context, Event event) {
        UUID id = event.getId();
        String title = EventData.get(context).getEvent(id).getTitle();

        Intent intent = AlarmService.newIntent(context);
        intent.putExtra(EXTRA_EVENT_ID, id);
        intent.putExtra(EXTRA_REMINDER_TEXT, title);
        PendingIntent pi = PendingIntent.getService(context,
                id.hashCode(), intent, PendingIntent.FLAG_CANCEL_CURRENT);

        return pi;
    }

    private static long getReminderInMillis(Event event) {
        Date date = event.getReminderDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getTimeInMillis();
    }

    public AlarmService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        UUID id = (UUID) intent.getSerializableExtra(EXTRA_EVENT_ID);
        String text = intent.getStringExtra(EXTRA_REMINDER_TEXT);
        Resources resources = getResources();

        Intent eventIntent = EventActivity.newIntent(this, id);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(EventActivity.class);
        stackBuilder.addNextIntent(eventIntent);
        PendingIntent pi = stackBuilder.getPendingIntent(id.hashCode(),
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setTicker(resources.getString(R.string.event_reminder_title))
                .setSmallIcon(android.R.drawable.ic_menu_today)
                .setContentTitle(resources.getString(R.string.event_reminder_title))
                .setContentText(text)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);
        notificationManager.notify(id.hashCode(), notification);
    }
}
