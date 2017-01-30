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
  public static final float PROGRAM_VERSION = 1.4f;
  public static float SHIFT_FILE_VERSION = 0f;
  public static float SALARY_FILE_VERSION = 0f;

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
  
  // Shift collector tab
  protected ShiftCollectorTab sc;

  // DPL Fragebogen tab

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
    // this also checks the latest file- and program versions
    UtilityBox.instanciate(this);
    //read existing file versions
    Update.readFileVersions();
    // Setup ShiftContainer
    shiftContainer.loadShifts("Schichten.xml");
    shiftContainer.registerShifts(ShiftLoadSave.loadSavedShifts(), false);

    if (WACHENVERSION) {
      setTitle("AK-RD Abrechnungsprogramm  -  Wachenversion");
    } else {
      setTitle("AK-RD Abrechnungsprogramm");
    }
    getContentPane().setPreferredSize(new Dimension (900, 600)); // default size is 0,0
    pack();
    // place the window in the middle of the screen
    Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
    Dimension windowSize = this.getSize();
    int xPos = screenSize.width/2 - windowSize.width/2;
    setLocation(xPos, 50);
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
    sc = new ShiftCollectorTab(shiftContainer);
    tabbedPane.addTab("Schichten", null, sc, "Schichten eingeben und bearbeiten");


    // basePanel.add(tabbedPane);
    getContentPane().setLayout(null);
    tabbedPane.setBounds(0, 0, 900, 585);
    getContentPane().add(tabbedPane);
    statusBar.setBounds(0, 585,900, 15);
    
    getContentPane().add(statusBar);
    
    
    // Add Schichtenabgabe pane
    ShiftFormTab sft = new ShiftFormTab();
    tabbedPane.addTab("DPL Fragebogen", null, sft,
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

//    // add statistics tab
//    StatisticTab statisticTab = new StatisticTab();
//    tabbedPane.addTab("Statistik", statisticTab);
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
    f.setResizable(false);
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
