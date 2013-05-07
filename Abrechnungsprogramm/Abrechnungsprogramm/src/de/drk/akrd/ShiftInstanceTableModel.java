package de.drk.akrd;

import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class ShiftInstanceTableModel extends AbstractTableModel {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  // the column names
  private String[] columns = new String[]{"Datum", "Beginn", "Ende",
      "Pause", "Dezimal", "Schichtpartner", "Art", "Kommentar","Fahrtkosten",
      "Verdienst"};
  
  // all shift instances displayed
  private ArrayList<ShiftInstance> data = new ArrayList<ShiftInstance>();
  
  // the overall salary
  private float overallSalary = 0.0f;
  
  public String getColumnName(int col) {
    return columns[col];
  }
  @Override
  public int getColumnCount() {
    return columns.length;
  }

  @Override
  public int getRowCount() {
    return data.size() + 1;
  }

  @Override
  public Object getValueAt(int row, int col) {
    // if the last row was selected display the summary line
    if (row == data.size()) {
      if (col == columns.length - 1) {
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(overallSalary) + " €";
      } else {
        return "";
      }
    }
    switch(col){
      case 0:
        return data.get(row).getDateString();
      case 1:
        return UtilityBox.createTimeStringFromInt(data.get(row).getActualStartingTimeWithPrepTime());
      case 2:
        return UtilityBox.createTimeStringFromInt(data.get(row).getActualEndTime());
      case 3:
        return UtilityBox.createTimeStringFromInt(data.get(row).getActualBreakTime());
      case 4: {
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(data.get(row).getTimeAsFloat()); }
      case 5:
        return data.get(row).getPartner();
      case 6:
        return data.get(row).getType().toString();
      case 7:
        return data.get(row).getComment();
      case 8:
        return Integer.toString(data.get(row).getCommuteExpenses()) + " €";
      case 9: {
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(UtilityBox.calculateSalary(data.get(row), PersonalData.getInstance().getQualification())) + " €";}
      default:
        return "???"; 
    }
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    // all cells false
    return false;
  }
  
  // removes all data
  public void clear() {
    data.clear();
    overallSalary = 0.0f;
    fireTableDataChanged();
  }
  
  // adds a shift instance to the model
  public void add(ShiftInstance si) {
   data.add(si);
   overallSalary += UtilityBox.calculateSalary(si, PersonalData.getInstance().getQualification());
   fireTableDataChanged();
  }
  
  // returns the requested shift instance
  public ShiftInstance getItem(int selectedRow) {
    if (selectedRow < data.size()) {
      return data.get(selectedRow);
    } else {
      return null;
    }
  }
}
