/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.drk.akrd;

import java.io.IOException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import java.text.ParseException;
import java.io.File;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * Parse the monthly duty rota to extract the user-specific shifts
 * @author Jo
 */
public class DRManager {

  private static DRManager INSTANCE = null;
  private static String failMessage = null;
  private static Shift[] savedShifts = null;
  private static Date[] savedShiftDates = null;

  private DRManager() {
  }

  public static DRManager GetInstance() {
    if (INSTANCE == null) {
      INSTANCE = new DRManager();
    }
    return INSTANCE;
  }

  public void parseDutyRota() {
    String filePath = getPdfFilePath();
    if ((filePath == null) || (!(new File(filePath).exists()))) {
      return;
    }
    String[] contentStrings = parsePdf(filePath);
    if (contentStrings == null) {
      String message = failMessage;
      if (message == null) {
        message = "Die Datei kann nicht ausgelesen werden";
      }
      parsingFailed(message);
      return;
    }
    int month;
    int year;
    String[] shiftStrings = contentStrings[1].split(" ");
    String[] endingTimeStrings = contentStrings[2].split(" ");
    int[] shiftDates = new int[shiftStrings.length];
    ArrayList<Shift> shifts = new ArrayList<Shift>();
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    Calendar cal = Calendar.getInstance();
    Date date = cal.getTime();
    try {
      date = sdf.parse(contentStrings[0]);
    } catch (ParseException ex) {
      parsingFailed("Datum kann nicht ausgelesen werden:\n" + ex.getMessage());
      return;
    }
    cal.setTime(date);
    month = cal.get(Calendar.MONTH);
    year = cal.get(Calendar.YEAR);
    int shiftDatesIterator = 0;
    for (int i = 0; i < endingTimeStrings.length; i++) {
      if (!endingTimeStrings[i].equals("0h00")) {
        if (shiftDatesIterator >= shiftDates.length) {
          parsingFailed("Ungültige Dateiformatierung");
          return;
        }
        shiftDates[shiftDatesIterator] = i + 1;
        shiftDatesIterator++;
      }
    }
    ArrayList<Shift> existingShifts = ShiftContainer.getShiftsAsList();
    for (int i = 0; i < shiftStrings.length; i++) {
      for (int j = 0; j < existingShifts.size(); j++) {
        Shift shift = existingShifts.get(j);
        if (shift.getId().startsWith(shiftStrings[i])) {
          shifts.add(shift);
          break;
        }
      }
    }
    savedShifts = new Shift[shifts.size()];
    savedShifts = shifts.toArray(savedShifts);
    savedShiftDates = new Date[shiftDates.length];
    for (int i = 0; i < shiftDates.length; i++) {
      int j = shiftDates[i];
      cal.set(year, month, j);
      savedShiftDates[i] = cal.getTime();
    }
//    for (int i = 0; i < shifts.size(); i++) {
//      Shift shift = shifts.get(i);
//      System.out.println("Schicht " + i + ": " + shift.getId() + " am " + shiftDates[i] + "." + (month + 1) + "." + year);
//    }
  }

  /**
   * open fileChooser dialog to select a dutyrota-file
   * @return filepath
   */
  private static String getPdfFilePath() {
    JFileChooser fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
    fileChooser.setMultiSelectionEnabled(false);
    fileChooser.setFileFilter(new FileFilter() {

      @Override
      public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase().endsWith(".pdf");
      }

      @Override
      public String getDescription() {
        return "Pdf-Dateien";
      }
    });
    int state = fileChooser.showOpenDialog(null);
    if (state == JFileChooser.APPROVE_OPTION) {
      return fileChooser.getSelectedFile().getPath();
    }
    return null;
  }

  /**
   * 
   * @param filename filename of the duty rota-file (pdf)
   * @return a String array
   * returnArray[0] a date-string (dd.mm.yyyy)
   * returnArray[1] String which contains all shifts for the month, seperated by whitespace
   * returnArray[2] endingtimes for the whole month, seperated by whitespace
   */
  private String[] parsePdf(String filename) {
    String[] returnArray = null;
    try {
      PdfReader reader = new PdfReader(filename);
      PdfReaderContentParser parser = new PdfReaderContentParser(reader);
      File tempFile = new File("data" + System.getProperty("file.separator") + "tempFile");
      // TODO: for JDK7 use try-with
      FileWriter fileWriter = null;
      try {//(FileWriter fileWriter = new FileWriter(tempFile)) {
        fileWriter = new FileWriter(tempFile);
        TextExtractionStrategy strategy;
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
          strategy = parser.processContent(i, new LocationTextExtractionStrategy());
          fileWriter.write(strategy.getResultantText());
        }
        fileWriter.flush();
        returnArray = getStings(tempFile);
        fileWriter.close();
      } catch (Exception e) {
        fileWriter.close();
      }

      // delete the temporary file
      if (!tempFile.delete()) {
        System.out.println("Deletation of temp-file in DRManager.parsePDF failed.");
      }
    } catch (IOException ex) {
      parsingFailed(ex.getMessage());
      return null;
    }
    return returnArray;
  }

  /**
   * 
   * @param file the temporary textfile
   * @return a String array
   * returnArray[0] a date-string (dd.mm.yyyy)
   * returnArray[1] String which contains all shifts for the month, seperated by whitespace
   * returnArray[2] endingtimes for the whole month, seperated by whitespace
   */
  private static String[] getStings(File file) {
    BufferedReader bufferedReader = null;
    String[] returnArray = null;
    boolean personFound = false;
    try {
      bufferedReader = new BufferedReader(new FileReader(file));
      returnArray = new String[3];
      String line = bufferedReader.readLine();
      // get Month
      line = bufferedReader.readLine();
      returnArray[0] = bufferedReader.readLine();
      boolean shiftsSaved = false;
      String firstName = PersonalData.getInstance().getFirstName();
      String lastName = PersonalData.getInstance().getLastName();
      // find person
      while (line != null) {
        if (line.contains(lastName + ", " + firstName)) {
          personFound = true;
          break;
        }
        line = bufferedReader.readLine();
      }
      if (personFound) {
        // find shift names
        while (line != null) {
          if (line.contains("Dienstplan")) {
            line = bufferedReader.readLine();
//          System.out.println("dienstplanline:" + line);
            returnArray[1] = line;
            shiftsSaved = true;
            break;
          }
          line = bufferedReader.readLine();
        }
        // find working time strings
        Calendar cal = Calendar.getInstance();
        try {
          cal.setTime(UtilityBox.SIMPLE_DATE_FORMAT.parse(returnArray[0]));
        } catch (ParseException ex) {
          // TODO: handle exception
          System.out.println("Parse Date failed.");
        }
        int nDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        while (line != null) {
          // split line and check for working time strings
          String[] lineStrings = line.split(" ");
          if ((lineStrings.length == nDaysInMonth) && (lineStrings[0].subSequence(1, 2).equals("h"))) {
            returnArray[2] = line;
            break;
          }
          line = bufferedReader.readLine();
        }
      }
    } catch (IOException ex) {
      returnArray = null;
      parsingFailed(ex.getMessage());
    } finally {
      try {
        bufferedReader.close();
      } catch (IOException ex) {
        parsingFailed("BufferedReader not closed:\n" + ex.getMessage());
      }
    }
    if (personFound) {
      return returnArray;
    } else {
      failMessage = "Person '" + PersonalData.getInstance().getFirstName() + " "
              + PersonalData.getInstance().getLastName() + "' nicht gefunden.";
      return null;
    }
  }

  /**
   * display errormessage(parsing failed)
   * @param message 
   */
  private static void parsingFailed(String message) {
    UtilityBox.getInstance().displayErrorPopup("Fehler beim Lesen des Dienstplans", message);
  }

  /**
   * get the dates of parsed shifts
   * @return array of Date
   */
  public Date[] getSavedShiftDates() {
    return savedShiftDates;
  }

  /**
   * get the saved shifts. Make sure you get the correesponding dates too.
   * (call DRManager.getSavedShiftDates())
   * @return array of Shift
   */
  public Shift[] getSavedShifts() {
    return savedShifts;
  }
}
