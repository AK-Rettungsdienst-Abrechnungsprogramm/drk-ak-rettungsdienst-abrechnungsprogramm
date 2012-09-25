package de.drk.akrd;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.swing.table.DefaultTableModel;

/**
 * Manage the ImportExport-window defined in MainWindow
 * @author Jo
 */
public class ImportExport {
  private static ImportExport INSTANCE = null;
  private ArrayList<ShiftInstance> selectedShifts;
  private MainWindow mainWindow;
  Calendar calendar = Calendar.getInstance();
  private SimpleDateFormat sdf = UtilityBox.SIMPLE_DATE_FORMAT;
  private int[] selectableYears;
  private ArrayList<String> importedPersData = null;
  private ImportExport(MainWindow mw){
    selectedShifts = new ArrayList<>();
    mainWindow = mw;
  }
  public static ImportExport GetInstance(MainWindow mw) {
    if (INSTANCE == null) {
      INSTANCE = new ImportExport(mw);
    }
    return INSTANCE;
  }
  /**
   * Write displayed (selected) shifts t an XML-file using XMLEditor Class
   * @return true is success
   */
  public boolean exportSelected() {
    UtilityBox ub = UtilityBox.getInstance();
    if (selectedShifts.isEmpty()) {
      ub.displayInfoPopup("Import", "Keine Schichten ausgewählt.");
      return false;
    }
    XMLEditor.exportData(selectedShifts);
    UtilityBox.getInstance().displayInfoPopup("Export", "Die angezeigten Schichten wurden exportiert.");
    return true;
  }

  /**
   * Import displayed (selected) shifts to the global shiftlist
   * @return true if success
   */
  public boolean importSelected() {
    UtilityBox ub = UtilityBox.getInstance();
    if (selectedShifts.isEmpty()) {
      ub.displayInfoPopup("Import", "Keine Schichten ausgewählt.");
      return false;
    }
    mainWindow.shiftContainer.registerShift(selectedShifts, true);
    if (importedPersData!=null) {
      overwritePersonalData();
    }
    ub.displayInfoPopup("Import", "Die angezeigten Schichten wurden importiert.");
    return true;
  }

  /**
   * Questiondialog weather the user wants to overwrite the stored personal
   * data with the imported data.
   * If so, overwrites the stored data.
   */
  private void overwritePersonalData() {
    PersonalData pd = PersonalData.getInstance();
    if(UtilityBox.getInstance().displayYesNoPopup("Import",
            "Persönliche Daten von\n"+pd.getFirstName()+" "+pd.getLastName()
            +"\nmit den importierten Daten von\n"+importedPersData.get(0) +" "
            +importedPersData.get(1) +"\nüberschreiben?")){
      System.out.println("daten überschreiben");
      PersonalData.getInstance().setData(
            importedPersData.get(0),
            importedPersData.get(1),
            importedPersData.get(2),
            importedPersData.get(3),
            importedPersData.get(4),
            importedPersData.get(5),
            importedPersData.get(6),
            importedPersData.get(7),
            importedPersData.get(8));
    }
    
  }
  /**
   * Set selected shifts, calls fillList
   * @param shiftTableModel
   * @param month 0=full year, 1=january...
   * @param year 
   */
  public void setSelected(DefaultTableModel shiftTableModel, int month, int year) {
    // TODO: globale variable setzen, month ist hier 0=ganzes jahr, 1=januar...
    selectedShifts = new ArrayList<>();
    ArrayList<ShiftInstance> allShifts = mainWindow.shiftContainer.shiftInstances;
    boolean wholeYear = (month == 0)? true: false;
    try {
      for (int i = 0; i < allShifts.size(); i++) {
        ShiftInstance shiftInstance = allShifts.get(i);
        calendar.setTime(sdf.parse(shiftInstance.getDateString()));
        if ((wholeYear || (calendar.get(Calendar.MONTH) == (month - 1))) && (calendar.get(Calendar.YEAR) == selectableYears[year])) {
          selectedShifts.add(shiftInstance);
        }
      }
    } catch (ParseException ex) {
      UtilityBox.getInstance().displayErrorPopup("Schichtfilter", "Schichtdaten"
              + " konnten nicht ausgelesen werden:\n" + ex.getMessage());
    }
    fillList(shiftTableModel, selectedShifts);
  }
  /**
   * Fill the displayed List with the selectet shifts
   * @param shifts 
   */
  private void fillList(DefaultTableModel shiftTableModel, ArrayList<ShiftInstance> shifts) {
    shiftTableModel.setRowCount(0);
    for (int i = 0; i < shifts.size(); i++) {
      try {
        ShiftInstance shiftInstance = shifts.get(i);
        String dateString = shiftInstance.getDateString();
        calendar.setTime(sdf.parse(dateString));
        String dayOfWeek = UtilityBox.getDayOfWeekString(calendar.get(Calendar.DAY_OF_WEEK));
        String fromToString =
                UtilityBox.createTimeStringFromInt(shiftInstance.getActualStartingTime())
                + " - " + UtilityBox.createTimeStringFromInt(shiftInstance.getActualEndTime());
        String[] rowData = new String[]{dayOfWeek + ", " + dateString,
          fromToString, shiftInstance.getType().toString(),
          shiftInstance.getPartner()};
        shiftTableModel.addRow(rowData);
      } catch (ParseException ex) {
        UtilityBox.getInstance().displayErrorPopup("Schicht-Anzeige",
                "Datum konnte nicht gelesen werden:\n" + ex.getMessage());
      }
    }
  }

  public void showSaveDialog(int month, int year) {
    // TODO: savedialog
  }

  /**
   * FileChooser-dialog to select an XML-inputfile
   * @param shiftTableModel
   * @return path to the selected file
   */
  public String selectImportFile(DefaultTableModel shiftTableModel) {
    String filePath = UtilityBox.getInstance().getFilePathFromFileCooser("xml", "XML-Dateien", System.getProperty("user.dir"));
    importedPersData = new ArrayList<>();
    selectedShifts = XMLEditor.importData(filePath, importedPersData);
    fillList(shiftTableModel, selectedShifts);
    return filePath;
  }

  /**
   * Return a String[] filled with selectable years
   * and set selectableYears-array
   * @return String[]
   */
  public String[] getYearStrings() {
    calendar.setTime(new Date());
//    int currentYear = calendar.get(Calendar.YEAR);
//    selectableYears = new int[]{currentYear-1, currentYear};
    // TODO: alle jahre von denen schichten existieren
    ArrayList<Integer> yearList = ShiftContainer.getSortedYearList();
    int nYears = yearList.size();
    selectableYears = new int[nYears];
    for (int i = 0; i < nYears; i++) {
      selectableYears[i] = yearList.get(i);
    }
    String[] yearStrings = new String[nYears];
    for (int i = 0; i<nYears; i++){
      Integer integer = yearList.get(i);
      yearStrings[i] = integer.toString();
    }
    return yearStrings;
  }
}
