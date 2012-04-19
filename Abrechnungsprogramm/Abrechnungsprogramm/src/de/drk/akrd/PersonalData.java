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
  private static boolean dataSet = false;
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
   * @return the data-instance if a datafile exists or the data is already set,
   * null oterwise
   */
  public static PersonalData getInstance() {
    if ((instance == null) || (!dataSet)) {
      instance = new PersonalData();
      boolean loadingSuccessful = XMLEditor.loadPersonalData();
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
  public static void setData(String firstName, String LastName,
          String BankNameAndCity, int accountNumber, int blz, Qualification qualifikation,
          boolean dataKnown, String emailAdress, String calendarId) {
    if(instance==null) {
      instance = new PersonalData();
    }
    instance.accountNumber = accountNumber;
    instance.bankNameAndCity = BankNameAndCity;
    instance.blz = blz;
    instance.firstName = firstName;
    instance.lastName = LastName;
    instance.qualification = qualifikation;
    instance.dataKnown = dataKnown;
    instance.dataSet = true;
    dataSet = true;
    instance.emailAdress = emailAdress;
    instance.calendarId = calendarId;
    XMLEditor.writePersonalData(instance);
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
    return instance.lastName;
  }

  /**
   * 
   * @return the qualification (see enum PersonalData.Qualification)
   */
  public Qualification getQualification() {
    return instance.qualification;
  }
  /**
   * 
   * @return the google calendar Id
   */
  public String getCalendarId() {
    return instance.calendarId;
  }

  /**
   * 
   * @return weather the data is already set
   */
  public static boolean isDataSet() {
    return instance.dataSet;
  }

  /**
   * 
   * @return the email adress
   */
  public String getEmailAdress() {
    return instance.emailAdress;
  }
  
}
