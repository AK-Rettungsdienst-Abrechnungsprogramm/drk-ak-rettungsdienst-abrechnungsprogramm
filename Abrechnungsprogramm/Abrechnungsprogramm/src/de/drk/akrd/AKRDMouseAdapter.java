package de.drk.akrd;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
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
	// The calendar popup
	private Popup calendarPopup;
	// The calendar widget
	private JCalendar calendar = new JCalendar();
	
	public AKRDMouseAdapter(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
		
		apply.addMouseListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {

		Object source = e.getSource();

		if (source == mainWindow.dateField) {
			PopupFactory factory = PopupFactory.getSharedInstance();

			JPanel panel = new JPanel();
			panel.add(calendar);
			panel.add(apply);

			calendarPopup = factory.getPopup(e.getComponent().getParent(), panel,
					mainWindow.getLocation().x, mainWindow.getLocation().y);
			calendarPopup.show();
		}
		
		if(source == apply)
		{
			Date selectedDate = calendar.getDate();
			
			StringBuilder dateString = new StringBuilder();
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(selectedDate);

			dateString.append(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
			dateString.append(".");
			int month = cal.get(Calendar.MONTH) + 1;
			if(month < 10) dateString.append("0");
			dateString.append(Integer.toString(month));
			dateString.append(".");
			dateString.append(Integer.toString(cal.get(Calendar.YEAR)));
			
			// Set the selected type of day in mainWindow
			int dayType = cal.get(Calendar.DAY_OF_WEEK);
			
			switch(dayType)
			{
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
		}
	}

}
