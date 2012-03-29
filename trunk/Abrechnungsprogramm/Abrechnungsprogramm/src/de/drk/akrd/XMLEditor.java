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
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.util.ArrayList;
import org.xml.sax.SAXException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.util.Properties;

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
    try {
      File xmlFile = new File(filePath);
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
      Document document = docBuilder.parse(xmlFile);
      document.getDocumentElement().normalize();
      NodeList nodeList = document.getElementsByTagName("Schicht");
      for (int i = 0; i < nodeList.getLength(); i++) {
        Node node = nodeList.item(i);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
          Element element = (Element) node;
          String shiftId = getTagValue("Schichtname", element);
          int begin = Integer.parseInt(getTagValue("von", element));
          int end = Integer.parseInt(getTagValue("bis", element));
          int breakTime = Integer.parseInt(getTagValue("Pause", element));
          shiftList.add(new Shift(shiftId, begin, end, breakTime));

        }
      }
      System.out.println("shiftList Länge: " + shiftList.size());
      return true;
    } catch (ParserConfigurationException | SAXException | IOException | NumberFormatException e) {
      System.out.println("Exception in XMLReader.fillShiftList:" + e);
      return false;
    }
  }

  private static String getTagValue(String tag, Element element) {
    NodeList nList = element.getElementsByTagName(tag).item(0).getChildNodes();
    Node nValue = (Node) nList.item(0);
    return nValue.getNodeValue();
  }

  /**
   * write the data in dataInstance to a xml-file named "PersonalData.xml"
   * @param dataInstance
   * @return true if succesful, false otherwise
   */
  public static boolean writePersonalData(PersonalData dataInstance) {
    try {
      File dataFile = new File("PersonalData.xml");
      FileWriter fileWriter = new FileWriter(dataFile);
      String[] elementNames = new String[]{"firstName", "lastName",
        "bankaccountAndCity", "accountNumber", "blz", "qualification", "dataKnown"};
      String[] elemetArray = new String[]{dataInstance.getFirstName(),
        dataInstance.getLastName(), dataInstance.getBankNameAndCity(),
        Integer.toString(dataInstance.getAccountNumber()),
        Integer.toString(dataInstance.getBlz()),
        dataInstance.getQualification().toString(),
        Boolean.toString(dataInstance.isDataKnown())};
      fileWriter.write("<personalData>"+System.getProperty("line.separator"));
      //fileWriter.write("  <dataset>"+System.getProperty("line.separator"));
      for (int i = 0; i < elementNames.length; i++) {
        fileWriter.write("    <" + elementNames[i] + ">" + elemetArray[i] + "</"
                + elementNames[i] + ">" + System.getProperty("line.separator"));
      }
      fileWriter.write("</personalData>"+System.getProperty("line.separator"));
      fileWriter.flush();
      fileWriter.close();
      return true;
    } catch (java.io.IOException e) {
      return false;
    }
  }

  /**
   * loads the personal data from the data file.
   * To access the data use PersonalData.getInstance();
   * @return true if successful, false othewise (i.e. file not found)
   */
  public static boolean loadPersonalData() {
    File dataFile = new File("PersonalData.xml");
    if (dataFile.exists()) {
      try {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
        Document document = docBuilder.parse(dataFile);
        document.getDocumentElement().normalize();
        NodeList nodeList = document.getElementsByTagName("personalData");
        for (int i = 0; i < nodeList.getLength(); i++) {
          Node node = nodeList.item(i);
          if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            PersonalData.setData(
            getTagValue("firstName", element),
            getTagValue("lastName", element),
            getTagValue("bankaccountAndCity", element),
            Integer.parseInt(getTagValue("accountNumber", element)),
            Integer.parseInt(getTagValue("blz", element)),
            PersonalData.Qualification.valueOf(getTagValue("qualification", element)),
            Boolean.getBoolean(getTagValue("dataKnown", element)));
          }
        }
        return true;
      } catch (ParserConfigurationException | SAXException | IOException | NumberFormatException e) {
        System.out.println("Exception in XMLReader.fillShiftList:" + e);
        return false;
      }
    }
    return false;
  }
}
