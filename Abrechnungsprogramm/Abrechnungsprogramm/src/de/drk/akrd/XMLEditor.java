/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.drk.akrd;

import de.drk.akrd.PersonalData.Qualification;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
//import org.w3c.dom.Document;
import org.jdom.JDOMException;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
//import org.w3c.dom.Element;
import java.io.File;
import java.util.ArrayList;
import org.xml.sax.SAXException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import org.jdom.Attribute;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.Document;
import java.util.List;
import org.jdom.JDOMException;

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
        if(daysString.equals("Mo-Fr")) days = 0;
        if(daysString.equals("Sa")) days = 1;
        if(daysString.equals("So")) days = 2;
        if(daysString.equals("Mo-So")) days = 3;
        if(days == -1)
        	{
        	System.err.println("Could not detect days for shift! Malformed XML!");
        	System.err.println("Caused by shift: " + node.getChildText("Schichtname"));
        	}
        
        shiftList.add(new Shift(shiftId, begin, end, breakTime, days));
      }
      return true;
    } catch (JDOMException | IOException | NumberFormatException e) {
      System.out.println("Exception in function XMLEditor.fillShiftList: " + e.getMessage());
    }
    return false;
  }

  private static String getTagValue(String tag, Element element) {
    /*NodeList nList = element.getElementsByTagName(tag).item(0).getChildNodes();
    Node nValue = (Node) nList.item(0);*/
    return "";//nValue.getNodeValue();
  }

  /**
   * write the data in dataInstance to a xml-file named "PersonalData.xml"
   * @param dataInstance
   * @return true if succesful, false otherwise
   */
  public static boolean writePersonalData(PersonalData dataInstance) {
    File dataFile = new File("PersonalData.xml");
    try (FileWriter fileWriter = new FileWriter(dataFile)) {
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
    } catch (IOException e) {
      System.out.println("Exception in XMLEditor.writePersonalData: " + e.getMessage());
    }
    return false;

  }

  /**
   * load the personal data from the data file.
   * To access the data use PersonalData.getInstance();
   * @return true if successful, false othewise (i.e. file not found)
   */
  public static boolean loadPersonalData(PersonalData pd) {
    File dataFile = new File("PersonalData.xml");
    if (dataFile.exists()) {
      SAXBuilder saxBuilder = new SAXBuilder();
      try {
        Document document = (Document) saxBuilder.build(dataFile);
        Element documentElement = document.getRootElement();
        List nodeList = documentElement.getChildren("dataset");
        for (int i = 0; i < nodeList.size(); i++) {
          Element node = (Element) nodeList.get(i);
          String emailAdress = node.getChildText("emailAdress");
          if(emailAdress.equals("null")) emailAdress = null;
          String calendarId = node.getChildText("calendarId");
          if(calendarId.equals("null")) calendarId= null;
          boolean dataKnown = false;
          if (node.getChildText("dataKnown").equals("true"))
            dataKnown = true;
          pd.setData(
                  node.getChildText("firstName"),
                  node.getChildText("lastName"),
                  node.getChildText("bankaccountAndCity"),
                  Integer.parseInt(node.getChildText("accountNumber")),
                  Integer.parseInt(node.getChildText("blz")),
                  PersonalData.Qualification.valueOf(node.getChildText("qualification")),
                  dataKnown,
                  emailAdress,
                  calendarId);
        }
        return true;
      } catch (JDOMException | IOException | NumberFormatException e) {
        System.out.println("Exception in XMLEditor.loadPersonalData: " + e.getMessage());
        return false;
      }
    }
    return false;
  }

  /**
   * load saved shifts from File "SchichtenYYYY.xml", YYYY is the year
   * @param year
   * @return ArrayList<ArrayList<ShiftInstance>>
   * the List contains 12 ArrayList<ShiftInstance> where each stands for a month
   */
  public static ArrayList<ArrayList<ShiftInstance>> loadSavedShifts(int year) {
    SAXBuilder saxBuilder = new SAXBuilder();
    File xmlFile = new File("Schichten"+year+".xml");
    try {
      ArrayList<ArrayList<ShiftInstance>> outputList = new ArrayList<>();
      Document document = (Document) saxBuilder.build(xmlFile);
      Element rootNode = document.getRootElement();
      List monthList = rootNode.getChildren();
      for (int i=0; i < monthList.size(); i++) {
        Element monthNode = (Element) monthList.get(i);
        ArrayList<ShiftInstance> shiftListOfMonth = new ArrayList<>();
        List shiftNodesOfMonth = monthNode.getChildren();
        for (int j=0; j<shiftNodesOfMonth.size(); j++) {
          Element currentNode = (Element) shiftNodesOfMonth.get(j);
          shiftListOfMonth.add(new ShiftInstance(
                  ShiftContainer.getShiftTypeFromId(currentNode.getChildText("id")), 
                  currentNode.getChildText("date"),
                  Integer.parseInt(currentNode.getChildText("actStartingTime")),
                  Integer.parseInt(currentNode.getChildText("actEndTime")),
                  Integer.parseInt(currentNode.getChildText("actBreakTime")),
                  currentNode.getChildText("partner"),
                  currentNode.getChildText("comment")));
        }
        outputList.add(shiftListOfMonth);
      }
      return outputList;
    } catch (JDOMException | IOException | NumberFormatException e) {
      System.out.println("Exception in XMLEditor.loadSavedShifts: "+e.getMessage());
    }
    return new ArrayList<ArrayList<ShiftInstance>>() {};
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
    String fileName = documentName + ".xml";
    try {
      SAXBuilder saxBuilder = new SAXBuilder();
      File xmlFile = new File(fileName);
      if (!xmlFile.exists()) {
        return storeShiftsInNewFile(shiftList, documentName);
      } else {
        Document document = (Document) saxBuilder.build(xmlFile);
        Element rootNode = document.getRootElement();
        for (int i = 0; i < shiftList.size(); i++) {
          ShiftInstance currentShift = shiftList.get(i);
          String shiftDateString = currentShift.getDate();
          String shiftIdString = currentShift.getType().name();
          calendar.setTime(sdf.parse(shiftDateString));
          int month = calendar.get(Calendar.MONTH);
          Element currentNode = findElement(rootNode.getChild("M" + month).getChildren(), shiftDateString + shiftIdString);
          // if no node for the shift exists add new node
          if (currentNode == null) {
            System.out.println("add new shift:"+currentShift.getType().name()+" "+currentShift.getDate());
            Element tempElement = new Element("Shift");
            ShiftInstance tempShiftInstance = shiftList.get(i);
            addShiftToElement(tempElement, tempShiftInstance);
            document.getRootElement().getChild("M" + month).addContent(tempElement);
          } // else update existing node
          else {
            System.out.println("update shift: " + shiftDateString + shiftIdString);
            currentNode.getChild("actStartingTime").setText(Integer.toString(currentShift.getActualStartingTime()));
            currentNode.getChild("actEndTime").setText(Integer.toString(currentShift.getActualEndTime()));
            currentNode.getChild("actBreakTime").setText(Integer.toString(currentShift.getActualBreakTime()));
            currentNode.getChild("timeAsFloat").setText(Float.toString(currentShift.getTimeAsFloat()));
            currentNode.getChild("partner").setText(currentShift.getPartner());
            currentNode.getChild("comment").setText(currentShift.getComment());
          }
        }
        XMLOutputter xmlOutput = new XMLOutputter();
        xmlOutput.output(document, new FileWriter(xmlFile));
        return true;
      }
    } catch (ParseException ex) {
    } catch (JDOMException | IOException e) {
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
    element.addContent(new Element("date").setText(shift.getDate()));
    element.addContent(new Element("id").setText(shift.getType().name()));
    element.addContent(new Element("actStartingTime").setText(Integer.toString(shift.getActualStartingTime())));
    element.addContent(new Element("actEndTime").setText(Integer.toString(shift.getActualEndTime())));
    element.addContent(new Element("actBreakTime").setText(Integer.toString(shift.getActualBreakTime())));
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
        calendar.setTime(sdf.parse(tempShiftInstance.getDate()));
        int month = calendar.get(Calendar.MONTH);
        document.getRootElement().getChild("M" + month).addContent(tempElement);
      }
      XMLOutputter xmlOutput = new XMLOutputter();
      xmlOutput.output(document, new FileWriter(documentName + ".xml"));

      return true;
    } catch (ParseException ex) {
    } catch (IOException io) {
      System.out.println(io.getMessage());
    }
    return false;
  }
}
