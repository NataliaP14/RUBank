package com.example.project3rubank.bank;

/**
 *  The Checking account subclass extends the Account class.
 *  It implements calculations for interest and fee.
 *
 * @author Natalia Peguero, Olivia Kamau
 */
public class Checking extends Account {
    private static final double ACCOUNT_FEE = 15.0;
    private static final double MIN_FEE_CUTOFF = 1000.0;
    public static final double ANNUAL_INTEREST_RATE = 0.015;
    public static final double MONTHLY_INTEREST_RATE = ANNUAL_INTEREST_RATE / 12;

    /**
     * Empty Constructor for the checking class
     */
    public Checking() {
		super();

	}

    /**
     * This constructor creates a checking account object with accountNumber, holder, and balance
     * @param number the account number for the checking account
     * @param holder holder information for the checking account
     * @param balance balance for the checking account
     */
    public Checking(AccountNumber number, Profile holder, double balance) {
        super(number, holder, balance);
    }

    /**
     *  Calculates the monthly interest for a checking account.
     *
     * @return  Monthly interest for checking account.
     */
    @Override
    public double interest() {
        return (balance * MONTHLY_INTEREST_RATE);
    }

    /**
     *  Calculates the monthly fee for a checking account.
     *
     * @return  Monthly fee for checking account.
     */
    @Override
    public double fee() {
        if (balance >= MIN_FEE_CUTOFF) {
            return 0.0;
        } else {
            return ACCOUNT_FEE;
        }
    }
}

