/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.drk.akrd;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Jo
 */
public class ICalendar extends CalendarManager {

  public ICalendar() {
    super();
  }

  public boolean createICalendarFile(Shift[] shifts, Date[] shiftDates) {
    if (shifts == null || shiftDates == null || shifts.length == 0 || shiftDates.length != shifts.length) {
      return false;
    }
    boolean success = false;
    File iCalendarFile = null;
    FileWriter fileWriter = null;
    String fileEnding = ".ics";
    String month = UtilityBox.getMonthString(getMonth(shiftDates[0]));
    int year = getYear(shiftDates[0]);
    String filePath = "Schichten_" + month + year;
    filePath = saveDialog(fileEnding, filePath);
    if (filePath == null) {
      return false;
    }
    iCalendarFile = new File(filePath);
    try {
      fileWriter = new FileWriter(iCalendarFile);
      fileWriter.write(getFileContent(shifts, shiftDates));
      fileWriter.flush();
    } catch (IOException ex) {
      System.out.println("Exception in ICalendar.createICalendarFile "
              + "(Datei konnte nicht geschrieben werden):" + ex.getMessage());
    } finally {
      try {
        fileWriter.close();
        success = true;
      } catch (IOException ex) {
        System.out.println("Exception in ICalendar.createICalendarFile "
                + "(Datei konnte nicht geschlossen werden):" + ex.getMessage());
      }
    }
    return success;
  }

  private String getFileContent(Shift[] shifts, Date[] dates) {
    String fileContent = ""
            + "BEGIN:VCALENDAR\n"
            + "VERSION:2.0\n"
            + "BEGIN:VTIMEZONE\n"
            + "TZID:Europe/Berlin\n"
            + "X-LIC-LOCATION:Europe/Berlin\n"
            + "BEGIN:DAYLIGHT\n"
            + "TZOFFSETFROM:+0100\n"
            + "TZOFFSETTO:+0200\n"
            + "TZNAME:CEST\n"
            + "DTSTART:19700329T020000\n"
            + "RRULE:FREQ=YEARLY;BYDAY=-1SU;BYMONTH=3\n"
            + "END:DAYLIGHT\n"
            + "BEGIN:STANDARD\n"
            + "TZOFFSETFROM:+0200\n"
            + "TZOFFSETTO:+0100\n"
            + "TZNAME:CET\n"
            + "DTSTART:19701025T030000\n"
            + "RRULE:FREQ=YEARLY;BYDAY=-1SU;BYMONTH=10\n"
            + "END:STANDARD\n"
            + "END:VTIMEZONE\n";
    for (int i = 0; i < shifts.length; i++) {
      Shift shift = shifts[i];
      String[] beginEndStrings = getBeginEndStrings(shift, dates[i], calendarEntryType.ICALENDAR_ENTRY);
      fileContent += ""
              + "BEGIN:VEVENT\n"
              + "DTSTART;TZID=Europe/Berlin:" + beginEndStrings[0] + "\n"
              + "DTEND;TZID=Europe/Berlin:" + beginEndStrings[1] + "\n"
              + "SUMMARY:" + shift.getType().toString() + " " + shift.getId().substring(0, 3) + "\n"
              + "DESCRIPTION:" + "Beschreibung" + "\n"
              + "END:VEVENT\n";
    }

    fileContent += "END:VCALENDAR";
    return fileContent;
  }

  private String saveDialog(String fileEnding, String fileName) {
    String filePath;
    JFileChooser jFileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
    jFileChooser.setMultiSelectionEnabled(false);
    jFileChooser.setSelectedFile(new File(fileName + fileEnding));
    jFileChooser.setFileFilter(new FileFilter() {

      @Override
      public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase().endsWith(".ics");
      }

      @Override
      public String getDescription() {
        return "iCalendar-Dateien";
      }
    });
    int returnValue = jFileChooser.showSaveDialog(null);
    if (returnValue == 1) {
      return null;
    }
    filePath = jFileChooser.getSelectedFile().getPath();
    if (!filePath.endsWith(fileEnding)) {
      filePath += fileEnding;
    }
    //System.out.println("filepath: " + filePath);
    return filePath;
  }
}
