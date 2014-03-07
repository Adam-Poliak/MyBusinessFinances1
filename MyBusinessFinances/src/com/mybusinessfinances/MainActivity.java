package com.mybusinessfinances;

import java.util.ArrayList;
import java.util.Locale;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends FragmentActivity {
	
	protected static ArrayList<ExpenseItem> expenses;
	protected static ArrayAdapter<ExpenseItem> aa;
	
	protected static ExpensedbAdaptor dbAdapt;
	private static Cursor eCursor; //expense table cursor
	
	private EditText expenseName;
	private EditText expenseCost;
	private String inputCost;
	private String inputExpense;
	
    private ViewPager mViewPager;
    private MyAdapter mAdapter;
    
    private String sortType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_pager);
		
		dbAdapt = new ExpensedbAdaptor(this);
		dbAdapt.open();
		this.sortType = "abc";
	}
	
	public static class MyAdapter extends FragmentPagerAdapter {
		private static int NUM_LISTS = 3;
		
		public MyAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return ArrayListFragment.newInstance(position);
		}

		@Override
		public int getCount() {
			return NUM_LISTS;
		}
		
		@Override
	    public CharSequence getPageTitle(int position) {
	        if (position == 1) {
	        	return "Alphabetical";
	        } else if (position == 2) {
	        	return "Descending";
	        } else if (position == 3) {
	        	return "Ascending";
	        } else {
	        	return null;
	        }
	    }
	}
	
	public static class ArrayListFragment extends ListFragment {
	    int mNum;

	    /**
	     * Create a new instance of CountingFragment, providing "num"
	     * as an argument.
	     */
	    static ArrayListFragment newInstance(int num) {
	        ArrayListFragment f = new ArrayListFragment();

	        // Supply num input as an argument.
	        Bundle args = new Bundle();
	        args.putInt("num", num);
	        f.setArguments(args);

	        return f;
	    }
	    
	    /**
	     * When creating, retrieve this instance's number from its arguments.
	     */
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
	    }

	    /**
	     * The Fragment's UI is just a simple text view showing its
	     * instance number.
	     */
	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	        View v = inflater.inflate(R.layout.list_fragment, container, false);
	        return v;
	    }

	    @Override
	    public void onActivityCreated(Bundle savedInstanceState) {
	        super.onActivityCreated(savedInstanceState);
	        setListAdapter(aa);
	    }

	    @Override
	    public void onListItemClick(ListView l, View v, int position, long id) {
	        Log.i("FragmentList", "Item clicked: " + id);
	    }
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		expenses = new ArrayList<ExpenseItem>();
		aa = new ArrayAdapter<ExpenseItem>(this, android.R.layout.simple_list_item_1, expenses);
		
		// Set up tabs
		mAdapter = new MyAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager)findViewById(R.id.pager);
        mViewPager.setAdapter(mAdapter);
	    mViewPager.setOnPageChangeListener(
	            new ViewPager.SimpleOnPageChangeListener() {
	                @Override
	                public void onPageSelected(int position) {
	                    // When swiping between pages, select the
	                    // corresponding tab.
	                    getActionBar().setSelectedNavigationItem(position);
	                }
	            });
		
		final ActionBar actionBar = getActionBar();
		
		// Specify that tabs should be displayed in the action bar.
	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	    
	    // Create a tab listener that is called when the user changes tabs.
	    ActionBar.TabListener tabListener = new ActionBar.TabListener() {
	    	@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
	    		mViewPager.setCurrentItem(tab.getPosition());
	    		if (tab.getText().equals("Alphabetical")) {
	    			sortType = "abc";
	    		} else if (tab.getText().equals("Descending")) {
	    			sortType = "desc";
	    		} else if (tab.getText().equals("Ascending")) {
	    			sortType = "asc";
	    		}
	    		populateList();
			}
	    	@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				// hide the given tab
			}
	    	@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				// ignore
			}
	    };

	    // Create Tabs
		
		ActionBar.Tab tab1 = actionBar.newTab();
	    tab1.setText("Alphabetical");
	    tab1.setTabListener(tabListener);
	    ActionBar.Tab tab2 = actionBar.newTab();
	    tab2.setText("Descending");
	    tab2.setTabListener(tabListener);
	    ActionBar.Tab tab3 = actionBar.newTab();
	    tab3.setText("Ascending");
	    tab3.setTabListener(tabListener);
	    
	    actionBar.addTab(tab1);
	    actionBar.addTab(tab2);
	    actionBar.addTab(tab3);
		populateList();
	}
	
	private void populateList() {
		eCursor = dbAdapt.getAllExpenses();
		startManagingCursor(eCursor);
		eCursor.requery();
		updateArray();
	}

	private void updateArray() {
		eCursor.requery();
		expenses.clear();
		if(eCursor.moveToFirst()) {
			do {
				ExpenseItem result = new ExpenseItem(eCursor.getString(1), eCursor.getString(2));
				expenses.add(0, result);
			} while (eCursor.moveToNext());
			aa.notifyDataSetChanged();
		}
		sortList();
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
			        inputCost = expenseCost.getText().toString();
			        sendToDB();
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
        expenses.add(newExpense);
        aa.add(newExpense);
	    aa.notifyDataSetChanged();
        populateList();
	}
	
	private void sortList() {
		double comp = 0;
		for (int i = 0; i < expenses.size(); i++) {   
		    for (int j = 0; j < expenses.size()-1; j++) {
		    	if (sortType.equals("abc")) {
		    		comp = expenses.get(j).getExpense().toLowerCase(Locale.US)
		    				.compareTo(expenses.get(j+1).getExpense().toLowerCase(Locale.US));
		    	} else if (sortType.equals("asc")) {
		    		comp = Double.parseDouble(expenses.get(j).getCost())
		    				- Double.parseDouble((expenses.get(j+1).getCost()));
		    	} else if (sortType.equals("desc")) {
		    		comp = Double.parseDouble(expenses.get(j+1).getCost())
		    				- Double.parseDouble((expenses.get(j).getCost()));
		    	}
		    	
		    	/* if this pair is out of order */
		    	if (comp > 0) {
		    		/* swap them and remember something changed */
		    		swap(j, j+1);
		    	}
		     }
		}
	}
	
	private void swap(int i, int j) {
		ExpenseItem swapped = expenses.get(i);
		expenses.remove(i);
		expenses.add(j, swapped);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		dbAdapt.close();
	}
}
