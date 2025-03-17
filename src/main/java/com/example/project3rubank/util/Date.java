package com.example.project3rubank.util;
import java.util.Calendar;

/**
 * This date class is used to check for validity in the dates and implementing the date in mm/dd/yyyy format
 * @author Natalia Peguero, Olivia Kamau
 */
public class Date implements Comparable<Date> {
    private int year;
    private int month;
    private int day;

    public static final int QUADRENNIAL = 4;
    public static final int CENTENNIAL = 100;
    public static final int QUATERCENTENNIAL = 400;
    public static final int[] DAYS = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    public static final int LEAP_YEAR = 29;
    public static final int MINIMUM_AGE = 18;


    /**
     * This constructor creates a date object with the current date
     */
    public Date() {
        Calendar date = Calendar.getInstance();
        year = date.get(Calendar.YEAR);
        month = date.get(Calendar.MONTH) + 1;
        day = date.get(Calendar.DATE);
    }

    /**
     * this constructor creates a date object as a string, and uses regex to split the info, for the testbed() main method
     * @param date passes the date as a string
     */
    public Date(String date) {
        String[] dateSplit = date.split("-");

        this.year = Integer.parseInt(dateSplit[0]);
        this.month = Integer.parseInt(dateSplit[1]);
        this.day = Integer.parseInt(dateSplit[2]);
    }

    /**
     * Getting the year
     * @return returning the year
     */
    public int getYear() {
        return year;
    }

    /**
     * Getting the month
     * @return return the month
     */
    public int getMonth() {
        return month;
    }

    /**
     * Getting the day
     * @return returning the day
     */
    public int getDay() {
        return day;
    }

    /**
     * This boolean method checks if the date of an individual is valid by checking whether it's a valid year, month or day
     * @return returns false if any of the info in the mm/dd/yyyy format is invalid, returns true otherwise
     */
    public boolean isValid() {
        //check for valid year
        if (this.year < 0) {
            return false;
        }
        //check for valid month
        if (this.month > Calendar.DECEMBER + 1 || this.month < Calendar.JANUARY + 1) {
            return false;
        }
        //check for valid leap year
        if (this.month == Calendar.FEBRUARY + 1 && isLeapYear()) {
            if (this.day < 1 || this.day > LEAP_YEAR) return false;
        } else if (this.day < 1 || this.day > DAYS[this.month - 1]) {
            return false;
        }
       return true;
    }

    /**
     * This boolean method calculates whether there is a leap year using the modulo operator
     * @return returns true if there is a leap year, false otherwise
     */
    public boolean isLeapYear() {

        if (this.year % QUADRENNIAL == 0) {
            if (this.year % CENTENNIAL == 0) {
                return this.year % QUATERCENTENNIAL == 0;
            }
            return true;
        }
        return false;
    }

    /**
     * This boolean method checks whether a person is at least 18 years of age
     * @return returns true if the person is at least 18 years old, false otherwise
     */
    public boolean isAdult() {
        Calendar date = Calendar.getInstance();
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH) + 1;
        int day = date.get(Calendar.DATE);

        int age = year - this.year; //getting current age

        //checking if their birthday happened this year yet
        if (this.month > month || (this.month == month && this.day > day)) {
            age--;
        }
        return age >= MINIMUM_AGE;
    }

    /**
     * This method checks whether the year, month and day this equal to the other year, month and day
     * @param o the date object being compared
     * @return returns true if year, month and date are equal, returns false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Date date) {
            return this.year == date.year && this.month == date.month && this.day == date.day;
        }
        return false;
    }

    /**
     * This method compares the year, month, and day to the other year, month and day.
     * @param o the object to be compared.
     * @return returns -1, 0 or 1 for comparison purposes
     */
    @Override
    public int compareTo(Date o) {
        if (this.year != o.year) {
            return this.year - o.year;
        }
        if (this.month != o.month) {
            return this.month - o.month;
        }
        return this.day - o.day;
    }

    /**
     * returns the date
     * @return returns the date in mm/dd/yyyy format
     */
    @Override
    public String toString() {
        return month + "/" + day + "/" + year;
    }

}
