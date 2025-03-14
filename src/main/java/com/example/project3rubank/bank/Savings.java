package com.example.project3rubank.bank;

/**
 *	The Savings account subclass extends the Account class. It represents a
 *	savings account and implements its own calculations for interest and fee.
 *
 * @author Natalia Peguero, Olivia Kamau
 */
public class Savings extends Account {

	private static final double ACCOUNT_FEE = 25.0;
	private static final double MIN_FEE_CUTOFF = 500.0;
	public static final double ANNUAL_INTEREST_RATE = 0.025;
	public static final double LOYALTY_BONUS = 0.0025;
	public static final double MONTHLY_INTEREST_RATE = ANNUAL_INTEREST_RATE / 12;
	public static final double LOYAL_MONTHLY_INTEREST_RATE = (ANNUAL_INTEREST_RATE + LOYALTY_BONUS) / 12;
	protected boolean isLoyal;

	/**
	 * 	Constructor for a savings account with specified account number,
	 * 	 profile, balance and loyalty status.
	 *
	 * @param number	Account number for the savings account.
	 * @param holder	The profile of the account holder.
	 * @param balance	The initial balance of the savings account.
	 * @param isLoyal	Shows whether the account holder is a loyal customer.
	 */
	public Savings(AccountNumber number, Profile holder, double balance, boolean isLoyal) {
		super(number, holder, balance);
		this.isLoyal = isLoyal;
	}

	/**
	 *	Sets the loyalty status of the specified account.
	 *
	 * @param loyal		Loyalty status for the account.
	 */
	public void setLoyal(boolean loyal) {
		isLoyal = loyal;
	}

	/**
	 *  Calculates the monthly interest for a savings account.
	 *
	 * @return	The monthly interest for a savings account.
	 */
	@Override
	public double interest() {
		if(isLoyal) {
			return balance * LOYAL_MONTHLY_INTEREST_RATE;
		} else {
			return balance * MONTHLY_INTEREST_RATE;
		}
	}

	/**
	 *  Gets the account fee for the savings account.
	 *
	 * @return	The monthly account fee
	 */
	@Override
	public double fee() {
		if(balance >= MIN_FEE_CUTOFF) {
			return 0.0;
		} else {
			return ACCOUNT_FEE;
		}
	}


	/**
	 * 	Returns a string representation of the savings account information
	 * 	(including loyalty status).
	 *
	 * @return	Formatted string with savings account details.
	 */
	@Override
	public String toString() {
		String base = String.format("Account#[%s] Holder[%s] Balance[$%,.2f] Branch[%s]",
				getNumber(), holder.toString(), balance, number.getBranch().name());

		if (isLoyal) {
			base += " [LOYAL]";
		}

		return base;
	}
}
