package com.example.project3rubank.bank;

import com.example.project3rubank.util.Date;

import java.util.Calendar;

/**
 * The CollegeChecking account type class that extends the checking class
 * also determines whether a user is eligible to open.
 * @author Natalia Peguero, Olivia Kamau
 */
public class CollegeChecking extends Checking {

	private static final int AGE_LIMIT = 24;
	private static final double COLLEGE_CHECKING_FEE = 0.0;
	private Campus campus;

	/**
	 *  Constructor to create a college checking account object.
	 *
	 * @param number	Account number.
	 * @param holder	Profile holder information.
	 * @param balance	Initial balance of the account.
	 * @param campus	College campus information.
	 */
	public CollegeChecking(AccountNumber number, Profile holder, double balance, Campus campus) {
		super(number, holder, balance);
		this.campus = campus;
	}

	/**
	 * 	Changes the fee to 0 for college checking accounts.
	 * @return	0.0 since college checking doesn't have fees.
	 */
	@Override
	public double fee() {
		return COLLEGE_CHECKING_FEE;
	}

	/**
	 * This checks if a college checking account user is eligible, if a user 24 years old or younger
	 * @return returns true if 24 or younger, false otherwise
	 */
	public boolean isEligible() {

		Calendar today = Calendar.getInstance();
		Date birthDate = holder.getDateOfBirth();

		int age = today.get(Calendar.YEAR) + 1 - birthDate.getYear();

		// If their birthday hasn't happened yet this year, subtract 1
		if (today.get(Calendar.MONTH) + 1 < birthDate.getMonth() ||
				(today.get(Calendar.MONTH) + 1 == birthDate.getMonth() &&
						today.get(Calendar.DATE) < birthDate.getDay())) {
			age--;
		}
		return age <= AGE_LIMIT;
	}

	/**
	 *  Returns a string representation of the college checking account information
	 * 	(including the campus information).
	 * @return	String representation of College Checking account information.
	 */
	@Override
	public String toString() {
		String base = String.format("Account#[%s] Holder[%s] Balance[$%,.2f] Branch[%s]",
				getNumber(), holder.toString(), balance, number.getBranch().name());

		base += String.format(" Campus[%s]", campus);

		return base;
	}
}
