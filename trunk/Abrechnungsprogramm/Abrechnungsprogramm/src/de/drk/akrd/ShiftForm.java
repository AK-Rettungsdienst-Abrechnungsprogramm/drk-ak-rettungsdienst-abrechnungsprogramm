/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.drk.akrd;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Calendar;
import sun.invoke.empty.Empty;

/**
 *
 * @author Jo
 */
public class ShiftForm {

  private static ShiftForm INSTANCE = null;
  public enum TimeCode {EMPTY, X, F, S, T, N}
  private ShiftForm() {
  }

  public static ShiftForm getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new ShiftForm();
    }
    return INSTANCE;
  }

  public boolean createShiftFormPdf() {
    try {
      Document shiftFormDocument = new Document();
      PdfWriter pdfWriter = PdfWriter.getInstance(shiftFormDocument, new FileOutputStream("Dienstmöglichkeitennn.pdf"));
      shiftFormDocument.open();
      shiftFormDocument.newPage();
      TimeCode[] timeCodes = new TimeCode[29];
      for (int i = 0; i < timeCodes.length; i++) {
        timeCodes[i] = TimeCode.EMPTY;
      }
      timeCodes[5] = TimeCode.T;
      timeCodes[10] = TimeCode.X;
      timeCodes[11] = TimeCode.F;
      timeCodes[17] = TimeCode.S;
      timeCodes[28] = TimeCode.N;
      createPdf(shiftFormDocument, timeCodes, 1, 1, 5);

      shiftFormDocument.close();
    } catch (FileNotFoundException | DocumentException ex) {
      UtilityBox.getInstance().displayErrorPopup("Fehler beim Erzeugen des Fragebogens", ex.getMessage());
    }
    return false;
  }

  private boolean createPdf(Document shiftFormDocument, TimeCode[] timeCodes, int month, int year, int maxShifts) {
    try {
      Font helveticaFont8 = FontFactory.getFont(FontFactory.HELVETICA, 8);
      Font helveticaFont8Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
      Font helveticaFont9 = FontFactory.getFont(FontFactory.HELVETICA, 9);
      Font helveticaFont10 = FontFactory.getFont(FontFactory.HELVETICA, 10);
      Font helveticaFont9Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
      Font helveticaFont11Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
      Font helveticaFont11 = FontFactory.getFont(FontFactory.HELVETICA, 11);
      Font helveticaFont12 = FontFactory.getFont(FontFactory.HELVETICA, 12);
      Font helveticaFont14 = FontFactory.getFont(FontFactory.HELVETICA, 14);
      Font helveticaFont16Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
      Font helveticaFont18Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
      
      Image drkLogo = Image.getInstance("images/logo_Abrechnung.jpg");
      drkLogo.scaleAbsolute(145f, 19f);
      drkLogo.setAbsolutePosition(400f, 775f);
      PdfPTable table1 = new PdfPTable(7);
      table1.setWidthPercentage(100);
      float[] table1CellWidth = new float[]{1.48f,1.99f,2.35f,4.56f,2.41f,2.07f,4.11f};
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
        table1.addCell(getNewCell(7, Element.ALIGN_CENTER, helveticaFont9, null, Rectangle.NO_BORDER));
        table1.addCell(getNewCell(7, Element.ALIGN_CENTER, helveticaFont9, null, Rectangle.NO_BORDER));
        table1.addCell(getNewCell(4, Element.ALIGN_LEFT, helveticaFont11Bold, "Name, Vorname", Rectangle.NO_BORDER));
        table1.addCell(getNewCell(1, Element.ALIGN_LEFT, helveticaFont11Bold, "Monat", Rectangle.NO_BORDER));
        table1.addCell(getNewCell(1, Element.ALIGN_LEFT, helveticaFont11Bold, "Jahr", Rectangle.NO_BORDER));
        table1.addCell(getNewCell(1, Element.ALIGN_LEFT, helveticaFont11Bold, "max. Dienste", Rectangle.NO_BORDER));
        table1.addCell(getNewCell(4, Element.ALIGN_CENTER, helveticaFont11Bold, nameString));
        table1.addCell(getNewCell(1, Element.ALIGN_CENTER, helveticaFont11Bold, String.valueOf(month+1)));
        table1.addCell(getNewCell(1, Element.ALIGN_CENTER, helveticaFont11Bold, String.valueOf(year)));
        table1.addCell(getNewCell(1, Element.ALIGN_CENTER, helveticaFont11Bold, String.valueOf(maxShifts)));
        table1.addCell(getNewCell(7, Element.ALIGN_CENTER, helveticaFont9, null, Rectangle.NO_BORDER));
        table1.addCell(getNewCell(7, Element.ALIGN_CENTER, helveticaFont9, null, Rectangle.NO_BORDER));
      }
      
      // Block 3 month-table
      {
        table1.addCell(getNewCell(1, Element.ALIGN_LEFT, helveticaFont12, "Tag:"));
        table1.addCell(getNewCell(1, Element.ALIGN_LEFT, helveticaFont12, "Datum:"));
        table1.addCell(getNewCell(1, Element.ALIGN_LEFT, helveticaFont12, "Kürzel:"));
        table1.addCell(getNewCell(4, Element.ALIGN_LEFT, helveticaFont12, "Besonderheiten:"));
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        PdfPCell cell1, cell2, cell3, cell4;
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
          table1.addCell(getNewCell(1, Element.ALIGN_CENTER, helveticaFont12, weekday));
          table1.addCell(getNewCell(1, Element.ALIGN_CENTER, helveticaFont12, date));
          table1.addCell(getNewCell(1, Element.ALIGN_CENTER, helveticaFont12, timeCodeString));
          table1.addCell(getNewCell(4, Element.ALIGN_CENTER, helveticaFont12, ""));
          calendar.add(Calendar.DATE, 1);
        }
      }
      
      // Block 4 shortcuts and "Mentorenschichten"
      {
      table1.addCell(getNewCell(7, Element.ALIGN_CENTER, helveticaFont9, null, Rectangle.NO_BORDER));
      table1.addCell(getNewCell(2, Element.ALIGN_RIGHT, helveticaFont11Bold, "Kürzel:", Rectangle.NO_BORDER));
      table1.addCell(getNewCell(5, Element.ALIGN_LEFT, helveticaFont11, "X: ganzer Tag / F: Frühdienst / S: Spätdienst / T: Tag (Früh&Spät) / N: Nachtdienst", Rectangle.NO_BORDER));
      table1.addCell(getNewCell(7, Element.ALIGN_CENTER, helveticaFont9, null, Rectangle.NO_BORDER));
      PdfPCell lineCell = getNewCell(7, Element.ALIGN_CENTER, helveticaFont9, null);
      lineCell.setFixedHeight(0.5f);
      table1.addCell(lineCell);
      table1.addCell(getNewCell(7, Element.ALIGN_CENTER, helveticaFont9, null, Rectangle.NO_BORDER));
      table1.addCell(getNewCell(7, Element.ALIGN_CENTER, helveticaFont9, null, Rectangle.NO_BORDER));
      table1.addCell(getNewCell(7, Element.ALIGN_CENTER, helveticaFont11Bold, "Anzahl benötigter Mentorenschichten:", Rectangle.NO_BORDER));
      table1.addCell(getNewCell(7, Element.ALIGN_CENTER, helveticaFont9, null, Rectangle.NO_BORDER));
    }
      shiftFormDocument.add(table1);
      shiftFormDocument.add(drkLogo);
    } catch (BadElementException ex) {
      ex.printStackTrace();
    } catch (MalformedURLException ex) {
      ex.printStackTrace();
    } catch (IOException ex) {
      ex.printStackTrace();
    } catch (DocumentException ex) {
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
    returnCell.setMinimumHeight(15f);
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
}
