package com.example.project3rubank.util;

import com.example.project3rubank.bank.Account;

/**
 * This class is used to sort the accounts
 */
public class Sort {

	/**
	 *  This is a private helper method that performs the bubble sort
	 *  algorithm when called.
	 *
	 * @param accounts swaps the elements from the accounts array
	 * @param i     Number of sorted passes through the array.
	 * @param j     Current index being compared to i.
	 */
    private static void swap(List<Account> accounts, int i, int j) {
        Account temp = accounts.get(i);
        accounts.set(i, accounts.get(j));
        accounts.set(j, temp);
    }

	/**
	     * This method sorts the array by branch, then county, then city name
	     * by implementing a bubble sorting algorithm which pushes the largest
	     * values to the end of the array. (handles the PB command).
	     * @param accounts  used to sort the actual user accounts.
	 */
   private static void sortByBranch(List<Account> accounts) {
       int n = accounts.size();

       boolean swap;
       for (int i = 0; i < n - 1; i++) {
           swap = false;
           for (int j = 0; j < n - i - 1; j++) {
               try {
                   //County Comparison
                   int countyComparison = accounts.get(j).getNumber().getBranch().getCounty()
                           .compareTo(accounts.get(j + 1).getNumber().getBranch().getCounty());

                   if (countyComparison > 0) {
                       swap(accounts, j, j + 1);
                       swap = true;
                   } else if (countyComparison == 0) {
                       //Branch comparison:
                       int branchComparison = accounts.get(j).getNumber().getBranch().name()
                               .compareTo(accounts.get(j + 1).getNumber().getBranch().name());

                       if (branchComparison > 0) {
                           swap(accounts, j, j + 1);
                           swap = true;
                       }

                   }
               } catch (NullPointerException e) {

                   System.err.println("NullPointerException during sorting: " + e.getMessage());
               }
           }
           if (!swap) break;
       }
   }

	/**
	     * This method sorts the array by profile in order of last name,
	     * first name, date of birth and account number using the bubble sort
	     * algorithm.
	     *
	     * @param accounts the account to be swapped
	 */
   private static void sortByHolder(List<Account> accounts) {
       int n = accounts.size();
       for(int i = 0; i < n-1; i++) {
           for(int j = 0; j < n - i-1; j++) {
               try { int holderComparison = accounts.get(j).getHolder().compareTo(accounts.get(j + 1).getHolder());
                   if (holderComparison > 0) swap(accounts, j, j + 1);
                   if (holderComparison == 0) { int lastNameComparison = accounts.get(j).getHolder().getLastName().compareTo(accounts.get(j + 1).getHolder().getLastName());
                       if (lastNameComparison > 0) swap(accounts, j, j + 1);
                       if (lastNameComparison == 0) {
                           int firstNameComparison = accounts.get(j).getHolder().getFirstName().compareTo(accounts.get(j + 1).getHolder().getFirstName());
                           if (firstNameComparison > 0)
                               swap(accounts, j, j + 1);
                           if (firstNameComparison == 0) {
                               int dateOfBirthComparison = accounts.get(j).getHolder().getDateOfBirth().compareTo(accounts.get(j + 1).getHolder().getDateOfBirth());
                               if (dateOfBirthComparison > 0)
                                   swap(accounts, j, j + 1);
                               if (dateOfBirthComparison == 0) {
                                   int accountNumberComparison = accounts.get(j).getNumber().compareTo(accounts.get(j + 1).getNumber());
                                   if (accountNumberComparison > 0)
                                       swap(accounts, j, j + 1);
                               }
                           }
                       }
                   }
               } catch(NullPointerException e) { }
           }
       }
   }

   /**
    * This method sorts the array by type in order of account type and
    * account number using the bubble sort algorithm.
    *
    * @param accounts the account to be swapped
    */
   private static void sortByType(List<Account> accounts) {
       int n = accounts.size();
       boolean swap;

       for(int i = 0; i < n-1; i++) {
           swap = false;
           for (int j = 0; j < n - i - 1; j++) {
               try {
                   //Account type comparison
                   int accountTypeComparison = accounts.get(j).getNumber().getType()
                           .compareTo(accounts.get(j + 1).getNumber().getType());

                   if (accountTypeComparison > 0) {
                       swap(accounts, j, j + 1);
                       swap = true;
                   } else if (accountTypeComparison == 0) {
                       //Account number comparison:
                       int accountNumberComparison = accounts.get(j).getNumber()
                               .compareTo(accounts.get(j + 1).getNumber());

                       if (accountNumberComparison > 0) {
                           swap(accounts, j, j + 1);
                           swap = true;
                       }
                   }
               } catch(NullPointerException e) { }
           }
       }
   }

	/**
	 * This method sets the key, where based off the key it does the specific sorting
	 * @param list the list to sort
	 * @param key the key (char) to use when calling the sort
	 */
	public static void account(List<Account> list, char key) {
		if (key == 'B') { sortByBranch(list);}
		else if (key == 'H') { sortByHolder(list); }
		else if (key == 'T') { sortByType(list); }
	}
}
