package de.drk.akrd;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.ColumnText;
import java.io.*;
import java.net.MalformedURLException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.RadioCheckField;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfContentByte;
import java.text.DecimalFormat;
import java.util.*;

/**
 *
 * @author Jo
 */
public class PdfCreator {

  public PdfCreator() {
  }

  public static void createAccounting(ShiftInstance[] shiftsToAccount) {
    ArrayList<ShiftInstance> rd = new ArrayList<>();
    ArrayList<ShiftInstance> ktp = new ArrayList<>();
    ArrayList<ShiftInstance> baby = new ArrayList<>();
    ArrayList<ShiftInstance> breisach = new ArrayList<>();
    ArrayList<ShiftInstance> kiza = new ArrayList<>();
    ArrayList<ShiftInstance> event = new ArrayList<>();
    ArrayList<ShiftInstance> kvs = new ArrayList<>();
    System.out.println("shiftsToAccount: "+shiftsToAccount.length);
    for (int i = 0; i < shiftsToAccount.length; i++) {
      switch (shiftsToAccount[i].getType()) {
        case ShiftContainer.KTW:
          ktp.add(shiftsToAccount[i]);
          break;
        case ShiftContainer.RTW:
          rd.add(shiftsToAccount[i]);
          break;
        case ShiftContainer.EVENT:
          event.add(shiftsToAccount[i]);
          break;
        case ShiftContainer.KVS:
          kvs.add(shiftsToAccount[i]);
          break;
        case ShiftContainer.BABY:
          baby.add(shiftsToAccount[i]);
          break;
        case ShiftContainer.BREISACH:
          breisach.add(shiftsToAccount[i]);
          break;
        case ShiftContainer.KIZA:
          kiza.add(shiftsToAccount[i]);
          break;
        default:
          break;
      }

    }
    ArrayList<ShiftInstance>[] allShifts = new ArrayList<>[]{rd, ktp, baby, breisach, kiza, event, kvs};
    Document accounting = new Document();
    for (int i = 0; i < allShifts.length; i++) {
      if (!allShifts[i].isEmpty()) {
        int numberOfPages = ((int) (allShifts[i].size() / 13)) + 1;
        String fileName;
        switch (allShifts[i].get(0).getType()) {
          case ShiftContainer.KTW:
            fileName = "KTWFr";
            break;
          case ShiftContainer.RTW:
            fileName = "RTWFr";
            break;
          case ShiftContainer.EVENT:
            fileName = "Veranstaltung";
            break;
          case ShiftContainer.KVS:
            fileName = "KVS";
            break;
          case ShiftContainer.BABY:
            fileName = "BabyNAW";
            break;
          case ShiftContainer.BREISACH:
            fileName = "Breisach";
            break;
          case ShiftContainer.KIZA:
            fileName = "KiZa";
            break;
          default:
            fileName = "";
            break;
        }
        int counter = 0;
        for (int j = 1; j <= numberOfPages; j++) {
          ArrayList<ShiftInstance> tempShiftInstances = new ArrayList<>();
          for (int k = 0; k < 13; k++) {
            if (counter >= (allShifts[i].size())) {
              break;
            }
            tempShiftInstances.add(allShifts[i].get(counter));
            counter++;
          }
          if (numberOfPages > 1) {
            createSingleAccounting("Abrechnungstest_" + fileName + "_" + j + ".pdf", tempShiftInstances);
          } else {
            createSingleAccounting("Abrechnungstest_" + fileName + ".pdf", tempShiftInstances);
          }
        }
      }
    }

  }

  private static void createSingleAccounting(String path, ArrayList<ShiftInstance> shifts) {
    Document testDocument = new Document();
    PersonalData personalData = PersonalData.getInstance();
    System.out.println("anzahl der schichten in " + path + ": " + shifts.size());
    try {
      PdfWriter writer = PdfWriter.getInstance(testDocument, new FileOutputStream(path));
      testDocument.open();
      Font helveticaFont8 = FontFactory.getFont(FontFactory.HELVETICA, 8);
      Font helveticaFont9 = FontFactory.getFont(FontFactory.HELVETICA, 9);
      Font helveticaFont10 = FontFactory.getFont(FontFactory.HELVETICA, 10);
      Font helveticaFont9Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
      Font helveticaFont11Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
      Font helveticaFont18Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
      PdfPTable table1 = new PdfPTable(3);
      table1.setWidthPercentage(100);
      float[] table1CellWidth = new float[]{22.87f, 51.45f, 19.29f};
      table1.setWidths(table1CellWidth);
      PdfPCell cell1 = new PdfPCell();
      cell1.addElement(new Paragraph("Rettungsdienst Freiburg", helveticaFont9));
      cell1.setVerticalAlignment(Element.ALIGN_BOTTOM);
      cell1.setMinimumHeight(28f);
      cell1.setBorderWidth(1);
      Image drkLogo = Image.getInstance("images/logo_Abrechnung.jpg");
      drkLogo.scaleAbsolute(115f, 14f);
      drkLogo.setAbsolutePosition(38f, 788f);
      PdfPCell cell2 = new PdfPCell(new Paragraph("Abrechnung AK-RD/Aushilfen", helveticaFont18Bold));
      cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
      cell2.setPaddingBottom(6);
      cell2.setBorderWidth(1);
      PdfPCell cell3 = new PdfPCell(new Paragraph("Ablage:\nPersAbtlg./Lohnabrechn.", helveticaFont8));
      cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
      cell3.setVerticalAlignment(Element.ALIGN_BOTTOM);
      cell3.setBorderWidth(1);

      table1.addCell(cell1);
      table1.addCell(cell2);
      table1.addCell(cell3);
      // create empty line
      PdfPTable table2 = new PdfPTable(1);
      table2.setWidthPercentage(100);
      PdfPCell cellEmpty = new PdfPCell();
      cellEmpty.setMinimumHeight(8f);
      cellEmpty.setBorder(Rectangle.NO_BORDER);
      table2.addCell(cellEmpty);

      // set headlines for checkboxes
      PdfPTable table3 = new PdfPTable(5);
      table3.setWidthPercentage(100);
      PdfPCell cell4 = new PdfPCell(new Paragraph("RD Freiburg", helveticaFont9Bold));
      cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
      cell4.setMinimumHeight(26f);
      cell4.setUseBorderPadding(true);
      PdfPCell cell5 = new PdfPCell(new Paragraph("KTP Freiburg", helveticaFont9Bold));
      cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
      cell5.setUseBorderPadding(true);
      PdfPCell cell6 = new PdfPCell(new Paragraph("RD Aussenwache /\nBaby-NAW", helveticaFont9Bold));
      cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
      cell6.setUseBorderPadding(true);
      PdfPCell cell7 = new PdfPCell(new Paragraph("Veranstaltungs-\nbetreuung", helveticaFont9Bold));
      cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
      cell7.setUseBorderPadding(true);
      PdfPCell cell8 = new PdfPCell(new Paragraph("KVS Freiburg", helveticaFont9Bold));
      cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
      cell8.setUseBorderPadding(true);
      // set checkboxcells
      PdfPCell cell9 = new PdfPCell();
      cell9.setMinimumHeight(90f);
      PdfPCell cell10 = new PdfPCell();
      PdfPCell cell11 = new PdfPCell();
      PdfPCell cell12 = new PdfPCell();
      PdfPCell cell13 = new PdfPCell();
      PdfPCell cell14 = new PdfPCell(new Paragraph("KoSt.: 964001", helveticaFont9Bold));
      cell14.setFixedHeight(18f);
      PdfPCell cell15 = new PdfPCell(new Paragraph("KoSt.: 962100", helveticaFont9Bold));
      PdfPCell cell16 = new PdfPCell(new Paragraph("KoSt.: 9640 - X", helveticaFont9Bold));
      PdfPCell cell17 = new PdfPCell(new Paragraph("KoSt.: 365301", helveticaFont9Bold));
      PdfPCell cell18 = new PdfPCell(new Paragraph("KoSt.: 973100", helveticaFont9Bold));

      table3.addCell(cell4);
      table3.addCell(cell5);
      table3.addCell(cell6);
      table3.addCell(cell7);
      table3.addCell(cell8);
      table3.addCell(cell9);
      table3.addCell(cell10);
      table3.addCell(cell11);
      table3.addCell(cell12);
      table3.addCell(cell13);
      table3.addCell(cell14);
      table3.addCell(cell15);
      table3.addCell(cell16);
      table3.addCell(cell17);
      table3.addCell(cell18);

      // checkboxes
      String[][] KoSt = new String[5][];
      KoSt[0] = new String[]{"RH (7,80 €/h)", "RS (8,80 €/h)",
        "RA (9,90 €/h)"};
      KoSt[1] = KoSt[0];
      KoSt[2] = new String[]{"Baby-NAW - 01", "Breisach - 03",
        "Kirchzarten - 10", "RH (5,90 €/h)", "RS (6,70 €/h)", "RA (7,60 €/h)"};
      KoSt[3] = new String[]{"RH (5,90 €/h)",
        "RS (6,70 €/h)", "RA (7,60 €/h)"};
      KoSt[4] = new String[]{"RH - RA (9,00 €/h"};
      boolean[] boolArray = new boolean[]{false, false, false, false, false, false};
      int checkboxSetter;
      switch(PersonalData.getInstance().getQualification()) {
        case RH:
          checkboxSetter=0;
          break;
        case RS:
          checkboxSetter=1;
          break;
        case RA:
          checkboxSetter=2;
          break;
        default:
          checkboxSetter=0;
      }
      int accountType;
      switch(shifts.get(0).getType()) {
        case ShiftContainer.RTW:
          accountType = 0;
          break;
        case ShiftContainer.KTW:
          accountType = 1;
          break;
        case ShiftContainer.EVENT:
          accountType = 3;
          break;
        case ShiftContainer.KVS:
          accountType = 4;
          checkboxSetter = 0;
          break;
        default:
          accountType = 2;
      }
      int xPosition = 50;
      for (int i = 0; i < KoSt.length; i++) {
        if(accountType==i) {
          if(i==2) {
            switch(shifts.get(0).getType()) {
              case ShiftContainer.BABY:
                boolArray[0] = true;
                break;
              case ShiftContainer.BREISACH:
                boolArray[1] = true;
                break;
              default:
                boolArray[2] = true;
            }
            boolArray[checkboxSetter+3] = true;
          }
          else {
            boolArray[checkboxSetter]= true;
          }
        }
        createCheckbox(writer, helveticaFont9, KoSt[i], xPosition, 740, boolArray);
        boolArray = new boolean[]{false, false, false, false, false, false};
        xPosition += 105;
      }
      // create another empty line
      PdfPTable table4 = new PdfPTable(1);
      table4.setWidthPercentage(100);
      PdfPCell cellEmpty2 = new PdfPCell();
      cellEmpty2.setMinimumHeight(6f);
      cellEmpty2.setBorder(Rectangle.NO_BORDER);
      table4.addCell(cellEmpty2);

      // create personal-date-table
      PdfPTable table5 = new PdfPTable(4);
      table5.setWidthPercentage(100);
      table5.setWidths(new float[]{137f, 4f, 192f, 115});
      String bankNameAndCity = "Bekannt";
      String accountNumber = "Bekannt";
      String blz = "Bekannt";
      if(!personalData.isDataKnown()) {
        bankNameAndCity = personalData.getBankNameAndCity();
        accountNumber = Integer.toString(personalData.getAccountNumber());
        blz = Integer.toString(personalData.getBlz());
      }
      
      // name
      PdfPCell cell20 = new PdfPCell(new Paragraph("Name", helveticaFont11Bold));
      cell20.setFixedHeight(22f);
      cell20.disableBorderSide(Rectangle.RIGHT);
      PdfPCell cell21 = new PdfPCell(new Paragraph(":", helveticaFont11Bold));
      cell21.disableBorderSide(Rectangle.LEFT);
      cell21.disableBorderSide(Rectangle.RIGHT);
      PdfPCell cell22 = new PdfPCell(
              new Paragraph(" "+personalData.getFirstName()+" "+
              personalData.getLastName()));
      cell22.setColspan(2);
      cell22.disableBorderSide(Rectangle.LEFT);
      // bankname
      PdfPCell cell23 = new PdfPCell(new Paragraph("Bankname und Ort*", helveticaFont11Bold));
      cell23.setFixedHeight(22f);
      cell23.disableBorderSide(Rectangle.RIGHT);
      PdfPCell cell24 = new PdfPCell(new Paragraph(":", helveticaFont11Bold));
      cell24.disableBorderSide(Rectangle.LEFT);
      cell24.disableBorderSide(Rectangle.RIGHT);
      PdfPCell cell25 = new PdfPCell(new Paragraph(" "+bankNameAndCity));
      cell25.disableBorderSide(Rectangle.LEFT);
      cell25.setColspan(2);
      // accountnr
      PdfPCell cell26 = new PdfPCell(new Paragraph("Kontonummer*", helveticaFont11Bold));
      cell26.setFixedHeight(22f);
      cell26.disableBorderSide(Rectangle.RIGHT);
      PdfPCell cell27 = new PdfPCell(new Paragraph(":", helveticaFont11Bold));
      cell27.disableBorderSide(Rectangle.LEFT);
      cell27.disableBorderSide(Rectangle.RIGHT);
      PdfPCell cell28 = new PdfPCell(new Paragraph(" "+accountNumber));
      cell28.disableBorderSide(Rectangle.LEFT);
      cell28.disableBorderSide(Rectangle.RIGHT);
      PdfPCell cell29 = new PdfPCell(new Paragraph("BLZ*: "+blz, helveticaFont11Bold));
      cell29.disableBorderSide(Rectangle.LEFT);

      PdfPCell cell30 = new PdfPCell(new Paragraph("zu belastende Kostenstelle:", helveticaFont11Bold));
      cell30.setColspan(4);
      cell30.setFixedHeight(22f);

      table5.addCell(cell20);
      table5.addCell(cell21);
      table5.addCell(cell22);
      table5.addCell(cell23);
      table5.addCell(cell24);
      table5.addCell(cell25);
      table5.addCell(cell26);
      table5.addCell(cell27);
      table5.addCell(cell28);
      table5.addCell(cell29);
      table5.addCell(cell30);

      // set textline
      PdfPTable table6 = new PdfPTable(1);
      table6.setWidthPercentage(100);
      PdfPCell cell31 = new PdfPCell(new Paragraph("* Angabe nur bei Neueinstellung oder Veränderungen", helveticaFont8));
      cell31.setFixedHeight(13f);
      cell31.setBorder(Rectangle.NO_BORDER);
      table6.addCell(cell31);

      // set the shift table
      PdfPTable table7 = new PdfPTable(10);
      table7.setWidthPercentage(100);
      table7.setWidths(new float[]{1.96f, 0.98f, 1.96f, 1.96f, 2.94f, 1.96f, 1.96f, 1.96f, 1.96f, 2.1f});
      // headlines
      PdfPCell cell32 = new PdfPCell(new Paragraph("Datum", helveticaFont9Bold));
      cell32.setFixedHeight(50f);
      cell32.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
      cell32.setHorizontalAlignment(Rectangle.ALIGN_CENTER);

      PdfPCell cell33 = new PdfPCell(new Paragraph("Tag", helveticaFont9Bold));
      cell33.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
      cell33.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
      PdfPCell cell34 = new PdfPCell(new Paragraph("Uhrzeit\nvon", helveticaFont9Bold));
      cell34.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
      cell34.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
      PdfPCell cell35 = new PdfPCell(new Paragraph("Uhrzeit\nbis", helveticaFont9Bold));
      cell35.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
      cell35.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
      PdfPCell cell36 = new PdfPCell(new Paragraph("Besatzung", helveticaFont9Bold));
      cell36.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
      cell36.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
      PdfPCell cell37 = new PdfPCell(new Paragraph("Anzahl geleisteter Stunden (Zeit)", helveticaFont9Bold));
      cell37.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
      cell37.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
      PdfPCell cell38 = new PdfPCell(new Paragraph("Anzahl geleisteter Stunden (Dezimal)", helveticaFont9Bold));
      cell38.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
      cell38.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
      PdfPCell cell39 = new PdfPCell(new Paragraph("€ pro\nStunde", helveticaFont9Bold));
      cell39.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
      cell39.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
      PdfPCell cell40 = new PdfPCell(new Paragraph("€ gesamt", helveticaFont9Bold));
      cell40.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
      cell40.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
      PdfPCell cell41 = new PdfPCell(new Paragraph("Unterschrift f.d. Richtigk.", helveticaFont9Bold));
      cell41.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
      cell41.setHorizontalAlignment(Rectangle.ALIGN_CENTER);

      table7.addCell(cell32);
      table7.addCell(cell33);
      table7.addCell(cell34);
      table7.addCell(cell35);
      table7.addCell(cell36);
      table7.addCell(cell37);
      table7.addCell(cell38);
      table7.addCell(cell39);
      table7.addCell(cell40);
      table7.addCell(cell41);

      float salary = calculateSalary(shifts.get(0));
      for (int i = 0; i <= 12; i++) {
        ShiftInstance currentShift = null;
        String date = "";
        String startTimeAsString = "";
        String endTimeAsString = "";
        String partner = "";
        String timeInHours = "";
        String timeasFloat = "";
        String salaryPerHour = "";
        String shiftSalary = "";
        String comment = "";
        
        if (shifts.size() > i) {
          currentShift = shifts.get(i);
          date = currentShift.getDate();
          int startTime = currentShift.getActualStartingTime();
          int endTime = currentShift.getActualEndTime();
          int breakTime = currentShift.getActualBreakTime();
          startTimeAsString = createTimeStringFromInt(startTime);
          endTimeAsString = createTimeStringFromInt(endTime);
          partner = currentShift.getPartner();
          timeInHours = calculateTimeInHours(startTime, endTime, breakTime);
          timeasFloat = Float.toString(currentShift.getTimeAsFloat());
          DecimalFormat df = new DecimalFormat("#0.00");
          salaryPerHour = df.format(salary);
          shiftSalary = df.format(currentShift.getTimeAsFloat()*salary);
          comment = currentShift.getComment();
        }
        PdfPCell tempCell = emptyPdfPCell();
        Paragraph content = new Paragraph(date, helveticaFont9);
        tempCell.addElement(content);
        tempCell.setFixedHeight(19f);
        table7.addCell(tempCell);
        tempCell = emptyPdfPCell();
        content = new Paragraph("", helveticaFont9);
        tempCell.addElement(content);
        table7.addCell(tempCell);
        tempCell = emptyPdfPCell();
        content = new Paragraph(startTimeAsString, helveticaFont9);
        tempCell.addElement(content);
        table7.addCell(tempCell);
        tempCell = emptyPdfPCell();
        content = new Paragraph(endTimeAsString, helveticaFont9);
        tempCell.addElement(content);
        table7.addCell(tempCell);
        tempCell = emptyPdfPCell();
        content = new Paragraph(partner, helveticaFont9);
        tempCell.addElement(content);
        table7.addCell(tempCell);
        tempCell = emptyPdfPCell();
        content = new Paragraph(timeInHours, helveticaFont9);
        tempCell.addElement(content);
        table7.addCell(tempCell);
        tempCell = emptyPdfPCell();
        content = new Paragraph(timeasFloat, helveticaFont9);
        tempCell.addElement(content);
        table7.addCell(tempCell);
        tempCell = emptyPdfPCell();
        content = new Paragraph(salaryPerHour, helveticaFont9);
        tempCell.addElement(content);
        table7.addCell(tempCell);
        tempCell = emptyPdfPCell();
        content = new Paragraph(shiftSalary, helveticaFont9);
        tempCell.addElement(content);
        table7.addCell(tempCell);
        tempCell = emptyPdfPCell();
        content = new Paragraph(comment, helveticaFont9);
        tempCell.addElement(content);
        table7.addCell(tempCell);
      }
      PdfPCell cell42 = emptyPdfPCell();
      cell42.setBorderWidth(2);
      cell42.setColspan(6);
      cell42.addElement(new Paragraph("Summe der geleisteten Stunden / Auszahlungsbetrag:", helveticaFont11Bold));
      cell42.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
      cell42.setPaddingBottom(6);
      PdfPCell cell43 = emptyPdfPCell();
      cell43.setBorderWidthBottom(2);
      cell43.setBorderWidthTop(2);
      cell43.setBorderWidthLeft(2);
      PdfPCell cell44 = emptyPdfPCell();
      cell44.setBorderWidthBottom(2);
      cell44.setBorderWidthTop(2);
      PdfPCell cell45 = emptyPdfPCell();
      cell45.setBorderWidthBottom(2);
      cell45.setBorderWidthTop(2);
      PdfPCell cell46 = emptyPdfPCell();
      cell46.setBorderWidthBottom(2);
      cell46.setBorderWidthTop(2);
      cell46.setBorderWidthRight(2);

      table7.addCell(cell42);
      table7.addCell(cell43);
      table7.addCell(cell44);
      table7.addCell(cell45);
      table7.addCell(cell46);

      // another empty line
      PdfPTable table8 = new PdfPTable(1);
      table8.setWidthPercentage(100);
      PdfPCell cell47 = new PdfPCell();
      cell47.setFixedHeight(8);
      cell47.setBorder(Rectangle.NO_BORDER);
      table8.addCell(cell47);

      // sign-field
      PdfPTable table9 = new PdfPTable(1);
      table9.setWidthPercentage(100);
      PdfPCell cell48 = new PdfPCell(new Paragraph("Unterschrift Mitarbeiter/in:", helveticaFont11Bold));
      cell48.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
      cell48.setFixedHeight(25);
      cell48.setBorderWidth(2);
      table9.addCell(cell48);

      // another empty line. We use table8 again.
      // 
      PdfPTable table10 = new PdfPTable(3);
      table10.setWidthPercentage(100);
      table10.setWidths(new int[]{138, 194, 115});
      PdfPCell cell49 = new PdfPCell(new Paragraph("Eingang RDL", helveticaFont10));
      cell49.setVerticalAlignment(Rectangle.ALIGN_TOP);
      cell49.setHorizontalAlignment(Rectangle.ALIGN_LEFT);
      cell49.setBorderWidthBottom(0);
      cell49.setFixedHeight(48);
      PdfPCell cell50 = new PdfPCell(new Paragraph("Eingang Personalabteilung", helveticaFont10));
      cell50.setVerticalAlignment(Rectangle.ALIGN_TOP);
      cell50.setHorizontalAlignment(Rectangle.ALIGN_LEFT);
      cell50.setBorderWidthBottom(0);
      PdfPCell cell51 = new PdfPCell(new Paragraph("geprüft:", helveticaFont10));
      cell51.setVerticalAlignment(Rectangle.ALIGN_TOP);
      cell51.setHorizontalAlignment(Rectangle.ALIGN_LEFT);
      PdfPCell cell52 = new PdfPCell();
      cell52.setBorderWidthTop(0);
      cell52.setFixedHeight(48);

      PdfPCell cell53 = new PdfPCell();
      cell53.setBorderWidthTop(0);
      PdfPCell cell54 = new PdfPCell(new Paragraph("zur Zahlung angewiesen:", helveticaFont10));
      cell54.setVerticalAlignment(Rectangle.ALIGN_TOP);
      cell54.setHorizontalAlignment(Rectangle.ALIGN_LEFT);
      cell54.setFixedHeight(48);

      table10.addCell(cell49);
      table10.addCell(cell50);
      table10.addCell(cell51);
      table10.addCell(cell52);
      table10.addCell(cell53);
      table10.addCell(cell54);

      // use table8 for a empty line again
      // Version/Author
      PdfPTable table11 = new PdfPTable(4);
      table11.setWidthPercentage(100);
      table11.addCell(new PdfPCell(new Paragraph("Version 4.0", helveticaFont9)));
      table11.addCell(new PdfPCell(new Paragraph("Erstellt:", helveticaFont9)));
      table11.addCell(new PdfPCell(new Paragraph("Freigegeben:", helveticaFont9)));
      table11.addCell(new PdfPCell(new Paragraph("Seite 1 von 1", helveticaFont9)));
      table11.addCell(new PdfPCell(new Paragraph("Stand:", helveticaFont9)));
      table11.addCell(new PdfPCell(new Paragraph("B.Sakschewski", helveticaFont9)));
      table11.addCell(new PdfPCell());
      table11.addCell(new PdfPCell());

      // add tables to document
      testDocument.add(table1);
      testDocument.add(drkLogo);
      testDocument.add(table2);
      testDocument.add(table3);
      testDocument.add(table4);
      testDocument.add(table5);
      testDocument.add(table6);
      testDocument.add(table7);
      testDocument.add(table8);
      testDocument.add(table9);
      testDocument.add(table8);
      testDocument.add(table10);
      testDocument.add(table8);
      testDocument.add(table11);
      testDocument.close();
    } catch (DocumentException e) {
    } catch (FileNotFoundException e) {
    } catch (MalformedURLException e) {
    } catch (IOException e) {
    }
  }

  private static PdfPCell emptyPdfPCell() {
    PdfPCell cell = new PdfPCell();
    cell.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
    cell.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
    //cell.setBorderWidth(1);
    return cell;
  }

  private static void setAligneMiddleCenter(PdfPCell cell) {
    cell.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
    cell.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
  }

  private static void createCheckbox(PdfWriter writer, Font font, String[] label, int xPosition, int yPosition, boolean[] checked) {
    PdfContentByte canvas = writer.getDirectContent();
    Rectangle rect;
    PdfFormField field;
    RadioCheckField checkbox;
    try {
      for (int i = 0; i < label.length; i++) {
        rect = new Rectangle((xPosition + 10), (yPosition - 10 - i * 15), xPosition, yPosition - i * 15);
        checkbox = new RadioCheckField(writer, rect, xPosition + "," + i, "on");
        checkbox.setChecked(checked[i]);
        checkbox.setOptions(RadioCheckField.READ_ONLY);
        checkbox.setBorderColor(BaseColor.BLACK);
        checkbox.setBackgroundColor(GrayColor.WHITE);
        checkbox.setBorderWidth(1.5f);
        checkbox.setCheckType(RadioCheckField.TYPE_CHECK);
        field = checkbox.getCheckField();
        writer.addAnnotation(field);
        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                new Phrase(label[i], font), (xPosition + 16), (yPosition - 8 - i * 15), 0);
      }

    } catch (com.itextpdf.text.DocumentException | java.io.IOException e) {
    }
  }
  
  private static String createTimeStringFromInt(int time) {
    String timeString;
    if (time >= 1000) {
      timeString = ((int) (time / 100)) + "";
    } else {
      timeString = "0" + ((int) (time / 100));
    }
    timeString = timeString + (((time % 100) < 10) ? ":0" : ":") + (time % 100);
    return timeString;
  }
  private static String calculateTimeInHours(int start, int end, int breakTime) {
    if (start > end) {
      int startHours = 24-((int)(start/100));
      int startMinutes = (start%100);
      int endHours = ((int)(end/100));
      int endMinutes = (end%100);
      if (endMinutes >=startMinutes) {
        end = 100*(endHours+startHours) + (endMinutes-startMinutes);
      }
      else {
        end = 100*(endHours+startHours-1) + (60+(endMinutes-startMinutes));
      }
    }
    else {
      end -= start-breakTime;
    }
    return createTimeStringFromInt(end);
  }
  private static float calculateSalary(ShiftInstance shift) {
    float salary;
    switch (shift.getType()) {
      case ShiftContainer.KTW:
      case ShiftContainer.RTW:
        switch(PersonalData.getInstance().getQualification()) {
          case RH:
            salary=7.8f;
            break;
          case RS:
            salary=8.8f;
            break;
          default:
            salary=9.9f;
        }
        break;
      case ShiftContainer.KVS:
        salary = 9f;
        break;
      default:
        switch(PersonalData.getInstance().getQualification()) {
          case RH:
            salary=5.9f;
            break;
          case RS:
            salary=6.7f;
            break;
          default:
            salary=7.6f;
        }
    }
    return salary;
  }
}
