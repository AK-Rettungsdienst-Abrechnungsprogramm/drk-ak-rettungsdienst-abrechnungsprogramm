package de.drk.akrd;

// Represents a shift
public class Shift {

  private String id;
  private int startingTime;
  private int endTime;
  private ShiftContainer.ShiftType type;
  private int breakTime;
  private boolean external; // external shift? (i.e. Breisach and Kirchzarten)

  public Shift(String id, int startingTime, int endTime, int breakTime) {
    super();
    this.id = id;
    this.startingTime = startingTime;
    this.endTime = endTime;
    this.breakTime = breakTime;
    switch (id.substring(0, 2)) {
      case "KV":
        type = ShiftContainer.ShiftType.KVS;
        break;
      case "KT":
        type = ShiftContainer.ShiftType.KIZA;
        break;
      case "KN":
        type = ShiftContainer.ShiftType.KIZA;
        break;
      default:
        switch (id.substring(0, 1)) {
          case "K":
            type = ShiftContainer.ShiftType.KTW;
            break;
          case "R":
            type = ShiftContainer.ShiftType.RTW;
            break;
          case "B":
            type = ShiftContainer.ShiftType.BREISACH;
            break;
          default:
            type = ShiftContainer.ShiftType.BABY;
        }
    }
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
  public ShiftContainer.ShiftType getType() {
    return type;
  }

  /**
   * @param type the type to set
   */
  public void setType(ShiftContainer.ShiftType type) {
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
