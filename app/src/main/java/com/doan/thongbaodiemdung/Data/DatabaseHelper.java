package com.doan.thongbaodiemdung.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by vthha on 4/5/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "data.db";
    public static final int DATA_VERSION = 2;
    private SQLiteDatabase database;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATA_VERSION);
    }

    public static final String TABLE_ROUTE = "tb_route";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_INFO = "info";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ISENABLE = "isEnable";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String COLUMN_DISTORING = "disToRing";
    public static final String COLUMN_RINGTONE = "ringtone";
    public static final String COLUMN_RINGTONEPATH = "ringtonePath";

    @Override
    public void onCreate(SQLiteDatabase db) {
        String script = "CREATE TABLE " + TABLE_ROUTE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_LATITUDE + " DOUBLE, "
                + COLUMN_LONGITUDE + " DOUBLE, "
                + COLUMN_INFO + " TEXT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_ISENABLE + " INTEGER, "
                + COLUMN_DISTANCE + " DOUBLE, "
                + COLUMN_DISTORING + " INT, "
                + COLUMN_RINGTONE + " TEXT, "
                + COLUMN_RINGTONEPATH + " TEXT)";
        db.execSQL(script);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTE);
        onCreate(db);
    }

    public void openToWrite() {
        try {
            database = this.getWritableDatabase();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void openToRead() {
        try {
            database = this.getReadableDatabase();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void close() {
        if(database != null && database.isOpen()) {
            try {
                database.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Cursor getAll(String sql) {
        openToRead();
        Cursor cursor = database.rawQuery(sql, null);
        if(cursor != null) {
            cursor.moveToFirst();
        }
        close();
        return cursor;
    }

    public long insert(ContentValues values) {
        openToWrite();
        long index = database.insert(TABLE_ROUTE, null, values);
        close();
        return index;
    }

    public boolean update(ContentValues values, String where) {
        openToWrite();
        long index = database.update(TABLE_ROUTE, values, where, null);
        close();
        return index > 0;
    }

    public boolean delete(String where) {
        openToWrite();
        long index = database.delete(TABLE_ROUTE, where, null);
        close();
        return index > 0;
    }

    //convert route to values
    private ContentValues routeToValues(Route route) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_INFO, route.getInfo());
        contentValues.put(COLUMN_NAME, route.getName());
        contentValues.put(COLUMN_LATITUDE, route.getLatitude());
        contentValues.put(COLUMN_LONGITUDE, route.getLongitude());
        contentValues.put(COLUMN_ISENABLE, route.getIsEnable());
        contentValues.put(COLUMN_DISTANCE, route.getDistance());
        contentValues.put(COLUMN_DISTORING, route.getMinDistance());
        contentValues.put(COLUMN_RINGTONE, route.getRingtone());
        contentValues.put(COLUMN_RINGTONEPATH, route.getRingtonePath());
        return contentValues;
    }

    //convert cursor to route
    private Route cursorToRoute(Cursor cursor) {
        try {
            Route route = new Route();
            route.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)))
                    .setInfo(cursor.getString(cursor.getColumnIndex(COLUMN_INFO)))
                    .setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)))
                    .setLatitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE)))
                    .setLongitude(cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE)))
                    .setIsEnable(cursor.getInt(cursor.getColumnIndex(COLUMN_ISENABLE)))
                    .setDistance(cursor.getDouble(cursor.getColumnIndex(COLUMN_DISTANCE)))
                    .setMinDistance(cursor.getInt(cursor.getColumnIndex(COLUMN_DISTORING)))
                    .setRingtone(cursor.getString(cursor.getColumnIndex(COLUMN_RINGTONE)))
                    .setRingtonePath(cursor.getString(cursor.getColumnIndex(COLUMN_RINGTONEPATH)));
            return route;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;

    }

    //get route by sql command
    public Route getRoute(String sql) {
        Route route = null;
        Cursor cursor = getAll(sql);
        if(cursor != null) {
            route = cursorToRoute(cursor);
            cursor.close();
        }
        return route;
    }

    //get all route by sql command
    public ArrayList<Route> getListRoute(String sql) {
        ArrayList<Route> routes = new ArrayList<>();
        Cursor cursor = getAll(sql);

        while (!cursor.isAfterLast()) {
            routes.add(cursorToRoute(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return routes;
    }

    //insert route to table
    //return id of route
    public long insertRoute(Route route) {
        return insert(routeToValues(route));
    }

    //update route
    public boolean updateRoute(Route route) {
        return update(routeToValues(route), COLUMN_ID + " = " + route.getId());
    }

    //delete route
    public boolean deleteRoute(String where) {
        return delete(where);
    }
}
