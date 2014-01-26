/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.dodatno;

import gui.frmPdfRead;
import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

/**
 *
 * @author sheky
 */
public class fileDialog {

    public String savePdfFile(Component parent) {
        JFileChooser chooser = new JFileChooser();
        ExtensionFileFilter filter = new ExtensionFileFilter();


        filter.addExtension("pdf", true);
        filter.setDescription("PDF datoteka");
        chooser.setFileFilter(filter);

        int returnVal = chooser.showSaveDialog(parent);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath().toString();
            System.out.println("PDF datoteka: " + path);
            return path;
        } else {
            return null;
        }
    }

    public boolean copyFile(String input, String output) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            File f1 = new File(input);
            File f2 = new File(output);
            fis = new FileInputStream(f1);
            fos = new FileOutputStream(f2);
            long len = f1.length();

            System.out.println("Datoteka moze biti kreirana: " + f2.createNewFile());
            int oneByte, count = 0;
            while ((oneByte = fis.read()) != -1) {
                fos.write(oneByte);
            }
            fos.close();
            fis.close();
            return true;
        } catch (IOException ex) {            
            ex.printStackTrace();
            return false;
        } 
    }
}
