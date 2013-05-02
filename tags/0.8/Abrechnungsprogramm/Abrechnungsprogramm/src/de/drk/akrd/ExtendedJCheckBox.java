/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.drk.akrd;

import de.drk.akrd.ShiftForm.TimeCode;
import java.util.Date;
import javax.swing.JCheckBox;

/**
 *
 * @author Jo
 */
public class ExtendedJCheckBox extends JCheckBox{
  private boolean Checked = false;
  private ShiftForm.TimeCode timeCode = ShiftForm.TimeCode.EMPTY;
  private Date date = null;

  public ExtendedJCheckBox(String text, ShiftForm.TimeCode tc, Date d) {
    super(text);
    timeCode = tc;
    date = d;
  }

  public TimeCode getTimeCode() {
    return timeCode;
  }

  public void setTimeCode(TimeCode timeCode) {
    this.timeCode = timeCode;
  }

  public boolean isChecked() {
    return Checked;
  }

  /**
   * Sets the variable checked, doesn't change the "selected"-status!
   * @param Checked 
   */
  public void setChecked(boolean Checked) {
    this.Checked = Checked;
  }
  
}
