package com.example.project3rubank.bank;

import com.example.project3rubank.util.Date;

import java.text.DecimalFormat;

/**
 * The Activity class shows the transaction activity associated with each bank
 *  account. It includes the date of the transaction, the location where it
 *  happened, the transaction type, the transaction amount and whether or not it occurred at an
 *  ATM.
 *
 * @author Natalia Peguero, Olivia Kamau
 */
public class Activity implements Comparable<Activity>{
	private Date date;
	private Branch location; //the location of the activity
	private char type; //D or W
	private double amount;
	private boolean atm; //true if this is made at an ATM (from the text file)

	/**
	 *  Constructor initializes an activity object with the specified parameters.
	 * @param date			Date of the transaction
	 * @param location		Branch where the transaction occurred.
	 * @param type			The type of transaction, 'D' for deposit, 'W' for withdrawal.
	 * @param amount		The amount deposited/withdrawn.
	 * @param atm			True if transaction occurred at an ATM, false if not.
	 */
	public Activity(Date date, Branch location, char type, double amount, boolean atm) {
		this.date = date;
		this.location = location;
		this.type = type;
		this.amount = amount;
		this.atm = atm;
	}

	/**
	 *	This method compares the current activity with another activity (o)
	 *		based on their transaction dates.
	 *
	 * @param 		o the object to be compared.
	 * @return		Returns a negative integer if the activity's date is before,
	 * 					a positive integer if the activity's date is after or
	 * 					zero if they occurred on the same date.
	 */
	@Override
	public int compareTo(Activity o) {
		return this.date.compareTo(o.date);
	}


	/**
	 *  Gets the date of the transaction.
	 *
	 * @return	Returns the date of the transaction.
	 */
	public Date getDate() {
		return date;
	}

	/**
	 *  This method returns a string representation of the activity in the required format.
	 *
	 * 	@return		Returns a formatted string with the transaction details.
	 */
	@Override
	public String toString() {

		String transactionType;
		if (type == 'D') {
			transactionType = "deposit";
		} else {
			transactionType = "withdrawal";
		}

		String atmTransaction = "";
		if (atm) { atmTransaction = "[ATM]"; }

		DecimalFormat amountFormatter = new DecimalFormat("$#,##0.00");

		return String.format("%s::%s%s::%s::%s", date, location, atmTransaction, transactionType, amountFormatter.format(amount));
	}
}
