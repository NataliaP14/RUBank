package com.example.project3rubank.bank;

/**
 * This is an enum class for the branches of New Jersey that includes the zip, branchCode and county variables.
 *
 * @author Natalia Peguero, Olivia Kamau
 */
public enum Branch {
    EDISON ("08817", "100", "Middlesex"),
    BRIDGEWATER ("08807", "200", "Somerset"),
    PRINCETON ("08542", "300", "Mercer"),
    PISCATAWAY ("08854", "400", "Middlesex"),
    WARREN ("07057", "500", "Somerset");

    private final String zip;
    private final String branchCode;
    private final String county;

    /**
     * This constructor is used to create the branch object with the specified zip, branch code and county.
     *
     * @param zip the zip code of the location of the branch.
     * @param branchCode the unique code that identifies the branch.
     * @param county the county name where the branch is located.
     */
    Branch(String zip, String branchCode, String county) {
        this.zip = zip;
        this.branchCode = branchCode;
        this.county = county;
    }

    /**
     * Gets the zip code of the branch.
     *
     * @return the zip code.
     */
    public String getZip() {
        return zip;
    }

    /**
     * Gets the unique code of the branch.
     *
     * @return the branch code.
     */
    public String getBranchCode() {
        return branchCode;
    }

    /**
     * Gets the county of the location of the branch
     *
     * @return the county
     */
    public String getCounty() {
        return county;
    }
}

