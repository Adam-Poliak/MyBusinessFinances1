package com.mybusinessfinances;

public class ExpenseItem {

	private String expense;
	private String cost;
	
	public ExpenseItem(String e, String c) {
		this.expense = e;
		this.cost = c;
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
