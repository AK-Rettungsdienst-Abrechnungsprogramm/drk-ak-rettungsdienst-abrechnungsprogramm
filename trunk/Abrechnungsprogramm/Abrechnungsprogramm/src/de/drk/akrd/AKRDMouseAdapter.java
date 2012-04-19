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

		apply.addMouseListener(this);
		closePopup.addMouseListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		Object source = e.getSource();

		if (source == mainWindow.dateField) {
			PopupFactory factory = PopupFactory.getSharedInstance();

			JPanel panel = new JPanel();
			panel.add(calendar);
			panel.add(apply);

			calendarPopup = factory.getPopup(e.getComponent().getParent(),
					panel, mainWindow.getLocation().x,
					mainWindow.getLocation().y);
			calendarPopup.show();
			return;
		}

		if (source == apply) {
			Date selectedDate = calendar.getDate();

			StringBuilder dateString = new StringBuilder();

			Calendar cal = Calendar.getInstance();
			cal.setTime(selectedDate);

			dateString.append(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
			dateString.append(".");
			int month = cal.get(Calendar.MONTH) + 1;
			if (month < 10)
				dateString.append("0");
			dateString.append(Integer.toString(month));
			dateString.append(".");
			dateString.append(Integer.toString(cal.get(Calendar.YEAR)));

			// Set the selected type of day in mainWindow
			int dayType = cal.get(Calendar.DAY_OF_WEEK);

			switch (dayType) {
			case 1: // sunday
				mainWindow.currentlySelectedDay = 2;
				break;
			case 7: // saturday
				mainWindow.currentlySelectedDay = 1;
				break;
			default:
				mainWindow.currentlySelectedDay = 0;
				break;
			}

			mainWindow.dateField.setText(dateString.toString());
			calendarPopup.hide();
			mainWindow.updateShiftContainer();
			return;
		}

		// Handle clicks on the popup
		if (source == closePopup) {
			messagePopup.setVisible(false);
			return;
		}

		// Handle clicks on the shift list, set begin, end, break fields
		// accordingly
		if (source == mainWindow.shiftTable) {
			// Get values from table
			int selectedRow = mainWindow.shiftTable.getSelectedRow();
			String begin = (String) mainWindow.shiftTable.getValueAt(
					selectedRow, 1);
			String end = (String) mainWindow.shiftTable.getValueAt(selectedRow,
					2);
			String breakTime = (String) mainWindow.shiftTable.getValueAt(
					selectedRow, 3);

			// Set values to fields
			mainWindow.beginField.setText(begin);
			mainWindow.endField.setText(end);
			mainWindow.breakField.setText(breakTime);

			return;
		}

		// Register Shifts
		if (source == mainWindow.submitButton) {
			String date = mainWindow.dateField.getText();
			String partner = mainWindow.shiftPartnerField.getText();
			int begin = 0;
			int end = 0;
			int breakTime = 0;

			// If no date has been selected create popup
			if (date.equals("bitte auswählen")) {
				mainWindow.showMessagePopup("Bitte Datum auswählen!");
				return;
			}

			// Try to parse the begin time
			try {
				begin = Integer.parseInt(mainWindow.beginField.getText());
			} catch (NumberFormatException exception) {
				mainWindow.showMessagePopup("Ungültige Anfangszeit!");
				return;
			}
			try {
				end = Integer.parseInt(mainWindow.endField.getText());
			} catch (NumberFormatException exception) {
				mainWindow.showMessagePopup("Ungültige Endezeit!");
				return;
			}
			try {
				breakTime = Integer.parseInt(mainWindow.breakField.getText());
			} catch (NumberFormatException exception) {
				mainWindow.showMessagePopup("Ungültige Pausenzeit!");
				return;
			}
			
			if(partner.equals(""))
			{
				mainWindow.showMessagePopup("Bitte Schichtpartner angeben!");
				return;
			}

			mainWindow.shiftContainer.registerShift(null, date,
					begin,
					end,
					breakTime,
					partner,
					mainWindow.commentField.getText());
			return;
		}
		
		if(source == mainWindow.read_DPL)
		{
			DRManager.GetInstance().parseDutyRota();
			mainWindow.updateShiftsFromDPL();
		}
	}



}
