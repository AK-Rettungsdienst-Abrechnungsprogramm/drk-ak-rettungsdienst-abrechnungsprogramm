/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.drk.akrd;
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
/**
 *
 * @author Jo
 */
public class XMLReader {

  public XMLReader() {
  }
  public static void fillShiftList(String filePath, ArrayList<Shift> shiftList) {
    try {
      File xmlFile = new File(filePath);
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
      Document document = docBuilder.parse(xmlFile);
      document.getDocumentElement().normalize();
      NodeList nodeList = document.getElementsByTagName("Schicht");
      for(int i=0; i<nodeList.getLength();i++) {
        Node node = nodeList.item(i);
        if(node.getNodeType() == Node.ELEMENT_NODE) {
          Element element = (Element) node;
          String shiftId = getTagValue("Schichtname", element);
          int begin = Integer.parseInt(getTagValue("von", element));
          int end = Integer.parseInt(getTagValue("bis", element));
          int breakTime = Integer.parseInt(getTagValue("Pause", element));
          shiftList.add(new Shift(shiftId, begin, end, breakTime));
          
        }
      }
      System.out.println("shiftList Länge: "+shiftList.size());
    }
    catch(ParserConfigurationException | SAXException | IOException | NumberFormatException e) {
      System.out.println("Exception in XMLReader.fillShiftList:"+e);
    }
  }
  private static String getTagValue(String tag, Element element) {
    NodeList nList = element.getElementsByTagName(tag).item(0).getChildNodes();
    Node nValue = (Node) nList.item(0);
    return nValue.getNodeValue();
  }
}
