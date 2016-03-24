package site.elioplasma.ecook.spacetimeeventreminder;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by eli on 3/10/16.
 */
public class AlarmService extends IntentService {

    private static final String TAG = "AlarmService";

    public static Intent newIntent(Context context) {
        return new Intent(context, AlarmService.class);
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        List<Event> eventList = EventData.get(context).getEvents();
        for (Event event : eventList) {
            if (event.isReminderOn()) {
                Intent i = AlarmService.newIntent(context);
                PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

                AlarmManager alarmManager = (AlarmManager)
                        context.getSystemService(Context.ALARM_SERVICE);

                if (isOn) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(event.getReminderDate());
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
                } else {
                    alarmManager.cancel(pi);
                    pi.cancel();
                }
            }
        }
    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent i = AlarmService.newIntent(context);
        PendingIntent pi = PendingIntent
                .getService(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    public AlarmService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Resources resources = getResources();
        Intent i = EventFragment.newIntent(this);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setTicker(resources.getString(R.string.event_reminder_title))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(resources.getString(R.string.event_reminder_title))
                .setContentText(resources.getString(R.string.event_reminder_text))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);
        notificationManager.notify(0, notification);
    }
}
