/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.drk.akrd;

import com.google.gdata.client.*;
import com.google.gdata.client.calendar.*;
import com.google.gdata.data.*;
import com.google.gdata.data.extensions.*;
import com.google.gdata.util.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

/**
 *
 * @author Jo
 */
public class GoogleConnect {

  public static void createNewAppointment(String[][] entrys) {

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

      System.out.println("termine eingetragen!");
    } catch (IOException | ServiceException ex) {
      System.out.println("Eintragen des Termins fehlgeschlagen: "+ex.getMessage());
    }
  }
}
