/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.drk.akrd;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

/**
 * show programinfo and update-button
 *
 * @author Jo
 */
public class InfoUpdateTab extends JPanel {

  private JLabel updateInfo = new JLabel();
  // TODO: onsetfocus listener : update newest shift und salary file versions

  public InfoUpdateTab() {

    //panel.setLayout(new BorderLayout());
    this.setLayout(new GridLayout(4, 0));
    updateInfoLabel();
    String programInfo = "<html><br><h1>AK-RD Abrechnungsprogramm</h1>"
        + "Version " + Float.toString(MainWindow.PROGRAM_VERSION)
        + "<br><br>"
        + "&copy; Johannes Güttler, Niklas Meinzer 2013<br>"
        + "Email: <a href='mailto:Software@ak-rd.de'>Software@ak-rd.de</a>"
        + "<br><br><br><br></html>";
    JLabel programInfoLabel = new JLabel(programInfo);
    programInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
    //7panel.add(programInfoLabel, BorderLayout.PAGE_START);
    this.add(programInfoLabel);
    JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
    this.add(separator);
    updateInfo.setHorizontalAlignment(SwingConstants.CENTER);
    this.add(updateInfo);
    // create and add update-button
    JButton updateButton = new JButton("Schicht-Update");
    updateButton.setFont(new Font(updateButton.getFont().getName(),
                                  updateButton.getFont().getStyle(), updateButton.getFont().getSize()));
    updateButton.setMaximumSize(new Dimension(150, 50));
    ActionListener updateButtonActionListener = new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        Update.downloadNewSalaryFile();
        Update.downloadNewShiftFile();
        UtilityBox.getInstance().requestShiftListReload();
        updateInfoLabel();
      }
    };
    updateButton.addActionListener(updateButtonActionListener);
    updateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
    buttonPanel.add(updateButton);
    this.add(buttonPanel);
  }

  // resets the update info label with the current version
  private void updateInfoLabel() {
    float shiftFileversion = MainWindow.SHIFT_FILE_VERSION;
    float salaryFileVersion = MainWindow.SALARY_FILE_VERSION;
    float latestShiftFileVersion = Update.getLatestShiftFileVersion();
    float latestSalaryFileVersion = Update.getLatestSalaryFileVersion();
    String updateInfoString =
        "<html><CENTER><h1>Update der Hintegrunddaten</h1></CENTER>"
        + "Mit Klick auf den Update-Button werden die neusten Versionen der "
        + "Schicht-Liste und Gehalts-Liste heruntergeladen.<br>"
        + "Aktuelle Versionen:<br>"
        + "<table ><tr>"
        + "<b><td>aktuell</td><td>neuste</td></b></tr>"
        + "<tr><td>Schichten: </td><td>" + shiftFileversion + "</td>"
        + "<td>" + latestShiftFileVersion + "</td></tr>"
        + "<tr><td>Gehalt: </td><td>" + salaryFileVersion + "</td>"
        + "<td>" + latestSalaryFileVersion + "</td></tr>"
        + "</html>";
    updateInfo.setText(updateInfoString);
  }

}
