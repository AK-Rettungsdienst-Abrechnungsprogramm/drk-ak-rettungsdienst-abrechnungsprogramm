package de.drk.akrd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ProgressMonitorInputStream;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author Jo
 */
public class Update {

  private static final String UPDATE_DATA_FILE_URL = "https://raw.githubusercontent.com/AK-Rettungsdienst-Abrechnungsprogramm/drk-ak-rettungsdienst-abrechnungsprogramm/master/Abrechnungsprogramm/Abrechnungsprogramm/data/updatedata.xml";
  private static final String UPDATE_DATA_FILE_PATH = "data" + System.getProperty("file.separator") + "updatedata.xml";
  public static final String SHIFT_FILE_PATH = "data" + System.getProperty("file.separator") + "Schichten.xml";
  public static final String SALARY_FILE_PATH = "data" + System.getProperty("file.separator") + "Salary.xml";
  private static float latestProgramVersion = 0.0f;
  private static float latestShiftFileVersion = 0.0f;
  private static float latestSalaryFileVersion = 0.0f;
  public static String SHIFT_FILE_URL = "";
  public static String SALARY_FILE_URL = "";

  /**
   * read shiftfile and salaryfile-versions and safe in MainWindow fields
   */
  public static void readFileVersions() {
    FileReader xmlFileReader;
    try {
      xmlFileReader = new FileReader(new File(SHIFT_FILE_PATH));
      MainWindow.SHIFT_FILE_VERSION = readVersionFromFileReader(xmlFileReader);
    } catch (FileNotFoundException ex) {
      System.out.println("Exception in function XMLEditor.fillShiftList: " + ex.getStackTrace().toString());
    }
    try {
      xmlFileReader = new FileReader(new File(SALARY_FILE_PATH));
      MainWindow.SALARY_FILE_VERSION= readVersionFromFileReader(xmlFileReader);
    } catch (FileNotFoundException ex) {
      System.out.println("Exception in function XMLEditor.fillShiftList: " + ex.getStackTrace().toString());
    }
  }

  private static float readVersionFromFileReader(FileReader fileReader) {
    try {
      SAXBuilder saxBuilder = new SAXBuilder();
      Document document = (Document) saxBuilder.build(fileReader);
      Element documentElement = document.getRootElement();
      String documentVersionString = documentElement.getAttributeValue("version");
      float version = Float.parseFloat(documentVersionString);
      return version;
    } catch (Exception ex) { //TODO: multicatch IOException | JDOMException
      System.err.println("Exception in Update.readVersionFromFileReader: " + ex.getStackTrace().toString());
      return 0f;
    }
  }

  /**
   * download new updatedata file and check versions
   */
  public static void readUpdateData() {
    boolean downloadSuccess = downloadNewUpdatefile(UPDATE_DATA_FILE_URL);
    if (downloadSuccess) {
    SAXBuilder saxBuilder = new SAXBuilder();
    try {
      FileReader xmlFileReader = new FileReader(new File(UPDATE_DATA_FILE_PATH));
      Document document = (Document) saxBuilder.build(xmlFileReader);
      Element documentElement = document.getRootElement();
      latestProgramVersion = Float.parseFloat(documentElement.getChildText("program-version"));
      latestShiftFileVersion = Float.parseFloat(documentElement.getChildText("shiftfile-version"));
      latestSalaryFileVersion = Float.parseFloat(documentElement.getChildText("salaryfile-version"));

      SHIFT_FILE_URL = documentElement.getChildText("shiftfile-url");
      SALARY_FILE_URL = documentElement.getChildText("salaryfile-url");
    } catch (Exception e) { // TODO: multicatch in 1.7 JDOMException | IOException | FileNotFoundException e
      System.out.println("Exception in function XMLEditor.fillShiftList: " + e.getMessage());
    }
    }
  }

  /**
   * download current shiftlist
   *
   * @return
   */
  public static float downloadNewShiftFile() {
    try {
      Update.downloadFile(SHIFT_FILE_URL, "Schichten.xml");
      readFileVersions();
      UtilityBox.getInstance().displayInfoPopup("Schichten.xml", "Die Schichtliste wurde aktualisiert.");
    } catch (Exception ex) {
      try {
        // TODO: display URL as Link
        URL url = new URL(SHIFT_FILE_URL);
        UtilityBox.getInstance().displayErrorPopup("Download", "Fehler beim Download. Die Datei kann unter\n" + url + "\nheruntergeladen werden.");
      } catch (MalformedURLException ex1) {
        Logger.getLogger(XMLEditor.class.getName()).log(Level.SEVERE, null, ex1);
      }
    }

    return 0f;
  }

  public static float downloadNewSalaryFile() {
    try {
      Update.downloadFile(SALARY_FILE_URL, "Salary.xml");
      readFileVersions();
      UtilityBox.getInstance().displayInfoPopup("Salary.xml", "Die Gehaltsliste wurde aktualisiert.");
    } catch (Exception ex) {
      try {
        // TODO: display URL as Link
        URL url = new URL(SALARY_FILE_URL);
        UtilityBox.getInstance().displayErrorPopup("Download", "Fehler beim Download. Die Datei kann unter\n" + url + "\nheruntergeladen werden.");
      } catch (MalformedURLException ex1) {
        Logger.getLogger(XMLEditor.class.getName()).log(Level.SEVERE, null, ex1);
      }
    }

    return 0f;
  }

  public static void downloadFile(String http, String fileName) throws Exception {
//    throw new NotImplementedException();
    URL url = new URL(http);
    URLConnection uc = url.openConnection();
    InputStream is = (InputStream) uc.getInputStream();
    ProgressMonitorInputStream pmis = new ProgressMonitorInputStream(null, "Downloading...", is);
    pmis.getProgressMonitor().setMaximum(uc.getContentLength());
    // check if data dir exists and if not create it
    File dataDir = new File("data");
    if (!dataDir.exists()) {
      dataDir.mkdir();
    }

    File outputFile = new File("data" + File.separatorChar + fileName);
    FileOutputStream out = new FileOutputStream(outputFile);

    byte[] buffer = new byte[1024];
    for (int n; (n = pmis.read(buffer)) != -1; out.write(buffer, 0, n));

    pmis.close();
    out.flush();
    out.close();
  }

  private static boolean downloadNewUpdatefile(String http) {
    try {
      Update.downloadFile(http, "updatedata.xml");
      //UtilityBox.getInstance().displayInfoPopup("updateinfo.xml", "Updateinformationen einholen...");
      return true;
    } catch (Exception ex) {
      try {
        // TODO: display URL as Link
        URL url = new URL(http);
        //UtilityBox.getInstance().displayErrorPopup("Download", "Fehler beim Download. Die Datei kann unter\n" + url + "\nheruntergeladen werden.");
      } catch (MalformedURLException ex1) {
        Logger.getLogger(XMLEditor.class.getName()).log(Level.SEVERE, null, ex1);
      }
    }

    return false;
  }

  public static float getLatestProgramVersion() {
    return latestProgramVersion;
  }

  public static float getLatestShiftFileVersion() {
    return latestShiftFileVersion;
  }

  public static float getLatestSalaryFileVersion() {
    return latestSalaryFileVersion;
  }
}
