/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.drk.akrd;

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
  public static boolean saveShifts(ArrayList<ShiftInstance> shifts) {
    ArrayList<ArrayList<ShiftInstance>> yearLists = new ArrayList<>();
    ArrayList<Integer> years = new ArrayList<>();
    int currentYear = 0;
    ArrayList<ShiftInstance> currentList = new ArrayList<>();
    for (int i = 0; i < shifts.size(); i++) {
      ShiftInstance shiftInstance = shifts.get(i);
      calendar.setTime(shiftInstance.getDate());
      int shiftYear = calendar.get(Calendar.YEAR);
      if (shiftYear != currentYear) {
        if (!currentList.isEmpty()) {
          yearLists.add(currentList);
          currentList = new ArrayList<>();
        }
        currentYear = shiftYear;
      }
      currentList.add(shiftInstance);
    }
    
    System.out.print("bla\n");
    return true;
  }
}
