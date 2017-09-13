package localStorage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import Bean.MainActivityVo;

import static android.os.Build.ID;

/**
 * Created by zithas on 16/8/17.
 */

public class DataBaseHelper  extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    //database name
    private static final String DATABASE_NAME = "Rush";

    //table name
    private static final String TABLE_ADDRESS = "Address_Table";

    //Adress  table feild
    private static final String ID = "id";
    private static final String TIME = "Time";
    private static final String ADDRESS = "Address";


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_ADDRESS_TABLE = "CREATE TABLE " + TABLE_ADDRESS + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + TIME + " TEXT," + ADDRESS + " TEXT " + ")";
        db.execSQL(CREATE_ADDRESS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADDRESS);

    }

    //Insert Date
    public void InsertData(MainActivityVo mainActivityVo) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(TIME, mainActivityVo.getTime());
        contentValues.put(ADDRESS, mainActivityVo.getAddress());
        db.insert(TABLE_ADDRESS, null, contentValues);
        db.close();
        Log.e("contentValues", "" + contentValues);
    }

    //Get all technicianIssue data
    public Cursor GetData() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_ADDRESS, new String[]{ID, TIME, ADDRESS},
                null, null, null, null, null);
    }


/*    //Get FavList
    public List<MainActivityVo> getFavList(){
        String selectQuery = "SELECT  * FROM " + TABLE_ADDRESS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<MainActivityVo> FavList = new ArrayList<MainActivityVo>();
        if (cursor.moveToFirst()) {
            do {
                MainActivityVo mainActivityVo = new MainActivityVo();
                mainActivityVo.setTime(cursor.getString(1));
                mainActivityVo.setAddress(cursor.getString(2));
                FavList.add(mainActivityVo);
            } while (cursor.moveToNext());
        }
        return FavList;
    }
    */

    public String getTableAsString() {
        //  Log.e("getTableAsString", "getTableAsString called"+getTableAsString());
        SQLiteDatabase db = this.getReadableDatabase();
        String tableString = String.format("Table %s:\n", TABLE_ADDRESS);
        Cursor allRows = db.rawQuery("SELECT * FROM " + TABLE_ADDRESS, null);

        if (allRows.moveToFirst()) {
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name : columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name)));

                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }
        allRows.close();
        return tableString;
    }

}
