/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * frmPdfRead.java
 *
 * Created on 19.03.2009., 02:58:57
 */
package gui;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.ByteBuffer;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PagePanel;
import gui.dodatno.fileDialog;
import java.awt.Dimension;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 *
 * @author sheky
 */
public class frmPdfRead extends javax.swing.JPanel {

    File file = null;
    private boolean isSubReport = false;
    RandomAccessFile raf = null;
    public String pdfTemp = "D:/temp"; //nije static, te se stoga prilikom svakog novog pokretanja, postavlja ova vrjednost
    public static Integer tmpBr = 0; //static.. tako da vrjednost pohranjena ostane i prilikom drugog pokretanja,
    //tj. vrjednost se ne inicijalizira ponovo, nego ostaje inkrement iz funkcije 'loadReport'
    int progBarMax = 12;
    PagePanel panel = new PagePanel();
    PDFFile pdffile = null;
    String reportPdf;
    static boolean stop = false;
    static boolean func = false;
    String DriverDB = "com.mysql.jdbc.Driver";
    String URLdb = "jdbc:mysql://localhost:3306/purgarDB";
    Integer maxPg = 0;
    Integer pg = 0;
    String dbUserName = "root";
    String dbUserPass = "";
    JasperPrint jasperPrint = null;
    HashMap parametri = null;
    private JPanel parentJPanel = null;

    public void setParametri(HashMap param) {
        parametri = param;
        isSubReport = true;
    }

    /** Creates new form frmPdfRead */
    public frmPdfRead(String reportFile) {
        initComponents();
        setProgresVisible(true);

        panel.setBounds(0, 0, okvir.getWidth(), okvir.getHeight());
        okvir.add(panel);
        reportPdf = reportFile;

        chThread();
    }

    public void setParentJPanel(JPanel frame) {
        parentJPanel = frame;
        System.out.println("parent panel:" + frame.getClass().getName());
    }

    public void closePanel() {
        MainWindow.activeJPanel = parentJPanel;
    }

    public boolean getIsShow() {
        return this.isShowing();
    }

    public void loadReport() {
        System.out.println("load report");
        tmpBr++;
        pdfTemp = pdfTemp + tmpBr.toString() + ".pdf";
        System.out.println("PDF temp: " + pdfTemp);
        showReport(reportPdf);
    }

    public void chThread() {
        Thread th = new Thread(doneAction);
        System.out.println("parent thread start");
        th.start();
        System.out.println("parent thread stop");
    }
    Runnable doneAction = new Runnable() {

        public void run() {
            System.out.println(Thread.currentThread().getName() + " start setting text ");
            int i = 0;
            while (!getIsShow()) {
                System.out.println((i++) + " thread: stop=" + stop);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    System.out.println("Error: sporedna paralelna dretva");
                }
            }
            System.out.println(Thread.currentThread().getName() + " finish setting text ");
            loadReport();
        }
    };

    public void setProgresVisible(boolean var) {
        panelStatus.setVisible(var);
        panelFunkcije.setVisible(!var);
    }

    public void setProgres(int var) {
        progresBar.setMaximum(progBarMax);
        progresBar.setValue(var);
    }

    public void sleep(int milisec) {
        try {
            Thread.sleep(milisec);
        } catch (InterruptedException ex) {
            System.out.println("sleep function error..");
        }
    }

    public void showReport(String reportFile) {
        setProgresVisible(true);

        try {
            Class.forName(DriverDB);
            Connection conn = DriverManager.getConnection(URLdb, dbUserName, dbUserPass);

            setProgres(1);

            //java.io.InputStream is = this.getClass().getResourceAsStream(reportFile);
            //JasperDesign jasperDesign = JRXmlLoader.load(is);
            setProgres(2);
            URL url = MainWindow.class.getResource(reportFile);//this.getClass().getResource(reportFile);
            System.out.println("REPORT_FILE:" + url.toString());
            String urlDir = url.toString();
            
            if (isSubReport == true) {
                urlDir = urlDir.substring(0, urlDir.lastIndexOf("/") + 1);
                System.out.println("SUBREPORT_DIR:" + urlDir);
                parametri.put("SUBREPORT_DIR", urlDir);
            }

            //JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(url);
            setProgres(3);

            jasperPrint = JasperFillManager.fillReport(jasperReport, parametri, conn);
            setProgres(4);

            //JasperViewer.viewReport(jasperPrint, false);
            JasperExportManager.exportReportToPdfFile(jasperPrint, pdfTemp);

            setProgres(5);
            setProgres(6);

            readPdfFile();
        } catch (Exception ex) {
            String connectMsg = "Nemoguce kreirat report: " + ex.getMessage() + " " + ex.getLocalizedMessage();
            System.out.println(connectMsg);
            ex.printStackTrace();
        }

    }

    public void readPdfFile() {
        try {
            file = new File(pdfTemp);
            raf = new RandomAccessFile(file, "r");

            setProgres(7);
            FileChannel channel = raf.getChannel();
            setProgres(8);
            ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            setProgres(9);
            pdffile = new PDFFile(buf);

            setProgres(10);
            // show the first page
            PDFPage page = pdffile.getPage(1);
            setProgres(11);
            panel.showPage(page);
            setProgres(12);
            maxPg = pdffile.getNumPages();
            lblMaxStr.setText(" / " + maxPg.toString() + "    ");
            pg = 1;
            txtStranica.setText(pg.toString());
            zoomPage(100);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        setProgresVisible(false);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        entityManager = java.beans.Beans.isDesignTime() ? null : javax.persistence.Persistence.createEntityManagerFactory("purgarPU").createEntityManager();
        panelStatus = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        progresBar = new javax.swing.JProgressBar();
        panelFunkcije = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnClose = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtStranica = new javax.swing.JTextField();
        lblMaxStr = new javax.swing.JLabel();
        btnPrevPg = new javax.swing.JButton();
        btnNextPg = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txtZoom = new javax.swing.JTextField();
        btnZoomIn = new javax.swing.JButton();
        btnZoomOut = new javax.swing.JButton();
        panelScroll = new javax.swing.JScrollPane();
        okvir = new javax.swing.JPanel();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        panelStatus.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        panelStatus.setLayout(new java.awt.GridLayout(1, 4, 5, 0));

        jLabel2.setText("Dokument");
        panelStatus.add(jLabel2);

        jLabel1.setText("Generiranje dokumenta u tijeku");
        panelStatus.add(jLabel1);

        progresBar.setMaximum(5);
        panelStatus.add(progresBar);

        panelFunkcije.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnClose.setText("Zatvori");
        btnClose.setFocusable(false);
        btnClose.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClose.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jToolBar1.add(btnClose);

        btnPrint.setText("Ispis");
        btnPrint.setFocusable(false);
        btnPrint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrint);

        btnSave.setText("Snimi");
        btnSave.setToolTipText("Snimi kao PDF dokument");
        btnSave.setFocusable(false);
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);

        jLabel3.setText("     Stranica: ");
        jToolBar1.add(jLabel3);

        txtStranica.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtStranicaKeyReleased(evt);
            }
        });
        jToolBar1.add(txtStranica);

        lblMaxStr.setText("  /     ");
        jToolBar1.add(lblMaxStr);

        btnPrevPg.setText("Prethodna");
        btnPrevPg.setFocusable(false);
        btnPrevPg.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrevPg.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrevPg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrevPgActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrevPg);

        btnNextPg.setText("Sljedeća");
        btnNextPg.setFocusable(false);
        btnNextPg.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNextPg.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNextPg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextPgActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNextPg);

        jLabel4.setText("     Povećaj na: ");
        jToolBar1.add(jLabel4);

        txtZoom.setText("100");
        txtZoom.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtZoomKeyReleased(evt);
            }
        });
        jToolBar1.add(txtZoom);

        btnZoomIn.setText("+");
        btnZoomIn.setFocusable(false);
        btnZoomIn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnZoomIn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnZoomIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnZoomInActionPerformed(evt);
            }
        });
        jToolBar1.add(btnZoomIn);

        btnZoomOut.setText("-");
        btnZoomOut.setFocusable(false);
        btnZoomOut.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnZoomOut.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnZoomOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnZoomOutActionPerformed(evt);
            }
        });
        jToolBar1.add(btnZoomOut);

        javax.swing.GroupLayout panelFunkcijeLayout = new javax.swing.GroupLayout(panelFunkcije);
        panelFunkcije.setLayout(panelFunkcijeLayout);
        panelFunkcijeLayout.setHorizontalGroup(
            panelFunkcijeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
        );
        panelFunkcijeLayout.setVerticalGroup(
            panelFunkcijeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFunkcijeLayout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelScroll.setBackground(new java.awt.Color(204, 204, 204));
        panelScroll.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        javax.swing.GroupLayout okvirLayout = new javax.swing.GroupLayout(okvir);
        okvir.setLayout(okvirLayout);
        okvirLayout.setHorizontalGroup(
            okvirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 584, Short.MAX_VALUE)
        );
        okvirLayout.setVerticalGroup(
            okvirLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 252, Short.MAX_VALUE)
        );

        panelScroll.setViewportView(okvir);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 612, Short.MAX_VALUE)
            .addComponent(panelFunkcije, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 612, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelFunkcije, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        panel.setBounds(0, 0, okvir.getWidth(), okvir.getHeight());
    }//GEN-LAST:event_formComponentResized

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        try {
            closePanel();
            this.removeAll();
            this.setVisible(false);

            raf.close();
            file.delete();
        } catch (IOException ex) {
            System.out.println("error: nemogu zatvorit raf ili obrisat temp file:" + file.getAbsolutePath());
        }
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        try {
            JasperPrintManager.printReport(jasperPrint, true);
        } catch (JRException ex) {
            msgPrintError("Program nije u mogučnosti isprintat dokument!", "Pogreška prilikom printanja");
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btnPrintActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        fileDialog fd = new fileDialog();
        String savePath = fd.savePdfFile(this) + ".pdf";

        boolean created = fd.copyFile(pdfTemp, savePath);
        if (created == true) {
            msgInfo("Datoteka je uspješno spremljena!", "Spremanje dokumenta");
        } else {
            msgPrintError("Došlo je do pogreške prilikom spremanja datoteke!", "Pogreška");
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void txtStranicaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtStranicaKeyReleased
        int tmpPg = 0;
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tmpPg = Integer.parseInt(txtStranica.getText());
            goToPageNum(tmpPg);
        }
    }//GEN-LAST:event_txtStranicaKeyReleased

    private void btnPrevPgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrevPgActionPerformed
        goToPageNum(pg - 1);
    }//GEN-LAST:event_btnPrevPgActionPerformed

    private void btnNextPgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextPgActionPerformed
        goToPageNum(pg + 1);
    }//GEN-LAST:event_btnNextPgActionPerformed

    private void txtZoomKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtZoomKeyReleased
        int tmpZoom;
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tmpZoom = Integer.parseInt(txtZoom.getText());
            zoomPage(tmpZoom);
        }
}//GEN-LAST:event_txtZoomKeyReleased

    private void btnZoomInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnZoomInActionPerformed
        Integer zoom = Integer.parseInt(txtZoom.getText());
        zoom += 10;
        txtZoom.setText(zoom.toString());
        zoomPage(zoom);
    }//GEN-LAST:event_btnZoomInActionPerformed

    private void btnZoomOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnZoomOutActionPerformed
        Integer zoom = Integer.parseInt(txtZoom.getText());
        zoom -= 10;
        txtZoom.setText(zoom.toString());
        zoomPage(zoom);
    }//GEN-LAST:event_btnZoomOutActionPerformed

    public void goToPageNum(int pgNum) {
        if ((pgNum <= maxPg) && (pgNum > 0)) {
            pg = pgNum;
            PDFPage page = pdffile.getPage(pg);
            panel.showPage(page);
            txtStranica.setText(pg.toString());
        } else {
            txtStranica.setText(pg.toString());
        }
    }

    public void zoomPage(int var) {
        int zoom = var;
        if ((zoom > 300)) {
            txtZoom.setText("100");
            zoom = 100;
        }

        float paperW = (float) (210 * 37.795275591);
        float paperH = (float) (297 * 37.795275591);
        float paperOmjer = paperH / paperW;

        float zoomSize = (float) zoom / 100;
        float scrollW = panelScroll.getWidth();
        int panelW = (int) (scrollW * zoomSize);
        int panelH = (int) (scrollW * zoomSize * paperOmjer);

        okvir.setPreferredSize(new Dimension(panelW - 20, panelH));
        panel.setBounds(10, 10, panelW - 40, panelH);
        this.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));
        //System.out.println("new -- panel w:" + panel.getWidth() + "  panel h:" + panel.getHeight());
        this.updateUI();
    }

    public void msgPrintError(String poruka, String naslov) {
        String[] button = {"Uredu"};
        int resp = JOptionPane.showOptionDialog(null, poruka, naslov, 0, JOptionPane.ERROR_MESSAGE, null, button, 0);
    }

    public void msgInfo(String poruka, String naslov) {
        String[] button = {"Uredu"};
        int resp = JOptionPane.showOptionDialog(null, poruka, naslov, 0, JOptionPane.INFORMATION_MESSAGE, null, button, 0);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnNextPg;
    private javax.swing.JButton btnPrevPg;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnZoomIn;
    private javax.swing.JButton btnZoomOut;
    private javax.persistence.EntityManager entityManager;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblMaxStr;
    private javax.swing.JPanel okvir;
    private javax.swing.JPanel panelFunkcije;
    private javax.swing.JScrollPane panelScroll;
    private javax.swing.JPanel panelStatus;
    private javax.swing.JProgressBar progresBar;
    private javax.swing.JTextField txtStranica;
    private javax.swing.JTextField txtZoom;
    // End of variables declaration//GEN-END:variables
}
