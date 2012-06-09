/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.drk.akrd;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Calendar;

/**
 *
 * @author Jo
 */
public class ShiftForm {

  private static ShiftForm INSTANCE = null;
  private static final float minimumCellHeight = 14f;

  public enum TimeCode {

    EMPTY, X, F, S, T, N
  }

  private ShiftForm() {
  }

  public static ShiftForm getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new ShiftForm();
    }
    return INSTANCE;
  }

  public boolean createShiftFormPdf(TimeCode[] timeCodes, int month, int year, int maxShifts, int mentorShift2ndPos, int mentorShift3rdPos) {
    File dir = new File("Frageboegen");
    if (!dir.isDirectory()) {
      dir.mkdir();
    }
    Document shiftFormDocument = new Document();
    shiftFormDocument.setPageSize(PageSize.A4);
    String fileName = "Fragebogen_" + UtilityBox.getMonthString(month) + "_" + year + ".pdf";
    String filePath = "Frageboegen/" + fileName;
    boolean pdfCreated = false;
    try {
      PdfWriter pdfWriter = PdfWriter.getInstance(shiftFormDocument, new FileOutputStream(filePath));
      shiftFormDocument.open();
      shiftFormDocument.newPage();
      pdfCreated = createPdf(shiftFormDocument, timeCodes, month, year, maxShifts, mentorShift2ndPos, mentorShift3rdPos);
      shiftFormDocument.close();
    } catch (FileNotFoundException | DocumentException ex) {
      UtilityBox.getInstance().displayErrorPopup("Fehler beim Erzeugen des Fragebogens", ex.getMessage());
      return false;
    }
    if (pdfCreated) {
      UtilityBox.getInstance().displayInfoPopup("Dienst-Fragebogen", "Der Fragebogen wurde unter " + filePath + " gespeichert.");
      //sendEMail(filePath, fileName, month, year);
      return true;
    }
    return false;
  }

  private boolean createPdf(Document shiftFormDocument, TimeCode[] timeCodes, int month, int year, int maxShifts, int mentorShift2ndPos, int mentorShift3rdPos) {
    try {
      Font helveticaFont8 = FontFactory.getFont(FontFactory.HELVETICA, 8);
      Font helveticaFont8Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
      Font helveticaFont9 = FontFactory.getFont(FontFactory.HELVETICA, 9);
      Font helveticaFont9Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
      Font helveticaFont10 = FontFactory.getFont(FontFactory.HELVETICA, 10);
      Font helveticaFont10Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
      Font helveticaFont11Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
      Font helveticaFont11 = FontFactory.getFont(FontFactory.HELVETICA, 11);
      Font helveticaFont12 = FontFactory.getFont(FontFactory.HELVETICA, 12);
      Font helveticaFont14 = FontFactory.getFont(FontFactory.HELVETICA, 14);
      Font helveticaFont16Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
      Font helveticaFont18Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);

      Image drkLogo = Image.getInstance(MainWindow.class.getResource("logo_Abrechnung.jpg"));
      drkLogo.scaleAbsolute(152f, 19f);
      drkLogo.setAbsolutePosition(400f, 780f);
      PdfPTable table1 = new PdfPTable(7);
      table1.setWidthPercentage(100);
      float[] table1CellWidth = new float[]{1.48f, 2.25f, 2.35f, 4.56f, 2.41f, 2.07f, 4.11f};
      table1.setWidths(table1CellWidth);
      // Block 1 (headline etc)
      {
        PdfPCell cell1 = getNewCell(3, Element.ALIGN_LEFT, helveticaFont8,
                "Rettungsdienst Freiburg gGmbH\nBereich Freiburg");
        cell1.setRowspan(2);
        cell1.setVerticalAlignment(Element.ALIGN_TOP);
        PdfPCell cell2 = getNewCell(2, Element.ALIGN_CENTER, helveticaFont8Bold, "Fragebogen");
        PdfPCell cell3 = getNewCell(2, Element.ALIGN_CENTER, null, null);
        cell3.setRowspan(2);
        PdfPCell cell4 = getNewCell(2, Element.ALIGN_CENTER, helveticaFont16Bold, "Dienstmöglichkeiten");
        cell4.setMinimumHeight(18);
        cell4.setVerticalAlignment(Element.ALIGN_TOP);
        PdfPCell cell5 = getNewCell(2, Element.ALIGN_LEFT, helveticaFont8, "Gültig ab: 01.01.2010");
        PdfPCell cell6 = getNewCell(2, Element.ALIGN_LEFT, helveticaFont8, "Aushang bis:");
        PdfPCell cell7 = getNewCell(2, Element.ALIGN_LEFT, helveticaFont8, "Ablage: DPL Fragebogen");
        PdfPCell cell8 = getNewCell(1, Element.ALIGN_LEFT, helveticaFont8, "Archivierung bis: 3 Monate");


        table1.addCell(cell1);
        table1.addCell(cell2);
        table1.addCell(cell3);
        table1.addCell(cell4);
        table1.addCell(cell5);
        table1.addCell(cell6);
        table1.addCell(cell7);
        table1.addCell(cell8);
      }

      // block 2 name, month, year
      {
        PersonalData personalData = PersonalData.getInstance();
        String nameString = personalData.getLastName() + ", " + personalData.getFirstName();
        table1.addCell(getEmptyCell(7, Rectangle.NO_BORDER));
        table1.addCell(getEmptyCell(7, Rectangle.NO_BORDER));
        PdfPCell cell1 = getNewCell(4, Element.ALIGN_LEFT, helveticaFont11Bold, "Name, Vorname", Rectangle.NO_BORDER);
        cell1.setMinimumHeight(16);
        table1.addCell(cell1);
        table1.addCell(getNewCell(1, Element.ALIGN_LEFT, helveticaFont11Bold, "Monat", Rectangle.NO_BORDER));
        table1.addCell(getNewCell(1, Element.ALIGN_LEFT, helveticaFont11Bold, "Jahr", Rectangle.NO_BORDER));
        table1.addCell(getNewCell(1, Element.ALIGN_LEFT, helveticaFont11Bold, "max. Dienste", Rectangle.NO_BORDER));
        BaseColor yellow = new BaseColor(255, 255, 185);
        PdfPCell cell2 = getNewCell(4, Element.ALIGN_CENTER, helveticaFont11Bold, nameString);
        cell2.setBackgroundColor(yellow);
        table1.addCell(cell2);
        PdfPCell cell3 = getNewCell(1, Element.ALIGN_CENTER, helveticaFont11Bold, String.valueOf(month + 1));
        cell3.setBackgroundColor(yellow);
        table1.addCell(cell3);
        PdfPCell cell4 = getNewCell(1, Element.ALIGN_CENTER, helveticaFont11Bold, String.valueOf(year));
        cell4.setBackgroundColor(yellow);
        table1.addCell(cell4);
        PdfPCell cell5 = getNewCell(1, Element.ALIGN_CENTER, helveticaFont11Bold, String.valueOf(maxShifts));
        cell5.setBackgroundColor(yellow);
        table1.addCell(cell5);
        PdfPCell cell6 = getEmptyCell(7, Rectangle.NO_BORDER);
        cell6.setRowspan(2);
        cell6.setFixedHeight(20);
        table1.addCell(cell6);
      }

      // Block 3 month-table
      {
        table1.addCell(getNewCell(1, Element.ALIGN_LEFT, helveticaFont11, "Tag:"));
        table1.addCell(getNewCell(1, Element.ALIGN_LEFT, helveticaFont11, "Datum:"));
        table1.addCell(getNewCell(1, Element.ALIGN_LEFT, helveticaFont11, "Kürzel:"));
        table1.addCell(getNewCell(4, Element.ALIGN_LEFT, helveticaFont11, "Besonderheiten:"));
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        for (int i = 0; i < 31; i++) {
          String weekday = "", date = "", timeCodeString = "";
          if (calendar.get(Calendar.MONTH) == month && i < timeCodes.length) {
            weekday = UtilityBox.getDayOfWeekString(calendar.get(Calendar.DAY_OF_WEEK));
            date = UtilityBox.getFormattedDateString(calendar.getTime());
            TimeCode timeCode = timeCodes[i];
            if (timeCode != TimeCode.EMPTY) {
              timeCodeString = timeCode.name();
            }
          }
          table1.addCell(getNewCell(1, Element.ALIGN_CENTER, helveticaFont11, weekday));
          table1.addCell(getNewCell(1, Element.ALIGN_CENTER, helveticaFont11, date));
          table1.addCell(getNewCell(1, Element.ALIGN_CENTER, helveticaFont11, timeCodeString));
          table1.addCell(getNewCell(4, Element.ALIGN_CENTER, helveticaFont11, ""));
          calendar.add(Calendar.DATE, 1);
        }
      }

      // Block 4 shortcuts and "Mentorenschichten"
      {
        PdfPCell cell2 = getEmptyCell(7, Rectangle.NO_BORDER);
        cell2.setRowspan(2);
        cell2.setFixedHeight(20);
        table1.addCell(cell2);
        table1.addCell(getNewCell(2, Element.ALIGN_RIGHT, helveticaFont10, "Kürzel: ", Rectangle.NO_BORDER));
        table1.addCell(getNewCell(5, Element.ALIGN_LEFT, helveticaFont10, "X: ganzer Tag / F: Frühdienst / S: Spätdienst / T: Tag (Früh&Spät) / N: Nachtdienst", Rectangle.NO_BORDER));
        PdfPCell cell = getEmptyCell(7, Rectangle.NO_BORDER);
        cell.setFixedHeight(10);
        table1.addCell(cell);
        PdfPCell lineCell = getNewCell(7, Element.ALIGN_CENTER, helveticaFont9, null);
        lineCell.setFixedHeight(0.5f);
        table1.addCell(lineCell);
        PdfPCell cell1 = getEmptyCell(7, Rectangle.NO_BORDER);
        cell1.setRowspan(2);
        cell1.setFixedHeight(20);
        table1.addCell(cell1);
        table1.addCell(getNewCell(7, Element.ALIGN_CENTER, helveticaFont10Bold, "Anzahl benötigter Mentorenschichten:", Rectangle.NO_BORDER));
        table1.addCell(getEmptyCell(7, Rectangle.NO_BORDER));
        table1.addCell(getEmptyCell(3, Rectangle.NO_BORDER));
        table1.addCell(getNewCell(1, Element.ALIGN_RIGHT, helveticaFont10, "2. Position:", Rectangle.NO_BORDER));
        table1.addCell(getNewCell(1, Element.ALIGN_RIGHT, helveticaFont10, String.valueOf(mentorShift2ndPos)));
        table1.addCell(getEmptyCell(2, Rectangle.NO_BORDER));
        table1.addCell(getEmptyCell(7, Rectangle.NO_BORDER));
        table1.addCell(getEmptyCell(3, Rectangle.NO_BORDER));
        table1.addCell(getNewCell(1, Element.ALIGN_RIGHT, helveticaFont10, "3. Position:", Rectangle.NO_BORDER));
        table1.addCell(getNewCell(1, Element.ALIGN_RIGHT, helveticaFont10, String.valueOf(mentorShift3rdPos)));
        table1.addCell(getEmptyCell(2, Rectangle.NO_BORDER));
        table1.addCell(getEmptyCell(7, Rectangle.NO_BORDER));
        table1.addCell(getEmptyCell(7, Rectangle.NO_BORDER));
      }

      // Block 5 Version etc.
      {
        PdfPCell cell1 = getNewCell(2, Element.ALIGN_LEFT, helveticaFont8, "Version 1.0\nStand: 05.01.10");
        cell1.setRowspan(2);
        table1.addCell(cell1);
        PdfPCell cell2 = getNewCell(2, Element.ALIGN_LEFT, helveticaFont8, "Erstellt / Aktualisiert:\nMager, I. / Kälble, R.");
        cell2.setRowspan(2);
        table1.addCell(cell2);
        PdfPCell cell3 = getNewCell(2, Element.ALIGN_LEFT, helveticaFont8, "Freigegeben:\nKälble, R.");
        cell3.setRowspan(2);
        table1.addCell(cell3);
        PdfPCell cell4 = getNewCell(2, Element.ALIGN_LEFT, helveticaFont8, "Seite 1 von 1\nfragebogen.doc");
        cell4.setRowspan(2);
        table1.addCell(cell4);

      }
      shiftFormDocument.add(table1);
      shiftFormDocument.add(drkLogo);
    } catch (IOException | DocumentException ex) {
      UtilityBox.getInstance().displayErrorPopup("Fehler beim Erzeugen des Fragebogen-Pdfs", ex.getMessage());
      return false;
    }
    return true;
  }

  /**
   * return a new PdfPCell with fixed heigth and specific columnspan
   * @param colspan 
   * @param text
   * @return new PdfPCell
   */
  private PdfPCell getNewCell(int colspan, int horizontalAlignment, Font font, String text) {
    PdfPCell returnCell = new PdfPCell(new Paragraph(text, font));
    returnCell.setMinimumHeight(minimumCellHeight);
    returnCell.setColspan(colspan);
    returnCell.setHorizontalAlignment(horizontalAlignment);
    returnCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    return returnCell;
  }

  private PdfPCell getNewCell(int colspan, int horizontalAlignment, Font font, String text, int border) {
    PdfPCell returnCell = getNewCell(colspan, horizontalAlignment, font, text);
    returnCell.setBorder(border);
    return returnCell;
  }

  private PdfPCell getEmptyCell(int colspan, int border) {
    PdfPCell returnCell = new PdfPCell();
    returnCell.setColspan(colspan);
    returnCell.setBorder(border);
    returnCell.setMinimumHeight(minimumCellHeight - 3f);
    return returnCell;
  }

  private void sendEMail(String filePath, String fileName, int month, int year) {
    PersonalData personalData = PersonalData.getInstance();
    String spaceUTF8 = "\u00A0";
    String subject = "Fragebogen" + spaceUTF8 + personalData.getFirstName() 
            + spaceUTF8 + personalData.getLastName() + spaceUTF8
            + UtilityBox.getMonthString(month) + spaceUTF8 + year;
    String body = "Angehängt:" + spaceUTF8 + fileName;
    try {
      body = URLEncoder.encode(body, "UTF-8");
      subject = URLEncoder.encode(subject, "UTF-8");
      filePath = "'file:///C:/test.txt'";
      System.out.println("mailprog: ");
      Desktop.getDesktop().mail(new URI(
              "mailto:thomas.franz@drk-freiburg.de?subject=" + subject + "&body="
              + body + "&attachment=" + filePath));
      System.out.println("pfad: "+filePath);
      System.out.println("datei existiert: "+new File(filePath).exists());
    } catch (URISyntaxException | IOException ex) {
      UtilityBox.getInstance().displayErrorPopup("Fehler beim erstellen der Email", ex.getMessage());
      System.out.println(ex.getMessage());
      
    }
  }
}
