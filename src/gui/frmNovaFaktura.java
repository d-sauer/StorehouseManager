/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * frmNovaFaktura.java
 *
 * Created on 13.03.2009., 19:51:01
 */
package gui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.beansbinding.AbstractBindingListener;
import org.jdesktop.beansbinding.PropertyStateEvent;
import persistence.Racun;
import persistence.Stavkaponuda;

/**
 *
 * @author sheky
 */
public class frmNovaFaktura extends javax.swing.JPanel {

    public String reportFile = "report/Racun.jasper"; //.jrxml";
    private Integer idRacuna = null;
    private Integer idPonuda = null;
    private String mjestoIzdavanja = null;
    private Float iznosRacuna = null;
    private boolean novaFaktura = true;
    private boolean isNewStav = false;
    public final int varKupac = 0;
    public final int varDobavljac = 1;
    //old value - za roll back
    private String old_mjestoIzdavanja = null;
    //private List<persistence.Stavkaponuda> oldStavke = new LinkedList();
    private Collection<persistence.Stavkaponuda> oldStavke;
    private String oldHeader = null;
    private String oldFooter = null;
    //

    /** Creates new form frmNovaFaktura */
    public frmNovaFaktura(int idRac, boolean nova) {
        initComponents();

        panelStavkeFakture.setVisible(false);
        panelTextFakture.setVisible(false);
        btnPrihvatiStavku.setEnabled(false);
        btnOdustaniStavka.setEnabled(false);
        btnObrisiStavku.setEnabled(false);
        panelStavkeFakture.setEnabledAt(1, false);

        fillPopisPartnera();

        idRacuna = idRac;
        novaFaktura = nova;
        if (nova == true) { //nova faktura
            newFaktura();
        } else { //editiranje postojece fakture
            readFaktura();
            loadStavkeRac(idRac);
            getUkupnaCj();
        }




        tblStavka.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

                    public void valueChanged(ListSelectionEvent e) {
                        btnObrisiStavku.setEnabled(true);
                        getSelectedStavkaPremision();
                        if (!entityManager.getTransaction().isActive()) {
                            entityManager.getTransaction().begin();
                        }
                    }
                });

        bindingGroup.addBindingListener(new AbstractBindingListener() {

            @Override
            public void targetChanged(org.jdesktop.beansbinding.Binding binding, PropertyStateEvent event) {
                btnPrihvatiStavku.setEnabled(true);
                btnOdustaniStavka.setEnabled(true);
            }
        });

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

    public void getSelectedStavkaPremision() {
        int selRow = tblStavka.getSelectedRow();
        if (selRow != -1) {
            Integer idPon = listStavka.get(selRow).getIdPonuda();
            if ((idPon != null)) { //stavka pripada ponudi, nemogu se mjenjati parametri
                //panelStavkeFakture.setEnabledAt(0, false);
                txtOpis.setEditable(false);
                txtDimenz.setEditable(false);
                txtKolicina.setEditable(false);
                txtJedCijena.setEditable(false);
                btnObrisiStavku.setEnabled(false);
                panelStavkeFakture.setSelectedIndex(1);
            } else { //stavka NE pripada ponudi
                //panelStavkeFakture.setEnabledAt(0, true);
                txtOpis.setEditable(true);
                txtDimenz.setEditable(true);
                txtKolicina.setEditable(true);
                txtJedCijena.setEditable(true);
                btnObrisiStavku.setEnabled(true);

                panelStavkeFakture.setSelectedIndex(0);
            }
        }
    }

    public void setStavkePonude(Integer idPon) {
        idPonuda = idPon;
        String natpis = "Stavke iz ponude br. " + idPon.toString();
        panelStavkeFakture.setTitleAt(1, natpis);

        getSlobodneStavkePon(idPon);

        panelStavkeFakture.setEnabledAt(1, true);
        panelStavkeFakture.setSelectedIndex(1);
    }

    public void getSlobodneStavkePon(Integer idPon) {
        EntityManager em = entityManager;
        String sqlQuery = "SELECT sp FROM Stavkaponuda sp WHERE sp.idStPon NOT IN (SELECT s.idStPon FROM Stavkaponuda s WHERE (s.idRacuna=:idRacuna)) AND (sp.idPonuda=:idPonuda)";
        Query q = em.createQuery(sqlQuery);
        q.setParameter("idRacuna", idRacuna);
        q.setParameter("idPonuda", idPon);
        Collection data = q.getResultList();
        listStPonude.clear();
        listStPonude.addAll(data);
    }

    public void loadStavkeRac(int idRac) {
        EntityManager em = entityManager;
        String sqlQuery = "SELECT sp FROM Stavkaponuda sp WHERE sp.idRacuna=:idRacuna";
        Query q = em.createQuery(sqlQuery);
        q.setParameter("idRacuna", idRac);
        Collection data = q.getResultList();
        oldStavke = q.getResultList();
        listStavka.clear();
        listStavka.addAll(data);
    }

    public void getUkupnaCj() {
        int numRow = listStavka.size();
        int kol = 0;
        float jedCj = 0;
        float ukupno = 0;
        float pdv = (float) 0.22;
        float iznos_pdv = 0;
        float iznos = 0;

        for (int i = 0; i < numRow; i++) {
            kol = listStavka.get(i).getKolicina();
            jedCj = listStavka.get(i).getJedCijena();
            ukupno += ((float) kol * jedCj);
        }
        lblUkupnaCijena.setText(Float.valueOf(ukupno).toString() + " kn");
        iznos_pdv = pdv * ukupno;
        lblPDV.setText(Float.valueOf(iznos_pdv).toString() + " kn");
        iznos = iznos_pdv + ukupno;
        lblUkupanIznos.setText(Float.valueOf(iznos).toString() + " kn");
        iznosRacuna = (float) iznos;
    }

    public void newFaktura() {
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        } else {
            entityManager.getTransaction().rollback();
            entityManager.getTransaction().begin();
        }

        persistence.Racun rac = new persistence.Racun();
        //rac.setIdPonuda(idPonude);
        rac.setDatumIzdavanja(new Date());
        entityManager.persist(rac);
        entityManager.getTransaction().commit();

        DateFormat df = new SimpleDateFormat("dd.MM.yyyy.");
        lblDatum.setText(df.format(new Date()));
        idRacuna = rac.getIdRacuna();
        lblIdFakture.setText(idRacuna.toString());
    }

    public void readFaktura() {
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        } else {
            entityManager.getTransaction().rollback();
            entityManager.getTransaction().begin();
        }

        String sqlQuery = "SELECT r FROM Racun r WHERE r.idRacuna=:idRacuna";
        Query q = entityManager.createQuery(sqlQuery);
        q.setParameter("idRacuna", idRacuna);
        persistence.Racun rac = (Racun) q.getResultList().get(0);

        entityManager.getTransaction().commit();

        DateFormat df = new SimpleDateFormat("dd.MM.yyyy.");
        lblDatum.setText(df.format(rac.getDatumIzdavanja()));
        idRacuna = rac.getIdRacuna();
        lblIdFakture.setText(idRacuna.toString());
        txtHeader.setText(rac.getOpisHeader());
        txtFooter.setText(rac.getOpisFooter());
        txtMjestoIzdavanja.setText(rac.getMjestoIzdavanja());
        setSelPartnerId(rac.getIdPartner());

        old_mjestoIzdavanja = rac.getMjestoIzdavanja();
        oldHeader = rac.getOpisHeader();
        oldFooter = rac.getOpisFooter();


        //pretraživanje dali faktura ima neku od stavki ponude, tj. dali je faktura kreirana iz neke ponude
        entityManager.getTransaction().begin();
        sqlQuery = "SELECT sp FROM Stavkaponuda sp WHERE sp.idRacuna=:idRacuna AND sp.idPonuda>0";
        q = entityManager.createQuery(sqlQuery);
        q.setParameter("idRacuna", idRacuna);
        if (!q.getResultList().isEmpty()) {
            persistence.Stavkaponuda stPon = (Stavkaponuda) q.getResultList().get(0);
            if (stPon.getIdPonuda() != null) {
                setStavkePonude(stPon.getIdPonuda());
            }
        }
        entityManager.getTransaction().commit();
    }

    public void acceptFaktura() {
        if (txtMjestoIzdavanja.getText().isEmpty() || cbPopisPartnera.getSelectedItem().toString().isEmpty() || txtHeader.getText().isEmpty() || txtFooter.getText().isEmpty() || (listStavka.isEmpty())) {
            String[] btn = {"Uredu"};
            JOptionPane.showOptionDialog(null, "Niste unjeli sve podatke!", "Faktura", 0, JOptionPane.INFORMATION_MESSAGE, null, btn, 0);
            return;
        }

        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        } else {
            entityManager.getTransaction().rollback();
            entityManager.getTransaction().begin();
        }
        persistence.Racun rac = entityManager.find(persistence.Racun.class, idRacuna);
        rac.setOpisHeader(txtHeader.getText());
        rac.setOpisFooter(txtFooter.getText());
        rac.setIznos(iznosRacuna);
        rac.setMjestoIzdavanja(txtMjestoIzdavanja.getText());
        rac.setIdPartner(getSelPartnerId());

        entityManager.persist(rac);

        entityManager.getTransaction().commit();
        System.out.println("faktura fakturirana");
    }

    public void deleteFaktura() {
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        } else {
            entityManager.getTransaction().rollback();
            entityManager.getTransaction().begin();
        }
        persistence.Racun rac = entityManager.find(persistence.Racun.class, idRacuna);
        entityManager.remove(rac);
        entityManager.getTransaction().commit();
    }

    public void novaStavka() {
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        } else {
            entityManager.getTransaction().rollback();
            entityManager.getTransaction().begin();
        }
        txtOpis.setEditable(true);
        txtDimenz.setEditable(true);
        txtKolicina.setEditable(true);
        txtJedCijena.setEditable(true);


        persistence.Stavkaponuda stavka = new persistence.Stavkaponuda();
        System.out.println("novaStavkaRacuna idRac:" + idRacuna);
        stavka.setIdRacuna(idRacuna);

        entityManager.persist(stavka);
        listStavka.add(stavka);

        int row = listStavka.size() - 1;
        tblStavka.setRowSelectionInterval(row, row);
        tblStavka.scrollRectToVisible(tblStavka.getCellRect(row, 0, true));

        btnPrihvatiStavku.setEnabled(true);
        btnOdustaniStavka.setEnabled(true);
        btnObrisiStavku.setEnabled(false);
        isNewStav = true;
    //btnObrisiStavku.setEnabled(false);
    }

    public void odustaniStavka() {
        entityManager.getTransaction().rollback();
        entityManager.getTransaction().begin();
        refreshStavke();
        btnPrihvatiStavku.setEnabled(false);
        btnOdustaniStavka.setEnabled(false);
    }

    public void refreshStavke() {
        //int selRow = tblStavka.getSelectedRow();

        if (!entityManager.getTransaction().isActive()) {
            //    entityManager.getTransaction().begin();
        }
        String strQuery = "SELECT s FROM Stavkaponuda s WHERE s.idRacuna = :idRacuna";
        System.out.println("Stavke query: " + strQuery);
        EntityManager em = entityManager;
        Query q = em.createQuery(strQuery);
        q.setParameter("idRacuna", idRacuna);

        Collection data = q.getResultList();
        for (Object entity : data) {
            entityManager.refresh(entity);
        }
        listStavka.clear();
        listStavka.addAll(data);

        tblStavka.updateUI();
    //if (isNewStav != true) {
    //    tblStavka.setRowSelectionInterval(selRow, selRow);
    //}
    }

    public void prihvatiStavku() {
        entityManager.getTransaction().commit();
        entityManager.getTransaction().begin();

        if (isNewStav == true) {
            isNewStav = false;
        }

        getUkupnaCj(); //racuna ukupnu cijenu
        btnPrihvatiStavku.setEnabled(false);
        btnOdustaniStavka.setEnabled(false);
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

        btnPrihvatiStavku.setEnabled(false);
        btnOdustaniStavka.setEnabled(false);
        btnObrisiStavku.setEnabled(false);
    }

    public void dodajStFakture() {
        int selRow = tblStPonude.getSelectedRow();
        if (selRow != -1) {
            if (!entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().begin();
            } else {
                entityManager.getTransaction().rollback();
                entityManager.getTransaction().begin();
            }

            Integer IdStPon = listStPonude.get(selRow).getIdStPon();
            Integer idPon = listStPonude.get(selRow).getIdPonuda();
            persistence.Stavkaponuda stPon = entityManager.find(persistence.Stavkaponuda.class, IdStPon);
            stPon.setIdRacuna(idRacuna);
            entityManager.persist(stPon);
            entityManager.getTransaction().commit();
            getSlobodneStavkePon(idPon);
            refreshStavke();
        }
    }

    private void obrisiStFakture() {
        int selRow = tblStavka.getSelectedRow();
        if (selRow != -1) {
            Integer idPon = listStavka.get(selRow).getIdPonuda();
            if (idPon != null) {
                if (!entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().begin();
                } else {
                    entityManager.getTransaction().rollback();
                    entityManager.getTransaction().begin();
                }
                Integer idStPon = listStavka.get(selRow).getIdStPon();
                persistence.Stavkaponuda stPon = entityManager.find(persistence.Stavkaponuda.class, idStPon);
                stPon.setIdRacuna(null);
                entityManager.persist(stPon);
                entityManager.getTransaction().commit();
                tblStavka.clearSelection();
                getSlobodneStavkePon(idPon);
                refreshStavke();
            }
        }
    }

    private void rollBackFaktura() {
        Collection<persistence.Stavkaponuda> new_data = listStavka;

        Iterator<persistence.Stavkaponuda> old_it;
        old_it = oldStavke.iterator();
        System.out.println("Stara lista");
        while (old_it.hasNext()) {
            System.out.println("idStPon: " + old_it.next().getIdStPon().toString());
        }

        System.out.println("++++++++++++ roll back ++++++++++++");
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        } else {
            entityManager.getTransaction().rollback();
            entityManager.getTransaction().begin();
        }
        old_it = oldStavke.iterator();
        while (old_it.hasNext()) {
            persistence.Stavkaponuda old_StPon = old_it.next();
            if (new_data.contains(old_StPon)) {
                System.out.println("Nova stavka sadrži stari element:" + old_StPon.getIdStPon().toString());
            } else {
                System.out.println("Nova stavka NE sadrži element:" + old_StPon.getIdStPon().toString() + "   + treba ga dodati");
                Integer idRac = old_StPon.getIdRacuna();
                Integer idPon = old_StPon.getIdPonuda();
                Integer old_idStPon = old_StPon.getIdStPon();
                if (idPon == null) { //stavka koju treba dodati nije iz ponude
                    persistence.Stavkaponuda StPon = new persistence.Stavkaponuda();
                    StPon = old_StPon;
                    StPon.setIdStPon(null);
                    entityManager.persist(StPon);
                } else { //stavka je iz ponude, treba joj samo pridruzit idRacuna
                    persistence.Stavkaponuda StPon = new persistence.Stavkaponuda();
                    StPon = entityManager.find(persistence.Stavkaponuda.class, old_idStPon);
                    StPon.setIdRacuna(idRacuna);
                    entityManager.persist(StPon);
                }
            }
        }
        entityManager.getTransaction().commit();

        System.out.println("------------ roll back -------------");
        entityManager.getTransaction().begin();
        Iterator<persistence.Stavkaponuda> new_it;
        new_it = listStavka.iterator();
        while (new_it.hasNext()) {
            persistence.Stavkaponuda new_StPon = new_it.next();
            if (oldStavke.contains(new_StPon)) {
                System.out.println("Stara stavka sadrži novi element :" + new_StPon.getIdStPon().toString());
            } else {
                System.out.println("Stara stavke NE sadrže novi element:" + new_StPon.getIdStPon().toString() + "    - treba ga uklonit");
                Integer idRac = new_StPon.getIdRacuna();
                Integer idPon = new_StPon.getIdPonuda();
                Integer new_idStPon = new_StPon.getIdStPon();
                if (idPon == null) { //stavka koju treba obrisati nije iz ponude
                    persistence.Stavkaponuda StPon;// = new persistence.Stavkaponuda();
                    StPon = entityManager.find(persistence.Stavkaponuda.class, new_idStPon);
                    //entityManager.remove(new_StPon);
                    entityManager.remove(StPon);
                } else { //stavka je iz ponude, treba joj samo maknuti idRacuna
                    persistence.Stavkaponuda StPon;// = new persistence.Stavkaponuda();
                    StPon = entityManager.find(persistence.Stavkaponuda.class, new_idStPon);
                    StPon.setIdRacuna(null);
                    entityManager.persist(StPon);
                }
            }
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
        listStavka = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(query.getResultList());
        listStPonude = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(query.getResultList());
        jLabel1 = new javax.swing.JLabel();
        lblIdFakture = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblStavka = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        lblUkupnaCijena = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblPDV = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblUkupanIznos = new javax.swing.JLabel();
        panelStavkeFakture = new javax.swing.JTabbedPane();
        panelStavke = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        btnNovaStavka = new javax.swing.JButton();
        btnOdustaniStavka = new javax.swing.JButton();
        btnPrihvatiStavku = new javax.swing.JButton();
        btnObrisiStavku = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtKolicina = new javax.swing.JTextField();
        txtDimenz = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtJedCijena = new javax.swing.JTextField();
        txtOpis = new javax.swing.JTextField();
        panelStavkePonude = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblStPonude = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        tbTekstFakture = new javax.swing.JToggleButton();
        tbNoveStavke = new javax.swing.JToggleButton();
        panelTextFakture = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtHeader = new javax.swing.JTextArea();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtFooter = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel31 = new javax.swing.JLabel();
        lblDatum = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtMjestoIzdavanja = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        cbPopisPartnera = new javax.swing.JComboBox();

        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel1.setText("Račun:");

        lblIdFakture.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblIdFakture.setText("00");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Stavke fakture"));

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
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane2.setViewportView(tblStavka);

        jPanel3.setLayout(new java.awt.GridLayout(3, 2, 3, 1));

        jLabel5.setText("Iznos");
        jPanel3.add(jLabel5);

        lblUkupnaCijena.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblUkupnaCijena.setText("__________");
        jPanel3.add(lblUkupnaCijena);

        jLabel6.setText("PDV 22%");
        jPanel3.add(jLabel6);

        lblPDV.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblPDV.setText("__________");
        jPanel3.add(lblPDV);

        jLabel7.setText("Ukupan iznos");
        jPanel3.add(jLabel7);

        lblUkupanIznos.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblUkupanIznos.setText("__________");
        jPanel3.add(lblUkupanIznos);

        panelStavkeFakture.setDoubleBuffered(true);
        panelStavkeFakture.setMinimumSize(new java.awt.Dimension(70, 40));

        jLabel8.setText("Jedinična cijena:");

        jPanel6.setLayout(new java.awt.GridLayout(2, 2, 1, 1));

        btnNovaStavka.setText("Nova stavka");
        btnNovaStavka.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovaStavkaActionPerformed(evt);
            }
        });
        jPanel6.add(btnNovaStavka);

        btnOdustaniStavka.setText("Odustani");
        btnOdustaniStavka.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOdustaniStavkaActionPerformed(evt);
            }
        });
        jPanel6.add(btnOdustaniStavka);

        btnPrihvatiStavku.setText("Prihvati");
        btnPrihvatiStavku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrihvatiStavkuActionPerformed(evt);
            }
        });
        jPanel6.add(btnPrihvatiStavku);

        btnObrisiStavku.setText("Obriši");
        btnObrisiStavku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnObrisiStavkuActionPerformed(evt);
            }
        });
        jPanel6.add(btnObrisiStavku);

        jLabel11.setText("Dimenzije:");

        jLabel12.setText("Opis:");

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblStavka, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.kolicina}"), txtKolicina, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblStavka, org.jdesktop.beansbinding.ELProperty.create("${selectedElement!=null}"), txtKolicina, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblStavka, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.dimenzije}"), txtDimenz, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblStavka, org.jdesktop.beansbinding.ELProperty.create("${selectedElement!=null}"), txtDimenz, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel13.setText("Komada:");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblStavka, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.jedCijena}"), txtJedCijena, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblStavka, org.jdesktop.beansbinding.ELProperty.create("${selectedElement!=null}"), txtJedCijena, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblStavka, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.opis}"), txtOpis, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblStavka, org.jdesktop.beansbinding.ELProperty.create("${selectedElement!=null}"), txtOpis, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout panelStavkeLayout = new javax.swing.GroupLayout(panelStavke);
        panelStavke.setLayout(panelStavkeLayout);
        panelStavkeLayout.setHorizontalGroup(
            panelStavkeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStavkeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelStavkeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelStavkeLayout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtKolicina, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtJedCijena, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelStavkeLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDimenz, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(152, 152, 152))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelStavkeLayout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtOpis, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71, Short.MAX_VALUE)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelStavkeLayout.setVerticalGroup(
            panelStavkeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStavkeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelStavkeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelStavkeLayout.createSequentialGroup()
                        .addGroup(panelStavkeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(txtOpis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelStavkeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(txtDimenz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelStavkeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtKolicina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(jLabel13)
                            .addComponent(txtJedCijena, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        panelStavkeFakture.addTab("Stavke", panelStavke);

        jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, listStPonude, tblStPonude);
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
        jScrollPane1.setViewportView(tblStPonude);

        jPanel7.setLayout(new java.awt.GridLayout(2, 0, 0, 3));

        jButton4.setText("Dodaj u fakturu");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel7.add(jButton4);

        jButton5.setText("Obriši iz fakture");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel7.add(jButton5);

        javax.swing.GroupLayout panelStavkePonudeLayout = new javax.swing.GroupLayout(panelStavkePonude);
        panelStavkePonude.setLayout(panelStavkePonudeLayout);
        panelStavkePonudeLayout.setHorizontalGroup(
            panelStavkePonudeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelStavkePonudeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelStavkePonudeLayout.setVerticalGroup(
            panelStavkePonudeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStavkePonudeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelStavkePonudeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        panelStavkeFakture.addTab("Stavke iz ponude", panelStavkePonude);

        jPanel5.setLayout(new java.awt.GridLayout(1, 0, 3, 0));

        tbTekstFakture.setText("Tekst fakture");
        tbTekstFakture.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbTekstFaktureActionPerformed(evt);
            }
        });
        jPanel5.add(tbTekstFakture);

        tbNoveStavke.setText("Stavke fakture");
        tbNoveStavke.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbNoveStavkeActionPerformed(evt);
            }
        });
        jPanel5.add(tbNoveStavke);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 176, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(panelStavkeFakture, javax.swing.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(panelStavkeFakture, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        panelStavkeFakture.getAccessibleContext().setAccessibleName("Stavke");

        panelTextFakture.setBorder(javax.swing.BorderFactory.createTitledBorder("Tekst fakture"));

        txtHeader.setColumns(20);
        txtHeader.setFont(new java.awt.Font("Arial", 0, 10));
        txtHeader.setRows(2);
        jScrollPane3.setViewportView(txtHeader);

        jLabel9.setText("Tekst prije stavki ponude");

        jLabel10.setText("Tekst poslje stavki ponude");

        txtFooter.setColumns(20);
        txtFooter.setFont(new java.awt.Font("Arial", 0, 10));
        txtFooter.setRows(2);
        jScrollPane4.setViewportView(txtFooter);

        javax.swing.GroupLayout panelTextFaktureLayout = new javax.swing.GroupLayout(panelTextFakture);
        panelTextFakture.setLayout(panelTextFaktureLayout);
        panelTextFaktureLayout.setHorizontalGroup(
            panelTextFaktureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTextFaktureLayout.createSequentialGroup()
                .addComponent(jLabel9)
                .addContainerGap(450, Short.MAX_VALUE))
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
            .addGroup(panelTextFaktureLayout.createSequentialGroup()
                .addComponent(jLabel10)
                .addContainerGap())
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
        );
        panelTextFaktureLayout.setVerticalGroup(
            panelTextFaktureLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTextFaktureLayout.createSequentialGroup()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5))
        );

        jPanel4.setLayout(new java.awt.GridLayout(1, 0, 3, 0));

        jButton3.setText("Pregled");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton3);

        jButton2.setText("Fakturiraj");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton2);

        jButton1.setText("Odustani");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton1);

        jLabel31.setText("Datum:");

        lblDatum.setText("dd.mm.yyyy.");

        jLabel2.setText("Mjesto:");

        jLabel3.setText("Partner:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblIdFakture)
                .addGap(34, 34, 34)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbPopisPartnera, 0, 128, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel31)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblDatum)
                .addGap(26, 26, 26)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtMjestoIzdavanja, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(323, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(panelTextFakture, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblIdFakture)
                    .addComponent(txtMjestoIzdavanja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(cbPopisPartnera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(lblDatum)
                    .addComponent(jLabel31))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelTextFakture, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        acceptFaktura();
        this.setVisible(false);
        this.removeAll();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (novaFaktura == true) {
            deleteFaktura();
        } else {
            rollBackFaktura();
        }

        this.setVisible(false);
        this.removeAll();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        acceptFaktura();
        //pregled PDF-a
        frmPdfRead newReport = new frmPdfRead(reportFile);
        HashMap param = new HashMap();
        param.put("idRacuna", idRacuna.toString());
        newReport.setParametri(param);
        newReport.setParentJPanel(this);
        MainWindow.openJPanel(newReport, false);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        System.out.println("frmNovaFaktura shown...");
        MainWindow.openJPanel(this, false);
    }//GEN-LAST:event_formFocusGained

    private void btnNovaStavkaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovaStavkaActionPerformed
        novaStavka();
}//GEN-LAST:event_btnNovaStavkaActionPerformed

    private void btnOdustaniStavkaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOdustaniStavkaActionPerformed
        odustaniStavka();
}//GEN-LAST:event_btnOdustaniStavkaActionPerformed

    private void btnPrihvatiStavkuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrihvatiStavkuActionPerformed
        prihvatiStavku();
}//GEN-LAST:event_btnPrihvatiStavkuActionPerformed

    private void btnObrisiStavkuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnObrisiStavkuActionPerformed
        obirisStavku();
}//GEN-LAST:event_btnObrisiStavkuActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        obrisiStFakture();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void tbNoveStavkeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbNoveStavkeActionPerformed
        if (tbNoveStavke.isSelected()) {
            panelStavkeFakture.setVisible(true);
        } else {
            panelStavkeFakture.setVisible(false);
        }
        this.updateUI();
}//GEN-LAST:event_tbNoveStavkeActionPerformed

    private void tbTekstFaktureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbTekstFaktureActionPerformed
        if (tbTekstFakture.isSelected()) {
            panelTextFakture.setVisible(true);
        } else {
            panelTextFakture.setVisible(false);
        }
        this.updateUI();
    }//GEN-LAST:event_tbTekstFaktureActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        dodajStFakture();
    }//GEN-LAST:event_jButton4ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnNovaStavka;
    private javax.swing.JButton btnObrisiStavku;
    private javax.swing.JButton btnOdustaniStavka;
    private javax.swing.JButton btnPrihvatiStavku;
    private javax.swing.JComboBox cbPopisPartnera;
    private javax.persistence.EntityManager entityManager;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblDatum;
    private javax.swing.JLabel lblIdFakture;
    private javax.swing.JLabel lblPDV;
    private javax.swing.JLabel lblUkupanIznos;
    private javax.swing.JLabel lblUkupnaCijena;
    private java.util.List<persistence.Stavkaponuda> listStPonude;
    private java.util.List<persistence.Stavkaponuda> listStavka;
    private javax.swing.JPanel panelStavke;
    private javax.swing.JTabbedPane panelStavkeFakture;
    private javax.swing.JPanel panelStavkePonude;
    private javax.swing.JPanel panelTextFakture;
    private javax.persistence.Query query;
    private javax.swing.JToggleButton tbNoveStavke;
    private javax.swing.JToggleButton tbTekstFakture;
    private javax.swing.JTable tblStPonude;
    private javax.swing.JTable tblStavka;
    private javax.swing.JTextField txtDimenz;
    private javax.swing.JTextArea txtFooter;
    private javax.swing.JTextArea txtHeader;
    private javax.swing.JTextField txtJedCijena;
    private javax.swing.JTextField txtKolicina;
    private javax.swing.JTextField txtMjestoIzdavanja;
    private javax.swing.JTextField txtOpis;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
