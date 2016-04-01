package site.elioplasma.ecook.spacetimeeventreminder.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import site.elioplasma.ecook.spacetimeeventreminder.database.EventDbSchema.EventTable;
import site.elioplasma.ecook.spacetimeeventreminder.database.EventDbSchema.SettingTable;

/**
 * Created by eli on 3/26/16.
 */
public class EventBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "eventBase.db";

    public EventBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + EventTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                EventTable.Cols.UUID + ", " +
                EventTable.Cols.CUSTOM + ", " +
                EventTable.Cols.TITLE + ", " +
                EventTable.Cols.DATE + ", " +
                EventTable.Cols.DESCRIPTION + ", " +
                EventTable.Cols.REMINDER_TIME_AMOUNT + ", " +
                EventTable.Cols.REMINDER_TIME_UNIT + ", " +
                EventTable.Cols.REMINDER_ON +
                ")"
        );

        db.execSQL("create table " + SettingTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                SettingTable.Cols.REMINDERS_ENABLED + ", " +
                SettingTable.Cols.FILTER_BY_REMINDERS + ", " +
                SettingTable.Cols.FILTER_BY_CUSTOM +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //
    }
}
