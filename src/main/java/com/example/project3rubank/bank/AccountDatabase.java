package com.example.project3rubank.bank;

import com.example.project3rubank.util.Date;
import com.example.project3rubank.util.List;
import com.example.project3rubank.util.Sort;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * The AccountDatabase class manages a collection of bank accounts,
 * allowing addition, removal, and transactions.
 * It also provides sorting and printing functionalities to organize
 * accounts by various criteria.
 *
 * @author Natalia Peguero, Olivia Kamau
 */
public class AccountDatabase extends List<Account> {
    private Archive archive;

    /**
     * This constructor creates an account database object that holds the
     * accounts, size and archive.
     */
    public AccountDatabase() {
        super();
        this.archive = new Archive();
    }


    /**
     * Getter method to fetch the archive
     *
     * @return returns the archive
     */
    public Archive getArchive() {
        return archive;
    }

    /**
     * This method prints the archive from the archive class.
     */
    public String printArchive() {
        StringBuilder print = new StringBuilder();
        print.append(archive.print());

        return print.toString();
    }

    /**
     *  This method sorts and groups account statements by holder, then prints them out.
     *
     */
    public String printStatements() {
        StringBuilder print = new StringBuilder();

        print.append("*Account statements by account holder.").append("\n");

        List<Account> copy = new List<>();
        for (int i = 0; i < this.size(); i++) {
            copy.add(this.get(i));
        }
        Sort.account(copy, 'H');
        int count = 0;
        Profile prevProfile= null;

        for (int i = 0; i < copy.size(); i++) {
            Account account = copy.get(i);
            Profile profile = account.getHolder();

            if (prevProfile == null || !prevProfile.equals(profile)) {
                count++;
                if (prevProfile != null) {
                    print.append("");
                }
                print.append("\n").append(count).append(".").append(profile.getFirstName()).append(" ").append(profile.getLastName()).append(" ").append(profile.getDateOfBirth()).append("\n");
            } else {
                print.append("");
            }
            print.append("\t[Account#] ").append(account.getNumber()).append("\n");
            print.append(account.statement());
            prevProfile = profile;
        }
        print.append("\n*end of statements.");

        return print.toString();
    }


    /**
     * This method is used to load the accounts into the database from the accounts.txt file.
     *
     * @param file the text file used to parse the accounts
     * @throws IOException used for exception handling for the text file
     */
    public void loadAccounts(File file) throws IOException {
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            StringTokenizer token = new StringTokenizer(scanner.nextLine(), ",");
            String type = token.nextToken().toLowerCase(); String branchTxt = token.nextToken(); // branch and type
            String fName = token.nextToken(); String lName = token.nextToken(); Date dob = new Date(token.nextToken()); //profile info
            Double balance = Double.parseDouble(token.nextToken());
            Profile holder = new Profile(fName, lName, dob);
            Branch branch = Branch.valueOf(branchTxt.toUpperCase());
            AccountNumber number = null; Account account = null; boolean isLoyal = false;

            switch (type) {
                case "checking":  number = new AccountNumber(branch, AccountType.CHECKING); account = new Checking(number, holder, balance); break;
                case "savings": number = new AccountNumber(branch, AccountType.SAVINGS); account = new Savings(number, holder, balance, isLoyal); break;
                case "moneymarket": number = new AccountNumber(branch, AccountType.MONEY_MARKET); account = new MoneyMarket(number, holder, balance, isLoyal); break;
                case "college": number = new AccountNumber(branch, AccountType.COLLEGE_CHECKING);String campusCode = token.nextToken();
                    Campus campus = null;
                    for (Campus c : Campus.values()) { if (c.getCode().equals(campusCode)) { campus = c; break; } }
                    account = new CollegeChecking(number, holder, balance, campus);break;
                case "certificate": number = new AccountNumber(branch, AccountType.CD);int term = Integer.parseInt(token.nextToken());Date open = new Date(token.nextToken());account = new CertificateDeposit(number, holder, balance, isLoyal, term, open);

            }
            if (account != null) { this.add(account); }
        }
        scanner.close();

        for (int i = 0; i < this.size(); i++) {
            if (get(i).getNumber().getType() == AccountType.SAVINGS) { Savings savingsAcc = (Savings) this.get(i); Profile holder = savingsAcc.getHolder();
                boolean hasChecking = false;
                for (int j = 0; j < this.size(); j++) { if (get(j).getHolder().equals(holder) && (get(j).getNumber().getType() == AccountType.CHECKING)) { hasChecking = true;break;}
                }
                savingsAcc.setLoyal(hasChecking);
            }

            if (get(i).getNumber().getType() == AccountType.MONEY_MARKET) { MoneyMarket moneyAcc = (MoneyMarket) this.get(i);
                if (moneyAcc.getBalance() >= 5000) { moneyAcc.setLoyal(true); }
            }
        }
    }


    /**
     *  Processes the account activities and updates the respective account.
     *
     * @param file      File with the transaction records.
     * @throws IOException      Handles errors that occurs when reading the file.
     */
    public void processActivities(File file) throws IOException {
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            StringTokenizer token = new StringTokenizer(line, ",");
            char type = token.nextToken().charAt(0);
            String number = token.nextToken();
            Date date = new Date(token.nextToken());
            String branchTxt = token.nextToken().toLowerCase();
            int amount = Integer.parseInt(token.nextToken());
            Branch location = Branch.valueOf(branchTxt.toUpperCase());
            boolean atm = true;
            Activity activity = new Activity(date, location, type, amount, atm);


            Account account = null;
            for (int i = 0; i < this.size(); i++) {
                if (this.get(i).getNumber().toString().equals(number)) {
                    account = this.get(i);
                    account.addActivity(activity);
                    break;
                }
            }
            if (account != null) {
                if (type == 'D') {
                    account.deposit(amount);
                } else if (type == 'W') {
                    account.withdraw(amount);
                    if (account.getNumber().getType() == AccountType.MONEY_MARKET) {
                        MoneyMarket moneyAcc = (MoneyMarket) account;
                        moneyAcc.incrementWithdrawals();

                    }
                }
            }
        }
        scanner.close();
    }


    /**
     * This method updates the database and withdraws the amount from an account.
     *
     * @param number the account number to withdraw from.
     * @param amount the amount of money to withdraw.
     * @return returns true if number is found and withdraws, false otherwise.
     */
    public boolean withdraw(AccountNumber number, double amount) {

        for (int i = 0; i < this.size(); i++) {
            //finding the account number by checking if it matches
            if (this.get(i).getNumber().equals(number)) {
                Account account = this.get(i);

                if (account.getBalance() >= amount) {
                    account.withdraw(amount);
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * This method updates the database and deposits the amount from an account.
     *
     * @param number the account number to deposit from
     * @param amount the amount of money to deposit
     */
    public void deposit(AccountNumber number, double amount) {
        for (int i = 0; i < this.size(); i++) {
            if (this.get(i).getNumber().equals(number)) {
                Account account = this.get(i);
                account.deposit(amount);
            }
        }
    }

   /**
    * This method prints out the array sorted by  branch, county and city.
    */
   public String printByBranch() {
       StringBuilder print = new StringBuilder();

       if (this.size() == 0) {
           print.append("Account database is empty!");
           return print.toString();
       }

       List<Account> copy = new List<>();
       for (int i = 0; i < this.size(); i++) {
           copy.add(this.get(i));
       }
       Sort.account(copy, 'B');

       print.append("\n*List of accounts ordered by branch location (county, city).");

       String currCounty = "";

       for (int i = 0; i < this.size(); i++) {

           String county = copy.get(i).getNumber().getBranch().getCounty();

           if (!county.equals(currCounty)) {
               print.append("\nCounty: ").append(county).append("\n");
               currCounty = county;
           }

           print.append(copy.get(i).toString()).append("\n");
       }

       print.append("\n").append("*end of list.");

       return print.toString();
   }

   /**
    * This method prints out the array sorted by last name, first name,
    * date of birth and account number.
    */
   public String printByHolder() {
       StringBuilder print =  new StringBuilder();

       if (this.size() == 0) {
           print.append("Account database is empty!");
           return print.toString();
       }

       List<Account> copy = new List<>();
       for (int i = 0; i < this.size(); i++) {
           copy.add(this.get(i));
       }
       Sort.account(copy, 'H');

       print.append("\n*List of accounts ordered by account holder and number.").append("\n");
       for (int i = 0; i < this.size(); i++) {
           print.append(copy.get(i).toString()).append("\n");
       }
       print.append("\n").append("*end of list.\n");

       return print.toString();
   }

   /**
    * This method prints out the array sorted by account type and
    * account number.
    *
    */
   public String printByType() {
        StringBuilder print = new StringBuilder();

       if (this.size() == 0) {
           print.append("Account database is empty!");
           return print.toString();
       }

       List<Account> copy = new List<>();
       for (int i = 0; i < this.size(); i++) {
           copy.add(this.get(i));
       }
       Sort.account(copy,'T');
       String currType = "";
       print.append("\n*List of accounts ordered by account type and number.");
       for (int i = 0; i < this.size(); i++) {
           Account account = copy.get(i);
           String type = String.valueOf(account.getNumber().getType());

           if (!type.equals(currType)) {
               print.append("\nAccount Type: ").append(account.getNumber().getType()).append("\n");
               currType = type;
           }
           print.append(account).append("\n");
       }
       print.append("\n").append("*end of list.\n");

       return print.toString();
   }
}








