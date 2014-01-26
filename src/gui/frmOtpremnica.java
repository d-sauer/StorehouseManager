/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * frmOtpremnica.java
 *
 * Created on 16.03.2009., 01:07:32
 */
package gui;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.JOptionPane;
import persistence.Otpremnica;
import persistence.Partner;
import persistence.Racun;
import persistence.Stavkaponuda;
import persistence.Zaposlenici;

/**
 *
 * @author sheky
 */
public class frmOtpremnica extends javax.swing.JPanel {

    public String reportFile = "report/Otpremnica.jasper"; //.jrxml";
    private int IdRacuna;
    private boolean isNovaOtp;
    private String partner = null;
    private Integer idOtpremnice = null;
    private int idPart = 0;
    private String mjestoKreiranja = null;
    //old variables za ROLLBACK
    private Integer old_idZap = null;
    private Date old_date = null;
    private String old_nacOtpreme = "";
    private List<Integer> old_stavkeOtp = new LinkedList<Integer>();
    //--

    /** Creates new form frmOtpremnica */
    public frmOtpremnica(int idRac, int idOtp, boolean novaOtpremnica) {
        initComponents();

        IdRacuna = idRac;
        idOtpremnice = (Integer) idOtp;
        isNovaOtp = novaOtpremnica;
        lblIdPonude.setText(String.valueOf(idRac));
        getListZaposlnika();

        if (novaOtpremnica == true) { //kreiranje nove otpremnice
            getRacun();
            listStOtpremnice.clear();
            newOtpremnica();
            getOtpremnice(IdRacuna);
        } else { //citanje postojece otpremnice
            getRacun();
            //Podaci o otpremnici
            getOtpremnice(IdRacuna);
            getStavkeOtp(idOtpremnice);
        }
    }

    public void newOtpremnica() {
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        } else {
            entityManager.getTransaction().rollback();
            entityManager.getTransaction().begin();
        }
        persistence.Otpremnica newOtp = new persistence.Otpremnica();
        newOtp.setDatum(new Date());
        newOtp.setIdRacuna(IdRacuna);
        entityManager.persist(newOtp);
        entityManager.getTransaction().commit();

        idOtpremnice = newOtp.getIdOtp();
    }

    public void getListZaposlnika() {
        EntityManager em = entityManager;
        String strQuery = "SELECT z FROM Zaposlenici z";
        Query q = em.createQuery(strQuery);
        Collection data = q.getResultList();
        persistence.Zaposlenici zap = null;
        String zaposlenik = null;
        for (Object entity : data) {
            zap = (Zaposlenici) entity;
            zaposlenik = zap.getIme() + " " + zap.getPrezime();
            cbZaposlenici.addItem(makeZaposlenikObject(zaposlenik, zap.getIdZap()));
        }
    }

    private Object makeZaposlenikObject(final String str, final int id) {
        return new Object() {

            @Override
            public String toString() {
                return str;
            }
            //koristi se za pohranjivanje ID partnera

            @Override
            public int hashCode() {
                return id;
            }
        };
    }

    private int getSelZaposlenikId() {
        Object o = cbZaposlenici.getSelectedItem();
        return o.hashCode();
    }

    private void setSelZaposlenikId(int idZap) {
        int len = cbZaposlenici.getItemCount();
        Object o;
        for (int i = 0; i < len; i++) {
            o = cbZaposlenici.getItemAt(i);
            if (idZap == o.hashCode()) {
                System.out.println("set sel zaposlenik: " + i);
                cbZaposlenici.setSelectedIndex(i);
                break;
            }
        }
    }

    public void getRacun() {
        if (IdRacuna != -1) {
            int idRac = IdRacuna;

            EntityManager em = entityManager;
            String strQuery = "SELECT r FROM Racun r WHERE r.idRacuna = :idRacuna";
            Query q = em.createQuery(strQuery);
            q.setParameter("idRacuna", idRac);
            persistence.Racun racun = (Racun) q.getSingleResult();

            mjestoKreiranja = racun.getMjestoIzdavanja();
            idPart = racun.getIdPartner();


            getStavkeRacuna(idRac);
        }
    }

    private void getStavkeRacuna(int idRac) {
        String strQuery = "SELECT sp FROM Stavkaponuda sp WHERE sp.idStPon NOT IN (SELECT so.stavkaotpremnicePK.idStPon FROM Stavkaotpremnice so) AND sp.idRacuna=:idRacuna";
        EntityManager em = entityManager;
        Query q = em.createQuery(strQuery);
        q.setParameter("idRacuna", idRac);
        Collection data = q.getResultList();
        listStPonude.clear();
        listStPonude.addAll(data);
    }

    public void getOtpremnice(int idPon) {
        String strQuery = "SELECT o FROM Otpremnica o WHERE o.IdRacuna = :IdRacuna";
        EntityManager em = entityManager;
        Query q = em.createQuery(strQuery);
        q.setParameter("IdRacuna", idPon);


        if (q.getResultList().size() != 0) {
            persistence.Otpremnica otp = (Otpremnica) q.getSingleResult();
            idOtpremnice = otp.getIdOtp();
            lblIdOtpremnice.setText(idOtpremnice.toString());
            txtNacOtpreme.setText(otp.getNacin());

            strQuery = "SELECT p FROM Partner p WHERE p.idPartner = :idPartner";
            q = em.createQuery(strQuery);
            q.setParameter("idPartner", idPart);
            persistence.Partner part = (Partner) q.getSingleResult();

            String naziv = null;
            if (part.getNaziv() == null) {
                naziv = part.getIme() + " " + part.getPrezime();
            } else {
                naziv = part.getNaziv();
            }

            lblTekstOtp.setText("Za " + part.getAdresa() + ", " + part.getMjesto() + ", " + naziv);
            Date datum = otp.getDatum();
            old_date = datum;
            DateFormat formatDatuma = new SimpleDateFormat("dd.MM.yyyy.");

            txtDatum.setText(formatDatuma.format(datum).toString());
            if (otp.getIzdao() != null) {
                setSelZaposlenikId(otp.getIzdao());
            }

            old_idZap = otp.getIzdao();
            old_nacOtpreme = otp.getNacin();
        }
    }

    public void getStavkeOtp(int idOtp) {
        String strQuery = "SELECT sp FROM Stavkaponuda sp, Stavkaotpremnice so WHERE (sp.idStPon=so.stavkaotpremnicePK.idStPon) AND so.stavkaotpremnicePK.idOtp=:idOtp";
        EntityManager em = entityManager;
        Query q = em.createQuery(strQuery);
        q.setParameter("idOtp", idOtp);
        Collection data = q.getResultList();
        //stavke otpremnice za ROLLBACK, u slucaju odustajanja od promjena
        for (Object o : data) {
            persistence.Stavkaponuda sp = (Stavkaponuda) o;
            if (sp.getIdStPon() != null) {
                System.out.println("ADD old_stavkeOtp ID: " + sp.getIdStPon().toString());
                old_stavkeOtp.add(sp.getIdStPon());
            } else {
                System.out.println("ADD old_stavkeOtp ID: null");
            }
            entityManager.refresh(o);
        }

        listStOtpremnice.clear();
        listStOtpremnice.addAll(data);
    }

    public void addStavkaOtp() {
        int selRow = tblStRacuna.getSelectedRow();
        if (selRow != -1) {
            int selIdStavke = listStPonude.get(selRow).getIdStPon();
            persistence.Stavkaponuda stRac = listStPonude.get(selRow);
            listStPonude.remove(stRac);
            listStOtpremnice.add(stRac);
            tblStRacuna.updateUI();
            tblStOtpremnice.updateUI();


            if (!entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().begin();
            } else {
                entityManager.getTransaction().rollback();
                entityManager.getTransaction().begin();
            }
            persistence.Stavkaotpremnice so = new persistence.Stavkaotpremnice(selIdStavke, idOtpremnice);
            entityManager.persist(so);

            entityManager.getTransaction().commit();
        }

    }

    public void delStavkaOtp() {
        int selRow = tblStOtpremnice.getSelectedRow();
        if (selRow != -1) {
            int selIdStavke = listStOtpremnice.get(selRow).getIdStPon();
            persistence.Stavkaponuda stPon = listStOtpremnice.get(selRow);
            listStOtpremnice.remove(stPon);
            listStPonude.add(stPon);
            tblStOtpremnice.updateUI();
            tblStRacuna.updateUI();


            if (!entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().begin();
            } else {
                entityManager.getTransaction().rollback();
                entityManager.getTransaction().begin();
            }
            persistence.StavkaotpremnicePK soPK = new persistence.StavkaotpremnicePK(selIdStavke, idOtpremnice);
            persistence.Stavkaotpremnice so = entityManager.find(persistence.Stavkaotpremnice.class, soPK);
            entityManager.remove(so);

            entityManager.getTransaction().commit();
        }
    }

    private void acceptOtpremnica() {
        if (txtDatum.getText().isEmpty() || txtNacOtpreme.getText().isEmpty() || cbZaposlenici.getSelectedItem().toString().isEmpty() || listStOtpremnice.isEmpty()) {
            String[] btn = {"Uredu"};
            JOptionPane.showOptionDialog(null, "Niste unjeli sve podatke!", "Otpremnica", 0, JOptionPane.INFORMATION_MESSAGE, null, btn, 0);
            return;
        }

        Integer idZap = null;
        Date date = null;
        String nacOtpreme = "";

        idZap = getSelZaposlenikId();

        DateFormat df = new SimpleDateFormat("dd.MM.yyyy.");
        try {
            date = df.parse(txtDatum.getText());
        } catch (ParseException ex) {
            System.out.println("nepravilno unesen datum");
        }
        System.out.println("unesen datum: " + date.toString());

        nacOtpreme = txtNacOtpreme.getText();


        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        } else {
            entityManager.getTransaction().rollback();
            entityManager.getTransaction().begin();
        }

        persistence.Otpremnica otp = entityManager.find(persistence.Otpremnica.class, idOtpremnice);
        otp.setDatum(date);
        otp.setNacin(nacOtpreme);
        otp.setIzdao(idZap);
        entityManager.persist(otp);
        entityManager.getTransaction().commit();
    }

    private void abortOtpremnica() {
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        } else {
            entityManager.getTransaction().rollback();
            entityManager.getTransaction().begin();
        }

        persistence.Otpremnica otp = entityManager.find(persistence.Otpremnica.class, idOtpremnice);
        otp.setDatum(old_date);
        otp.setIzdao(old_idZap);
        otp.setNacin(old_nacOtpreme);
        entityManager.persist(otp);
        entityManager.getTransaction().commit();

        //trenutne stavke otpremnice
        List<Integer> new_stavkeOtp = new LinkedList<Integer>();
        Iterator<Stavkaponuda> itNew = listStOtpremnice.iterator();

        Iterator<Integer> itOld = old_stavkeOtp.iterator();

        List<Integer> lstRB = new LinkedList<Integer>();
        Integer oldIdSt;
        Integer newIdSt;
        boolean postoji = false;

        //persistence.StavkaotpremnicePK soPK = null;

        entityManager.getTransaction().begin();
        while (itNew.hasNext()) {
            persistence.StavkaotpremnicePK soPK = new persistence.StavkaotpremnicePK(itNew.next().getIdStPon(), idOtpremnice);
            persistence.Stavkaotpremnice so = entityManager.find(persistence.Stavkaotpremnice.class, soPK);
            entityManager.remove(so);
            System.out.println("remove: " + so.getStavkaotpremnicePK().getIdStPon() + "  idOtpremnice:" + so.getStavkaotpremnicePK().getIdOtp());
        }
        entityManager.getTransaction().commit();

        entityManager.getTransaction().begin();
        while (itOld.hasNext()) {
            persistence.StavkaotpremnicePK oldSoPK = new persistence.StavkaotpremnicePK(itOld.next(), idOtpremnice);
            persistence.Stavkaotpremnice oldSO = new persistence.Stavkaotpremnice(oldSoPK);
            entityManager.persist(oldSO);
            System.out.println("roolback: " + oldSO.getStavkaotpremnicePK().getIdStPon() + "  idOtpremnice:" + oldSO.getStavkaotpremnicePK().getIdOtp());
        }
        entityManager.getTransaction().commit();
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
        query = java.beans.Beans.isDesignTime() ? null : entityManager.createQuery("SELECT sp FROM Stavkaponuda sp");
        listStPonude = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : query.getResultList();
        listStOtpremnice = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : query.getResultList();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblStRacuna = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblStOtpremnice = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        lblIdOtpremnice = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblIdPonude = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblTekstOtp = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtNacOtpreme = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        cbZaposlenici = new javax.swing.JComboBox();
        jPanel4 = new javax.swing.JPanel();
        btnPrew = new javax.swing.JButton();
        btnAcceptOtp = new javax.swing.JButton();
        btnAbortOtp = new javax.swing.JButton();
        txtDatum = new javax.swing.JTextField();

        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Stavke ponude"));

        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, listStPonude, tblStRacuna);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${opis}"));
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
        jScrollPane2.setViewportView(tblStRacuna);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Stavke otpremnice"));

        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, listStOtpremnice, tblStOtpremnice);
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
        jScrollPane3.setViewportView(tblStOtpremnice);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
        );

        jButton1.setText("Dodaj u otpremnicu");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Obriši iz otpremnice");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel1.setText("Otpremnica:");

        lblIdOtpremnice.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblIdOtpremnice.setText("       ");

        jLabel3.setText("Datum:");

        jLabel5.setText("Otpremnica na temelju fakture:");

        lblIdPonude.setText("00");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Otpremnica"));

        lblTekstOtp.setText("Za");

        jLabel9.setText("Način otpreme:");

        jLabel10.setText("Izdao:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTekstOtp)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNacOtpreme, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbZaposlenici, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(140, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(lblTekstOtp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtNacOtpreme, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(cbZaposlenici, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel4.setLayout(new java.awt.GridLayout(1, 0, 3, 0));

        btnPrew.setText("Pregled");
        btnPrew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrewActionPerformed(evt);
            }
        });
        jPanel4.add(btnPrew);

        btnAcceptOtp.setText("Prihvati");
        btnAcceptOtp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAcceptOtpActionPerformed(evt);
            }
        });
        jPanel4.add(btnAcceptOtp);

        btnAbortOtp.setText("Odustani");
        btnAbortOtp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbortOtpActionPerformed(evt);
            }
        });
        jPanel4.add(btnAbortOtp);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblIdOtpremnice)
                        .addGap(65, 65, 65)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDatum, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 75, Short.MAX_VALUE)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblIdPonude)
                        .addGap(19, 19, 19))
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)))
                .addGap(0, 0, 0))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(315, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblIdOtpremnice)
                    .addComponent(jLabel5)
                    .addComponent(lblIdPonude)
                    .addComponent(jLabel3)
                    .addComponent(txtDatum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addGap(3, 3, 3)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        addStavkaOtp();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        delStavkaOtp();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void btnAbortOtpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbortOtpActionPerformed
        if (isNovaOtp == true) { //nova otpremnica- obrisati otpremnicu
            frmEvFaktura pon = new frmEvFaktura();
            pon.deleteOtpremnica(idOtpremnice);
            pon.removeAll();
        } else {
            abortOtpremnica();
        }
        this.removeAll();
        this.setVisible(false);
}//GEN-LAST:event_btnAbortOtpActionPerformed

    private void btnAcceptOtpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAcceptOtpActionPerformed
        acceptOtpremnica();
        this.removeAll();
        this.setVisible(false);
    }//GEN-LAST:event_btnAcceptOtpActionPerformed

    private void btnPrewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrewActionPerformed
        acceptOtpremnica();
        //pregled PDF-a
        frmPdfRead newReport = new frmPdfRead(reportFile);
        HashMap param = new HashMap();
        param.put("idOtp", idOtpremnice.toString());
        newReport.setParametri(param);
        newReport.setParentJPanel(this);
        MainWindow.openJPanel(newReport, false);
        System.out.println(entityManager.getDelegate().toString());
    }//GEN-LAST:event_btnPrewActionPerformed

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        System.out.println("frmOtpremnica shown...");
        MainWindow.openJPanel(this, false);
    }//GEN-LAST:event_formFocusGained

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbortOtp;
    private javax.swing.JButton btnAcceptOtp;
    private javax.swing.JButton btnPrew;
    private javax.swing.JComboBox cbZaposlenici;
    private javax.persistence.EntityManager entityManager;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblIdOtpremnice;
    private javax.swing.JLabel lblIdPonude;
    private javax.swing.JLabel lblTekstOtp;
    private java.util.List<persistence.Stavkaponuda> listStOtpremnice;
    private java.util.List<persistence.Stavkaponuda> listStPonude;
    private javax.persistence.Query query;
    private javax.swing.JTable tblStOtpremnice;
    private javax.swing.JTable tblStRacuna;
    private javax.swing.JTextField txtDatum;
    private javax.swing.JTextField txtNacOtpreme;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
