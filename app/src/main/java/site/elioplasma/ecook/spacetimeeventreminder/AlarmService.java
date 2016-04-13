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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by eli on 3/10/16.
 */
public class AlarmService extends IntentService {

    private static final String TAG = "AlarmService";
    private static final String EXTRA_EVENT_ID = "event_id";
    private static final String EXTRA_REMINDER_TEXT = "reminder_title";
    private static List<UUID> sEventIds;

    public static Intent newIntent(Context context) {
        return new Intent(context, AlarmService.class);
    }

    public static void initAlarmService(Context context) {
        if (sEventIds == null) {
            sEventIds = new ArrayList<>();
            List<Event> reminderEvents = EventData.get(context).getEventsWithReminders();

            for (Event event : reminderEvents) {
                sEventIds.add(event.getId());
            }
        }
    }

    public static void setAlarmAll(Context context, boolean isOn) {
        for (UUID id : sEventIds) {
            String title = EventData.get(context).getEvent(id).getTitle();
            Intent intent = AlarmService.newIntent(context);
            intent.putExtra(EXTRA_EVENT_ID, id);
            intent.putExtra(EXTRA_REMINDER_TEXT, title);
            PendingIntent pi = PendingIntent.getService(context, id.hashCode(), intent, 0);

            AlarmManager alarmManager = (AlarmManager)
                    context.getSystemService(Context.ALARM_SERVICE);

            if (isOn) {
                Event event = EventData.get(context).getEvent(id);
                Date date = event.getReminderDate();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
            } else {
                alarmManager.cancel(pi);
                pi.cancel();
            }
        }
    }

    public static void setAlarmById(Context context, boolean isOn, UUID id) {
        if (sEventIds == null) {
            sEventIds = new ArrayList<>();
        }
        if (isOn) {
            sEventIds.add(id);
            if (!QueryPreferences.getStoredRemindersEnabled(context)) {
                return;
            }
        }
        String title = EventData.get(context).getEvent(id).getTitle();
        Intent intent = AlarmService.newIntent(context);
        intent.putExtra(EXTRA_EVENT_ID, id);
        intent.putExtra(EXTRA_REMINDER_TEXT, title);
        PendingIntent pi = PendingIntent.getService(context, id.hashCode(), intent, 0);

        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {
            Event event = EventData.get(context).getEvent(id);
            Date date = event.getReminderDate();
            Log.i(TAG, "Reminder Date: " + date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
            sEventIds.remove(id);
        }
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
