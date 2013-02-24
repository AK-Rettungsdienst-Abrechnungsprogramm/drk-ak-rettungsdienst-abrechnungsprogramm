package de.drk.akrd;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.Popup;
import javax.swing.PopupFactory;

import com.toedter.calendar.JCalendar;

import de.drk.akrd.ShiftContainer.ShiftType;

/**
 * 
 * @author niklas
 * 
 *         Mouse adapter that manages all mouse interaction with the main window
 */
public class AKRDMouseAdapter extends MouseAdapter {

	private MainWindow mainWindow;
    private CreateAccountmentFrame createAccountmentFrame;

	// JButton used to apply date from calendar
	private JButton apply = new JButton("Übernehmen");

	private JButton closePopup = new JButton("Ok");
	// The calendar popup
	private Popup calendarPopup;
	// A Message Popup
	private JDialog messagePopup = new JDialog();
	// The calendar widget
	private JCalendar calendar = new JCalendar();

	public AKRDMouseAdapter(MainWindow mainWindow) {
		this.mainWindow = mainWindow;

		this.calendar.setLocale(java.util.Locale.GERMANY);
		apply.addMouseListener(this);
		closePopup.addMouseListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		Object source = e.getSource();
		
		// Handle clicks on the popup
		if (source == closePopup) {
			messagePopup.setVisible(false);
			return;
		}

		// Read DPL
		if(source == mainWindow.read_DPL)
		{
			DRManager.GetInstance().parseDutyRota();
			mainWindow.updateShiftsFromDPL();
			return;
		}
		
		// Export calendar data
		if(source == mainWindow.iCalButton || source == mainWindow.googleCalButton)
		{
			if(DRManager.GetInstance().getSavedShifts() == null)
			{
				UtilityBox.getInstance().displayErrorPopup("Fehler", "Bitte zuerst Dienstplan auslesen!");
				return;
			}
			
			if(source == mainWindow.iCalButton)
			{
			 ICalendar iCalendar = new ICalendar();
	         iCalendar.createICalendarFile(DRManager.GetInstance().getSavedShifts(), DRManager.GetInstance().getSavedShiftDates());
	         return;
			}
			if(source == mainWindow.googleCalButton)
			{
				GoogleConnect gc = new GoogleConnect();
				
				gc.createGoogleCalendarEntry(DRManager.GetInstance().getSavedShifts(), DRManager.GetInstance().getSavedShiftDates());
			}
		}
		
		// Apply personal info
		if(source == mainWindow.personalInfoApply)
		{
			String firstName = mainWindow.firstNameField.getText();
			String lastName = mainWindow.lastNameField.getText();
			
			PersonalData.Qualification quali = (PersonalData.Qualification)mainWindow.trainingsChooser.getSelectedItem();
			
			if(firstName.length() == 0 || lastName.length() == 0)
			{
				UtilityBox.getInstance().displayErrorPopup("Fehler", "Bitte Namen korrekt eingeben!");
				return;
			}
			
			if(quali == null)
			{
				UtilityBox.getInstance().displayErrorPopup("Fehler", "Bitte Ausbildung auswählen!");
				return;
			}
			
			// check bank data (only if bankDataKnow is not checked)
			
			boolean dataKnown = mainWindow.bankInfoKnown.isSelected();
			
			int blz = 0;
			int account = 0;
			String bankName = "";

			if(!dataKnown) {
				try
				{
					blz = Integer.parseInt(mainWindow.blz.getText());
					account = Integer.parseInt(mainWindow.accountNo.getText());
				}
				catch(NumberFormatException exeption)
				{
					UtilityBox.getInstance().displayErrorPopup("Fehler", "Bitte Bankdaten korrekt eingeben!");
					return;
				}
				
				bankName = mainWindow.bankNameField.getText();
				
				if(bankName.length() == 0)
				{
					UtilityBox.getInstance().displayErrorPopup("Fehler", "Bitte Bankdaten korrekt eingeben!");
					return;
				}
			}

			String gmail = mainWindow.gMailAdressField.getText();
			String calID = mainWindow.calendarIDFiled.getText();
			
			PersonalData pd = PersonalData.getInstance();
			
			boolean success = pd.setData(firstName, lastName, bankName, account, blz, quali, dataKnown, gmail, calID);
            if (success) {
                UtilityBox.getInstance().displayInfoPopup("Persönliche Daten", "Daten gespeichert.");
//                mainWindow.updateRegisteredShifts();
            }
			return;
			
		}
	}



}
