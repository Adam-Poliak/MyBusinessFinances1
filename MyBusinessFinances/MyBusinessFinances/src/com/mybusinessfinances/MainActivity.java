package com.mybusinessfinances;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
	
	private ListView expensesList;
	protected static ArrayList<ExpenseItem> expenses; 
	protected static ArrayAdapter<ExpenseItem> aa;
	
	protected static ExpensedbAdaptor dbAdapt;
	private static Cursor eCursor; //expense table cursor

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		expensesList = (ListView) findViewById(R.id.ExpenseList);
		
		expenses = new ArrayList<ExpenseItem>();
		
		aa = new ArrayAdapter<ExpenseItem>(this, android.R.layout.simple_list_item_1, expenses);
		expensesList.setAdapter(aa);
		populateList();
		updateArray();
		
		
	}

	private void updateArray() {
		eCursor = dbAdapt.getAllExpenses();
		expenses.clear();
		
		if(eCursor.moveToFirst()) {
			do {
				ExpenseItem result = new ExpenseItem(eCursor.getString(1), eCursor.getDouble(2));
				expenses.add(0, result);
			} while (eCursor.moveToNext());
			aa.notifyDataSetChanged();
		}
		
	}

	private void populateList() {
		eCursor = dbAdapt.getAllExpenses();
		updateArray();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
}
