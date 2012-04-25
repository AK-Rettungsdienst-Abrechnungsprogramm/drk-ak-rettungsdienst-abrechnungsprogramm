package de.drk.akrd;

import de.drk.akrd.ShiftContainer.ShiftType;

public class ShiftInstance {

  private ShiftContainer.ShiftType type;
  private String date;
  private int actualStartingTime;
  private int actualEndTime;
  private int actualBreakTime;
  private String partner;
  private String comment;

  /**
   * create a new ShiftInstance
   * @param type ShiftContainer.ShiftType
   * @param date date-string
   * @param actualStartingTime
   * @param actualEndTime
   * @param actualBreakTime
   * @param partner shift-partner; maximal 18 characters
   * @param comment comment; maximal 36 characters
   */
  public ShiftInstance(ShiftContainer.ShiftType type, String date, int actualStartingTime, 
          int actualEndTime, int actualBreakTime, String partner, String comment) {
    this.type = type;
    this.date = date;
    this.actualStartingTime = actualStartingTime;
    this.actualEndTime = actualEndTime;
    this.actualBreakTime = actualBreakTime;
    this.partner = partner;
    this.comment = comment;
  }

  /**
   * 
   * @return the date string
   */
  public String getDate() {
    return date;
  }

  /**
   * @return the actualEndTime
   */
  public int getActualEndTime() {
    return actualEndTime;
  }

  /**
   * @return the actualStartingTime
   */
  public int getActualStartingTime() {
    return actualStartingTime;
  }

  /**
   * @return the comment
   */
  public String getComment() {
    return comment;
  }

  /**
   * @return the shift-partner
   */
  public String getPartner() {
    return partner;
  }

  /**
   * @return the time als float value
   */
  public float getTimeAsFloat() {
    return UtilityBox.getInstance().calculateTimeAsFloat(actualStartingTime, actualEndTime, actualBreakTime);
  }

  /**
   * @return the actualBreakTime
   */
  public int getActualBreakTime() {
    return actualBreakTime;
  }

  /**
   * @return the ShiftType (ShiftContainer.ShiftType)
   */
  public ShiftType getType() {
    return type;
  }
  

  public static float timeToFloat(int time)
  {
	  return (float)((Math.floor(time)/100) + (float)((time % 60)/60));
  }
}
