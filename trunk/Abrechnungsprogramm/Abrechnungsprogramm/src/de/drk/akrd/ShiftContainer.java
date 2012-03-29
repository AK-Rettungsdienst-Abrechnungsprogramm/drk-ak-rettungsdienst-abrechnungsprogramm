package de.drk.akrd;

import java.util.ArrayList;
import java.util.List;


// Contains all shifts and takes care of loading and managing
public class ShiftContainer {
	
	public static final int KTW = 0;
	public static final int RTW = 1;
	public static final int KIZA = 2;
	public static final int BREISACH = 3;
    public static final int BABY = 4;
    public static final int EVENT = 5;
    public static final int KVS = 6;
	
	private ArrayList<Shift> shifts = new ArrayList<>();
	
	public ShiftContainer()
	{
		
	}
	
	public void loadShifts(String shiftfilePath)
	{
	/*	
      shifts.add(new Shift("K01", 900, 1800, false, KTW));
		shifts.add(new Shift("K02", 900, 1800, false, KTW));
		shifts.add(new Shift("K03", 900, 1800, false, KTW));
		shifts.add(new Shift("K04", 900, 1800, false, KTW));
		shifts.add(new Shift("K05", 900, 1800, false, KTW));
		shifts.add(new Shift("K06", 900, 1800, false, KTW));
		shifts.add(new Shift("R1F", 900, 1800, false ,RTW));
		shifts.add(new Shift("R2F", 900, 1800, false ,RTW));
		shifts.add(new Shift("BRT", 900, 1800, false ,BREISACH));
		shifts.add(new Shift("KRT", 900, 1800, false ,KIZA));
		shifts.add(new Shift("KRN", 1800, 900, false ,KIZA));
     * 
     */
        XMLEditor.fillShiftList("Schichten.xml", shifts);
	}
	
	public Shift[] getShifts()
	{
		return (Shift[]) shifts.toArray(new Shift[shifts.size()]);
	}
	
	public Shift[] filterShifts(int type)
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
