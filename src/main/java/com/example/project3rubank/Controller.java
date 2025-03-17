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
	@FXML public HBox termBox;
	@FXML private Button openAccountButton;
	@FXML private Button clearFieldsButton;
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
		switch(type) {
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
		exitButton.setOnAction( event -> {
			exit();
		});
	}

	@FXML
	private void exit() {
		System.exit(0);
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

	/**
	 * This method is the implementation of the O command, gets user input and validates it to open an account.
	 * @param actionEvent
	 *
	 */
	@FXML
	private void openAccount(ActionEvent actionEvent) {
		String firstName = fName.getText().trim();
		String lastName = lName.getText().trim();
		AccountType type = accountTypeComboBox.getSelectionModel().getSelectedItem();
		Branch branch = branchComboBox.getSelectionModel().getSelectedItem();
		String term = null;

		RadioButton termSelected = (RadioButton) termsToggleGroup.getSelectedToggle();
		if (termSelected != null) { term = termSelected.getText(); }


		if (firstName.isEmpty() || lastName.isEmpty() || type == null || branch == null || dobValue.getValue() == null) {
			System.out.println("Fill in the required fields.");
			return;
		}

		Date dob = new Date(dobValue.getValue().toString());

		if (!dob.isValid()) { System.out.println("DOB invalid: " + dob + " not a valid calendar date!"); return; }

		if (dob.compareTo(new Date()) > 0) { System.out.println("DOB invalid: " + dob + " cannot be today or a future day."); return; }

		if (!dob.isAdult()) { System.out.println("Not eligible to open: " + dob + " under 18."); return; }

		Profile profile = new Profile(firstName, lastName, dob);

		String amountStr = initialDeposit.getText().trim();
		double initialDeposit = 0.0;
		try { initialDeposit = Double.parseDouble(amountStr);
		} catch (NumberFormatException e) { System.out.println("For input string: \"" + amountStr  + "\" - not a valid amount."); return; }

		if (type == AccountType.CD && term == null) {System.out.println("Missing term for CD Account"); return; }
		if (type == AccountType.CD && cdDateOpen.getValue() == null) {System.out.println("Missing opening date for CD account"); return;}

		RadioButton campusSelect = (RadioButton) campusToggleGroup.getSelectedToggle();
		if(type == AccountType.COLLEGE_CHECKING && campusSelect == null) {System.out.println("Missing campus for college account"); return;}


		int termNumber = 0;
		if (term != null) {
			try {
				termNumber = Integer.parseInt(term);
			} catch (NumberFormatException e) {
				return;
			}
		}

		Account duplicateAccount = findDuplicateAccount(accountDB, profile, type, termNumber);
		if (duplicateAccount != null && accountDB.contains(duplicateAccount)) {System.out.println(firstName + " " + lastName + " already has a " + type + " account."); return; }

		if (type == AccountType.MONEY_MARKET && initialDeposit < MONEY_MARKET_MINIMUM) { System.out.println("Minimum of $2,000 to open a Money Market account."); return; }
		if (type == AccountType.CD && initialDeposit < CertificateDeposit.MIN_BALANCE) { System.out.println("Minimum of $1,000 to open a Certificate Deposit account."); return; }

		if (initialDeposit <= 0) { System.out.println("Initial deposit cannot be 0 or negative."); return; }

		Account account = createAccount(accountDB, type, branch, profile, initialDeposit, termNumber);
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
	 * @param term the term of the CD account
	 * @return returns the account object of the user, of the correct type
	 */
	private Account createAccount(AccountDatabase accountDB, AccountType type, Branch branch, Profile profile, double initialDeposit, int term) {
		boolean isLoyal = false;
		switch (type) {
			case CHECKING:
				AccountNumber number = new AccountNumber(branch, type);
				return new Checking(number, profile, initialDeposit);

		}
		return null;
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






}
