package com.example.project3rubank.bank;

import com.example.project3rubank.util.Date;
import com.example.project3rubank.util.List;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * This class processes the command line logic, and logic to process the user input when entering information.
 * @author Natalia Peguero, Olivia Kamau
 */
public class TransactionManager {
    private static final double MONEY_MARKET_MINIMUM_FOR_LOYAL = 5000.0;
    private static final double MONEY_MARKET_MINIMUM = 2000.0;

    /**
     * Default constructor for the manager.
     */
    public TransactionManager() {
    }

    /**
     * This method is the implementation of the O command, gets user input and validates it to open an account.
     * @param accountDB Used to add the user to the account database
     * @param userInput To get each input from the user for validation and logic implementation
     */
    private void openAccount(AccountDatabase accountDB, String[] userInput) {
        if (userInput.length < 7) { System.out.println("Missing data tokens for opening an account."); return; }
        String accountType = userInput[1]; String city = userInput[2]; // account type and city
        String fName = userInput[3];String lName = userInput[4]; String dobInput = userInput[5]; //profile
        String amount = userInput[6]; double initialDeposit = 0; // amount
        AccountType type = null; Branch branch = null; Date dob = null; int term = 0;

        type = convertAccountType(accountType);
        if (type == null) { return; }
        branch = convertCity(city);
        if (branch == null) { return; }
        dob = new Date(dobInput);
        if (!dob.isValid()) { System.out.println("DOB invalid: " + dob + " not a valid calendar date!"); return; }

        if (dob.compareTo(new Date()) > 0) { System.out.println("DOB invalid: " + dob + " cannot be today or a future day."); return; }

        if (!dob.isAdult()) { System.out.println("Not eligible to open: " + dob + " under 18."); return; }
        Profile profile = new Profile(fName, lName, dob);

        try { initialDeposit = Double.parseDouble(amount);
        } catch (NumberFormatException e) { System.out.println("For input string: \"" + amount  + "\" - not a valid amount."); return; }

        if (type == AccountType.CD) {
            try { term = Integer.parseInt(userInput[7]);
            } catch (NumberFormatException e) { System.out.println(userInput[7] + " is not a valid term."); return; }
            if (isValidTerm(userInput[7])) { System.out.println(userInput[7] + " is not a valid term."); return; } }
        Account duplicateAccount = findDuplicateAccount(accountDB, profile, type, term);
        if (duplicateAccount != null && accountDB.contains(duplicateAccount)) {System.out.println(fName + " " + lName + " already has a " + type + " account."); return; }

        if (type == AccountType.MONEY_MARKET && initialDeposit < MONEY_MARKET_MINIMUM) { System.out.println("Minimum of $2,000 to open a Money Market account."); return; }
        if (type == AccountType.CD && initialDeposit < CertificateDeposit.MIN_BALANCE) { System.out.println("Minimum of $1,000 to open a Certificate Deposit account."); return; }

        if (initialDeposit <= 0) { System.out.println("Initial deposit cannot be 0 or negative."); return; }

        Account account = createAccount(accountDB, type, branch, profile, initialDeposit, userInput);
        if (account == null) { return; } accountDB.add(account);
		System.out.println(type + " account " + account.getNumber() + " has been opened.");
	}

    /**
     * This method actually creates the account of the user from the O command, based off the type of account they make.
     * @param accountDB the database of the accounts, to check if a user already has a checking account
     * @param type the account type to compare to, to create the correct type of account
     * @param branch the branch of where the user wants to create the account
     * @param profile the profile details of the user to create the account
     * @param initialDeposit the initial amount the user wants to deposit
     * @param userInput the actual input of the user, making sure they input the correct length
     * @return returns the account object of the user, of the correct type
     */
    private Account createAccount(AccountDatabase accountDB, AccountType type, Branch branch, Profile profile, double initialDeposit, String[] userInput) {
        boolean isLoyal = false;
        switch (type) {
            case CHECKING: if (userInput.length < 7) { System.out.println("Missing data tokens for opening an account."); return null; }
                AccountNumber number = new AccountNumber(branch, type);
                return new Checking(number, profile, initialDeposit);
            case SAVINGS: if (userInput.length < 7) { System.out.println("Missing data tokens for opening an account."); return null; }
                AccountNumber number1 = new AccountNumber(branch, type);
               Savings savingsAcc =  new Savings(number1, profile, initialDeposit, isLoyal);
                isLoyal = hasChecking(accountDB, profile);
                if (isLoyal) { savingsAcc.setLoyal(true); }
            return savingsAcc;
            case MONEY_MARKET: if (userInput.length < 7) { System.out.println("Missing data tokens for opening an account."); return null; }
                AccountNumber number2 = new AccountNumber(branch, type);
                isLoyal = initialDeposit >= MONEY_MARKET_MINIMUM_FOR_LOYAL;
                MoneyMarket moneyAcc = new MoneyMarket(number2, profile, initialDeposit, isLoyal);
                if (isLoyal) { moneyAcc.setLoyal(true);}
                return moneyAcc;
            case COLLEGE_CHECKING: if (userInput.length < 8) { System.out.println("Missing data tokens for opening an account."); return null; }
                String campusInput = userInput[7];
                Campus campus = null; campus = convertCampus(campusInput);
                CollegeChecking college = new CollegeChecking(null, profile, initialDeposit, campus);
                boolean eligible = college.isEligible();
                if (!eligible) { System.out.println("Not eligible to open: " + profile.getDateOfBirth() + " over 24."); return null; }
                AccountNumber number3 = new AccountNumber(branch, type);
                college = new CollegeChecking(number3, profile, initialDeposit, campus);
                return college;
            case CD: if (userInput.length < 9) { System.out.println("Missing data tokens for opening an account."); return null; }
                String termInput = userInput[7]; String dateInput = userInput[8];
                int term = Integer.parseInt(termInput);
                Date open = new Date(dateInput);
                if (isValidTerm(termInput)) { System.out.println(termInput + " is not a valid term."); return null; }
                if (!open.isValid()) { System.out.println("DOB invalid: " + open + " not a valid calendar date!"); return null; }
                else if (open.compareTo(new Date()) > 0) { System.out.println("DOB invalid: " + open + " cannot be today or a future day."); return null; }
                AccountNumber number4 = new AccountNumber(branch, type);
                return new CertificateDeposit(number4, profile, initialDeposit, isLoyal, term, open);
        }
        return null;
    }

    /**
     * This method compares the term, and checks whether it's valid for CD accounts
     * @param term the term to compare
     * @return returns true if valid, false if not
     */
    private boolean isValidTerm(String term) {
        return !term.equals("3") && !term.equals("6") && !term.equals("9") && !term.equals("12");
    }

    /**
     * Converts the campus from the user input to the enum value
     * @param campusInput the user input to convert
     * @return returns the correct campus relative to user input
     */
    private Campus convertCampus(String campusInput) {
        Campus campus = null;

        if (campusInput.equalsIgnoreCase("1")) {
            campus = Campus.NEW_BRUNSWICK;
        } else if (campusInput.equalsIgnoreCase("2")) {
            campus = Campus.NEWARK;
        } else if (campusInput.equalsIgnoreCase("3")) {
            campus = Campus.CAMDEN;
        } else {
            System.out.println(campusInput + " - not a valid campus.");
        }
        return campus;
    }

    /**
     * This method is a helper method for creating an account and checks if a savings account being made has a checking account as well
     * @param accountDB the account database to traverse from
     * @param profile the profile to compare
     * @return returns true if it has a checking account, otherwise false
     */
    private boolean hasChecking(AccountDatabase accountDB, Profile profile) {
        for (int i = 0; i < accountDB.size(); i++) {
            if (accountDB.get(i).getHolder().equals(profile) && accountDB.get(i).getNumber().getType() == AccountType.CHECKING) {
                return true;
            }
        }
        return false;
    }

    /**
     * This is a private helper method for checking duplicate accounts when a user tries to open an account
     * @param accountDB the database to get accounts
     * @param profile the profile to compare
     * @param type the account type to compare
     * @param term the term to compare for CD accounts
     * @return returns the account if theirs a duplicate, null otherwise.
     */
    private Account findDuplicateAccount(AccountDatabase accountDB, Profile profile, AccountType type, int term) {
        for (int i = 0; i < accountDB.size(); i++) {
            //System.out.println("Checking account: " + accountDB.get(i).getHolder().getFirstName() + " " + accountDB.get(i).getHolder().getLastName() + " Type: " + accountDB.get(i).getNumber().getType());
            if (accountDB.get(i).getNumber().getType() == AccountType.CD && type == AccountType.CD) {
                CertificateDeposit cdAcc = (CertificateDeposit) accountDB.get(i);
                if (accountDB.get(i).getHolder().equals(profile) &&
                        accountDB.get(i).getNumber().getType() == type && cdAcc.getTerm() == term) {
                    return accountDB.get(i);
                }
            } else {
                if (accountDB.get(i).getHolder().equals(profile) &&
                        accountDB.get(i).getNumber().getType() == type) {
                    return accountDB.get(i);
            }
            }
        }
        return null;
    }


    /**
     * This method converts the user input account type to the enum class variables
     * @param accountType the account type the user inputted
     * @return returns the account type
     */
    private AccountType convertAccountType(String accountType) {
        AccountType type = null;

        if (accountType.equalsIgnoreCase("checking")) {
            type = AccountType.CHECKING;
        } else if (accountType.equalsIgnoreCase("savings")) {
            type = AccountType.SAVINGS;
        } else if (accountType.equalsIgnoreCase("moneymarket")) {
            type = AccountType.MONEY_MARKET;
        } else if (accountType.equalsIgnoreCase("college")) {
            type = AccountType.COLLEGE_CHECKING;
        } else if (accountType.equalsIgnoreCase("certificate")) {
            type = AccountType.CD;
        } else {
            System.out.println(accountType + " - invalid account type.");
        }
        return type;
    }

    /**
     * This method converts the user input branch to the enum class variables
     * @param city the city(branch) the user inputted
     * @return returns the branch
     */
    private Branch convertCity(String city) {
        Branch branch = null;

        if (city.equalsIgnoreCase("edison")) {
            branch = Branch.EDISON;
        } else if (city.equalsIgnoreCase("bridgewater")) {
            branch = Branch.BRIDGEWATER;
        } else if (city.equalsIgnoreCase("princeton")) {
            branch = Branch.PRINCETON;
        } else if (city.equalsIgnoreCase("piscataway")) {
            branch = Branch.PISCATAWAY;
        } else if (city.equalsIgnoreCase("warren")) {
            branch = Branch.WARREN;
        } else {
            System.out.println(city + " - invalid branch.");
        }

        return branch;
    }

    /**
     * This method is for the implementation the C command, gets user input and validates it to close an existing account.
     * Additionally, prints the interest and fees, based off account type
     * @param accountDB Used to add the closed account to the archive and remove the account
     * @param userInput To get each input from the user for validation and logic implementation
     */
    private void closeAccount(AccountDatabase accountDB, String[] userInput) {
        String closeDate = userInput[1];
        double interest = 0; double penalty = 0;
        Date close = new Date(closeDate); Profile holder = null; boolean closedChecking = false;
        if (!close.isValid()) { System.out.println("DOB invalid: " + close + " not a valid calendar date!"); return; }
        if (userInput.length == 3) { String number = userInput[2]; boolean found = false;
            for (int i = 0; i < accountDB.size(); i++) {
                Account account = accountDB.get(i);
                if (account.getNumber().toString().equals(number)) { found = true; System.out.println("Closing account " + account.getNumber().toString());
                    if (account.getNumber().getType() != AccountType.CD) { interest = calculateInterestClosing(account, close); System.out.println("--interest earned: $" + String.format("%,.2f", interest));
                    } else { CertificateDeposit cd = (CertificateDeposit) account; interest = cd.calculateClosingInterest(close);
                        if (close.compareTo(cd.getMaturityDate()) < 0) { penalty = cd.calculatePenalty(close);
                            System.out.println("--interest earned: $" + String.format("%,.2f", interest));
                            System.out.println("--penalty: $" + String.format("%,.2f", penalty));
                        } else { System.out.println("--interest earned: $" + String.format("%,.2f", interest));}
                    }
                    if (account.getNumber().getType() == AccountType.CHECKING) { holder = account.getHolder(); closedChecking = true; }
                    accountDB.getArchive().add(account, close); accountDB.remove(account);
                    if (closedChecking && holder != null) { removeLoyalStatus(accountDB, holder); }
                    return;
                }
            }
            if (!found) { System.out.println(number + " account does not exist."); }
        } else if (userInput.length == 5) { String fName = userInput[2]; String lName = userInput[3]; String dob = userInput[4]; boolean found = false;
            for (int i = accountDB.size() - 1; i >= 0; i--) { Account account = accountDB.get(i);
                if (account.getHolder().getFirstName().equalsIgnoreCase(fName)
                        && account.getHolder().getLastName().equalsIgnoreCase(lName)
                        && account.getHolder().getDateOfBirth().equals(new Date(dob))) {
                    if (!found) { System.out.println("Closing accounts for " + fName + " " + lName + " " + dob); found = true; }
                    if (account.getNumber().getType() == AccountType.CD) { CertificateDeposit cd = (CertificateDeposit) account;interest = cd.calculateClosingInterest(close);penalty = cd.calculatePenalty(close);
                        System.out.println("--" + account.getNumber() + " interest earned: " + String.format("$%,.2f", interest));
                        System.out.println("  [penalty] $" + String.format("%,.2f", penalty));
                    } else { interest = calculateInterestClosing(account, close); System.out.println("--" + account.getNumber() + " interest earned: " + String.format("$%,.2f", interest)); }
                    accountDB.getArchive().add(account, close); accountDB.remove(account);
                }
            }
            if (!found) { System.out.println(fName + " " + lName + " " + dob + " does not have any accounts in the database.");
            } else { System.out.println("All accounts for " + fName + " " + lName + " " + dob + " are closed and moved to archive."); }
        } else { System.out.println("Missing data for closing an account."); }
    }

    /**
     * This is a helper method for closing an account, where if a user closes a checking account, and they also have a savings account, it removes the loyal status.
     * @param accountDB the account database to traverse from
     * @param holder the holder to compare
     */
    private void removeLoyalStatus(AccountDatabase accountDB, Profile holder) {
        for (int i = 0; i < accountDB.size(); i++) {
            if (accountDB.get(i).getHolder().equals(holder)) {
                if (accountDB.get(i).getNumber().getType() == AccountType.SAVINGS) {
                    Savings savingsAcc = (Savings) accountDB.get(i);
                    savingsAcc.setLoyal(false);
                }
            }
        }
    }

    /**
     * This method is a helper method for closing an account, it calculates the interest at closing of non-CD accounts.
     * @param account the account to compare the account type
     * @param close the close date, to get the day of the date
     * @return returns the interest of non-CD accounts using the formula
     */
    private double calculateInterestClosing(Account account, Date close) {
        int day = close.getDay();
        double rate = 0.0;
        double balance = account.getBalance();
        double days_per_year = 365.0;

        if (account.getNumber().getType() == AccountType.CHECKING) {
            rate = Checking.ANNUAL_INTEREST_RATE;
        } else if (account.getNumber().getType() == AccountType.SAVINGS) {
            Savings savingsAcc = (Savings) account;
            if (savingsAcc.isLoyal)
                rate = Savings.LOYALTY_BONUS + Savings.ANNUAL_INTEREST_RATE;
            else {
                rate = Savings.ANNUAL_INTEREST_RATE;
            }
        } else if (account.getNumber().getType() == AccountType.MONEY_MARKET) {
            rate = MoneyMarket.ANNUAL_INTEREST_RATE;
        } else if (account.getNumber().getType() == AccountType.COLLEGE_CHECKING) {
            rate = CollegeChecking.ANNUAL_INTEREST_RATE;
        }
		return (balance * (rate / days_per_year) * day);
    }

    /**
     * This method prints the activities that were processed in accountDB
     * @param accountDB the account database used to call the process activities method
     * @throws IOException exception handler to handle the file
     */
    private void printActivities(AccountDatabase accountDB) throws IOException {
        System.out.println("Processing \"activities.txt\"...");

        accountDB.processActivities(new File("activities.txt"));
        Scanner scanner = new Scanner(new File("activities.txt"));

        List<String> printedAccounts = new List<>();
        while (scanner.hasNextLine()) { StringTokenizer token = new StringTokenizer(scanner.nextLine(), ","); token.nextToken();
            String accountNumber = token.nextToken();

            Account account = null;
            for (int i = 0; i < accountDB.size(); i++) {
                if (accountDB.get(i).getNumber().toString().equals(accountNumber)) { account = accountDB.get(i); break; }
            }
            if (account != null) { boolean alreadyPrinted = false;
                for (int i = 0; i < printedAccounts.size(); i++) {
                    if (printedAccounts.get(i).equals(accountNumber)) { alreadyPrinted = true; break;
                    }
                }
                if (alreadyPrinted) { continue; }
                printedAccounts.add(accountNumber);

                if (account.getActivities() != null) {
                    List<Activity> activities = account.getActivities();
                    for (int i = 0; i < activities.size(); i++) {
                        Activity activity = activities.get(i);
                        System.out.println(accountNumber + "::" + activity.toString());
                    }
                }
            }
        }
        scanner.close();
        System.out.println("Account activities in \"activities.txt\" processed.");
    }

    /**
     *  This method deposits the specified amount to the user's balance. It
     *  checks to ensure that the specified account exists and the deposit
     *  amount is not zero/negative.
     *
     * @param accountDB the account database to get the accounts
     * @param userInput the user input, and to check the length
     */
    private void deposit(AccountDatabase accountDB, String[] userInput) {
        if(userInput.length < 3) { System.out.println("Missing data tokens for the deposit."); return; }

        String accountNumber = userInput[1];
        String amountString = userInput[2];
        double amount;

        try {
            amount = Double.parseDouble(amountString);
            if (amount <= 0) { System.out.println(amount + " - deposit amount cannot be 0 or negative."); return; }
        } catch(NumberFormatException e) {
            System.out.println("For input string: \"" + amountString + "\" - not a valid amount."); return;
        }
        boolean accountFound = false;

        for (int i = 0; i < accountDB.size(); i++) {
            if (accountDB.get(i).getNumber().toString().equals(accountNumber)) { accountFound = true;
                accountDB.deposit(accountDB.get(i).getNumber(), amount);
                System.out.println("$" + String.format("%,.2f", amount) +
                        " deposited to " + accountNumber);

                if (accountDB.get(i).getNumber().getType() == AccountType.MONEY_MARKET) {
                    MoneyMarket moneyAcc = (MoneyMarket) accountDB.get(i);
                    if (accountDB.get(i).getBalance() > MONEY_MARKET_MINIMUM_FOR_LOYAL) { moneyAcc.setLoyal(true);
                    }
                }
                Activity deposit = new Activity(new Date(), accountDB.get(i).getNumber().getBranch(), 'D', amount, false);
                accountDB.get(i).addActivity(deposit);
                break;
            }
        }
        if (!accountFound) { System.out.println(accountNumber + " does not exist."); }
    }

    /**
     *  The withdrawal method deducts the specified amount from the account's balance.
     *  It performs checks to ensure the holder has sufficient funds, doesn't
     *  withdraw zero/a negative amount and changes account type from money market
     *  to savings  if the balance drops below $2,000.
     *
     * @param accountDB the account database to get the accounts
     * @param userInput the user input to check the length
     */
    private void withdraw(AccountDatabase accountDB, String[] userInput) {
        if (userInput.length < 3) { System.out.println("Missing data tokens for the withdrawal."); return; }
        String accountNumber = userInput[1]; String amountString = userInput[2]; double amount;

        try { amount = Double.parseDouble(amountString); if (amount <= 0) { System.out.println(amount + " withdrawal amount cannot be 0 or negative."); return; }
        } catch (NumberFormatException e) { System.out.println("For input string: \"" + amountString + "\" - not a valid amount."); return; }

        boolean accountFound = false;

        for (int i = 0; i < accountDB.size(); i++) {
            if (accountDB.get(i).getNumber().toString().replace(" ", "").equals(accountNumber)) { accountFound = true;
                if (accountDB.get(i).getBalance() >= amount) {
                    Activity withdrawal = new Activity(new Date(), accountDB.get(i).getNumber().getBranch(), 'W', amount, false);
                    accountDB.get(i).addActivity(withdrawal);
                    accountDB.withdraw(accountDB.get(i).getNumber(), amount);

                    if (accountDB.get(i).getNumber().getType() == AccountType.MONEY_MARKET) { MoneyMarket moneyAcc = (MoneyMarket) accountDB.get(i); moneyAcc.incrementWithdrawals();
                        if (accountDB.get(i).getBalance() < MONEY_MARKET_MINIMUM) {
                            System.out.print(accountNumber + " balance below $2,000 - ");
                            if (amount <= accountDB.get(i).getBalance()) { System.out.println("$" + String.format("%,.2f", amount) + " withdrawn from " + accountNumber); }
                        } else {
                            if (amount <= accountDB.get(i).getBalance()) { System.out.println("$" + String.format("%,.2f", amount) + " withdrawn from " + accountNumber); }
                        }


                        if (accountDB.get(i).getBalance() < MONEY_MARKET_MINIMUM_FOR_LOYAL) { moneyAcc.setLoyal(false); }

                        return;
                    }
                    System.out.println("$" + String.format("%,.2f", amount) + " withdrawn from " + accountNumber);
                    return;
                }
                if (amount > accountDB.get(i).getBalance() && accountDB.get(i).getBalance() < MONEY_MARKET_MINIMUM) { System.out.println(accountNumber + " balance below $2,000 - " + "withdrawing $" + String.format("%,.2f", amount) + " - insufficient funds."); return; }
                else if (amount > accountDB.get(i).getBalance()) { System.out.println(accountNumber + " - insufficient funds."); return; }
                break;
            }
        } if(!accountFound) { System.out.println(accountNumber + " does not exist."); }
    }

    /**
     * This method reads user input and executes the appropriate commands
     */
    public void run() throws IOException {
        AccountDatabase accountDB = new AccountDatabase();
        accountDB.loadAccounts(new File("accounts.txt"));
        System.out.println("Accounts in \"accounts.txt\" loaded to the database.");
        System.out.println("Transaction Manager is running.\n");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            String[] userInput = input.split("\\s+");

            String command = userInput[0];

            if (command.equals("Q")) { System.out.println("\nTransaction Manager is terminated."); break;
            } else if (command.equals("P")) { System.out.println("P command is deprecated!"); continue; }

            switch (command) {
                case "A": printActivities(accountDB); break;
                case "O": openAccount(accountDB, userInput); break;
                case "C": closeAccount(accountDB, userInput); break;
                case "D": deposit(accountDB, userInput); break;
                case "W": withdraw(accountDB, userInput); break;
                case "PA": accountDB.printArchive(); break;
                case "PB": accountDB.printByBranch(); break;
                case "PH": accountDB.printByHolder(); break;
                case "PT": accountDB.printByType(); break;
                case "PS": accountDB.printStatements(); break;
                default: System.out.println("Invalid command!");
            }
        }
    }
}