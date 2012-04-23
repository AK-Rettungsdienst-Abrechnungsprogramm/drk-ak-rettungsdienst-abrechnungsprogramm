package de.drk.akrd;

import java.util.Calendar;
import java.util.Date;
import javax.swing.JOptionPane;

public class UtilityBox {

  private static UtilityBox instance = null;
  private MainWindow mainWindow = null;

  public UtilityBox(MainWindow mainWindow) {
    instance = this;
    this.mainWindow = mainWindow;
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
}
