package com.example.project3rubank.bank;

import com.example.project3rubank.util.Date;

/**
 * The profile class represents a user profile with first name, last name and
 * date of birth.
 * It has methods to compare profiles, check equality between profiles and
 * return a formatted string for each user profile.
 *
 * @author Natalia Peguero, Olivia Kamau
 */
public class Profile implements Comparable<Profile>{

    private String fname;
    private String lname;
    private Date dob;

    /**
     * This constructor creates a profile object that holds the first name,
     * last name and date of birth.
     *
     * @param fname is the first name
     * @param lname is the last name
     * @param dob is the date of birth
     */
    public Profile(String fname, String lname, Date dob) {
        this.fname = fname;
        this.lname = lname;
        this.dob = dob;
    }

    /**
     * Returns the value stored in the fname variable.
     *
     * @return  returns the values.
     */
    public String getFirstName() {
        return fname;
    }

    /**
     * Returns the value stored in the lname variable.
     *
     * @return  returns the values.
     */
    public String getLastName() {
        return lname;
    }

    /**
     * Returns the value stored in the dob variable.
     *
     * @return  returns the values.
     */
    public Date getDateOfBirth() {
        return dob;
    }

    /**
     * compareTo() method (compares current profile object to other profile
     * objects for ordering)
     *
     * @param other the object to be compared.
     * @return Negative int if this profile comes before the other
     *         Positive int if this profile comes after the other
     *         Zero if both profiles are equal
     */
    @Override
    public int compareTo(Profile other) {

        // Compares last names
        if(this.lname.compareToIgnoreCase(other.lname)>0) return 1;
        else if(this.lname.compareToIgnoreCase(other.lname)<0) return -1;
        // Compare first names
        else if(this.fname.compareToIgnoreCase(other.fname)>0) return 1;
        else if(this.fname.compareToIgnoreCase(other.fname)<0) return -1;
        // Compare dob's
        else if (this.dob.compareTo(other.dob)>0) return 1;
        else if(this.dob.compareTo(other.dob)<0) return -1;

        return 0;
    }


    /**
     * equals() method: This checks whether this profile object is equal to
     * another object
     *
     * @param obj to compare with this profile
     * @return true if this profile object matches another profile object;
     * false otherwise
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass()) return false;

        Profile profile = (Profile) obj;
        return fname.equalsIgnoreCase(profile.fname) &&
                lname.equalsIgnoreCase(profile.lname) &&
                dob.equals(profile.dob);

    }

    /**
     * toString() method: Returns a string represenation of the Profile object
     *
     * @return A formatted string with the first name, last name and date of
     * birth
     */
    @Override
    public String toString() {
        return fname + " " + lname + " " + dob.toString();
    }

}
