package com.netwokz.flatratetracker;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "BDHelper";
    //The Android's default system path of your application database.
    private static String DB_PATH = "";
    private static String DB_NAME = "vehicle-db";
    public static final String KEY_ID = "_id";
    public static final String KEY_TABLE = "VehicleModelYear";
    public static final String KEY_YEAR = "year";
    public static final String KEY_MAKE = "make";
    public static final String KEY_MODEL = "model";
    private SQLiteDatabase myDataBase;
    private SQLiteDatabase mDB;
    private final Context mContext;

    public ArrayList<String> tYears = new ArrayList<String>();
    public ArrayList<String> tMakes = new ArrayList<String>();
    public ArrayList<String> tModels = new ArrayList<String>();

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     *
     * @param context takes a context.
     */
    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
        if (android.os.Build.VERSION.SDK_INT >= 4.2) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        this.mContext = context;
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if (dbExist) {
            //do nothing - database already exist
        } else {

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();
            this.close();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        if (checkDB != null) {
            checkDB.close();
        }

        return checkDB != null ? true : false;
    }

    public boolean dbExists() {
        return checkDataBase();
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    private void copyDataBase() throws IOException {

        //Open your local db as the input stream
        InputStream myInput = mContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {
        if (mDB != null)
            mDB.close();
        super.close();
    }

    public DBHelper openDB() throws SQLException {
//        this.openDataBase();
        mDB = this.getReadableDatabase();
//        this.close();
        return this;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<String> getYears() {
        String[] colums = new String[]{KEY_YEAR};
        ArrayList<String> mYears = new ArrayList<String>();
        Cursor cur = mDB.query(true, KEY_TABLE, colums, null, null, null, null, null, null);
        if (cur.moveToFirst()) {
            do {
                mYears.add(cur.getString(cur.getColumnIndex(KEY_YEAR)).trim());
            } while (cur.moveToNext());
        }

        tYears = mYears;

        HashSet<String> mHashSet = new HashSet<String>();
        mHashSet.addAll(mYears);
        mYears.clear();
        mYears.addAll(mHashSet);
        Collections.sort(mYears);
        Collections.reverse(mYears);
        return mYears;
    }

    public ArrayList<String> getMakes(int year) {
        String[] columns = new String[]{KEY_YEAR, KEY_MAKE};
        ArrayList<String> mMakes = new ArrayList<String>();
        Cursor cur = mDB.query(true, KEY_TABLE, columns, null, null, null, null, null, null);
        if (cur.moveToFirst()) {
            do {
                tMakes.add(cur.getString(cur.getColumnIndex(KEY_MAKE)).trim());
                int mYear = Integer.parseInt(cur.getString(cur.getColumnIndex(KEY_YEAR)).trim());
                if (mYear == year)
                    mMakes.add(cur.getString(cur.getColumnIndex(KEY_MAKE)).trim());
            } while (cur.moveToNext());
        }

        HashSet<String> mHashSet = new HashSet<String>();
        mHashSet.addAll(mMakes);
        mMakes.clear();
        mMakes.addAll(mHashSet);
        Collections.sort(mMakes);
        return mMakes;
    }

    public ArrayList<String> getModels(int year, String make) {
        String[] colums = new String[]{KEY_YEAR, KEY_MAKE, KEY_MODEL};
        ArrayList<String> mModels = new ArrayList<String>();
        Cursor cur = mDB.query(true, KEY_TABLE, colums, null, null, null, null, null, null);

        if (cur.moveToFirst()) {
            do {
                tModels.add(cur.getString(cur.getColumnIndex(KEY_MODEL)).trim());

                int mYear = Integer.parseInt(cur.getString(cur.getColumnIndex(KEY_YEAR)).trim());
                if (mYear == year) {
                    String mMake = cur.getString(cur.getColumnIndex(KEY_MAKE)).trim();
                    if (mMake.equalsIgnoreCase(make))
                        mModels.add(cur.getString(cur.getColumnIndex(KEY_MODEL)).trim());
                }
            } while (cur.moveToNext());
        }

        HashSet<String> mHashSet = new HashSet<String>();
        mHashSet.addAll(mModels);
        mModels.clear();
        mModels.addAll(mHashSet);
        Collections.sort(mModels);

        copyDataToSD(tYears, tMakes, tModels);

        return mModels;
    }

    public void copyDataToSD(ArrayList<String> array1, ArrayList<String> array2, ArrayList<String> array3) {
        File file = new File(Environment.getExternalStorageDirectory() + "/database.txt");
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (out != null) {
            for (int i = 0; i < array1.size(); i++) {
                out.println(array1.get(i) + "   " + array2.get(i) + "   " + array3.get(i));
            }
            out.close();
        }
    }
}
