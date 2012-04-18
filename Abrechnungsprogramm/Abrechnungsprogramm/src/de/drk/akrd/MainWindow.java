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
import java.awt.event.MouseEvent;
import javax.swing.JToggleButton;
import org.eclipse.wb.swing.FocusTraversalOnArray;
import java.awt.Component;
import java.awt.event.MouseMotionAdapter;

public class MainWindow extends JFrame {

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
	
	// This int holds the TYPE of day that is currently selected
	// types are: 0 = weekday, 1 = holyday or saturday, 2 = sunday, -1 means not set
	protected int currentlySelectedDay = -1;
	
	private JPanel shiftEditor = new JPanel();
	protected ShiftContainer shiftContainer = new ShiftContainer(this);

	protected JComboBox shiftTypeChooser = new JComboBox();
	private final JLabel lblDatum = new JLabel("Datum");
	private final JLabel lblSchichtart = new JLabel("Schichtart");
	protected JTextField dateField;
	protected JTable shiftTable;
	protected DefaultTableModel shiftTableModel = new DefaultTableModel(
			new Object[][] {}, new String[] { "Schichtkürzel", "Beginn",
					"Ende", "Pause" });
	protected DefaultTableModel registeredShiftsTableModel = new DefaultTableModel(
			new Object[][] {}, new String[] { "Datum", "Beginn",
					"Ende", "Pause", "Schichtpartner", "Kommentar" });
	private JTable registeredShiftsTable;
	private final JLabel lblSchichtpartner = new JLabel("Schichtpartner:");
	protected JTextField shiftPartnerField;
	protected JTextField beginField;
	protected JTextField endField;
	protected JTextField breakField;
	protected JButton submitButton;
	private final JLabel lblKommentar = new JLabel("Kommentar");
	protected JTextField commentField = new JTextField();

	public MainWindow() {
		commentField.setColumns(10);

		// Setup ShiftContainer
		shiftContainer.loadShifts("Schichten.xml");
//		// / TEST ///
//		// personaldatatest
//		PersonalData.setData("Heino", "Meyer", "Privatbank Musterhausen", 1234,
//				666, PersonalData.Qualification.RA, false);
//		// XMLEditor.writePersonalData(PersonalData.getInstance());
//		boolean loadPersonalData = XMLEditor.loadPersonalData();
//		ShiftInstance[] shiftsToAccount = new ShiftInstance[46];
//
//		int iterator = 0;
//		for (iterator = 0; iterator < 46; iterator++) {
//			int shiftIndex = (iterator >= shiftContainer.getShifts().length) ? 0
//					: iterator;
//			Shift beispiel = shiftContainer.getShifts()[shiftIndex];
//			shiftsToAccount[iterator] = new ShiftInstance(beispiel,
//					"30.05.2012", 5f, "heino" + iterator, "" + iterator);
//		}
//		PdfCreator.createAccounting(shiftsToAccount);
//		/*
//		 * SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy"); Calendar
//		 * cal = Calendar.getInstance(); Date date = cal.getTime(); try { date =
//		 * sdf.parse("30.05.2012");
//		 * System.out.println("dateToString: "+date.toString());
//		 * cal.setTime(date);
//		 * System.out.println("cal.getDay: "+cal.get(Calendar.DAY_OF_MONTH));
//		 * System.out.println("cal.getMonth: "+cal.get(Calendar.MONTH));
//		 * System.out.println("cal.getYear: "+cal.get(Calendar.YEAR)); date =
//		 * sdf.parse("01.01.2012");
//		 * System.out.println("dateToString: "+date.toString());
//		 * cal.setTime(date);
//		 * System.out.println("cal.getDay: "+cal.get(Calendar.DAY_OF_MONTH));
//		 * System.out.println("cal.getMonth: "+cal.get(Calendar.MONTH));
//		 * System.out.println("cal.getYear: "+cal.get(Calendar.YEAR)); date =
//		 * sdf.parse("31.12.2012");
//		 * System.out.println("dateToString: "+date.toString());
//		 * cal.setTime(date);
//		 * System.out.println("cal.getDay: "+cal.get(Calendar.DAY_OF_MONTH));
//		 * System.out.println("cal.getMonth: "+cal.get(Calendar.MONTH));
//		 * System.out.println("cal.getYear: "+cal.get(Calendar.YEAR)); } catch
//		 * (ParseException ex) { ex.printStackTrace();
//		 * System.out.println("das war wohl nichts"); }
//		 */
//		ArrayList<ShiftInstance> testList = new ArrayList<>();
//		for (int it = 0; it < shiftsToAccount.length; it++) {
//			// testList.add(shiftsToAccount[it]);
//		}
//		Shift beispiel = shiftContainer.getShifts()[0];
//		testList.add(new ShiftInstance(beispiel, "30.05.2012", 5f,
//				"musterfrau?", "name geändert"));
//		beispiel = shiftContainer.getShifts()[2];
//		testList.add(new ShiftInstance(beispiel, "06.06.2012", 5f, "horst5",
//				"war auch super!"));
//		// XMLEditor.storeShifts(testList, 2012);
//		ArrayList<ArrayList<ShiftInstance>> schichten = XMLEditor
//				.loadSavedShifts(2012);
//		for (Iterator<ArrayList<ShiftInstance>> it = schichten.iterator(); it
//				.hasNext();) {
//			ArrayList<ShiftInstance> arrayList = it.next();
//			for (Iterator<ShiftInstance> it1 = arrayList.iterator(); it1
//					.hasNext();) {
//				ShiftInstance shiftInstance = it1.next();
//				System.out
//						.println("schicht geladen: " + shiftInstance.getDate()
//								+ " " + shiftInstance.getId());
//			}
//		}
//		// / END TEST ///

		// Set Shift Type Chooser from Enum
		DefaultComboBoxModel enumModel = new DefaultComboBoxModel(ShiftType.values());
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
		tabbedPane.addTab("Schichten", null, shiftEditor,
				"Schichten eingeben und bearbeiten");

		JScrollPane scrollPane = new JScrollPane();
		JScrollPane scrollPane2 = new JScrollPane();


		dateField = new JTextField();
		dateField.setText("bitte auswählen");
		dateField.addMouseListener(mouseAdapter);
		dateField.setEditable(false);
		dateField.setColumns(10);
		
		registeredShiftsTable = new JTable();
		
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
		
		GroupLayout gl_shiftEditor = new GroupLayout(shiftEditor);
		gl_shiftEditor.setHorizontalGroup(
			gl_shiftEditor.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_shiftEditor.createSequentialGroup()
					.addGroup(gl_shiftEditor.createParallelGroup(Alignment.LEADING)
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
											.addGroup(gl_shiftEditor.createParallelGroup(Alignment.LEADING)
												.addComponent(lblBeginn)
												.addComponent(lblPause))
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addGroup(gl_shiftEditor.createParallelGroup(Alignment.LEADING, false)
												.addComponent(breakField, 0, 0, Short.MAX_VALUE)
												.addComponent(beginField, GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE))
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(lblEnde)
											.addPreferredGap(ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
											.addComponent(endField, GroupLayout.PREFERRED_SIZE, 69, GroupLayout.PREFERRED_SIZE))
										.addGroup(gl_shiftEditor.createSequentialGroup()
											.addComponent(lblKommentar)
											.addGap(21)
											.addGroup(gl_shiftEditor.createParallelGroup(Alignment.LEADING)
												.addGroup(gl_shiftEditor.createSequentialGroup()
													.addGap(12)
													.addComponent(submitButton))
												.addComponent(commentField, GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE))))
									.addPreferredGap(ComponentPlacement.RELATED)))
							.addPreferredGap(ComponentPlacement.RELATED, 18, GroupLayout.PREFERRED_SIZE)
							.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_shiftEditor.createSequentialGroup()
							.addContainerGap()
							.addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 769, Short.MAX_VALUE)))
					.addContainerGap())
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
							.addGroup(gl_shiftEditor.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_shiftEditor.createParallelGroup(Alignment.TRAILING)
									.addGroup(gl_shiftEditor.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblEnde)
										.addComponent(endField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addGroup(gl_shiftEditor.createSequentialGroup()
										.addComponent(beginField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addGap(2)))
								.addComponent(lblBeginn))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_shiftEditor.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblPause)
								.addComponent(breakField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_shiftEditor.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblKommentar)
								.addComponent(commentField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(submitButton))
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
					.addContainerGap())
		);
		shiftTypeChooser.addItemListener(this.itemListener);

		shiftTable = new JTable();
		shiftTable.addMouseListener(mouseAdapter);
		shiftTable.setShowGrid(false);
		Object[][] data = ShiftContainer
				.shiftToTableData(shiftContainer.getShifts());
		for (int i = 0; i < data.length; i++) {
			shiftTableModel.addRow(data[i]);
		}
		shiftTable.setModel(shiftTableModel);
		registeredShiftsTable.setModel(registeredShiftsTableModel);
		scrollPane.setViewportView(shiftTable);
		scrollPane2.setViewportView(registeredShiftsTable);
		shiftEditor.setLayout(gl_shiftEditor);
		shiftEditor.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{shiftPartnerField, beginField, endField, breakField, commentField, submitButton, lblKommentar, dateField, lblSchichtart, lblDatum, shiftTypeChooser, lblSchichtpartner, lblBeginn, lblPause, lblEnde, scrollPane, shiftTable, scrollPane2, registeredShiftsTable}));
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

	/**
	 * Gets the current day and shift type (for filter options) and updated the
	 * Shift container
	 * 
	 * @author niklas
	 */
	public void updateShiftContainer()
	{
		// Get currently selected shift type
		ShiftType type = (ShiftType)shiftTypeChooser.getSelectedItem();
		
		Object[][] data = ShiftContainer.shiftToTableData(shiftContainer.filterShifts(type, currentlySelectedDay));
		shiftTableModel.setNumRows(0);
		for(int i = 0; i < data.length; i++)
		{
			shiftTableModel.addRow(data[i]);
		}
		
	}
	
	public void updateRegisteredShifts()
	{
		Object[][] data = ShiftContainer.shiftInstancesToTableData((ShiftInstance[]) shiftContainer.shiftInstances.toArray(new ShiftInstance[shiftContainer.shiftInstances.size()]));
		registeredShiftsTableModel.setNumRows(0);
		for(int i = 0; i < data.length; i++)
		{
			registeredShiftsTableModel.addRow(data[i]);
		}		
		
	}
}
