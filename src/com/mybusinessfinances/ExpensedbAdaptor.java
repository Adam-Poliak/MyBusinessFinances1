package com.mybusinessfinances;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ExpensedbAdaptor {
	
	private SQLiteDatabase db;
	private ExpensedbHelper dbHelper;
	private final Context context;
	
	private static final String DB_NAME = "exp.db";
    private static final int DB_VERSION = 1; 
 
    private static final String EXPENSE_TABLE = "expenses";
    public static final String EXP_ID = "exp_id";   // column 0
    public static final String EXP_NAME = "exp_name"; //column 1
    public static final String EXP_COST = "exp_cost"; //Column 2
    public static final String[] EXP_COLS = {EXP_ID, EXP_NAME, EXP_COST};
 
 
    public ExpensedbAdaptor(Context ctx) {
        context = ctx;
        dbHelper = new ExpensedbHelper(context, DB_NAME, null, DB_VERSION);
    }
 
    public void open() throws SQLiteException {
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLiteException ex) {
            db = dbHelper.getReadableDatabase();
        }
    }
 
    public void close() {
        db.close();
    }
     
    // database update methods
     
    public long insertExpense(ExpenseItem exp) {
        // create a new row of values to insert
        ContentValues cvalues = new ContentValues();
        // assign values for each col
        cvalues.put(EXP_NAME, exp.getExpense());
        cvalues.put(EXP_COST, exp.getCost());
        return db.insert(EXPENSE_TABLE, null, cvalues);
    }
     
    // database query methods
    public Cursor getAllExpenses() {
        return db.query(EXPENSE_TABLE, EXP_COLS, null, null, null, null, null);
    }
     
    public Cursor getExpenseCursor(long ri) throws SQLException {
        Cursor result = db.query(true, EXPENSE_TABLE, EXP_COLS, EXP_ID+"="+ri, null, null, null, null, null);
        if ((result.getCount() == 0) || !result.moveToFirst()) {
            throw new SQLException("No course items found for row: " + ri);
        }
        return result;
    }
     
    //Dont think we need to get an individual expense Item for now
  /*  public ExpenseItem getExpenseItem(long ri) throws SQLException {
        Cursor cursor = db.query(true, EXPENSE_TABLE, EXP_COLS, EXP_ID+"="+ri, null, null, null, null, null);
        if ((cursor.getCount() == 0) || !cursor.moveToFirst()) {
            throw new SQLException("No course items found for row: " + ri);
        }
        // must use column indices to get column values
        int whatIndex = cursor.getColumnIndex(EXP_NAME);
        ExpenseItem result = new ExpenseItem(cursor.getString(whatIndex), cursor.getString(2), cursor.getFloat(3), cursor.getString(4));
        return result;
    } */
 
 
    private static class ExpensedbHelper extends SQLiteOpenHelper {
 
        // SQL statement to create a new database
        private static final String DB_CREATE = "CREATE TABLE " + EXPENSE_TABLE
                + " (" + EXP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + EXP_NAME + " TEXT,"
                + EXP_COST + " TEXT );";
 
        public ExpensedbHelper(Context context, String name, CursorFactory fct, int version) {
            super(context, name, fct, version);
        }
 
        @Override
        public void onCreate(SQLiteDatabase adb) {
            // TODO Auto-generated method stub
            adb.execSQL(DB_CREATE);
        }
 
        @Override
        public void onUpgrade(SQLiteDatabase adb, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            Log.w("GPAdb", "upgrading from version " + oldVersion + " to "
                    + newVersion + ", destroying old data");
            // drop old table if it exists, create new one
            // better to migrate existing data into new table
            adb.execSQL("DROP TABLE IF EXISTS " + EXPENSE_TABLE);
            onCreate(adb);
        }
    } // GPAdbHelper class
 
} // GPAdbAdapter class

