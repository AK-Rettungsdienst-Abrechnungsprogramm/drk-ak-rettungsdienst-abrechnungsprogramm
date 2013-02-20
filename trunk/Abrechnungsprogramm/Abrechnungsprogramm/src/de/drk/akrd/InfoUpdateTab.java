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
 * @author Jo
 */
public class InfoUpdateTab{
  
  public InfoUpdateTab(JPanel panel){

    //panel.setLayout(new BorderLayout());
    panel.setLayout(new GridLayout(4,0));
    String programInfo = "<html><br><h1>AK-RD Abrechnungsprogramm</h1>"
            + "Version "+Float.toString(MainWindow.PROGRAM_VERSION)+"<br><br>"
            + "&copy; Johannes Güttler, Niklas Meinzer 2013<br>"
            + "Email: <a href='mailto:Software@ak-rd.de'>Software@ak-rd.de</a>"
            + "<br><br><br><br></html>";
    String updateInfo = "<html><CENTER><h1>Schicht-Update</h1></CENTER>"
            + "Mit Klick auf den Update-Button wird die neuste Version der "
            + "Schicht-Liste heruntergeladen.<br></html>";
    JLabel programInfoLabel = new JLabel(programInfo);
    programInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
    //7panel.add(programInfoLabel, BorderLayout.PAGE_START);
    panel.add(programInfoLabel);
    JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
    panel.add(separator);
    JLabel updateInfoLabel = new JLabel(updateInfo);
    updateInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
    panel.add(updateInfoLabel);
    // create and add update-button
    JButton updateButton = new JButton("Schicht-Update");
    updateButton.setFont(new Font(updateButton.getFont().getName(),
            updateButton.getFont().getStyle(), updateButton.getFont().getSize()));
    updateButton.setMaximumSize(new Dimension(150, 50));
    ActionListener updateButtonActionListener = new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        Update.downloadNewShiftFile();
      }
    };
    updateButton.addActionListener(updateButtonActionListener);
    updateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
    buttonPanel.add(updateButton);
    panel.add(buttonPanel);
  }
  
}
