package com.mybusinessfinances;

import java.text.DecimalFormat;

public class ExpenseItem {
	
	private String expense;
	private String cost;
	
	public ExpenseItem(String e, String c) {
		this.expense = e;
		DecimalFormat df = new DecimalFormat("0.00");
		double tempCost = Double.parseDouble(c);
		this.cost = df.format(tempCost);
	}
	
	public String toString() {
		return this.expense + " $" + this.cost;
	}
	
	public String getExpense() {
		return this.expense;
	}

	public String getCost() {
		return this.cost;
	}
	
}
