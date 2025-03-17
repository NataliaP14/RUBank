package com.example.project3rubank;

import com.example.project3rubank.bank.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.example.project3rubank.util.Date;
import javafx.scene.layout.HBox;

/**
 *
 * @Author Natalia Peguero, Olivia Kamau
 */
public class Controller {
	private static final double MONEY_MARKET_MINIMUM_FOR_LOYAL = 5000.0;
	private static final double MONEY_MARKET_MINIMUM = 2000.0;
	private AccountDatabase accountDB;

	@FXML private TextField accountNumber;
	@FXML private TextField transactionAmount;
	@FXML private Button deposit;
	@FXML private Button withdraw;
	@FXML private TextField closeFName;
	@FXML private TextField closeLName;
	@FXML private DatePicker closeProfileDob;
	@FXML private Button closeByProfile;
	@FXML private DatePicker closeDate;
	@FXML private TextField closeAccountNumber;
	@FXML private Button closeByNumber;
	@FXML private HBox termBox;
	@FXML private Button openAccount;
	@FXML private Button clearFields;
	@FXML private TextField lName;
	@FXML private TextField fName;
	@FXML private DatePicker dobValue;
	@FXML private HBox campusBox;
	@FXML private CheckBox loyalCustomerCheckBox;
	@FXML private DatePicker cdDateOpen;
	@FXML private TextField initialDeposit;
	@FXML private Button exitButton;
	@FXML private ComboBox<AccountType> accountTypeComboBox;
	@FXML private ComboBox<Branch> branchComboBox;
	@FXML private ToggleGroup campusToggleGroup;
	@FXML private ToggleGroup termsToggleGroup;

	private void hideElements(AccountType type) {
		switch (type) {
			case CHECKING, MONEY_MARKET:
				campusBox.setDisable(true);
				termBox.setDisable(true);
				loyalCustomerCheckBox.setDisable(true);
				cdDateOpen.setDisable(true);
				break;
			case SAVINGS:
				campusBox.setDisable(true);
				termBox.setDisable(true);
				loyalCustomerCheckBox.setDisable(false);
				cdDateOpen.setDisable(true);
				break;
			case COLLEGE_CHECKING:
				campusBox.setDisable(false);
				termBox.setDisable(true);
				loyalCustomerCheckBox.setDisable(true);
				cdDateOpen.setDisable(true);
				break;
			case CD:
				campusBox.setDisable(true);
				termBox.setDisable(false);
				loyalCustomerCheckBox.setDisable(true);
				cdDateOpen.setDisable(false);
				break;
		}
	}

	private String changeAccountTypeFormat(AccountType type) {
		return switch (type) {
			case CHECKING -> "Checking";
			case COLLEGE_CHECKING -> "College Checking";
			case SAVINGS -> "Savings";
			case MONEY_MARKET -> "Money Market";
			case CD -> "Certificate Deposit";
		};
	}


	private void setUpButtons() {
		exitButton.setOnAction(event -> {
			exit();
		});
	}

	@FXML
	private void exit() {
		System.exit(0);
	}

	/**
	 * This method is the implementation of the O command, gets user input and validates it to open an account.
	 *
	 * @param actionEvent
	 */
	@FXML
	private void openAccount(ActionEvent actionEvent) {
		String firstName = fName.getText().trim();
		String lastName = lName.getText().trim();
		AccountType type = accountTypeComboBox.getSelectionModel().getSelectedItem();
		Branch branch = branchComboBox.getSelectionModel().getSelectedItem();
		String term = null;

		RadioButton termSelected = (RadioButton) termsToggleGroup.getSelectedToggle();
		if (termSelected != null) {
			term = termSelected.getText();
		}


		if (firstName.isEmpty() || lastName.isEmpty() || type == null || branch == null || dobValue.getValue() == null) {
			System.out.println("Fill in the required fields.");
			return;
		}

		Date dob = new Date(dobValue.getValue().toString());

		if (!dob.isValid()) {
			System.out.println("DOB invalid: " + dob + " not a valid calendar date!");
			return;
		}

		if (dob.compareTo(new Date()) > 0) {
			System.out.println("DOB invalid: " + dob + " cannot be today or a future day.");
			return;
		}

		if (!dob.isAdult()) {
			System.out.println("Not eligible to open: " + dob + " under 18.");
			return;
		}

		Profile profile = new Profile(firstName, lastName, dob);

		String amountStr = initialDeposit.getText().trim();
		double initialDeposit = 0.0;
		try {
			initialDeposit = Double.parseDouble(amountStr);
		} catch (NumberFormatException e) {
			System.out.println("For input string: \"" + amountStr + "\" - not a valid amount.");
			return;
		}

		if (type == AccountType.CD && term == null) {
			System.out.println("Missing term for CD Account");
			return;
		}
		if (type == AccountType.CD && cdDateOpen.getValue() == null) {
			System.out.println("Missing opening date for CD account");
			return;
		}

		RadioButton campusSelect = (RadioButton) campusToggleGroup.getSelectedToggle();
		if (type == AccountType.COLLEGE_CHECKING && campusSelect == null) {
			System.out.println("Missing campus for college account");
			return;
		}


		int termNumber = 0;
		if (term != null) {
			try {
				termNumber = Integer.parseInt(term);
			} catch (NumberFormatException e) {
				return;
			}
		}

		Account duplicateAccount = findDuplicateAccount(accountDB, profile, type, termNumber);
		if (duplicateAccount != null && accountDB.contains(duplicateAccount)) {
			System.out.println(firstName + " " + lastName + " already has a " + type + " account.");
			return;
		}

		if (type == AccountType.MONEY_MARKET && initialDeposit < MONEY_MARKET_MINIMUM) {
			System.out.println("Minimum of $2,000 to open a Money Market account.");
			return;
		}
		if (type == AccountType.CD && initialDeposit < CertificateDeposit.MIN_BALANCE) {
			System.out.println("Minimum of $1,000 to open a Certificate Deposit account.");
			return;
		}

		if (initialDeposit <= 0) {
			System.out.println("Initial deposit cannot be 0 or negative.");
			return;
		}

		Account account = createAccount(accountDB, type, branch, profile, initialDeposit, termNumber);
		if (account == null) {
			return;
		}
		accountDB.add(account);
		System.out.println(type + " account " + account.getNumber() + " has been opened.");
	}

	/**
	 * This method actually creates the account of the user from the O command, based off the type of account they make.
	 *
	 * @param accountDB      the database of the accounts, to check if a user already has a checking account
	 * @param type           the account type to compare to, to create the correct type of account
	 * @param branch         the branch of where the user wants to create the account
	 * @param profile        the profile details of the user to create the account
	 * @param initialDeposit the initial amount the user wants to deposit
	 * @param term           the term of the CD account
	 * @return returns the account object of the user, of the correct type
	 */
	private Account createAccount(AccountDatabase accountDB, AccountType type, Branch branch, Profile profile, double initialDeposit, int term) {
		boolean isLoyal = false;
		switch (type) {
			case CHECKING:
				AccountNumber number = new AccountNumber(branch, type);
				return new Checking(number, profile, initialDeposit);
			case SAVINGS:
				AccountNumber number1 = new AccountNumber(branch, type);
				Savings savingsAcc = new Savings(number1, profile, initialDeposit, isLoyal);
				isLoyal = hasChecking(accountDB, profile);
				if (isLoyal) {
					savingsAcc.setLoyal(true);
				}
				return savingsAcc;
			case MONEY_MARKET:
				AccountNumber number2 = new AccountNumber(branch, type);
				isLoyal = initialDeposit >= MONEY_MARKET_MINIMUM_FOR_LOYAL;
				MoneyMarket moneyAcc = new MoneyMarket(number2, profile, initialDeposit, isLoyal);
				if (isLoyal) {
					moneyAcc.setLoyal(true);
				}
				return moneyAcc;
			case COLLEGE_CHECKING:
				RadioButton campusSelect = (RadioButton) campusToggleGroup.getSelectedToggle();
				String campusText = campusSelect.getText();
				Campus campus = null;
				campus = convertCampus(campusText);
				CollegeChecking college = new CollegeChecking(null, profile, initialDeposit, campus);
				boolean eligible = college.isEligible();
				if (!eligible) {
					System.out.println("Not eligible to open: " + profile.getDateOfBirth() + " over 24.");
					return null;
				}
				AccountNumber number3 = new AccountNumber(branch, type);
				college = new CollegeChecking(number3, profile, initialDeposit, campus);
				return college;
			case CD:
				Date open = new Date(cdDateOpen.getValue().toString());
				CertificateDeposit cd = null;
				if (open.compareTo(new Date()) > 0) {
					System.out.println("DOB invalid: " + open + " cannot be today or a future day.");
					return null;
				}
				AccountNumber number4 = new AccountNumber(branch, type);
				cd = new CertificateDeposit(number4, profile, initialDeposit, isLoyal, term, open);
				return cd;
		}
		return null;
	}

	/**
	 * Converts the campus from the user input to the enum value
	 *
	 * @param campusInput the user input to convert
	 * @return returns the correct campus relative to user input
	 */
	private Campus convertCampus(String campusInput) {
		Campus campus = null;

		if (campusInput.equalsIgnoreCase("New Brunswick")) {
			campus = Campus.NEW_BRUNSWICK;
		} else if (campusInput.equalsIgnoreCase("Newark")) {
			campus = Campus.NEWARK;
		} else if (campusInput.equalsIgnoreCase("Camden")) {
			campus = Campus.CAMDEN;
		}
		return campus;
	}


	/**
	 * This method is a helper method for creating an account and checks if a savings account being made has a checking account as well
	 *
	 * @param accountDB the account database to traverse from
	 * @param profile   the profile to compare
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
	 *
	 * @param accountDB the database to get accounts
	 * @param profile   the profile to compare
	 * @param type      the account type to compare
	 * @param term      the term to compare for CD accounts
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

	@FXML
	private void closeAccountByNumber(ActionEvent actionEvent) {
		if (closeDate.getValue() == null || closeAccountNumber.getText().trim().isEmpty()) {
			System.out.println("Fill in the required fields.");
			return;
		}
		Date close = new Date(closeDate.getValue().toString());
		String number = closeAccountNumber.getText().trim();
		double interest = 0;
		double penalty = 0;
		Profile holder = null;
		boolean closedChecking = false;
		boolean found = false;
		for (int i = 0; i < accountDB.size(); i++) {
			Account account = accountDB.get(i);
			if (account.getNumber().toString().equals(number)) {
				found = true;
				System.out.println("Closing account " + account.getNumber().toString());
				if (account.getNumber().getType() != AccountType.CD) {
					interest = calculateInterestClosing(account, close);
					System.out.println("--interest earned: $" + String.format("%,.2f", interest));
				} else {
					CertificateDeposit cd = (CertificateDeposit) account;
					interest = cd.calculateClosingInterest(close);
					if (close.compareTo(cd.getMaturityDate()) < 0) {
						penalty = cd.calculatePenalty(close);
						System.out.println("--interest earned: $" + String.format("%,.2f", interest));
						System.out.println("--penalty: $" + String.format("%,.2f", penalty));
					} else {
						System.out.println("--interest earned: $" + String.format("%,.2f", interest));
					}
				}
				if (account.getNumber().getType() == AccountType.CHECKING) {
					holder = account.getHolder();
					closedChecking = true;
				}
				accountDB.getArchive().add(account, close);
				accountDB.remove(account);
				if (closedChecking && holder != null) {
					removeLoyalStatus(accountDB, holder);
				}
				return;
			}
		}
		System.out.println(number + " account does not exist.");
	}

	@FXML
	private void closeAllAccounts(ActionEvent actionEvent) {
		Date close = new Date(closeDate.getValue().toString());
		String fName = closeFName.getText().trim();
		String lName = closeLName.getText().trim();
		Date dob = new Date(closeProfileDob.getValue().toString());
		if (closeDate.getValue() == null || fName.isEmpty() || lName.isEmpty() || closeProfileDob.getValue() == null) {
			System.out.println("FIll in the required fields.");
			return;
		}
		double interest = 0;
		double penalty = 0;
		boolean found = false;

		for (int i = accountDB.size() - 1; i >= 0; i--) {
			Account account = accountDB.get(i);
			if (account.getHolder().getFirstName().equalsIgnoreCase(fName)
					&& account.getHolder().getLastName().equalsIgnoreCase(lName)
					&& account.getHolder().getDateOfBirth().equals(new Date(closeProfileDob.getValue().toString()))) {
				if (!found) {
					System.out.println("Closing accounts for " + fName + " " + lName + " " + dob);
					found = true;
				}
				if (account.getNumber().getType() == AccountType.CD) {
					CertificateDeposit cd = (CertificateDeposit) account;
					interest = cd.calculateClosingInterest(close);
					penalty = cd.calculatePenalty(close);
					System.out.println("--" + account.getNumber() + " interest earned: " + String.format("$%,.2f", interest));
					System.out.println("  [penalty] $" + String.format("%,.2f", penalty));
				} else {
					interest = calculateInterestClosing(account, close);
					System.out.println("--" + account.getNumber() + " interest earned: " + String.format("$%,.2f", interest));
				}
				accountDB.getArchive().add(account, close);
				accountDB.remove(account);
			}
		}
		if (!found) {
			System.out.println(fName + " " + lName + " " + dob + " does not have any accounts in the database.");
		} else {
			System.out.println("All accounts for " + fName + " " + lName + " " + dob + " are closed and moved to archive.");
		}
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
	 *
	 */
	public void initialize() {
		accountDB = new AccountDatabase();
		setUpButtons();
		if (campusToggleGroup == null) { campusToggleGroup = new ToggleGroup();}
		if (termsToggleGroup == null) { termsToggleGroup = new ToggleGroup();}
		ObservableList<AccountType> types = FXCollections.observableArrayList(AccountType.values());
		accountTypeComboBox.setItems(types);

		accountTypeComboBox.setCellFactory(ListView -> new ListCell<AccountType>() {
			@Override
			protected void updateItem(AccountType type, boolean empty) {
				super.updateItem(type, empty);
				setText((empty || type == null) ? null : changeAccountTypeFormat(type));
			}
		});

		accountTypeComboBox.setButtonCell(new ListCell<AccountType>() {
			@Override
			protected void updateItem(AccountType type, boolean empty) {
				super.updateItem(type, empty);
				setText((empty || type == null) ? null : changeAccountTypeFormat(type));
			}
		});

		ObservableList<Branch> branches = FXCollections.observableArrayList(Branch.values());
		branchComboBox.setItems(branches);

		accountTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			hideElements(newValue);
		});

	}
}
