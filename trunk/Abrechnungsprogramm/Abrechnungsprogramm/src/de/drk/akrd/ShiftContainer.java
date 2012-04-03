package de.drk.akrd;

import java.util.ArrayList;


// Contains all shifts and takes care of loading and managing
public class ShiftContainer {
	private static ArrayList<Shift> shifts = new ArrayList<>();
    public static final int KTW = 0;
	public static final int RTW = 1;
	public static final int KIZA = 2;
	public static final int BREISACH = 3;
    public static final int BABY = 4;
    public static final int EVENT = 5;
    public static final int KVS = 6;  
    public enum ShiftType {RTW, KTW, KIZA, BREISACH, BABY, EVENT, KVS;
    	
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
	
	public ShiftContainer()
	{
		
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
	
	public Shift[] filterShifts(ShiftContainer.ShiftType type)
	{
		ArrayList<Shift> filteredShifts = new ArrayList<Shift>();
		
		// Iterate over all shifts and filter by type
		
		for(int i=0; i < shifts.size(); i++)
		{
			if(shifts.get(i).getType() == type)
			{
				filteredShifts.add(shifts.get(i));
			}
		}
		
		return (Shift[]) filteredShifts.toArray(new Shift[filteredShifts.size()]);
	}
	
	public static Object[][] toTableData(Shift[] input)
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
}
