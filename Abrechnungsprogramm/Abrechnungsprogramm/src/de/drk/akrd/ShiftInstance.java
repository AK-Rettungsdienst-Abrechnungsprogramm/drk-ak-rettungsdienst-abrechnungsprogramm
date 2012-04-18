package de.drk.akrd;
import java.text.SimpleDateFormat;
public class ShiftInstance {

  private Shift instanceOf;
  private String date;
  private int actualStartingTime;
  private int actualEndTime;
  private int actualBreakTime;
  private float timeAsFloat;
  private String partner;
  private String comment;

  public ShiftInstance(Shift instanceOf, String date, float timeAsFloat, String partner, String comment) {
    this.instanceOf = instanceOf;
    this.date = date;
    actualStartingTime = instanceOf.getStartingTime();
    actualEndTime = instanceOf.getEndTime();
    actualBreakTime = instanceOf.getBreakTime();
    this.timeAsFloat = timeAsFloat;
    this.partner = partner;
    this.comment = comment;
  }
  public ShiftInstance(Shift instanceOf, String date, int actualStartingTime, 
          int actualEndTime, int actualBreakTime, float timeAsFloat, 
          String partner, String comment) {
    this.instanceOf = instanceOf;
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
   * @return the corresponding shift
   */
  public Shift getInstanceOf() {
    return instanceOf;
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
   * @return the breakTime
   */
  public int getBreakTime() {
    return instanceOf.getBreakTime();
  }

  /**
   * @return the actualBreakTime
   */
  public int getActualBreakTime() {
    return actualBreakTime;
  }

  /**
   * @return the type
   */
  public ShiftContainer.ShiftType getType() {
    return instanceOf.getType();
  }

  /**
   * @return the id
   */
  public String getId() {
    return instanceOf.getId();
  }

  /**
   * @return the startingTime
   */
  public int getStartingTime() {
    return instanceOf.getStartingTime();
  }

  /**
   * @return the endTime
   */
  public int getEndTime() {
    return instanceOf.getEndTime();
  }

  /**
   * @return the external
   */
  public boolean isExternal() {
    return instanceOf.isExternal();
  }
  
  public static float timeToFloat(int time)
  {
	  return (float)((Math.floor(time)/100) + (float)((time % 60)/60));
  }
}
