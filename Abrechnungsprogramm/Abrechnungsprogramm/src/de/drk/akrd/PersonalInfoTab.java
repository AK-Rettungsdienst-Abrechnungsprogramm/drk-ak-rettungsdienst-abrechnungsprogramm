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
	
	private JLabel lblAdress = new JLabel("Adresse:");
	
	// TextFields
	private JTextField firstNameField = new JTextField();
	private JTextField lastNameField = new JTextField();
    private JTextField addressField = new JTextField();
	private JTextField blzField = new JTextField();
	private JTextField accountNoField = new JTextField();
	private JTextField bankNameField = new JTextField();
	
	// checkboxes
	private JCheckBox bankInfoKnown = new JCheckBox("Bankdaten bekannt");
	private JCheckBox addressKnown = new JCheckBox("bekannt");
	
	private JComboBox<Qualification>  trainingChooser = new JComboBox<Qualification>();
	
	private JButton submitChanges = new JButton("Änderungen übernehmen");
	
	// panel to keep bank info
	private JPanel bankForm = new JPanel();
	private JPanel nameAndTraining = new JPanel();

	public PersonalInfoTab() {
		bankForm.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
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
        
		
		addressKnown.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				addressKnownClickCallback(e.getStateChange());
			}
		});
        
        
		layoutUiElements();
		
		loadPersonalData();
        if(MainWindow.WACHENVERSION) {
          bankInfoKnown.doClick();
          addressKnown.doClick();
        }
	}
	
	protected void addressKnownClickCallback(int stateChange) {
		boolean newState = (stateChange == ItemEvent.DESELECTED);
		addressField.setEditable(newState);
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
        
        String address = addressField.getText();
        boolean isAddressKnown = addressKnown.isSelected();
        if (!isAddressKnown && address.length() == 0) {
        	UtilityBox.getInstance().displayErrorPopup("Fehler", "Bitte Adresse eingeben!");
        	return;
        }

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
		String account = "";
		String bankName = "";

		if(!dataKnown) {
			try
			{
				blz = Integer.parseInt(blzField.getText());
				account = accountNoField.getText();
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

		PersonalData pd = PersonalData.getInstance();

    // TODO: gmail+calId
    String gmail = "";
    String calID = "";
		boolean success = pd.setData(firstName, lastName, address, bankName, account, blz, quali, dataKnown, gmail, calID, addressKnown.isSelected());
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
		lblFirstName.setFont(font);
		
		nameAndTraining.add(lblSecondName);
		lblSecondName.setBounds(0, lineSpacing, SwingUtilities.computeStringWidth(fm, lblSecondName.getText()), labelHeight);
		lblSecondName.setFont(font);
		
		nameAndTraining.add(lblTraining);
		lblTraining.setBounds(0, lineSpacing * 2, SwingUtilities.computeStringWidth(fm, lblTraining.getText()), labelHeight);
		lblTraining.setFont(font);
		
		nameAndTraining.add(lblAdress);
		lblAdress.setBounds(0, lineSpacing * 3 + 10, SwingUtilities.computeStringWidth(fm, lblTraining.getText()), labelHeight);
		lblAdress.setFont(font);
		
		int nameFieldX = 100;
		int nameFieldWidth = 250;
		nameAndTraining.add(firstNameField);
		firstNameField.setBounds(nameFieldX, 0, nameFieldWidth, textFieldHeight);
		
		nameAndTraining.add(lastNameField);
		lastNameField.setBounds(nameFieldX, lineSpacing, nameFieldWidth, textFieldHeight);
		
		nameAndTraining.add(trainingChooser);
		trainingChooser.setBounds(nameFieldX, lineSpacing * 2, nameFieldWidth, comboBoxHeight);
		
		nameAndTraining.add(addressKnown);
		addressKnown.setBounds(nameFieldX, lineSpacing * 3 + 10, SwingUtilities.computeStringWidth(fm, addressKnown.getText()) + 50, labelHeight);
		
		nameAndTraining.add(addressField);
		addressField.setBounds(nameFieldX, lineSpacing * 4 + 10, nameFieldWidth, textFieldHeight);
		this.add(nameAndTraining);
		nameAndTraining.setBounds(20, 20, 350, 150);
		
		// Assemble Bank info panel
		
		bankForm.setLayout(null);
		int bankTextOffsetY = 30;
		int bankTextOffsetX = 20;
		bankForm.add(bankInfoKnown);
		bankInfoKnown.setBounds(bankTextOffsetX, bankTextOffsetY, SwingUtilities.computeStringWidth(fm, bankInfoKnown.getText()) + 50, labelHeight);
		
		bankForm.add(lblBLZ);
		lblBLZ.setBounds(bankTextOffsetX, bankTextOffsetY + lineSpacing, SwingUtilities.computeStringWidth(fm, lblBLZ.getText()), labelHeight);
		lblBLZ.setFont(font);
		
		bankForm.add(lblAccountNo);
		lblAccountNo.setBounds(bankTextOffsetX, bankTextOffsetY + lineSpacing *2, SwingUtilities.computeStringWidth(fm, lblAccountNo.getText()), labelHeight);
		lblAccountNo.setFont(font);
		
		bankForm.add(lblBankName);
		lblBankName.setBounds(bankTextOffsetX, bankTextOffsetY + lineSpacing * 3, SwingUtilities.computeStringWidth(fm, lblBankName.getText()), labelHeight);
		lblBankName.setFont(font);
		
		int bankTextFieldX = 200 + bankTextOffsetX;
		
		
		bankForm.add(blzField);
		blzField.setBounds(bankTextFieldX, bankTextOffsetY + lineSpacing, 150, textFieldHeight);
		
		bankForm.add(accountNoField);
		accountNoField.setBounds(bankTextFieldX, bankTextOffsetY + lineSpacing * 2, 150, textFieldHeight);
		
		bankForm.add(bankNameField);
		bankNameField.setBounds(bankTextFieldX, bankTextOffsetY + lineSpacing *3, 150, textFieldHeight);
		
		this.add(bankForm);
		bankForm.setBounds(400, 10, 400, 180);
		
		this.add(submitChanges);
		
		int buttonWidth = SwingUtilities.computeStringWidth(fm, submitChanges.getText()) + 40;
		submitChanges.setBounds(420 - buttonWidth/2, 260, buttonWidth, 30);
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
		      accountNoField.setText(pd.getAccountNumber());
		      bankInfoKnown.setSelected(pd.isDataKnown());
		      lastNameField.setText(pd.getLastName());
		      bankNameField.setText(pd.getBankNameAndCity());
		      addressField.setText(pd.getAddress());
		      addressKnown.setSelected(pd.addressKnown());
		    }
		  }

}
