/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.drk.akrd;

import de.drk.akrd.PersonalData.Qualification;
import java.io.IOException;
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
        shiftList.add(new Shift(shiftId, begin, end, breakTime));
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
        "bankaccountAndCity", "accountNumber", "blz", "qualification", "dataKnown"};
      String[] elemetArray = new String[]{dataInstance.getFirstName(),
        dataInstance.getLastName(), dataInstance.getBankNameAndCity(),
        Integer.toString(dataInstance.getAccountNumber()),
        Integer.toString(dataInstance.getBlz()),
        dataInstance.getQualification().toString(),
        Boolean.toString(dataInstance.isDataKnown())};
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
   * loads the personal data from the data file.
   * To access the data use PersonalData.getInstance();
   * @return true if successful, false othewise (i.e. file not found)
   */
  public static boolean loadPersonalData() {
    File dataFile = new File("PersonalData.xml");
    if (dataFile.exists()) {
      SAXBuilder saxBuilder = new SAXBuilder();
      try {
        Document document = (Document) saxBuilder.build(dataFile);
        Element documentElement = document.getRootElement();
        List nodeList = documentElement.getChildren("dataset");
        for (int i = 0; i < nodeList.size(); i++) {
          Element node = (Element) nodeList.get(i);
          PersonalData.setData(
                  node.getChildText("firstName"),
                  node.getChildText("lastName"),
                  node.getChildText("bankaccountAndCity"),
                  Integer.parseInt(node.getChildText("accountNumber")),
                  Integer.parseInt(node.getChildText("blz")),
                  PersonalData.Qualification.valueOf(node.getChildText("qualification")),
                  Boolean.getBoolean(node.getChildText("dataKnown")));
        }
        return true;
      } catch (JDOMException | IOException | NumberFormatException e) {
        System.out.println("Exception in XMLEditor.loadPersonalData: " + e.getMessage());
      }
    }
    return false;
  }

  public static boolean storeShifts(ArrayList<ShiftInstance> shiftList, int year) {
    String documentName = "Schichten"+year;
    try {
      Element documentElement = new Element("Schichten"+year);
      Document document = new Document(documentElement);
      for (int i=0; i<shiftList.size(); i++) {
        Element tempElement = new Element("Shift");
        ShiftInstance tempShiftInstance = shiftList.get(i);
        tempElement.addContent(new Element("id").setText(tempShiftInstance.getId()));
        tempElement.addContent(new Element("date").setText(tempShiftInstance.getDate()));
        tempElement.addContent(new Element("actStartingTime").setText(Integer.toString(tempShiftInstance.getActualStartingTime())));
        tempElement.addContent(new Element("actEndTime").setText(Integer.toString(tempShiftInstance.getActualEndTime())));
        tempElement.addContent(new Element("actBreakTime").setText(Integer.toString(tempShiftInstance.getActualBreakTime())));
        tempElement.addContent(new Element("timeAsFloat").setText(Float.toString(tempShiftInstance.getTimeAsFloat())));
        tempElement.addContent(new Element("partner").setText(tempShiftInstance.getPartner()));
        tempElement.addContent(new Element("comment").setText(tempShiftInstance.getComment()));
        document.getRootElement().addContent(tempElement);
      }

      XMLOutputter xmlOutput = new XMLOutputter();
      xmlOutput.output(document, new FileWriter(documentName+".xml"));
      
      return true;
    } catch (IOException io) {
      System.out.println(io.getMessage());
    }
    return false;
  }
}
