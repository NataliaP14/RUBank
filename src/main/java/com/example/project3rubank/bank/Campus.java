package com.example.project3rubank.bank;

/**
 * This enum class assigns a number that corresponds to the campus type.
 *
 * @author Natalia Peguero, Olivia Kamau
 */
public enum Campus {
	NEW_BRUNSWICK ("1"),
	NEWARK ("2"),
	CAMDEN ("3");

	private String code;

	/**
	 *	Constructor that creates an account type object with the respective campus code.
	 *
	 * @param code	The campus code.
	 */
	Campus(String code) { this.code = code; }

	/**
	 *	Gets the campus code for the specific account.
	 *
	 * @return	The campus code.
	 */
	public String getCode() { return code; }
}


