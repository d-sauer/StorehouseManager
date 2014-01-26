/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * frmEvFaktura.java
 *
 * Created on 13.03.2009., 19:49:28
 */
package gui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import persistence.Otpremnica;
import persistence.Partner;

/**
 *
 * @author sheky
 */
public class frmEvFaktura extends javax.swing.JPanel {

    public String reportFile = "report/Racun.jasper"; //.jrxml";
    public String reportFileOtpremnica = "report/Otpremnica.jasper"; //.jrxml";
    private Integer idRacuna = null;
    private Integer idOtpremnice = null;

    /** Creates new form frmEvFaktura */
    public frmEvFaktura() {
        initComponents();
        panelHeader.setVisible(false);
        panelFooter.setVisible(false);
        listStavke.clear();


        btnNewOtp.setEnabled(false);
        btnDelOtp.setEnabled(false);
        btnPrewOtp.setEnabled(false);
        btnChangeOtp.setEnabled(false);

        panelPoslPart.setVisible(false);

        tblFakture.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

                    public void valueChanged(ListSelectionEvent e) {
                        getStavkeFakture();
                    }
                });

    }

    public void getStavkeFakture() {
        int selRow = tblFakture.getSelectedRow();
        if (selRow != -1) {
            idRacuna = listFakture.get(selRow).getIdRacuna();
            String fakturaHeader = listFakture.get(selRow).getOpisHeader();
            String fakturaFooter = listFakture.get(selRow).getOpisFooter();

            if ((fakturaHeader != null) && (fakturaHeader.length() != 0)) {
                txtOpisHeader.setText(fakturaHeader);
                panelHeader.setVisible(true);
            } else {
                panelHeader.setVisible(false);
            }
            if ((fakturaFooter != null) && (fakturaFooter.length() != 0)) {
                txtOpisFooter.setText(fakturaFooter);
                panelFooter.setVisible(true);
            } else {
                panelFooter.setVisible(false);
            }


            String sqlQuery = "SELECT sp FROM Stavkaponuda sp WHERE sp.idRacuna=:idRacuna";
            Query q = entityManager.createQuery(sqlQuery);
            q.setParameter("idRacuna", idRacuna);
            Collection data = q.getResultList();

            listStavke.clear();
            listStavke.addAll(data);

            persistence.Racun racun = entityManager.find(persistence.Racun.class, idRacuna);
            Integer idPart = racun.getIdPartner();
            String mjestoIzdavanja = racun.getMjestoIzdavanja();

            if (idPart != null) {
                try {
                    persistence.Partner partner = entityManager.find(persistence.Partner.class, idPart);
                    persistence.Vrpartnera vrPart = entityManager.find(persistence.Vrpartnera.class, partner.getIdVrsta());

                    lblNaziv.setText(vrPart.getVrsta());
                    lblIme.setText(partner.getIme());
                    lblPrezime.setText(partner.getPrezime());
                    lblMatBr.setText(partner.getMaticniBr());
                    lblTelefon.setText(partner.getTelefon());
                    lblMjesto.setText(partner.getMjesto() + " (" + partner.getPostBroj().toString() + ")");
                    lblAdresa.setText(partner.getAdresa());
                    lblZiro.setText(partner.getZiroRac());
                } catch (Exception ex) {
                }
            }

            //read otpremnica
            getOtpremnice(idRacuna, idPart, mjestoIzdavanja);

            this.updateUI();
            tblFakture.setRowSelectionInterval(selRow, selRow);
            tblFakture.scrollRectToVisible(tblFakture.getCellRect(selRow, 0, true));
        }
    }

    public void refreshFaktura() {
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        } else {
            entityManager.getTransaction().rollback();
            entityManager.getTransaction().begin();
        }
        int selRow = tblFakture.getSelectedRow();

        String strQuery = "SELECT r FROM Racun r";
        Query q = entityManager.createQuery(strQuery);

        Collection data = q.getResultList();
        for (Object entity : data) {
            entityManager.refresh(entity);
        }
        listFakture.clear();
        listFakture.addAll(data);

        tblFakture.updateUI();
        if (selRow != -1) {
            tblFakture.setRowSelectionInterval(selRow, selRow);
        }
    }

    public void getOtpremnice(Integer idRac, Integer idPart, String mjesto) {
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        } else {
            entityManager.getTransaction().rollback();
            entityManager.getTransaction().begin();
        }
        String strQuery = "SELECT o FROM Otpremnica o WHERE o.IdRacuna = :idRacuna";
        EntityManager em = entityManager;
        Query q = em.createQuery(strQuery);
        q.setParameter("idRacuna", idRac);


        if (q.getResultList().size() != 0) {
            persistence.Otpremnica otp = (Otpremnica) q.getResultList().get(0);
            idOtpremnice = otp.getIdOtp();
            lblIdOtp.setText(otp.getIdOtp().toString());

            strQuery = "SELECT p FROM Partner p WHERE p.idPartner = :idPartner";
            q = em.createQuery(strQuery);
            q.setParameter("idPartner", idPart);

            persistence.Partner part = (Partner) q.getResultList().get(0);
            String naziv = null;
            if (part.getNaziv() == null) {
                naziv = part.getIme() + " " + part.getPrezime();
            } else {
                naziv = part.getNaziv();
            }

            lblTekstOtp.setText("Za " + part.getAdresa() + ", " + part.getMjesto() + ", " + naziv);
            Date datum = otp.getDatum();
            DateFormat formatDatuma = new SimpleDateFormat("dd.MM.yyyy.");

            lblMjestoDatumOtp.setText("U " + mjesto + ", " + formatDatuma.format(datum).toString());
            btnNewOtp.setEnabled(false);
            btnDelOtp.setEnabled(true);
            btnPrewOtp.setEnabled(true);
            btnChangeOtp.setEnabled(true);
        } else {
            lblIdOtp.setText("");
            lblTekstOtp.setText("Za");
            lblMjestoDatumOtp.setText("Mjesto, datum");

            btnNewOtp.setEnabled(true);
            btnDelOtp.setEnabled(false);
            btnPrewOtp.setEnabled(false);
            btnChangeOtp.setEnabled(false);
        }
        entityManager.getTransaction().commit();
    }

    public void deleteOtpremnica(int idOtp) {
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        } else {
            entityManager.getTransaction().rollback();
            entityManager.getTransaction().begin();
        }
        //brisanje stavki otpremnice
        EntityManager em = entityManager;
        String strQuery = "DELETE FROM Stavkaotpremnice so WHERE so.stavkaotpremnicePK.idOtp = :idOtp";
        Query q = em.createQuery(strQuery);
        q.setParameter("idOtp", idOtp);
        q.executeUpdate();

        //brisanje otpremnice
        strQuery = "DELETE FROM Otpremnica o WHERE o.idOtp = :idOtp";
        q = em.createQuery(strQuery);
        q.setParameter("idOtp", idOtp);
        q.executeUpdate();

        entityManager.getTransaction().commit();
    }

    private Integer getSelIdRacuna() {
        int selRow = tblFakture.getSelectedRow();
        if (selRow != -1) {
            return listFakture.get(selRow).getIdRacuna();
        } else {
            return null;
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        entityManager = java.beans.Beans.isDesignTime() ? null : javax.persistence.Persistence.createEntityManagerFactory("purgarPU").createEntityManager();
        query = java.beans.Beans.isDesignTime() ? null : entityManager.createQuery("SELECT r FROM Racun r");
        listFakture = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(query.getResultList());
        listStavke = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(query.getResultList());
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblFakture = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblStavke = new javax.swing.JTable();
        panelHeader = new javax.swing.JScrollPane();
        txtOpisHeader = new javax.swing.JTextArea();
        panelFooter = new javax.swing.JScrollPane();
        txtOpisFooter = new javax.swing.JTextArea();
        panelPoslPart = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lblTipPart = new javax.swing.JLabel();
        lblNaziv = new javax.swing.JLabel();
        lblIme = new javax.swing.JLabel();
        lblPrezime = new javax.swing.JLabel();
        lblMatBr = new javax.swing.JLabel();
        lblTelefon = new javax.swing.JLabel();
        lblMjesto = new javax.swing.JLabel();
        lblAdresa = new javax.swing.JLabel();
        lblZiro = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        btnNewFaktura = new javax.swing.JButton();
        btnPromjeni = new javax.swing.JButton();
        btnPregled = new javax.swing.JButton();
        panelOtpremnica = new javax.swing.JPanel();
        panelOtpButtons = new javax.swing.JPanel();
        btnNewOtp = new javax.swing.JButton();
        btnChangeOtp = new javax.swing.JButton();
        btnDelOtp = new javax.swing.JButton();
        btnPrewOtp = new javax.swing.JButton();
        panelOtpPodaci = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        lblIdOtp = new javax.swing.JLabel();
        lblTekstOtp = new javax.swing.JLabel();
        lblMjestoDatumOtp = new javax.swing.JLabel();
        btnPoslPartPodaci = new javax.swing.JToggleButton();

        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Fakture"));

        tblFakture.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, listFakture, tblFakture);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${idRacuna}"));
        columnBinding.setColumnName("Id Racuna");
        columnBinding.setColumnClass(Integer.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${datumIzdavanja}"));
        columnBinding.setColumnName("Datum Izdavanja");
        columnBinding.setColumnClass(java.util.Date.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${mjestoIzdavanja}"));
        columnBinding.setColumnName("Mjesto Izdavanja");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${iznos}"));
        columnBinding.setColumnName("Iznos");
        columnBinding.setColumnClass(Float.class);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane1.setViewportView(tblFakture);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Stavke fakture"));

        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, listStavke, tblStavke);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${opis}"));
        columnBinding.setColumnName("Opis");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${dimenzije}"));
        columnBinding.setColumnName("Dimenzije");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${kolicina}"));
        columnBinding.setColumnName("Kolicina");
        columnBinding.setColumnClass(Integer.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${jedCijena}"));
        columnBinding.setColumnName("Jed Cijena");
        columnBinding.setColumnClass(Float.class);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane2.setViewportView(tblStavke);

        panelHeader.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        txtOpisHeader.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.background"));
        txtOpisHeader.setColumns(20);
        txtOpisHeader.setEditable(false);
        txtOpisHeader.setFont(new java.awt.Font("Monospaced", 0, 12));
        txtOpisHeader.setRows(2);
        panelHeader.setViewportView(txtOpisHeader);

        panelFooter.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        txtOpisFooter.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.background"));
        txtOpisFooter.setColumns(20);
        txtOpisFooter.setEditable(false);
        txtOpisFooter.setFont(new java.awt.Font("Monospaced", 0, 12));
        txtOpisFooter.setRows(2);
        panelFooter.setViewportView(txtOpisFooter);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelHeader, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
            .addComponent(panelFooter, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(panelHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelFooter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        panelPoslPart.setBorder(javax.swing.BorderFactory.createTitledBorder("Podaci o poslovnom partneru"));

        jLabel1.setText("Tip poslovnog partnera:");

        jLabel2.setText("Naziv:");

        jLabel3.setText("Ime:");

        jLabel4.setText("Prezime:");

        jLabel9.setText("Maticni broj:");

        jLabel5.setText("Telefon:");

        jLabel6.setText("Mjesto:");

        jLabel8.setText("Adresa:");

        jLabel10.setText("Bankovni račun:");

        lblTipPart.setText("__");

        lblNaziv.setText("__");

        lblIme.setText("__");

        lblPrezime.setText("__");

        lblMatBr.setText("__");

        lblTelefon.setText("__");

        lblMjesto.setText("__");

        lblAdresa.setText("__");

        lblZiro.setText("__");

        javax.swing.GroupLayout panelPoslPartLayout = new javax.swing.GroupLayout(panelPoslPart);
        panelPoslPart.setLayout(panelPoslPartLayout);
        panelPoslPartLayout.setHorizontalGroup(
            panelPoslPartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPoslPartLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPoslPartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPoslPartLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblIme))
                    .addGroup(panelPoslPartLayout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblMatBr))
                    .addComponent(jLabel1)
                    .addGroup(panelPoslPartLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblNaziv)))
                .addGroup(panelPoslPartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPoslPartLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jLabel4))
                    .addGroup(panelPoslPartLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTipPart)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblPrezime)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 93, Short.MAX_VALUE)
                .addGroup(panelPoslPartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelPoslPartLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTelefon))
                    .addGroup(panelPoslPartLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblMjesto))
                    .addGroup(panelPoslPartLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblAdresa))
                    .addGroup(panelPoslPartLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblZiro)))
                .addGap(120, 120, 120))
        );
        panelPoslPartLayout.setVerticalGroup(
            panelPoslPartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPoslPartLayout.createSequentialGroup()
                .addGroup(panelPoslPartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblTipPart)
                    .addComponent(jLabel5)
                    .addComponent(lblTelefon))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPoslPartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(lblNaziv)
                    .addComponent(jLabel6)
                    .addComponent(lblMjesto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPoslPartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(lblIme)
                    .addComponent(lblPrezime)
                    .addComponent(jLabel8)
                    .addComponent(lblAdresa))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelPoslPartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(lblMatBr)
                    .addComponent(jLabel10)
                    .addComponent(lblZiro))
                .addContainerGap())
        );

        jPanel4.setLayout(new java.awt.GridLayout(1, 0, 3, 0));

        btnNewFaktura.setText("Nova faktura");
        btnNewFaktura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewFakturaActionPerformed(evt);
            }
        });
        jPanel4.add(btnNewFaktura);

        btnPromjeni.setText("Promjeni");

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblFakture, org.jdesktop.beansbinding.ELProperty.create("${selectedElement!=null}"), btnPromjeni, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        btnPromjeni.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPromjeniActionPerformed(evt);
            }
        });
        jPanel4.add(btnPromjeni);

        btnPregled.setText("Pregled");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblFakture, org.jdesktop.beansbinding.ELProperty.create("${selectedElement!=null}"), btnPregled, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        btnPregled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPregledActionPerformed(evt);
            }
        });
        jPanel4.add(btnPregled);

        panelOtpremnica.setBorder(javax.swing.BorderFactory.createTitledBorder("Otpremnice"));

        panelOtpButtons.setLayout(new java.awt.GridLayout(2, 2, 3, 0));

        btnNewOtp.setText("Nova");
        btnNewOtp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewOtpActionPerformed(evt);
            }
        });
        panelOtpButtons.add(btnNewOtp);

        btnChangeOtp.setText("Izmjeni");
        btnChangeOtp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeOtpActionPerformed(evt);
            }
        });
        panelOtpButtons.add(btnChangeOtp);

        btnDelOtp.setText("Obriši");
        btnDelOtp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelOtpActionPerformed(evt);
            }
        });
        panelOtpButtons.add(btnDelOtp);

        btnPrewOtp.setText("Pregled");
        btnPrewOtp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrewOtpActionPerformed(evt);
            }
        });
        panelOtpButtons.add(btnPrewOtp);

        jLabel7.setText("Otpremnica br.");

        lblIdOtp.setText("     ");

        lblTekstOtp.setText("Za");

        lblMjestoDatumOtp.setText("Mjesto, datum");

        javax.swing.GroupLayout panelOtpPodaciLayout = new javax.swing.GroupLayout(panelOtpPodaci);
        panelOtpPodaci.setLayout(panelOtpPodaciLayout);
        panelOtpPodaciLayout.setHorizontalGroup(
            panelOtpPodaciLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOtpPodaciLayout.createSequentialGroup()
                .addGroup(panelOtpPodaciLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelOtpPodaciLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblIdOtp))
                    .addComponent(lblTekstOtp)
                    .addComponent(lblMjestoDatumOtp))
                .addContainerGap(221, Short.MAX_VALUE))
        );
        panelOtpPodaciLayout.setVerticalGroup(
            panelOtpPodaciLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelOtpPodaciLayout.createSequentialGroup()
                .addGroup(panelOtpPodaciLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(lblIdOtp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTekstOtp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblMjestoDatumOtp)
                .addContainerGap(6, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelOtpremnicaLayout = new javax.swing.GroupLayout(panelOtpremnica);
        panelOtpremnica.setLayout(panelOtpremnicaLayout);
        panelOtpremnicaLayout.setHorizontalGroup(
            panelOtpremnicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelOtpremnicaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelOtpPodaci, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelOtpButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelOtpremnicaLayout.setVerticalGroup(
            panelOtpremnicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelOtpPodaci, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(panelOtpremnicaLayout.createSequentialGroup()
                .addComponent(panelOtpButtons, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnPoslPartPodaci.setText("Podaci o poslovnom partneru");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblFakture, org.jdesktop.beansbinding.ELProperty.create("${selectedElement!=null}"), btnPoslPartPodaci, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        btnPoslPartPodaci.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPoslPartPodaciActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnPoslPartPodaci, javax.swing.GroupLayout.PREFERRED_SIZE, 170, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelOtpremnica, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelPoslPart, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelOtpremnica, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPoslPart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPoslPartPodaci))
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPregledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPregledActionPerformed
        //pregled PDF-a
        frmPdfRead newReport = new frmPdfRead(reportFile);
        HashMap param = new HashMap();
        param.put("idRacuna", idRacuna.toString());
        newReport.setParametri(param);
        newReport.setParentJPanel(this);
        MainWindow.openJPanel(newReport, false);
}//GEN-LAST:event_btnPregledActionPerformed

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        System.out.println("frmEvFaktura shown...");
        refreshFaktura();
        MainWindow.openJPanel(this, false);
    }//GEN-LAST:event_formFocusGained

    private void btnPromjeniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPromjeniActionPerformed
        frmNovaFaktura newRacun = new frmNovaFaktura(idRacuna, false);
        MainWindow.openJPanel(newRacun, false);
}//GEN-LAST:event_btnPromjeniActionPerformed

    private void btnNewFakturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewFakturaActionPerformed
        frmNovaFaktura newRacun = new frmNovaFaktura(-1, true);
        MainWindow.openJPanel(newRacun, false);
}//GEN-LAST:event_btnNewFakturaActionPerformed

    private void btnNewOtpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewOtpActionPerformed
        frmOtpremnica newOtp = new frmOtpremnica(getSelIdRacuna(), 0, true);
        MainWindow.openJPanel(newOtp, false);
}//GEN-LAST:event_btnNewOtpActionPerformed

    private void btnChangeOtpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeOtpActionPerformed
        frmOtpremnica newOtp = new frmOtpremnica(getSelIdRacuna(), idOtpremnice, false);
        MainWindow.openJPanel(newOtp, false);
}//GEN-LAST:event_btnChangeOtpActionPerformed

    private void btnDelOtpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelOtpActionPerformed
        String[] button = {"Da", "Ne"};
        String poruka = "Dali sigurno želite obrisati otpremnicu?";
        String naslov = "Otpremnica";
        int resp = JOptionPane.showOptionDialog(null, poruka, naslov, 0, JOptionPane.QUESTION_MESSAGE, null, button, 0);
        if (resp == 0) {
            deleteOtpremnica(idOtpremnice);

            String[] btn = {"Uredu"};
            JOptionPane.showOptionDialog(null, "Otpremnica je uspješno obrisana", "Otpremnica", 0, JOptionPane.INFORMATION_MESSAGE, null, btn, 0);
        }

    }//GEN-LAST:event_btnDelOtpActionPerformed

    private void btnPrewOtpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrewOtpActionPerformed
        //pregled PDF-a
        frmPdfRead newReport = new frmPdfRead(reportFileOtpremnica);
        HashMap param = new HashMap();
        param.put("idOtp", idOtpremnice.toString());
        newReport.setParametri(param);
        newReport.setParentJPanel(this);
        MainWindow.openJPanel(newReport, false);
        System.out.println(entityManager.getDelegate().toString());
}//GEN-LAST:event_btnPrewOtpActionPerformed

    private void btnPoslPartPodaciActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPoslPartPodaciActionPerformed
        if (btnPoslPartPodaci.isSelected()) {
            panelPoslPart.setVisible(true);
        } else {
            panelPoslPart.setVisible(false);
        }
        this.updateUI();
    }//GEN-LAST:event_btnPoslPartPodaciActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChangeOtp;
    private javax.swing.JButton btnDelOtp;
    private javax.swing.JButton btnNewFaktura;
    private javax.swing.JButton btnNewOtp;
    private javax.swing.JToggleButton btnPoslPartPodaci;
    private javax.swing.JButton btnPregled;
    private javax.swing.JButton btnPrewOtp;
    private javax.swing.JButton btnPromjeni;
    private javax.persistence.EntityManager entityManager;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblAdresa;
    private javax.swing.JLabel lblIdOtp;
    private javax.swing.JLabel lblIme;
    private javax.swing.JLabel lblMatBr;
    private javax.swing.JLabel lblMjesto;
    private javax.swing.JLabel lblMjestoDatumOtp;
    private javax.swing.JLabel lblNaziv;
    private javax.swing.JLabel lblPrezime;
    private javax.swing.JLabel lblTekstOtp;
    private javax.swing.JLabel lblTelefon;
    private javax.swing.JLabel lblTipPart;
    private javax.swing.JLabel lblZiro;
    private java.util.List<persistence.Racun> listFakture;
    private java.util.List<persistence.Stavkaponuda> listStavke;
    private javax.swing.JScrollPane panelFooter;
    private javax.swing.JScrollPane panelHeader;
    private javax.swing.JPanel panelOtpButtons;
    private javax.swing.JPanel panelOtpPodaci;
    private javax.swing.JPanel panelOtpremnica;
    private javax.swing.JPanel panelPoslPart;
    private javax.persistence.Query query;
    private javax.swing.JTable tblFakture;
    private javax.swing.JTable tblStavke;
    private javax.swing.JTextArea txtOpisFooter;
    private javax.swing.JTextArea txtOpisHeader;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
