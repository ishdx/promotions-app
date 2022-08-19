package com.example.project483;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.project483.modals.LocationModal;
import com.example.project483.modals.UserModel;
import com.example.project483.service.GPSTracker;

public class  DatabaseHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_NAME = "UserManager.db";

    //USERS TABLE
    private static final String TABLE_USER = "user";

    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_NAME = "user_name";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_USER_PASSWORD = "user_password";
    private static final String COLUMN_IS_RETAILER = "is_retailer"; // 0 user, 1 retailer

    private String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_NAME + " TEXT,"
            + COLUMN_USER_EMAIL + " TEXT," + COLUMN_USER_PASSWORD + " TEXT, " + COLUMN_IS_RETAILER + " INTEGER" + ")";

    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;

    //CAMPAIGNS TABLE
    private static final String TABLE_CAMPAIGN = "campaigns";

    private static final String COLUMN_TITLE = "campaign_title";
    private static final String COLUMN_DESCRIPTION = "campaign_description";
    private static final String COLUMN_CAMPAIGN_ID = "campaign_id";
    private static final String COLUMN_CAMPAIGN_LATITUDE = "campaign_lat";
    private static final String COLUMN_CAMPAIGN_LONGITUDE = "campaign_long";



    private String CREATE_CAMPAIGN_TABLE = "CREATE TABLE " + TABLE_CAMPAIGN + "("
            + COLUMN_CAMPAIGN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_TITLE + " TEXT," + COLUMN_DESCRIPTION + " TEXT," + COLUMN_CAMPAIGN_LATITUDE + " TEXT," +
            COLUMN_CAMPAIGN_LONGITUDE + " TEXT" + ")";

    private String DROP_CAMPAIGN_TABLE = "DROP TABLE IF EXISTS " + TABLE_CAMPAIGN;

        //SUBSCRIBER TABLE
    private static final String TABLE_SUBSCRIBER = "subscriber";

    private static final String COLUMN_SUBSCRIBER_ID = "subscriber_id";
    private static final String COLUMN_CAMPAIGN_SUBSCRIBER_ID = "campaign_subscriber_id";



    private String CREATE_SUBSCRIBER_TABLE = "CREATE TABLE " + TABLE_SUBSCRIBER + "("
            + COLUMN_SUBSCRIBER_ID + " TEXT," + COLUMN_CAMPAIGN_SUBSCRIBER_ID + " TEXT" + ")";

    private String DROP_SUBSCRIBER_TABLE = "DROP TABLE IF EXISTS " + TABLE_SUBSCRIBER;






    //Methods
    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_CAMPAIGN_TABLE);
        db.execSQL(CREATE_SUBSCRIBER_TABLE);
    }

    @Override
    public  void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(DROP_USER_TABLE);
        db.execSQL(DROP_CAMPAIGN_TABLE);
        db.execSQL(DROP_SUBSCRIBER_TABLE);
        onCreate(db);
    }

    //Users
    public void addUser(UserModel user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        values.put(String.valueOf(COLUMN_IS_RETAILER), user.getRetailer());

        db.insert(TABLE_USER, null, values);
        db.close();

        checkUser(user.getEmail());
    }

    public boolean checkUser(String email){
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = COLUMN_USER_EMAIL + " = ?";
        String[] selectionArgs = { email };

        Cursor cursor = db.query(TABLE_USER,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);
        int cursorCount = cursor.getCount();

        if (cursorCount > 0){
            cursor.moveToFirst();
            GPSTracker.currentUserId=cursor.getInt(0);
            return true;
        }
        cursor.close();
        db.close();
        return false;
    }

    public boolean checkUser(String email, String password, int isRetailer){
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = COLUMN_USER_EMAIL + " = ?" + " AND " + COLUMN_USER_PASSWORD + " =?"+ " AND " + COLUMN_IS_RETAILER + " =?";
        String[] selectionArgs = { email, password, String.valueOf(isRetailer)};

        Cursor cursor = db.query(TABLE_USER,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        if (cursorCount > 0){
            checkUser(email);
            return true;
        }
        return false;
    }

    //Campaigns
    public void addCampaign(String title, String desc, LocationModal locationModal) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, title);
        contentValues.put(COLUMN_DESCRIPTION, desc);
        contentValues.put(COLUMN_CAMPAIGN_LATITUDE, locationModal.getLatitude());
        contentValues.put(COLUMN_CAMPAIGN_LONGITUDE, locationModal.getLongitude());

        db.insert(TABLE_CAMPAIGN, null, contentValues);
        db.close();

    }
    //Subscription
    public void addSubscript(int userId, int campaignSubscriberId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SUBSCRIBER_ID, userId);
        contentValues.put(COLUMN_CAMPAIGN_SUBSCRIBER_ID, campaignSubscriberId);

        db.insert(TABLE_SUBSCRIBER, null, contentValues);
        db.close();
    }


    public Cursor getCampaigns(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select " + COLUMN_CAMPAIGN_ID + ", " + COLUMN_TITLE + ", " + COLUMN_DESCRIPTION + ", " + COLUMN_CAMPAIGN_LATITUDE + ", " + COLUMN_CAMPAIGN_LONGITUDE + " from " + TABLE_CAMPAIGN;
        Cursor campaigns = db.rawQuery(query, null);
        return campaigns;
    }

    public Cursor getSubscribe(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select *" + " from " + TABLE_SUBSCRIBER;
        Cursor campaigns = db.rawQuery(query, null);
        return campaigns;
    }

}