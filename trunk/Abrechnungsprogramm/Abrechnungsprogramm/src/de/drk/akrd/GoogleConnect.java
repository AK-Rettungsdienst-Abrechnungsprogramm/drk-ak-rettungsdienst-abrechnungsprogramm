/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.drk.akrd;

import com.google.gdata.client.*;
import com.google.gdata.data.*;
import com.google.gdata.data.extensions.*;
import java.net.URL;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

/**
 *
 * @author Jo
 */
public class GoogleConnect extends CalendarManager {

  public GoogleConnect() {
    super();
  }
    /**
   * creates Googleclaendar-entrys for the saved shifts
   * @return true if successful, false otherwise
   */
  public boolean createGoogleCalendarEntry(Shift[] shifts, Date[] shiftDates) {
    if (shifts == null || shiftDates == null) {
      return false;
    }
    String[][] entryStrings = new String[shifts.length][4];
      for (int i = 0; i < shifts.length; i++) {
        String[] beginEndStrings = getBeginEndStrings(shifts[i], shiftDates[i], calendarEntryType.GOOGLE_ENTRY);
        entryStrings[i][0] = shifts[i].getId().substring(0, 3);
        entryStrings[i][1] = "Termintext";
        entryStrings[i][2] = beginEndStrings[0];
        entryStrings[i][3] = beginEndStrings[1];
      }
      GoogleConnect.createNewAppointment(entryStrings);
    return false;
  }
  private static void createNewAppointment(String[][] entrys) {

    try {
      PersonalData personalData = PersonalData.getInstance();
      URL postUrl =
              new URL("http://www.google.com/calendar/feeds/"+personalData.getCalendarId()+"@group.calendar.google.com/private/full");

      GoogleService myService = new GoogleService("cl", "AKRD-AKRD_Abrechnungsprogramm-0.1");
      JPasswordField passwordField = new JPasswordField();
      passwordField.setEchoChar('*');
      JOptionPane.showMessageDialog(null, passwordField, "Bitte Passwort eingeben:", JOptionPane.OK_CANCEL_OPTION);
      myService.setUserCredentials(personalData.getEmailAdress(), String.valueOf(passwordField.getPassword()));
      

      Person author = new Person("AK-Abrechnungsprogramm", null, "software@ak-rd.de");
      

      for (int i = 0; i < entrys.length; i++) {
        EventEntry myEntry = new EventEntry();
        myEntry.getAuthors().add(author);
        myEntry.setTitle(new PlainTextConstruct(entrys[i][0]));
        myEntry.setContent(new PlainTextConstruct(entrys[i][1]));
        DateTime startTime = DateTime.parseDateTime(entrys[i][2]);
        DateTime endTime = DateTime.parseDateTime(entrys[i][3]);
        When eventTimes = new When();
        eventTimes.setStartTime(startTime);
        eventTimes.setEndTime(endTime);
        myEntry.addTime(eventTimes);

        // Send the request and receive the response:
        EventEntry insertedEntry = myService.insert(postUrl, myEntry);
      }

      UtilityBox.getInstance().displayInfoPopup("Google Kalender", "Termine wurden eingetragen.");
      // TODO: for JDK7 use Multicatch
    } catch (Exception ex){//IOException | ServiceException ex) {
      UtilityBox.getInstance().displayErrorPopup("Google Kalender", "Eintragen des Termins fehlgeschlagen: "+ex.getMessage());
    }
  }
}
