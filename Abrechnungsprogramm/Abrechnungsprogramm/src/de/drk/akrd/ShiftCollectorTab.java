package de.drk.akrd;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.HeadlessException;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JCalendar;

import de.drk.akrd.ShiftContainer.ShiftType;

public class ShiftCollectorTab extends JPanel {

	private static final long serialVersionUID = 1L;
	
	// the weekday currently selected
	int currentlySelectedDay = 1;
	
	// UI Elements
	
	// The calendar widget
	private JCalendar calendar = new JCalendar();
	private Popup calendarPopup;
	
	// Scroll pane containing the known shifts
	JScrollPane shiftCataloguePane = new JScrollPane();
	JTable shiftCatalogueTable = new JTable();
	protected DefaultTableModel shiftCatalogueTableModel = new DefaultTableModel(
	          new Object[][]{}, new String[]{"Schichtkürzel", "Beginn",
	            "Ende", "Pause"}) {

	    private static final long serialVersionUID = 1L;

	    @Override
	    public boolean isCellEditable(int row, int column) {
	      // all cells false
	      return false;
	    }
	  };
	// Scroll pane and table containing the registered shifts
	JScrollPane shiftInstancePane = new JScrollPane();
	JTable shiftInstanceTable = new JTable();
	DefaultTableModel shiftInstanceTableModel = new DefaultTableModel(
		          new Object[][]{}, new String[]{"Datum", "Beginn", "Ende",
		                  "Pause", "Dezimal", "Schichtpartner", "Kommentar","Fahrtkosten",
		                  "Verdienst"}) {

		          private static final long serialVersionUID = 1L;

		          @Override
		          public boolean isCellEditable(int row, int column) {
		            // all cells false
		            return false;
		          }
		        };
	
	
	// Labels
	JLabel lblBegin = new JLabel("Beginn:");
	JLabel lblEnd = new JLabel("Ende:");
	JLabel lblBreak = new JLabel("Pause:");
	JLabel lblShiftType = new JLabel("Schichtart:");
	JLabel lblDate = new JLabel("Datum:");
	JLabel lblPartner = new JLabel("Schichtpartner:");
	JLabel lblComment = new JLabel("Kommentar:");
	JLabel lblMonthSelection = new JLabel("Anzeigen:");
	boolean initialized = false;

	// Text fields
	JTextField shiftPartnerField = new JTextField();
	JTextField dateField = new JTextField();
	JTextField beginField = new JTextField();
	JTextField endField = new JTextField();
	JTextField breakField = new JTextField();
	JTextField commentField = new JTextField();
	
	// Buttons
	JButton submitButton = new JButton();
	JButton createSalaryStatementButton = new JButton();
    JButton deleteRegisteredShiftButton = new JButton();
    JButton editRegisteredShiftButton = new JButton();
    
    JCheckBox prepTimeBox = new JCheckBox();
    
    JComboBox<ShiftType> shiftTypeChooser = new JComboBox<ShiftType>();
    
    // ComboBoxes for the selection of shifts to display
    JComboBox<String> yearChooser = new JComboBox<String>();
    JComboBox<String> monthChooser = new JComboBox<String>();

    public ShiftCollectorTab(MouseAdapter mouseAdapter, ItemListener itemListener, ShiftContainer shiftContainer) throws HeadlessException {
    	super();
    	
    	// set UI elements up
	    dateField.setText("bitte auswählen");
	    dateField.addMouseListener(new MouseAdapter() {
	    	public void mouseClicked(MouseEvent e) {
	    		datePickerCallback();
	    	}
	    	});
	    dateField.setEditable(false);
	    dateField.setColumns(10);

	    shiftInstanceTable.getTableHeader().setReorderingAllowed(false);
	    shiftInstanceTable.getTableHeader().setResizingAllowed(false);

	    shiftPartnerField.setColumns(10);

	    DefaultComboBoxModel<ShiftContainer.ShiftType> enumModel = new DefaultComboBoxModel<ShiftType>(
	            ShiftType.values());
	    shiftTypeChooser.setModel(enumModel);

	    beginField.setColumns(10);

	    endField.setColumns(10);

	    breakField.setColumns(10);

	    submitButton.setText("Eintragen");
	    submitButton.addMouseListener(new MouseAdapter(){
	    	public void mouseClicked(MouseEvent e) {
	    		registerShiftButtonCallback();
	    	}
	    });
	    
	    createSalaryStatementButton.setText("Abrechnung erstellen");
	    createSalaryStatementButton.addMouseListener(new MouseAdapter() {
	    	public void mouseClicked(MouseEvent e){
	    		createSalaryStatementCallback();
	    	}
	    });

	    prepTimeBox.setText("10 min Rüstzeit");
	    
	    deleteRegisteredShiftButton.setText("Löschen");
	    editRegisteredShiftButton.setText("Bearbeiten");
	    deleteRegisteredShiftButton.addMouseListener(new MouseAdapter(){
	    	public void mouseClicked(MouseEvent e){
	    		deleteShiftButtonCallback();
	    	}
	    });
	    editRegisteredShiftButton.addMouseListener(new MouseAdapter(){
	    	public void mouseClicked(MouseEvent e) {
	    		editShiftButtonCallback();
	    	}
	    });
	    
	    
	    shiftTypeChooser.addItemListener(itemListener);
	    shiftTypeChooser.addItemListener(new ItemListener() {
	    	public void itemStateChanged(ItemEvent e) {
	    		// If this is the deselect event don't do anything
	    		if(e.getStateChange() == ItemEvent.DESELECTED) return;
	    		// If override flag was set don't do anything
	    		//	    			if(mainWindow.noShiftTypeUpdate)
	    		//	    			{
	    		//	    				mainWindow.noShiftTypeUpdate = false;
	    		//	    				return;
	    		//	    			}
	    		ShiftContainer.ShiftType type = (ShiftContainer.ShiftType)shiftTypeChooser.getSelectedItem();
	    		prepTimeBox.setSelected(UtilityBox.hasPreparationTime(type));

	    		updateShiftContainer();

	    		// if ELW was selected, fill comment field
	    		if(type == ShiftContainer.ShiftType.ELW) {
	    			commentField.setText("ELW Einsatz");
	    		}

	    		return;

	    	}
	    });

	    shiftCatalogueTable.getTableHeader().setReorderingAllowed(false);
	    shiftCatalogueTable.getTableHeader().setResizingAllowed(false);
	    shiftCatalogueTable.addMouseListener(new MouseAdapter() {
	    	public void mouseClicked(MouseEvent e) {
				 shiftCatalogueClickCallback();
	    	}
	    });	    
	    shiftCatalogueTable.setShowGrid(false);
	    Object[][] data = ShiftContainer.shiftToTableData(shiftContainer.getShifts());
	    for (int i = 0; i < data.length; i++) {
	      shiftCatalogueTableModel.addRow(data[i]);
	    }
	    shiftCatalogueTable.setModel(shiftCatalogueTableModel);
	    shiftInstanceTable.setModel(shiftInstanceTableModel);
	    shiftCataloguePane.setViewportView(shiftCatalogueTable);
	    shiftInstancePane.setViewportView(shiftInstanceTable);
	    
	    // set the time range selectors  
	    setYearComboBox();
	    setMonthComboBox();
	    yearChooser.addItemListener(new ItemListener() {
	    	public void itemStateChanged(ItemEvent e) {
	    		displaySelectionStateChangeCallback(e);
	    	}
	    });
	    monthChooser.addItemListener(new ItemListener() {
	    	public void itemStateChanged(ItemEvent e) {
	    		displaySelectionStateChangeCallback(e);
	    	}
	    });
	    
	    // Place UI Elements in Panel
	    layoutUiElements();
	    
	    updateRegisteredShifts();
	    initialized = true;
	}

    private void displaySelectionStateChangeCallback(ItemEvent e) {
		// do nothing if this is the DESELECT event
		if (e.getStateChange() == ItemEvent.DESELECTED || ! initialized)  return;
		updateRegisteredShifts(true);
	}

	private void setYearComboBox() {
	    ArrayList<Integer> yearsToDisplay = UtilityBox.getInstance().getShiftContainer().getSortedYearList();
	    Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
	    // if the current year is not in the display list add it
	    if (!yearsToDisplay.contains(currentYear))
	    	yearsToDisplay.add(currentYear);
	    DefaultComboBoxModel<String> yearModel = new DefaultComboBoxModel<String>();
	    yearModel.addElement("Alle");
	    for(Integer i : yearsToDisplay) 
	    	yearModel.addElement(i.toString());
	    yearChooser.setModel(yearModel);		
	    yearChooser.setSelectedItem(currentYear.toString());
	}
    
    private void setMonthComboBox() {
    	String[] months = new String[] {"Alle", "Januar", "Februar", "März", "April", "Mai", "Juni", "Juli",
    									"August", "September", "Oktober", "November", "Dezember"};
    	DefaultComboBoxModel<String> monthModel = new DefaultComboBoxModel<String>(months);
    	monthChooser.setModel(monthModel);
    	// set the current month as selected
    	monthChooser.setSelectedIndex(Calendar.getInstance().get(Calendar.MONTH) + 1);
    }

	private void createSalaryStatementCallback() {
		String y = (String) yearChooser.getSelectedItem();
		int year = (y == "Alle")? -1 : Integer.parseInt(y);
		int month = monthChooser.getSelectedIndex() - 1;
		
        new CreateAccountmentFrame(year, month);
		
	}

	private void deleteShiftButtonCallback() {
		// get the currently selected shift number (== rowNumber)
		int selectedRow = shiftInstanceTable.getSelectedRow();
		
		if(selectedRow == -1) return;
		UtilityBox.getInstance().getShiftContainer().deleteShift(selectedRow);
		updateRegisteredShifts();
	}

	private void editShiftButtonCallback() {
    	int selectedRow = shiftInstanceTable.getSelectedRow();
		
		ShiftInstance shift = UtilityBox.getInstance().getShiftContainer().getShift(selectedRow);
		
		// reset the fields
		beginField.setText(UtilityBox.createTimeStringFromInt(shift.getActualStartingTime()));
		endField.setText(UtilityBox.createTimeStringFromInt(shift.getActualEndTime()));
		breakField.setText(UtilityBox.createTimeStringFromInt(shift.getActualBreakTime()));
		shiftPartnerField.setText(shift.getPartner());
		dateField.setText(shift.getDateString());
		commentField.setText(shift.getComment());
		shiftTypeChooser.setSelectedItem(shift.getType());
		prepTimeBox.setSelected(shift.PreparationTimeSet());
		
		// delete the shift
		UtilityBox.getInstance().getShiftContainer().deleteShift(selectedRow);
		updateRegisteredShifts();
	}

	// This method handles selection of a shift from the catalogue by click 
    // on the list
    private void shiftCatalogueClickCallback() {
    	// Get values from table
		int selectedRow = shiftCatalogueTable.getSelectedRow();
		String begin = (String) shiftCatalogueTable.getValueAt(
				selectedRow, 1);
		String end = (String) shiftCatalogueTable.getValueAt(selectedRow,
				2);
		String breakTime = (String) shiftCatalogueTable.getValueAt(
				selectedRow, 3);

		String ID = (String) shiftCatalogueTable.getValueAt(
				selectedRow, 0);
		
		Shift shift = Shift.getShiftFromId(ID);
		
		
		// Set values to fields
        ShiftType shiftType = shift.getType();
		beginField.setText(begin);
		endField.setText(end);
		breakField.setText(breakTime);
		// mainWindow.noShiftTypeUpdate = true;
		shiftTypeChooser.setSelectedItem(shiftType);
        prepTimeBox.setSelected(UtilityBox.hasPreparationTime(shiftType));
	}

	// This method handles display and function of the date picker
	private void datePickerCallback() {
		PopupFactory factory = PopupFactory.getSharedInstance();

		JPanel panel = new JPanel();
		panel.add(calendar);
		JButton apply = new JButton("Übernehmen");
		apply.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
			Date selectedDate = calendar.getDate();

			StringBuilder dateString = new StringBuilder();

			Calendar cal = Calendar.getInstance();
			cal.setTime(selectedDate);

			// pad day string with 0 if < 10
			int day = cal.get(Calendar.DAY_OF_MONTH);
			String daystring;
			if (day < 10) {
				daystring = "0" + Integer.toString(day);
			} else {
				daystring = Integer.toString(day);
			}
			
			dateString.append(daystring);
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
				currentlySelectedDay = 2;
				break;
			case 7: // saturday
				currentlySelectedDay = 1;
				break;
			default:
				currentlySelectedDay = 0;
				break;
			}

			dateField.setText(dateString.toString());
			calendarPopup.hide();
			updateShiftContainer();
			return;
		}
		});
		panel.add(apply);

		calendarPopup = factory.getPopup(this,
				panel, UtilityBox.getInstance().getWindowPosX(),
				UtilityBox.getInstance().getWindowPosY());
		calendarPopup.show();
		return;
	}

	private void layoutUiElements() {
		// base positions of the input form
		int formX = 20;
		int formY = 10;
		
		// place between lines in the form
		int lineSpacing = 30;
		
		Font font = UtilityBox.getInstance().getDefaultFont();
		FontMetrics fm = this.getFontMetrics(font);
		// height for all the labels
		int labelHeight = fm.getHeight();
		// height for all buttons
		int buttonHeight = fm.getHeight() + 10;
		// height for all text fields
		int textFieldHeight = fm.getHeight() + 5;
		int timeFieldWidth = 80;
		int comboBoxHeight = 20;
		
		this.setLayout(null);
		
		// FORM LAYOUT
		
		// Line 1
		
		this.add(lblShiftType);
		lblShiftType.setBounds(formX, formY, SwingUtilities.computeStringWidth(fm, lblShiftType.getText()), labelHeight);
		lblShiftType.setFont(font);
		
		this.add(shiftTypeChooser);
		shiftTypeChooser.setBounds(90 + formX, formY, 150, comboBoxHeight);
		
		// Line 2
		
		this.add(lblDate);
		lblDate.setBounds(formX, formY + lineSpacing, SwingUtilities.computeStringWidth(fm, lblDate.getText()), labelHeight);
		lblDate.setFont(font);
		
		this.add(dateField);
		dateField.setBounds(90 + formX, formY + lineSpacing, 120, 20);
		
		// Line 3
		
		this.add(lblPartner);
		lblPartner.setBounds(formX, formY + lineSpacing * 2, SwingUtilities.computeStringWidth(fm, lblPartner.getText()), labelHeight);
		
		this.add(shiftPartnerField);
		shiftPartnerField.setBounds(SwingUtilities.computeStringWidth(fm, lblPartner.getText()) + 10 + formX, formY  + lineSpacing * 2,
				100, textFieldHeight);
		
		// Line 4
		
		this.add(lblBegin);
		lblBegin.setBounds(formX, formY +lineSpacing * 3, SwingUtilities.computeStringWidth(fm, lblBegin.getText()), labelHeight);
		lblBegin.setFont(font);
		
		this.add(beginField);
		beginField.setBounds(70 + formX, formY + lineSpacing * 3, timeFieldWidth, textFieldHeight);
		
		this.add(lblEnd);
		lblEnd.setBounds(190 + formX, formY +lineSpacing * 3, SwingUtilities.computeStringWidth(fm, lblEnd.getText()), labelHeight);
		lblEnd.setFont(font);
	
		this.add(endField);
		endField.setBounds(240 + formX, formY + lineSpacing * 3, timeFieldWidth, textFieldHeight);
		
		// Line 5
		
		this.add(lblBreak);
		lblBreak.setBounds(formX, formY + lineSpacing * 4, SwingUtilities.computeStringWidth(fm, lblBreak.getText()), labelHeight);
		lblBreak.setFont(font);
		
		this.add(breakField);
		breakField.setBounds(formX + 70, formY + lineSpacing * 4, timeFieldWidth, textFieldHeight);
		
		this.add(prepTimeBox);
		prepTimeBox.setBounds(formX + 170, formY + lineSpacing * 4, SwingUtilities.computeStringWidth(fm, prepTimeBox.getText()) + 50, labelHeight);
		
		// Line 6
		
		this.add(lblComment);
		lblComment.setBounds(formX, formY + lineSpacing * 5, SwingUtilities.computeStringWidth(fm, lblComment.getText()), labelHeight);
		lblComment.setFont(font);
		
		this.add(commentField);
		commentField.setBounds(formX + 100, formY + lineSpacing * 5, 200, textFieldHeight);
		
		// Line 7
		
		this.add(submitButton);
		submitButton.setBounds(formX + 100, formY + lineSpacing * 6, SwingUtilities.computeStringWidth(fm, submitButton.getText()) + 40, buttonHeight);
		
		// shifts to display selection
		this.add(lblMonthSelection);
		lblMonthSelection.setBounds(10, 250, SwingUtilities.computeStringWidth(fm, lblMonthSelection.getText()), labelHeight);
		lblMonthSelection.setFont(font);
		
		this.add(monthChooser);
		monthChooser.setBounds(100, 250, 100, comboBoxHeight);
		
		this.add(yearChooser);
		yearChooser.setBounds(220, 250, 80, comboBoxHeight);
		
		
		// Shift Catalogue
		this.add(shiftCataloguePane);
		shiftCataloguePane.setBounds(400, 10, 480, 250);
		
		// Shift Instances
		this.add(shiftInstancePane);
		shiftInstancePane.setBounds(10, 280, 870, 300);
		
		// lower buttons
		int lowerButtonY = 590;
		
		this.add(createSalaryStatementButton);
		createSalaryStatementButton.setBounds(150, lowerButtonY, SwingUtilities.computeStringWidth(fm, createSalaryStatementButton.getText()) + 40, buttonHeight);
		
		this.add(editRegisteredShiftButton);
		editRegisteredShiftButton.setBounds(500, lowerButtonY, SwingUtilities.computeStringWidth(fm, editRegisteredShiftButton.getText()) + 40, buttonHeight);
		
		this.add(deleteRegisteredShiftButton);
		deleteRegisteredShiftButton.setBounds(650, lowerButtonY, SwingUtilities.computeStringWidth(fm, deleteRegisteredShiftButton.getText()) + 40, buttonHeight);
	}
	
	  /**
	   * Gets the current day and shift type (for filter options) and updated the
	   * Shift container
	   * 
	   * @author niklas
	   */
	  public void updateShiftContainer() {
	    // Get currently selected shift type
	    ShiftType type = (ShiftType) shiftTypeChooser.getSelectedItem();

	    ShiftContainer shiftContainer = UtilityBox.getInstance().getShiftContainer();
	    
	    Object[][] data = ShiftContainer.shiftToTableData(shiftContainer.filterShifts(type, currentlySelectedDay));
	    shiftCatalogueTableModel.setNumRows(0);
	    for (int i = 0; i < data.length; i++) {
	      shiftCatalogueTableModel.addRow(data[i]);
	    }

	  }

	  // Handle clicks on register shift button
	  private void registerShiftButtonCallback(){
		  String date = dateField.getText();
		  String partner = shiftPartnerField.getText();
		  int begin = 0;
		  int end = 0;
		  int breakTime = 0;

		  // check if personal data was set
		  if(!PersonalData.getInstance().isDataSet()){
			  UtilityBox.getInstance().displayErrorPopup("Fehler", "Bitte persönliche Daten eintragen und übernehmen!\nSonst kann dein Gehalt nicht berechnet werden ;-)");
			  return;
		  }

		  // if no type was selected, display error
		  if (shiftTypeChooser.getSelectedItem() == ShiftContainer.ShiftType.Alle)
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
			  begin = Integer.parseInt(beginField.getText().replaceAll("[^\\d]",""));
		  } catch (NumberFormatException exception) {
			  UtilityBox.getInstance().displayErrorPopup("Fehler", "Ungültige Anfangszeit!");
			  return;
		  }
		  try {
			  end = Integer.parseInt(endField.getText().replaceAll("[^\\d]",""));
		  } catch (NumberFormatException exception) {
			  UtilityBox.getInstance().displayErrorPopup("Fehler", "Ungültige Endezeit!");
			  return;
		  }
		  try {
			  // If the break time field is empty, assume no break
			  if (breakField.getText().length() == 0) {
				  breakTime = 0;
			  } else {
				  breakTime = Integer.parseInt(breakField.getText().replaceAll("[^\\d]",""));
			  }
		  } catch (NumberFormatException exception) {
			  UtilityBox.getInstance().displayErrorPopup("Fehler", "Ungültige Pausenzeit!");
			  return;
		  }

		  if(partner.equals("") && shiftTypeChooser.getSelectedItem() != ShiftContainer.ShiftType.KVS
				  && shiftTypeChooser.getSelectedItem() != ShiftContainer.ShiftType.BABY
				  && shiftTypeChooser.getSelectedItem() != ShiftContainer.ShiftType.ELW)
		  {
			  UtilityBox.getInstance().displayErrorPopup("Fehler", "Bitte Schichtpartner angeben!");
			  return;
		  }

		  UtilityBox.getInstance().getShiftContainer().registerShift(
				  (ShiftContainer.ShiftType)shiftTypeChooser.getSelectedItem(), date,
				  begin,
				  end,
				  breakTime,
				  partner,
				  commentField.getText(),
				  prepTimeBox.isSelected());
		  
		  // set the year and month combo boxes to display the month the shift just registered was in
		  DateFormat f = new SimpleDateFormat("dd.MM.yyyy");
		  Calendar cal = Calendar.getInstance();
		  try {
			  cal.setTime(f.parse(date));
		  } catch (ParseException e) {
			  // TODO Auto-generated catch block
			  e.printStackTrace();
		  }

		  updateRegisteredShifts();
		  
		  yearChooser.setSelectedItem(Integer.toString(cal.get(Calendar.YEAR)));
		  monthChooser.setSelectedIndex(cal.get(Calendar.MONTH) + 1);
		  
		  // Finally clear the fields
		  commentField.setText("");
		  beginField.setText("");
		  endField.setText("");
		  shiftPartnerField.setText("");
	  }
	  
	  /**
	   * Refreshes the table displaying the registered shifts
	   */
	  public void updateRegisteredShifts() {
		  updateRegisteredShifts(false);
	  }
	  public void updateRegisteredShifts(boolean noYearMonthBoxUpdate) {

	    float completeSalary = 0;
	    
	    // get the values from the time to display selectors
	    int month = monthChooser.getSelectedIndex();
	    String year = (String) yearChooser.getSelectedItem();

	    ShiftContainer sc = UtilityBox.getInstance().getShiftContainer();
	    
	    // get the shifts to display
	    // if the selected month is 0 this means all months are to be displayed, so it needs 
	    // to be -1 for the function to understand it
	    month--;
	    if (year == "Alle") year = "-1";
	    ArrayList<ShiftInstance> temp = sc.getShiftInsances(Integer.parseInt(year), month);
	    
	    ShiftInstance[] instancesToDisplay = (ShiftInstance[]) temp.toArray(new ShiftInstance[temp.size()]);
	    
	    // get all registered shifts and convert them to table data
	    Object[][] data = ShiftContainer.shiftInstancesToTableData(instancesToDisplay);
	    // reset the table model
	    shiftInstanceTableModel.setNumRows(0);
	    // iterate over all shifts
	    for (int i = 0; i < data.length; i++) {
	      ArrayList<Object> list = new ArrayList<Object>();
	      Collections.addAll(list, data[i]);
	      // get the original shift item
	      ShiftInstance currentShift = sc.shiftInstances.get(i);
	      // calculate the salary for this shift and add to complete salary
	      float salary = UtilityBox.getInstance().calculateSalaryPerHour(currentShift) * currentShift.getTimeAsFloat();
	      completeSalary += salary;
	      // add the shifts salary to list entry
	      list.add(String.format("%.2f", salary) + "€");
	      // add this shift to table model
	      shiftInstanceTableModel.addRow(list.toArray());
	    }
	    // create last line which displays the overall salary
	    Object[] lastLine = new Object[]{"", "", "", "", "", "", "", "Gesamt",
	      String.format("%.2f", completeSalary) + "€"};
	    shiftInstanceTableModel.addRow(lastLine);
	    
	    if (! noYearMonthBoxUpdate) setYearComboBox();
	  }

}