package de.drk.akrd;

import javax.swing.JOptionPane;

public class UtilityBox {

	private static UtilityBox instance = null;

	private MainWindow mainWindow = null;

	public UtilityBox(MainWindow mainWindow) {
		instance = this;
		this.mainWindow = mainWindow;
	}

	public static void instanciate(MainWindow mainWindow) {
		if (instance != null)
			return;

		instance = new UtilityBox(mainWindow);
	}

	public static UtilityBox getInstance() {
		return instance;
	}

	/**
	 * Display an error message
	 * 
	 * @param title
	 *            Title of the message
	 * @param message
	 *            Message
	 */
	public void displayErrorPopup(String title, String message) {
		displayPopup(title, message, JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * 
	 * @param title
	 *            Title of the message
	 * @param message
	 *            Message text
	 * @param messageType
	 *            Message Type (e.g error)
	 */
	private void displayPopup(String title, String message, int messageType) {
		JOptionPane.showMessageDialog(mainWindow, message, title, messageType);
	}
	/**
	 * 
	 * @param time the time value as integer
	 * @return
	 */
	public static String createTimeStringFromInt(int time) {
		String timeString;
		if (time >= 1000) {
			timeString = ((int) (time / 100)) + "";
		} else {
			timeString = "0" + ((int) (time / 100));
		}
		timeString = timeString + (((time % 100) < 10) ? ":0" : ":")
				+ (time % 100);
		return timeString;
	}

}
