/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.drk.akrd;

import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;

/**
 *
 * @author Jo
 */
public class CalendarManager {
  private Calendar cal = Calendar.getInstance();
  public enum calendarEntryType {

    GOOGLE_ENTRY, ICALENDAR_ENTRY;
  }

  public CalendarManager() {
  }

  public String[] getBeginEndStrings(Shift shift, Date date, calendarEntryType entryType) {
    Calendar cal = Calendar.getInstance();
    String[] returnArray = new String[2];
    int startHour = (int) (shift.getStartingTime() / 100);
    int startMinutes = (int) (shift.getStartingTime() % 100);
    int endHour = (int) (shift.getEndTime() / 100);
    int endMinutes = (int) (shift.getEndTime() % 100);
    cal.setTime(date);
    // begin string
    returnArray[0] = getDateTimeString(cal.getTime(), startHour, startMinutes, entryType);
    if (endHour < startHour) {
      // night shift
      cal.add(Calendar.DATE, 1);
    }
    // end string
    returnArray[1] = getDateTimeString(cal.getTime(), endHour, endMinutes, entryType);

    return returnArray;
  }

  public String getDateTimeString(Date date, int hour, int minute, calendarEntryType entryType) {
    SimpleTimeZone mez = new SimpleTimeZone(+1 * 60 * 60 * 1000, "ECT");
    mez.setStartRule(Calendar.MARCH, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
    mez.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
    cal.setTime(date);
    int year = cal.get(Calendar.YEAR);
    String monthString = UtilityBox.getTwoLetterStringFromInt(cal.get(Calendar.MONTH) + 1);
    String dayString = UtilityBox.getTwoLetterStringFromInt(cal.get(Calendar.DAY_OF_MONTH));
    String seperatorOne = "";
    String seperatorTwo = "";
    if (entryType == calendarEntryType.GOOGLE_ENTRY) {
      seperatorOne = "-";
      seperatorTwo = ":";
    }
    int timeCorrection = (mez.inDaylightTime(cal.getTime())) ? 2 : 1;
    String returnString = year + seperatorOne + monthString + seperatorOne + dayString + "T"
            + UtilityBox.getTwoLetterStringFromInt(hour) + seperatorTwo + UtilityBox.getTwoLetterStringFromInt(minute) + seperatorTwo
            + "00";
    if (entryType == calendarEntryType.GOOGLE_ENTRY) {
      returnString += "+" + UtilityBox.getTwoLetterStringFromInt(timeCorrection) + seperatorTwo + "00";
//    } else if (entryType == calendarEntryType.ICALENDAR_ENTRY) {
//      returnString += "Z";
    }
    return returnString;
  }

  public int getMonth(Date date) {
    cal.setTime(date);
    return cal.get(Calendar.MONTH);
  }
  public int getYear(Date date) {
    cal.setTime(date);
    return cal.get(Calendar.YEAR);
  }
  
}
