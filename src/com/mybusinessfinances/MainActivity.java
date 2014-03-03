package com.mybusinessfinances;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private ListView expenses0;
	private ListView expenses1;
	private ListView expenses2;
	protected static ArrayList<ExpenseItem> expenses;
	protected static ArrayAdapter<ExpenseItem> aa;
	
	protected static ExpensedbAdaptor dbAdapt;
	private static Cursor eCursor; //expense table cursor
	
	private EditText expenseName;
	private EditText expenseCost;
	private String inputCost;
	private String inputExpense;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		populateTabs();
		 
		dbAdapt = new ExpensedbAdaptor(this);
		dbAdapt.open();
		
	
		expenses0 = (ListView) findViewById(R.id.expense_list0);
		expenses1 = (ListView) findViewById(R.id.expense_list1);
		expenses2 = (ListView) findViewById(R.id.expense_list2);
		
		expenses = new ArrayList<ExpenseItem>();
		aa = new ArrayAdapter<ExpenseItem>(this, android.R.layout.simple_list_item_1, expenses);
		expenses0.setAdapter(aa);
		expenses1.setAdapter(aa);
		expenses2.setAdapter(aa);
		populateList();
	}

	private void populateTabs() {
		TabHost tabhost = (TabHost) findViewById(R.id.tabhost);
		tabhost.setup();
		
		final TabWidget tabWidget = tabhost.getTabWidget();
		final FrameLayout tabContent = tabhost.getTabContentView();
		
		// Get the original tab textviews and remove them from the viewgroup.
				TextView[] originalTextViews = new TextView[tabWidget.getTabCount()];
				for (int index = 0; index < tabWidget.getTabCount(); index++) {
					originalTextViews[index] = (TextView) tabWidget.getChildTabViewAt(index);
				}
				tabWidget.removeAllViews();
				
				// Ensure that all tab content childs are not visible at startup.
				for (int index = 0; index < tabContent.getChildCount(); index++) {
					tabContent.getChildAt(index).setVisibility(View.GONE);
				}
				
				// Create the tabspec based on the textview childs in the xml file.
				// Or create simple tabspec instances in any other way...
				for (int index = 0; index < originalTextViews.length; index++) {
					final TextView tabWidgetTextView = originalTextViews[index];
					final View tabContentView = tabContent.getChildAt(index);
					TabSpec tabSpec = tabhost.newTabSpec((String) tabWidgetTextView.getTag());
					tabSpec.setContent(new TabContentFactory() {
						@Override
						public View createTabContent(String tag) {
							return tabContentView;
						}
					});
					if (tabWidgetTextView.getBackground() == null) {
						tabSpec.setIndicator(tabWidgetTextView.getText());
					} else {
						tabSpec.setIndicator(tabWidgetTextView.getText(), tabWidgetTextView.getBackground());
					}
					tabhost.addTab(tabSpec);
				}
	}
	
	private void populateList() {
		eCursor = dbAdapt.getAllExpenses();
		startManagingCursor(eCursor);
		eCursor.requery();
		updateArray("abc");
	}

	private void updateArray(String sortType) {
		eCursor.requery();
		expenses.clear();
		if(eCursor.moveToFirst()) {
			do {
				ExpenseItem result = new ExpenseItem(eCursor.getString(1), eCursor.getString(2));
				expenses.add(0, result);
			} while (eCursor.moveToNext());
			aa.notifyDataSetChanged();
		}
		expenses = sortList(expenses, sortType);
		aa.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.add:
				addExpense();
				return true;
			default:
				return super.onOptionsItemSelected(item);
			
		}
	} 
	
	private void addExpense() {
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    LayoutInflater inflater = this.getLayoutInflater();
	    final View v = inflater.inflate(R.layout.new_expense, null);
		builder.setView(v);
	    builder.setTitle(R.string.add_expense);
	    
	    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		        	expenseName = (EditText) v.findViewById(R.id.name_input);
		            expenseCost = (EditText) v.findViewById(R.id.price_input);
			        inputExpense = expenseName.getText().toString();
			//        System.out.println(inputExpense);
			        inputCost = expenseCost.getText().toString();
			//        System.out.println(inputCost);
			        sendToDB();
			        System.out.println("came back from DataBase");
			        
				    aa.notifyDataSetChanged();

			       
					expenses0.setAdapter(aa);
					expenses1.setAdapter(aa);
					expenses2.setAdapter(aa);
					populateList();

		        }
		    });
	    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            }
	        });
	    aa.notifyDataSetChanged();
	    AlertDialog ad = builder.create();
	    ad.show(); 
	}
		

	protected void sendToDB() {
		ExpenseItem newExpense = new ExpenseItem(inputExpense, inputCost);
        dbAdapt.insertExpense(newExpense);
	    aa.notifyDataSetChanged();
        populateList();
	}
	
	private ArrayList<ExpenseItem> sortList(ArrayList<ExpenseItem> list, String sortType) {
		ArrayList<ExpenseItem> sorted = (ArrayList<ExpenseItem>) list.clone();

		int comp = 0;
		for (int i = 0; i < list.size(); i++) {   
		    for (int j = 0; j < list.size()-1; j++) {
		    	/* if this pair is out of order */
			    	
		    	if (sortType.equals("abc")) {
		    		comp = list.get(j).getExpense().toLowerCase().compareTo(list.get(j+1).getExpense().toLowerCase());
		    	} else if (sortType.equals("asc")) {
		    		comp = list.get(j).getCost().compareTo(list.get(j+1).getCost());
		    	} else if (sortType.equals("desc")) {
		    		comp = list.get(j+1).getCost().compareTo(list.get(j).getCost());
		    	}
		    	
		    	if (comp > 0) {
		    		/* swap them and remember something changed */
		    		swap(list, j, j+1);
		    	}
		     }
		}
		return sorted;
	}
	
	private void swap(ArrayList<ExpenseItem> list, int i, int j) {
		ExpenseItem swapped = list.get(i);
		list.remove(i);
		list.add(j, swapped);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		dbAdapt.close();
	}
}
