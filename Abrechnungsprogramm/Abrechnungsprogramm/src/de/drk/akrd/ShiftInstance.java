package de.drk.akrd;

import de.drk.akrd.ShiftContainer.ShiftType;

public class ShiftInstance {

  private ShiftContainer.ShiftType type;
  private String date;
  private int actualStartingTime;
  private int actualEndTime;
  private int actualBreakTime;
  private float timeAsFloat;
  private String partner;
  private String comment;

  public ShiftInstance(ShiftContainer.ShiftType type, String date, int actualStartingTime, 
          int actualEndTime, int actualBreakTime, float timeAsFloat, 
          String partner, String comment) {
    this.type = type;
    this.date = date;
    this.actualStartingTime = actualStartingTime;
    this.actualEndTime = actualEndTime;
    this.actualBreakTime = actualBreakTime;
    this.timeAsFloat = timeAsFloat;
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
    return timeAsFloat;
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
