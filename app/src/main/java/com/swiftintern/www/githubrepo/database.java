package com.swiftintern.www.githubrepo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by MUKESH on 10-Oct-15.
 */
public class database extends SQLiteOpenHelper {

    final static String Table_name = "Follow_table";
    final static String Database_name = "Follow_database.db";
    final static String Col1 = "Serial_Number";
    final static String Col2 = "User_ID" ;

    public database(Context context) {
        super(context, Database_name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String execute = "create table " + Table_name + " ( " + Col1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + Col2 + " TEXT UNIQUE );";
        Log.v("database", execute);
        db.execSQL(execute);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + Table_name);
        onCreate(db);
    }

    public  boolean onAdd( String id ){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col2, id);
        if( db.insert(Table_name, null, contentValues ) == -1 ){
            return false;
        }
        else{
            return true;
        }

    }

    public Cursor onShow( ){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + Table_name, null );
        return res;
    }

    public boolean isthere( String userid ){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select "+ Col2 + " from " + Table_name + " where (" + Col2 + " = '" + userid + "' )" , null );
        Log.v( "Database.isthere()" , "select "+ Col2 + " from " + Table_name + " where (" + Col2 + " = '" + userid + "' )");
        boolean b = res.moveToFirst();
        res.close();
        db.close();
        return b;
    }

    public boolean update( String serialno, String userid ){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col1, serialno);
        contentValues.put(Col2, userid );
        if ( db.update(Table_name, contentValues, "Serial_Number = ?", new String[] {serialno} ) == 0 ){
            return false;
        }

        return true;
    }

    public boolean delete( String userid ){
        SQLiteDatabase db = this.getWritableDatabase();
        int num = db.delete(Table_name, "User_ID = ?", new String[] {userid});
        if(num==0){
            return false;
        }
        return true;
    }

}
