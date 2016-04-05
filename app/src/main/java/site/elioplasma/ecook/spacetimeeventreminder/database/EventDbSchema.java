package site.elioplasma.ecook.spacetimeeventreminder.database;

/**
 * Created by eli on 3/26/16.
 */
public class EventDbSchema {
    public static final class EventTable {
        public static final String NAME = "events";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String CUSTOM = "custom";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String DESCRIPTION = "description";
            public static final String REMINDER_TIME_AMOUNT = "reminder_time_amount";
            public static final String REMINDER_TIME_UNIT = "reminder_time_unit";
            public static final String REMINDER_ON = "reminder_on";
        }
    }
}
