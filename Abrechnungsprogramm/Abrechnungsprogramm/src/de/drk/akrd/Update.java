package de.drk.akrd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.ProgressMonitorInputStream;

/**
 *
 * @author Jo
 */
public class Update {

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
