package com.example.project3rubank;

import com.example.project3rubank.bank.*;
import com.example.project3rubank.util.List;
import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.example.project3rubank.util.Date;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;


/**
 *	This class manages the user interactions for the JavaFX application.
 *	It connects the UI interactions with their underlying logic.
 * @Author Natalia Peguero, Olivia Kamau
 */
public class Controller {
	private static final double MONEY_MARKET_MINIMUM_FOR_LOYAL = 5000.0;
	private static final double MONEY_MARKET_MINIMUM = 2000.0;
	private AccountDatabase accountDB;

	@FXML
	private Button loadAccountsButton, loadActivitiesButton, deposit, withdraw, printByBranchButton, printByTypeButton, printByHolderButton, printStatementsButton, printArchiveButton;
	@FXML
	private Button closeByProfile, closeByNumber, openAccount, clearFields;
	@FXML
	private TextField accountNumber, transactionAmount, closeFName, closeLName, closeAccountNumber, lName, fName, initialDeposit;
	@FXML
	private DatePicker closeProfileDob, closeDate, dobValue, cdDateOpen;
	@FXML
	private HBox termBox, campusBox;
	@FXML
	private ComboBox<AccountType> accountTypeComboBox;
	@FXML
	private ComboBox<Branch> branchComboBox;
	@FXML
	private ToggleGroup campusToggleGroup, termsToggleGroup;
	@FXML
	private CheckBox loyalCustomerCheckBox;
	@FXML
	private TextArea outputTextArea;

	/**
	 * This method disables the UI elements depending on the account type.
	 *
	 * @param type The selected account type.
	 */
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

	/**
	 * Converts the account type from an enum to a string representation.
	 *
	 * @param type The specified account type.
	 * @return The string representation of the account type.
	 */
	private String changeAccountTypeFormat(AccountType type) {

		try {
			return switch (type) {
				case CHECKING -> "Checking";
				case COLLEGE_CHECKING -> "College Checking";
				case SAVINGS -> "Savings";
				case MONEY_MARKET -> "Money Market";
				case CD -> "Certificate Deposit";
			};
		} catch (NullPointerException e) {
			return "Account Type";
		}
	}


	/**
	 * Private helper method that connects the UI buttons to their
	 * specific methods.
	 */
	private void setUpButtons() {
		openAccount.setOnAction(this::openAccount);

		clearFields.setOnAction(this::clearFields);

		closeByNumber.setOnAction(this::closeAccountByNumber);

		closeByProfile.setOnAction((this::closeAllAccounts));

		deposit.setOnAction(this::deposit);

		withdraw.setOnAction(this::withdraw);

		printByBranchButton.setOnAction(this::printByBranch);

		printByTypeButton.setOnAction(this::printByType);

		printByHolderButton.setOnAction(this::printByHolder);

		printStatementsButton.setOnAction(this::printStatements);

		printArchiveButton.setOnAction(this::printArchive);

		loadAccountsButton.setOnAction(this::loadAccounts);

		loadActivitiesButton.setOnAction(this::loadActivities);

		dobValue.getEditor().setDisable(true);

		cdDateOpen.getEditor().setDisable(true);

		closeProfileDob.getEditor().setDisable(true);

		closeDate.getEditor().setDisable(true);

	}


	/**
	 * This method is the implementation of the O command, gets user input
	 * and validates it to open an account.
	 *
	 * @param actionEvent the open account event
	 */
	@FXML
	private void openAccount(ActionEvent actionEvent) {
		String firstName = fName.getText().trim();
		String lastName = lName.getText().trim();
		AccountType type = accountTypeComboBox.getSelectionModel().getSelectedItem();
		Branch branch = branchComboBox.getSelectionModel().getSelectedItem();
		String term = null;
		String dateOfBirth;
		RadioButton termSelected = (RadioButton) termsToggleGroup.getSelectedToggle();
		if (termSelected != null) {term = termSelected.getText();}
		if (firstName.isEmpty() || lastName.isEmpty() || type == null || branch == null || dobValue.getValue() == null) { notifications("Fill in the required fields.", false); return; }
		dateOfBirth = dobValue.getValue().toString();
		Date dob = new Date(dateOfBirth);
		if (!dob.isValid()) { notifications("DOB invalid: " + dob + " not a valid calendar date!", false); return; }
		if (dob.compareTo(new Date()) > 0) { notifications("DOB invalid: " + dob + " cannot be today or a future day.", false); return; }
		if (!dob.isAdult()) { notifications("Not eligible to open: " + dob + " under 18.", false); return;}

		Profile profile = new Profile(firstName, lastName, dob);
		String amountStr = initialDeposit.getText().trim();
		double initialDeposit = 0.0;
		try { initialDeposit = Double.parseDouble(amountStr); } catch (NumberFormatException e) { notifications("For input string: \"" + amountStr + "\" - not a valid amount.", false); return; }

		if (type == AccountType.CD && term == null) { notifications("Missing term for CD Account", false); return; }
		if (type == AccountType.CD && cdDateOpen.getValue() == null) { notifications("Missing opening date for CD account", false); return; }

		RadioButton campusSelect = (RadioButton) campusToggleGroup.getSelectedToggle();
		if (type == AccountType.COLLEGE_CHECKING && campusSelect == null) { notifications("Missing campus for college account", false); return; }
		int termNumber = 0;
		if (term != null) { try { termNumber = Integer.parseInt(term); } catch (NumberFormatException e) { return; } }

		Account duplicateAccount = findDuplicateAccount(accountDB, profile, type, termNumber);
		if (duplicateAccount != null && accountDB.contains(duplicateAccount)) { notifications(firstName + " " + lastName + " already has a " + type + " account.", false); return; }
		if (type == AccountType.MONEY_MARKET && initialDeposit < MONEY_MARKET_MINIMUM) { notifications("Minimum of $2,000 to open a Money Market account.", false); return; }
		if (type == AccountType.CD && initialDeposit < CertificateDeposit.MIN_BALANCE) { notifications("Minimum of $1,000 to open a Certificate Deposit account.", false); return; }
		if (initialDeposit <= 0) { notifications("Initial deposit cannot be 0 or negative.", false); return; }
		Account account = createAccount(accountDB, type, branch, profile, initialDeposit, termNumber);
		if (account == null) { return; }
		accountDB.add(account);
		notifications(type + " account " + account.getNumber() + " has been opened.", true);
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
				if (isLoyal) { savingsAcc.setLoyal(true); }
				return savingsAcc;
			case MONEY_MARKET:
				AccountNumber number2 = new AccountNumber(branch, type);
				isLoyal = initialDeposit >= MONEY_MARKET_MINIMUM_FOR_LOYAL;
				MoneyMarket moneyAcc = new MoneyMarket(number2, profile, initialDeposit, isLoyal);
				if (isLoyal) { moneyAcc.setLoyal(true); }
				return moneyAcc;
			case COLLEGE_CHECKING:
				RadioButton campusSelect = (RadioButton) campusToggleGroup.getSelectedToggle();
				String campusText = campusSelect.getText();
				Campus campus = null;
				campus = convertCampus(campusText);
				CollegeChecking college = new CollegeChecking(null, profile, initialDeposit, campus);
				boolean eligible = college.isEligible();
				if (!eligible) { notifications("Not eligible to open: " + profile.getDateOfBirth() + " over 24.", false); return null; }
				AccountNumber number3 = new AccountNumber(branch, type);
				college = new CollegeChecking(number3, profile, initialDeposit, campus);
				return college;
			case CD:
				Date open = new Date(cdDateOpen.getValue().toString());
				CertificateDeposit cd = null;
				if (open.compareTo(new Date()) > 0) { notifications("DOB invalid: " + open + " cannot be today or a future day.", false); return null; }
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
	 * @return returns the correct campus relative to user input.
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
	 * This method handles the D command (deposit)
	 *
	 * @param actionEvent
	 */
	@FXML
	private void deposit(ActionEvent actionEvent) {
		String accNumber = accountNumber.getText().trim();
		String amountString = transactionAmount.getText().trim();

		if (accNumber.isEmpty() || amountString.isEmpty()) {
			notifications("Please fill in both fields.", false);
			return;
		}

		double amount;
		try {
			amount = Double.parseDouble(amountString);
			if (amount <= 0) { notifications(amount + " - deposit amount cannot be 0 or negative.", false); return; }
		} catch (NumberFormatException e) {
			notifications("For input string: \"" + amountString + "\" - not a valid amount.", false); return; }
		boolean accountFound = false;

		for (int i = 0; i < accountDB.size(); i++) {
			if (accountDB.get(i).getNumber().toString().equals(accNumber)) {
				accountFound = true;
				accountDB.deposit(accountDB.get(i).getNumber(), amount);
				notifications("$" + String.format("%,.2f", amount) + " deposited to " + accNumber, true);

				if (accountDB.get(i).getNumber().getType() == AccountType.MONEY_MARKET) {
					MoneyMarket moneyAcc = (MoneyMarket) accountDB.get(i);
					if (accountDB.get(i).getBalance() > MONEY_MARKET_MINIMUM_FOR_LOYAL) { moneyAcc.setLoyal(true); }
				}
				Activity deposit = new Activity(new Date(), accountDB.get(i).getNumber().getBranch(), 'D', amount, false);
				accountDB.get(i).addActivity(deposit);
				break;
			}
		}
		if (!accountFound) { notifications(accNumber + " does not exist.", false); }
	}


	/**
	 * This method handles the W command (withdraw)
	 *
	 * @param actionEvent
	 */
	@FXML
	private void withdraw(ActionEvent actionEvent) {
		String accNumber = accountNumber.getText().trim();
		String amountString = transactionAmount.getText().trim();

		if (accNumber.isEmpty() || amountString.isEmpty()) { notifications("Please fill in both fields.", false); return; }

		double amount;
		try { amount = Double.parseDouble(amountString);
			if (amount <= 0) { notifications(amountString + " withdrawal amount cannot be 0 or negative.", false); return; }
		} catch (NumberFormatException e) { notifications("For input string: \"" + amountString + "\" - not a valid amount.", false); return;}
		boolean accountFound = false;
		for (int i = 0; i < accountDB.size(); i++) {
			if (accountDB.get(i).getNumber().toString().replace(" ", "").equals(accNumber)) {
				accountFound = true;
				if (accountDB.get(i).getBalance() >= amount) {
					Activity withdrawal = new Activity(new Date(), accountDB.get(i).getNumber().getBranch(), 'W', amount, false);
					accountDB.get(i).addActivity(withdrawal);
					accountDB.withdraw(accountDB.get(i).getNumber(), amount);
					if (accountDB.get(i).getNumber().getType() == AccountType.MONEY_MARKET) {
						MoneyMarket moneyAcc = (MoneyMarket) accountDB.get(i);
						moneyAcc.incrementWithdrawals();
						if (accountDB.get(i).getBalance() < MONEY_MARKET_MINIMUM) {
							if (amount <= accountDB.get(i).getBalance()) { notifications(accNumber + "\" balance below $2,000 - \" $" + String.format("%,.2f", amount) + " withdrawn from " + accNumber, true); }
						} else {
							if (amount <= accountDB.get(i).getBalance()) { notifications("$" + String.format("%,.2f", amount) + " withdrawn from " + accNumber, true); }
						}
						if (accountDB.get(i).getBalance() < MONEY_MARKET_MINIMUM_FOR_LOYAL) { moneyAcc.setLoyal(false); } return;
					}
					notifications("$" + String.format("%,.2f", amount) + " withdrawn from " + accNumber, true); return;
				}
				if (amount > accountDB.get(i).getBalance() && accountDB.get(i).getBalance() < MONEY_MARKET_MINIMUM) { notifications(accNumber + " balance below $2,000 - " + "withdrawing $" + String.format("%,.2f", amount) + " - insufficient funds.", false); return;
				} else if (amount > accountDB.get(i).getBalance()) { notifications(accNumber + " - insufficient funds.", false); return;
				}
				break;
			}
		}
		if (!accountFound) { notifications(accNumber + " does not exist.", false); }
	}


	/**
	 * Closes the account based on the account number and closing date
	 *
	 * @param actionEvent Event triggered by user interaction.
	 */
	@FXML
	private void closeAccountByNumber(ActionEvent actionEvent) {
		if (closeDate.getValue() == null || closeAccountNumber.getText().trim().isEmpty()) {
			notifications("Fill in the required fields.", false);
			return;
		}
		Date close = new Date(closeDate.getValue().toString());
		String number = closeAccountNumber.getText().trim();
		double interest = 0;
		double penalty = 0;
		Profile holder = null;
		boolean closedChecking = false;
		boolean found = false;
		StringBuilder print = new StringBuilder();
		for (int i = 0; i < accountDB.size(); i++) {
			Account account = accountDB.get(i);
			if (account.getNumber().toString().equals(number)) { found = true;
				print.append("Closing account ").append(account.getNumber().toString()).append("\n");
				if (account.getNumber().getType() != AccountType.CD) { interest = calculateInterestClosing(account, close); print.append("--interest earned: $").append(String.format("%,.2f", interest)).append("\n");
				} else {
					CertificateDeposit cd = (CertificateDeposit) account;
					if (close.compareTo(cd.getOpen()) < 0) { notifications("Closing date is earlier than the opening date of the Certificate Deposit account, please choose a date that is after.", false); return; }

					interest = cd.calculateClosingInterest(close);
					if (close.compareTo(cd.getMaturityDate()) < 0) {
						penalty = cd.calculatePenalty(close);
						print.append("--interest earned: $").append(String.format("%,.2f", interest)).append("\n");
						print.append("--penalty: $").append(String.format("%,.2f", penalty)).append("\n");
					} else { print.append("--interest earned: $").append(String.format("%,.2f", interest)).append("\n"); }
				}
				if (account.getNumber().getType() == AccountType.CHECKING) { holder = account.getHolder(); closedChecking = true; }
				accountDB.getArchive().add(account, close);
				accountDB.remove(account);
				alert(print.toString(), true);
				if (closedChecking && holder != null) { removeLoyalStatus(accountDB, holder); } return; }
		}
		notifications(number + " account does not exist.", false);
	}

	/**
	 * Closes all accounts associated with a user's profile.
	 *
	 * @param actionEvent The event triggered by the user interaction.
	 */
	@FXML
	private void closeAllAccounts(ActionEvent actionEvent) {
		Date close = new Date(closeDate.getValue().toString());
		String fName = closeFName.getText().trim();
		String lName = closeLName.getText().trim();
		Date dob = new Date(closeProfileDob.getValue().toString());
		StringBuilder print = new StringBuilder();
		if (closeDate.getValue() == null || fName.isEmpty() || lName.isEmpty() || closeProfileDob.getValue() == null) { notifications("Fill in the required fields.", false); return; }
		double interest = 0;
		double penalty = 0;
		boolean found = false;
		for (int i = accountDB.size() - 1; i >= 0; i--) {
			Account account = accountDB.get(i);
			if (account.getHolder().getFirstName().equalsIgnoreCase(fName) && account.getHolder().getLastName().equalsIgnoreCase(lName) && account.getHolder().getDateOfBirth().equals(new Date(closeProfileDob.getValue().toString()))) {
				if (!found) { print.append("Closing accounts for ").append(fName).append(" ").append(lName).append(" ").append(dob).append("\n"); found = true; }
				if (account.getNumber().getType() == AccountType.CD) {
					CertificateDeposit cd = (CertificateDeposit) account;
					if (close.compareTo(cd.getOpen()) < 0) { notifications("Closing date is earlier than the opening date of the Certificate Deposit account, please choose a date that is after.", false); return; }
					interest = cd.calculateClosingInterest(close);
					penalty = cd.calculatePenalty(close);
					print.append("--").append(account.getNumber()).append(" interest earned: ").append(String.format("$%,.2f", interest)).append("\n");
					print.append("  [penalty] $").append(String.format("%,.2f", penalty)).append("\n");
				} else {
					interest = calculateInterestClosing(account, close);
					print.append("--").append(account.getNumber()).append(" interest earned: ").append(String.format("$%,.2f", interest)).append("\n");
				}
				accountDB.getArchive().add(account, close);
				accountDB.remove(account);
			}
		}
		if (!found) {
			notifications(fName + " " + lName + " " + dob + " does not have any accounts in the database.", false);
		} else {
			print.append("All accounts for ").append(fName).append(" ").append(lName).append(" ").append(dob).append(" are closed and moved to archive.").append("\n");
			alert(print.toString(), true);
		}
	}

	/**
	 * This is a helper method for closing an account, where if a user closes a checking account, and they also have a savings account, it removes the loyal status.
	 *
	 * @param accountDB the account database to traverse from
	 * @param holder    the holder to compare
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
	 *
	 * @param account the account to compare the account type
	 * @param close   the close date, to get the day of the date
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
	 * This method handles the PB command.
	 *
	 * @param actionEvent The event triggered by the user interaction.
	 */
	@FXML
	private void printByBranch(ActionEvent actionEvent) {
		if (accountDB != null) {
			outputTextArea.setText(accountDB.printByBranch());
		} else {
			outputTextArea.setText("Database is empty.");
		}
	}

	/**
	 * This method handles the PT command.
	 *
	 * @param actionEvent The event triggered by the user interaction.
	 */
	@FXML
	private void printByType(ActionEvent actionEvent) {
		if (accountDB != null) {
			outputTextArea.setText(accountDB.printByType());
		} else {
			outputTextArea.setText("Database is empty.");
		}
	}

	/**
	 * This method handles the PH command
	 *
	 * @param actionEvent The event triggered by the user interaction.
	 */
	@FXML
	private void printByHolder(ActionEvent actionEvent) {
		if (accountDB != null) {
			outputTextArea.setText(accountDB.printByHolder());
		} else {
			outputTextArea.setText("Database is empty.");
		}
	}

	/**
	 * This method handles the PS command.
	 *
	 * @param actionEvent The event triggered by the user interaction.
	 */
	@FXML
	private void printStatements(ActionEvent actionEvent) {
		if (accountDB != null) {
			outputTextArea.setText(accountDB.printStatements());
		} else {
			outputTextArea.setText("Database is empty.");
		}
	}

	/**
	 * This method handles the PA command.
	 *
	 * @param actionEvent The event triggered by the user interaction.
	 */
	@FXML
	private void printArchive(ActionEvent actionEvent) {
		if (accountDB != null) {
			outputTextArea.setText(accountDB.printArchive());
		} else {
			outputTextArea.setText("Archive is empty");
		}
	}

	/**
	 * This method resets the selections in the account creation form.
	 *
	 * @param actionEvent The event triggered by the user interaction.
	 */
	@FXML
	private void clearFields(ActionEvent actionEvent) {
		fName.clear();
		lName.clear();
		initialDeposit.clear();
		dobValue.setValue(null);
		cdDateOpen.setValue(null);
		accountTypeComboBox.getSelectionModel().clearSelection();
		branchComboBox.getSelectionModel().clearSelection();
		campusToggleGroup.selectToggle(null);
		termsToggleGroup.selectToggle(null);
		loyalCustomerCheckBox.setSelected(false);
	}

	/**
	 * Loads account data from a text file into the database.
	 *
	 * @param actionEvent
	 */
	@FXML
	private void loadAccounts(ActionEvent actionEvent) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Accounts File");
		FileChooser.ExtensionFilter txtFilter = new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt");
		fileChooser.getExtensionFilters().add(txtFilter);
		File file = fileChooser.showOpenDialog(new Stage());

		if (file != null) {
			try {
				accountDB.loadAccounts(file);
				outputTextArea.setText("Accounts in " + file.getName() + " loaded to the database.");
			} catch (IOException e) {
				outputTextArea.setText("Error loading accounts, please select a file in the correct format.");
			} catch (Exception e) {
				outputTextArea.setText("Error loading accounts, The file you selected is not in the correct format");
			}
		}
	}

	/**
	 * This method prints the activities that were processed in accountDB
	 *
	 * @param file the file used to process the activities
	 * @return returns the stringbuilder that prints out the activities
	 * @throws IOException exception handler to handle the file
	 */
	private String printActivities(File file) throws IOException {
		StringBuilder print = new StringBuilder();
		print.append("Processing \"").append(file.getName()).append("\"...\n");

		accountDB.processActivities(file);
		Scanner scanner = new Scanner(file);
		List<String> printedAccounts = new List<>();

		while (scanner.hasNextLine()) {
			StringTokenizer token = new StringTokenizer(scanner.nextLine(), ",");
			token.nextToken();
			String accountNumber = token.nextToken();

			Account account = null;
			for (int i = 0; i < accountDB.size(); i++) {
				if (accountDB.get(i).getNumber().toString().equals(accountNumber)) {
					account = accountDB.get(i);
					break;
				}
			}

			if (account != null) {
				if (printedAccounts.contains(accountNumber)) {
					continue;
				}
				printedAccounts.add(accountNumber);

				if (account.getActivities() != null) {
					List<Activity> activities = account.getActivities();
					for (int i = 0; i < activities.size(); i++) {
						Activity activity = activities.get(i);
						print.append(accountNumber).append("::").append(activity.toString()).append("\n");
					}
				}
			}
		}
		scanner.close();
		print.append("Account activities in \"").append(file.getName()).append("\" processed.\n");
		return print.toString();
	}

	/**
	 * This methods loads the activities from the selected file.
	 *
	 * @param actionEvent The event triggered by the user interaction.
	 */
	@FXML
	private void loadActivities(ActionEvent actionEvent) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Activities File");
		FileChooser.ExtensionFilter txtFilter = new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt");
		fileChooser.getExtensionFilters().add(txtFilter);
		File file = fileChooser.showOpenDialog(new Stage());

		if (file != null) {
			try {
				outputTextArea.setText(printActivities(file));
			} catch (IOException e) {
				outputTextArea.setText("Error loading activities, please select a file that is in the correct format. ");
			} catch (Exception e) {
				outputTextArea.setText("Error loading activities, please select a file that is in the correct format!");
			}
		}
	}


	/**
	 * Displays a temporary notification based on the user's actions.
	 *
	 * @param message The message displayed in the notification.
	 * @param success If true, the notification is green. Otherwise, the notification is red.
	 */
	private void notifications(String message, boolean success) {
		Label label = new Label(message);
		label.setPadding(new Insets(10));
		label.setStyle("-fx-background-color: " + (success ? "#4CAF50" : "#f44336") + "; -fx-text-fill: white;");

		StackPane notification = new StackPane(label);
		notification.setAlignment(Pos.TOP_RIGHT);
		notification.setPadding(new Insets(10));
		notification.setMouseTransparent(true);

		BorderPane root = (BorderPane) openAccount.getScene().getRoot();

		StackPane notificationContainer = (StackPane) root.lookup("#notificationContainer");
		if (notificationContainer == null) {
			notificationContainer = new StackPane();
			notificationContainer.setId("notificationContainer");
			root.setBottom(notificationContainer);
		}
		notificationContainer.getChildren().add(notification);
		notification.setTranslateX(200);
		Timeline timeline = new Timeline();
		KeyValue keyValue = new KeyValue(notification.translateXProperty(), 0, Interpolator.EASE_IN);
		KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), keyValue);
		timeline.getKeyFrames().add(keyFrame);
		timeline.play();
		FadeTransition fade = new FadeTransition(Duration.seconds(2), label);
		fade.setFromValue(1.0);
		fade.setToValue(0.0);

		StackPane finalNotificationContainer = notificationContainer;
		fade.setOnFinished(e -> {

			PauseTransition delay = new PauseTransition(Duration.seconds(0.5));
			delay.setOnFinished(f -> finalNotificationContainer.getChildren().remove(notification));
			delay.play();
		});
		PauseTransition delay = new PauseTransition(Duration.seconds(2));
		delay.setOnFinished(e -> fade.play());
		delay.play();
	}

	/**
	 * This method displays an alert to tell the user whether their action
	 * was successful or unsuccessful.
	 *
	 * @param message The message displayed by the alert.
	 * @param success If true, the alert is an information dialog,
	 *                else it's an error dialog.
	 */
	private void alert(String message, boolean success) {
		Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
		alert.setTitle(success ? "Success" : "Error");
		alert.setHeaderText("Closing Account");
		alert.setContentText(message);
		alert.showAndWait();
	}

	/**
	 * This changes the branch selection after clearing fields.
	 * @param branch the branch to switch to
	 * @return returns the branch
	 */
	private String changeBranch(Branch branch) {
		return switch (branch) {
			case EDISON -> "EDISON";
			case BRIDGEWATER -> "BRIDGEWATER";
			case PRINCETON-> "PRINCETON";
			case PISCATAWAY -> "PISCATAWAY";
			case WARREN -> "WARREN";
		};
	}

	/**
	 * Initializes the controller when the FXML file is loaded.
	 */
	public void initialize() {
		accountDB = new AccountDatabase();
		setUpButtons();
		if (campusToggleGroup == null) {
			campusToggleGroup = new ToggleGroup();
		}
		if (termsToggleGroup == null) {
			termsToggleGroup = new ToggleGroup();
		}

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
				if (empty || type == null) {
					setText("Account Type");
				} else {
					setText(changeAccountTypeFormat(type));
				}
			}
		});

		ObservableList<Branch> branches = FXCollections.observableArrayList(Branch.values());
		branchComboBox.setItems(branches);

		branchComboBox.setButtonCell(new ListCell<Branch>() {
			@Override
			protected void updateItem(Branch branch, boolean empty) {
				super.updateItem(branch, empty);
				if (empty || branch == null) {
					setText("Branch");
				} else {
					setText(changeBranch(branch));
				}
			}
		});

		accountTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				hideElements(newValue);

			}
		});


	}



}
