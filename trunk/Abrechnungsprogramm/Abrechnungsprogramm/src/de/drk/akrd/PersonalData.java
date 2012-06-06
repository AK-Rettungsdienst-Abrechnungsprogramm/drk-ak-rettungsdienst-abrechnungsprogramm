/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.drk.akrd;

//import com.sun.crypto.provider.RC2Cipher;


//import sun.security.jca.GetInstance;

/**
 *
 * @author Jo
 */
public class PersonalData {
  private static PersonalData instance = null;
  private boolean dataSet = false;
  private String firstName;
  private String lastName;
  private String bankNameAndCity;
  private int accountNumber;
  private int blz;
  private Qualification qualification;
  private boolean dataKnown;
  private String emailAdress;
  private String calendarId;
  public static enum Qualification {
    RH, RS, RA;
    @Override
    public String toString(){
        
        switch(name())
        {
        case "RH":
        	return "Rettungshelfer";
        case "RS":
        	return "Rettungssanitäter";
        case "RA":
        	return "Rettungsassistent";
        default:
        	return name();
        }
    }
  }

  private PersonalData(){}

  /**
   * 
   * @return the data-instance if a datafile exists
   * null oterwise
   */
  public static PersonalData getInstance() {
    if ((instance == null)) {
      instance = new PersonalData();
      boolean loadingSuccessful = XMLEditor.loadPersonalData(instance);
      if(!loadingSuccessful) {
        return null;
      }
    }
    return instance;
  }
  /**
   * set personal data
   * (only necessary if PersonalData.getInstance(); returns null!)
   * @param firstName
   * @param LastName
   * @param BankNameAndCity
   * @param accountNumber
   * @param blz
   * @param qualifikation (enum PersonalData.Qualification)
   * @param dataKnown 
   */
  public boolean setData(String firstName, String LastName,
          String BankNameAndCity, int accountNumber, int blz, Qualification qualifikation,
          boolean dataKnown, String emailAdress, String calendarId) {

    this.accountNumber = accountNumber;
    this.bankNameAndCity = BankNameAndCity;
    this.blz = blz;
    this.firstName = firstName;
    this.lastName = LastName;
    this.qualification = qualifikation;
    this.dataKnown = dataKnown;
    this.dataSet = true;
    this.emailAdress = emailAdress;
    this.calendarId = calendarId;
    boolean success = XMLEditor.writePersonalData(instance);
    return success;
  }

  /**
   * 
   * @return the account number
   */
  public int getAccountNumber() {
    return accountNumber;
  }

  /**
   * 
   * @return the bank name and city as string
   */
  public String getBankNameAndCity() {
    return bankNameAndCity;
  }

  /**
   * 
   * @return the BLZ
   */
  public int getBlz() {
    return blz;
  }

  /**
   * 
   * @return true if (bank-)data is already known, false otherwise
   */
  public boolean isDataKnown() {
    return dataKnown;
  }

  /**
   * 
   * @return the first name
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * 
   * @return the last name
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * 
   * @return the qualification (see enum PersonalData.Qualification)
   */
  public Qualification getQualification() {
    return qualification;
  }
  /**
   * 
   * @return the google calendar Id
   */
  public String getCalendarId() {
    return calendarId;
  }

  /**
   * 
   * @return weather the data is already set
   */
  public boolean isDataSet() {
    return dataSet;
  }

  /**
   * 
   * @return the email adress
   */
  public String getEmailAdress() {
    return emailAdress;
  }
  
}
