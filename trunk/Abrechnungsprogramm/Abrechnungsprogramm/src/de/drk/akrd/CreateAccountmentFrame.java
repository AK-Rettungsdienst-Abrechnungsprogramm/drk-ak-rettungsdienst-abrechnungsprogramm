package de.drk.akrd;

import java.text.ParseException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.border.BevelBorder;
import javax.swing.JCheckBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.DefaultListModel;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

/**
 *
 * @author Jo
 */
public class CreateAccountmentFrame {

  private JFrame createAccountmentFrame = new JFrame("Abrechnung erstellen");
  private static ArrayList<ShiftInstance> allShifts = null;
  private ArrayList<ShiftInstance> selectedShifts = null;
  private JComboBox yearComboBox;
  private JComboBox monthComboBox;
  private JCheckBox printCheckbox;
  private JList displayList;
  private DefaultListModel displayListModel = new DefaultListModel();
  private Calendar calendar = Calendar.getInstance();
  private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
  private int selectedYear = 0;
  private int selectedMonth = 0;

  public CreateAccountmentFrame(ArrayList<ShiftInstance> shifts) {
	createAccountmentFrame.setSize(515, 360);
    createAccountmentFrame.getContentPane().setLayout(null);
    createAccountmentFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    calendar.setTimeInMillis(System.currentTimeMillis());
    JButton btnAbbrechen = new JButton("Abbrechen");
    btnAbbrechen.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        createAccountmentFrame.dispose();
      }
    });
    btnAbbrechen.setBounds(390, 10, 100, 30);
    createAccountmentFrame.getContentPane().add(btnAbbrechen);

    JButton btnAusgeben = new JButton("Ausgeben");
    btnAusgeben.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent arg0) {
        // TODO: Abrechnung speichern und evtl drucken
        String filePath = PdfCreator.createAccounting(selectedShifts.toArray(new ShiftInstance[selectedShifts.size()]), selectedMonth, selectedYear);
        if (printCheckbox.isSelected() && (filePath!=null)) {
          UtilityBox.getInstance().printFile(filePath);
        }
      }
    });
    btnAusgeben.setBounds(280, 10, 100, 30);
    createAccountmentFrame.getContentPane().add(btnAusgeben);

    yearComboBox = new JComboBox();
    selectedYear = calendar.get(Calendar.YEAR);
    yearComboBox.setModel(new DefaultComboBoxModel(new String[]{Integer.toString(selectedYear - 1), Integer.toString(selectedYear)}));
    yearComboBox.setBounds(100, 10, 66, 30);
    yearComboBox.setSelectedIndex(1);
    yearComboBox.addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent arg0) {
        selectedYear = Integer.parseInt((String)yearComboBox.getSelectedItem());
        System.out.println("jahr geändert: "+selectedYear);
        setSelectedShifts(selectedMonth, selectedYear);
    	}
    });
    createAccountmentFrame.getContentPane().add(yearComboBox);

    monthComboBox = new JComboBox();
    selectedMonth = calendar.get(Calendar.MONTH);
    monthComboBox.setModel(new DefaultComboBoxModel(new String[]{"Januar", "Februar", "M\u00E4rz", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"}));
    monthComboBox.setBounds(10, 10, 80, 30);
    monthComboBox.setSelectedIndex(selectedMonth);
    monthComboBox.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        // TODO: monat wurde ge�ndert-> liste neu laden
        selectedMonth = monthComboBox.getSelectedIndex();
        System.out.println("monat geändert: "+selectedMonth);
        setSelectedShifts(selectedMonth, selectedYear);
      }
    });
    createAccountmentFrame.getContentPane().add(monthComboBox);

    displayList = new JList(displayListModel);
    displayList.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
    displayList.setBounds(10, 48, 480, 263);
    createAccountmentFrame.getContentPane().add(displayList);

    printCheckbox = new JCheckBox("Drucken");
    printCheckbox.setBounds(180, 15, 80, 23);
    printCheckbox.setSelected(true);
    createAccountmentFrame.getContentPane().add(printCheckbox);
    allShifts = shifts;
    createAccountmentFrame.setVisible(true);
    // fill the displayList
    setSelectedShifts(selectedMonth, selectedYear);
  }

  /**
   * Fill the displayed List with the selectet shifts
   * @param shifts 
   */
  private void fillList(ArrayList<ShiftInstance> shifts) {
    displayListModel.clear();
    for (int i = 0; i < shifts.size(); i++) {
      ShiftInstance shiftInstance = shifts.get(i);
      displayListModel.add(i, getShiftString(shiftInstance));
    }
  }

  /**
   * 
   * @param shiftInstance
   * @return 
   */
  private String getShiftString(ShiftInstance shiftInstance) {
    // TODO: string bauen
    String returnString = shiftInstance.getDate()+"\f"+shiftInstance.getType().toString();
    return returnString;
  }

  /**
   * Set global value selectedShifts to a filtered List, containing all shifts
   * from allShifts that match month and year.
   * calls fillList(selectedShifts)
   * @param month
   * @param year 
   */
  private void setSelectedShifts(int month, int year) {
    selectedShifts = new ArrayList<>();
    // TODO: filter
    try {
      for (int i = 0; i < allShifts.size(); i++) {

        ShiftInstance shiftInstance = allShifts.get(i);
        calendar.setTime(sdf.parse(shiftInstance.getDate()));
        if ((calendar.get(Calendar.MONTH)==month)&&(calendar.get(Calendar.YEAR)==year)) {
          selectedShifts.add(shiftInstance);
          
        }
      }
    } catch (ParseException ex) {
      UtilityBox.getInstance().displayErrorPopup("Schichtfilter", "Schichtdaten"
              + " konnten nicht ausgelesen werden:\n"+ex.getMessage());
    }
    fillList(selectedShifts);
  }
}
