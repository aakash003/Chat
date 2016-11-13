package com.sash.chat.toolbox;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Aakash on 26-06-2016.
 */
public class StorageManipulater extends SQLiteOpenHelper {


    public static final String Database_name="AndroidChatter.db";
    public static final int Database_version = 1;

    public static final String ID="id";
    public static final String Table_name_message="table_message";
    public static final String Message_receiver="Receiver";
    public static final String Message_sender="Sender";
    public static final String Message_message="Message";

    private static final String TABLE_MESSAGE_CREATE
            = "CREATE TABLE " + Table_name_message
            + " (" +ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Message_receiver + " VARCHAR(25), "
            + Message_sender + " VARCHAR(25) ";

    public static final String TABLE_MESSAGE_DROP= "DROP TABLE IF EXISTS" + Table_name_message;

    public StorageManipulater(Context context) {
        super(context, Database_name, null, Database_version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_MESSAGE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(TABLE_MESSAGE_DROP);
    }

    public void insert(String Sender ,String Receiver,String Message){
        long rowID;
        try{
            SQLiteDatabase db= getWritableDatabase();
            ContentValues contentValues= new ContentValues();
            contentValues.put(Message_receiver,Receiver);
            contentValues.put(Message_sender,Sender);
            contentValues.put(Message_message,Message);
            rowID=db.insert(Table_name_message,null,contentValues);
        }
        catch (Exception e){

        }
    }



    public Cursor get(String sender,String receiver){

        SQLiteDatabase db=getWritableDatabase();
        String selectQuery= "SELECT * FROM" + Table_name_message + "WHERE" + Message_sender + "LIKE" + sender + "AND" + Message_receiver + "LIKE" + receiver + "ORDER BY" + ID + "ASC";
        return null;
    }


}
