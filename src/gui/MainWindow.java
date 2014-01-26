/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MainWindow.java
 *
 * Created on 11.03.2009., 01:19:23
 */
package gui;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author sheky
 */
public class MainWindow extends javax.swing.JFrame {

    //TODO prilikom pokretanja aplikacije napraviti provjeru dali se moze spojiti na bazu podataka!!
    public static Integer numFrame = 0;
    public static JPanel activeJPanel;
    public static Integer pristup = null;
    public static Integer idZap = null;

    /** Creates new form MainWindow */
    public MainWindow() {
        initComponents();


        setFrameIcon();
        if (checkConnection() == false) {
            evFaktura.setEnabled(false);
            evPonuda.setEnabled(false);
            evPoslPart.setEnabled(false);
            evZaposlenih.setEnabled(false);

            String[] button = {"Izlaz"};
            String poruka = "Program se nemože spojiti na bazu!\nIzađite iz programa!";
            String naslov = "Pogreška";
            int resp = JOptionPane.showOptionDialog(null, poruka, naslov, 0, JOptionPane.ERROR_MESSAGE, null, button, 0);
            System.exit(1);
        }
        chThread();
    }

    private void restrict() {

        Integer pri = MainWindow.getPristup();
        if (pri == null) { //pocetno stanje
            evPoslPart.setEnabled(false);
            evZaposlenih.setEnabled(false);
            evFaktura.setEnabled(false);
            evPonuda.setEnabled(false);
        } else {
            System.out.println("RESTRICT: " + pri.toString());
            evPoslPart.setEnabled(true);
            evZaposlenih.setEnabled(true);
            evFaktura.setEnabled(true);
            evPonuda.setEnabled(true);
            if (pri == 0) { //administrator
                evPoslPart.setVisible(true);
                evZaposlenih.setVisible(true);
                evFaktura.setVisible(true);
                evPonuda.setVisible(true);
            } else if (pri == 1) { //vlasnik
                evPoslPart.setVisible(true);
                evZaposlenih.setVisible(true);
                evFaktura.setVisible(true);
                evPonuda.setVisible(true);
            } else if (pri == 2) { //zaposlenik
                evPoslPart.setVisible(true);
                evZaposlenih.setVisible(false);
                evFaktura.setVisible(false);
                evPonuda.setVisible(true);
            }
        }
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
                System.out.println((i++) + " thread: stop");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    System.out.println("Error: sporedna paralelna dretva");
                }
            }
            System.out.println(Thread.currentThread().getName() + " finish setting text ");
            showLogin();
        }
    };

    public static void setPristup(Integer pri, Integer idZaposlenika) {
        pristup = pri;
        idZap = idZaposlenika;
    }

    public static void setPodaciZap(String korisnik) {
        lblKorIme.setText(korisnik);
    }

    public static Integer getPristup() {
        return pristup;
    }

    public Integer getLoginIdZap() {
        return idZap;
    }

    public boolean getIsShow() {
        return this.isShowing();
    }

    public void showLogin() {
        JFrame mainFrame = this;
        frmLogin login = new frmLogin(mainFrame, true);
        login.setLocationRelativeTo(this);
        login.setVisible(true);
    }

    public boolean checkConnection() {
        try {
            EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("purgarPU");
            EntityManager em = emFactory.createEntityManager();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
//Action Listener - radi, ali ga ne koristim

    /** Radi - ActionListener!! ;)    ---------------------------------------------------------------
    private void menuGroupActionListener(ActionEvent evt) {
    String command = evt.getActionCommand().toString();
    this.setTitle(command);
    }

    public void addListener() { //dodavanje listenera za elemente koji su u menuGroup
    Enumeration e = menuGroup.getElements();
    AbstractButton ab;
    while (e.hasMoreElements()) {
    ab = (AbstractButton) e.nextElement();
    //dodavanje ActionListener -a, te instanciranje actionPerformed, i prosljedjivanje dogadjaja u funkciju menuGroupActionListener
    ab.addActionListener(new java.awt.event.ActionListener() {
    public void actionPerformed(java.awt.event.ActionEvent evt) {
    menuGroupActionListener(evt);
    }
    });
    }
    }
    --------------------------------------------------------------------------------*/
    private void setFrameIcon() {
        Image icon = null;
        try {
            final String iconPath = "ikone/Brief.png";
            URL input = null;
            input = MainWindow.class.getResource(iconPath);
            System.out.println("icon url: " + input.toString());

            icon = ImageIO.read(input);
        //icon = ImageIO.read(this.getClass().getResource("../picture/Brief.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (icon != null) {
            this.setIconImage(icon);
        }

    }

    public void resizeJPanel() {
        if (activeJPanel != null) {
            int width, height;
            width = deskPane.getWidth();
            height = deskPane.getHeight();
            activeJPanel.setBounds(0, 0, width, height);
            activeJPanel.updateUI();
        }

    }

    public static void openJPanel(JPanel frame, boolean novi) {
        if (activeJPanel != null && novi == true) { //zatvorit prethodni frame
            System.out.println("delete frame (" + numFrame.toString() + ") :" + activeJPanel.getClass().getName());
            deskPane.removeAll();
            numFrame = 0;
        }

        numFrame++;
        int width, height;
        width = deskPane.getWidth();
        height = deskPane.getHeight();

        JPanel f = frame;
        activeJPanel = frame;
        f.setBounds(0, 0, width, height);

        deskPane.add(f, numFrame);
        System.out.println("activeJPanel: " + activeJPanel.toString());
        f.setVisible(true);
        f.setFocusable(true);

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        menuGroup = new javax.swing.ButtonGroup();
        deskPane = new javax.swing.JDesktopPane();
        menuPane = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        izlaz = new javax.swing.JButton();
        izbornik = new javax.swing.JPanel();
        evPoslPart = new javax.swing.JToggleButton();
        evZaposlenih = new javax.swing.JToggleButton();
        evFaktura = new javax.swing.JToggleButton();
        evPonuda = new javax.swing.JToggleButton();
        btnLogout = new javax.swing.JButton();
        lblKorIme = new javax.swing.JLabel();
        chgPass = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Stolarski obrt 'Purgar'");
        setMinimumSize(new java.awt.Dimension(870, 450));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        deskPane.setAutoscrolls(true);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        izlaz.setText("Izlaz");
        izlaz.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                izlazActionPerformed(evt);
            }
        });

        izbornik.setLayout(new java.awt.GridBagLayout());

        menuGroup.add(evPoslPart);
        evPoslPart.setText("Evidencija poslovnih partnera");
        evPoslPart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                evPoslPartActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        izbornik.add(evPoslPart, gridBagConstraints);

        menuGroup.add(evZaposlenih);
        evZaposlenih.setText("Evidencija zaposlenika");
        evZaposlenih.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                evZaposlenihActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        izbornik.add(evZaposlenih, gridBagConstraints);

        menuGroup.add(evFaktura);
        evFaktura.setText("Fakture");
        evFaktura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                evFakturaActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        izbornik.add(evFaktura, gridBagConstraints);

        menuGroup.add(evPonuda);
        evPonuda.setText("Ponude");
        evPonuda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                evPonudaActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        izbornik.add(evPonuda, gridBagConstraints);

        btnLogout.setText("Odjava");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        chgPass.setText("Promjena lozinke");
        chgPass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chgPassActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout menuPaneLayout = new javax.swing.GroupLayout(menuPane);
        menuPane.setLayout(menuPaneLayout);
        menuPaneLayout.setHorizontalGroup(
            menuPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuPaneLayout.createSequentialGroup()
                .addGroup(menuPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(menuPaneLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblKorIme, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE))
                    .addComponent(izlaz, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                    .addComponent(btnLogout, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                    .addComponent(izbornik, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chgPass, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        menuPaneLayout.setVerticalGroup(
            menuPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, menuPaneLayout.createSequentialGroup()
                .addComponent(izbornik, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 209, Short.MAX_VALUE)
                .addComponent(lblKorIme, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chgPass)
                .addGap(7, 7, 7)
                .addComponent(btnLogout)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(izlaz, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
            .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(menuPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deskPane, javax.swing.GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(menuPane, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(deskPane, javax.swing.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        resizeJPanel();
    }//GEN-LAST:event_formComponentResized

    private void izlazActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_izlazActionPerformed
        System.exit(0);
    }//GEN-LAST:event_izlazActionPerformed

    private void evPoslPartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_evPoslPartActionPerformed
        prikaz();
    }//GEN-LAST:event_evPoslPartActionPerformed

    private void evZaposlenihActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_evZaposlenihActionPerformed
        prikaz();
    }//GEN-LAST:event_evZaposlenihActionPerformed

    private void evFakturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_evFakturaActionPerformed
        prikaz();
    }//GEN-LAST:event_evFakturaActionPerformed

    private void evPonudaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_evPonudaActionPerformed
        prikaz();
    }//GEN-LAST:event_evPonudaActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        showLogin();
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        restrict();
    }//GEN-LAST:event_formWindowActivated

    private void chgPassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chgPassActionPerformed
        //promjena lozinke
        String pass = null;
        frmLozinka frmLoz = new frmLozinka(this, true);
        frmLoz.setLocationRelativeTo(this);
        frmLoz.setVisible(true);
        pass = frmLoz.getNewLozinka();

        if (pass != null) {
            EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("purgarPU");
            EntityManager em = emFactory.createEntityManager();
            persistence.Zaposlenici zap = em.find(persistence.Zaposlenici.class, idZap);
            em.getTransaction().begin();
            zap.setLozinka(pass);
            em.persist(zap);
            em.getTransaction().commit();

            String[] button = {"Uredu"};
            String poruka = "Uspješno ste promjenili lozinku.";
            String naslov = "Promjena lozinke";
            int resp = JOptionPane.showOptionDialog(null, poruka, naslov, 0, JOptionPane.ERROR_MESSAGE, null, button, 0);
        }
    }//GEN-LAST:event_chgPassActionPerformed

    public void prikaz() {
        if (evZaposlenih.isSelected()) {
            openJPanel(new frmEvZaposlenih(), true);
        } else if (evPoslPart.isSelected()) {
            openJPanel(new frmEvPoslPart(), true);
        } else if (evFaktura.isSelected()) {
            openJPanel(new frmEvFaktura(), true);
        } else if (evPonuda.isSelected()) {
            openJPanel(new frmEvPonuda(), true);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton chgPass;
    public static javax.swing.JDesktopPane deskPane;
    private javax.swing.JToggleButton evFaktura;
    private javax.swing.JToggleButton evPonuda;
    private javax.swing.JToggleButton evPoslPart;
    private javax.swing.JToggleButton evZaposlenih;
    private javax.swing.JPanel izbornik;
    private javax.swing.JButton izlaz;
    private javax.swing.JSeparator jSeparator1;
    private static javax.swing.JLabel lblKorIme;
    private javax.swing.ButtonGroup menuGroup;
    private javax.swing.JPanel menuPane;
    // End of variables declaration//GEN-END:variables
}
