package de.drk.akrd;

import java.text.ParseException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;

/**
 *
 * @author Jo
 */
public class CreateAccountmentFrame {

  private JFrame createAccountmentFrame = new JFrame("Abrechnung erstellen");
  private ArrayList<ShiftInstance> selectedShifts = null;
  private JComboBox<String> yearComboBox = new JComboBox<String>();
  private JComboBox<String> monthComboBox = new JComboBox<String>();;
  private JCheckBox printCheckbox;
  private JScrollPane displayPane;
  private JTable displayTable;
  private DefaultTableModel displayTableModel = new DefaultTableModel(
          new Object[][]{}, new String[]{"Tag, Datum", "von - bis",
            "Schichttyp", "Schichtpartner"}) {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean isCellEditable(int row, int column) {
      // all cells false
      return false;
    }
  };
  private Calendar calendar = Calendar.getInstance();
  private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
  private int selectedYear = 0;
  private int selectedMonth = 0;

  public CreateAccountmentFrame(int year, int month) {
	// position relative to main window
	int x = UtilityBox.getInstance().getWindowPosX();
	int y = UtilityBox.getInstance().getWindowPosY();
	
	selectedYear = year;
	selectedMonth = month;

    createAccountmentFrame.setBounds(x + 20, y + 20, 600, 400);
    createAccountmentFrame.getContentPane().setLayout(null);
    createAccountmentFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    calendar.setTimeInMillis(System.currentTimeMillis());
    JButton btnAbbrechen = new JButton("Abbrechen");
    btnAbbrechen.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        createAccountmentFrame.dispose();
      }
    });
    btnAbbrechen.setBounds(460, 320, 120, 30);
    createAccountmentFrame.getContentPane().add(btnAbbrechen);

    JButton btnAusgeben = new JButton("Ausgeben");
    btnAusgeben.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent arg0) {
        String filePath = PdfCreator.createAccounting(selectedShifts.toArray(new ShiftInstance[selectedShifts.size()]), selectedMonth, selectedYear);
        if (printCheckbox.isSelected() && (filePath != null)) {
          UtilityBox.getInstance().printFile(filePath);
        }
      }
    });
    btnAusgeben.setBounds(330, 320, 120, 30);
    createAccountmentFrame.getContentPane().add(btnAusgeben);

    calendar.setTime(new Date());
    if (selectedYear == -1 || selectedMonth == -1) selectedYear = calendar.get(Calendar.YEAR);
    yearComboBox.setModel(new DefaultComboBoxModel<String>(new String[]{Integer.toString(selectedYear - 1), Integer.toString(selectedYear)}));
    yearComboBox.setBounds(140, 10, 100, 30);
    yearComboBox.setSelectedIndex(1);
    yearComboBox.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent arg0) {
        selectedYear = Integer.parseInt((String) yearComboBox.getSelectedItem());
        setSelectedShifts(selectedMonth, selectedYear);
      }
    });
    createAccountmentFrame.getContentPane().add(yearComboBox);

    if (selectedYear == -1 || selectedMonth == -1) selectedMonth = calendar.get(Calendar.MONTH);
    monthComboBox.setModel(new DefaultComboBoxModel<String>(new String[]{"Januar", "Februar", "M\u00E4rz", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"}));
    monthComboBox.setBounds(10, 10, 120, 30);
    monthComboBox.setSelectedIndex(selectedMonth);
    monthComboBox.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        // TODO: monat wurde ge�ndert-> liste neu laden
        selectedMonth = monthComboBox.getSelectedIndex();
        setSelectedShifts(selectedMonth, selectedYear);
      }
    });
    createAccountmentFrame.getContentPane().add(monthComboBox);

    displayTable = new JTable(displayTableModel);
    displayTable.getTableHeader().setReorderingAllowed(false);
    displayTable.getTableHeader().setResizingAllowed(false);
    displayPane = new JScrollPane(displayTable);
    displayPane.setBounds(10, 48, 580, 263);
    //displayPane.setAutoscrolls(true);
    displayTableModel.addRow(new String[]{"do", "datum?", "1-3", "ktw"});
    createAccountmentFrame.getContentPane().add(displayPane);

    printCheckbox = new JCheckBox("Drucken");
    printCheckbox.setBounds(250, 15, 100, 23);
    printCheckbox.setSelected(true);
    createAccountmentFrame.getContentPane().add(printCheckbox);

    createAccountmentFrame.setVisible(true);
    // fill the displayTable
    setSelectedShifts(selectedMonth, selectedYear);
  }

  /**
   * Fill the displayed List with the selectet shifts
   * @param shifts 
   */
  private void fillList(ArrayList<ShiftInstance> shifts) {
    displayTableModel.setRowCount(0);
    for (int i = 0; i < shifts.size(); i++) {
      try {
        ShiftInstance shiftInstance = shifts.get(i);
        String dateString = shiftInstance.getDateString();
        calendar.setTime(sdf.parse(dateString));
        String dayOfWeek = UtilityBox.getDayOfWeekString(calendar.get(Calendar.DAY_OF_WEEK));
        String fromToString =
                UtilityBox.createTimeStringFromInt(shiftInstance.getActualStartingTimeWithPrepTime())
                + " - " + UtilityBox.createTimeStringFromInt(shiftInstance.getActualEndTime());
        String[] rowData = new String[]{dayOfWeek + ", " + dateString,
          fromToString, shiftInstance.getType().toString(),
          shiftInstance.getPartner()};
        displayTableModel.addRow(rowData);
      } catch (ParseException ex) {
        UtilityBox.getInstance().displayErrorPopup("Schicht-Anzeige",
                "Datum konnte nicht gelesen werden:\n" + ex.getMessage());
      }
    }
  }

  /**
   * Set global value selectedShifts to a filtered List, containing all shifts
   * from allShifts that match month and year.
   * calls fillList(selectedShifts)
   * @param month
   * @param year 
   */
  private void setSelectedShifts(int month, int year) {
    selectedShifts = UtilityBox.getInstance().getShiftContainer().getShiftInsances(year, month);
    fillList(selectedShifts);
  }
}
