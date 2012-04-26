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
			// If this is the deselect event don't do anything
			if(e.getStateChange() == ItemEvent.DESELECTED) return;
			// If override flag was set don't do anything
			if(mainWindow.noShiftTypeUpdate)
			{
				mainWindow.noShiftTypeUpdate = false;
				return;
			}
			ShiftContainer.ShiftType type = (ShiftContainer.ShiftType)mainWindow.shiftTypeChooser.getSelectedItem();
			
			if(type == ShiftContainer.ShiftType.RTW || type == ShiftContainer.ShiftType.KTW || type == ShiftContainer.ShiftType.BREISACH || type == ShiftContainer.ShiftType.KIZA)
			{
				mainWindow.prepTimeBox.setSelected(true);
			}else
			{
				mainWindow.prepTimeBox.setSelected(false);
			}
            mainWindow.updateShiftContainer();
			return;
			
		}
	}

}
