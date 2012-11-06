package de.drk.akrd;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Calendar;

/**
 *
 * @author Jo
 */
public class ShiftLoadSave {
  private static Calendar calendar = Calendar.getInstance();
  /**
   * Requires a sorted List of ShiftInstances
   * @param shifts
   * @return 
   */
  public static boolean saveShifts(ArrayList<ShiftInstance> shifts, int year) {
    ArrayList<ShiftInstance> yearList = new ArrayList<ShiftInstance>();
    for (int i = 0; i < shifts.size(); i++) {
      ShiftInstance shiftInstance = shifts.get(i);
      calendar.setTime(shiftInstance.getDate());
      int shiftYear = calendar.get(Calendar.YEAR);
      if (shiftYear == year) {
        yearList.add(shiftInstance);
      }
    }
    XMLEditor.storeShifts(yearList, year);
    return true;
  }
  /**
   * finds all files with saved shifts and calls the load-function from
   * XMLEditor
   * @return ArrayList<ShiftInstance> of all saved shifts
   */
  public static ArrayList<ShiftInstance> loadSavedShifts() {
    ArrayList<ShiftInstance> loadedShifts = new ArrayList<ShiftInstance>();
    File dir = new File("data");
    String[] fileList = dir.list(new FilenameFilter() {
      public boolean accept(File d, String name) {
        return (name.startsWith("Schichten") && name.endsWith(".xml") && name.length()==17);
      }
    });
    if (fileList != null) {
      for (String string : fileList) {
        int year = Integer.parseInt(string.substring(9, 13));
        ArrayList<ShiftInstance> tempList = XMLEditor.loadSavedShifts(year);
        for (ShiftInstance shiftInstance : tempList) {
          loadedShifts.add(shiftInstance);
        }
      }
    }
    return loadedShifts;
  }
}
