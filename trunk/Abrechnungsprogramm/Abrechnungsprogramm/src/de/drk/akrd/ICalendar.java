/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.drk.akrd;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;

/**
 *
 * @author Jo
 */
public class ICalendar extends CalendarManager {
  public ICalendar() {
    super();
  }
  public boolean createICalendarFile(Shift[] shifts, Date[] shiftDates) {
    if (shifts == null || shiftDates == null) {
         return false;
       }
    boolean success = false;
    FileWriter fileWriter = null;
    try {
      
      File iCalendarFile = new File("termine.ics");
      fileWriter = new FileWriter(iCalendarFile);
      fileWriter.write(getFileContent(shifts, shiftDates));
      fileWriter.flush();
    } catch (IOException ex) {
      System.out.println("Exception in ICalendar.createICalendarFile "
              + "(Datei konnte nicht geschrieben werden):"+ex.getMessage());
    } finally {
      try {
        fileWriter.close();
        success = true;
      } catch (IOException ex) {
        System.out.println("Exception in ICalendar.createICalendarFile "
              + "(Datei konnte nicht geschlossen werden):"+ex.getMessage());
      }
    }
    return success;
  }
  private String getFileContent(Shift[] shifts, Date[] dates) {
    String fileContent = "BEGIN:VCALENDAR\nVERSION:2.0\n";
    for (int i = 0; i < shifts.length; i++) {
      Shift shift = shifts[i];
      String[] beginEndStrings = getBeginEndStrings(shift, dates[i], calendarEntryType.ICALENDAR_ENTRY);
      fileContent +=""
              + "BEGIN:VEVENT\n"
              + "DTSTART:"+beginEndStrings[0]+"\n"
              + "DTEND:"+beginEndStrings[1]+"\n"
              + "SUMMARY:"+shift.getId().substring(0, 3)+"\n"
              + "END:VEVENT\n";
    }
    
    fileContent+="END:VCALENDAR";
    return fileContent;
  }
}
