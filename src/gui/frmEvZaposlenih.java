/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * frmEvZaposlenih.java
 *
 * Created on 11.03.2009., 17:48:40
 */
package gui;

import java.util.Collection;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.beansbinding.AbstractBindingListener;
import org.jdesktop.beansbinding.PropertyStateEvent;
import persistence.Privilegije;

/**
 *
 * @author sheky
 */
public class frmEvZaposlenih extends javax.swing.JPanel {

    public boolean isNewZap = false;
    public String reportFile = "report/PopisZap.jasper"; //.jrxml";
    public static JPanel newPanel = null;
    private int idPristup;

    /** Creates new form frmEvZaposlenih */
    public frmEvZaposlenih() {
        initComponents();

        setSaveBtn(false);
        setObrisiBtn(false);
        fillPristup();

        tblZaposlenici.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

                    public void valueChanged(ListSelectionEvent e) {
                        setObrisiBtn(true);
                        setSelPristupId();
                    }
                });

        bindingGroup.addBindingListener(
                new AbstractBindingListener() {

                    @Override
                    public void targetChanged(org.jdesktop.beansbinding.Binding binding, PropertyStateEvent event) {
                        //super.targetChanged(binding, event);
                        setSaveBtn(true);
                    }
                });



        //pocetak transakcije
        entityManager.getTransaction().begin();
    }

    private void fillPristup() {
        String sql = "SELECT p FROM Privilegije p";
        Query q = entityManager.createQuery(sql);
        Collection data = q.getResultList();
        persistence.Privilegije pri;
        for (Object o : data) {
            pri = (Privilegije) o;
            cbPristup.addItem(makeCBObject(pri.getNaziv(), (int) pri.getIdPristup()));
        }
    }

    private int getSelPristupId() {
        Object o = cbPristup.getSelectedItem();
        return o.hashCode();
    }

    private void setSelPristupId() {
        int selRow = tblZaposlenici.getSelectedRow();
        if (selRow != -1) {
            int idPriv = (int) listZap.get(selRow).getIdPristup();
            int len = cbPristup.getItemCount();
            Object o;
            for (int i = 0; i < len; i++) {
                o = cbPristup.getItemAt(i);
                if (idPriv == o.hashCode()) {
                    cbPristup.setSelectedIndex(i);
                    break;
                }
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

    private void setSaveBtn(boolean var) {
        btnSnimi.setEnabled(var);
        btnOdustani.setEnabled(var);
    }

    private void setObrisiBtn(boolean var) {
        btnObrisi.setEnabled(var);
    }

    /*    public void showReport() {
    try {
    Class.forName("com.mysql.jdbc.Driver");
    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/purgarDB", "root", "");

    java.io.InputStream is = this.getClass().getResourceAsStream("../report/PopisZap.jrxml");
    JasperDesign jasperDesign = JRXmlLoader.load(is);
    JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap(), conn);




    JasperViewer.viewReport(jasperPrint, false);

    } catch (Exception ex) {
    String connectMsg = "Nemoguce kreirat report: " + ex.getMessage() + " " + ex.getLocalizedMessage();
    System.out.println(connectMsg);
    ex.printStackTrace();
    }
    }
     */
    public void newZaposlnik() {
        persistence.Zaposlenici zap = new persistence.Zaposlenici();
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        }
        entityManager.persist(zap);
        listZap.add(zap);

        int row = listZap.size() - 1;
        tblZaposlenici.setRowSelectionInterval(row, row);
        tblZaposlenici.scrollRectToVisible(tblZaposlenici.getCellRect(row, 0, true));

        setSaveBtn(true);
        isNewZap = true;
    }

    public void delZaposlenik() {
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        }
        int selRow = tblZaposlenici.getSelectedRow();
        int idZap = listZap.get(selRow).getIdZap();
        persistence.Zaposlenici zap = entityManager.find(persistence.Zaposlenici.class, idZap);
        entityManager.remove(zap);
        entityManager.getTransaction().commit();
        listZap.remove(zap);
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        }
    }

    public void saveZaposlenik() {
        if (txtKorIme.getText().isEmpty() || txtLozinka.getText().isEmpty() || txtImeField.getText().isEmpty() || jTextField2.getText().isEmpty() || jTextField3.getText().isEmpty() || jTextField4.getText().isEmpty() || jTextField5.getText().isEmpty() || jTextField6.getText().isEmpty() || jTextField7.getText().isEmpty() || jTextField8.getText().isEmpty()) {
            String[] btn = {"Uredu"};
            JOptionPane.showOptionDialog(null, "Niste unjeli sve podatke o zaposleniku!", "Zaposlenik", 0, JOptionPane.INFORMATION_MESSAGE, null, btn, 0);
            return;
        }

        try {
            int selRow = tblZaposlenici.getSelectedRow();
            listZap.get(selRow).setIdPristup(getSelPristupId());

            entityManager.getTransaction().commit();

            if (isNewZap == true) {
                refreshTblZap();
                isNewZap = false;
                String[] btn = {"Uredu"};
                JOptionPane.showOptionDialog(null, "Podaci o zaposleniku su uspješno pohranjeni!", "Zaposlenik", 0, JOptionPane.INFORMATION_MESSAGE, null, btn, 0);
            } else {
                entityManager.getTransaction().begin();
            }

            setSaveBtn(false);
        } catch (RollbackException rex) {
            rex.printStackTrace();
            msgNotSaved("Došlo je do pogreške! Podaci o zaposleniku nisu pohranjeni!", "Zaposlenik");
            entityManager.getTransaction().begin();
            Collection data = queryZap.getResultList();
            for (Object entity : data) {
                entityManager.refresh(entity);
            }
            listZap.clear();
            listZap.addAll(data);
        }
    }

    public void refreshTblZap() {
        int selRow = tblZaposlenici.getSelectedRow();

        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        }
        Collection data = queryZap.getResultList();
        for (Object entity : data) {
            entityManager.refresh(entity);
        }
        listZap.clear();
        listZap.addAll(data);

        tblZaposlenici.updateUI();
        if (isNewZap != true) {
            tblZaposlenici.setRowSelectionInterval(selRow, selRow);
        }

    }

    public void odustaniProm() {
        if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().rollback();
        }

        refreshTblZap();

        setSaveBtn(false);
        if (isNewZap == true) {
            isNewZap = false;
        }
    }

    public void msgNotSaved(String poruka, String naslov) {
        String[] button = {"Uredu"};
        int resp = JOptionPane.showOptionDialog(null, poruka, naslov, 0, JOptionPane.ERROR_MESSAGE, null, button, 0);
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
        queryZap = java.beans.Beans.isDesignTime() ? null : entityManager.createQuery("Select z FROM Zaposlenici z ORDER BY z.prezime");
        listZap = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(queryZap.getResultList());
        popisPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblZaposlenici = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtImeField = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        btnNoviZap = new javax.swing.JButton();
        btnSnimi = new javax.swing.JButton();
        btnOdustani = new javax.swing.JButton();
        btnObrisi = new javax.swing.JButton();
        btnIspis = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        txtKorIme = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtLozinka = new javax.swing.JPasswordField();
        jLabel11 = new javax.swing.JLabel();
        cbPristup = new javax.swing.JComboBox();

        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });

        popisPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Popis zaposlenika"));
        popisPanel.setMinimumSize(new java.awt.Dimension(50, 50));
        popisPanel.setPreferredSize(new java.awt.Dimension(400, 200));

        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, listZap, tblZaposlenici);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${matBroj}"));
        columnBinding.setColumnName("Mat Broj");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${ime}"));
        columnBinding.setColumnName("Ime");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${prezime}"));
        columnBinding.setColumnName("Prezime");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${telefon}"));
        columnBinding.setColumnName("Telefon");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${mjesto}"));
        columnBinding.setColumnName("Mjesto");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${adresa}"));
        columnBinding.setColumnName("Adresa");
        columnBinding.setColumnClass(String.class);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${ziroRacun}"));
        columnBinding.setColumnName("Ziro Racun");
        columnBinding.setColumnClass(String.class);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        jScrollPane1.setViewportView(tblZaposlenici);

        javax.swing.GroupLayout popisPanelLayout = new javax.swing.GroupLayout(popisPanel);
        popisPanel.setLayout(popisPanelLayout);
        popisPanelLayout.setHorizontalGroup(
            popisPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(popisPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
                .addContainerGap())
        );
        popisPanelLayout.setVerticalGroup(
            popisPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(popisPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Detalji o zaposleniku"));
        jPanel1.setMinimumSize(new java.awt.Dimension(50, 10050));

        jLabel1.setText("Ime:");

        jLabel2.setText("Prezime:");

        jLabel3.setText("JMBG:");

        jLabel4.setText("Bankovni račun:");

        jLabel5.setText("Telefon:");

        jLabel6.setText("Poštanski broj:");

        jLabel7.setText("Mjesto:");

        jLabel8.setText("Adresa:");

        txtImeField.setName("txtImeField"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblZaposlenici, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.ime}"), txtImeField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblZaposlenici, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), txtImeField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblZaposlenici, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.prezime}"), jTextField2, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblZaposlenici, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), jTextField2, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblZaposlenici, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.matBroj}"), jTextField3, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblZaposlenici, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), jTextField3, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblZaposlenici, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.ziroRacun}"), jTextField4, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblZaposlenici, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), jTextField4, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblZaposlenici, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.telefon}"), jTextField5, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblZaposlenici, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), jTextField5, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblZaposlenici, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.postBroj}"), jTextField6, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblZaposlenici, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), jTextField6, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblZaposlenici, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.mjesto}"), jTextField7, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblZaposlenici, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), jTextField7, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblZaposlenici, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.adresa}"), jTextField8, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblZaposlenici, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), jTextField8, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jPanel2.setLayout(new java.awt.GridLayout(5, 0, 0, 5));

        btnNoviZap.setText("Dodaj novog zaposlenika");
        btnNoviZap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNoviZapActionPerformed(evt);
            }
        });
        jPanel2.add(btnNoviZap);

        btnSnimi.setText("Prihvati promjenu");
        btnSnimi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSnimiActionPerformed(evt);
            }
        });
        jPanel2.add(btnSnimi);

        btnOdustani.setText("Odustani");
        btnOdustani.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOdustaniActionPerformed(evt);
            }
        });
        jPanel2.add(btnOdustani);

        btnObrisi.setText("Obriši");
        btnObrisi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnObrisiActionPerformed(evt);
            }
        });
        jPanel2.add(btnObrisi);

        btnIspis.setText("Ispis zaposlenika");
        btnIspis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIspisActionPerformed(evt);
            }
        });
        jPanel2.add(btnIspis);

        jLabel9.setText("Korisničko ime:");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblZaposlenici, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.korIme}"), txtKorIme, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblZaposlenici, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), txtKorIme, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel10.setText("Lozinka:");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblZaposlenici, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.lozinka}"), txtLozinka, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblZaposlenici, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), txtLozinka, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel11.setText("Pravo pristupa:");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tblZaposlenici, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), cbPristup, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        cbPristup.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                cbPristupMouseReleased(evt);
            }
        });
        cbPristup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbPristupActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel4)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jTextField4))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel3)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtImeField, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(16, 16, 16)
                                    .addComponent(jLabel2)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jTextField2)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel7))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jTextField7, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jTextField5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jLabel6)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtKorIme, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtLozinka, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbPristup, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 59, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtKorIme, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(txtLozinka, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(cbPristup, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(jLabel1)
                    .addComponent(txtImeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(104, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(popisPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(popisPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSnimiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSnimiActionPerformed
        saveZaposlenik();
    }//GEN-LAST:event_btnSnimiActionPerformed

    private void btnOdustaniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOdustaniActionPerformed
        odustaniProm();
    }//GEN-LAST:event_btnOdustaniActionPerformed

    private void btnNoviZapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNoviZapActionPerformed
        newZaposlnik();
    }//GEN-LAST:event_btnNoviZapActionPerformed

    private void btnObrisiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnObrisiActionPerformed
        String[] button = {"Da", "Ne"};
        String poruka = "Dali sigurno želite obrisati podatke o zaposleniku?";
        String naslov = "Zaposlenik";
        int resp = JOptionPane.showOptionDialog(null, poruka, naslov, 0, JOptionPane.QUESTION_MESSAGE, null, button, 0);
        if (resp == 0) {
            delZaposlenik();

            String[] btn = {"Uredu"};
            JOptionPane.showOptionDialog(null, "Podaci o zaposleniku su obrisani!", "Zaposlenik", 0, JOptionPane.INFORMATION_MESSAGE, null, btn, 0);
        }
    }//GEN-LAST:event_btnObrisiActionPerformed

    private void btnIspisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIspisActionPerformed
        frmPdfRead newReader = new frmPdfRead(reportFile);
        newReader.setParentJPanel(this);
        MainWindow.openJPanel(newReader, false);
        System.out.println(entityManager.getDelegate().toString());
    }//GEN-LAST:event_btnIspisActionPerformed

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        System.out.println("frmEvZaposlenih shown...");
        MainWindow.openJPanel(this, false);
    }//GEN-LAST:event_formFocusGained

    private void cbPristupMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cbPristupMouseReleased
    }//GEN-LAST:event_cbPristupMouseReleased

    private void cbPristupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbPristupActionPerformed
        if (tblZaposlenici.getSelectedRow() != -1) {
            int old = listZap.get(tblZaposlenici.getSelectedRow()).getIdPristup();
            if (old != getSelPristupId()) {
                //idPristup=getSelPristupId();
                setSaveBtn(true);
            }
        }
    }//GEN-LAST:event_cbPristupActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnIspis;
    private javax.swing.JButton btnNoviZap;
    private javax.swing.JButton btnObrisi;
    private javax.swing.JButton btnOdustani;
    private javax.swing.JButton btnSnimi;
    private javax.swing.JComboBox cbPristup;
    private javax.persistence.EntityManager entityManager;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private java.util.List<persistence.Zaposlenici> listZap;
    private javax.swing.JPanel popisPanel;
    private javax.persistence.Query queryZap;
    private javax.swing.JTable tblZaposlenici;
    private javax.swing.JTextField txtImeField;
    private javax.swing.JTextField txtKorIme;
    private javax.swing.JPasswordField txtLozinka;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
