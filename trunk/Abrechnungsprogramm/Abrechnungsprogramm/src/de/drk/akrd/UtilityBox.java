package de.drk.akrd;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseField;
import com.itextpdf.text.pdf.PdfBorderDictionary;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RadioCheckField;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPrintPage;
import com.sun.pdfview.PDFRenderer;
import com.itextpdf.text.BaseColor;
import java.awt.Desktop;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.Sides;
import javax.swing.JOptionPane;
import org.apache.pdfbox.pdmodel.PDDocument;

public class UtilityBox {

  private static UtilityBox instance = null;
  private MainWindow mainWindow = null;
  private Calendar calendar = null;
  private Date[] holidays = null;

  public UtilityBox(MainWindow mainWindow) {
    instance = this;
    this.mainWindow = mainWindow;
    calendar = Calendar.getInstance();
  }

  public static void instanciate(MainWindow mainWindow) {
    if (instance != null) {
      return;
    }

    instance = new UtilityBox(mainWindow);
  }

  public static UtilityBox getInstance() {
    return instance;
  }

  /**
   * Display an error message
   * 
   * @param title
   *            Title of the message
   * @param message
   *            Message
   */
  public void displayErrorPopup(String title, String message) {
    displayPopup(title, message, JOptionPane.ERROR_MESSAGE);
  }

    /**
   * Display an information message
   * 
   * @param title
   *            Title of the message
   * @param message
   *            Message
   */
  public void displayInfoPopup(String title, String message) {
    displayPopup(title, message, JOptionPane.INFORMATION_MESSAGE);
  }
  /**
   * 
   * @param title
   *            Title of the message
   * @param message
   *            Message text
   * @param messageType
   *            Message Type (e.g error)
   */
  private void displayPopup(String title, String message, int messageType) {
    JOptionPane.showMessageDialog(mainWindow, message, title, messageType);
  }

  /**
   * 
   * @param time the time value as integer
   * @return
   */
  public static String createTimeStringFromInt(int time) {
    String timeString;
    if (time >= 1000) {
      timeString = ((int) (time / 100)) + "";
    } else {
      timeString = "0" + ((int) (time / 100));
    }
    timeString = timeString + (((time % 100) < 10) ? ":0" : ":")
            + (time % 100);
    return timeString;
  }

  /**
   * get a german month-name from an int value. note that months start with 
   * 0 for january in this case
   * @param month a value from 0 to 11
   * @return german month-name
   */
  public static String getMonthString(int month) {
    switch (month) {
      case 0:
        return "Januar";
      case 1:
        return "Februar";
      case 2:
        return "März";
      case 3:
        return "April";
      case 4:
        return "Mai";
      case 5:
        return "Juni";
      case 6:
        return "Juli";
      case 7:
        return "August";
      case 8:
        return "September";
      case 9:
        return "Oktober";
      case 10:
        return "November";
      default:
        return "Dezember";
    }
  }
  public static String getDayOfWeekString(int dayOfWeek) {
    switch (dayOfWeek) {
      case Calendar.MONDAY:
        return "Mo";
      case Calendar.TUESDAY:
        return "Di";
      case Calendar.WEDNESDAY:
        return "Mi";
      case Calendar.THURSDAY:
        return "Do";
      case Calendar.FRIDAY:
        return "Fr";
      case Calendar.SATURDAY:
        return "Sa";
      case Calendar.SUNDAY:
        return "So";
      default:
        return "";
    }
  }
  /**
   * return a date-string in format dd.mm.yyyy
   * @param date
   * @return date-string
   */
  public static String getFormattedDateString(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return getTwoLetterStringFromInt(calendar.get(Calendar.DAY_OF_MONTH)) + "."
            + getTwoLetterStringFromInt(calendar.get(Calendar.MONTH)+1) + "."
            + calendar.get(Calendar.YEAR);
  }
  public static String getTwoLetterStringFromInt(int x) {
    return ((x < 10) ? ("0" + x) : x + "");
  }

  public float calculateSalary(ShiftInstance shift) {
    float salary;
    switch (shift.getType()) {
      case KTW:
      case RTW:
        switch (PersonalData.getInstance().getQualification()) {
          case RH:
            salary = 7.8f;
            break;
          case RS:
            salary = 8.8f;
            break;
          default:
            salary = 9.9f;
        }
        break;
      case KVS:
        salary = 9f;
        break;
      default:
        switch (PersonalData.getInstance().getQualification()) {
          case RH:
            salary = 5.9f;
            break;
          case RS:
            salary = 6.7f;
            break;
          default:
            salary = 7.6f;
        }
    }
    return salary;
  }
  public String calculateTimeInHours(int start, int end, int breakTime) {
    int time = calculateTime(start, end, breakTime);
    int hours = ((int) (time / 100));
    int minutes = (time % 100);
    return createTimeStringFromInt((hours*100)+minutes);
  }
  public float calculateTimeAsFloat(int start, int end, int breakTime) {
    int time = calculateTime(start, end, breakTime);
    int hours = ((int) (time / 100));
    int minutes = (time % 100);
    return (float)(hours+(minutes/60));
  }
  private int calculateTime(int start, int end, int breakTime) {
    int hours = 0;
    int minutes = 0;
    int breakTimeHours = ((int) (breakTime / 100));
    int breakTimeMinutes = (breakTime % 100);
    int endHours = ((int) (end / 100));
    int endMinutes = (end % 100);
    if(start>end) {
      int firstDayHours = 24 - ((int) (start / 100));
      int firstDayMinutes = (start % 100);
      if(firstDayMinutes!=0) {
        firstDayHours--;
        firstDayMinutes = 60-firstDayMinutes;
      }
      hours = firstDayHours+endHours-breakTimeHours;
      minutes = firstDayMinutes+endMinutes+breakTimeMinutes;
      while (minutes>=60) {        
        minutes-=60;
        hours++;
      }
    }
    else {
      int startHours = ((int) (start / 100));
      int startMinutes = (start % 100);
      hours = endHours-startHours-breakTimeHours;
      minutes = endMinutes-startMinutes-breakTimeMinutes;
      while (minutes<0) {        
        minutes+=60;
        hours--;
      }
    }
    return ((hours*100)+minutes);
  }
  public boolean isHoliday(Date date) {
    calendar.setTime(date);
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    int month = calendar.get(Calendar.MONTH);
    int year = calendar.get(Calendar.YEAR);
    if (holidays == null) {
      holidays = calculateGermanBWHolidays(year);
    }
    for (int i = 0; i < holidays.length; i++) {
      calendar.setTime(holidays[i]);
      if(day == calendar.get(Calendar.DAY_OF_MONTH)
              && month == calendar.get(Calendar.MONTH)) {
        return true;
      }
    }
    return false;
  }

  private Date[] calculateGermanBWHolidays(int year) {
    // calculation of easter sunday
    int ES;
    {
      // säkularzahl
      int säkularzahl = (int) Math.floor(year / 100);
      // säkulareMondschaltung
      int säkulareMondschaltung = (int) (15 + Math.floor((3 * säkularzahl + 3) / 4)
              - Math.floor((8 * säkularzahl + 13) / 25));
      // säkulareSonnenschaltung
      int säkulareSonnenschaltung = (int) (2 - Math.floor((3 * säkularzahl + 3) / 4));
      // Mondarameter
      int mondParameter = year % 19;
      // ersterFrühlingsVollmond
      int ersterFrühlingsVollmond = (19 * mondParameter + säkulareMondschaltung) % 30;
      // kalendarische Korrekturgröße
      int kalendarischeKorrekturgröße = (int) (Math.floor(ersterFrühlingsVollmond / 29)
              + (Math.floor(ersterFrühlingsVollmond / 28)
              - Math.floor(ersterFrühlingsVollmond / 29))
              * (Math.floor(mondParameter / 11)));

      // Ostergrenze:
      int OG = 21 + ersterFrühlingsVollmond - kalendarischeKorrekturgröße;
      // ersten Sonntag im März
      int SZ = (int) (7 - (year + Math.floor(year / 4) + säkulareSonnenschaltung) % 7);
      // Entfernung des Ostersonntags von der Ostergrenze (Osterentfernung in Tagen)
      int OE = 7 - (OG - SZ) % 7;
      // Datum des Ostersonntags als Märzdatum (32. März = 1. April usw.)
      ES = OG + OE;
    }
    Date[] holidayDates = new Date[12];
    calendar.set(year, 2, 1);
    calendar.add(Calendar.DAY_OF_YEAR, ES);
    Date easterSunday = calendar.getTime();
    calendar.add(Calendar.DAY_OF_YEAR, -3);
    Date goodFriday = calendar.getTime();
    calendar.add(Calendar.DAY_OF_YEAR, 3);
    Date easterMonday = calendar.getTime();
    calendar.add(Calendar.DAY_OF_YEAR, 38);
    Date ascensionDay = calendar.getTime();
    calendar.add(Calendar.DAY_OF_YEAR, 11);
    Date whitMonday = calendar.getTime();
    calendar.add(Calendar.DAY_OF_YEAR, 10);
    Date corpusChristi = calendar.getTime();
    // new years day
    calendar.set(year, 0, 1);
    holidayDates[0] = calendar.getTime();
    // Twelfth Day (the three Magi)
    calendar.set(year, 0, 6);
    holidayDates[1] = calendar.getTime();
    holidayDates[2] = goodFriday;
    holidayDates[3] = easterMonday;
    // labour day
    calendar.set(year, 4, 1);
    holidayDates[4] = calendar.getTime();
    holidayDates[5] = ascensionDay;
    holidayDates[6] = whitMonday;
    holidayDates[7] = corpusChristi;
    // German Unification Day
    calendar.set(year, 9, 3);
    holidayDates[8] = calendar.getTime();
    // Allhallows
    calendar.set(year, 10, 1);
    holidayDates[9] = calendar.getTime();
    // first and second chrismas day
    calendar.set(year, 11, 25);
    holidayDates[10] = calendar.getTime();
    calendar.set(year, 11, 26);
    holidayDates[11] = calendar.getTime();
    for (int i = 0; i < holidayDates.length; i++) {
      Date date = holidayDates[i];
      calendar.setTime(date);
      System.out.println("feiertag "+(i+1)+": "+calendar.get(Calendar.DAY_OF_MONTH)+"."+(calendar.get(Calendar.MONTH)+1)+"."+calendar.get(Calendar.YEAR));
    }
    return holidayDates;
  }
  public boolean printFile(String filePath) {
//    FileInputStream fileInputStream = null;
//    try {
//      //try {
      File file = new File(filePath);
      if (!file.exists()) {
        displayErrorPopup("Drucken", "Zu druckende Datei nicht gefunden");
        return false;
      }
      PDDocument document = null;
      try {
        document = PDDocument.load(filePath);
        document.print();
    } catch (Exception e) {
    } finally {
        if(document!=null){
        try {
          document.close();
        } catch (IOException ex) {
          ex.printStackTrace();
        }
        }
      }
//      fileInputStream = new FileInputStream(file);
//      FileChannel fileChannel = fileInputStream.getChannel();
//      ByteBuffer byteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
//      PDFFile pdfFile = new PDFFile(byteBuffer);
//      PDFPrintPage pdfPrintPage = new PDFPrintPage(pdfFile);
//      //create print job
//      PrinterJob printJob = PrinterJob.getPrinterJob();
//      PageFormat pageFormat = printJob.defaultPage();
//      //      Paper a4paper = new Paper();
//      //            double paperWidth = 8.27;
//      //            double paperHeight = 11.69;
//      //            a4paper.setSize(paperWidth * 72.0, paperHeight * 72.0);
//      //
//      //            /*
//      //             * set the margins respectively the imageable area
//      //             */
//      //            double leftMargin = 0.3;
//      //            double rightMargin = 0.3;
//      //            double topMargin = 0.5;
//      //            double bottomMargin = 0.5;
//      //
//      //            a4paper.setImageableArea(leftMargin * 72.0, topMargin * 72.0,
//      //                    (paperWidth - leftMargin - rightMargin) * 72.0,
//      //                    (paperHeight - topMargin - bottomMargin) * 72.0);
//      //            pageFormat.setPaper(a4paper);
//            printJob.setJobName(file.getName());
//            Book book = new Book();
//            book.append(pdfPrintPage, pageFormat, pdfFile.getNumPages());
//            printJob.setPageable(book);
//            if (printJob.printDialog()) {
//                printJob.print();
//                return true;
//              }
//            fileChannel.close();
//      //    } catch (IOException | PrinterException ex) {
//      //      displayErrorPopup("Drucken", ex.getMessage());
//      //    }
//      return false;
//    } catch (IOException |PrinterException ex) {
//      ex.printStackTrace();
//    } finally {
//      try {
//        fileInputStream.close();
//      } catch (IOException ex) {
//        ex.printStackTrace();
//      }
//    }
    return false;
  }
  public void testStuff() {
    Document document = new Document(PageSize.A4, 50, 50, 50, 50);
    try {
      PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("output.pdf"));
      document.open();
      PdfContentByte cb = writer.getDirectContent();
      RadioCheckField bt = new RadioCheckField(writer, new Rectangle(100, 100, 200, 200), "radio", "v1");
      bt.setCheckType(RadioCheckField.TYPE_CIRCLE);
      bt.setBackgroundColor(BaseColor.CYAN);
      bt.setBorderStyle(PdfBorderDictionary.STYLE_SOLID);
      bt.setBorderColor(BaseColor.RED);
      bt.setTextColor(BaseColor.YELLOW);
      bt.setBorderWidth(BaseField.BORDER_WIDTH_THICK);
      bt.setChecked(false);
      PdfFormField f1 = bt.getRadioField();
      bt.setOnValue("v2");
      bt.setChecked(true);
      bt.setBox(new Rectangle(100, 300, 200, 400));
      PdfFormField f2 = bt.getRadioField();
      bt.setChecked(false);
      PdfFormField top = bt.getRadioGroup(true, false);
      bt.setOnValue("v3");
      bt.setBox(new Rectangle(100, 500, 200, 600));
      PdfFormField f3 = bt.getRadioField();
      top.addKid(f1);
      top.addKid(f2);
      top.addKid(f3);
      writer.addAnnotation(top);
      bt = new RadioCheckField(writer, new Rectangle(300, 300, 400, 400), "check1", "Yes");
      bt.setCheckType(RadioCheckField.TYPE_CHECK);
      bt.setBorderWidth(BaseField.BORDER_WIDTH_THIN);
      bt.setBorderColor(BaseColor.BLACK);
      bt.setBackgroundColor(BaseColor.WHITE);
      PdfFormField ck = bt.getCheckField();
      writer.addAnnotation(ck);
      document.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    } catch (DocumentException ex) {
      ex.printStackTrace();
    }
  }
}
