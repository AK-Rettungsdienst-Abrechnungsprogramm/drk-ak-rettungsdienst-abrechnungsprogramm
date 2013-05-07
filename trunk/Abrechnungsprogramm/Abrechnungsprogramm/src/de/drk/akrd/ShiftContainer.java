package de.drk.akrd;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// Contains all shifts and takes care of loading and managing
public class ShiftContainer  {

  // a pointer to the posessing mainWindow
  private MainWindow mainWindow;
  private static Calendar calendar = Calendar.getInstance();
  private static ArrayList<Shift> shifts = new ArrayList<Shift>();
// Holds all registered shifts.
  public ArrayList<ShiftInstance> shiftInstances = new ArrayList<ShiftInstance>();
  
  
  // the id of the next shift instance that will be registered
  // is incremented after each shift instance is registered
  private int shiftIdCounter;
  
  private static Map<String, ShiftContainer.ShiftType> _shiftTypeStringToEnum;
  public enum ShiftType {

    Alle, RTW, KTW, KIZA, BREISACH, EVENT, KVS, HINTERGRUND, ELW, SC, CONCERT_HALL;

    // Override the toString method to get nicer strings for the shiftTypeChooser
    @Override
    public String toString() {
      // TODO: for JDK7 use switch
//      switch (name()) {
//        case "RTW":
//          return "RTW & ITW";
//        case "KTW":
//          return "KTW";
//        case "KIZA":
//          return "RD Kirchzarten";
//        case "BREISACH":
//          return "RD Breisach";
//        case "BABY":
//          return "Baby NAW";
//        case "EVENT":
//          return "Sandienst";
//        case "KVS":
//          return "KV Dienst";
//        default:
//          return name();
//      }
      if (name().equals("RTW")) {
        return "RTW & ITW";
      } else if (name().equals("KTW")) {
        return "KTW";
      } else if (name().equals("KIZA")) {
        return "RD Kirchzarten";
      } else if (name().equals("BREISACH")) {
        return "RD Breisach";
      } else if (name().equals("EVENT")) {
        return "Sandienst";
      } else if (name().equals("KVS")) {
        return "KV Dienst";
      } else if (name().equals("HINTERGRUND")) {
    	return "Hintergrund";
      } else if (name().equals("CONCERT_HALL")) {
    	  return "Konzerthaus";
      } else if (name().equals("SC")){
    	  return "SC Sandienst";
      } else {
        return name();
      }
    }
  }
  

  public ShiftContainer(MainWindow mainWindow) {
    this.mainWindow = mainWindow;
    this.shiftIdCounter = 0;
  }

  String getPathToJarfileDir(Object classToUse) {
	  String url = classToUse.getClass().getResource("/" + classToUse.getClass().getName().replaceAll("\\.", "/") + ".class").toString();
	  url = url.substring(4).replaceFirst("/[^/]+\\.jar!.*$", "/");
	  try {
	      File dir = new File(new URL(url).toURI());
	      url = dir.getAbsolutePath();
	  } catch (MalformedURLException mue) {
	      url = null;
	  } catch (URISyntaxException ue) {
	      url = null;
	  }
	  return url;
	}
  
  public void loadShifts(String shiftfilePath) {
    XMLEditor.fillShiftList("data" + System.getProperty("file.separator") + "Schichten.xml", shifts);
   
//    UtilityBox.getInstance().displayInfoPopup("benutzer pfad: data",  System.getProperty("user.home") + "Schichten.xml\n");
//
//    	UtilityBox.getInstance().displayInfoPopup("get Resource", new File(ClassLoader.getSystemClassLoader().getResource(".").getPath()).getAbsolutePath());
//
//    
//    UtilityBox.getInstance().displayInfoPopup("über Class", MainWindow.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    
  }

  public Shift[] getShifts() {
    return (Shift[]) shifts.toArray(new Shift[shifts.size()]);
  }

  public static ArrayList<Shift> getShiftsAsList() {
    return shifts;
  }

  /**
   * This method is private, since 
   * @param type
   * @param day - 0 = weekday, 1 = holyday or saturday, 2 = sunday, -1 = no date filtering
   * @return
   */
  public Shift[] filterShifts(ShiftContainer.ShiftType type, int day) {
    ArrayList<Shift> filteredShifts = new ArrayList<Shift>();
    ArrayList<Shift> resultingShifts = new ArrayList<Shift>();

    // Iterate over all shifts and filter by type

    for (int i = 0; i < shifts.size(); i++) {
      if (type == ShiftType.Alle || shifts.get(i).getType() == type) {
        filteredShifts.add(shifts.get(i));
      }
    }

    // iterate over all filtered shifts and filter again by date

    for (int i = 0; i < filteredShifts.size(); i++) {
      Shift element = filteredShifts.get(i);
      if (day == -1 || element.getDays() == day || element.getDays() == 3) {
        resultingShifts.add(element);
      }
    }

    return (Shift[]) resultingShifts.toArray(new Shift[resultingShifts.size()]);
  }

  /**
   * 	Converts an array of Shifts to an Object[][] that fits into a table
   * 
   * @param input 
   * @return the converted object
   * @author niklas
   */
  public static Object[][] shiftToTableData(Shift[] input) {
    ArrayList<String[]> result = new ArrayList<String[]>();

    for (int i = 0; i < input.length; i++) {
      ArrayList<String> entry = new ArrayList<String>();
      entry.add(input[i].getId());
      entry.add(UtilityBox.createTimeStringFromInt(input[i].getStartingTime()));
      entry.add(UtilityBox.createTimeStringFromInt(input[i].getEndTime()));
      entry.add(UtilityBox.createTimeStringFromInt(input[i].getBreakTime()));

      result.add((String[]) entry.toArray(new String[entry.size()]));
    }

    return (Object[][]) result.toArray(new Object[result.size()][4]);


  }

  /**
   * 	Converts an array of ShiftInstances to an Object[][] that fits into a table
   * 
   * @param input 
   * @return the converted object
   * @author niklas
   */
  public static Object[][] shiftInstancesToTableData(ShiftInstance[] input) {
    ArrayList<String[]> result = new ArrayList<String[]>();

    DecimalFormat df = new DecimalFormat("#0.00");

    for (int i = 0; i < input.length; i++) {
      ArrayList<String> entry = new ArrayList<String>();
      entry.add(input[i].getDateString());

      entry.add(UtilityBox.createTimeStringFromInt(input[i].getActualStartingTimeWithPrepTime()));
      entry.add(UtilityBox.createTimeStringFromInt(input[i].getActualEndTime()));
      entry.add(UtilityBox.createTimeStringFromInt(input[i].getActualBreakTime()));
      entry.add(df.format(input[i].getTimeAsFloat()));
      entry.add(input[i].getPartner());
      entry.add(input[i].getType().toString());
      entry.add(input[i].getComment());
      entry.add(Integer.toString(input[i].getCommuteExpenses()) + " €");
      result.add((String[]) entry.toArray(new String[entry.size()]));
    }

    return (Object[][]) result.toArray(new Object[result.size()][4]);


  }

  /**
   * @param instanceOf The shift this is an instance of, null if not applicable (e.g. sandienst)
   * @param date	The date the shift was worked.
   * @param actualStart The actual starting time
   * @param actualEnd the actual end time
   * @param actualBreak the actual break time
   * @param partner The colleague of the shift
   * @param comment 
   * @author niklas
   */
  protected void registerShift(ShiftContainer.ShiftType type, String date, int actualStart, int actualEnd, int actualBreak, String partner, String comment, String prepTime) {
    // calculate time in float
    float startingFloat = ShiftInstance.timeToFloat(actualStart);
    float endFloat = ShiftInstance.timeToFloat(actualEnd);
    float breakFloat = ShiftInstance.timeToFloat(actualBreak);

    // account for shifts ending after midnight
    if (endFloat < startingFloat) {
      endFloat += 24;
    }

    float shiftAsFloat = endFloat - startingFloat - breakFloat;
    
    // calculate commute expenses
    int commuteExpenses = 0;
    if (type == ShiftType.KIZA || type == ShiftType.BREISACH) {
    	commuteExpenses = 12;
    }
    

    ShiftInstance entry = new ShiftInstance(shiftIdCounter, type, date, actualStart, actualEnd, actualBreak, commuteExpenses, Integer.parseInt(prepTime), partner, comment);
    // increment id counter
    shiftIdCounter++;

    shiftInstances.add(entry);
    Collections.sort(shiftInstances);
    // save new ShiftList
    calendar.setTime(entry.getDate());
    ShiftLoadSave.saveShifts(shiftInstances, calendar.get(Calendar.YEAR));
  }

  /**
   * add a list of shifts to the shiftInstances
   * @param shifts ArrayList<ShiftInstance>
   * @param saveShifts write or not write a new saved-shifts-file
   * @author Jo
   */
  protected void registerShifts(ArrayList<ShiftInstance> shifts, boolean saveShifts) {
    // iterate over all shifts, give them an id and add them to the list
    for(int i = 0; i < shifts.size(); i++) {
      shifts.get(i).setId(shiftIdCounter);
      shiftIdCounter++;
      shiftInstances.add(shifts.get(i));
    }

    Collections.sort(shiftInstances);
    if (saveShifts) {
      calendar.setTime(shifts.get(0).getDate());
      ShiftLoadSave.saveShifts(shiftInstances, calendar.get(Calendar.YEAR));
    }
  }

  /**
   * 
   * @param id the unique id of the shift that is to be deleted
   */
  public void deleteShift(int id) {

    // get instance to delete (to get the year)
    ShiftInstance deletedInstance = null;
    // the array index of the deleted shift
    int number = -1;
    for (int i = 0; i < shiftInstances.size(); i++) {
      if (shiftInstances.get(i).getId() == id) {
        deletedInstance = shiftInstances.get(i);
        number = i;
        break;
      }
    }
    // if no matching instance was found return 
    if (deletedInstance == null) return;
    
    shiftInstances.remove(number);
    
    // rewrite XML file
    calendar.setTime(deletedInstance.getDate());
    ShiftLoadSave.saveShifts(shiftInstances, calendar.get(Calendar.YEAR));
  }

  public ShiftInstance getShift(int number) {
    if (number >= shiftInstances.size()) {
      return null;
    }

    return shiftInstances.get(number);
  }

  /**
   * Get an ShiftType Enum from a shift id, i.e. "R1N"
   * @param id
   * @return ShiftContainer.ShiftType
   */
  public static ShiftType getShiftTypeFromId(String id) {
	  // if the hash map is not yet populated, do it now
	  if (_shiftTypeStringToEnum == null) {
		  _shiftTypeStringToEnum = new HashMap<String, ShiftContainer.ShiftType>();
		  for (ShiftType shiftType : ShiftType.values()) {
			  _shiftTypeStringToEnum.put(shiftType.name(), shiftType);
		  }
	  }
	  if (! _shiftTypeStringToEnum.containsKey(id)){
		  // TODO: this should rather yield an error
		  System.err.println("Unknown shift type:" + id);
		  return ShiftContainer.ShiftType.KTW;
	  }
	  else return _shiftTypeStringToEnum.get(id);
    }
  /**
   * Return a sorted list of all years appearing in the shiftlist
   * @return 
   */
  public ArrayList<Integer> getSortedYearList() {
    ArrayList<Integer> yearList = new ArrayList<Integer>();
    for (int i = 0; i < shiftInstances.size(); i++) {
      ShiftInstance shiftInstance = shiftInstances.get(i);
      calendar.setTime(shiftInstance.getDate());
      int currentYear = calendar.get(Calendar.YEAR);
      if (!yearList.contains(currentYear)) {
        yearList.add(currentYear);
      }
    }
    Collections.sort(yearList);
    Collections.reverse(yearList);
    return yearList;
  }
  /**
   * Returns the shift insances and is able to filter to a specific month
   * @param year: the year the selected month is in or -1 if all
   * @param month: the selected month january = 0 .. or -1 if all
   * @return
   */
  public ArrayList<ShiftInstance> getShiftInsances(int year, int month) {
	  ArrayList<ShiftInstance> result = new ArrayList<ShiftInstance>();
	  // iterate over all instances and filter out
	  Calendar cal = Calendar.getInstance();
	  for (ShiftInstance i : shiftInstances) {
		  cal.setTime(i.getDate());
		  if (year == -1 || cal.get(Calendar.YEAR) == year) {
			  if (year == -1 || month == -1 || cal.get(Calendar.MONTH) == month) {
				  result.add(i);
			  }
		  }
	  }
	  return result;
  }
}
