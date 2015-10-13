package com.fluxinated.mixins.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.fluxinated.mixins.R;
import com.fluxinated.mixins.model.CardInformation;
import com.fluxinated.mixins.model.Liquor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by flux on 7/17/15.
 */
public class DB
{
   private  DBhelper mDbHelper;

    public DB()
    {

        mDbHelper = DBhelper.getInstance();
    }

    public boolean insert(Object... params)
    {
        boolean isSuccess = false;
        SQLiteDatabase db = mDbHelper.openToWriteDB();
        ContentValues mContentValues = new ContentValues();
        try{
            //for (Object param : params)
            for (int i = 0; i<params.length;i++)
            {
                //if (param instanceof JSONObject)
                //{
                    mContentValues.put(DBhelper.mTableColumns[i + 1], params[i].toString());

                    Log.i("SUCCESS","SUCC");
                //}
            }
           // db.setTransactionSuccessful();
            db.insertOrThrow(DBhelper.mTableName, null, mContentValues);
            isSuccess = true;
        }
        catch(Exception exp)
        {

            exp.printStackTrace();
        }
        finally
        {
            mDbHelper.closeDB();
        }

        return isSuccess;
    }

    public void  delete(Object... params)
    {
        SQLiteDatabase db = mDbHelper.openToWriteDB();
        try
        {
            //if(params[0] instanceof  JSONObject)
                //db.delete(DBhelper.mTableName,DBhelper.mTableColumns[1]+" like '%" + params[0].toString() + "%'; ",null);
            db.delete(DBhelper.mTableName,DBhelper.mTableColumns[0] +" =" +params[0].toString() +";" ,null);
           // db.delete(DBhelper.mTableName,DBhelper.mTableColumns[0] ,new String[]{params[0].toString()});

        }
        catch (Exception exp)
        {
            Toast.makeText(MyApplication.getAppContext(),"Failed to delete liquor", Toast.LENGTH_SHORT).show();
            exp.printStackTrace();
        }finally
        {
            mDbHelper.closeDB();
        }

    }

    public void select(int offset, ArrayList<CardInformation> cardInformations,String args)
    {
        SQLiteDatabase db = mDbHelper.openToReadDB();
        //Cursor cursor = db.query(DBhelper.mTableName, DBhelper.mTableColumns, null, null, null, null, null, "LIMIT 10 OFFSET 10");
        //Log.i("String query","Select * from " + DBhelper.mTableName + " WHERE "+getDBColumns()[1] +" LIKE '%"+args+"%' OR WHERE "+getDBColumns()[2]+" LIKE '%"+args+"%' LIMIT 10 OFFSET " + offset);
        Cursor cursor;
        if(args == null)
             cursor =  db.rawQuery("Select * from " + DBhelper.mTableName +" LIMIT 10 OFFSET " + offset,null);
        else
            cursor = db.rawQuery("Select * from " + DBhelper.mTableName + " WHERE "+getDBColumns()[1] +" LIKE '%"+args+"%' OR "+getDBColumns()[2]+" LIKE '%"+args+"%' LIMIT 10 OFFSET " + offset,null);
        Liquor mTempLiquor;
        CardInformation mTempInfo;
        JSONObject mJsonObject;
        while(cursor.moveToNext())
        {
            try
            {
                mJsonObject = new JSONObject(cursor.getString(cursor.getColumnIndex(this.getDBColumns()[3])));
                mTempLiquor = new Liquor(mJsonObject);
                mTempLiquor.setLiquorId(cursor.getInt(cursor.getColumnIndex(this.getDBColumns()[0])));
                //Log.i("Liquor ID",mTempLiquor.getLiquorId()+"");
                mTempInfo = new CardInformation(mTempLiquor);
                cardInformations.add(mTempInfo);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        cursor.close();
        mDbHelper.closeDB();
    }

    public boolean update(Object... params)
    {
        boolean isSuccess = false;
        SQLiteDatabase db = mDbHelper.openToWriteDB();
        ContentValues mContentValues = new ContentValues();
        int id = 0;
        try
        {
            //for(Object obj:params)
            for(int i = 0; i< params.length ; i++)
            {
                if(params[i] instanceof JSONObject)
                {
                    mContentValues.put(DBhelper.mTableColumns[i], params[i].toString());
                    //db.insertOrThrow(DBhelper.mTableName, null, mContentValues);
                    Log.e("update string",params[i].toString());
                }
                else if(params[i] instanceof Integer)
                {
                    id = (Integer)params[i];
                }
                else
                {
                    mContentValues.put(DBhelper.mTableColumns[i], params[i].toString());
                }

               // else
                //{
                    //db.update(DBhelper.mTableName, mContentValues, DBhelper.mTableColumns[0] + " =" + obj.toString(), null);
                    //Log.e("object string",obj.toString());
                //}
            }
            db.update(DBhelper.mTableName, mContentValues, DBhelper.mTableColumns[0] + " =" + String.valueOf(id), null);

            isSuccess = true;
            //Toast.makeText(MyApplication.getAppContext(),"updated successfully", Toast.LENGTH_SHORT).show();
        }
        catch(Exception e)
        {

            e.printStackTrace();
        }
        finally {
            mDbHelper.closeDB();
        }
        return isSuccess;
    }


    public String[] getDBColumns()
    {
        return DBhelper.mTableColumns;
    }


    public static class DBhelper extends SQLiteOpenHelper
    {
        private static DBhelper dbhelper;
        private static final String mTableName = MyApplication.getAppContext().getResources().getString(R.string.db_table);
        private static final String[] mTableColumns = MyApplication.getAppContext().getResources().getStringArray(R.array.db_columns);
        private static final int mDbVersion =MyApplication.getAppContext().getResources().getInteger(R.integer.db_version);
        private static final  String mCreateTableSQL ="create table if not exists " +  mTableName
                + " (" +  mTableColumns[0] +  " integer primary key autoincrement, "
                + mTableColumns[1]  + " TEXT,"
                + mTableColumns[2]  + " TEXT,"
                + mTableColumns[3]  + " TEXT);";

        private DBhelper(Context context)
        {
            super(context, mTableName,null, mDbVersion);
        }

        private static synchronized DBhelper getInstance()
        {
                if(dbhelper == null)
                {
                    dbhelper = new DBhelper(MyApplication.getAppContext());
                    Log.i("dbhelper", "dbhelper is null");
                }

                return dbhelper;
        }



        private SQLiteDatabase openToWriteDB()
        {
            return this.getWritableDatabase();
        }

        private SQLiteDatabase openToReadDB()
        {
            return this.getReadableDatabase();
        }


        private void closeDB()
        {
             this.close();
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
                db.execSQL(mCreateTableSQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
                db.execSQL("Drop table if exists "+mTableName);
                onCreate(db);
        }
    }

}
