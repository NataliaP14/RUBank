package com.example.project3rubank.bank;

import com.example.project3rubank.util.Date;

import java.util.Calendar;

/**
 * The Certificate Deposit account type class that extends the Savings class.
 * Also calculates the interest and penalties for CD accounts.
 * @author Natalia Peguero, Olivia Kamau
 */
public class CertificateDeposit extends Savings {
	private static final double THREE_MONTH_TERM = 3.0;
	private static final double SIX_MONTH_TERM = 6.0;
	private static final double NINE_MONTH_TERM = 9.0;
	private static final double TWELVE_MONTH_TERM = 12.0;
	private static final double THREE_MONTH_RATE = 0.03;
	private static final double SIX_MONTH_RATE = 0.0325;
	private static final double NINE_MONTH_RATE = 0.035;
	private static final double TWELVE_MONTH_RATE = 0.04;
	private static final double EARLY_WITHDRAWAL_PENALTY = 0.10;
	private static final double DAYS_IN_YEAR = 365.0;
	private static final double DAYS_IN_MONTH = 30.0;
	public static final int MIN_BALANCE = 1000;

	private int term;
	private Date open;

	/**
	 *  Creates a Certificate Deposit account with the listed parameters.
	 *
	 * @param number	The account number.
	 * @param holder	The profile holder.
	 * @param balance	Initial balance of the account.
	 * @param isLoyal	Loyalty status for CD accounts (always loyal).
	 * @param term		The length of the term for a CD account (in months).
	 * @param open		The date the account was opened.
	 */
	public CertificateDeposit(AccountNumber number, Profile holder, double balance, boolean isLoyal, int term, Date open) {
		super(number, holder, balance, true);
		this.term = term;
		this.open = open;
	}

	/**
	 * 	Private helper method to get the interest rate based on the term selected.
	 *
	 * @return	The interest rate for the selected term.
	 */
	private double getInterestRate() {
		if(term == THREE_MONTH_TERM) {
			return THREE_MONTH_RATE;
		} else if(term == SIX_MONTH_TERM) {
			return SIX_MONTH_RATE;
		} else if (term == NINE_MONTH_TERM) {
			return NINE_MONTH_RATE;
		} else {
			return TWELVE_MONTH_RATE;
		}
	}

	/**
	 * 	Private helper method that calculates the number of days between two
	 * 	Calendar instances.
	 *
	 * @param openDateCal	The opening date as a Calendar object.
	 * @param closeDateCal	The closing date as a Calendar object.
	 * @return		The number of days between startCal and endCal.
	 */
	private int calculateDaysBetween(Calendar openDateCal, Calendar closeDateCal) {
		Calendar tempCal = (Calendar) openDateCal.clone();
		Calendar endCal = (Calendar) closeDateCal.clone();

		tempCal.set(Calendar.HOUR_OF_DAY, 0);
		tempCal.set(Calendar.MINUTE, 0);
		tempCal.set(Calendar.SECOND, 0);
		tempCal.set(Calendar.MILLISECOND, 0);

		endCal.set(Calendar.HOUR_OF_DAY, 0);
		endCal.set(Calendar.MINUTE, 0);
		endCal.set(Calendar.SECOND, 0);
		endCal.set(Calendar.MILLISECOND, 0);

		int daysBetween = 1;

		while (tempCal.before(endCal)) {
			tempCal.add(Calendar.DAY_OF_MONTH, 1);
			daysBetween++;
		}
		return daysBetween;
	}

	/**
	 * Helper method to calculate common operations for penalty and closing interest.
	 * @param close The closing date of the account.
	 * @return The number of days the account was open and the early withdrawal rate.
	 */
	private double[] calculateCommonWithdrawalData(Date close) {
		Calendar openDate = Calendar.getInstance();
		openDate.set(Calendar.YEAR, open.getYear());
		openDate.set(Calendar.MONTH, open.getMonth() - 1);
		openDate.set(Calendar.DAY_OF_MONTH, open.getDay());

		Calendar closeDate = Calendar.getInstance();

		closeDate.set(Calendar.YEAR, close.getYear());
		closeDate.set(Calendar.MONTH, close.getMonth() - 1);
		closeDate.set(Calendar.DAY_OF_MONTH, close.getDay());

		int daysOpen = calculateDaysBetween(openDate, closeDate);

		double earlyWithdrawalRate;
		if ((daysOpen / DAYS_IN_MONTH) <= SIX_MONTH_TERM) {
			earlyWithdrawalRate = THREE_MONTH_RATE;
		} else if ((daysOpen / DAYS_IN_MONTH) <= NINE_MONTH_TERM) {
			earlyWithdrawalRate = SIX_MONTH_RATE;
		} else if ((daysOpen / DAYS_IN_MONTH) <= TWELVE_MONTH_TERM) {
			earlyWithdrawalRate = NINE_MONTH_RATE;
		} else {
			earlyWithdrawalRate = TWELVE_MONTH_RATE;
		}
		return new double[]{daysOpen, earlyWithdrawalRate};
	}

	/**
	 * Private helper method to calculate the maturity date of the CD account.
	 *
	 * @return The maturity data of the CD account.
	 */
	public Date getMaturityDate() {
		Calendar maturityDateCal = Calendar.getInstance();
		maturityDateCal.set(Calendar.YEAR, open.getYear());
		maturityDateCal.set(Calendar.MONTH, open.getMonth() - 1);
		maturityDateCal.set(Calendar.DAY_OF_MONTH, open.getDay());

		maturityDateCal.add(Calendar.MONTH, term);

		String maturityDateString =
				maturityDateCal.get(Calendar.YEAR) + "-" +
						(maturityDateCal.get(Calendar.MONTH) + 1) + "-" +
				maturityDateCal.get(Calendar.DAY_OF_MONTH);

		return new Date(maturityDateString);
	}

	/**
	 *  Calculates the monthly interest for a certificate deposit account.
	 *
	 * @return	The monthly interest for a CD account.
	 */
	@Override
	public double interest() {
		return balance * (getInterestRate() / 12);
	}

	/**
	 * Method to calculate and return the penalty amount for early withdrawal.
	 *
	 * @param close The date the account was closed on.
	 * @return The penalty amount.
	 */
	public double calculatePenalty(Date close) {
		double[] commonData = calculateCommonWithdrawalData(close);
		int daysOpen = (int) commonData[0];
		double earlyWithdrawalRate = commonData[1];
		double interestEarned = 0.0;

		if (daysOpen == 0) {
			interestEarned = balance * (earlyWithdrawalRate / (double)DAYS_IN_YEAR);
		} else {
			interestEarned = balance * (earlyWithdrawalRate / (double)DAYS_IN_YEAR) * daysOpen;
		}
		return interestEarned * EARLY_WITHDRAWAL_PENALTY;
	}

	/**
	 * Method to calculate the closing interest for a certificate deposit account,
	 * depending on whether the account was closed before or after maturity.
	 *
	 * @param close The date the account was closed on.
	 * @return The closing interest for the account, after subtracting the penalty.
	 */
	public double calculateClosingInterest(Date close) {
		double[] commonData = calculateCommonWithdrawalData(close);
		int daysOpen = (int) commonData[0];
		double earlyWithdrawalRate = commonData[1];

		double interestEarned = 0.0;

		// If closed after maturity, calculate the regular interest

		if (daysOpen >= term * DAYS_IN_MONTH) {
			return balance * (getInterestRate() / DAYS_IN_YEAR) * daysOpen;
		}

		if (daysOpen == 0) {
			interestEarned = balance * (earlyWithdrawalRate / DAYS_IN_YEAR);
		} else {
			interestEarned = balance * (earlyWithdrawalRate / DAYS_IN_YEAR) * daysOpen;
		}

		return interestEarned;
	}

	/**
	 *	Gets the term for the account
	 *
	 * @return	The term chosen for the account.
	 */
	public int getTerm() {
		return term;
	}

	/**
	 *  Returns a string representation of the CD account information
	 *  (including Term, Date opened and Maturity date details).
	 *
	 * @return	String representation of the CD account information.
	 */
	@Override
	public String toString() {

		String base = String.format("Account#[%s] Holder[%s] Balance[$%,.2f] Branch[%s]",
				getNumber(), holder.toString(), balance, number.getBranch().name());

		base += String.format( " Term[%s] Date opened[%s] Maturity date[%s]", term, open, getMaturityDate());

		return base;
	}
}
