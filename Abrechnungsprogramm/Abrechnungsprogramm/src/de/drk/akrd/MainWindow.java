package de.drk.akrd;

import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;

import de.drk.akrd.ShiftContainer.ShiftType;
import javax.swing.JButton;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import de.drk.akrd.PersonalData.Qualification;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.border.EtchedBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;

public class MainWindow extends JFrame {
  public static final boolean WACHENVERSION = false;

  private static final long serialVersionUID = 1L;
  // Listeners and Adapters
  private AKRDMouseAdapter mouseAdapter = new AKRDMouseAdapter(this);
  private ItemListener itemListener = new AKRDItemListener(this);
  // Personal Info Components
  private JPanel personalInfoTab = new JPanel();
  // Name Fields
  private final JLabel lblName = new JLabel("Vorname:");
  protected final JTextField firstNameField = new JTextField();
  private final JLabel lblNachname = new JLabel("Nachname:");
  protected final JTextField lastNameField = new JTextField();
  public final JComboBox<PersonalData.Qualification> trainingsChooser = new JComboBox<Qualification>();
  protected final JTextField accountNo = new JTextField();
  protected JTextField blz = new JTextField();
  protected JCheckBox bankInfoKnown = new JCheckBox();
  protected final JTextField bankNameField = new JTextField();
  protected JTextField gMailAdressField = new JTextField();
  protected JTextField calendarIDFiled = new JTextField();
  protected JButton personalInfoApply;
  // Shift Collector Components
  // This int holds the TYPE of day that is currently selected
  // types are: 0 = weekday, 1 = holyday or saturday, 2 = sunday, -1 means not
  // set
  protected int currentlySelectedDay = -1;
  private JPanel shiftEditor = new JPanel();
  protected ShiftContainer shiftContainer = new ShiftContainer(this);
  protected JComboBox<ShiftContainer.ShiftType> shiftTypeChooser = new JComboBox<ShiftType>();
  protected boolean noShiftTypeUpdate = false;
  private final JLabel lblDatum = new JLabel("Datum");
  private final JLabel lblSchichtart = new JLabel("Schichtart");
  protected JTextField dateField;
  protected JTable shiftTable;
  protected DefaultTableModel shiftTableModel = new DefaultTableModel(
          new Object[][]{}, new String[]{"Schichtkürzel", "Beginn",
            "Ende", "Pause"}) {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean isCellEditable(int row, int column) {
      // all cells false
      return false;
    }
  };
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
            "Pause", "Dezimal", "Schichtpartner", "Kommentar",
            "Verdienst"}) {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean isCellEditable(int row, int column) {
      // all cells false
      return false;
    }
  };
  protected JTable registeredShiftsTable;
  private final JLabel lblSchichtpartner = new JLabel("Schichtpartner:");
  protected JTextField shiftPartnerField;
  protected JTextField beginField;
  protected JTextField endField;
  protected JTextField breakField;
  protected JButton submitButton;
  private final JLabel lblKommentar = new JLabel("Kommentar");
  protected JTextField commentField = new JTextField();
  private final JPanel panel = new JPanel();
  protected JCheckBox prepTimeBox;
  protected final JButton createSalaryStatementButton = new JButton();
  protected final JButton deleteRegisteredShiftButton = new JButton("Löschen");
  protected final JButton editRegisteredShiftButton = new JButton("Bearbeiten");

	private final JLabel lblAusbildung = new JLabel("Ausbildung:");
  private final JPanel panel_1 = new JPanel();
  private final JLabel lblBlz = new JLabel("BLZ");
  private final JLabel lblKontonummer = new JLabel("Kontonummer:");
  // DPL Tab
  protected JButton read_DPL;
  protected JButton iCalButton;
  private JTable dplTable;
  private final JLabel lblNameDerBank = new JLabel("Name der Bank:");
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

    bankNameField.setColumns(10);
    lastNameField.setColumns(10);
    accountNo.setColumns(10);
    blz.setColumns(10);
    commentField.setColumns(10);

    // Setup ShiftContainer
    shiftContainer.loadShifts("Schichten.xml");
    shiftContainer.registerShift(ShiftLoadSave.loadSavedShifts(), false);
    // // / TEST ///
     //ShiftFormPane shiftFormPane = new ShiftFormPane();
     //UtilityBox.getInstance().testTime();
    // printtest
    //UtilityBox.getInstance().testStuff();
    //UtilityBox.getInstance().printFile("Abrechnungstest.pdf");
    // isHolidayTest
    // Calendar cal = Calendar.getInstance();
    // cal.set(2011, 10, 1);
    // System.out.println("der erste nov ist feiertag: "+UtilityBox.getInstance().isHoliday(cal.getTime()));
    // shiftformtest
    // ShiftForm.TimeCode[] timeCodes = new ShiftForm.TimeCode[29];
    // for (int i = 0; i < timeCodes.length; i++) {
    // timeCodes[i] = ShiftForm.TimeCode.EMPTY;
    // }
    // timeCodes[5] = ShiftForm.TimeCode.T;
    // timeCodes[10] = ShiftForm.TimeCode.X;
    // timeCodes[11] = ShiftForm.TimeCode.F;
    // timeCodes[17] = ShiftForm.TimeCode.S;
    // timeCodes[28] = ShiftForm.TimeCode.N;
    // ShiftForm.getInstance().createShiftFormPdf(timeCodes, 1, 2012, 5, 3,
    // 1);
    // // personaldatatest
    // PersonalData.setData("Heino", "Meyer", "Privatbank Musterhausen",
    // 1234,
    // 666, PersonalData.Qualification.RA, false);
    // // XMLEditor.writePersonalData(PersonalData.getInstance());
    // boolean loadPersonalData = XMLEditor.loadPersonalData();
//		 ShiftInstance[] shiftsToAccount = new ShiftInstance[5];
//		 int iterator = 0;
//		 for (iterator = 0; iterator < shiftsToAccount.length; iterator++) {
//		 int shiftIndex = (iterator >= shiftContainer.getShifts().length) ? 0
//		 : iterator;
//		 Shift beispiel = shiftContainer.getShifts()[shiftIndex];
//		 shiftsToAccount[iterator] = new ShiftInstance(beispiel.getType(), "01.02.2012", beispiel.getStartingTime(), beispiel.getEndTime(), beispiel.getBreakTime(), "testpartner"+iterator, ""+iterator);
//		 }
//		// PdfCreator.createAccounting(shiftsToAccount);
//
//         ArrayList<ShiftInstance> shifts = new ArrayList<>(shiftsToAccount.length);
//         for (int i = 0; i < shiftsToAccount.length; i++) {
//        ShiftInstance shiftInstance = shiftsToAccount[i];
//        System.out.print("exporttest: "+shiftInstance.getType().toString()+" "+shiftInstance.getComment());
//        shifts.add(shiftInstance);
//      }
//         XMLEditor.exportData(shifts);
//         ArrayList<ShiftInstance> test = XMLEditor.importData();
//         for (Iterator<ShiftInstance> it = test.iterator(); it.hasNext();) {
//        ShiftInstance shiftInstance = it.next();
//        System.out.println("importtest: "+shiftInstance.getType().toString()+" "+shiftInstance.getComment());
//      }
    // // / END TEST ///

    // Set Shift Type Chooser from Enum
    DefaultComboBoxModel<ShiftContainer.ShiftType> enumModel = new DefaultComboBoxModel<ShiftType>(
            ShiftType.values());
    shiftTypeChooser.setModel(enumModel);
    setTitle("AK-RD Abrechnungsprogramm");
    setSize(800, 600); // default size is 0,0
    setLocation(10, 200); // default is 0,0 (top left corner)
    addWindowListener(new WindowClosingAdapter(true));

    JPanel basePanel = new JPanel();
    JLabel logo = new JLabel("bla");
    ImageIcon icon = new ImageIcon("../images/drk_logo.gif", "bla");
    logo.setIcon(icon);
    basePanel.add(logo);

    JTabbedPane tabbedPane = new JTabbedPane();

    // add Actionlistener to tabbedPane
    tabbedPane.addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {
        int selectedTab = ((JTabbedPane) e.getSource()).getSelectedIndex();
        if ((selectedTab == 0)&&(PersonalData.getInstance().isDataSet())) {
          loadPersonalData();
        }
      }
    });
    
    // Assebmle personal Layout

    bankInfoKnown.addItemListener(itemListener);

    tabbedPane.addTab("Persönliche Info", null, personalInfoTab,
            "Persönliche Daten eingeben");

    JPanel panel_2 = new JPanel();
    panel_2.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));

    personalInfoApply = new JButton("Änderungen Übernehmen");
    personalInfoApply.addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent arg0) {
    	}
    });
    personalInfoApply.addMouseListener(mouseAdapter);
    GroupLayout gl_personalInfoTab = new GroupLayout(personalInfoTab);
    gl_personalInfoTab.setHorizontalGroup(gl_personalInfoTab.createParallelGroup(Alignment.LEADING).addGroup(
            gl_personalInfoTab.createSequentialGroup().addGroup(
            gl_personalInfoTab.createParallelGroup(
            Alignment.LEADING).addGroup(
            gl_personalInfoTab.createSequentialGroup().addContainerGap().addGroup(
            gl_personalInfoTab.createParallelGroup(
            Alignment.LEADING).addComponent(
            panel_2,
            GroupLayout.PREFERRED_SIZE,
            768,
            GroupLayout.PREFERRED_SIZE).addGroup(
            gl_personalInfoTab.createSequentialGroup().addComponent(
            panel,
            GroupLayout.PREFERRED_SIZE,
            411,
            GroupLayout.PREFERRED_SIZE).addPreferredGap(
            ComponentPlacement.RELATED).addComponent(
            panel_1,
            GroupLayout.PREFERRED_SIZE,
            352,
            GroupLayout.PREFERRED_SIZE)))).addGroup(
            gl_personalInfoTab.createSequentialGroup().addGap(292).addComponent(
            personalInfoApply))).addContainerGap(
            GroupLayout.DEFAULT_SIZE,
            Short.MAX_VALUE)));
    gl_personalInfoTab.setVerticalGroup(gl_personalInfoTab.createParallelGroup(Alignment.LEADING).addGroup(
            gl_personalInfoTab.createSequentialGroup().addContainerGap().addGroup(
            gl_personalInfoTab.createParallelGroup(
            Alignment.LEADING).addComponent(
            panel_1,
            GroupLayout.PREFERRED_SIZE,
            116,
            GroupLayout.PREFERRED_SIZE).addComponent(
            panel,
            GroupLayout.PREFERRED_SIZE,
            115,
            GroupLayout.PREFERRED_SIZE)).addPreferredGap(
            ComponentPlacement.UNRELATED).addComponent(panel_2,
            GroupLayout.PREFERRED_SIZE,
            102, GroupLayout.PREFERRED_SIZE).addPreferredGap(
            ComponentPlacement.UNRELATED).addComponent(personalInfoApply).addContainerGap(260, Short.MAX_VALUE)));
    panel_1.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));

    JLabel lblNewLabel = new JLabel(
            "<html>Diese Angaben brauchst du nur, wenn du den Dienstplan automatisch in deinen Google Kalender<br>importieren willst.</html>");

    JLabel lblNewLabel_1 = new JLabel("Google Mail Adresse:");

    gMailAdressField.setColumns(10);

    JLabel lblCalendarid = new JLabel("Calendar-ID:");

    calendarIDFiled.setColumns(10);

    JLabel lblNewLabel_2 = new JLabel("");
    lblNewLabel_2.setToolTipText("Die Kalender ID findest du unter Kalender-Einstellungen bei \"Kalenderadresse\"");
    lblNewLabel_2.setIcon(new ImageIcon(
            MainWindow.class.getResource("/com/sun/java/swing/plaf/motif/icons/Question.gif")));
    GroupLayout gl_panel_2 = new GroupLayout(panel_2);
    gl_panel_2.setHorizontalGroup(gl_panel_2.createParallelGroup(Alignment.LEADING).addGroup(
            gl_panel_2.createSequentialGroup().addContainerGap().addGroup(
            gl_panel_2.createParallelGroup(
            Alignment.LEADING).addComponent(
            lblNewLabel,
            GroupLayout.DEFAULT_SIZE,
            740,
            Short.MAX_VALUE).addGroup(
            gl_panel_2.createSequentialGroup().addComponent(
            lblNewLabel_1).addPreferredGap(
            ComponentPlacement.UNRELATED).addComponent(
            gMailAdressField,
            GroupLayout.PREFERRED_SIZE,
            198,
            GroupLayout.PREFERRED_SIZE).addPreferredGap(
            ComponentPlacement.UNRELATED).addComponent(
            lblCalendarid).addPreferredGap(
            ComponentPlacement.RELATED).addComponent(
            lblNewLabel_2).addGap(10).addComponent(
            calendarIDFiled,
            GroupLayout.PREFERRED_SIZE,
            217,
            GroupLayout.PREFERRED_SIZE))).addContainerGap()));
    gl_panel_2.setVerticalGroup(gl_panel_2.createParallelGroup(Alignment.LEADING).addGroup(
            gl_panel_2.createSequentialGroup().addContainerGap().addComponent(lblNewLabel).addGap(13).addGroup(
            gl_panel_2.createParallelGroup(
            Alignment.TRAILING).addGroup(
            gl_panel_2.createParallelGroup(
            Alignment.LEADING).addGroup(
            gl_panel_2.createParallelGroup(
            Alignment.BASELINE).addComponent(
            lblNewLabel_1).addComponent(
            gMailAdressField,
            GroupLayout.PREFERRED_SIZE,
            GroupLayout.DEFAULT_SIZE,
            GroupLayout.PREFERRED_SIZE)).addGroup(
            gl_panel_2.createParallelGroup(
            Alignment.BASELINE).addComponent(
            lblCalendarid).addComponent(
            calendarIDFiled,
            GroupLayout.PREFERRED_SIZE,
            GroupLayout.DEFAULT_SIZE,
            GroupLayout.PREFERRED_SIZE))).addComponent(
            lblNewLabel_2)).addContainerGap(19, Short.MAX_VALUE)));
    panel_2.setLayout(gl_panel_2);
    panel_1.setLayout(new FormLayout(new ColumnSpec[]{
              FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
              FormFactory.RELATED_GAP_COLSPEC,
              ColumnSpec.decode("default:grow"),}, new RowSpec[]{
              FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
              FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
              FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
              FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,}));
    bankInfoKnown.setText("Bankdaten bekannt");

    panel_1.add(bankInfoKnown, "2, 2");

    panel_1.add(lblBlz, "2, 4, right, default");

    panel_1.add(blz, "4, 4, fill, default");

    panel_1.add(lblKontonummer, "2, 6, right, default");

    panel_1.add(accountNo, "4, 6, fill, default");

    panel_1.add(lblNameDerBank, "2, 8, right, default");

    panel_1.add(bankNameField, "4, 8, fill, default");
    panel.setLayout(new FormLayout(new ColumnSpec[]{
              FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
              FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
              FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC,
              FormFactory.RELATED_GAP_COLSPEC,
              ColumnSpec.decode("default:grow"),}, new RowSpec[]{
              FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
              FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
              FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
              FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,}));

    panel.add(lblName, "2, 4");
    firstNameField.setColumns(10);

    panel.add(firstNameField, "6, 4, 3, 1, fill, default");

    panel.add(lblNachname, "2, 6");

    panel.add(lastNameField, "6, 6, 3, 1, fill, default");

    panel.add(lblAusbildung, "2, 8");
    trainingsChooser.setModel(new DefaultComboBoxModel<Qualification>(Qualification.values()));
    panel.add(trainingsChooser, "6, 8, 3, 1");
    
    personalInfoTab.setLayout(gl_personalInfoTab);
    

    // Shift Collector

    tabbedPane.addTab("Schichten", null, shiftEditor,
            "Schichten eingeben und bearbeiten");

    JScrollPane scrollPane = new JScrollPane();
    //scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
    JScrollPane scrollPane2 = new JScrollPane();

    dateField = new JTextField();
    dateField.setText("bitte auswählen");
    dateField.addMouseListener(mouseAdapter);
    dateField.setEditable(false);
    dateField.setColumns(10);

    registeredShiftsTable = new JTable();
    registeredShiftsTable.getTableHeader().setReorderingAllowed(false);
    registeredShiftsTable.getTableHeader().setResizingAllowed(false);

    shiftPartnerField = new JTextField();
    shiftPartnerField.setColumns(10);

    JLabel lblBeginn = new JLabel("Beginn:");

    beginField = new JTextField();
    beginField.setColumns(10);

    JLabel lblEnde = new JLabel("Ende:");

    endField = new JTextField();
    endField.setColumns(10);

    JLabel lblPause = new JLabel("Pause:");

    breakField = new JTextField();
    breakField.setColumns(10);

    submitButton = new JButton("Eintragen");
    submitButton.addMouseListener(mouseAdapter);
    createSalaryStatementButton.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent arg0) {
      }
    });

    createSalaryStatementButton.setText("Abrechnung erstellen");
    createSalaryStatementButton.addMouseListener(mouseAdapter);

    prepTimeBox = new JCheckBox("10 min Rüstzeit");
    
    deleteRegisteredShiftButton.addMouseListener(mouseAdapter);
    editRegisteredShiftButton.addMouseListener(mouseAdapter);
    
    

    GroupLayout gl_shiftEditor = new GroupLayout(shiftEditor);
    gl_shiftEditor.setHorizontalGroup(
    	gl_shiftEditor.createParallelGroup(Alignment.LEADING)
    		.addGroup(gl_shiftEditor.createSequentialGroup()
    			.addGroup(gl_shiftEditor.createParallelGroup(Alignment.LEADING)
    				.addGroup(gl_shiftEditor.createSequentialGroup()
    					.addContainerGap()
    					.addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 781, Short.MAX_VALUE))
    				.addGroup(gl_shiftEditor.createSequentialGroup()
    					.addGap(1)
    					.addGroup(gl_shiftEditor.createParallelGroup(Alignment.LEADING)
    						.addGroup(gl_shiftEditor.createSequentialGroup()
    							.addGroup(gl_shiftEditor.createParallelGroup(Alignment.LEADING)
    								.addComponent(lblSchichtart, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)
    								.addComponent(lblDatum, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE))
    							.addPreferredGap(ComponentPlacement.RELATED)
    							.addGroup(gl_shiftEditor.createParallelGroup(Alignment.LEADING, false)
    								.addComponent(shiftTypeChooser, 0, 0, Short.MAX_VALUE)
    								.addComponent(dateField)))
    						.addGroup(gl_shiftEditor.createSequentialGroup()
    							.addComponent(lblSchichtpartner)
    							.addPreferredGap(ComponentPlacement.RELATED)
    							.addComponent(shiftPartnerField, GroupLayout.PREFERRED_SIZE, 137, GroupLayout.PREFERRED_SIZE))
    						.addGroup(gl_shiftEditor.createSequentialGroup()
    							.addPreferredGap(ComponentPlacement.RELATED)
    							.addGroup(gl_shiftEditor.createParallelGroup(Alignment.LEADING)
    								.addGroup(gl_shiftEditor.createSequentialGroup()
    									.addGroup(gl_shiftEditor.createParallelGroup(Alignment.TRAILING)
    										.addGroup(gl_shiftEditor.createSequentialGroup()
    											.addComponent(lblPause)
    											.addGap(13))
    										.addGroup(gl_shiftEditor.createSequentialGroup()
    											.addComponent(lblBeginn)
    											.addPreferredGap(ComponentPlacement.UNRELATED)))
    									.addGroup(gl_shiftEditor.createParallelGroup(Alignment.LEADING, false)
    										.addComponent(breakField, 0, 0, Short.MAX_VALUE)
    										.addComponent(beginField, GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE))
    									.addGap(18)
    									.addGroup(gl_shiftEditor.createParallelGroup(Alignment.TRAILING)
    										.addGroup(gl_shiftEditor.createSequentialGroup()
    											.addComponent(lblEnde)
    											.addPreferredGap(ComponentPlacement.UNRELATED)
    											.addComponent(endField, GroupLayout.PREFERRED_SIZE, 69, GroupLayout.PREFERRED_SIZE)
    											.addGap(6))
    										.addComponent(prepTimeBox)))
    								.addGroup(gl_shiftEditor.createSequentialGroup()
    									.addComponent(lblKommentar)
    									.addGroup(gl_shiftEditor.createParallelGroup(Alignment.LEADING)
    										.addGroup(gl_shiftEditor.createSequentialGroup()
    											.addGap(33)
    											.addComponent(submitButton))
    										.addGroup(gl_shiftEditor.createSequentialGroup()
    											.addGap(21)
    											.addComponent(commentField, GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)))))))
    					.addGap(17)
    					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
    				.addGroup(gl_shiftEditor.createSequentialGroup()
    					.addGap(284)
    					.addComponent(createSalaryStatementButton)
    					.addPreferredGap(ComponentPlacement.RELATED, 100, Short.MAX_VALUE)
    					.addComponent(editRegisteredShiftButton)
    					.addPreferredGap(ComponentPlacement.UNRELATED)
    					.addComponent(deleteRegisteredShiftButton)
    					.addPreferredGap(ComponentPlacement.RELATED)))
    			.addGap(0))
    );
    gl_shiftEditor.setVerticalGroup(
    	gl_shiftEditor.createParallelGroup(Alignment.LEADING)
    		.addGroup(gl_shiftEditor.createSequentialGroup()
    			.addContainerGap()
    			.addGroup(gl_shiftEditor.createParallelGroup(Alignment.LEADING)
    				.addGroup(gl_shiftEditor.createSequentialGroup()
    					.addGroup(gl_shiftEditor.createParallelGroup(Alignment.BASELINE)
    						.addComponent(lblSchichtart)
    						.addComponent(shiftTypeChooser, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
    					.addPreferredGap(ComponentPlacement.RELATED)
    					.addGroup(gl_shiftEditor.createParallelGroup(Alignment.BASELINE)
    						.addComponent(lblDatum)
    						.addComponent(dateField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
    					.addPreferredGap(ComponentPlacement.RELATED)
    					.addGroup(gl_shiftEditor.createParallelGroup(Alignment.LEADING)
    						.addComponent(lblSchichtpartner)
    						.addComponent(shiftPartnerField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
    					.addPreferredGap(ComponentPlacement.RELATED)
    					.addGroup(gl_shiftEditor.createParallelGroup(Alignment.TRAILING)
    						.addGroup(gl_shiftEditor.createParallelGroup(Alignment.BASELINE)
    							.addComponent(lblEnde)
    							.addComponent(endField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
    						.addGroup(gl_shiftEditor.createSequentialGroup()
    							.addGroup(gl_shiftEditor.createParallelGroup(Alignment.BASELINE)
    								.addComponent(beginField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
    								.addComponent(lblBeginn))
    							.addGap(2)))
    					.addPreferredGap(ComponentPlacement.UNRELATED)
    					.addGroup(gl_shiftEditor.createParallelGroup(Alignment.BASELINE)
    						.addComponent(lblPause)
    						.addComponent(breakField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
    						.addComponent(prepTimeBox))
    					.addPreferredGap(ComponentPlacement.UNRELATED)
    					.addGroup(gl_shiftEditor.createParallelGroup(Alignment.BASELINE)
    						.addComponent(lblKommentar)
    						.addComponent(commentField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
    					.addPreferredGap(ComponentPlacement.UNRELATED)
    					.addComponent(submitButton))
    				.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE))
    			.addPreferredGap(ComponentPlacement.UNRELATED)
    			.addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 258, GroupLayout.PREFERRED_SIZE)
    			.addPreferredGap(ComponentPlacement.RELATED)
    			.addGroup(gl_shiftEditor.createParallelGroup(Alignment.BASELINE)
    				.addComponent(createSalaryStatementButton)
    				.addComponent(deleteRegisteredShiftButton)
    				.addComponent(editRegisteredShiftButton))
    			.addContainerGap(24, Short.MAX_VALUE))
    );
    shiftTypeChooser.addItemListener(this.itemListener);

    shiftTable = new JTable();
    shiftTable.getTableHeader().setReorderingAllowed(false);
    shiftTable.getTableHeader().setResizingAllowed(false);
    shiftTable.addMouseListener(mouseAdapter);
    shiftTable.setShowGrid(false);
    Object[][] data = ShiftContainer.shiftToTableData(shiftContainer.getShifts());
    for (int i = 0; i < data.length; i++) {
      shiftTableModel.addRow(data[i]);
    }
    shiftTable.setModel(shiftTableModel);
    registeredShiftsTable.setModel(registeredShiftsTableModel);
    scrollPane.setViewportView(shiftTable);
    scrollPane2.setViewportView(registeredShiftsTable);
    shiftEditor.setLayout(gl_shiftEditor);

    // basePanel.add(tabbedPane);
    getContentPane().add(tabbedPane);

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

  }

  public static void main(String[] args) {

    MainWindow f = new MainWindow();
    f.setVisible(false);
    // set Frame-icon
    Image drkIcon = Toolkit.getDefaultToolkit().getImage(f.getClass().getResource("icon.jpg"));
    f.setIconImage(drkIcon);
    f.setVisible(true);
    f.loadPersonalData();

  }

  /**
   * Gets the current day and shift type (for filter options) and updated the
   * Shift container
   * 
   * @author niklas
   */
  public void updateShiftContainer() {
    // Get currently selected shift type
    ShiftType type = (ShiftType) shiftTypeChooser.getSelectedItem();

    Object[][] data = ShiftContainer.shiftToTableData(shiftContainer.filterShifts(type, currentlySelectedDay));
    shiftTableModel.setNumRows(0);
    for (int i = 0; i < data.length; i++) {
      shiftTableModel.addRow(data[i]);
    }

  }

  public void updateRegisteredShifts() {

    float completeSalary = -1;

    Object[][] data = ShiftContainer.shiftInstancesToTableData((ShiftInstance[]) shiftContainer.shiftInstances.toArray(new ShiftInstance[shiftContainer.shiftInstances.size()]));
    registeredShiftsTableModel.setNumRows(0);
    for (int i = 0; i < data.length; i++) {
      ArrayList<Object> list = new ArrayList<Object>();
      Collections.addAll(list, data[i]);
      // TODO: nicht 8.8f benutzen, sondern anpassen
      ShiftInstance currentShift = shiftContainer.shiftInstances.get(i);
      float salary = UtilityBox.getInstance().calculateSalary(currentShift) * currentShift.getTimeAsFloat();
      completeSalary += salary;
      list.add(String.format("%.2f", salary) + "€");
      registeredShiftsTableModel.addRow(list.toArray());
    }
    // if complete salary is 0 display error message
    if(completeSalary == 0)
    {
    	UtilityBox.getInstance().displayErrorPopup("Fehler", "Es wurden keine gültigen Personendaten gefunden und der Lohn konnte nicht berechnet werden. Bitte trage diese neu ein!");
    }
    // if complete salary is now still -1 no shifts are present and it can become 0
    if (completeSalary == -1) completeSalary = 0;
    Object[] lastLine = new Object[]{"", "", "", "", "", "", "Gesamt",
      String.format("%.2f", completeSalary) + "€"};
    registeredShiftsTableModel.addRow(lastLine);

  }

  public void loadPersonalData() {
    // Try to load personal data and fill the fields
    PersonalData pd = null;
    try {
      pd = PersonalData.getInstance();
    } catch (Exception e) {
      UtilityBox.getInstance().displayErrorPopup("Fehler",
              "Fehler beim Laden der persönlichen Daten");
    }
    if (pd != null) {
      firstNameField.setText(pd.getFirstName());
      trainingsChooser.setSelectedItem(pd.getQualification());
      blz.setText(Integer.toString(pd.getBlz()));
      accountNo.setText(Integer.toString(pd.getAccountNumber()));
      bankInfoKnown.setSelected(pd.isDataKnown());
      lastNameField.setText(pd.getLastName());
      gMailAdressField.setText(pd.getEmailAdress());
      calendarIDFiled.setText(pd.getCalendarId());
      bankNameField.setText(pd.getBankNameAndCity());
    }
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
