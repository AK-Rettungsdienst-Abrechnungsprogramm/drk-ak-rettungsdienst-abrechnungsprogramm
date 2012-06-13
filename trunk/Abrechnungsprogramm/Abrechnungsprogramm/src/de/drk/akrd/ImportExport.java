/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.drk.akrd;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Jo
 */
public class ImportExport {
  private static ImportExport INSTANCE = null;
  private ArrayList<ShiftInstance> selectedShifts;
  private int[] selectableYears;
  private ImportExport(){
    selectedShifts = new ArrayList<>();
  }
  public static ImportExport GetInstance() {
    if (INSTANCE == null) {
      INSTANCE = new ImportExport();
    }
    return INSTANCE;
  }
  public void exportSelected() {
    // TODO: implement
  }

  public void importSelected() {
    // TODO: implement
  }

  public void setSelected(int month, int year) {
    // TODO: globale variable setzen
  }

  public void showSaveDialog(int month, int year) {
    // TODO: savedialog
  }

  public String showFileChooserDialog() {
    // TODO: file select-dialog
    return null;
  }

  public String[] getYearStrings() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    // TODO: yearstrings ausgeben (angezeigte jahre in der export-jahr-auswahl)
    // TODO: set selectable years to read out later
    selectableYears = new int[]{};
    return new String[]{"jahr1", "jahr2", "jahr3"};
  }
}
