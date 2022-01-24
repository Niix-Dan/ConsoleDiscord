package me.daniel.consolediscord;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;

public class Http {
  public void get(String downloadURL, String outputPath) {
    try {
      URL url = new URL(downloadURL);
      URLConnection con = url.openConnection();
      DataInputStream dis = new DataInputStream(con.getInputStream());
      byte[] fileData = new byte[con.getContentLength()];
      for (int q = 0; q < fileData.length; q++)
        fileData[q] = dis.readByte(); 
      dis.close();
      FileOutputStream fos = new FileOutputStream(new File(outputPath));
      fos.write(fileData);
      fos.close();
    } catch (Exception m) {
      System.out.println(m);
    } 
  }
}
