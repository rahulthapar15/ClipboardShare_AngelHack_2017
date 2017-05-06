package com.sharedclipboard.storage.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sharedclipboard.SharedClipperApp;
import com.sharedclipboard.storage.db.models.Clipping;

public class DBHelper extends SQLiteOpenHelper {
    public static final String TAG = SharedClipperApp.getTag("DbHelper");
    public DBHelper(Context context) {
        super(context, DBConstants.DATABASE_NAME, null, DBConstants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
        SharedClipperApp.printTrace(TAG, "onCreate DB");
    }

    private void createTables(SQLiteDatabase db) {
        for (DBTable table : DBConstants.DB_TABLES){
            db.execSQL(table.getCreateQuery());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTables(db);
        createTables(db);
        SharedClipperApp.printTrace(TAG, "onUpgrade DB");
    }

    private void dropTables(SQLiteDatabase db) {
        for (DBTable table : DBConstants.DB_TABLES){
            db.execSQL(table.getDropQuery());
        }
    }

    protected long insert(String tableName, ContentValues values ){
        SQLiteDatabase writableDB = getWritableDatabase();
        long rowId = -1 ;
        synchronized (writableDB) {
            rowId  = writableDB.insert(tableName, null, values);
        }
        return rowId ;
    }
    protected long delete(String tableName, String whereClause, String[] selectionArgs){
        SQLiteDatabase writableDB = getWritableDatabase();
        int updatedRows = 0 ;
        synchronized (writableDB) {
            updatedRows = writableDB.delete(tableName, whereClause, selectionArgs);
        }
        return updatedRows ;
    }
    protected Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupby, String having, String orderBy){
        SQLiteDatabase redableDB = getReadableDatabase();
        Cursor resultCursor = null ;
        synchronized (redableDB) {
            resultCursor = redableDB.query(table, columns, selection, selectionArgs, groupby, having, orderBy);
        }
        return resultCursor ;
    }
    protected int update(String tableName, ContentValues values, String whereClause, String[] whereArgs){
        SQLiteDatabase writableDB = getWritableDatabase();
        int updatedRows = 0 ;
        synchronized (writableDB) {
            updatedRows = writableDB.update(tableName, values, whereClause, whereArgs);
        }
        return updatedRows ;
    }

    public long insertClipping(Clipping newClipping){
        long result = -1;
        if(newClipping!=null) {
            ContentValues values = newClipping.toContentValues();
            result = insert(ClippingContract.ClippingsTable.TABLE.getTableName(), values);
        }
        return result;
    }
    public int updateClipping(Clipping Clipping){
        int result = 0 ;
        long ClippingId = Clipping.getId();
        if(ClippingId > 0 ){
            String selection = ClippingContract.ClippingsTable._ID +  " = ? " ;
            String[] selectionArgs = {String.valueOf(ClippingId)};
            result = update(ClippingContract.ClippingsTable.TABLE.getTableName(), Clipping.toContentValues(), selection, selectionArgs);
        }
        return result;
    }
    public Cursor getClipping(long ClippingId){
        String selection = ClippingContract.ClippingsTable._ID +  " = ? " ;
        String[] selectionArgs = {String.valueOf(ClippingId)};
        Cursor result =  query(ClippingContract.ClippingsTable.TABLE.getTableName(), null, selection, selectionArgs, null, null, null);
        return result.moveToNext() ? result : null ;

    }
    public long deleteClipping(long ClippingId){
        String selection = ClippingContract.ClippingsTable._ID +  " = ? " ;
        String[] selectionArgs = {String.valueOf(ClippingId)};
        return delete(ClippingContract.ClippingsTable.TABLE.getTableName(), selection, selectionArgs);
    }

    public Cursor getClippingAll(boolean isDateLatest){
        String orderBy = ClippingContract.ClippingsTable.COLUMN_DATE_TIME + (isDateLatest ?  " DESC" : " ASC" );
        return query(ClippingContract.ClippingsTable.TABLE.getTableName(), null, null, null, null, null, orderBy);
    }
    public void resetDbValues(){
        for (DBTable table : DBConstants.DB_TABLES){
            delete(table.getTableName(),null,null);
        }
    }

}
