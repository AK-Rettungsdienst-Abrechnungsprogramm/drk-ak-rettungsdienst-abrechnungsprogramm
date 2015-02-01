package de.drk.akrd;

import de.drk.akrd.ShiftContainer.ShiftType;
import java.text.ParseException;
import java.util.Date;

public class ShiftInstance implements Comparable<ShiftInstance> {
  // the unique id of the shift instance
  // each shift instance has a unique id during the time the program runs 
  // (they are not stored)
  // the id is used to identify shifts when the user wants to edit or delete them
  private int id;
  // the type of the shit i.e. KTW, RTW....
  private ShiftContainer.ShiftType type;
  // the date of the shift as a string
  private String dateString;
  // the date of the shift
  private Date date;
  private int actualStartingTime;
  private int actualStartingTimeWithPrepTime;
  private int actualEndTime;
  private int actualBreakTime;
  // additional commute expenses (e.g. 12 € for KiZa and Breisach)
  private int commuteExpenses;
  private String partner;
  private String comment;
  private int preparationTime;

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
  public ShiftInstance(int id, ShiftContainer.ShiftType type, String dateString, int actualStartingTime, 
          int actualEndTime, int actualBreakTime, int commuteExpenses, int prepTime, String partner, String comment) {
    this.id = id;
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
    this.commuteExpenses = commuteExpenses;
    this.partner = partner;
    this.comment = comment;
    this.preparationTime = prepTime;
    
    int start = actualStartingTime;
    // if prepTime was set, calculate actualStartingTimeWithPrepTime

	// subtract prepTime minutes
	int minutes = start%100;
	if(minutes >= prepTime) {
		start -= prepTime;
	} else
	{
		int diff = prepTime - minutes;
		if (start > 100)
		{
			start = start - 40 - prepTime;
		} else
		{
			start = 2400 - 40 - diff;
		}
	}
	this.actualStartingTimeWithPrepTime = start;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

public void setCommuteExpenses(int commuteExpenses) {
	this.commuteExpenses = commuteExpenses;
}

public int getActualStartingTimeWithPrepTime() {
	return actualStartingTimeWithPrepTime;
}

public void setPreparationTime(int preparationTime) {
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
    return UtilityBox.calculateTimeAsFloat(actualStartingTimeWithPrepTime, actualEndTime, actualBreakTime);
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

public int getPrepTime() {
	return preparationTime;
}
}
