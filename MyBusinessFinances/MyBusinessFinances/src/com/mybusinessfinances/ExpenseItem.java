package com.mybusinessfinances;

import java.text.DecimalFormat;

public class ExpenseItem {

	private String expense;
	private double cost;
	
	public ExpenseItem(String e, double c) {
		this.expense = e;
		this.cost = c;
	}
	public String toString() {
		DecimalFormat df = new DecimalFormat("0.00");
		return this.expense + " $" + df.format(this.cost);
	}
	
	public String getExpense() {
		return this.expense;
	}
	
	public double getCost() {
		return this.cost;
	}
	
	public String getCostStr() {
		DecimalFormat df = new DecimalFormat("0.00");
		return "$" + df.format(this.cost);
	}
	
	
}
