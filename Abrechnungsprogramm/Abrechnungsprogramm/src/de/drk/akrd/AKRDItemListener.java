package de.drk.akrd;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JFrame;

import de.drk.akrd.ShiftContainer.ShiftType;
/**
 * 
 * An Item Listener that is aware of the MainWindow and handles all
 * Item events
 * 
 * @author niklas
 *
 */
public class AKRDItemListener implements ItemListener {

	private MainWindow mainWindow;
	
	public AKRDItemListener(MainWindow mainWindow)
	{
		this.mainWindow = mainWindow;
	}
	
	
	public void itemStateChanged(ItemEvent e) {

		Object source = e.getItemSelectable();
		
		// Deactivation of account info section
		if (source == mainWindow.bankInfoKnown) {
			mainWindow.blz.setEditable(!mainWindow.blz.isEditable());
			mainWindow.accountNo.setEditable(!mainWindow.accountNo.isEditable());
			return;
		}
		
		// Resets the shifts list, depending on the selected type
		if(source == mainWindow.shiftTypeChooser)
		{
            mainWindow.updateShiftContainer();
			return;
			
		}
	}

}
