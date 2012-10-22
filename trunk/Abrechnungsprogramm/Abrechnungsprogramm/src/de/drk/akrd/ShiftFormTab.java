package de.drk.akrd;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.table.DefaultTableModel;

public class ShiftFormTab extends JFrame {

  //private JPanel panel;
  private final Action cancel = new SwingAction();
  private final Action create = new SwingAction_1();
  private DefaultTableModel dayTableModel = new DefaultTableModel(
          new Object[][]{}, new String[]{""}) {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean isCellEditable(int row, int column) {
      // all cells false
      return false;
    }
  };
  private JTextField maxShiftsField;
  private JTextField mentor3rdPosField;
  private JTextField mentor2ndPosField;
  private ArrayList<ButtonGroup> allButtonGroups = new ArrayList<>();
  private ArrayList<ButtonGroup> monthButtonGroups = new ArrayList<>();
  private ArrayList<JLabel> allLabels = new ArrayList<>();
  private Calendar calendar = Calendar.getInstance();

  public ShiftFormTab(final JPanel panel) {
    //panel = jpanel;
    final JComboBox MonthComboBox;
    final JComboBox YearComboBox;
    calendar.setTime(new Date());
    int currentYear = calendar.get(Calendar.YEAR);
    int currentMonth = calendar.get(Calendar.MONTH);
    JButton cancelButton = new JButton("Abbrechen");
    cancelButton.setAction(cancel);
    
    JButton createButton = new JButton("Erstellen");
    createButton.setAction(create);

    MonthComboBox = new JComboBox();
    MonthComboBox.setModel(new DefaultComboBoxModel(new String[]{"Januar",
      "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September",
      "Oktober", "November", "Dezember"}));
    MonthComboBox.setBounds(10, 11, 78, 20);
    MonthComboBox.setSelectedIndex((currentMonth < 11)? (currentMonth+1): 0);

    YearComboBox = new JComboBox();
    YearComboBox.setModel(new DefaultComboBoxModel(new String[]{Integer.toString(currentYear), Integer.toString(currentYear+1)}));
    YearComboBox.setBounds(98, 11, 85, 20);
    YearComboBox.setSelectedIndex((currentMonth < 11)? 0: 1);

    // create Action listener for both comboboxes
    ActionListener comboBoxActionListener = new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        addWeeksToPanel(MonthComboBox.getSelectedIndex(), Integer.parseInt((String)YearComboBox.getSelectedItem()), panel);
      }
    };
    MonthComboBox.addActionListener(comboBoxActionListener);
    YearComboBox.addActionListener(comboBoxActionListener);
    panel.add(MonthComboBox);
    panel.add(YearComboBox);

    JLabel maxShiftsLabel = new JLabel("Max. Dienste");
    maxShiftsLabel.setBounds(209, 14, 80, 14);
    panel.add(maxShiftsLabel);

    maxShiftsField = new JTextField();
    maxShiftsField.setBounds(290, 11, 86, 20);
    panel.add(maxShiftsField);
    maxShiftsField.setColumns(10);

    JLabel lblMentorenschichten = new JLabel("Mentorenschichten:");
    lblMentorenschichten.setBounds(10, 427, 150, 14);
    panel.add(lblMentorenschichten);
    // Mentor-shifts 3. Pos
    mentor3rdPosField = new JTextField();
    mentor3rdPosField.setBounds(130, 421, 43, 20);
    panel.add(mentor3rdPosField);
    mentor3rdPosField.setColumns(10);
    JLabel lblPos = new JLabel("3. Pos.");
    lblPos.setBounds(130, 405, 46, 14);
    panel.add(lblPos);

    // Mentor-shifts 2. Pos
    mentor2ndPosField = new JTextField();
    mentor2ndPosField.setColumns(10);
    mentor2ndPosField.setBounds(178, 421, 43, 20);
    panel.add(mentor2ndPosField);
    JLabel lblPos_1 = new JLabel("2. Pos.");
    lblPos_1.setBounds(178, 405, 46, 14);
    panel.add(lblPos_1);

    JButton btnAusgeben = new JButton("Ausgeben");
    btnAusgeben.setBounds(305, 452, 89, 23);
    panel.add(btnAusgeben);

    JButton btnZurcksetzen = new JButton("Zurücksetzen");
    btnZurcksetzen.setBounds(20, 452, 104, 23);
    panel.add(btnZurcksetzen);

    JButton btnAbbrechen = new JButton("Abbrechen");
    btnAbbrechen.setBounds(209, 452, 89, 23);
    panel.add(btnAbbrechen);
    
    // initialize radiogroups
    calendar.add(Calendar.MONTH, 1);
    addWeeksToPanel(calendar.get(Calendar.MONTH), calendar.get(Calendar.MONTH), panel);
  }

  /**
   * step 1: remove all labels and radiogroups from panel
   * step 2: calculate shown dates
   * step 3: add new radiogroups
   * @param month 0-11
   * @param currentYear
   * @param panel 
   */
  private void addWeeksToPanel(int month, int year, JPanel panel) {
    
    // step 1 remove all labels and radiogroups from panel
    for (int i = 0; i < allButtonGroups.size(); i++) {
      Enumeration<AbstractButton> e = allButtonGroups.get(i).getElements();
      while (e.hasMoreElements()) {
        AbstractButton abstractButton = e.nextElement();
        abstractButton.setVisible(false);
        panel.remove(abstractButton);
      }
    }
    allButtonGroups.clear();
    for (int i = 0; i < allLabels.size(); i++) {
      JLabel l = allLabels.get(i);
      l.setVisible(false);
      panel.remove(l);
    }
    allLabels.clear();

    // step 2: calculate shown dates
    calendar.set(year, month, 1);
    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
    // set Date to Monday before 1st day of requested month
    calendar.add(Calendar.DATE, (dayOfWeek==1)? -6: (-1*(dayOfWeek-2)));
    System.out.println("1."+(calendar.get(Calendar.MONTH) +1)+"."+year+" tag der woche: "+dayOfWeek+"\n date: "+calendar.getTime().toString());
    //step 3: add new radiogroups
    // TODO: implement
    int x = 10;
    int y = 38;
    while ((calendar.get(Calendar.MONTH)<= month) && !((month == 11) && (calendar.get(Calendar.MONTH) == 0))) {      
      addRadioButtonGroupToPanel(calendar.getTime(), x, y, panel);
      System.out.println("Add radiogroup:"+UtilityBox.getFormattedDateString(calendar.getTime()));
      calendar.add(Calendar.DATE, 1);
      y += 25;
    }
  }
  /**
   * Add labeled radio button-goup at position (x,y) to the panel
   * @param date date in the label
   * @param x x-position
   * @param y y-position
   * @param panel 
   */
  private void addRadioButtonGroupToPanel(Date date, int x, int y, JPanel panel) {
    JLabel label;
    // create label
    calendar.setTime(date);
    String day = UtilityBox.getDayOfWeekString(calendar.get(Calendar.DAY_OF_WEEK));
    String formattedDate = UtilityBox.getFormattedDateString(date);
    label = new JLabel(day + ", " + formattedDate);
    allLabels.add(label);
    label.setBounds(x, y, 80, 23);
    panel.add(label);
    // create buttongroup
    ButtonGroup bg = returnRadioGroup(x+80, y);
    allButtonGroups.add(bg);
    Enumeration<AbstractButton> e = bg.getElements();
    while (e.hasMoreElements()) {      
      panel.add(e.nextElement());
    }
  }
  /**
   * return a radiogroup with 5 radio buttons, arranged horizontal
   * captions: "X", "F", "S", "T", "N"
   * @param x x-position of group
   * @param y y-position of group
   * @return ButtonGroup
   */
  private ButtonGroup returnRadioGroup(int x, int y){
    int xSize = 33;
    JRadioButton rdbtnX = new JRadioButton("X");
    rdbtnX.setBounds(x, y, xSize, 23);
    rdbtnX.setVisible(true);
    JRadioButton rdbtnF = new JRadioButton("F");
    rdbtnF.setBounds(x+xSize, y, xSize, 23);
    rdbtnF.setVisible(true);
    JRadioButton rdbtnS = new JRadioButton("S");
    rdbtnS.setBounds(x+(2*xSize), y, xSize, 23);
    rdbtnS.setVisible(true);
    JRadioButton rdbtnT = new JRadioButton("T");
    rdbtnT.setBounds(x+(3*xSize), y, xSize, 23);
    rdbtnT.setVisible(true);
    JRadioButton rdbtnN = new JRadioButton("N");
    rdbtnN.setBounds(x+(4*xSize), y, xSize, 23);
    rdbtnN.setVisible(true);
    
    ButtonGroup bg = new ButtonGroup();
    bg.add(rdbtnX);
    bg.add(rdbtnF);
    bg.add(rdbtnS);
    bg.add(rdbtnT);
    bg.add(rdbtnN);
    return bg;
  }
  private void setRadioFields(){}
  private class SwingAction extends AbstractAction {

    public SwingAction() {
      putValue(NAME, "SwingAction");
      putValue(SHORT_DESCRIPTION, "Some short description");
    }

    public void actionPerformed(ActionEvent e) {
    }
  }

  private class SwingAction_1 extends AbstractAction {

    public SwingAction_1() {
      putValue(NAME, "SwingAction_1");
      putValue(SHORT_DESCRIPTION, "Some short description");
    }

    public void actionPerformed(ActionEvent e) {
    }
  }
}
