package de.drk.akrd;

import de.drk.akrd.ShiftContainer.ShiftType;
import java.text.ParseException;
import java.util.Date;

public class ShiftInstance implements Comparable<ShiftInstance> {

  private ShiftContainer.ShiftType type;
  private String dateString;
  private Date date;
  private int actualStartingTime;
  private int actualStartingTimeWithPrepTime;
  private int actualEndTime;
  private int actualBreakTime;
  private String partner;
  private String comment;
  private boolean preparationTime;

  /**
   * create a new ShiftInstance
   * @param type ShiftContainer.ShiftType
   * @param dateString dateString-string
   * @param actualStartingTime
   * @param actualEndTime
   * @param actualBreakTime
   * @param partner shift-partner; maximal 18 characters
   * @param comment comment; maximal 36 characters
   */
  public ShiftInstance(ShiftContainer.ShiftType type, String dateString, int actualStartingTime, 
          int actualEndTime, int actualBreakTime, String partner, String comment, boolean prepTime) {
    this.type = type;
    this.dateString = dateString;
    try {
      this.date = UtilityBox.SIMPLE_DATE_FORMAT.parse(dateString);
    } catch (ParseException ex) {
      UtilityBox.getInstance().displayErrorPopup("Schicht-Datum", "Fehler beim "
              + "lesen des Datums. \nDadurch kann es eventuell zu fehlerhaften "
              + "Angaben in der Anzeige und Abrechnung kommen.\n"+ex.getMessage());
    }
    this.actualStartingTime = actualStartingTime;
    this.actualEndTime = actualEndTime;
    this.actualBreakTime = actualBreakTime;
    this.partner = partner;
    this.comment = comment;
    this.preparationTime = prepTime;
    
    int start = actualStartingTime;
    // if prepTime was set, calculate actualStartingTimeWithPrepTime
	if(prepTime)
	{
		// subtract 10 minutes
		int minutes = start%100;
		if(minutes >= 10) {
			start -= 10;
		} else
		{
			int diff = 10 - minutes;
			if (start > 100)
			{
				start = start - 40 - 10;
			} else
			{
				start = 2400 - 40 - diff;
			}
		}
	}
	this.actualStartingTimeWithPrepTime = start;
  }

  public int getActualStartingTimeWithPrepTime() {
	return actualStartingTimeWithPrepTime;
}

public boolean PreparationTimeSet() {
	return preparationTime;
}

public void setPreparationTime(boolean preparationTime) {
	this.preparationTime = preparationTime;
}

/**
   * 
   * @return the dateString string
   */
  public String getDateString() {
    return dateString;
  }
  public Date getDate() {
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
    return UtilityBox.getInstance().calculateTimeAsFloat(actualStartingTimeWithPrepTime, actualEndTime, actualBreakTime);
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

  @Override
  public int compareTo(ShiftInstance si) {
    if (si.getDate().before(date)) {
      return -1;
    }
    else
      return 1;
  }
}
