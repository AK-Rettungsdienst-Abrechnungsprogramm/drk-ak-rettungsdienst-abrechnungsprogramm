package de.drk.akrd;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Popup;
import javax.swing.PopupFactory;

import com.toedter.calendar.JCalendar;


/**
 * 
 * @author niklas
 * 
 * Mouse adapter that manages all mouse interaction with the main window
 */
public class AKRDMouseAdapter extends MouseAdapter {

	private MainWindow mainWindow;
	
	public AKRDMouseAdapter(MainWindow mainWindow)
	{
		this.mainWindow = mainWindow;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
        PopupFactory factory = PopupFactory.getSharedInstance();
        
        JPanel panel = new JPanel();
        panel.add(new JCalendar());
        panel.add(new JButton("Übernehmen"));
        
        
        Popup popup = factory.getPopup(e.getComponent().getParent(), panel, mainWindow.getLocation().x, mainWindow.getLocation().y);
        popup.show();
	}

}
