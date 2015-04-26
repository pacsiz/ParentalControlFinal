package hu.uniobuda.nik.parentalcontrol.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "parentalcontrol";
    private static final String TABLE_BLOCKED_APPS = "blocked_apps";
    private static final String TABLE_PERSONS = "persons";
    private static final String TABLE_SETTINGS = "settings";
    private static final String TABLE_PERSONAL_SETTINGS = "personal_settings";

    private static final String KEY_ID = "id";
    private static final String KEY_PERSONNAME = "personname";
    private static final String KEY_PERSONID = "personid";

    //Blocked apps table
    private static final String KEY_PACKAGENAME = "packagename";

    //settings Table - column names
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password";
    private static final String COL_FACE_REC_ENABLED = "facerecenabled";
    private static final String COL_ACCESS_CONTROL = "accesscontrol";

    //personal settings TAGS Table - column names
    private static final String COL_ACCESS_CONTROL_FOR_PERSON = "accesscontrolforperson";
    private static final String COL_FROM_TIME = "fromtime";
    private static final String COL_TO_TIME = "totime";
    private static final String COL_SELECTED_DAYS = "selecteddays";


    private static final String CREATE_TABLE_BLOCKEDAPPS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_BLOCKED_APPS + "(" + KEY_PACKAGENAME + " TEXT PRIMARY KEY," + KEY_PERSONID
            + " TEXT" + ")";

    private static final String CREATE_TABLE_PERSONS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_PERSONS + "(" + KEY_PERSONID + " INTEGER PRIMARY KEY," + KEY_PERSONNAME
            + " TEXT" + ")";

    private static final String CREATE_TABLE_SETTINGS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_SETTINGS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + COL_EMAIL
            + " TEXT," + COL_PASSWORD+" TEXT,"+COL_FACE_REC_ENABLED+" INTEGER,"
            +COL_ACCESS_CONTROL+" INTEGER"+")";

    private static final String CREATE_TABLE_PERSONAL_SETTINGS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_PERSONAL_SETTINGS + "(" + KEY_PERSONNAME + " TEXT PRIMARY KEY," + COL_ACCESS_CONTROL_FOR_PERSON
            + " INTEGER," + COL_FROM_TIME+" TEXT,"+COL_TO_TIME+" TEXT,"
            +COL_SELECTED_DAYS+" TEXT"+")";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    public void removeFromBlockedApps(String packageName, String personName)
    {

    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_BLOCKEDAPPS);
        sqLiteDatabase.execSQL(CREATE_TABLE_PERSONS);
        sqLiteDatabase.execSQL(CREATE_TABLE_SETTINGS);
        sqLiteDatabase.execSQL(CREATE_TABLE_PERSONAL_SETTINGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_BLOCKED_APPS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_PERSONS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_SETTINGS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_PERSONAL_SETTINGS);

        onCreate(sqLiteDatabase);
    }


}

