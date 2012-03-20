package de.drk.akrd;

// Represents a shift
public class Shift {
	

	private String id;
	private int startingTime;
	private int endTime;
	private int type;
	private int breakTime;
	private boolean external; // external shift? (i.e. Breisach and Kirchzarten)
	
	
	public Shift(String id, int startingTime, int endTime, boolean external, int type) {
		super();
		this.id = id;
		this.startingTime = startingTime;
		this.endTime = endTime;
		this.external = external;
		this.type = type;
		this.breakTime = 30;
	}


	/**
	 * @return the breakTime
	 */
	public int getBreakTime() {
		return breakTime;
	}


	/**
	 * @param breakTime the breakTime to set
	 */
	public void setBreakTime(int breakTime) {
		this.breakTime = breakTime;
	}


	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}


	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}


	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.id;
	}


	/**
	 * @return the startingTime
	 */
	public int getStartingTime() {
		return startingTime;
	}


	/**
	 * @return the endTime
	 */
	public int getEndTime() {
		return endTime;
	}


	/**
	 * @return the external
	 */
	public boolean isExternal() {
		return external;
	}
}
