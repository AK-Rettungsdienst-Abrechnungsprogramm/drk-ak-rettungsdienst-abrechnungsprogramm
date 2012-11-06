/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.drk.akrd;

import java.io.IOException;
import java.text.ParseException;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
import java.io.File;
import java.util.ArrayList;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import org.jdom.output.XMLOutputter;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.Document;
import java.util.List;

/**
 *
 * @author Jo
 */
public class XMLEditor {

  private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
  private static Calendar calendar = Calendar.getInstance();

  public XMLEditor() {
  }

  /**
   * fill the given list with the shifts from the file filePath
   * @param filePath
   * @param shiftList
   * @return true if successful, false otherwise
   */
  public static boolean fillShiftList(String filePath, ArrayList<Shift> shiftList) {
    SAXBuilder saxBuilder = new SAXBuilder();
    File xmlFile = new File(filePath);
    try {
      Document document = (Document) saxBuilder.build(xmlFile);
      Element documentElement = document.getRootElement();
      List nodeList = documentElement.getChildren("Schicht");
      for (int i = 0; i < nodeList.size(); i++) {
        Element node = (Element) nodeList.get(i);
        String shiftId = node.getChildText("Schichtname");
        int begin = Integer.parseInt(node.getChildText("von"));
        int end = Integer.parseInt(node.getChildText("bis"));
        int breakTime = Integer.parseInt(node.getChildText("Pause"));

        // Identify days
        String daysString = node.getChildText("Tage");
        int days = -1;
        if (daysString.equals("Mo-Fr")) {
          days = 0;
        }
        if (daysString.equals("Sa")) {
          days = 1;
        }
        if (daysString.equals("So")) {
          days = 2;
        }
        if (daysString.equals("Mo-So")) {
          days = 3;
        }
        if (days == -1) {
          System.err.println("Could not detect days for shift! Malformed XML!");
          System.err.println("Caused by shift: " + node.getChildText("Schichtname"));
        }

        shiftList.add(new Shift(shiftId, begin, end, breakTime, days));
      }
      return true;
      // TODO: for JDK7 use Multicatch
    } catch (Exception e) { //JDOMException | IOException | NumberFormatException e) {
      System.out.println("Exception in function XMLEditor.fillShiftList: " + e.getMessage());
    }
    return false;
  }

  /**
   * write the data in dataInstance to a xml-file named "PersonalData.xml"
   * @param dataInstance
   * @return true if succesful, false otherwise
   */
  public static boolean writePersonalData(PersonalData dataInstance) {
    File dataFile = new File("data" + System.getProperty("file.separator") + "PersonalData.xml");
    boolean fileExists = dataFile.exists();
    // TODO: for JDK7 use try-with
    try {//(FileWriter fileWriter = new FileWriter(dataFile)) {
      FileWriter fileWriter = new FileWriter(dataFile);
      String[] elementNames = new String[]{"firstName", "lastName",
        "bankaccountAndCity", "accountNumber", "blz", "qualification", "dataKnown", "emailAdress", "calendarId"};
      String[] elemetArray = new String[]{dataInstance.getFirstName(),
        dataInstance.getLastName(), dataInstance.getBankNameAndCity(),
        Integer.toString(dataInstance.getAccountNumber()),
        Integer.toString(dataInstance.getBlz()),
        dataInstance.getQualification().name(),
        Boolean.toString(dataInstance.isDataKnown()),
        dataInstance.getEmailAdress(),
        dataInstance.getCalendarId()};
      fileWriter.write("<personalData>" + System.getProperty("line.separator"));
      fileWriter.write("  <dataset>" + System.getProperty("line.separator"));
      for (int i = 0; i < elementNames.length; i++) {
        fileWriter.write("    <" + elementNames[i] + ">" + elemetArray[i] + "</"
                + elementNames[i] + ">" + System.getProperty("line.separator"));
      }
      fileWriter.write("  </dataset>" + System.getProperty("line.separator"));
      fileWriter.write("</personalData>" + System.getProperty("line.separator"));
      fileWriter.flush();
      return true;
      // TODO: for JDK7 use Multicatch
    } catch (Exception e){//IOException | NullPointerException e) {
      // if the file didn't exist before delete the new, empty file
      if (!fileExists) {
        dataFile.delete();
      }
      UtilityBox.getInstance().displayErrorPopup("Persönliche Daten", "Fehler beim Speichern der Daten:\n"+e.getMessage());
    }
    return false;
  }

  private static void writePersonalDataToElement(Element element, PersonalData dataInstance) {
    if (element == null) {
      return;
    }
    String[] elementNames = new String[]{"firstName", "lastName",
      "bankaccountAndCity", "accountNumber", "blz", "qualification",
      "dataKnown", "emailAdress", "calendarId"};
    String[] elemetArray = new String[]{dataInstance.getFirstName(),
      dataInstance.getLastName(), dataInstance.getBankNameAndCity(),
      Integer.toString(dataInstance.getAccountNumber()),
      Integer.toString(dataInstance.getBlz()),
      dataInstance.getQualification().name(),
      Boolean.toString(dataInstance.isDataKnown()),
      dataInstance.getEmailAdress(),
      dataInstance.getCalendarId()};
    for (int i = 0; i < elemetArray.length; i++) {
      element.addContent(new Element(elementNames[i]).setText(elemetArray[i]));

    }
  }

  /**
   * load the personal data from the data file.
   * To access the data use PersonalData.getInstance();
   * @return true if successful, false othewise (i.e. file not found)
   */
  public static boolean loadPersonalData(PersonalData pd) {
    File dataFile = new File("data" + System.getProperty("file.separator") + "PersonalData.xml");
    if (dataFile.exists()) {
      SAXBuilder saxBuilder = new SAXBuilder();
      try {
        Document document = (Document) saxBuilder.build(dataFile);
        Element documentElement = document.getRootElement();
        List nodeList = documentElement.getChildren("dataset");
        if ((nodeList == null) || (nodeList.isEmpty())) {
          UtilityBox.getInstance().displayErrorPopup("Persönliche Daten", 
                  "Persönliche Daten konnten nicht geladen werden.\n"
                  + "(Fehlerhafte Datei?)\nErneutes Eintragen/Speichern der "
                  + "Daten wird das Problem beheben.");
          return false;
        }
        Element node = (Element) nodeList.get(0);
        ArrayList<String> persData = new ArrayList<String>();
        loadPersonalDataFromNode(node, persData);
        pd.setData(
                persData.get(0),
                persData.get(1),
                persData.get(2),
                persData.get(3),
                persData.get(4),
                persData.get(5),
                persData.get(6),
                persData.get(7),
                persData.get(8));
        return true;
        // TODO: for JDK7 use Multicatch
      } catch (Exception e){//JDOMException | IOException | NumberFormatException e) {
        System.out.println("Exception in XMLEditor.loadPersonalData: " + e.getMessage());
        return false;
      }
    }
    return false;
  }

  /**
   * load an eventually write personal data from xml-node
   * @param node the node containing the data.
   * @return String list
   */
  private static void loadPersonalDataFromNode(Element node, ArrayList<String> persDataList) {
    String emailAdress = node.getChildText("emailAdress");
    if (emailAdress.equals("null")) {
      emailAdress = null;
    }
    String calendarId = node.getChildText("calendarId");
    if (calendarId.equals("null")) {
      calendarId = null;
    }
    boolean dataKnown = false;
    if (node.getChildText("dataKnown").equals("true")) {
      dataKnown = true;
    }
    
    persDataList.add(node.getChildText("firstName"));
    persDataList.add(node.getChildText("lastName"));
    persDataList.add(node.getChildText("bankaccountAndCity"));
    persDataList.add(node.getChildText("accountNumber"));
    persDataList.add(node.getChildText("blz"));
    persDataList.add(node.getChildText("qualification"));
    persDataList.add(node.getChildText("dataKnown"));
    persDataList.add(node.getChildText("emailAdress"));
    persDataList.add(node.getChildText("calendarId"));
  }

  /**
   * load saved shifts from File "SchichtenYYYY.xml", YYYY is the year
   * if no such file exists an empty list is returned
   * @param year
   * @return ArrayList<ShiftInstance>
   */
  public static ArrayList<ShiftInstance> loadSavedShifts(int year) {
    SAXBuilder saxBuilder = new SAXBuilder();
    File xmlFile = new File("data" + System.getProperty("file.separator") + "Schichten" + year + ".xml");
    try {
      ArrayList<ShiftInstance> outputList = new ArrayList<ShiftInstance>();
      Document document = (Document) saxBuilder.build(xmlFile);
      Element rootNode = document.getRootElement();
      List monthList = rootNode.getChildren();
      for (int i = 0; i < monthList.size(); i++) {
        Element monthNode = (Element) monthList.get(i);
        ArrayList<ShiftInstance> shiftListOfMonth = loadShiftsFromNode(monthNode);
        for (Iterator<ShiftInstance> it = shiftListOfMonth.iterator(); it.hasNext();) {
          outputList.add(it.next());
        }
      }
      return outputList;
      // TODO: for JDK7 use Multicatch
    } catch (Exception e){//JDOMException | IOException | NumberFormatException e) {
      System.out.println("Exception in XMLEditor.loadSavedShifts: " + e.getMessage());
    }
    return new ArrayList<ShiftInstance>() {
    };
  }

  private static ArrayList<ShiftInstance> loadShiftsFromNode(Element node) {
    ArrayList<ShiftInstance> returnList = new ArrayList<ShiftInstance>();
    List shiftNodes = node.getChildren();
        for (int j = 0; j < shiftNodes.size(); j++) {
          Element currentNode = (Element) shiftNodes.get(j);
          returnList.add(new ShiftInstance(
                  ShiftContainer.getShiftTypeFromId(currentNode.getChildText("id")),
                  currentNode.getChildText("date"),
                  Integer.parseInt(currentNode.getChildText("actStartingTime")),
                  Integer.parseInt(currentNode.getChildText("actEndTime")),
                  Integer.parseInt(currentNode.getChildText("actBreakTime")),
                  Boolean.parseBoolean(currentNode.getChildText("preparationTime")),
                  currentNode.getChildText("partner"),
                  currentNode.getChildText("comment")));
        }
    return returnList;
  }
  /**
   * store a list of shiftInstances to a xml-file.
   * if the shift already exist it will be updated
   * @param shiftList an Arraylist<ShiftsInstance>
   * @param year
   * @return true if successful, false otherwise
   */
  public static boolean storeShifts(ArrayList<ShiftInstance> shiftList, int year) {
    String documentName = "Schichten" + year;
    String fileName = "data" + System.getProperty("file.seperator") + documentName + ".xml";
    try {
      SAXBuilder saxBuilder = new SAXBuilder();
      File xmlFile = new File(fileName);
      if (/*!xmlFile.exists()*/true) { // TODO: übergangslösung: es wird immer ein neues file angelegt
        return storeShiftsInNewFile(shiftList, documentName);
      } else {
        Document document = (Document) saxBuilder.build(xmlFile);
        Element rootNode = document.getRootElement();
        for (int i = 0; i < shiftList.size(); i++) {
          ShiftInstance currentShift = shiftList.get(i);
          String shiftDateString = currentShift.getDateString();
          String shiftIdString = currentShift.getType().name();
          calendar.setTime(sdf.parse(shiftDateString));
          int month = calendar.get(Calendar.MONTH);
          Element currentNode = findElement(rootNode.getChild("M" + month).getChildren(), shiftDateString + shiftIdString);
          // if no node for the shift exists add new node
          if (currentNode == null) {
            Element tempElement = new Element("Shift");
            ShiftInstance tempShiftInstance = shiftList.get(i);
            addShiftToElement(tempElement, tempShiftInstance);
            document.getRootElement().getChild("M" + month).addContent(tempElement);
          } // else update existing node
          else {
            currentNode.getChild("actStartingTime").setText(Integer.toString(currentShift.getActualStartingTime()));
            currentNode.getChild("actEndTime").setText(Integer.toString(currentShift.getActualEndTime()));
            currentNode.getChild("actBreakTime").setText(Integer.toString(currentShift.getActualBreakTime()));
            currentNode.getChild("preparationTime").setText(Boolean.toString(currentShift.PreparationTimeSet()));
            currentNode.getChild("partner").setText(currentShift.getPartner());
            currentNode.getChild("comment").setText(currentShift.getComment());
          }
        }
        XMLOutputter xmlOutput = new XMLOutputter();
        xmlOutput.output(document, new FileWriter(xmlFile));
        return true;
      }
    } catch (ParseException ex) {
      // TODO: for JDK7 use Multicatch
    } catch (Exception e){//JDOMException | IOException e) {
    }
    return false;
  }

  /**
   * add shift data to the element
   * @param element
   * @param shift 
   */
  private static void addShiftToElement(Element element, ShiftInstance shift) {
    if (element == null) {
      return;
    }
    element.addContent(new Element("date").setText(shift.getDateString()));
    element.addContent(new Element("id").setText(shift.getType().name()));
    element.addContent(new Element("actStartingTime").setText(Integer.toString(shift.getActualStartingTime())));
    element.addContent(new Element("actEndTime").setText(Integer.toString(shift.getActualEndTime())));
    element.addContent(new Element("actBreakTime").setText(Integer.toString(shift.getActualBreakTime())));
    element.addContent(new Element("preparationTime").setText(Boolean.toString(shift.PreparationTimeSet())));
    element.addContent(new Element("partner").setText(shift.getPartner()));
    element.addContent(new Element("comment").setText(shift.getComment()));
  }

  /**
   * find a specific shift in a list of elements using date and shift-id as
   * identifier 
   * @param elements
   * @param tag shift-date+shift-id (i.e. 01.11.2012K01)
   * @return the element if search is successful, null otherwise
   */
  private static Element findElement(List<Element> elements, String tag) {
    String text;
    for (int i = 0; i < elements.size(); i++) {
      Element currentElement = elements.get(i);
      text = currentElement.getChildText("date") + currentElement.getChildText("id");
      if (text.equals(tag)) {
        return currentElement;
      }
    }
    return null;
  }

  /**
   * create or overwrite(!) an xml-file named documentName.xml
   * @param shiftList
   * @param documentName
   * @return true if successful, false otherwise
   */
  private static boolean storeShiftsInNewFile(ArrayList<ShiftInstance> shiftList, String documentName) {
    try {
      Element documentElement = new Element(documentName);
      Document document = new Document(documentElement);
      document.setRootElement(documentElement);
      for (int month = 0; month < 12; month++) {
        Element monthElement = new Element("M" + month);
        document.getRootElement().addContent(monthElement);
      }
      for (int i = 0; i < shiftList.size(); i++) {
        Element tempElement = new Element("Shift");
        ShiftInstance tempShiftInstance = shiftList.get(i);
        addShiftToElement(tempElement, tempShiftInstance);
        calendar.setTime(sdf.parse(tempShiftInstance.getDateString()));
        int month = calendar.get(Calendar.MONTH);
        document.getRootElement().getChild("M" + month).addContent(tempElement);
      }
      XMLOutputter xmlOutput = new XMLOutputter();
      xmlOutput.output(document, new FileWriter("data/" + documentName + ".xml"));

      return true;
    } catch (ParseException ex) {
    } catch (IOException io) {
      System.out.println(io.getMessage());
    }
    return false;
  }

  public static boolean exportData(ArrayList<ShiftInstance> shiftList) {
    try {
      Element documentElement = new Element("exportData");
      Document document = new Document(documentElement);
      document.setRootElement(documentElement);
      Element personalDataElement = new Element("personalData");
      Element shiftsElement = new Element("shifts");
      documentElement.addContent(personalDataElement);
      documentElement.addContent(shiftsElement);
      PersonalData personalData = PersonalData.getInstance();
      if (personalData == null) {
        UtilityBox.getInstance().displayErrorPopup("Export", "Keine Benutzerdaten gefunden.");
        return false;
      }
      writePersonalDataToElement(personalDataElement, personalData);
      for (int i = 0; i < shiftList.size(); i++) {
        Element tempElement = new Element("Shift");
        ShiftInstance tempShiftInstance = shiftList.get(i);
        addShiftToElement(tempElement, tempShiftInstance);
        documentElement.getChild("shifts").addContent(tempElement);
      }
      XMLOutputter xmlOutput = new XMLOutputter();
      String fileName = UtilityBox.getInstance().saveDialog(".xml", "SchichtenIrgendwann", "XML-Dateien");
      if (fileName != null) {
        xmlOutput.output(document, new FileWriter(fileName));
        return true;
      }
      return false;
    } catch (IOException io) {
      UtilityBox.getInstance().displayErrorPopup("Export", "Fehler beim "
              + "Exportieren der Daten:\n" + io.getMessage());
      return false;
    }
  }

  public static ArrayList<ShiftInstance> importData(String filePath, ArrayList<String> persData) {
    ArrayList<ShiftInstance> returnList = new ArrayList<ShiftInstance>();
    if (filePath == null) {
      return null;
    }
    SAXBuilder saxBuilder = new SAXBuilder();
    File xmlFile = new File(filePath);
    try {
      Document document = (Document) saxBuilder.build(xmlFile);
      Element rootNode = document.getRootElement();
      List personalDataList = rootNode.getChildren("personalData");
      List shiftList = rootNode.getChildren("shifts");
      if (personalDataList == null||shiftList==null||personalDataList.size() != 1|| shiftList.size() != 1) {
        UtilityBox.getInstance().displayErrorPopup("Import", "Keine gültige Import-Datei.");
        return null;
      }
      // load the personal Data
      loadPersonalDataFromNode((Element)personalDataList.get(0), persData);
      // load shifts
      returnList = loadShiftsFromNode((Element)shiftList.get(0));
      return returnList;
      // TODO: for JDK7 use Multicatch
    } catch (Exception e){//JDOMException | IOException | NumberFormatException e) {
      UtilityBox.getInstance().displayErrorPopup("Import", "Fehler beim Importieren der Daten:\n"+e.getMessage());
      return null;
    }
  }
}
