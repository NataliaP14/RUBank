package com.example.project3rubank.bank;
import com.example.project3rubank.util.Date;

/**
 * This class implements a linked list that holds a list of closed accounts, includes adding and printing
 * @author Natalia Peguero, Olivia Kamau
 */
public class Archive {
    private AccountNode first; // head node of the linked list

    /**
     *  Default constructor for Archive class
     */
    public Archive() {

    }

    /**
     * This nested class represents a node in the linked list
     * Each node stores an account and a reference (next) to the next node
     */
    private static class AccountNode {
        private Account account;
        private AccountNode next;
        private Date close;

        /**
         * Constructor that creates accountNode object
         * @param account to store the node inside of account
         */
        public AccountNode(Account account, Date close) {
            this.account = account;
            this.next = null;
            this.close = close;
        }

        /**
         * Returns a formatted string containing the account activities and
         * closing information.
         *
         * @return  String representation of the account activities.
         */
        public String toString() {
            String activities = "";
                if (account.getActivities() != null && !account.getActivities().isEmpty()) {
                activities = "\n\t[Activity]";
                for (Activity activity : account.getActivities()) {
                    activities += "\n\t\t" + activity;
                }
            }
            return account.toString() + " Closed[" + close + "]" +  activities;
        }
    }

    /**
     *  Adds account to the front of the linked list
     * @param account The account that gets added into the linked list
     */
    public void add(Account account, Date close) {
        AccountNode node = new AccountNode(account, close);

        node.next = first;
        first = node;
    }

    /**
     * Prints the closed accounts in the linked list
     * Traverses through the linked list and prints the accounts
     *
     * @return
     */
    public String print() {
        StringBuilder print = new StringBuilder();

        AccountNode currentNode = first;

        print.append("\n*List of closed accounts in the archive.").append("\n");

        while (currentNode != null) {
            print.append(currentNode).append("\n");
            currentNode = currentNode.next;
        }
        print.append("*end of list.\n");
        return print.toString();
    }
}
