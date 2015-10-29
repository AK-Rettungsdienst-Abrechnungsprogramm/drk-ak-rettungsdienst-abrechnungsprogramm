/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.drk.akrd;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author Jo
 */
public class StatisticTab extends JPanel{
  private final JComboBox<String> MonthComboBox;
  private final JComboBox<String> YearComboBox;
  private Calendar calendar = Calendar.getInstance();
  
  public StatisticTab() {
    super();
    this.setLayout(null);
    int yFirstLine = 8;
    
    calendar.setTime(new Date());
    int currentYear = calendar.get(Calendar.YEAR);
    int currentMonth = calendar.get(Calendar.MONTH);
    MonthComboBox = new JComboBox<String>();
    MonthComboBox.setModel(new DefaultComboBoxModel<String>(new String[]{"Alle","Januar",
              "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September",
              "Oktober", "November", "Dezember"}));
    MonthComboBox.setBounds(10, yFirstLine, 120, 20);
    MonthComboBox.setSelectedIndex(currentMonth+1);

    YearComboBox = new JComboBox<String>();
    ArrayList<Integer> yearList = UtilityBox.getInstance().getShiftContainer().getSortedYearList();
    int nYears = yearList.size();
    String[] yearStrings = new String[nYears];
    for (int i = 0; i < nYears; i++) {
      yearStrings[i] = Integer.toString(yearList.get(nYears-i-1));
    }
    YearComboBox.setModel(new DefaultComboBoxModel<String>(yearStrings));
    YearComboBox.setBounds(140, yFirstLine, 85, 20);
    YearComboBox.setSelectedIndex(yearStrings.length -1);
    
    // add comboboxes to pane
    this.add(MonthComboBox);
    this.add(YearComboBox);
    
    
    
    
  }
  
}
