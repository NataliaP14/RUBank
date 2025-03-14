package com.example.project3rubank.bank;

/**
 * This is an enum class for the account types users can pick from which include: checking, savings and money market savings.
 * @author Natalia Peguero, Olivia Kamau
 */
public enum AccountType {
    CHECKING ("01"),
    SAVINGS ("02"),
    MONEY_MARKET ("03"),
    COLLEGE_CHECKING("04"),
    CD("05");

    private String code;

    /**
     * This constructor is used to create the account type object with the specified code for account type.
     * @param code the unique code that identifies the account type
     */
    AccountType(String code) {
        this.code = code;
    }

    /**
     * Gets the code that identifies the account type
     * @return the code
     */
    public String getCode() {
        return code;
    }
}
