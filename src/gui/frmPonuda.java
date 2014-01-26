/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * frmPonuda.java
 *
 * Created on 14.03.2009., 16:19:36
 */
package gui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.beansbinding.AbstractBindingListener;
import org.jdesktop.beansbinding.PropertyStateEvent;
import persistence.Ponuda;

/**
 *
 * @author sheky
 */
public class frmPonuda extends javax.swing.JPanel {

    private Collection<persistence.Stavkaponuda> old_StPon;
    private Integer idPon = -1;
    public final int varKupac = 0;
    public final int varDobavljac = 1;
    private boolean isNewStav = false;
    public boolean novaPonuda = false;
    public String reportFile = "report/Ponuda.jasper"; //.jrxml";
    public static JPanel newPanel = null;
    //old variables
    private int old_idPartner;
    private String old_mjesto = null;
    private String old_opisHeader = null;
    private String old_opisFooter = null;
    //-----

    /** Creates new form frmPonuda */
    public frmPonuda() {
        initComponents();

        setSaveBtn(false);
        btnObrisiStavku.setEnabled(false);

        tblStavka.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

                    public void valueChanged(ListSelectionEvent e) {
                        btnObrisiStavku.setEnabled(true);
                        if (!entityManager.getTransaction().isActive()) {
                            entityManager.getTransaction().begin();
                        }
                    }
                });


        bindingGroup.addBindingListener(new AbstractBindingListener() {

            @Override
            public void targetChanged(org.jdesktop.beansbinding.Binding binding, PropertyStateEvent event) {
                setSaveBtn(true);
            }
        });
    }

//--- dodano (Alt + Insert -> Add properties , pod Type otic na browse i pronac persistence.Ponuda (tablica)
    protected Ponuda trenutnaPonuda;

    public Ponuda getTrenutnaPonuda() {
        return trenutnaPonuda;
    }

    public void setTrenutnaPonuda(Ponuda trenutnaPonuda, boolean nova_Ponuda) {
        novaPonuda = nova_Ponuda;

        Ponuda old = this.trenutnaPonuda; //dodano
        this.trenutnaPonuda = trenutnaPonuda;
        propertyChangeSupport.firePropertyChange("trenutnaPonuda", old, trenutnaPonuda); //dodano
        //--
        Date dK = trenutnaPonuda.getDatumKreiranja();
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy.");
        lblDatum.setText(df.format(dK).toString());
        txtMjesto.setText(trenutnaPonuda.getMjestoKreiranja());
        txtOpisHeader.setText(trenutnaPonuda.getOpisHeader());
        txtOpisFooter.setText(trenutnaPonuda.getOpisFooter());

        idPon = trenutnaPonuda.getIdPonuda();
        System.out.println("idPonude: " + idPon.toString());


        //Trenutna ponuda QUERY
        //izvrsavanje query-a za prikaz stavki samo trenutne ponude
        String strQuery = "SELECT s FROM Stavkaponuda s WHERE s.idPonuda = " + idPon.toString();
        System.out.println("Stavke query: " + strQuery);
        EntityManager em = entityManager;
        Query q = em.createQuery(strQuery);

        Collection data = q.getResultList();
        old_StPon = q.getResultList();

        listStavka.clear();
        listStavka.addAll(data);


        fillPopisPartnera();
        setSelPartnerId(trenutnaPonuda.getIdPartner());

        old_idPartner = trenutnaPonuda.getIdPartner();
        old_mjesto = txtMjesto.getText();
        old_opisHeader = txtOpisHeader.getText();
        old_opisFooter = txtOpisFooter.getText();
        //računanje ukupne cijene
        getUkupnaCj();
    }

    private void fillPopisPartnera() {
        //Popis Partnera QUERY
        String strQuery = "SELECT p FROM Partner p";

        EntityManager em1 = entityManager;
        Query q1 = em1.createQuery(strQuery);

        cbPopisPartnera.removeAllItems();
        Collection data1 = q1.getResultList();
        persistence.Partner pp;
        String tipP = null;

        for (Object o : data1) {
            pp = (persistence.Partner) o;
            String naziv = null;
            if (pp.getNaziv().toString().length() == 0) {
                naziv = pp.getIme() + " " + pp.getPrezime();
            } else {
                naziv = pp.getNaziv();
            }

            if (pp.getIdVrsta() == varKupac) {
                tipP = naziv + "  (kupac)";
            }
            if (pp.getIdVrsta() == varDobavljac) {
                tipP = naziv + "  (dobavljač)";
            }

            cbPopisPartnera.addItem(makeCBObject(tipP, pp.getIdPartner()));
        }
    }

    private int getSelPartnerId() {
        Object o = cbPopisPartnera.getSelectedItem();
        return o.hashCode();
    }

    private void setSelPartnerId(int idPart) {
        int len = cbPopisPartnera.getItemCount();
        Object o;
        for (int i = 0; i < len; i++) {
            o = cbPopisPartnera.getItemAt(i);
            if (idPart == o.hashCode()) {
                System.out.println("set partner: " + i);
                cbPopisPartnera.setSelectedIndex(i);
                break;
            }
        }
    }

    private Object makeCBObject(final String str, final int id) {
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

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
//----------------------------------------------------------------------------------------

    public void delIdPonuda(Integer idPon) {
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        } else {
            entityManager.getTransaction().rollback();
            entityManager.getTransaction().begin();
        }
        persistence.Ponuda pon = entityManager.find(persistence.Ponuda.class, idPon);
        entityManager.remove(pon);
        entityManager.getTransaction().commit();
        System.out.println("brisanje ponude idPonuda: " + idPon.toString());
    }

    public void delStavkeByIdPon(Integer idPon) {
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        } else {
            entityManager.getTransaction().rollback();
            entityManager.getTransaction().begin();
        }
        String strQuery = "DELETE FROM Stavkaponuda s WHERE s.idPonuda = " + idPon.toString();
        System.out.println("Delete query: " + strQuery);
        EntityManager em = entityManager;
        Query q = em.createQuery(strQuery);
        q.executeUpdate();
        entityManager.getTransaction().commit();
        System.out.println("brisanje stavki ponude: " + idPon.toString());
    }

    public void prihvatiPonudu(Integer idPon) {
        if (txtMjesto.getText().isEmpty() || cbPopisPartnera.getSelectedItem().toString().isEmpty() || txtOpisFooter.getText().isEmpty() || txtOpisHeader.getText().isEmpty() || listStavka.isEmpty()) {
            String[] btn = {"Uredu"};
            JOptionPane.showOptionDialog(null, "Podaci o poslovnom partneru su uspješno pohranjeni!", "Poslovni partner", 0, JOptionPane.INFORMATION_MESSAGE, null, btn, 0);
            return;
        }

        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        } else {
            entityManager.getTransaction().rollback();
            entityManager.getTransaction().begin();
        }
        String strQuery = "UPDATE Ponuda p SET p.idPartner = :idPartner, p.mjestoKreiranja = :mjesto, p.opisHeader = :opisHeader, p.opisFooter = :opisFooter WHERE p.idPonuda = :idPonuda";
        System.out.println("Update query: " + strQuery);
        EntityManager em = entityManager;
        Query q = em.createQuery(strQuery);
        q.setParameter("idPartner", getSelPartnerId());
        q.setParameter("mjesto", txtMjesto.getText());
        q.setParameter("opisHeader", txtOpisHeader.getText());
        q.setParameter("opisFooter", txtOpisFooter.getText());
        q.setParameter("idPonuda", idPon);

        q.executeUpdate();
        entityManager.getTransaction().commit();
    }

    public void getUkupnaCj() {
        int numRow = listStavka.size();
        int kol = 0;
        float jedCj = 0;
        float ukupno = 0;

        for (int i = 0; i < numRow; i++) {
            kol = listStavka.get(i).getKolicina();
            jedCj = listStavka.get(i).getJedCijena();
            ukupno += ((float) kol * jedCj);
        }
        lblUkupnaCijena.setText(Float.valueOf(ukupno).toString() + " kn");
    }

    public void setSaveBtn(boolean var) {
        btnPrihvatiStavku.setEnabled(var);
        btnOdustaniStavka.setEnabled(var);
    }

    public void novaStavka() {
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        } else {
            entityManager.getTransaction().rollback();
            entityManager.getTransaction().begin();
        }
        persistence.Stavkaponuda stavka = new persistence.Stavkaponuda();
        System.out.println("novaStavka idPon:" + idPon);
        stavka.setIdPonuda(idPon);

        entityManager.persist(stavka);
        listStavka.add(stavka);

        int row = listStavka.size() - 1;
        tblStavka.setRowSelectionInterval(row, row);
        tblStavka.scrollRectToVisible(tblStavka.getCellRect(row, 0, true));

        setSaveBtn(true);
        btnObrisiStavku.setEnabled(false);
        isNewStav = true;
    //btnObrisiStavku.setEnabled(false);
    }

    public void prihvatiStavku() {
        entityManager.getTransaction().commit();
        entityManager.getTransaction().begin();

        if (isNewStav == true) {
            isNewStav = false;
        }

        getUkupnaCj(); //racuna ukupnu cijenu
        setSaveBtn(false);
    }

    public void odustaniStavka() {
        entityManager.getTransaction().rollback();
        entityManager.getTransaction().begin();
        refreshStavke();
        setSaveBtn(false);
    }

    public void obirisStavku() {
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        }
        int selRow = tblStavka.getSelectedRow();
        int idStav = listStavka.get(selRow).getIdStPon();
        persistence.Stavkaponuda stavka = entityManager.find(persistence.Stavkaponuda.class, idStav);
        entityManager.remove(stavka);
        entityManager.getTransaction().commit();
        listStavka.remove(stavka);

        setSaveBtn(false);
        btnObrisiStavku.setEnabled(false);
    }

    public void refreshStavke() {
        int selRow = tblStavka.getSelectedRow();

        if (!entityManager.getTransaction().isActive()) {
            //    entityManager.getTransaction().begin();
        }
        String strQuery = "SELECT s FROM Stavkaponuda s WHERE s.idPonuda = " + idPon.toString();
        System.out.println("Stavke query: " + strQuery);
        EntityManager em = entityManager;
        Query q = em.createQuery(strQuery);

        Collection data = q.getResultList();
        for (Object entity : data) {
            entityManager.refresh(entity);
        }
        listStavka.clear();
        listStavka.addAll(data);

        tblStavka.updateUI();
        if (isNewStav != true) {
            tblStavka.setRowSelectionInterval(selRow, selRow);
        }
    }

    private void rollBackStPon() {
        System.out.println("+++++ roll back +++++++");
        Iterator<persistence.Stavkaponuda> new_it = listStavka.iterator();
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        } else {
            entityManager.getTransaction().rollback();
            entityManager.getTransaction().begin();
        }
        while (new_it.hasNext()) {
            persistence.Stavkaponuda stPon = new_it.next();
            if (!old_StPon.contains(stPon)) { //dodati stavku koja je obrisana, da bi dobili stavku iz stare liste
                System.out.println("Stara stavka NIJE u novoj listi:" + stPon.getIdStPon().toString() + "  - izbaciti" + "  opis:" + stPon.getOpis());
                persistence.Stavkaponuda rmStPon = entityManager.find(persistence.Stavkaponuda.class, stPon.getIdStPon());
                entityManager.remove(rmStPon);
            }
        }
        entityManager.getTransaction().commit();

        entityManager.getTransaction().begin();
        System.out.println("------ roll back --------");
        Iterator<persistence.Stavkaponuda> old_it = old_StPon.iterator();
        while (old_it.hasNext()) {
            persistence.Stavkaponuda stPon = old_it.next();
            if (listStavka.contains(stPon)) {
                System.out.println("Nova stavka u staroj listi: " + stPon.getIdStPon().toString());
            } else {
                System.out.println("Nova stavka NIJE u staroj listi: " + stPon.getIdStPon().toString() + "  + dodati" + "  opis:" + stPon.getOpis());
                //persistence.Stavkaponuda newStPon = entityManager.find(persistence.Stavkaponuda.class, stPon.getIdStPon());
                persistence.Stavkaponuda newStPon = new persistence.Stavkaponuda();
                newStPon = stPon;
                newStPon.setIdStPon(null);
                entityManager.persist(newStPon);
            }

        }
        entityManager.getTransaction().commit();

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        entityManager = java.beans.Beans.isDesignTime() ? null : javax.persistence.Persistence.createEntityManagerFactory("purgarPU").createEntityManager();
        queryStavka = java.beans.Beans.isDesignTime() ? null : entityManager.createQuery("SELECT s FROM Stavkaponuda s");
        listStavka = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(queryStavka.getResultList());
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cbPopisPartnera = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblStavka = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        txtOpis = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtDimenz = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtKolicina = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtJedCijena = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        btnNovaStavka = new javax.swing.JButton();
        btnOdustaniStavka = new javax.swing.JButton();
        btnPrihvatiStavku = new javax.swing.JButton();
        btnObrisiStavku = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        lblUkupnaCijena = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        btnOdustani = new javax.swing.JButton();
        panelTextPonude = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtOpisHeader = new javax.swing.JTextArea();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtOpisFooter = new javax.swing.JTextArea();
        jLabel13 = new javax.swing.JLabel();
        lblDatum = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtMjesto = new javax.swing.JTextField();
        tbtextPonude = new javax.swing.JToggleButton();

        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Poslovni partner"));

        jLabel1.setText("Poslovni partner:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbPopisPartnera, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(260, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel1)
                .addComponent(cbPopisPartnera, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel3.setText("Ponuda:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 12));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${trenutnaPonuda.idPonuda}"), jLabel4, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Stavke ponude"));

        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, listStavka, tblStavka);
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
        jTableBinding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane1.setViewportView(tblStavka);

        jLabel5.setText("Opis:");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblStavka, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.opis}"), txtOpis, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblStavka, org.jdesktop.beansbinding.ELProperty.create("${selectedElement!=null}"), txtOpis, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel6.setText("Dimenzije:");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblStavka, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.dimenzije}"), txtDimenz, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblStavka, org.jdesktop.beansbinding.ELProperty.create("${selectedElement!=null}"), txtDimenz, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel7.setText("Komada:");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblStavka, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.kolicina}"), txtKolicina, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblStavka, org.jdesktop.beansbinding.ELProperty.create("${selectedElement!=null}"), txtKolicina, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel8.setText("Jedinična cijena:");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblStavka, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.jedCijena}"), txtJedCijena, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblStavka, org.jdesktop.beansbinding.ELProperty.create("${selectedElement!=null}"), txtJedCijena, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jPanel3.setLayout(new java.awt.GridLayout(2, 2, 1, 1));

        btnNovaStavka.setText("Nova stavka");
        btnNovaStavka.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovaStavkaActionPerformed(evt);
            }
        });
        jPanel3.add(btnNovaStavka);

        btnOdustaniStavka.setText("Odustani");
        btnOdustaniStavka.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOdustaniStavkaActionPerformed(evt);
            }
        });
        jPanel3.add(btnOdustaniStavka);

        btnPrihvatiStavku.setText("Prihvati");
        btnPrihvatiStavku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrihvatiStavkuActionPerformed(evt);
            }
        });
        jPanel3.add(btnPrihvatiStavku);

        btnObrisiStavku.setText("Obriši");
        btnObrisiStavku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnObrisiStavkuActionPerformed(evt);
            }
        });
        jPanel3.add(btnObrisiStavku);

        jLabel11.setText("Ukupno:");

        lblUkupnaCijena.setText("00, 00");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(28, 28, 28)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtDimenz, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(158, 158, 158))
                            .addComponent(txtOpis, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtKolicina, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtJedCijena, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(18, 18, 18)
                        .addComponent(lblUkupnaCijena))
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtOpis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUkupnaCijena)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(txtDimenz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(txtKolicina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(txtJedCijena, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel4.setLayout(new java.awt.GridLayout(1, 0, 3, 0));

        jButton2.setText("Pregled");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton2);

        jButton1.setText("Prihvati");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton1);

        btnOdustani.setText("Odustani");
        btnOdustani.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOdustaniActionPerformed(evt);
            }
        });
        jPanel4.add(btnOdustani);

        panelTextPonude.setBorder(javax.swing.BorderFactory.createTitledBorder("Tekst ponude"));

        txtOpisHeader.setColumns(20);
        txtOpisHeader.setFont(new java.awt.Font("Arial", 0, 10));
        txtOpisHeader.setRows(2);
        jScrollPane2.setViewportView(txtOpisHeader);

        jLabel9.setText("Tekst prije stavki ponude");

        jLabel10.setText("Tekst poslje stavki ponude");

        txtOpisFooter.setColumns(20);
        txtOpisFooter.setFont(new java.awt.Font("Arial", 0, 10));
        txtOpisFooter.setRows(2);
        jScrollPane3.setViewportView(txtOpisFooter);

        javax.swing.GroupLayout panelTextPonudeLayout = new javax.swing.GroupLayout(panelTextPonude);
        panelTextPonude.setLayout(panelTextPonudeLayout);
        panelTextPonudeLayout.setHorizontalGroup(
            panelTextPonudeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTextPonudeLayout.createSequentialGroup()
                .addComponent(jLabel9)
                .addContainerGap(396, Short.MAX_VALUE))
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)
            .addGroup(panelTextPonudeLayout.createSequentialGroup()
                .addComponent(jLabel10)
                .addContainerGap())
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)
        );
        panelTextPonudeLayout.setVerticalGroup(
            panelTextPonudeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTextPonudeLayout.createSequentialGroup()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );

        jLabel13.setText("Datum:");

        lblDatum.setText("dd.mm.yyyy.");

        jLabel14.setText("Mjesto:");

        tbtextPonude.setSelected(true);
        tbtextPonude.setText("Tekst ponude");
        tbtextPonude.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbtextPonudeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 137, Short.MAX_VALUE)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDatum, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMjesto, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tbtextPonude)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 174, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(panelTextPonude, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(txtMjesto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel14)
                        .addComponent(lblDatum, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13))
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelTextPonude, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tbtextPonude))
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnOdustaniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOdustaniActionPerformed
        if (novaPonuda == true) {
            delStavkeByIdPon(idPon);
            delIdPonuda(idPon);
        } else { //rollback
            txtMjesto.setText(old_mjesto);
            txtOpisHeader.setText(old_opisHeader);
            txtOpisFooter.setText(old_opisFooter);
            System.out.println("old mjesto: " + old_mjesto + "  header:" + old_opisHeader + "   footer" + old_opisFooter);

            setSelPartnerId(old_idPartner);
            rollBackStPon();
            prihvatiPonudu(idPon);
        }

        this.removeAll();
        this.setVisible(false);
    }//GEN-LAST:event_btnOdustaniActionPerformed

    private void btnPrihvatiStavkuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrihvatiStavkuActionPerformed
        prihvatiStavku();
}//GEN-LAST:event_btnPrihvatiStavkuActionPerformed

    private void btnNovaStavkaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovaStavkaActionPerformed
        novaStavka();
    }//GEN-LAST:event_btnNovaStavkaActionPerformed

    private void btnObrisiStavkuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnObrisiStavkuActionPerformed
        obirisStavku();
    }//GEN-LAST:event_btnObrisiStavkuActionPerformed

    private void btnOdustaniStavkaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOdustaniStavkaActionPerformed
        odustaniStavka();
    }//GEN-LAST:event_btnOdustaniStavkaActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        prihvatiPonudu(idPon);
        this.setVisible(false);
        frmEvPonuda.newPanel = null;
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        prihvatiPonudu(idPon);
        frmPdfRead newReport = new frmPdfRead(reportFile);
        HashMap param = new HashMap();
        param.put("idPonude", idPon.toString());
        newReport.setParametri(param);
        newReport.setParentJPanel(this);
        MainWindow.openJPanel(newReport, false);
        System.out.println(entityManager.getDelegate().toString());
    }//GEN-LAST:event_jButton2ActionPerformed

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        System.out.println("frmPonuda shown...");
        MainWindow.openJPanel(this, false);
    }//GEN-LAST:event_formFocusGained

    private void tbtextPonudeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbtextPonudeActionPerformed
        if (tbtextPonude.isSelected()) {
            panelTextPonude.setVisible(true);
        } else {
            panelTextPonude.setVisible(false);
        }
        this.updateUI();
    }//GEN-LAST:event_tbtextPonudeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnNovaStavka;
    private javax.swing.JButton btnObrisiStavku;
    private javax.swing.JButton btnOdustani;
    private javax.swing.JButton btnOdustaniStavka;
    private javax.swing.JButton btnPrihvatiStavku;
    private javax.swing.JComboBox cbPopisPartnera;
    private javax.persistence.EntityManager entityManager;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblDatum;
    private javax.swing.JLabel lblUkupnaCijena;
    private java.util.List<persistence.Stavkaponuda> listStavka;
    private javax.swing.JPanel panelTextPonude;
    private javax.persistence.Query queryStavka;
    private javax.swing.JTable tblStavka;
    private javax.swing.JToggleButton tbtextPonude;
    private javax.swing.JTextField txtDimenz;
    private javax.swing.JTextField txtJedCijena;
    private javax.swing.JTextField txtKolicina;
    private javax.swing.JTextField txtMjesto;
    private javax.swing.JTextField txtOpis;
    private javax.swing.JTextArea txtOpisFooter;
    private javax.swing.JTextArea txtOpisHeader;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
