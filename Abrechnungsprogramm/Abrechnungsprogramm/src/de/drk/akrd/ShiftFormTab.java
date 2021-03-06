package de.drk.akrd;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
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
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

/**
 * UI to fill the Shiftform
 * @author Jo
 */
public class ShiftFormTab extends JPanel {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
  private final Action cancel = new ResetForm();
  private final Action create = new CreateShiftForm();

  private final JComboBox<String> MonthComboBox;
  private final JComboBox<String> YearComboBox;
  private JTextField maxShiftsField;
  private JTextField mentor3rdPosField;
  private JTextField mentor2ndPosField;
  private ArrayList<ButtonGroup> allButtonGroups = new ArrayList<ButtonGroup>();
  private ArrayList<JTextField> allCommentFields = new ArrayList<JTextField>();
  private ArrayList<JLabel> allLabels = new ArrayList<JLabel>();
  private ArrayList<JPanel> weekPanels = new ArrayList<JPanel>();
  private Calendar calendar = Calendar.getInstance();
  private int amountDaysPreviousMonth = 0;
  //private MouseListener checkBoxListener = new CheckboxListener();


  // The scroll pane that holds the weeks
  private JScrollPane scrollPane = new JScrollPane();
  private JPanel weekContainer = new JPanel();

  public ShiftFormTab() {
    int xFirstLine = 8;
    //panel = jpanel;
    calendar.setTime(new Date());
    int currentYear = calendar.get(Calendar.YEAR);
    int currentMonth = calendar.get(Calendar.MONTH);
    JButton cancelButton = new JButton("Abbrechen");
    cancelButton.setAction(cancel);

    JButton createButton = new JButton("Erstellen");
    createButton.setAction(create);

    MonthComboBox = new JComboBox<String>();
    MonthComboBox.setModel(new DefaultComboBoxModel<String>(new String[]{"Januar",
              "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September",
              "Oktober", "November", "Dezember"}));
    MonthComboBox.setBounds(10, xFirstLine-3, 120, 20);
    MonthComboBox.setSelectedIndex((currentMonth < 11) ? (currentMonth + 1) : 0);

    YearComboBox = new JComboBox<String>();
    YearComboBox.setModel(new DefaultComboBoxModel<String>(new String[]{Integer.toString(currentYear), Integer.toString(currentYear + 1)}));
    YearComboBox.setBounds(140, xFirstLine-3, 85, 20);
    YearComboBox.setSelectedIndex((currentMonth < 11) ? 0 : 1);

    
    
    // create Action listener for both comboboxes
    ActionListener comboBoxActionListener = new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        monthChangedCallback();
      }
    };
    MonthComboBox.addActionListener(comboBoxActionListener);
    YearComboBox.addActionListener(comboBoxActionListener);
    this.add(MonthComboBox);
    this.add(YearComboBox);

    JLabel maxShiftsLabel = new JLabel("Max. Dienste");
    maxShiftsLabel.setBounds(240, xFirstLine, 100, 14);
    this.add(maxShiftsLabel);

    maxShiftsField = new JTextField();
    maxShiftsField.setBounds(340, xFirstLine-3, 60, 20);
    this.add(maxShiftsField);
    maxShiftsField.setColumns(10);

    JLabel lblMentorenschichten = new JLabel("Mentorenschichten:");
    lblMentorenschichten.setBounds(430, xFirstLine, 150, 14);
    this.add(lblMentorenschichten);
    // Mentor-shifts 3. Pos
    mentor3rdPosField = new JTextField();
    mentor3rdPosField.setBounds(790, xFirstLine-3, 43, 20);
    this.add(mentor3rdPosField);
    mentor3rdPosField.setColumns(10);
    JLabel lblPos = new JLabel("3. Pos.:");
    lblPos.setBounds(730, xFirstLine, 60, 14);
    this.add(lblPos);

    // Mentor-shifts 2. Pos
    mentor2ndPosField = new JTextField();
    mentor2ndPosField.setColumns(10);
    mentor2ndPosField.setBounds(650, xFirstLine-3, 43, 20);
    this.add(mentor2ndPosField);
    JLabel lblPos_1 = new JLabel("2. Pos.:");
    lblPos_1.setBounds(590, xFirstLine, 60, 14);
    this.add(lblPos_1);
    
    // add caption
    JLabel caption = new JLabel("X: ganzer Tag   /   F: Frühdienst  /   S: "
            + "Spätdienst   /   T: Tag (Früh&Spät)   /   N: Nachtdienst   "
            + "/   G: Geteilt (Früh&Nacht)   /   K: Komplex (Spät&Nacht)");
    caption.setBounds(30, 488, 850, 25);
    this.add(caption);

    JButton btnAusgeben = new JButton("Ausgeben");
    btnAusgeben.setBounds(450, 520, 120, 23);
    btnAusgeben.addActionListener(create);
    this.add(btnAusgeben);

    JButton btnZurcksetzen = new JButton("Zurücksetzen");
    btnZurcksetzen.setBounds(200, 520, 140, 23);
    btnZurcksetzen.addActionListener(cancel);
    this.add(btnZurcksetzen);

    // initialize radiogroups
    calendar.add(Calendar.MONTH, 1);
    
    this.setLayout(null);
    
    
//    scrollPane.setLayout(null);
    scrollPane.setBounds(10,30,880,460);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    weekContainer.setPreferredSize(new Dimension(900,1000));
    weekContainer.setLayout(null);
    scrollPane.setViewportView(weekContainer);
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    this.add(scrollPane);
    addWeeksToPanel(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), weekContainer);
    scrollPane.validate();
  }

  /**
   * step 1: remove all labels and radiogroups from panel
   * step 2: calculate shown dates
   * step 3: add new radiogroups
   * @param month 0-11
   * @param currentYear
   * @param text 
   */
  private void addWeeksToPanel(int month, int year, JPanel text) {
    // step 1 remove all labels and radiogroups from panel
    for (int i = 0; i < allButtonGroups.size(); i++) {
      Enumeration<AbstractButton> e = allButtonGroups.get(i).getElements();
      while (e.hasMoreElements()) {
        AbstractButton abstractButton = e.nextElement();
        abstractButton.setVisible(false);
        text.remove(abstractButton);
      }
    }
    for (int i = 0; i < allButtonGroups.size(); i++) {
      ButtonGroup buttonGroup = allButtonGroups.get(i);
      Enumeration<AbstractButton> buttons = buttonGroup.getElements();
      while (buttons.hasMoreElements()) {
        AbstractButton abstractButton = buttons.nextElement();
        buttonGroup.remove(abstractButton);
      }
    }
    allButtonGroups.clear();
    allCommentFields.clear();
    for (int i = 0; i < allLabels.size(); i++) {
      JLabel l = allLabels.get(i);
      //l.setVisible(false);
      text.remove(l);
    }
    allLabels.clear();
    for (int i = 0; i < weekPanels.size(); i++) {
      JPanel paneli = weekPanels.get(i);
      paneli.removeAll();
      text.remove(paneli);
    }
    weekPanels.clear();
    // step 2: calculate shown dates
    calendar.set(year, month, 1);
    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
    // set Date to Monday before 1st day of requested month
    amountDaysPreviousMonth = (dayOfWeek == 1) ? -6 : (-1 * (dayOfWeek - 2));
    int nMonthDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    calendar.add(Calendar.DATE, amountDaysPreviousMonth);
    //step 3: add new radiogroups
    int x = 10;
    int y = 10;
    int i = 0;
    // add checkboxgroups to panel
    for (int j = 0; j < 6; j++) {
      JPanel weekPanel = new JPanel();
      String panelTitle = "KW" + calendar.get(Calendar.WEEK_OF_YEAR);
      weekPanel.setBorder(new TitledBorder(null, panelTitle, TitledBorder.LEADING, TitledBorder.TOP, null, null));
      weekPanel.setBounds(x, y, 800, 154);
      weekPanel.setLayout(null);
      int xCheckboxGroup = 15;
      int yCheckboxGroup = 17;
      for (int k = 0; k < 7; k++) {
        boolean active = (calendar.get(Calendar.MONTH) == month) ? true : false;
        addCheckboxGroupToPanel(calendar.getTime(), xCheckboxGroup, yCheckboxGroup, weekPanel, active);
        yCheckboxGroup += 19;
        calendar.add(Calendar.DATE, 1);
      }
      y += 153;
      text.add(weekPanel);

      weekPanels.add(weekPanel);
    }
    text.repaint();
//    while ((calendar.get(Calendar.MONTH) <= month) && !((month == 11) && (calendar.get(Calendar.MONTH) == 0))) {
//      addRadioButtonGroupToPanel(calendar.getTime(), x, y, panel);
//      System.out.println("Add radiogroup:" + UtilityBox.getFormattedDateString(calendar.getTime()));
//      calendar.add(Calendar.DATE, 1);
//      y += 25;
//      i++;
//      if (i > ((-amountDaysPreviousMonth + nMonthDays) / 2)) {
//        x = 400;
//        y = 38;
//        i = -100;
//      }
//    }
  }

  /**
   * Add labeled checkbox-goup at position (x,y) to the panel
   * @param date date in the label
   * @param x x-position
   * @param y y-position
   * @param panel 
   */
  private void addCheckboxGroupToPanel(Date date, int x, int y, JPanel panel, boolean active) {
    JLabel label;
    // create label
    calendar.setTime(date);
    String day = UtilityBox.getDayOfWeekString(calendar.get(Calendar.DAY_OF_WEEK));
    String formattedDate = UtilityBox.getFormattedDateString(date);
    label = new JLabel(day + ", " + formattedDate);
    allLabels.add(label);
    label.setBounds(x, y, 110, 18);
    label.setEnabled(active);
    panel.add(label);
    // create buttongroup
    ButtonGroup bg = returnCheckboxGroup(x + 125, y, active, date);
    allButtonGroups.add(bg);
    Enumeration<AbstractButton> e = bg.getElements();
    while (e.hasMoreElements()) {
      panel.add(e.nextElement());
    }
    JTextField commentField = new JTextField(100);
    commentField.setColumns(10);
    commentField.setBounds(x + 450, y, 300, 17);
    commentField.setEnabled(active);
    allCommentFields.add(commentField);
    panel.add(commentField);
  }

  /**
   * return a Checkbox group with 5 checkboxes, arranged horizontal
   * captions: "X", "F", "S", "T", "N"
   * @param x x-position of group
   * @param y y-position of group
   * @return ButtonGroup
   */
  private ButtonGroup returnCheckboxGroup(int x, int y, boolean active, Date date) {
    int xSize = 45;
    int ySize = 18;
    ButtonGroup bg = new ButtonGroup();
    String[] labels = {"X", "F", "S", "T", "N", "G", "K"};
    ShiftForm.TimeCode[] timeCodes = {ShiftForm.TimeCode.X,
      ShiftForm.TimeCode.F, ShiftForm.TimeCode.S, ShiftForm.TimeCode.T,
      ShiftForm.TimeCode.N, ShiftForm.TimeCode.G, ShiftForm.TimeCode.K};
    for (int i = 0; i < timeCodes.length; i++) {
      ExtendedJCheckBox checkBox = new ExtendedJCheckBox(labels[i], timeCodes[i], date);
      checkBox.setBounds((x + (i * xSize)), y, xSize, ySize);
      checkBox.setVisible(true);
      checkBox.setEnabled(active);
      if (active) {
        //checkBox.addItemListener(checkboxItemListener);
        checkBox.addActionListener(new CheckboxActionListener());
      }
      bg.add(checkBox);
    }
//    ExtendedJCheckBox checkBoxX = new ExtendedJCheckBox("X", ShiftForm.TimeCode.X, date);
//    checkBoxX.setBounds(x, y, xSize, ySize);
//    checkBoxX.setVisible(true);
//    checkBoxX.setEnabled(active);
//    ExtendedJCheckBox checkBoxF = new ExtendedJCheckBox("F", ShiftForm.TimeCode.F, date);
//    checkBoxF.setBounds(x + xSize, y, xSize, ySize);
//    checkBoxF.setVisible(true);
//    checkBoxF.setEnabled(active);
//    ExtendedJCheckBox checkBoxS = new ExtendedJCheckBox("S", ShiftForm.TimeCode.S, date);
//    checkBoxS.setBounds(x + (2 * xSize), y, xSize, ySize);
//    checkBoxS.setVisible(true);
//    checkBoxS.setEnabled(active);
//    ExtendedJCheckBox checkBoxT = new ExtendedJCheckBox("T", ShiftForm.TimeCode.T, date);
//    checkBoxT.setBounds(x + (3 * xSize), y, xSize, ySize);
//    checkBoxT.setVisible(true);
//    checkBoxT.setEnabled(active);
//    ExtendedJCheckBox checkBoxN = new ExtendedJCheckBox("N", ShiftForm.TimeCode.N, date);
//    checkBoxN.setBounds(x + (4 * xSize), y, xSize, ySize);
//    checkBoxN.setVisible(true);
//    checkBoxN.setEnabled(active);
//    if (active) {
//      checkBoxX.addMouseListener(checkBoxListener);
//      checkBoxF.addMouseListener(checkBoxListener);
//      checkBoxS.addMouseListener(checkBoxListener);
//      checkBoxT.addMouseListener(checkBoxListener);
//      checkBoxN.addMouseListener(checkBoxListener);
//    }
//
//    bg.add(checkBoxX);
//    bg.add(checkBoxF);
//    bg.add(checkBoxS);
//    bg.add(checkBoxT);
//    bg.add(checkBoxN);
    return bg;
  }

  private class ResetForm extends AbstractAction {

    public ResetForm() {
      putValue(NAME, "Reset");
      //putValue(SHORT_DESCRIPTION, "Some short description");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      for (int i = 0; i < allButtonGroups.size(); i++) {
        ButtonGroup buttonGroup = allButtonGroups.get(i);
        for (int j = 0; j < 2; j++) {
          Enumeration<AbstractButton> buttons = buttonGroup.getElements();
          while (buttons.hasMoreElements()) {
            AbstractButton abstractButton = buttons.nextElement();
            setExtendedJCheckboxInButtonGroupToValue(buttonGroup, (ExtendedJCheckBox) abstractButton, false);
          }
        }
      }
    }
  }

  private class CreateShiftForm extends AbstractAction {

    public CreateShiftForm() {
      putValue(NAME, "CreateShiftForm");
      //putValue(SHORT_DESCRIPTION, "Some short description");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      int month = MonthComboBox.getSelectedIndex();
      int year = Integer.parseInt((String) YearComboBox.getSelectedItem());
      int maxShifts = 0;
      try {
        maxShifts = Integer.parseInt(maxShiftsField.getText());
      } catch (Exception ex) {
//        UtilityBox.getInstance().displayErrorPopup("Max. Schichten", "Ungültige Eingabe für \"maximale Schichten\".");
//        maxShiftsField.setText("");
//        return;
        maxShifts = 0;
      }
      int mentor3 = 0;
      try {
        mentor3 = Integer.parseInt(mentor3rdPosField.getText());
      } catch (Exception ex) {
        mentor3 = 0;
      }
      int mentor2 = 0;
      try {
        mentor2 = Integer.parseInt(mentor2ndPosField.getText());
      } catch (Exception ex) {
        mentor2 = 0;
      }
      calendar.set(year, month, 1);
      int nDaysInMonth = calendar.getMaximum(Calendar.DAY_OF_MONTH);
      ShiftForm.TimeCode[] timeCodes = new ShiftForm.TimeCode[nDaysInMonth];
      String[] comments = new String[nDaysInMonth];
      int iterator = 0;
      for (int i = Math.abs(amountDaysPreviousMonth); i < nDaysInMonth + Math.abs(amountDaysPreviousMonth); i++) {
        // read checked time code fpr each day
        ButtonGroup buttonGroup = allButtonGroups.get(i);
        Enumeration<AbstractButton> buttons = buttonGroup.getElements();
        ShiftForm.TimeCode tempTimeCode = ShiftForm.TimeCode.EMPTY;
        while (buttons.hasMoreElements()) {
          ExtendedJCheckBox ejcb = (ExtendedJCheckBox) buttons.nextElement();
          if (ejcb.isChecked()) {
            tempTimeCode = ejcb.getTimeCode();
            break;
          }
        }
        timeCodes[iterator] = tempTimeCode;
        // read comment
        comments[iterator] = allCommentFields.get(i).getText();
        iterator++;
      }
      ShiftForm.getInstance().createShiftFormPdf(timeCodes, comments, month, year, maxShifts, mentor2, mentor3);
    }
  }

//  class CheckboxListener implements MouseListener {
//
//    public void mouseExited(MouseEvent e) {
//    }
//
//    public void mouseEntered(MouseEvent e) {
//    }
//
//    public void mouseReleased(MouseEvent e) {
//    }
//
//    public void mousePressed(MouseEvent e) {
//    }
//
//    public void mouseClicked(MouseEvent e) {
//      ExtendedJCheckBox source = (ExtendedJCheckBox) e.getSource();
//      for (int i = 0; i < allButtonGroups.size(); i++) {
//        ButtonGroup buttonGroup = allButtonGroups.get(i);
//        Enumeration<AbstractButton> buttons = buttonGroup.getElements();
//        while (buttons.hasMoreElements()) {
//          if (buttons.nextElement().equals(source)) {
//            boolean newSelectStatus = !source.isChecked();
//            setExtendedJCheckboxInButtonGroupToValue(buttonGroup, source, newSelectStatus);
//            return;
//          }
//        }
//      }
//    }
//  }

  /** After click on a checkbox, this method updates the whole group
   * 
   * @param buttonGroup
   * @param checkbox
   * @param value
   */
  private void setExtendedJCheckboxInButtonGroupToValue(ButtonGroup buttonGroup, ExtendedJCheckBox checkbox, boolean value) {
    Enumeration<AbstractButton> buttons = buttonGroup.getElements();
    // iterate over all checkboxes and set the new state to the clicked one
    // and false to all others
    while (buttons.hasMoreElements()) {
    	ExtendedJCheckBox cb = (ExtendedJCheckBox) buttons.nextElement();
        if (cb.equals(checkbox)) {
        	cb.setChecked(value);
        	
        } else {
        	cb.setChecked(false);
        }
    }
    // so some trickery to get the UI checkbox to repaint
    buttonGroup.remove(checkbox);
    checkbox.setChecked(value);
    checkbox.setSelected(value);
    buttonGroup.add(checkbox);
    checkbox.repaint();
  }

  private class CheckboxActionListener implements ActionListener {
//    public CheckboxItemListener() {
//      super();
//    }

    @Override
    public void actionPerformed(ActionEvent e) {
      ExtendedJCheckBox source = (ExtendedJCheckBox) e.getSource();

      for (int i = 0; i < allButtonGroups.size(); i++) {
        ButtonGroup buttonGroup = allButtonGroups.get(i);
        Enumeration<AbstractButton> buttons = buttonGroup.getElements();
        while (buttons.hasMoreElements()) {
          if (buttons.nextElement().equals(source)) {
            boolean newSelectStatus = !source.isChecked();
            setExtendedJCheckboxInButtonGroupToValue(buttonGroup, source, newSelectStatus);
            return;
          }
        }
      }
    }
  }
  
  // this callback is invoked when the selected month changed
  private void monthChangedCallback() {
    addWeeksToPanel(MonthComboBox.getSelectedIndex(), Integer.parseInt((String) YearComboBox.getSelectedItem()), weekContainer);
  }
}
