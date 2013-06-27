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
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;

/**
 *
 * @author Jo
 */
public class CreateAccountmentFrame extends JFrame{
  private ArrayList<ShiftInstance> selectedShifts = null;
  private JComboBox<String> yearComboBox = new JComboBox<String>();
  private JComboBox<String> monthComboBox = new JComboBox<String>();;
  private JCheckBox printCheckbox;
  private JCheckBox saveCheckbox;
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
    this.setTitle("Abrechnung erstellen");
	// position relative to main window
	int x = UtilityBox.getInstance().getWindowPosX();
	int y = UtilityBox.getInstance().getWindowPosY();
	
	selectedYear = year;
	selectedMonth = month;

    this.setBounds(x + 20, y + 20, 600, 400);
    this.getContentPane().setLayout(null);
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    calendar.setTimeInMillis(System.currentTimeMillis());
    JButton btnAbbrechen = new JButton("Abbrechen");
    btnAbbrechen.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    });
    btnAbbrechen.setBounds(460, 320, 120, 30);
    this.getContentPane().add(btnAbbrechen);

    JButton btnAusgeben = new JButton("Ausgeben");
    btnAusgeben.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent arg0) {
        // no checkbox selected
        if (!printCheckbox.isSelected() && !saveCheckbox.isSelected()) {
          UtilityBox.getInstance().displayInfoPopup("Keine Funktion ausgewählt",
                  "Bitte \"Drucken\" oder \"Abrechnung speichern\" auswählen.");
          return;
        }
        String filePath = PdfCreator.createAccounting(
                selectedShifts.toArray(new ShiftInstance[selectedShifts.size()]),
                selectedMonth, selectedYear);
        if (filePath != null) {
          // print file if "print"-checkbox is selected
          if (printCheckbox.isSelected()) {
          UtilityBox.getInstance().printFile(filePath);
          }
          // if "save"-checkbox is deselected delete accountment-file
          // else show "saved"-message
          if (!saveCheckbox.isSelected()) {
            File deleteFile = new File(filePath);
            boolean deleteSuccess = deleteFile.delete();
            if (!deleteSuccess) {
              System.out.println("deletesucc:"+deleteSuccess);
              UtilityBox.getInstance().displayErrorPopup("Löschen der "
                      + "temporären Datei", "Die temporäre Abrechnungs-Datei "
                      + "unter\n"+filePath+"\nkonnte nicht gelöscht werden.");
            }
          } else {
            UtilityBox.getInstance().displayInfoPopup("Abrechnung",
                    "Abrechnung unter "+filePath+" gespeichert.");
          }
        }
        // close Window
        dispose();
      }
    });
    btnAusgeben.setBounds(330, 320, 120, 30);
    this.getContentPane().add(btnAusgeben);

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
    this.getContentPane().add(yearComboBox);

    if (selectedYear == -1 || selectedMonth == -1) selectedMonth = calendar.get(Calendar.MONTH);
    monthComboBox.setModel(new DefaultComboBoxModel<String>(new String[]{"Januar", "Februar", "M\u00E4rz", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"}));
    monthComboBox.setBounds(10, 10, 120, 30);
    monthComboBox.setSelectedIndex(selectedMonth);
    monthComboBox.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        selectedMonth = monthComboBox.getSelectedIndex();
        setSelectedShifts(selectedMonth, selectedYear);
      }
    });
    this.getContentPane().add(monthComboBox);

    displayTable = new JTable(displayTableModel);
    displayTable.getTableHeader().setReorderingAllowed(false);
    displayTable.getTableHeader().setResizingAllowed(false);
    displayPane = new JScrollPane(displayTable);
    displayPane.setBounds(10, 48, 580, 263);
    //displayPane.setAutoscrolls(true);
    displayTableModel.addRow(new String[]{"do", "datum?", "1-3", "ktw"});
    this.getContentPane().add(displayPane);

    printCheckbox = new JCheckBox("Drucken");
    printCheckbox.setBounds(250, 15, 100, 23);
    printCheckbox.setSelected(true);
    this.getContentPane().add(printCheckbox);
    
    saveCheckbox = new JCheckBox("Abrechnung speichern");
    saveCheckbox.setBounds(350, 15, 250, 23);
    saveCheckbox.setSelected(!MainWindow.WACHENVERSION);
    this.getContentPane().add(saveCheckbox);

    this.setVisible(true);
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
