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
	}



}
