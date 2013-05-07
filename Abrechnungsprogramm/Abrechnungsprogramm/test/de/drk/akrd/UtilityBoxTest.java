package de.drk.akrd;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class UtilityBoxTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCalculateSalaryPerHour() {
		ShiftInstance si = new ShiftInstance(-1, ShiftContainer.ShiftType.KTW, "01.01.2013", 800, 900, 0, 0, 0, "testpartner", "");
		
		assertEquals(8.8f, UtilityBox.calculateSalaryPerHour(si, PersonalData.Qualification.RS), 0.001f);
		assertEquals(7.8f, UtilityBox.calculateSalaryPerHour(si, PersonalData.Qualification.RH), 0.001f);
		assertEquals(9.9f, UtilityBox.calculateSalaryPerHour(si, PersonalData.Qualification.RA), 0.001f);
	}
	
	@Test
  public void testCalculateSalary() {
	   ShiftInstance si = new ShiftInstance(-1, ShiftContainer.ShiftType.KTW, "01.01.2013", 800, 900, 0, 0, 0, "testpartner", "");
	   
	   assertEquals(8.8f, UtilityBox.calculateSalary(si, PersonalData.Qualification.RS), 0.0001f);
	   
	   //KTW
	   si = new ShiftInstance(-1, ShiftContainer.ShiftType.KTW, "01.01.2013", 800, 1800, 0, 0, 0, "testpartner", "");
	   assertEquals(88.0f, UtilityBox.calculateSalary(si, PersonalData.Qualification.RS), 0.0001f);
	   
	   si = new ShiftInstance(-1, ShiftContainer.ShiftType.KTW, "01.01.2013", 800, 1800, 30, 0, 10, "testpartner", "");
     assertEquals(85.0666f, UtilityBox.calculateSalary(si, PersonalData.Qualification.RS), 0.0001f);
	   
     // KIZA
	   si = new ShiftInstance(-1, ShiftContainer.ShiftType.KIZA, "01.01.2013", 800, 1800, 0, 12, 0, "testpartner", "");
     assertEquals(88.0f, UtilityBox.calculateSalary(si, PersonalData.Qualification.RA), 0.0001f);
     
     si = new ShiftInstance(-1, ShiftContainer.ShiftType.KIZA, "01.01.2013", 2000, 600, 0, 12, 0, "testpartner", "");
     assertEquals(88.0f, UtilityBox.calculateSalary(si, PersonalData.Qualification.RA), 0.0001f);

     // CONCERT HALL
     si = new ShiftInstance(-1, ShiftContainer.ShiftType.CONCERT_HALL, "01.01.2013", 2000, 2300, 0, 0, 0, "testpartner", "");
     assertEquals(28.5f, UtilityBox.calculateSalary(si, PersonalData.Qualification.RA), 0.0001f);
     assertEquals(28.5f, UtilityBox.calculateSalary(si, PersonalData.Qualification.RH), 0.0001f);
     assertEquals(28.5f, UtilityBox.calculateSalary(si, PersonalData.Qualification.RS), 0.0001f);
     
     // ELW
     si = new ShiftInstance(-1, ShiftContainer.ShiftType.ELW, "01.01.2013", 2000, 200, 0, 0, 0, "testpartner", "");
     assertEquals(52.8f, UtilityBox.calculateSalary(si, PersonalData.Qualification.RA), 0.0001f);
     assertEquals(52.8f, UtilityBox.calculateSalary(si, PersonalData.Qualification.RH), 0.0001f);
     assertEquals(52.8f, UtilityBox.calculateSalary(si, PersonalData.Qualification.RS), 0.0001f);
	}

	@Test
	public void testCalculateTimeAsFloat() {
	  assertEquals(0.0f, UtilityBox.calculateTimeAsFloat(0, 0, 0), 0.0001f);
	  assertEquals(11.5f, UtilityBox.calculateTimeAsFloat(0, 1200, 30), 0.0001f);
	  assertEquals(0.0f, UtilityBox.calculateTimeAsFloat(0, 1200, 1200), 0.0001f);
	  
	  assertEquals(14.0f, UtilityBox.calculateTimeAsFloat(2200, 1200, 0), 0.0001f);
	  assertEquals(13.0f, UtilityBox.calculateTimeAsFloat(2200, 1200, 100), 0.0001f);
	  assertEquals(2.0f, UtilityBox.calculateTimeAsFloat(2200, 0, 0), 0.0001f);
	  assertEquals(2.5f, UtilityBox.calculateTimeAsFloat(2000, 0, 130), 0.0001f);

	}
	
	@Test
	public void testCreateTimeStringFromInt() {
	  assertEquals("00:00", UtilityBox.createTimeStringFromInt(0));
	  assertEquals("01:12", UtilityBox.createTimeStringFromInt(112));
	  assertEquals("23:00", UtilityBox.createTimeStringFromInt(2300));
	}
}
