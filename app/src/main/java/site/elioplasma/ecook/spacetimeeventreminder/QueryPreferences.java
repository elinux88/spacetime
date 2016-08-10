package site.elioplasma.ecook.spacetimeeventreminder;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by eli on 4/5/16.
 */
public class QueryPreferences {
    private static final String PREF_REMINDERS_ENABLED = "remindersEnabled";
    private static final String PREF_FILTER_BY_REMINDERS = "filterByReminders";
    private static final String PREF_FILTER_BY_CUSTOM = "filterByCustom";
    private static final String PREF_NIGHT_MODE = "nightMode";
    private static final String PREF_DATE_UPDATED = "dateUpdated";

    public static boolean getStoredRemindersEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_REMINDERS_ENABLED, true);
    }

    public static void setStoredRemindersEnabled(Context context, boolean remindersEnabled) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_REMINDERS_ENABLED, remindersEnabled)
                .apply();
    }

    public static boolean getStoredFilterByReminders(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_FILTER_BY_REMINDERS, false);
    }

    public static void setStoredFilterByReminders(Context context, boolean filterByReminders) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_FILTER_BY_REMINDERS, filterByReminders)
                .apply();
    }

    public static boolean getStoredFilterByCustom(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_FILTER_BY_CUSTOM, false);
    }

    public static void setStoredFilterByCustom(Context context, boolean filterByCustom) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_FILTER_BY_CUSTOM, filterByCustom)
                .apply();
    }

    public static boolean getStoredNightMode(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_NIGHT_MODE, false);
    }

    public static void setStoredNightMode(Context context, boolean nightMode) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_NIGHT_MODE, nightMode)
                .apply();
    }

    public static long getStoredDateUpdated(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getLong(PREF_DATE_UPDATED, 0);
    }

    public static void setStoredDateUpdated(Context context, long dateUpdated) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putLong(PREF_DATE_UPDATED, dateUpdated)
                .apply();
    }
}
