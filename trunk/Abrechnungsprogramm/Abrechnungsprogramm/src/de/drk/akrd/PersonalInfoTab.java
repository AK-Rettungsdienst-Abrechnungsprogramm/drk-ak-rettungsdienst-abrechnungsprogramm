package de.drk.akrd;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;

import de.drk.akrd.PersonalData.Qualification;

public class PersonalInfoTab extends JPanel {

	private static final long serialVersionUID = 1L;

	// Labels
	private JLabel lblFirstName = new JLabel("Vorname:");
	private JLabel lblSecondName = new JLabel("Nachname:");
	private JLabel lblTraining = new JLabel("Ausbildung:");
	
	private JLabel lblBLZ = new JLabel("BLZ:");
	private JLabel lblAccountNo = new JLabel("Kontonummer:");
	private JLabel lblBankName = new JLabel("Name der Bank:");
	
	private JLabel lblGoogleHeader = new JLabel("<html>Diese Angaben brauchst du nur, wenn du den Dienstplan automatisch in deinen Google Kalender<br>importieren willst.</html>");
	private JLabel lblMail = new JLabel("Google Mail Adresse:");
	private JLabel lblCalendar = new JLabel("Calendar ID:");
	
	// TextFields
	private JTextField firstNameField = new JTextField();
	private JTextField lastNameField = new JTextField();
	private JTextField blzField = new JTextField();
	private JTextField accountNoField = new JTextField();
	private JTextField bankNameField = new JTextField();
	
	private JTextField mailField = new JTextField();
	private JTextField calIdField = new JTextField();
	
	// checkboxes
	private JCheckBox bankInfoKnown = new JCheckBox("Bankdaten bekannt");
	
	private JComboBox<Qualification>  trainingChooser = new JComboBox<Qualification>();
	
	private JButton submitChanges = new JButton("Änderungen übernehmen");
	
	// panel to keep bank info
	private JPanel bankForm = new JPanel();
	private JPanel nameAndTraining = new JPanel();
	private JPanel googleCal = new JPanel();
	
	public PersonalInfoTab() {
		bankForm.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		googleCal.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		trainingChooser.setModel(new DefaultComboBoxModel<Qualification>(Qualification.values()));
		
		submitChanges.addMouseListener(new MouseAdapter() {
	    	public void mouseClicked(MouseEvent e) {
				 submitChangesCallback();
	    	}
		});
		
		bankInfoKnown.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				bankInfoKnownClickCallback(e.getStateChange());
			}
		});
		layoutUiElements();
		
		loadPersonalData();
	}
	
	protected void bankInfoKnownClickCallback(int stateChange) {
		boolean newState = (stateChange == ItemEvent.DESELECTED);
		blzField.setEditable(newState);
		bankNameField.setEditable(newState);
		accountNoField.setEditable(newState);
	}

	private void submitChangesCallback() {
		String firstName = firstNameField.getText();
		String lastName = lastNameField.getText();

		PersonalData.Qualification quali = (PersonalData.Qualification)trainingChooser.getSelectedItem();

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

		boolean dataKnown = bankInfoKnown.isSelected();

		int blz = 0;
		int account = 0;
		String bankName = "";

		if(!dataKnown) {
			try
			{
				blz = Integer.parseInt(blzField.getText());
				account = Integer.parseInt(accountNoField.getText());
			}
			catch(NumberFormatException exeption)
			{
				UtilityBox.getInstance().displayErrorPopup("Fehler", "Bitte Bankdaten korrekt eingeben!");
				return;
			}

			bankName = bankNameField.getText();

			if(bankName.length() == 0)
			{
				UtilityBox.getInstance().displayErrorPopup("Fehler", "Bitte Bankdaten korrekt eingeben!");
				return;
			}
		}

		String gmail = mailField.getText();
		String calID = calIdField.getText();

		PersonalData pd = PersonalData.getInstance();

		boolean success = pd.setData(firstName, lastName, bankName, account, blz, quali, dataKnown, gmail, calID);
		if (success) {
			UtilityBox.getInstance().displayInfoPopup("Persönliche Daten", "Daten gespeichert.");
		}
		return;
	}

	private void layoutUiElements() {
		this.setLayout(null);
		
		int lineSpacing = 30;
		
		Font font = UtilityBox.getInstance().getDefaultFont();
		FontMetrics fm = this.getFontMetrics(font);
		// height for all the labels
		int labelHeight = fm.getHeight();
		// height for all text fields
		int textFieldHeight = fm.getHeight() + 5;
		int comboBoxHeight = 25;
		
		// Assemble Name and training panel
		nameAndTraining.setLayout(null);
		nameAndTraining.add(lblFirstName);
		lblFirstName.setBounds(0, 0, SwingUtilities.computeStringWidth(fm, lblFirstName.getText()), labelHeight);
		
		nameAndTraining.add(lblSecondName);
		lblSecondName.setBounds(0, lineSpacing, SwingUtilities.computeStringWidth(fm, lblSecondName.getText()), labelHeight);
		
		nameAndTraining.add(lblTraining);
		lblTraining.setBounds(0, lineSpacing * 2, SwingUtilities.computeStringWidth(fm, lblTraining.getText()), labelHeight);
		
		int nameFieldX = 100;
		int nameFieldWidth = 250;
		nameAndTraining.add(firstNameField);
		firstNameField.setBounds(nameFieldX, 0, nameFieldWidth, textFieldHeight);
		
		nameAndTraining.add(lastNameField);
		lastNameField.setBounds(nameFieldX, lineSpacing, nameFieldWidth, textFieldHeight);
		
		nameAndTraining.add(trainingChooser);
		trainingChooser.setBounds(nameFieldX, lineSpacing * 2, nameFieldWidth, comboBoxHeight);
		
		this.add(nameAndTraining);
		nameAndTraining.setBounds(20, 50, 350, 150);
		
		// Assemble Bank info panel
		
		bankForm.setLayout(null);
		int bankTextOffsetY = 30;
		int bankTextOffsetX = 20;
		bankForm.add(bankInfoKnown);
		bankInfoKnown.setBounds(bankTextOffsetX, bankTextOffsetY, SwingUtilities.computeStringWidth(fm, bankInfoKnown.getText()) + 50, labelHeight);
		
		bankForm.add(lblBLZ);
		lblBLZ.setBounds(bankTextOffsetX, bankTextOffsetY + lineSpacing, SwingUtilities.computeStringWidth(fm, lblBLZ.getText()), labelHeight);
		
		bankForm.add(lblAccountNo);
		lblAccountNo.setBounds(bankTextOffsetX, bankTextOffsetY + lineSpacing *2, SwingUtilities.computeStringWidth(fm, lblAccountNo.getText()), labelHeight);
		
		bankForm.add(lblBankName);
		lblBankName.setBounds(bankTextOffsetX, bankTextOffsetY + lineSpacing * 3, SwingUtilities.computeStringWidth(fm, lblBankName.getText()), labelHeight);
		
		int bankTextFieldX = 200 + bankTextOffsetX;
		
		
		bankForm.add(blzField);
		blzField.setBounds(bankTextFieldX, bankTextOffsetY + lineSpacing, 150, textFieldHeight);
		
		bankForm.add(accountNoField);
		accountNoField.setBounds(bankTextFieldX, bankTextOffsetY + lineSpacing * 2, 150, textFieldHeight);
		
		bankForm.add(bankNameField);
		bankNameField.setBounds(bankTextFieldX, bankTextOffsetY + lineSpacing *3, 150, textFieldHeight);
		
		this.add(bankForm);
		bankForm.setBounds(400, 10, 400, 180);
		
		// Assemble google calendar panel
		
		googleCal.setLayout(null);
		googleCal.add(lblGoogleHeader);
		lblGoogleHeader.setBounds(30, 0, 800, 50);
		
		int googleY = 70;
		
		googleCal.add(lblMail);
		lblMail.setBounds(30, googleY, SwingUtilities.computeStringWidth(fm, lblMail.getText()), labelHeight);
		
		googleCal.add(mailField);
		mailField.setBounds(200, googleY, 200, textFieldHeight);
		
		googleCal.add(lblCalendar);
		lblCalendar.setBounds(450, googleY, SwingUtilities.computeStringWidth(fm, lblCalendar.getText()), labelHeight);
		
		googleCal.add(calIdField);
		calIdField.setBounds(550, googleY, 200, textFieldHeight);
		
		this.add(googleCal);
		googleCal.setBounds(10, 200, 850, 150);
		
		this.add(submitChanges);
		
		int buttonWidth = SwingUtilities.computeStringWidth(fm, submitChanges.getText()) + 40;
		submitChanges.setBounds(420 - buttonWidth/2, 360, buttonWidth, 30);
	}
	
	  public void loadPersonalData() {
		    // Try to load personal data and fill the fields
		    PersonalData pd = null;
		    try {
		      pd = PersonalData.getInstance();
		    } catch (Exception e) {
		      UtilityBox.getInstance().displayErrorPopup("Fehler",
		              "Fehler beim Laden der persönlichen Daten");
		    }
		    if (pd != null) {
		      firstNameField.setText(pd.getFirstName());
		      trainingChooser.setSelectedItem(pd.getQualification());
		      blzField.setText(Integer.toString(pd.getBlz()));
		      accountNoField.setText(Integer.toString(pd.getAccountNumber()));
		      bankInfoKnown.setSelected(pd.isDataKnown());
		      lastNameField.setText(pd.getLastName());
		      mailField.setText(pd.getEmailAdress());
		      calIdField.setText(pd.getCalendarId());
		      bankNameField.setText(pd.getBankNameAndCity());
		    }
		  }

}
