package de.drk.akrd;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import de.drk.akrd.ShiftContainer.ShiftType;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.Iterator;

public class MainWindow extends JFrame{

	// Listeners and Adapters
	private MouseAdapter mouseAdapter = new AKRDMouseAdapter(this);
	private ItemListener itemListener = new AKRDItemListener(this);
	
	// Personal Info Components
	private JPanel personalInfoTab = new JPanel();
	private String[] trainingStrings = { "Rettungshelfer", "Rettungssanitäter",
			"Rettungsassistent" };
	private JComboBox trainingsChooser = new JComboBox(trainingStrings);
	protected JTextField accountNo = new JTextField();
	protected JTextField blz = new JTextField();
	protected JCheckBox bankInfoKnown = new JCheckBox();

	// Shift Collector Components
	private JPanel shiftCollector = new JPanel();
	protected ShiftContainer shiftContainer = new ShiftContainer();
	
	protected JComboBox shiftTypeChooser = new JComboBox();
	private final JLabel lblDatum = new JLabel("Datum");
	private final JLabel lblSchichtart = new JLabel("Schichtart");
	protected JTextField dateField;
	protected JTable shiftTable;
	protected JButton calendarButton = new JButton("Kalender");
	protected DefaultTableModel shiftTableModel = new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Schichtkürzel", "Beginn", "Ende", "Pause"
			}
		);
	
	public MainWindow() {
		
      // Setup ShiftContainer
		shiftContainer.loadShifts("Schichten.xml");
		/// TEST ///
      //personaldatatest
      PersonalData.setData("Heino", "Meyer", "Privatbank Musterhausen", 1234, 666, PersonalData.Qualification.RA, false);
      //XMLEditor.writePersonalData(PersonalData.getInstance());
      boolean loadPersonalData = XMLEditor.loadPersonalData();
      ShiftInstance[] shiftsToAccount = new ShiftInstance[46];
      
      int iterator =0;
      for(iterator=0;iterator<46; iterator++){
        int shiftIndex = (iterator>= shiftContainer.getShifts().length)? 0: iterator;
        Shift beispiel = shiftContainer.getShifts()[shiftIndex];
        shiftsToAccount[iterator] = new ShiftInstance(beispiel, "30.05.2012", 5f, "heino"+iterator, ""+iterator);
      }
      PdfCreator.createAccounting(shiftsToAccount);
      /*
      SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
      Calendar cal = Calendar.getInstance();
      Date date = cal.getTime();
    try {
      date = sdf.parse("30.05.2012");
      System.out.println("dateToString: "+date.toString());
      cal.setTime(date);
      System.out.println("cal.getDay: "+cal.get(Calendar.DAY_OF_MONTH));
      System.out.println("cal.getMonth: "+cal.get(Calendar.MONTH));
      System.out.println("cal.getYear: "+cal.get(Calendar.YEAR));
      date = sdf.parse("01.01.2012");
      System.out.println("dateToString: "+date.toString());
      cal.setTime(date);
      System.out.println("cal.getDay: "+cal.get(Calendar.DAY_OF_MONTH));
      System.out.println("cal.getMonth: "+cal.get(Calendar.MONTH));
      System.out.println("cal.getYear: "+cal.get(Calendar.YEAR));
      date = sdf.parse("31.12.2012");
      System.out.println("dateToString: "+date.toString());
      cal.setTime(date);
      System.out.println("cal.getDay: "+cal.get(Calendar.DAY_OF_MONTH));
      System.out.println("cal.getMonth: "+cal.get(Calendar.MONTH));
      System.out.println("cal.getYear: "+cal.get(Calendar.YEAR));
    } catch (ParseException ex) {
      ex.printStackTrace();
      System.out.println("das war wohl nichts");
    }
      */
      ArrayList<ShiftInstance> testList = new ArrayList<>();
      for (int it=0; it<shiftsToAccount.length; it++){
        //testList.add(shiftsToAccount[it]);
      }
      Shift beispiel = shiftContainer.getShifts()[0];
      testList.add(new ShiftInstance(beispiel, "30.05.2012", 5f, "musterfrau?", "name geändert"));
      beispiel = shiftContainer.getShifts()[2];
      testList.add(new ShiftInstance(beispiel, "06.06.2012", 5f, "horst5", "war auch super!"));
      //XMLEditor.storeShifts(testList, 2012);
      ArrayList<ArrayList<ShiftInstance>> schichten = XMLEditor.loadSavedShifts(2012);
      for (Iterator<ArrayList<ShiftInstance>> it = schichten.iterator(); it.hasNext();) {
        ArrayList<ShiftInstance> arrayList = it.next();
        for (Iterator<ShiftInstance> it1 = arrayList.iterator(); it1.hasNext();) {
          ShiftInstance shiftInstance = it1.next();
          System.out.println("schicht geladen: "+shiftInstance.getDate()+" "+shiftInstance.getId());
        }
      }
      /// END TEST ///
      
      // Set Shift Type Chooser from Enum
      ComboBoxModel enumModel = new DefaultComboBoxModel(ShiftType.values());
      shiftTypeChooser.setModel(enumModel);
		setTitle("AK-RD Abrechnungsprogramm");
		setSize(562, 367); // default size is 0,0
		setLocation(10, 200); // default is 0,0 (top left corner)
		addWindowListener(new WindowClosingAdapter(true));
	
		JPanel basePanel = new JPanel();
		JLabel logo = new JLabel("bla");
		ImageIcon icon = new ImageIcon("../images/drk_logo.gif", "bla");
		logo.setIcon(icon);
		basePanel.add(logo);
	
		JTabbedPane tabbedPane = new JTabbedPane();
	
		// Assebmle personal Layout
		SpringLayout personalLayout = new SpringLayout();
		personalInfoTab.setLayout(personalLayout);
		JTextField name = new JTextField(20);
		JLabel nameLabel = new JLabel("Name");
		personalInfoTab.add(nameLabel);
		personalInfoTab.add(name);
		personalInfoTab.add(new JLabel("Ausbildung"));
		personalInfoTab.add(trainingsChooser);
		trainingsChooser
				.setToolTipText("Wähle hier deine Ausbildung aus. Das ist wichtig für die Tarifbestimmung.");
		personalInfoTab.add(new JSeparator(JSeparator.HORIZONTAL));
		personalInfoTab.add(new JSeparator(JSeparator.HORIZONTAL));
		bankInfoKnown.addItemListener(this.itemListener);
		personalInfoTab.add(new JLabel("Bankdaten bekannt"));
		bankInfoKnown
				.setToolTipText("Wenn deine Bankdaten der Personalabteilung bereits vorliegen, brauchst du sie nicht nochmal angeben.");
		personalInfoTab.add(bankInfoKnown);
		personalInfoTab.add(new JLabel("Kontonummer:"));
		personalInfoTab.add(accountNo);
		personalInfoTab.add(new JLabel("BLZ:"));
		personalInfoTab.add(blz);
	
		SpringUtilities.makeCompactGrid(personalInfoTab,
				personalInfoTab.getComponentCount() / 2, 2, // rows, cols
				6, 10, // initX, initY
				6, 6); // xPad, yPad
		tabbedPane.addTab("Persönliche Info", null, personalInfoTab,
				"Persönliche Daten eingeben");
		tabbedPane.addTab("Schichten", null, shiftCollector,
				"Schichten eingeben und bearbeiten");
		
		JScrollPane scrollPane = new JScrollPane();
		
		dateField = new JTextField();
		dateField.setEditable(false);
		dateField.setColumns(10);
		calendarButton.addMouseListener(mouseAdapter);
		
		calendarButton.addItemListener(this.itemListener);
		GroupLayout gl_shiftCollector = new GroupLayout(shiftCollector);
		gl_shiftCollector.setHorizontalGroup(
			gl_shiftCollector.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_shiftCollector.createSequentialGroup()
					.addGap(1)
					.addGroup(gl_shiftCollector.createParallelGroup(Alignment.LEADING)
						.addComponent(lblSchichtart, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblDatum, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_shiftCollector.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_shiftCollector.createParallelGroup(Alignment.LEADING, false)
							.addComponent(shiftTypeChooser, 0, 0, Short.MAX_VALUE)
							.addComponent(dateField))
						.addComponent(calendarButton))
					.addGap(21)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_shiftCollector.setVerticalGroup(
			gl_shiftCollector.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_shiftCollector.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_shiftCollector.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 144, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_shiftCollector.createSequentialGroup()
							.addGroup(gl_shiftCollector.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblSchichtart)
								.addComponent(shiftTypeChooser, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_shiftCollector.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblDatum)
								.addComponent(dateField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(calendarButton)))
					.addGap(150))
		);
		shiftTypeChooser.addItemListener(this.itemListener);
		
		shiftTable = new JTable();
		shiftTable.setShowGrid(false);
		Object[][] data = ShiftContainer.toTableData(shiftContainer.getShifts());
		for(int i =0; i< data.length; i++)
		{
			shiftTableModel.addRow(data[i]);
		}
		shiftTable.setModel(shiftTableModel);
		scrollPane.setViewportView(shiftTable);
		shiftCollector.setLayout(gl_shiftCollector);
		// basePanel.add(tabbedPane);
		getContentPane().add(tabbedPane);
	}

	public static void main(String[] args) {

		// try {
		// for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		// if ("Nimbus".equals(info.getName())) {
		// UIManager.setLookAndFeel(info.getClassName());
		// break;
		// }
		//
		// } catch (Exception e) {
		// // If Nimbus is not available, you can set the GUI to another look
		// and feel.
		// }

		// Setup Shift Container
		JFrame f = new MainWindow();
		f.setVisible(true);
	}
}
