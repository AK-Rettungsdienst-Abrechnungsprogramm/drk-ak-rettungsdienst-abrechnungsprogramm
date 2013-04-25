package de.drk.akrd;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;

import javax.swing.JButton;
import java.util.Calendar;
import java.util.Date;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;

public class MainWindow extends JFrame {
  public static final boolean WACHENVERSION = false;
  public static final float PROGRAM_VERSION = 0.8f;
  public static float SHIFT_FILE_VERSION = 0f;

  private static final long serialVersionUID = 1L;
  // Listeners and Adapters
  private AKRDMouseAdapter mouseAdapter = new AKRDMouseAdapter(this);

  protected ShiftContainer shiftContainer = new ShiftContainer(this);
  
  
  protected JLabel statusBar = new JLabel("Rufe aktuelle Version aus dem Internet ab...");

  
  protected DefaultTableModel dplTableModel = new DefaultTableModel(
          new Object[][]{}, new String[]{"Datum", "Schichtkürzel",
            "Beginn", "Ende"}) {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean isCellEditable(int row, int column) {
      // all cells false
      return false;
    }
  };
  protected DefaultTableModel registeredShiftsTableModel = new DefaultTableModel(
          new Object[][]{}, new String[]{"Datum", "Beginn", "Ende",
            "Pause", "Dezimal", "Schichtpartner", "Kommentar","Fahrtkosten",
            "Verdienst"}) {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean isCellEditable(int row, int column) {
      // all cells false
      return false;
    }
  };
  
  // DPL Tab
  protected JButton read_DPL;
  protected JButton iCalButton;
  private JTable dplTable;
  protected final JButton googleCalButton = new JButton(
          "Dienste in Google Kalender eintragen");

  // DPL Fragebogen tab
  private final JPanel dplSurvey = new JPanel();
  private final JPanel importExportTab = new JPanel();
  private JButton importExportFileChooseButton;
  // Import/Export tab
  private final ImportExport importExport = ImportExport.GetInstance(this);
  private JComboBox exportMonthComboBox = new JComboBox();
  private JComboBox exportYearComboBox = new JComboBox();
  private final DefaultTableModel importExportDisplayTableModel = new DefaultTableModel(
            new Object[][]{}, new String[]{"Tag, Datum", "von - bis",
              "Schichttyp", "Schichtpartner"}) {

      private static final long serialVersionUID = 1L;

      @Override
      public boolean isCellEditable(int row, int column) {
        // all cells false
        return false;
      }
    };
  private final JTable importExportDisplayTable = new JTable(importExportDisplayTableModel);
  private final JScrollPane importExportShiftPane = new JScrollPane(importExportDisplayTable);
  
  public MainWindow() {

    // Instanciate UtilityBox
    UtilityBox.instanciate(this);
    
    // Setup ShiftContainer
    shiftContainer.loadShifts("Schichten.xml");
    shiftContainer.registerShifts(ShiftLoadSave.loadSavedShifts(), false);

    setTitle("AK-RD Abrechnungsprogramm");
    getContentPane().setPreferredSize(new Dimension (900, 600)); // default size is 0,0
    pack();
    setLocation(10, 200);
    addWindowListener(new WindowClosingAdapter(true));

    JPanel basePanel = new JPanel();
    JLabel logo = new JLabel("bla");
    ImageIcon icon = new ImageIcon("../images/drk_logo.gif", "bla");
    logo.setIcon(icon);
    basePanel.add(logo);

    JTabbedPane tabbedPane = new JTabbedPane();

    PersonalInfoTab personalDataTab = new PersonalInfoTab();
    
    tabbedPane.addTab("Persönliche Daten", null, personalDataTab, "Persönliche Daten eingeben");    

    // Shift Collector
    ShiftCollectorTab sc = new ShiftCollectorTab(shiftContainer);
    tabbedPane.addTab("Schichten", null, sc, "Schichten eingeben und bearbeiten");


    // basePanel.add(tabbedPane);
    getContentPane().setLayout(null);
    tabbedPane.setBounds(0, 0, 900, 585);
    getContentPane().add(tabbedPane);
    statusBar.setBounds(0, 585,900, 15);
    
    getContentPane().add(statusBar);
    
    JPanel DPL_Tab = new JPanel();
    tabbedPane.addTab("Dienstplan auslesen", null, DPL_Tab, null);

    JLabel DPLLabel = new JLabel(
            "<html>Hier kannst du den Monatsdienstplan automatisch auslesen lassen,<br/>um eine Übersicht über deine Schichten zu bekommen<br/>\nAußerdem kannst du deine Dienste gleich in einen Google Calender eintragen.</html>");

    read_DPL = new JButton("Dienstplan auslesen");
    read_DPL.addMouseListener(mouseAdapter);

    JScrollPane DPLScrollPane = new JScrollPane();

    dplTable = new JTable();
    dplTable.getTableHeader().setReorderingAllowed(false);
    dplTable.getTableHeader().setResizingAllowed(false);
    DPLScrollPane.setViewportView(dplTable);

    iCalButton = new JButton("iCal Datei erstellen");
    iCalButton.addMouseListener(this.mouseAdapter);
    googleCalButton.addMouseListener(mouseAdapter);

    GroupLayout gl_DPL_Tab = new GroupLayout(DPL_Tab);
    gl_DPL_Tab.setHorizontalGroup(gl_DPL_Tab.createParallelGroup(Alignment.LEADING).addGroup(
            gl_DPL_Tab.createSequentialGroup().addContainerGap().addGroup(
            gl_DPL_Tab.createParallelGroup(
            Alignment.LEADING).addComponent(
            DPLLabel,
            GroupLayout.PREFERRED_SIZE,
            760,
            GroupLayout.PREFERRED_SIZE).addComponent(read_DPL).addComponent(
            DPLScrollPane,
            GroupLayout.PREFERRED_SIZE,
            543,
            GroupLayout.PREFERRED_SIZE).addGroup(
            gl_DPL_Tab.createSequentialGroup().addComponent(
            iCalButton).addGap(41).addComponent(
            googleCalButton))).addContainerGap(21, Short.MAX_VALUE)));
    gl_DPL_Tab.setVerticalGroup(gl_DPL_Tab.createParallelGroup(
            Alignment.LEADING).addGroup(
            gl_DPL_Tab.createSequentialGroup().addContainerGap().addComponent(DPLLabel).addGap(18).addComponent(read_DPL).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(DPLScrollPane,
            GroupLayout.PREFERRED_SIZE, 135,
            GroupLayout.PREFERRED_SIZE).addGap(18).addGroup(
            gl_DPL_Tab.createParallelGroup(
            Alignment.BASELINE).addComponent(iCalButton).addComponent(googleCalButton)).addContainerGap(249, Short.MAX_VALUE)));
    DPL_Tab.setLayout(gl_DPL_Tab);
    dplTable.setModel(dplTableModel);
    
    
    // Add Schichtenabgabe pane
    dplSurvey.setLayout(null);
    ShiftFormTab sft = new ShiftFormTab(dplSurvey);
    tabbedPane.addTab("DPL Fragebogen", null, dplSurvey,
            "Dienstplan Fragebogen ausfüllen");

    // Import/export pane

    tabbedPane.addTab("Import / Export", null, importExportTab, null);
    importExportTab.setLayout(null);
    // change listener to update selectable years for the export
    tabbedPane.addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {
        if (((JTabbedPane)e.getSource()).getSelectedComponent() == importExportTab) {
          exportYearComboBox.setModel(new DefaultComboBoxModel(importExport.getYearStrings()));
          importExport.setSelected(importExportDisplayTableModel, exportMonthComboBox.getSelectedIndex(), exportYearComboBox.getSelectedIndex());
        }
      }
    });
    
    importExportDisplayTable.getTableHeader().setReorderingAllowed(false);
    importExportDisplayTable.getTableHeader().setResizingAllowed(false);
    importExportShiftPane.setBounds(295, 68, 452, 352);
    importExportShiftPane.setBorder(new TitledBorder(null, "ausgewählte Schichten", TitledBorder.LEADING, TitledBorder.TOP, null, null));
    importExportTab.add(importExportShiftPane);
    
    JPanel exportPanel = new JPanel();
    exportPanel.setBorder(new TitledBorder(null, "Import / Export - Schichten", TitledBorder.LEADING, TitledBorder.TOP, null, null));
    exportPanel.setBounds(10, 68, 267, 154);
    importExportTab.add(exportPanel);
    exportPanel.setLayout(null);
    
    ActionListener setSelectetImportExportShifts = new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        importExportShiftPane.setBorder(new TitledBorder(null, "zu exportierende Schichten", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        importExport.setSelected(importExportDisplayTableModel ,exportMonthComboBox.getSelectedIndex(), exportYearComboBox.getSelectedIndex());
      }
    };
    exportMonthComboBox.setModel(new DefaultComboBoxModel(new String[]{"" ,"Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"}));
    exportMonthComboBox.setBounds(10, 19, 105, 20);
    exportMonthComboBox.addActionListener(setSelectetImportExportShifts);
    exportPanel.add(exportMonthComboBox);
    
    exportYearComboBox.setModel(new DefaultComboBoxModel(importExport.getYearStrings()));
    exportYearComboBox.setBounds(147, 19, 90, 20);
    exportYearComboBox.addActionListener(setSelectetImportExportShifts);
    exportYearComboBox.setSelectedIndex(0);
    exportPanel.add(exportYearComboBox);
    
    JButton exportButton = new JButton("Exportieren");
    exportButton.setBounds(10, 50, 247, 93);
    exportButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        boolean suc = importExport.exportSelected();
          importExportShiftPane.setBorder(new TitledBorder(null, "ausgewählte Schichten", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      }
    });
    exportPanel.add(exportButton);
    
    JPanel importPanel = new JPanel();
    importPanel.setBorder(new TitledBorder(null, "Importieren", TitledBorder.LEADING, TitledBorder.TOP, null, null));
    importPanel.setBounds(10, 266, 267, 154);
    importExportTab.add(importPanel);
    importPanel.setLayout(null);
    
    importExportFileChooseButton = new JButton();
    importExportFileChooseButton.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent arg0) {
        String filePath = importExport.selectImportFile(importExportDisplayTableModel);
        if (filePath != null && filePath.length() > 0) {
          importExportFileChooseButton.setText(filePath);
          importExportFileChooseButton.setToolTipText(filePath);
          importExportShiftPane.setBorder(new TitledBorder(null, filePath, TitledBorder.LEADING, TitledBorder.TOP, null, null));
        }
      }
    });
    importExportFileChooseButton.setText("Datei wählen...");
    importExportFileChooseButton.setBounds(10, 19, 247, 20);
    importPanel.add(importExportFileChooseButton);
    
    JButton importButton = new JButton("Importieren");
    importButton.setBounds(10, 50, 247, 93);
    importButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        boolean suc = importExport.importSelected();
        if(suc) {
          importExportShiftPane.setBorder(new TitledBorder(null, "ausgewählte Schichten", TitledBorder.LEADING, TitledBorder.TOP, null, null));
          // update selectable years
          exportYearComboBox.setModel(new DefaultComboBoxModel(importExport.getYearStrings()));
        }
      }
    });
    importPanel.add(importButton);
    importExport.setSelected(importExportDisplayTableModel, 0, exportYearComboBox.getSelectedIndex());

    // add info / update-tab
    InfoUpdateTab infoUpdateTab = new InfoUpdateTab();
    tabbedPane.addTab("Info / Update", null, infoUpdateTab);
  }

  public static void main(String[] args) {

    MainWindow f = new MainWindow();
    f.setVisible(false);
    // set Frame-icon
    Image drkIcon = Toolkit.getDefaultToolkit().getImage(f.getClass().getResource("icon.jpg"));
    f.setIconImage(drkIcon);
    f.setVisible(true);
    f.setResizable(true);
  }


  protected void updateShiftsFromDPL() {
    Shift[] shifts = DRManager.GetInstance().getSavedShifts();
    Date[] shiftDates = DRManager.GetInstance().getSavedShiftDates();

    dplTableModel.setNumRows(0);

    Calendar cal = Calendar.getInstance();
    
    // if parsing failed return
    if ((shifts == null) || (shiftDates == null)) {
      return;
    }
    for (int i = 0; i < shifts.length; i++) {
      // Assemble date string
      String startingTime = UtilityBox.createTimeStringFromInt(shifts[i].getStartingTime());
      String endTime = UtilityBox.createTimeStringFromInt(shifts[i].getEndTime());
      cal.setTime(shiftDates[i]);
      String dateString = Integer.toString(cal.get(Calendar.DAY_OF_MONTH))
              + "."
              + Integer.toString(cal.get(Calendar.MONTH)+1)
              + "."
              + Integer.toString(cal.get(Calendar.YEAR));
      Object[] entry = new Object[]{dateString, shifts[i].getId(),
        startingTime, endTime};
      dplTableModel.addRow(entry);

    }

  }
}
