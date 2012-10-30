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

			String ID = (String) mainWindow.shiftTable.getValueAt(
					selectedRow, 0);
			
			Shift shift = Shift.getShiftFromId(ID);
			
			
			// Set values to fields
            ShiftType shiftType = shift.getType();
			mainWindow.beginField.setText(begin);
			mainWindow.endField.setText(end);
			mainWindow.breakField.setText(breakTime);
			mainWindow.noShiftTypeUpdate = true;
			mainWindow.shiftTypeChooser.setSelectedItem(shiftType);
            mainWindow.prepTimeBox.setSelected(UtilityBox.hasPreparationTime(shiftType));

			return;
		}

		// Register Shifts
		if (source == mainWindow.submitButton) {
			String date = mainWindow.dateField.getText();
			String partner = mainWindow.shiftPartnerField.getText();
			int begin = 0;
			int end = 0;
			int breakTime = 0;

			// check if personal data was set
			if(!PersonalData.getInstance().isDataSet()){
				UtilityBox.getInstance().displayErrorPopup("Fehler", "Bitte persönliche Daten eintragen und übernehmen!\nSonst kann dein Gehalt nicht berechnet werden ;-)");
				return;
			}
			
			// if no type was selected, display error
			if (mainWindow.shiftTypeChooser.getSelectedItem() == ShiftContainer.ShiftType.Alle)
			{
				UtilityBox.getInstance().displayErrorPopup("Fehler", "Bitte Schichttyp auswählen!");
				return;
			}
			
			// If no date has been selected create popup
			if (date.equals("bitte auswählen")) {
				UtilityBox.getInstance().displayErrorPopup("Fehler", "Bitte Datum auswählen!");
				return;
			}

			// Try to parse the begin time
			try {
				begin = Integer.parseInt(mainWindow.beginField.getText().replaceAll("[^\\d]",""));
			} catch (NumberFormatException exception) {
				UtilityBox.getInstance().displayErrorPopup("Fehler", "Ungültige Anfangszeit!");
				return;
			}
			try {
				end = Integer.parseInt(mainWindow.endField.getText().replaceAll("[^\\d]",""));
			} catch (NumberFormatException exception) {
				UtilityBox.getInstance().displayErrorPopup("Fehler", "Ungültige Endezeit!");
				return;
			}
			try {
				breakTime = Integer.parseInt(mainWindow.breakField.getText().replaceAll("[^\\d]",""));
			} catch (NumberFormatException exception) {
				UtilityBox.getInstance().displayErrorPopup("Fehler", "Ungültige Pausenzeit!");
				return;
			}
			
			if(partner.equals("") && mainWindow.shiftTypeChooser.getSelectedItem() != ShiftContainer.ShiftType.KVS
					&& mainWindow.shiftTypeChooser.getSelectedItem() != ShiftContainer.ShiftType.BABY)
			{
				UtilityBox.getInstance().displayErrorPopup("Fehler", "Bitte Schichtpartner angeben!");
				return;
			}

			mainWindow.shiftContainer.registerShift((ShiftContainer.ShiftType)mainWindow.shiftTypeChooser.getSelectedItem(), date,
					begin,
					end,
					breakTime,
					partner,
					mainWindow.commentField.getText(),
					mainWindow.prepTimeBox.isSelected());
			return;
		}
		
		// Delete registered Shift
		if(source == mainWindow.deleteRegisteredShiftButton)
		{
			// get the currently selected shift number (== rowNumber=)
			int selectedRow = mainWindow.registeredShiftsTable.getSelectedRow();
			
			if(selectedRow == -1) return;
			mainWindow.shiftContainer.deleteShift(selectedRow);
			
			return;
		}
		
		// Edit registered Shift
		if(source == mainWindow.editRegisteredShiftButton)
		{
			int selectedRow = mainWindow.registeredShiftsTable.getSelectedRow();
			
			ShiftInstance shift = mainWindow.shiftContainer.getShift(selectedRow);
			
			// reset the fields
			mainWindow.beginField.setText(UtilityBox.createTimeStringFromInt(shift.getActualStartingTime()));
			mainWindow.endField.setText(UtilityBox.createTimeStringFromInt(shift.getActualEndTime()));
			mainWindow.breakField.setText(UtilityBox.createTimeStringFromInt(shift.getActualBreakTime()));
			mainWindow.shiftPartnerField.setText(shift.getPartner());
			mainWindow.dateField.setText(shift.getDateString());
			mainWindow.commentField.setText(shift.getComment());
			mainWindow.shiftTypeChooser.setSelectedItem(shift.getType());
			mainWindow.prepTimeBox.setSelected(shift.PreparationTimeSet());
			
			// delete the shift
			mainWindow.shiftContainer.deleteShift(selectedRow);
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
            }
			return;
			
		}
		
		if(source == mainWindow.createSalaryStatementButton)
		{
          createAccountmentFrame = new CreateAccountmentFrame(mainWindow.shiftContainer.shiftInstances);
          
          //PdfCreator.createAccounting(mainWindow.shiftContainer.shiftInstances.toArray(new ShiftInstance[mainWindow.shiftContainer.shiftInstances.size()]));
		}
	}



}
