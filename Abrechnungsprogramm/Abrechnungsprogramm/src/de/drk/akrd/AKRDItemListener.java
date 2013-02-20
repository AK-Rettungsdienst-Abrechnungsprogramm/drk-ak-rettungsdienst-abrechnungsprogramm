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
			// Toggle the isEditable property of all bank info fields
			mainWindow.blz.setEditable(!mainWindow.blz.isEditable());
			mainWindow.accountNo.setEditable(!mainWindow.accountNo.isEditable());
			mainWindow.bankNameField.setEditable(!mainWindow.bankNameField.isEditable());
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
			mainWindow.prepTimeBox.setSelected(UtilityBox.hasPreparationTime(type));

            mainWindow.updateShiftContainer();
            
            // if ELW was selected, fill comment field
            if(type == ShiftContainer.ShiftType.ELW) {
            	mainWindow.commentField.setText("ELW Einsatz");
            }
            
			return;
			
		}
		
	}

}
