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
  private String address;
  private String bankNameAndCity;
  private String accountNumber;
  private int blz;
  private Qualification qualification;
  private boolean dataKnown;
private boolean addressKnown;
  public static enum Qualification {
    RH, RS, RA;
    @Override
    public String toString(){
        // TODO: for JDK7 use switch
//        switch(name())
//        {
//        case "RH":
//        	return "Rettungshelfer";
//        case "RS":
//        	return "Rettungssanitäter";
//        case "RA":
//        	return "Rettungsassistent";
//        default:
//        	return name();
//        }
      if ("RH".equals(name())) {
        return "Rettungshelfer";
      } else if("RS".equals(name())) {
        return "Rettungssanitäter";
      } else if ("RA".equals(name())) {
        return "Rettungsassistent";
      }
      return name();
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
    }
    return instance;
  }
  /**
   * set personal data
   * (only necessary if PersonalData.getInstance(); returns null!)
   * @param firstName
   * @param LastName
   * @param address
   * @param BankNameAndCity
   * @param accountNumber
   * @param blz
   * @param qualifikation (enum PersonalData.Qualification)
   * @param dataKnown
   */
  public boolean setData(String firstName, String LastName, String address,
          String BankNameAndCity, String accountNumber, int blz, Qualification qualifikation,
          boolean dataKnown, boolean addressKnown) {

    this.accountNumber = accountNumber;
    this.bankNameAndCity = BankNameAndCity;
    this.blz = blz;
    this.firstName = firstName;
    this.lastName = LastName;
    this.address = address;
    this.qualification = qualifikation;
    this.dataKnown = dataKnown;
    this.dataSet = true;

    this.addressKnown = addressKnown;
    boolean success = XMLEditor.writePersonalData(instance);
    return success;
  }
  /**
   * set personal data using Strings (i.e. the read Strings from an XML-file)
   * (only necessary if PersonalData.getInstance(); returns null!)
   * @param firstName
   * @param LastName
   * @param BankNameAndCity
   * @param accountNumber
   * @param blz
   * @param qualifikation
   * @param dataKnown
   * @param emailAdress
   * @param calendarId
   * @return
   */
  public boolean setData(String firstName, String lastName, String address,
          String bankNameAndCity, String accountNumber, String blz, String quali,
          String dataKnown, String emailAdress, String calendarId, String addressKnown){

    if (emailAdress.equals("null")) {
      emailAdress = null;
    }
    if (calendarId.equals("null")) {
      calendarId = null;
    }
    boolean isDataKnown = false;
    if (dataKnown.equals("true")) {
      isDataKnown = true;
    }
    boolean success = PersonalData.getInstance().setData(
            firstName,
            lastName,
            address,
            bankNameAndCity,
            accountNumber,
            Integer.parseInt(blz),Qualification.valueOf(quali),
            isDataKnown,
            addressKnown.equals("true"));
    return success;
  }

  /**
   *
   * @return the account number
   */
  public String getAccountNumber() {
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
   * @return the address
   */
  public String getAddress() {
    return address;
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
   * @return weather the data is already set
   */
  public boolean isDataSet() {
    return dataSet;
  }


public boolean addressKnown() {
	// TODO Auto-generated method stub
	return addressKnown;
}

}
