/**
package com.example.project3rubank.tests;

import com.example.project3rubank.bank.*;
import org.junit.BeforeEach;
import org.junit.Test;

import static org.junit.Assert.*;

public class AccountDatabaseTest {
	Account account;
	AccountDatabase db;

	@Before
	public void setUp() {
		account = new Checking();
		account.setNumber(new AccountNumber(Branch.EDISON, AccountType.CHECKING));
		db = new AccountDatabase();
		db.add(account);

	}

	@Test
	public void depositAnyAccount() {
		account.setBalance(100);
		db.deposit(account.getNumber(), 200);
		assertEquals(300.0, account.getBalance(), 0.01);
	}

	@Test
	public void depositToMoneyMarket() {
		account.setBalance(100);
		account.setNumber(new AccountNumber(Branch.PRINCETON, AccountType.MONEY_MARKET));
		db.deposit(account.getNumber(), 5000);
		assertEquals(5100.0, account.getBalance(), 0.01);
	}

	@Test
	public void withdrawTrue() {
		account.setBalance(1000);
		boolean result = db.withdraw(account.getNumber(), 800);
		assertTrue(result);
	}

	@Test
	public void withdrawInsufficientFalse() {
		account.setBalance(1000);
		boolean result = db.withdraw(account.getNumber(), 1100);
		assertFalse(result);
	}

	@Test
	public void withdrawMoneyMarketTrue() {
		account.setBalance(6000);
		account.setNumber(new AccountNumber(Branch.PISCATAWAY, AccountType.MONEY_MARKET));
		boolean result = db.withdraw(account.getNumber(), 3000);
		assertTrue(result);
	}

}
 **/