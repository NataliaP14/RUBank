package com.example.project3rubank.bank;
import com.example.project3rubank.util.List;

/**
 * The account class represents a bank account with an account number, account
 * holder and an account balance.
 * It provides methods for depositing, withdrawing and comparing accounts.
 *
 * @author Natalia Peguero, Olivia Kamau
 */
public abstract class Account implements Comparable<Account> {

    protected AccountNumber number;
    protected Profile       holder;
    protected double        balance;
    protected List<Activity> activities;

    /**
     * Empty constructor.
     */
    public Account() {

    }
    /**
     * The constructor for the Account class initializes an object with the
     * account number, profile holder information and initial balance in their
     * account.
     *
     * @param number    unique account number.
     * @param holder    the account holder's profile.
     * @param balance   the initial balance in the holder's account.
     */
    public Account(AccountNumber number, Profile holder, double balance) {
        this.number = number;
        this.holder = holder;
        this.balance = balance;
        this.activities = new List<>();
    }

    /**
     * Updates and prints the account balance after applying interest and fees.
     *
     * @param interest  Calculated interest amount.
     * @param fee       Calculated fee amount.
     */
    private String printBalance(double interest, double fee) {
        StringBuilder print = new StringBuilder();

        balance += interest - fee;
        print.append(String.format("\n\t[Balance] $%,.2f\n", balance));

        return print.toString();
    }

    /**
     *  Prints the interest earned and the fee charged.
     *
     * @param interest  Calculated interest.
     * @param fee       Calculated fees.
     */
    private String printInterestFee(double interest, double fee) {
        StringBuilder print = new StringBuilder();

        print.append(String.format("\t[interest] $%,.2f [Fee] $%,.2f", interest, fee));

        return print.toString();
    }

    /**
     * Prints all the activities recorded for the account.
     *
     * @return
     */
    private String printActivities() {
        StringBuilder print = new StringBuilder();

        if (activities.isEmpty()) return "";
        print.append("\t[Activity]\n");
            for(Activity activity: activities) {
                print.append("\t\t").append(activity).append("\n");
            }
        return print.toString();
    }

    /**
     *  Adds an account activity (D or W) to the activities list.
     *
     * @param activity  The recorded activity.
     */
    public void addActivity(Activity activity) {
        if(activity!=null) {
            activities.add(activity);
        }
    }


    /**
     *  Calculates the monthly interest for the account.
     *
     * @return  Monthly interest for the account.
     */
    public abstract double interest();  //monthly interest


    /**
     *  Calculates the monthly fee for the account.
     *
     * @return  Monthly fee for the account.
     */
    public abstract double fee();   //account fee


    /**
     * Template method that generates a statement for an account to display
     * the account activities, interest and fees.
     *
     * @return
     */
    public final String statement() {
        StringBuilder print = new StringBuilder();

        print.append(printActivities());
        double interest = interest();
        double fee = fee();
        print.append(printInterestFee(interest, fee));
        print.append(printBalance(interest, fee));
        return print.toString();
    }

    /**
     *  Returns the list containing the activities for each account (the date,
     *  branch, transaction type and amount.
     *
     * @return  The activities for each account.
     */
    public List<Activity> getActivities() {
        return activities;
    }

    /**
     *  Returns the values stored at the number variable.
     *
     * @return  returns the values.
     */
    public AccountNumber getNumber() { return number; }

    /**
     * Sets the account number.
     *
     * @param number the account number to set
     */
    public void setNumber(AccountNumber number) {
        this.number = number;
    }

    /**
     *  Returns the values stored at the holder variable.
     *
     * @return  returns the values.
     */
    public Profile getHolder() { return holder; }

    /**
     * Returns the value stored at the balance variable.
     * @return  returns the balance.
     */
    public double getBalance() { return balance; }

    /**
     * Sets the balance for the account.
     * @param balance   balance for the account.
     */
    public void setBalance(double balance) { this.balance = balance; }

    /**
     * equals() method: Checks if two accounts are equal based on their
     * account number.
     *
     * @param obj   the object to compare with.
     * @return      returns true if both accounts have the same account number,
     *              returns false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Account account = (Account) obj;

        // Handle the case where the AccountNumber is null
        if (this.number != null && account.number != null) {
            return this.holder.equals(account.holder) &&
                    this.number.getType().equals(account.number.getType()) &&
                    this.getNumber() == account.getNumber();
        } else {
            // In case the AccountNumber is null, don't compare it.
            return this.holder.equals(account.holder) &&
                    (this.number == null && account.number == null);
        }
    }


    /**
     * compareTo() method: Compares two accounts using their account numbers.
     *
     * @param other the object to be compared.
     * @return      Negative integer if the account number is less than,
     *              Positive integer if the account number is greater than,
     *              Zero if the account numbers are equal.
     */
    @Override
    public int compareTo(Account other) {

        if(this.number.compareTo(other.number)>0) return 1;
        if(this.number.compareTo(other.number)<0) return -1;
        return 0;
    }

    /**
     * Withdraws the specified amount from the account and updates the balance.
     *
     * @param amount    the amount to be withdrawn
     */
    public void withdraw(double amount) {
        if (amount > 0 && balance >= amount) {
            balance -= amount;
        }
    }

    /**
     * Deposits the specified amount from the account and updates the balance.
     *
     * @param amount    the amount to be deposited.
     */
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }
    /**
     * toString() method: Returns the string representation of the account
     * in the right format.
     *
     * @return  returns the formatted string with the account number, holder,
     *          balance and branch name.
     */
    @Override
    public String toString() {
        return String.format("Account#[%s] Holder[%s] Balance[$%,.2f] Branch[%s]",
                getNumber(), holder.toString(), balance, number.getBranch().name());
    }
}
