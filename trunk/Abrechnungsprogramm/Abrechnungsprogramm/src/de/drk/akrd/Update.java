package de.drk.akrd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ProgressMonitorInputStream;

/**
 *
 * @author Jo
 */
public class Update {

  /**
   * download actual shiftlist
   */
  public static void downloadNewShiftFile() {
      try {
        Update.downloadFile("http://drk-ak-rettungsdienst-abrechnungsprogramm.googlecode.com/files/Schichten.xml");
        UtilityBox.getInstance().displayInfoPopup("Schichten.xml", "Die Schichtliste wurde aktualisiert.");
      } catch (Exception ex) {
        try {
          // TODO: display URL as Link
          URL url = new URL("http://drk-ak-rettungsdienst-abrechnungsprogramm.googlecode.com/files/Schichten.xml");
          UtilityBox.getInstance().displayErrorPopup("Download", "Fehler beim Download. Die Datei kann unter\n"+url+"\nheruntergeladen werden.");
        } catch (MalformedURLException ex1) {
          Logger.getLogger(XMLEditor.class.getName()).log(Level.SEVERE, null, ex1);
        }
      }
  }

  public static void downloadFile(String http) throws Exception {
//    throw new NotImplementedException();
    URL url = new URL(http);
    URLConnection uc = url.openConnection();
    InputStream is = (InputStream) uc.getInputStream();
    ProgressMonitorInputStream pmis = new ProgressMonitorInputStream(null, "Downloading...", is);
    pmis.getProgressMonitor().setMaximum(uc.getContentLength());
    File outputFile = new File("data" + File.separatorChar + "Schichten.xml");
    FileOutputStream out = new FileOutputStream(outputFile);

    byte[] buffer = new byte[1024];
    //System.out.println("dödö");
    for (int n; (n = pmis.read(buffer)) != -1; out.write(buffer, 0, n));

    pmis.close();
    out.flush();
    out.close();
  }
}
