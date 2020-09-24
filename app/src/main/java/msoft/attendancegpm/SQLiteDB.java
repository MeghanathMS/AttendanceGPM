package msoft.attendancegpm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDB extends SQLiteOpenHelper {
    Context  context;
    public static final String DBName="Attendance.db";

    public SQLiteDB(Context context){
        super(context,DBName,null,1);
        this.context=context;

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE User (StaffId string)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS User");
        onCreate(db);
    }

    public  void saveUser(String StaffId)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("StaffId",StaffId);
        db.insert("User",null,contentValues);
    }

    public String isAvailable()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM User",null);
        int records=cursor.getCount();
        if(records>0)
        {
            cursor.moveToFirst();
            return  cursor.getString(0);
        }
        else
        {
            return "-1";
        }
    }
    public void deleteUser()
    {
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("DELETE from  User");
    }


}
