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
import com.itextpdf.text.BaseColor;

import de.drk.akrd.PersonalData.Qualification;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class UtilityBox {

  private static UtilityBox instance = null;
  private MainWindow mainWindow = null;
  private ShiftContainer shiftContainer = null;
  private Calendar calendar = null;
  public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
  private Date[] holidays = null;
  private static HashMap<ShiftContainer.ShiftType, HashMap<Qualification, Float>> salaryMap;

  // the default font used throughout the program 
  private static final Font defaultFont = new Font("Dialog", Font.BOLD, 12);

  // round to 2 decimal places
  private static DecimalFormat decimalFormat = new DecimalFormat("#0.00");

  public UtilityBox(MainWindow mainWindow) {
    instance = this;
    this.mainWindow = mainWindow;
    this.shiftContainer = mainWindow.shiftContainer;
    calendar = Calendar.getInstance();

    checkVersion();
    readSalary();
  }

  public static void instanciate(MainWindow mainWindow) {
    if (instance != null) {
      return;
    }

    instance = new UtilityBox(mainWindow);
  }

  /**
   *
   */
  public void checkVersion() {
    // get newest available version
    Thread versionChecker = new Thread() {
      public void run() {
        // get latest Program version
        Update.readUpdateData();
        float latestProgramVersion = Update.getLatestProgramVersion();
        // query sucessfull + no newer version
        if (latestProgramVersion <= MainWindow.PROGRAM_VERSION && latestProgramVersion > 0) {
          // check shift file version
          float newestShiftFileVersion = Update.getLatestShiftFileVersion();
          float newestSalaryFileVersion = Update.getLatestSalaryFileVersion();
          boolean newShiftFile = false;
          boolean newSalaryFile = false;
          if (newestShiftFileVersion > MainWindow.SHIFT_FILE_VERSION) {
            newShiftFile = true;
          }
          if (newestSalaryFileVersion > MainWindow.SALARY_FILE_VERSION) {
            newSalaryFile = true;
          }
          if (newSalaryFile && newShiftFile) {
            setStatusBarText("Es sind neue Versionen der Schichten- und Gehaltsdateien verfügbar. Bitte unter Info/Update herunterladen!", new Color(180, 0, 0));
            return;
          } else if (newSalaryFile) {
            setStatusBarText("Es ist eine neue Version der Gehalts-Datei verfügbar. Bitte unter Info/Update herunterladen!", new Color(180, 0, 0));
            return;
          } else if (newShiftFile) {
            setStatusBarText("Es ist eine neue Version der Schichten Datei verfügbar. Bitte unter Info/Update herunterladen!", new Color(180, 0, 0));
            return;
          }
          setStatusBarText("Das Programm ist auf dem neusten Stand!", new Color(0, 100, 0));
          return;
        }
        // query sicessfull & newer version available
        if (latestProgramVersion > MainWindow.PROGRAM_VERSION) {
          setStatusBarText("Es ist eine neue Version (" + Float.toString(latestProgramVersion)
              + ") verfügbar. Bitte lade diese herunter!", new Color(180, 0, 0));
          return;
        }
        // query not sucessfull
        if (latestProgramVersion <= 0) {
          setStatusBarText("Konnte die aktuelle Version nicht feststellen. Evtl. besteht keine Verbindung zum Internet.",
                           new Color(200, 150, 0));
          return;
        }

      }
    };

    versionChecker.start();
  }

  /**
   * read salary from Salary.xml to salaryMap
   *
   * @return success
   */
  private static boolean readSalary() {
    File salaryFile = new File(Update.SALARY_FILE_PATH);
    if (salaryFile.exists()) {
      salaryMap = new HashMap<ShiftContainer.ShiftType, HashMap<Qualification, Float>>();
      FileReader fileReader = null;
      try {
        SAXBuilder saxb = new SAXBuilder();
        fileReader = new FileReader(salaryFile);
        org.jdom.Document salaryFileDocument = saxb.build(fileReader);
        List elements = salaryFileDocument.getRootElement().getChildren("Shifttype");
        for (Iterator iterator = elements.iterator(); iterator.hasNext();) {
          Element shiftTypeNode = (Element) iterator.next();
          String shiftTypeName = shiftTypeNode.getAttributeValue("name");

          ShiftContainer.ShiftType shiftType = ShiftContainer.ShiftType.valueOf(shiftTypeName);
          // fill map with <qualification, salary>
          List qualificationNodes = shiftTypeNode.getChildren();
          HashMap<Qualification, Float> qualificationSalaryMap = new HashMap<Qualification, Float>();
          for (Iterator iterator1 = qualificationNodes.iterator(); iterator1.hasNext();) {
            Element qualificationNode = (Element) iterator1.next();
            Qualification q = Qualification.valueOf(qualificationNode.getName());
            qualificationSalaryMap.put(q, Float.parseFloat(qualificationNode.getValue()));
          }
          salaryMap.put(shiftType, qualificationSalaryMap);
        }
        return true;
      } catch (Exception ex) { // TODO: multicatch FileNotFoundException | JDOMException
        Logger.getLogger(UtilityBox.class.getName()).log(Level.SEVERE, null, ex);
      } finally {
        try {
          fileReader.close();
        } catch (IOException ex) {
          Logger.getLogger(UtilityBox.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }

    return false;
  }

  public ShiftContainer getShiftContainer() {
    return shiftContainer;
  }

  public static UtilityBox getInstance() {
    return instance;
  }

  /**
   * Display an error message
   *
   * @param title Title of the message
   * @param message Message
   */
  public void displayErrorPopup(String title, String message) {
    displayPopup(title, message, JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Display an information message
   *
   * @param title Title of the message
   * @param message Message
   */
  public void displayInfoPopup(String title, String message) {
    displayPopup(title, message, JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   *
   * @param title Title of the message
   * @param message Message text
   * @param messageType Message Type (e.g error)
   */
  private void displayPopup(String title, String message, int messageType) {
    JOptionPane.showMessageDialog(mainWindow, message, title, messageType);
  }

  /**
   * Display a Yes-No-dialog
   *
   * @param title
   * @param message
   * @return true if "Yes" was clicked, false otherwise
   */
  public boolean displayYesNoPopup(String title, String message) {
    int yesNo = JOptionPane.showConfirmDialog(mainWindow, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    if (yesNo == 0) {
      return true;
    }
    return false;
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
   * get a german month-name from an int value. note that months start with 0
   * for january in this case
   *
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

  /**
   * returns the first 2 letters of the german dayname i.e. for the input
   * Calendar.MONDAY the function will return "Mo". Default: ""
   *
   * @param dayOfWeek Calendar.dayname
   * @return first two letters of german dayname
   */
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
   *
   * @param date
   * @return date-string
   */
  public static String getFormattedDateString(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return getTwoLetterStringFromInt(calendar.get(Calendar.DAY_OF_MONTH)) + "."
        + getTwoLetterStringFromInt(calendar.get(Calendar.MONTH) + 1) + "."
        + calendar.get(Calendar.YEAR);
  }

  /**
   * returns a two-letter-string if x € {0, ..., 99}
   *
   * @param x
   * @return
   */
  public static String getTwoLetterStringFromInt(int x) {
    return ((x < 10) ? ("0" + x) : x + "");
  }

  /**
   * calculates the salary for a given shiftInstance in dependence of the
   * Qualification
   *
   * @param shiftType
   * @param qualification
   * @return salary as float (0 if an exception occurs, meaning no personal data
   * is known)
   */
  public static float calculateSalaryPerHour(ShiftContainer.ShiftType shiftType, Qualification qualification) {
    Float salary;
    if (salaryMap == null) {
      if (!readSalary()) {

        return 0f;
      }
    }
    HashMap<Qualification, Float> qualificationMap = salaryMap.get(shiftType);
    if ((qualificationMap != null) && (salary = qualificationMap.get(qualification)) != null) {
      return salary;
    }

    return 0f;
  }

  public static float calculateSalaryPerHour(ShiftInstance shiftType, Qualification qualification) {
    return calculateSalaryPerHour(shiftType.getType(), qualification);
  }

  public static String salaryPerHourString(ShiftContainer.ShiftType shiftType, Qualification qualification) {
    return decimalFormat.format(calculateSalaryPerHour(shiftType, qualification));
  }

  /**
   * Takes a shift instance and a qualification and returns the salary for the
   * complete shift
   *
   * @param shift
   * @param quali
   * @return
   */
  public static float calculateSalary(ShiftInstance shift, Qualification quali) {
    // calculate salary per hour
    float hourlySalary = calculateSalaryPerHour(shift, quali);
    // salary without commute expenses
    float salary = hourlySalary * shift.getTimeAsFloat();

    return salary;
  }

  public String calculateTimeInHours(int start, int end, int breakTime) {
    int time = calculateTime(start, end, breakTime);
    int hours = ((int) (time / 100));
    int minutes = (time % 100);
    return createTimeStringFromInt((hours * 100) + minutes);
  }

  public void testTime() {
    printTestTime(0, 1400, 30, 1330);
    printTestTime(1500, 600, 0, 1500);
    printTestTime(600, 1431, 30, 801);
    printTestTime(1650, 213, 45, 838);
    printTestTime(1550, 36, 30, 816);
    printTestTime(2350, 112, 0, 122);
    printTestTime(2330, 57, 45, 42);
    printTestTime(1550, 6, 30, 746);
    printTestTime(1550, 20, 30, 800);
    printTestTime(1506, 57, 30, 921);
  }

  private void printTestTime(int start, int end, int breakTime, int expected) {
    System.out.println(createTimeStringFromInt(start) + "-"
        + createTimeStringFromInt(end) + "; pause: "
        + createTimeStringFromInt(breakTime) + "; Ergebnis(String): "
        + calculateTimeInHours(start, end, breakTime) + " Ergebnis(float): "
        + calculateTimeAsFloat(start, end, breakTime) + " Ergebnis(fkt): "
        + calculateTime(start, end, breakTime) + " Erwartet: " + expected);
  }

  /**
   * Takes starting time, end time and breaktime and returns the time not on
   * break as a float (e.g. two hours 45 minutes -> 2.75)
   *
   * @param start Starting time as an int representing a 24h time (e.g. 02:45 ->
   * 245)
   * @param end same for end
   * @param breaktime
   * @return
   */
  public static float calculateTimeAsFloat(int start, int end, int breaktime) {
    int time = calculateTime(start, end, breaktime);
    int hours = ((int) (time / 100));
    int minutes = (time % 100);
    return ((float) (hours + (minutes / 60.0)));
  }

  private static int calculateTime(int start, int end, int breakTime) {
    int time = 0;
    int startMinutes = (((int) (start / 100)) * 60) + (start % 100);
    int endMinutes = (((int) (end / 100)) * 60) + (end % 100);
    int breakTimeMinutes = (((int) (breakTime / 100)) * 60) + (breakTime % 100);
    if (start > end) {
      int firstDayTotalMinutes = 1440 - startMinutes;
      time = firstDayTotalMinutes + endMinutes - breakTimeMinutes;
    } else {
      time = endMinutes - startMinutes - breakTimeMinutes;
    }
    int hours = (int) (time / 60);
    int minutes = time % 60;

    return ((hours * 100) + minutes);
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
      if (day == calendar.get(Calendar.DAY_OF_MONTH)
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
      int ersterFruehlingsVollmond = (19 * mondParameter + säkulareMondschaltung) % 30;
      // kalendarische Korrekturgröße
      int kalendarischeKorrekturgroesse = (int) (Math.floor(ersterFruehlingsVollmond / 29)
          + (Math.floor(ersterFruehlingsVollmond / 28)
          - Math.floor(ersterFruehlingsVollmond / 29))
          * (Math.floor(mondParameter / 11)));

      // Ostergrenze:
      int OG = 21 + ersterFruehlingsVollmond - kalendarischeKorrekturgroesse;
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
      //System.out.println("feiertag "+(i+1)+": "+calendar.get(Calendar.DAY_OF_MONTH)+"."+(calendar.get(Calendar.MONTH)+1)+"."+calendar.get(Calendar.YEAR));
    }
    return holidayDates;
  }

  /**
   * print a given file with System-specific standard-printer
   *
   * @param filePath
   * @return
   */
  public boolean printFile(String filePath) {
    File file = new File(filePath);
    if (!file.exists()) {
      displayErrorPopup("Drucken", "Zu druckende Datei nicht gefunden");
      return false;
    }
    PDDocument document = null;
    try {
      document = PDDocument.load(filePath);
      document.print();
      // TODO: for JDK7 use Multicatch
    } catch (Exception e) {//IOException | PrinterException e) {
      displayErrorPopup("Drucken", "Fehler während des Druckvorgangs:\n" + e.getMessage());
    } finally {
      if (document != null) {
        try {
          document.close();
        } catch (IOException ex) {
          displayErrorPopup("Drucken", "Dokument konnte nicht geschlossen werden:\n" + ex.getMessage());
        }
      }
    }
    return false;
  }

  public String getFilePathFromFileCooser(final String fileEnding, final String fileTypeDescription, String directory) {
    String startDirectory = (directory == null)
        ? System.getProperty("user.dir") : directory;
    JFileChooser fileChooser = new JFileChooser(new File(startDirectory));
    fileChooser.setMultiSelectionEnabled(false);
    fileChooser.setFileFilter(new FileFilter() {

      @Override
      public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase().endsWith(fileEnding);
      }

      @Override
      public String getDescription() {
        return fileTypeDescription;
      }
    });
    int state = fileChooser.showOpenDialog(null);
    if (state == JFileChooser.APPROVE_OPTION) {
      return fileChooser.getSelectedFile().getPath();
    }
    return null;
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

  /**
   * Create a Directory
   *
   * @param path
   * @return true if successful, false otherwise
   */
  public static boolean createDirectory(String path) {
    boolean success = false;
    File dir = new File(path);
    if (dir.exists()) {
      success = true;
    } else {
      success = dir.mkdirs();
    }
    return success;
  }

  public String saveDialog(final String fileEnding, String fileName, final String fileTypeDescription) {
    String filePath;
    JFileChooser jFileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
    jFileChooser.setMultiSelectionEnabled(false);
    jFileChooser.setSelectedFile(new File(fileName + fileEnding));
    jFileChooser.setFileFilter(new FileFilter() {

      @Override
      public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase().endsWith(fileEnding);
      }

      @Override
      public String getDescription() {
        return fileTypeDescription;
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
    return filePath;
  }

  /**
   * returns true if type is a shift with preparation time
   *
   * @param type
   * @return
   */
  public static boolean hasPreparationTime(ShiftContainer.ShiftType type) {
    if (type == ShiftContainer.ShiftType.RTW || type == ShiftContainer.ShiftType.KTW
        || type == ShiftContainer.ShiftType.BREISACH || type == ShiftContainer.ShiftType.KIZA) {
      return true;
    }
    return false;
  }

  // Used to trigger a reload of the shifts file 
  public void requestShiftListReload() {
    mainWindow.shiftContainer.loadShifts("Schichten.xml");
    mainWindow.sc.updateShiftContainer();
    checkVersion();
  }

  // returns the x position of the main window
  int getWindowPosX() {
    return this.mainWindow.getLocation().x;
  }

  // returns the y position of the main window
  int getWindowPosY() {
    return this.mainWindow.getLocation().y;
  }

  public static Font getDefaultFont() {
    return defaultFont;
  }

  public void setStatusBarText(String text, Color color) {
    mainWindow.statusBar.setForeground(color);
    mainWindow.statusBar.setText(text);
  }

  public static boolean isSalarySet() {
    if (salaryMap == null) {
      if (readSalary()) {
        return true;
      } else {
        return false;
      }
    } else {
      return true;
    }

  }
}
