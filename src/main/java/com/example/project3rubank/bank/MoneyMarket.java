package com.example.project3rubank.bank;

/**
 *	The MoneyMarket account subclass extends the Savings class. It represents
 *	a money market account and implements its own calculations for interest,
 *	fees and adds its own withdrawal methods.
 *
 * @author Natalia Peguero, Olivia Kamau
 */
public class MoneyMarket extends Savings {

	private static final double LOYALTY_BONUS = 0.0025;
	private static final double MIN_FEE_CUTOFF = 2000.0;
	private static final double ACCOUNT_FEE = 25.0;
	private static final int MAX_FREE_WITHDRAWALS = 3;
	private static final double EXCESS_WITHDRAWAL_FEE = 10.0;
	public static final double ANNUAL_INTEREST_RATE = 0.035;
	public static final double MONTHLY_INTEREST_RATE = ANNUAL_INTEREST_RATE / 12;
	public static final double LOYAL_MONTHLY_INTEREST_RATE = (ANNUAL_INTEREST_RATE + LOYALTY_BONUS) / 12;
	private int withdrawal;

	/**
	 *	Constructor that creates a moneymarket account object.
	 *
	 * @param number	The account number.
	 * @param holder	The profile for the account holder.
	 * @param balance	The initial balance of the moneymarket account.
	 * @param isLoyal	Indicates whether the account holder is a loyal customer or not.
	 */
	public MoneyMarket(AccountNumber number, Profile holder, double balance, boolean isLoyal) {
        super(number, holder, balance, isLoyal);
		    this.withdrawal = 0;
    }

	/**
	 * 	Calculates the monthly interest for a moneymarket account.
	 *
	 * @return	The calculated moneymarket interest.
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
	 * 	Calculates the account fee for a money market account.
	 *
	 * @return	The account fee for the money market account.
	 */
	@Override
	public double fee() {

		double totalFee = 0.0;

		if(balance < MIN_FEE_CUTOFF) {
			totalFee += ACCOUNT_FEE;
		}

		if(withdrawal > MAX_FREE_WITHDRAWALS) {
			totalFee += EXCESS_WITHDRAWAL_FEE;
		}

		return totalFee;
	}

	/**
	 * 	Helper method that increments the withdrawal count for the statement cycle.
	 */
	public void incrementWithdrawals() {
		withdrawal++;
	}

	/**
	 *  Returns the string representation of the account information
	 *  (includes loyalty and withdrawal checks).
	 *
	 * @return	Formatted string with moneymarket account details.
	 */
	@Override
	public String toString() {
		String base = String.format("Account#[%s] Holder[%s] Balance[$%,.2f] Branch[%s]",
				getNumber(), holder.toString(), balance, number.getBranch().name());

		if(isLoyal) { base += " [LOYAL]"; }

		base += String.format(" Withdrawal[%d]", withdrawal);

		return base;
	}

}
