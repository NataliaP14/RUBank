/**
package com.example.project3rubank.tests;

import org.junit.Test;
import util.Date;

import static org.junit.Assert.*;

public class DateTest {


	@Test
	public void testNegativeMonth() {
		Date d1 = new Date("-1/1/2000");
		assertFalse(d1.isValid());
	}

	@Test
	public void testInvalidLeapYear() {
		Date d2 = new Date("2/29/2009");
		assertFalse(d2.isValid());
	}

	@Test
	public void testInvalidMonth() {
		Date d3 = new Date("20/14/2009");
		assertFalse(d3.isValid());
	}

	@Test
	public void testInvalidDay() {
		Date d4 = new Date("4/31/2001");
		assertFalse(d4.isValid());
	}

	@Test
	public void testValidLeapYear() {
		Date d5 = new Date("2/29/2004");
		assertTrue(d5.isValid());
	}

	@Test
	public void testValidDate() {
		Date d6 = new Date("4/11/2003");
		assertTrue(d6.isValid());
	}
}
 **/