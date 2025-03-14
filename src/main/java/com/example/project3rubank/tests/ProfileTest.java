/**
package com.example.project3rubank.tests;

import bank.Profile;
import org.junit.Test;
import util.Date;

import static org.junit.Assert.*;

public class ProfileTest {

	@Test
	public void testLastNameReturnsNegativeOne() {
		Profile p1 = new Profile("Olivia", "Kamau", new Date("4/11/2003"));
		Profile p2 = new Profile("Natalia", "Peguero", new Date("6/27/2004"));
		assertEquals(-1, p1.compareTo(p2));
	}

	@Test
	public void testFirstNameReturnsNegativeOne() {
		Profile p1 = new Profile("Olivia", "Kamau", new Date("4/11/2003"));
		Profile p3 = new Profile("PJ", "Kamau", new Date("1/1/2000"));
		assertEquals(-1, p1.compareTo(p3));
	}

	@Test
	public void testDOBReturnsNegativeOne() {
		Profile p1 = new Profile("Olivia", "Kamau", new Date("4/11/2003"));
		Profile p4 = new Profile("Olivia", "Kamau", new Date("8/14/2005"));
		assertEquals(-1, p1.compareTo(p4));
	}


	@Test
	public void testLastNameReturnsOne() {
		Profile p1 = new Profile("Olivia", "Kamau", new Date("4/11/2003"));
		Profile p5 = new Profile("Sally", "Allen", new Date("10/3/2005"));
		assertEquals(1, p1.compareTo(p5));
	}

	@Test
	public void testFirstNameReturnsOne() {
		Profile p1 = new Profile("Olivia", "Kamau", new Date("4/11/2003"));
		Profile p6 = new Profile("Alice", "Kamau", new Date("12/25/2001"));
		assertEquals(1, p1.compareTo(p6));
	}

	@Test
	public void testDOBReturnsOne() {
		Profile p1 = new Profile("Olivia", "Kamau", new Date("4/11/2003"));
		Profile p7 = new Profile("Olivia", "Kamau", new Date("2/17/2002"));
		assertEquals(1, p1.compareTo(p7));
	}

	@Test
	public void testExactMatchReturnsZero() {
		Profile p1 = new Profile("Olivia", "Kamau", new Date("4/11/2003"));
		Profile p8 = new Profile("Olivia", "Kamau", new Date("4/11/2003"));
		assertEquals(0, p1.compareTo(p8));
	}
}
 **/