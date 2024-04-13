package niix.dan.consolediscord;

import org.apache.logging.log4j.core.util.IOUtils;
import org.bukkit.ChatColor;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class Http {
    public void download(String downloadURL, String outputPath) {
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

            System.out.println(ChatColor.RED + "    > Download complete!");
        } catch (Exception m) {
            System.out.println(ChatColor.RED + "    > Download ERROR!");
            System.out.println(m);
        }
    }

    public String get(String link) {
        try {
            URL url = new URL(link);
            URLConnection con = url.openConnection();
            InputStream is = con.getInputStream();
            String body = convertStreamToString(is);
            is.close();

            return body;
        } catch(Exception ex) {
            System.out.println(ex);
            return "";
        }
    }


    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
