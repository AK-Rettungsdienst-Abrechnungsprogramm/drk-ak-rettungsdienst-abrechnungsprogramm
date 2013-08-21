package de.drk.akrd;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.ColumnText;
import java.io.*;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 *
 * @author Jo
 */
public class PdfCreator {

  public PdfCreator() {
  }
  /**
   * 
   * @param shiftsToAccount
   * @param month
   * @param year
   * @return 
   */
  public static String createAccounting(ShiftInstance[] shiftsToAccount, int month, int year) {
    // check if personal Data exists
    if (!PersonalData.getInstance().isDataSet()) {
      UtilityBox.getInstance().displayInfoPopup("Fehlende Daten", "Um die "
              + "Abrechnung zu erstellen müssen\npersönliche Daten gespeichert sein.");
      return null;
    }
    boolean success = false;
    String filePath = "Abrechnungen/"+year;
    // create directory if nessessary
    UtilityBox.createDirectory(filePath);
    // change filePath from directory to file
    filePath = filePath + "/Abrechnung"+UtilityBox.getMonthString(month)+year+".pdf";
    Document accounting = new Document();

    ArrayList<ShiftInstance> rd = new ArrayList<ShiftInstance>();
    ArrayList<ShiftInstance> ktp = new ArrayList<ShiftInstance>();
    ArrayList<ShiftInstance> baby = new ArrayList<ShiftInstance>();
    ArrayList<ShiftInstance> breisach = new ArrayList<ShiftInstance>();
    ArrayList<ShiftInstance> kiza = new ArrayList<ShiftInstance>();
    ArrayList<ShiftInstance> event = new ArrayList<ShiftInstance>();
    ArrayList<ShiftInstance> sc = new ArrayList<ShiftInstance>();
    ArrayList<ShiftInstance> concert_hall = new ArrayList<ShiftInstance>();
    ArrayList<ShiftInstance> kvs = new ArrayList<ShiftInstance>();
    ArrayList<ShiftInstance> elw = new ArrayList<ShiftInstance>();

    // add shifts to seperate shift lists
    for (int i = 0; i < shiftsToAccount.length; i++) {
      switch (shiftsToAccount[i].getType()) {
        case KTW:
          ktp.add(shiftsToAccount[i]);
          break;
        case RTW:
          rd.add(shiftsToAccount[i]);
          break;
        case HINTERGRUND:
          rd.add(shiftsToAccount[i]);
          break;
        case ELW:
          elw.add(shiftsToAccount[i]);
          break;
        case EVENT:
          event.add(shiftsToAccount[i]);
          break;
        case SC:
          sc.add(shiftsToAccount[i]);
          break;
        case CONCERT_HALL:
          concert_hall.add(shiftsToAccount[i]);
          break;
        case KVS:
          kvs.add(shiftsToAccount[i]);
          break;
        case BREISACH:
          breisach.add(shiftsToAccount[i]);
          break;
        case KIZA:
          kiza.add(shiftsToAccount[i]);
          break;
        default:
          break;
      }
    }
    ArrayList<ShiftInstance>[] allShifts = (ArrayList<ShiftInstance>[])(
            new ArrayList[]{rd, elw, ktp, baby, breisach, kiza, event, sc,
              concert_hall, kvs});// TODO: add elw
    try {
      accounting.setPageSize(PageSize.A4);
      PdfWriter pdfWriter = PdfWriter.getInstance(accounting, new FileOutputStream(filePath));
      accounting.open();
      for (int i = 0; i < allShifts.length; i++) {
        if (!allShifts[i].isEmpty()) {
          int numberOfPages = ((int) (allShifts[i].size() / 13)) + 1;
          int counter = 0;
          for (int j = 1; j <= numberOfPages; j++) {
            ArrayList<ShiftInstance> tempShiftInstances = new ArrayList<ShiftInstance>();
            for (int k = 0; k < 13; k++) {
              if (counter >= (allShifts[i].size())) {
                break;
              }
              tempShiftInstances.add(allShifts[i].get(counter));
              counter++;
            }
            accounting.newPage();
            ArrayList<ShiftInstance> tempShiftInstancesReverse = new ArrayList<ShiftInstance>();
            for (int k = 0; k < tempShiftInstances.size(); k++) {
              ShiftInstance shiftInstance = tempShiftInstances.get(tempShiftInstances.size()-k-1);
              tempShiftInstancesReverse.add(shiftInstance);
            }
            success = createSingleAccounting(accounting, pdfWriter, tempShiftInstancesReverse, i*10+j);
          }
        }
      }
      // TODO: for JDK7 use Multicatch
    } catch (Exception e){//DocumentException | IOException e) {
      UtilityBox.getInstance().displayErrorPopup("Abrechnung", "Fehler beim "
              + "Erstellen der Abrechnung:\n"+e.getMessage());
      filePath = null;
    } finally {
      try {
        accounting.close();
      } catch (Exception e) {
        UtilityBox.getInstance().displayErrorPopup("Abrechnung", "Fehler beim "
                + "Erstellen der Abrechnung:\nDokument nicht geschlossen:\n"
                + ""+e.getMessage());
        filePath = null;
      }
    }
    return filePath;
  }

  private static boolean createSingleAccounting(Document accountingDocument, PdfWriter writer, ArrayList<ShiftInstance> shifts, int pageNr) {
    boolean success = false;
    PersonalData personalData;
    float timeSumAsFloat = 0;
    DecimalFormat euroFormat = new DecimalFormat("#0.00");
    float salarySum = 0; 
    try {
      personalData = PersonalData.getInstance();
      Font helveticaFont5 = FontFactory.getFont(FontFactory.HELVETICA, 5);
      Font helveticaFont6 = FontFactory.getFont(FontFactory.HELVETICA, 6);
      Font helveticaFont7 = FontFactory.getFont(FontFactory.HELVETICA, 7);
      Font helveticaFont7Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7);
      Font helveticaFont8 = FontFactory.getFont(FontFactory.HELVETICA, 8);
      Font helveticaFont8Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
      Font helveticaFont9 = FontFactory.getFont(FontFactory.HELVETICA, 9);
      Font helveticaFont10 = FontFactory.getFont(FontFactory.HELVETICA, 10);
      Font helveticaFont9Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
      Font helveticaFont10Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
      Font helveticaFont11Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
      Font helveticaFont18Bold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
      PdfPTable table1 = new PdfPTable(3);
      table1.setWidthPercentage(100);
      float[] table1CellWidth = new float[]{18f, 48f, 20f};
      table1.setWidths(table1CellWidth);
      PdfPCell cell1 = new PdfPCell();
      cell1.addElement(new Paragraph("DRK Freiburg\nRettungsdienst Freiburg", helveticaFont8));
      cell1.setVerticalAlignment(Element.ALIGN_TOP);
      cell1.setFixedHeight(27f);
      cell1.setPaddingTop(0);
      cell1.setBorderWidth(1);
      Image drkLogo = Image.getInstance(MainWindow.class.getResource("logo_Abrechnung.jpg"));
      drkLogo.scaleAbsolute(115f, 14f);
      drkLogo.setAbsolutePosition(441f, 784f);
      PdfPCell cell2 = new PdfPCell(new Paragraph("Abrechnung AK-RD/Aushilfen", helveticaFont18Bold));
      cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
      cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
      cell2.setPaddingBottom(6);
      cell2.setBorderWidth(1);
      cell2.setRowspan(2);
      PdfPCell cell3 = new PdfPCell();
      cell3.setBorderWidth(1);
      PdfPCell cell1p1 = new PdfPCell();
      cell1p1.setFixedHeight(10f);
      cell1p1.setPaddingTop(0);
      cell1p1.addElement(new Paragraph("Gültig ab: 01.06.2013", helveticaFont8));
      cell1p1.setHorizontalAlignment(Element.ALIGN_LEFT);
      cell1p1.setVerticalAlignment(Element.ALIGN_TOP);
      cell1p1.setBorderWidth(1);
      PdfPCell cell2p1 = new PdfPCell();
      cell2p1.setPaddingTop(0);
      cell2p1.addElement(new Paragraph("Ablage: Personalservice", helveticaFont8));
      cell2p1.setHorizontalAlignment(Element.ALIGN_RIGHT);
      cell2p1.setVerticalAlignment(Element.ALIGN_TOP);
      cell2p1.setBorderWidth(1);

      table1.addCell(cell1);
      table1.addCell(cell2);
      table1.addCell(cell3);
      table1.addCell(cell1p1);
      table1.addCell(cell2p1);
      //table1.addCell(cell1p1);
      // create empty line
      PdfPTable table2 = new PdfPTable(1);
      table2.setWidthPercentage(100);
      PdfPCell cellEmpty = new PdfPCell();
      cellEmpty.setMinimumHeight(6f);
      cellEmpty.setBorder(Rectangle.NO_BORDER);
      table2.addCell(cellEmpty);

      // set headlines for checkboxes
      PdfPTable table3 = new PdfPTable(5);
      table3.setWidthPercentage(100);
      PdfPCell cell4 = new PdfPCell(new Paragraph("  RD Freiburg", helveticaFont9Bold));
      cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
      cell4.setMinimumHeight(18f);
      cell4.setUseBorderPadding(true);
      PdfPCell cell5 = new PdfPCell(new Paragraph("  KTP Freiburg", helveticaFont9Bold));
      cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
      cell5.setUseBorderPadding(true);
      PdfPCell cell6 = new PdfPCell(new Paragraph("  RD Aussenwache", helveticaFont9Bold));
      cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
      cell6.setUseBorderPadding(true);
      PdfPCell cell7 = new PdfPCell(new Paragraph("  Sanitätsdienste", helveticaFont9Bold));
      cell7.setVerticalAlignment(Element.ALIGN_MIDDLE);
      cell7.setUseBorderPadding(true);
      PdfPCell cell8 = new PdfPCell(new Paragraph("  KVS Freiburg", helveticaFont9Bold));
      cell8.setVerticalAlignment(Element.ALIGN_MIDDLE);
      cell8.setUseBorderPadding(true);
      // set checkboxcells
      PdfPCell cell9 = new PdfPCell();
      cell9.setMinimumHeight(92f);
      PdfPCell cell10 = new PdfPCell();
      PdfPCell cell11 = new PdfPCell();
      PdfPCell cell12 = new PdfPCell();
      PdfPCell cell13 = new PdfPCell();
      PdfPCell cell14 = new PdfPCell(new Paragraph("KoSt.: 964001", helveticaFont9Bold));
      cell14.setFixedHeight(18f);
      PdfPCell cell15 = new PdfPCell(new Paragraph("KoSt.: 962100", helveticaFont9Bold));
      PdfPCell cell16 = new PdfPCell(new Paragraph("KoSt.: 9640 - X", helveticaFont9Bold));
      PdfPCell cell17 = new PdfPCell(new Paragraph("KoSt.: 3653 - X", helveticaFont9Bold));
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
      KoSt[2] = new String[]{/*"Baby-NAW - 01", */"Breisach - 03",
        "Kirchzarten - 10", "RH (5,90 €/h)", "RS (6,70 €/h)", "RA (7,60 €/h)"};
      KoSt[3] = new String[]{"Veranstaltung - 01","SC-Freiburg - 02","Konzerthaus - 03","RH (5,90 €/h)",
        "RS (6,70 €/h)", "RA (7,60 €/h)"};
      KoSt[4] = new String[]{"RH - RA (9,00 €/h)"};
      boolean[] boolArray = new boolean[]{false, false, false, false, false, false};
      int checkboxSetter;
      switch (PersonalData.getInstance().getQualification()) {
        case RH:
          checkboxSetter = 0;
          break;
        case RS:
          checkboxSetter = 1;
          break;
        case RA:
          checkboxSetter = 2;
          break;
        default:
          checkboxSetter = 0;
      }
      int accountType;
      String costUnit;
      switch (shifts.get(0).getType()) {
        case RTW:
        case HINTERGRUND:
          accountType = 0;
          costUnit = "964001";
          break;
        case ELW:
          accountType = 0;
          costUnit = "964001";
          break;
        case KTW:
          accountType = 1;
          costUnit = "962100";
          break;
        case EVENT:
          accountType = 3;
          costUnit = "3653";
          break;
        case SC:
          accountType = 3;
          costUnit = "3653";
          break;
        case CONCERT_HALL:
          accountType = 3;
          costUnit = "3653";
          break;
        case KVS:
          accountType = 4;
          checkboxSetter = 0;
          costUnit = "973100";
          break;
        default:                // Außenwache
          accountType = 2;
          costUnit = "9640";
      }
      int xPosition = 46;
      for (int i = 0; i < KoSt.length; i++) {
        if (accountType == i) {
          if (i == 2) {       // Außenwache
            switch (shifts.get(0).getType()) {
              case BREISACH:
                boolArray[0] = true;
                costUnit = costUnit+"03 (RD Breisach)";
                break;
              default:
                boolArray[1] = true;
                costUnit = costUnit+"10 (RD Kirchzarten)";
            }
            boolArray[checkboxSetter + 2] = true;
          } else if (i==3) {        // Sandienst etc.
            switch (shifts.get(0).getType()) {
              case SC:
                boolArray[1] = true;
                boolArray[checkboxSetter + 3] = true;
                costUnit = costUnit+"02 (SC-Freiburg)";
                break;
              case EVENT:
                boolArray[0] = true;
                boolArray[checkboxSetter + 3] = true;
                costUnit = costUnit+"01 (Veranstaltung)";
                break;
              default:                                    // CONCERT_HALL
                boolArray[2] = true;
                costUnit = costUnit+"03 (Konzerthaus, 9,50 €/h)";
                break;
            }
          } else {
            boolArray[checkboxSetter] = true;
          }
        }
        createCheckbox(writer, accountingDocument, helveticaFont9, KoSt[i], xPosition, 736, boolArray, pageNr);
        boolArray = new boolean[]{false, false, false, false, false, false};
        xPosition += 105;
      }
     
      // create another empty line
      PdfPTable table4 = new PdfPTable(1);
      table4.setWidthPercentage(100);
      PdfPCell cellEmpty2 = new PdfPCell();
      cellEmpty2.setFixedHeight(5f);
      cellEmpty2.setBorder(Rectangle.NO_BORDER);
      table4.addCell(cellEmpty2);

      // create personal-data-table
      float personalDataCellHeight = 18f;
      Font personalDataFont = helveticaFont10Bold;
      PdfPTable table5 = new PdfPTable(5);
      table5.setWidthPercentage(100);
      table5.setWidths(new float[]{137f, 4f, 192f, 30f, 115f});
      String address = "Bekannt";
      // if address is not known put it into the form
      if (!personalData.addressKnown()) {
    	  address = personalData.getAddress();
      }
      String bankNameAndCity = "Bekannt";
      String accountNumber = "Bekannt";
      String blz = "Bekannt";
      if (!personalData.isDataKnown()) {
        bankNameAndCity = personalData.getBankNameAndCity();
        accountNumber = Integer.toString(personalData.getAccountNumber());
        blz = Integer.toString(personalData.getBlz());
      }

      // name
      PdfPCell cell20 = new PdfPCell(new Paragraph("Name", personalDataFont));
      cell20.setFixedHeight(personalDataCellHeight);
      cell20.disableBorderSide(Rectangle.RIGHT);
      PdfPCell cell21 = new PdfPCell(new Paragraph(":", personalDataFont));
      cell21.disableBorderSide(Rectangle.LEFT);
      cell21.disableBorderSide(Rectangle.RIGHT);
      PdfPCell cell22 = new PdfPCell(
              new Paragraph(" " + personalData.getFirstName() + " "
              + personalData.getLastName()));
      cell22.setColspan(3);
      cell22.disableBorderSide(Rectangle.LEFT);
      // adress
      PdfPCell cell22p1 = new PdfPCell(new Paragraph("Adresse*", personalDataFont));
      cell22p1.setFixedHeight(personalDataCellHeight);
      cell22p1.disableBorderSide(Rectangle.RIGHT);
      PdfPCell cell22p2 = new PdfPCell(new Paragraph(":", personalDataFont));
      cell22p2.disableBorderSide(Rectangle.LEFT);
      cell22p2.disableBorderSide(Rectangle.RIGHT);
      PdfPCell cell22p3 = new PdfPCell(new Paragraph(" " + address));
      cell22p3.disableBorderSide(Rectangle.LEFT);
      cell22p3.setColspan(3);
      // bankname
      PdfPCell cell23 = new PdfPCell(new Paragraph("Bankname und Ort*", personalDataFont));
      cell23.setFixedHeight(personalDataCellHeight);
      cell23.disableBorderSide(Rectangle.RIGHT);
      PdfPCell cell24 = new PdfPCell(new Paragraph(":", personalDataFont));
      cell24.disableBorderSide(Rectangle.LEFT);
      cell24.disableBorderSide(Rectangle.RIGHT);
      PdfPCell cell25 = new PdfPCell(new Paragraph(" " + bankNameAndCity));
      cell25.disableBorderSide(Rectangle.LEFT);
      cell25.setColspan(3);
      // accountnr
      PdfPCell cell26 = new PdfPCell(new Paragraph("Kontonummer*", personalDataFont));
      cell26.setFixedHeight(personalDataCellHeight);
      cell26.disableBorderSide(Rectangle.RIGHT);
      PdfPCell cell27 = new PdfPCell(new Paragraph(":", personalDataFont));
      cell27.disableBorderSide(Rectangle.LEFT);
      cell27.disableBorderSide(Rectangle.RIGHT);
      PdfPCell cell28 = new PdfPCell(new Paragraph(" " + accountNumber));
      cell28.disableBorderSide(Rectangle.LEFT);
      cell28.disableBorderSide(Rectangle.RIGHT);
      PdfPCell cell29 = new PdfPCell(new Paragraph("BLZ :", personalDataFont));
      cell29.disableBorderSide(Rectangle.LEFT);
      cell29.disableBorderSide(Rectangle.RIGHT);
      PdfPCell cell2930 = new PdfPCell(new Paragraph(blz));
      cell2930.disableBorderSide(Rectangle.LEFT);

      PdfPCell cell30 = new PdfPCell(new Paragraph("zu belastende Kostenstelle", personalDataFont));
      cell30.setFixedHeight(personalDataCellHeight);
      cell30.disableBorderSide(Rectangle.RIGHT);
      PdfPCell cell3031 = new PdfPCell(new Paragraph(":", personalDataFont));
      cell3031.disableBorderSide(Rectangle.LEFT);
      cell3031.disableBorderSide(Rectangle.RIGHT);
      PdfPCell cell3032 = new PdfPCell(new Paragraph(" "+costUnit));
      cell3032.disableBorderSide(Rectangle.LEFT);
      cell3032.setColspan(3);

      table5.addCell(cell20);
      table5.addCell(cell21);
      table5.addCell(cell22);
      table5.addCell(cell22p1);
      table5.addCell(cell22p2);
      table5.addCell(cell22p3);
      table5.addCell(cell23);
      table5.addCell(cell24);
      table5.addCell(cell25);
      table5.addCell(cell26);
      table5.addCell(cell27);
      table5.addCell(cell28);
      table5.addCell(cell29);
      table5.addCell(cell2930);
      table5.addCell(cell30);
      table5.addCell(cell3031);
      table5.addCell(cell3032);

      // set textline
      PdfPTable table6 = new PdfPTable(1);
      table6.setWidthPercentage(100);
      PdfPCell cell31 = new PdfPCell(new Paragraph("* Angabe nur bei Neueinstellung oder Veränderungen", helveticaFont8));
      cell31.setFixedHeight(12f);
      cell31.setBorder(Rectangle.NO_BORDER);
      table6.addCell(cell31);

      // set the shift table
      PdfPTable table7 = new PdfPTable(11);
      table7.setWidthPercentage(100);
      table7.setWidths(new float[]{1.96f, 0.9f, 1.8f, 1.8f, 2.9f, 1.96f, 1.96f, 1.85f, 1.85f, 1.4f, 2.2f});
      // headlines
      Font shiftTableHeadlineFont = helveticaFont8Bold;
      PdfPCell cell32 = new PdfPCell(new Paragraph("Datum", shiftTableHeadlineFont));
      cell32.setFixedHeight(50f);
      cell32.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
      cell32.setHorizontalAlignment(Rectangle.ALIGN_CENTER);

      PdfPCell cell33 = new PdfPCell(new Paragraph("Tag", shiftTableHeadlineFont));
      cell33.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
      cell33.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
      PdfPCell cell34 = new PdfPCell(new Paragraph("Uhrzeit\nvon", shiftTableHeadlineFont));
      cell34.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
      cell34.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
      PdfPCell cell35 = new PdfPCell(new Paragraph("Uhrzeit\nbis", shiftTableHeadlineFont));
      cell35.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
      cell35.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
      PdfPCell cell36 = new PdfPCell(new Paragraph("Besatzung", shiftTableHeadlineFont));
      cell36.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
      cell36.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
      PdfPCell cell37 = new PdfPCell(new Paragraph("Anzahl geleisteter Stunden (Zeit)", shiftTableHeadlineFont));
      cell37.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
      cell37.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
      PdfPCell cell38 = new PdfPCell(new Paragraph("Anzahl geleisteter Stunden (Dezimal)", shiftTableHeadlineFont));
      cell38.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
      cell38.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
      PdfPCell cell39 = new PdfPCell(new Paragraph("€ pro\nStunde", shiftTableHeadlineFont));
      cell39.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
      cell39.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
      PdfPCell cell40 = new PdfPCell(new Paragraph("€\ngesamt", shiftTableHeadlineFont));
      cell40.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
      cell40.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
      Paragraph travelExpensesParagraph = new Paragraph();
      travelExpensesParagraph.add(new Phrase("Fahrt-\nkosten\n", shiftTableHeadlineFont));
      travelExpensesParagraph.add(new Phrase("(Kostenart 5803)", helveticaFont5));
      PdfPCell cell401 = new PdfPCell(travelExpensesParagraph);
      cell401.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
      cell401.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
      PdfPCell cell41 = new PdfPCell(new Paragraph("Kommentar", shiftTableHeadlineFont));
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
      table7.addCell(cell401);
      table7.addCell(cell41);

      float salary = UtilityBox.getInstance().calculateSalaryPerHour(shifts.get(0), PersonalData.getInstance().getQualification());
      int completeCommuteExpenses = 0;
      for (int i = 0; i <= 12; i++) {
        ShiftInstance currentShift = null;
        String weekDay = "";
        String date = "";
        String startTimeAsString = "";
        String endTimeAsString = "";
        String partner = "";
        Font partnerFont = helveticaFont9;
        String timeInHours = "";
        String timeasFloat = "";
        String salaryPerHour = "";
        String shiftSalary = "";
        String commuteExpenses = "";
        String comment = "";
        Font commentFont = helveticaFont9;

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Calendar cal = Calendar.getInstance();
        if (shifts.size() > i) {
          currentShift = shifts.get(i);
          date = currentShift.getDateString();
          try {
            cal.setTime(sdf.parse(date));
            weekDay = UtilityBox.getDayOfWeekString(cal.get(Calendar.DAY_OF_WEEK));
          } catch (ParseException ex) {
            System.out.println("Exception in PdfCreator.createAccounting "
                    + "(parsing date failed): "+ex.getMessage());
          }
          int startTime = currentShift.getActualStartingTimeWithPrepTime();
          int endTime = currentShift.getActualEndTime();
          int breakTime = currentShift.getActualBreakTime();
          startTimeAsString = UtilityBox.createTimeStringFromInt(startTime);
          endTimeAsString = UtilityBox.createTimeStringFromInt(endTime);
          partner = currentShift.getPartner();
          if (partner.length() > 14) {
            if (partner.length() > 18) {
              partner = partner.substring(0, 18);
            }
            partnerFont = helveticaFont7;
          }
          timeInHours = UtilityBox.getInstance().calculateTimeInHours(startTime, endTime, breakTime);
          timeasFloat = euroFormat.format(currentShift.getTimeAsFloat());
          timeSumAsFloat += currentShift.getTimeAsFloat();
          salaryPerHour = euroFormat.format(salary)+ " €";
          shiftSalary = euroFormat.format(currentShift.getTimeAsFloat() * salary)+" €";
          int shiftCommuteExpenses = currentShift.getCommuteExpenses();
          completeCommuteExpenses += shiftCommuteExpenses;
          commuteExpenses = euroFormat.format(shiftCommuteExpenses) + " €";
          comment = currentShift.getComment();
          int commentLength = comment.length();
          if (commentLength > 10) {
            if (commentLength > 13) {
              if (commentLength > 36) {
                comment = comment.substring(0, 36);
              }
              commentFont = helveticaFont5;
            }
            else {
              commentFont = helveticaFont7;
            }
          }
        }
        
        PdfPCell tempCell = emptyPdfPCell();
        Paragraph content = new Paragraph(date, helveticaFont9);
        tempCell.addElement(content);
        tempCell.setFixedHeight(19f);
        table7.addCell(tempCell);
        tempCell = emptyPdfPCell();
        content = new Paragraph(weekDay, helveticaFont9);
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
        content = new Paragraph(partner, partnerFont);
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
        content = new Paragraph(commuteExpenses, helveticaFont9);
        tempCell.addElement(content);
        table7.addCell(tempCell);
        tempCell = emptyPdfPCell();
        content = new Paragraph(comment, commentFont);
        tempCell.addElement(content);
        table7.addCell(tempCell);
      }
      // end of shift table
      //calculate the complete salary
      salarySum = timeSumAsFloat * salary;
      PdfPCell cell42 = emptyPdfPCell();
      cell42.setFixedHeight(20);
      cell42.setBorderWidth(2);
      cell42.setColspan(6);
      cell42.addElement(new Paragraph("Summe der geleisteten Stunden / Auszahlungsbetrag:", helveticaFont8Bold));
      cell42.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
      cell42.setPaddingBottom(6);
      PdfPCell cell43 = new PdfPCell(new Paragraph(euroFormat.format(timeSumAsFloat), helveticaFont9Bold));
      cell43.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
      cell43.setBorderWidthBottom(2);
      cell43.setBorderWidthTop(2);
      cell43.setBorderWidthLeft(2);
      PdfPCell cell44 = new PdfPCell(new Paragraph(euroFormat.format(salary)+" €", helveticaFont9Bold));
      cell44.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
      cell44.setBorderWidthBottom(2);
      cell44.setBorderWidthTop(2);
      PdfPCell cell45 = new PdfPCell(new Paragraph(euroFormat.format(salarySum)+ " €", helveticaFont9Bold));
      cell45.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
      cell45.setColspan(1);
      cell45.setBorderWidthBottom(2);
      cell45.setBorderWidthTop(2);
      PdfPCell cell46 = new PdfPCell(new Paragraph(euroFormat.format(completeCommuteExpenses)+ " €", helveticaFont9Bold));
      cell46.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
      cell46.setBorderWidthBottom(2);
      cell46.setBorderWidthTop(2);
      PdfPCell cell461 = emptyPdfPCell();
      cell461.setBorderWidthBottom(2);
      cell461.setBorderWidthTop(2);
      cell461.setBorderWidthRight(2);

      table7.addCell(cell42);
      table7.addCell(cell43);
      table7.addCell(cell44);
      table7.addCell(cell45);
      table7.addCell(cell46);
      table7.addCell(cell461);

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
      String signatureString = "Unterschrift Mitarbeiter/in:";
      if(false) { // TODO: wenn version veraltet
        signatureString += " NICHT FREIGEGEBENE VERSION. NICHT ABGEBEN.";
      }
      PdfPCell cell48 = new PdfPCell(new Paragraph(signatureString, helveticaFont11Bold));
      cell48.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
      cell48.setFixedHeight(25);
      cell48.setBorderWidth(2);
      table9.addCell(cell48);

      // another empty line. We use table8 again.
      // 
      PdfPTable table10 = new PdfPTable(3);
      table10.setWidthPercentage(100);
      table10.setWidths(new int[]{5, 6, 8});
      PdfPCell cell49 = new PdfPCell(new Paragraph("Eingang RDL", helveticaFont10));
      cell49.setVerticalAlignment(Rectangle.ALIGN_TOP);
      cell49.setHorizontalAlignment(Rectangle.ALIGN_LEFT);
      cell49.setBorderWidthBottom(0);
      cell49.setFixedHeight(48);
      PdfPCell cell50 = new PdfPCell(new Paragraph("Eingang Personalservice", helveticaFont10));
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
      
      // add department-box
      float departmentBoxX = 345;
      float departmentBoxY = 123;
      float departmentBoxWidth = 103;
      float departmentBoxHeight = 28;
      float departmentBoxTextY = departmentBoxY+20;
      PdfContentByte contentByte = writer.getDirectContent();
      contentByte.setLineWidth(.7f);
      contentByte.rectangle(departmentBoxX, departmentBoxY, departmentBoxWidth, departmentBoxHeight);
      contentByte.rectangle(departmentBoxX+departmentBoxWidth, departmentBoxY, departmentBoxWidth, departmentBoxHeight);
      contentByte.beginText();
      contentByte.setFontAndSize(BaseFont.createFont("Helvetica", "", false), 8);
      contentByte.showTextAligned(PdfContentByte.ALIGN_CENTER, "Dienstplanung", (int)(departmentBoxX +(departmentBoxWidth/2)), departmentBoxTextY, 0);
      contentByte.setFontAndSize(BaseFont.createFont("Helvetica", "", false), 8);
      contentByte.showTextAligned(PdfContentByte.ALIGN_CENTER, "bei SAN-Diensten Abt.RK", (int)(departmentBoxX +(departmentBoxWidth*1.5)), departmentBoxTextY, 0);
      contentByte.endText();

      // add payment order-box
      float paymentOrderBoxY = 75;
      float paymentOrderBoxTextY = paymentOrderBoxY+20;
      contentByte.rectangle(departmentBoxX, paymentOrderBoxY, 2*departmentBoxWidth, departmentBoxHeight);
      
      // use table8 for a empty line again
      // Version/Author
      PdfPTable table11 = new PdfPTable(4);
      table11.setWidthPercentage(100);
      table11.addCell(new PdfPCell(new Paragraph("Version 4.1", helveticaFont9)));
      table11.addCell(new PdfPCell(new Paragraph("Erstellt:", helveticaFont9)));
      table11.addCell(new PdfPCell(new Paragraph("Freigegeben:", helveticaFont9)));
      table11.addCell(new PdfPCell(new Paragraph("Seite 1 von 1", helveticaFont9)));
      table11.addCell(new PdfPCell(new Paragraph("Stand: 01.06..2013", helveticaFont9)));
      table11.addCell(new PdfPCell(new Paragraph("B. Sakschewski, J. Güttler", helveticaFont9)));
      table11.addCell(new PdfPCell(new Paragraph("Karin Müller", helveticaFont9)));
      table11.addCell(new PdfPCell(new Paragraph("Personalservice", helveticaFont9)));      
      
      // add tables to document
      accountingDocument.add(table1);
      accountingDocument.add(drkLogo);
      accountingDocument.add(table2);
      accountingDocument.add(table3);
      accountingDocument.add(table4);
      accountingDocument.add(table5);
      accountingDocument.add(table6);
      accountingDocument.add(table7);
      accountingDocument.add(table8);
      accountingDocument.add(table9);
      accountingDocument.add(table8);
      accountingDocument.add(table10);
      accountingDocument.add(table8);
      accountingDocument.add(table11);
      success = true;
      // TODO: for JDK7 use Multicatch
    } catch (Exception e){//DocumentException | IOException | NullPointerException e) {
      success = false;
      UtilityBox.getInstance().displayErrorPopup("Abrechnung", 
              "Fehler beim Erstellen der Abrechnung:\n"+e.getMessage());
    }
    return success;
  }

  private static PdfPCell emptyPdfPCell() {
    PdfPCell cell = new PdfPCell();
    cell.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
    cell.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
    cell.setFixedHeight(19f);
    //cell.setBorderWidth(1);
    return cell;
  }

  private static void setAligneMiddleCenter(PdfPCell cell) {
    cell.setVerticalAlignment(Rectangle.ALIGN_MIDDLE);
    cell.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
  }

  private static void createCheckbox(PdfWriter writer, Document accountingDocument, Font font, String[] label, int xPosition, int yPosition, boolean[] checked, int pageNr) {
    PdfContentByte canvas = writer.getDirectContent();
//    Rectangle rect;
//    PdfFormField field;
//    RadioCheckField checkbox;
    try {
      Image checkbox_checked = Image.getInstance(MainWindow.class.getResource("checkbox_checked.jpg"));
      checkbox_checked.scaleAbsolute(10f, 10f);
      Image checkbox = Image.getInstance(MainWindow.class.getResource("checkbox.jpg"));
      checkbox.scaleAbsolute(10f, 10f);
      for (int i = 0; i < label.length; i++) {
        Image checkboxImage;
        if (checked[i]) {
          checkboxImage = Image.getInstance(checkbox_checked);
        } else {
          checkboxImage = Image.getInstance(checkbox);
        }
        checkboxImage.setAbsolutePosition(xPosition, (yPosition - 10 - i * 15));
        accountingDocument.add(checkboxImage);
        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                new Phrase(label[i], font), (xPosition + 16), (yPosition - 8 - i * 15), 0);
      }
      // TODO: for JDK7 use Multicatch
    } catch (Exception e){//com.itextpdf.text.DocumentException | java.io.IOException e) {
      UtilityBox.getInstance().displayErrorPopup("Abrechnung", 
              "Fehler beim Erstellen der Abrechnung: "+e.getMessage());
    }
  }
}
