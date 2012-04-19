/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.drk.akrd;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.FilteredTextRenderListener;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.RegionTextRenderFilter;
import com.itextpdf.text.pdf.parser.RenderFilter;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import java.awt.geom.Rectangle2D;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.itextpdf.text.pdf.PRTokeniser;
import com.itextpdf.text.pdf.PdfReader;
import java.io.PrintWriter;

import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.PdfContentReaderTool;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.parser.PdfContentReaderTool;
import java.io.File;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PRIndirectReference;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PRTokeniser;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Jo
 */
public class PDFReader {

  private static String failMessage = null;
  public static enum TypeOfAction {

    CreateGoogleCalendarEntry,
    CreateIcs;
  }

  public static void parseDutyRota(TypeOfAction typeOfAction) {
    String filePath = getPdfFilePath();
    String[] contentStrings = parsePdf(filePath);
    if (contentStrings == null) {
      String message = failMessage;
      if(message == null) {
        message = "Die Datei kann nicht ausgelesen Werden";
      }
      parsingFailed(message);
      return;
    }
    int month;
    int year;
    String[] shiftStrings = contentStrings[1].split(" ");
    String[] endingTimeStrings = contentStrings[2].split(" ");
    int[] shiftDates = new int[shiftStrings.length];
    ArrayList<Shift> shifts = new ArrayList<>();
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    Calendar cal = Calendar.getInstance();
    Date date = cal.getTime();
    try {
      date = sdf.parse(contentStrings[0]);
      System.out.println("dateToString: " + date.toString());
    } catch (ParseException ex) {
      System.out.println("Exception in PDFReader.parseDutyRota "
              + "(parsing date): " + ex.getMessage());
    }
    cal.setTime(date);
    month = cal.get(Calendar.MONTH);
    year = cal.get(Calendar.YEAR);
    int shiftDatesIterator = 0;
    for (int i = 0; i < endingTimeStrings.length; i++) {
      if (!endingTimeStrings[i].equals("0h00")) {
        if (shiftDatesIterator >= shiftDates.length) {
          parsingFailed("Ungültige Dateiformatierung");
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
    for (int i = 0; i < shifts.size(); i++) {
      Shift shift = shifts.get(i);
      System.out.println("Schicht " + i + ": " + shift.getId() + " am " + shiftDates[i] + "." + (month + 1) + "." + year);
    }
    if (typeOfAction == TypeOfAction.CreateGoogleCalendarEntry) {
      String[][] entryStrings = new String[shifts.size()][4];
      SimpleTimeZone mez = new SimpleTimeZone(+1 * 60 * 60 * 1000, "ECT");
      mez.setStartRule(Calendar.MARCH, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
      mez.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
      for (int i = 0; i < shifts.size(); i++) {
        Shift shift = shifts.get(i);
        int day = shiftDates[i];
        int startHour = (int) (shift.getStartingTime() / 100);
        int startMinutes = (int) (shift.getStartingTime() % 100);
        int endHour = (int) (shift.getEndTime() / 100);
        int endMinutes = (int) (shift.getEndTime() % 100);
        cal.set(year, month, day);
        int timeCorrection = (mez.inDaylightTime(cal.getTime()))? 2: 1;
        String beginString = getDateTimeString(year, month, day, startHour, startMinutes, timeCorrection);
        if(endHour<startHour) {
          // night shift
          cal.add(Calendar.DATE, 1);
          day = cal.get(Calendar.DAY_OF_MONTH);
          month = cal.get(Calendar.MONTH);
          year = cal.get(Calendar.YEAR);
        }
        String endString = getDateTimeString(year, month, day, endHour, endMinutes, timeCorrection);
        entryStrings[i][0] = shiftStrings[i];
        entryStrings[i][1] = "Termintext";
        entryStrings[i][2] = beginString;
        entryStrings[i][3] = endString;
      }
      GoogleConnect.createNewAppointment(entryStrings);
    }

  }

private static String getTwoLetterStringFromInt(int x) {
  return ((x<10)? ("0"+x): x+"");
}
private static String getPdfFilePath() {
  JFileChooser fileChooser = new JFileChooser();
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
    System.out.println(fileChooser.getSelectedFile().getPath());
    return fileChooser.getSelectedFile().getPath();
  }
  return null;
}
private static String getDateTimeString(int year, int month, int day, int hour, int minute, int timeCorrection) {
  String monthString = getTwoLetterStringFromInt(month + 1);
  String dayString = getTwoLetterStringFromInt(day);
  String returnString = year + "-" + monthString + "-" + dayString + "T"
          + getTwoLetterStringFromInt(hour) + ":"
          + getTwoLetterStringFromInt(minute) + ":" + "00+0"
          + timeCorrection + ":00";
  return returnString;
}
  /**
   * 
   * @param filename filename of the duty rota-file (pdf)
   * @return a String array
   * returnArray[0] a date-string (dd.mm.yyyy)
   * returnArray[1] String which contains all shifts for the month, seperated by whitespace
   * returnArray[2] endingtimes for the whole month, seperated by whitespace
   */
  public static String[] parsePdf(String filename) {
    String[] returnArray = null;
    try {
      PdfReader reader = new PdfReader(filename);
      PdfReaderContentParser parser = new PdfReaderContentParser(reader);
      File tempFile = new File("data/tempFile");
      try (FileWriter fileWriter = new FileWriter(tempFile)) {
        TextExtractionStrategy strategy;
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
          strategy = parser.processContent(i, new LocationTextExtractionStrategy());
          fileWriter.write(strategy.getResultantText());
        }
        fileWriter.flush();
        returnArray = getStings(tempFile);
      }
      // delete the temporary file
      if (!tempFile.delete()) {
        System.out.println("Deletation of temp-file in PDFReader.parsePDF failed.");
      }

    } catch (IOException ex) {
      System.out.println("Exception in PDFReader.parsePdf: " + ex.getMessage());
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
      while (line != null) {
        if (line.contains(lastName+", "+firstName)) {
          System.out.println(line);
          personFound = true;
        } else if (personFound && line.contains("RS Dienstplan")) {
          line = bufferedReader.readLine();
          returnArray[1] = line;
          System.out.println(line);
          shiftsSaved = true;
        } else if (shiftsSaved && line.contains("Ende")) {
          line = bufferedReader.readLine();
          returnArray[2] = line;
          System.out.println(line);
          break;
        }
        line = bufferedReader.readLine();
      }


    } catch (IOException ex) {
      returnArray = null;
      System.out.println("Exception in PDFReader.getStrings: " + ex.getMessage());
    } finally {
      try {
        bufferedReader.close();
      } catch (IOException ex) {
        System.out.println("Exception in PDFReader.getStrings; "
                + "BufferedReader not closed: " + ex.getMessage());
      }
    }
    if (personFound) {
      return returnArray;
    }
    else {
      failMessage = "Person '"+PersonalData.getInstance().getFirstName()+" "+
              PersonalData.getInstance().getLastName()+"' nicht gefunden.";
      return null;
    }
  }

  private static void parsingFailed(String message) {
    JOptionPane.showMessageDialog(null, message, "Fehler beim Lesen des Dienstplans", JOptionPane.ERROR_MESSAGE);
  }
}
