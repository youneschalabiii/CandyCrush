    package com.example.candycrush;

    import android.content.ContentValues;
    import android.content.Context;
    import android.database.Cursor;
    import android.database.sqlite.SQLiteDatabase;
    import android.database.sqlite.SQLiteOpenHelper;

    public class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "candycrush.db";
        private static final int DATABASE_VERSION = 1;

        public static final String TABLE_SCORES = "scores";
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String THEME = "theme";
        public static final String TIME = "time";
        public static final String SCORE = "score";

        private static final String TABLE_CREATE =
                "CREATE TABLE " + TABLE_SCORES + " (" +
                        ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        NAME + " TEXT, " +
                        THEME + " TEXT, " +
                        TIME + " INTEGER, " +
                        SCORE + " INTEGER" + ")";

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
            onCreate(db);
        }

            public void insertScore(String name, String theme, int time, int score) {
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(NAME, name);
                values.put(THEME, theme);
                values.put(TIME, time);
                values.put(SCORE, score);
                db.insert(TABLE_SCORES, null, values);
            }

        public Cursor getAllScores() {
            SQLiteDatabase db = this.getReadableDatabase();
            return db.rawQuery("SELECT id AS _id, name, theme, time, score FROM " + TABLE_SCORES + " ORDER BY _id DESC ", null);
        }
    }
