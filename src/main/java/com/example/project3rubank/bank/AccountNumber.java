package com.example.project3rubank.bank;

import java.util.Random;

/**
 * The AccountNumber class represents a unique account identifier that
 * contains a branch code, an account type and a randomly generated 4-digit
 * number.
 *
 * It implements comparable for sorting purposes and comparison purposes
 * using the 9-digit account number.
 *
 * @author Natalia Peguero, Olivia Kamau
 */
public class AccountNumber implements Comparable<AccountNumber>{

    private static final int SEED = 9999;
    private static final Random rand = new Random(SEED);
    private Branch branch;
    private AccountType type;
    private String number;

    /**
     * Constructor for the AccountNumber class, it initializes a new account
     * number with a randomly generated 4-digit string.
     * @param branch This is the branch where the account is registered
     * @param type The type of account (Savings, Checking, Money Market)
     */
    public AccountNumber(Branch branch, AccountType type) {

        this.branch = branch;
        this.type = type;
        this.number = generateRandomNumber();
    }

    /**
     * Random number method: This method generates a fixed sequence random
     * 4-digit number using the SEED constant.
     *
     * @return This is a string representing a 4-digit number.
     */
    private String generateRandomNumber() {
		int randomNum = rand.nextInt(9999);
        return String.format("%04d", randomNum);
    }


    /**
     * This getter method returns the value stored at the branch
     *
     * @return  returns the branch.
     */
    public Branch getBranch() {
        return branch;
    }

    /**
     * This getter method returns value stored at the account type
     * @return returns the account type.
     */
    public AccountType getType() {
        return type;
    }


    /**
     * compareTo() method: This method compares two AccountNumber objects
     * using their 9-digit account number.
     *
     * @param other the object to be compared.
     * @return Negative integer if the object is less than the specified object,
     *         Positive integer if the object is greater than the specified object,
     *         Zero if both objects are equal.
     */
    @Override
    public int compareTo(AccountNumber other) {
        return this.toString().compareTo(other.toString());
    }


    /**
     * equals() method: This checks if two account numbers are equal based on
     * their branch, type and number.
     *
     * @param obj The object to compare with.
     * @return Returns true if the objects are eequal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        AccountNumber account = (AccountNumber) obj;

        return this.branch.equals(account.branch) &&
                this.type.equals(account.type) &&
                this.number.equals(account.number);
    }

    /**
     * toString() method: This returns the full 9-digit account number
     * as a string. Branch(3 digits) + Type(2 digits) + Number(4 digits).
     *
     * @return The string representation of the 9-digit account number.
     */
    @Override
    public String toString() {

        return String.format("%s%s%s", branch.getBranchCode(), type.getCode(), number);
    }
}
