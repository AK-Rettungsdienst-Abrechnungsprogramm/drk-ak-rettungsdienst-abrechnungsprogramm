package de.drk.akrd;

import java.util.ArrayList;


// Contains all shifts and takes care of loading and managing
public class ShiftContainer {
	
	// a pointer to the posessing mainWindow
	private MainWindow mainWindow;
	
	private static ArrayList<Shift> shifts = new ArrayList<>();
    public enum ShiftType {Alle, RTW, KTW, KIZA, BREISACH, BABY, EVENT, KVS;
    	
    	// Override the toString method to get nicer strings for the shiftTypeChooser
    	@Override
        public String toString(){
            
            switch(name())
            {
            case "RTW":
            	return "RTW";
            case "KTW":
            	return "KTW";
            case "KIZA":
            	return "RD Kirchzarten";
            case "BREISACH":
            	return "RD Breisach";
            case "BABY":
            	return "Baby NAW";
            case "EVENT":
            	return "Sandienst";
            case "KVS":
            	return "KV Dienst";
            default:
            	return name();
            }
        }
    }
    
    // Holds all registered shifts.
    public ArrayList<ShiftInstance> shiftInstances = new ArrayList<ShiftInstance>();
	

	public ShiftContainer(MainWindow mainWindow)
	{
		this.mainWindow = mainWindow;
	}
	
	public void loadShifts(String shiftfilePath)
	{
        XMLEditor.fillShiftList("Schichten.xml", shifts);
	}
	
	public Shift[] getShifts()
	{
		return (Shift[]) shifts.toArray(new Shift[shifts.size()]);
	}
    
    public static  ArrayList<Shift> getShiftsAsList() {
      return shifts;
    }
	/**
	 * This method is private, since 
	 * @param type
	 * @param day - 0 = weekday, 1 = holyday or saturday, 2 = sunday, -1 = no date filtering
	 * @return
	 */
	public Shift[] filterShifts(ShiftContainer.ShiftType type, int day)
	{
		ArrayList<Shift> filteredShifts = new ArrayList<Shift>();
		ArrayList<Shift> resultingShifts = new ArrayList<Shift>();
		
		// Iterate over all shifts and filter by type
		
		for(int i=0; i < shifts.size(); i++)
		{
			if(type == ShiftType.Alle || shifts.get(i).getType() == type)
			{
				filteredShifts.add(shifts.get(i));
			}
		}
		
		// iterate over all filtered shifts and filter again by date
		
		for(int i=0; i < filteredShifts.size(); i++)
		{
			Shift element = filteredShifts.get(i);
			if(day == -1 || element.getDays() == day || element.getDays() == 3)
			{
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
	public static Object[][] shiftToTableData(Shift[] input)
	{
		ArrayList<String[]> result = new ArrayList<String[]>();
		
		for(int i=0; i < input.length; i++)
		{
			ArrayList<String> entry = new ArrayList<String>();
			entry.add(input[i].getId());
			entry.add(Integer.toString(input[i].getStartingTime()));
			entry.add(Integer.toString(input[i].getEndTime()));
			entry.add(Integer.toString(input[i].getBreakTime()));
			
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
	public static Object[][] shiftInstancesToTableData(ShiftInstance[] input)
	{
		ArrayList<String[]> result = new ArrayList<String[]>();
		
		for(int i=0; i < input.length; i++)
		{
			ArrayList<String> entry = new ArrayList<String>();
			entry.add(input[i].getDate());
			entry.add(UtilityBox.createTimeStringFromInt(input[i].getActualStartingTime()));
			entry.add(UtilityBox.createTimeStringFromInt(input[i].getActualEndTime()));
			entry.add(UtilityBox.createTimeStringFromInt(input[i].getActualBreakTime()));
			entry.add(Float.toString(input[i].getTimeAsFloat()));
			entry.add(input[i].getPartner());
			entry.add(input[i].getComment());
			
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
	protected void  registerShift(ShiftContainer.ShiftType type, String date, int actualStart, int actualEnd, int actualBreak, String partner, String comment)
	{
		// calculate time in float
		float startingFloat = ShiftInstance.timeToFloat(actualStart);
		float endFloat = ShiftInstance.timeToFloat(actualEnd);
		float breakFloat = ShiftInstance.timeToFloat(actualBreak);

		// account for shifts ending after midnight
		if(endFloat < startingFloat) endFloat += 24;
		
		float shiftAsFloat = endFloat - startingFloat - breakFloat;
		
		ShiftInstance entry = new ShiftInstance(type, date, actualStart, actualEnd, actualBreak, shiftAsFloat , partner, comment);
		
		shiftInstances.add(entry);
		
		mainWindow.updateRegisteredShifts();
		
	}
    
    public static ShiftType getShiftTypeFromId(String id) {
    switch (id.substring(0, 2)) {
      case "KV":
        return ShiftContainer.ShiftType.KVS;
      case "KT":
        return ShiftContainer.ShiftType.KIZA;
      case "KN":
        return ShiftContainer.ShiftType.KIZA;
      default:
        switch (id.substring(0, 1)) {
          case "K":
            return ShiftContainer.ShiftType.KTW;
          case "R":
            return ShiftContainer.ShiftType.RTW;
          case "B":
            return ShiftContainer.ShiftType.BREISACH;
          default:
            return ShiftContainer.ShiftType.BABY;
        }
    }
  }
}
