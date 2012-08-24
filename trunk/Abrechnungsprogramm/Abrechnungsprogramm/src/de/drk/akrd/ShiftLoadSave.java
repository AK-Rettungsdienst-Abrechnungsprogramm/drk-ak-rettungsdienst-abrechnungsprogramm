/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.drk.akrd;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Calendar;
import sun.security.jca.GetInstance;

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
    ArrayList<ShiftInstance> yearList = new ArrayList<>();
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
  
  public static ArrayList<ShiftInstance> loadSavedShifts() {
    ArrayList<ShiftInstance> loadedShifts = new ArrayList<>();
    File dir = new File("data");
    String[] fileList = dir.list(new FilenameFilter() {
      public boolean accept(File d, String name) {
        return (name.startsWith("Schichten") && name.endsWith(".xml") && name.length()==17);
      }
    });
    System.out.println("filelist length: "+fileList.length);
    if (fileList != null) {
      for (String string : fileList) {
        System.out.println("substring: "+string.substring(9, 13));
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
