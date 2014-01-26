/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package persistence;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author sheky
 */
@Entity
@Table(name = "partner")
@NamedQueries({@NamedQuery(name = "Partner.findAll", query = "SELECT p FROM Partner p"), @NamedQuery(name = "Partner.findByIdPartner", query = "SELECT p FROM Partner p WHERE p.idPartner = :idPartner"), @NamedQuery(name = "Partner.findByNaziv", query = "SELECT p FROM Partner p WHERE p.naziv = :naziv"), @NamedQuery(name = "Partner.findByIme", query = "SELECT p FROM Partner p WHERE p.ime = :ime"), @NamedQuery(name = "Partner.findByPrezime", query = "SELECT p FROM Partner p WHERE p.prezime = :prezime"), @NamedQuery(name = "Partner.findByTelefon", query = "SELECT p FROM Partner p WHERE p.telefon = :telefon"), @NamedQuery(name = "Partner.findByMjesto", query = "SELECT p FROM Partner p WHERE p.mjesto = :mjesto"), @NamedQuery(name = "Partner.findByPostBroj", query = "SELECT p FROM Partner p WHERE p.postBroj = :postBroj"), @NamedQuery(name = "Partner.findByAdresa", query = "SELECT p FROM Partner p WHERE p.adresa = :adresa"), @NamedQuery(name = "Partner.findByMaticniBr", query = "SELECT p FROM Partner p WHERE p.maticniBr = :maticniBr"), @NamedQuery(name = "Partner.findByZiroRac", query = "SELECT p FROM Partner p WHERE p.ziroRac = :ziroRac"), @NamedQuery(name = "Partner.findByIdVrsta", query = "SELECT p FROM Partner p WHERE p.idVrsta = :idVrsta")})
public class Partner implements Serializable {
    //---rucno dodano: kako bi se prilikom promjene podataka u DataSet-u mogli prikazivati i promjene u jTable
    //      potrebno je i doraditi set metode.. tako da trigeraju changeSupport
    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    //---
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idPartner")
    private Integer idPartner;
    @Basic(optional = false)
    @Column(name = "idVrsta")
    private int idVrsta;
    @Column(name = "naziv")
    private String naziv;
    @Column(name = "ime")
    private String ime;
    @Column(name = "prezime")
    private String prezime;
    @Column(name = "telefon")
    private String telefon;
    @Column(name = "mjesto")
    private String mjesto;
    @Column(name = "postBroj")
    private Integer postBroj;
    @Column(name = "adresa")
    private String adresa;
    @Column(name = "maticniBr")
    private String maticniBr;
    @Column(name = "ziroRac")
    private String ziroRac;
    

    //--dodano: listener koji osluskuje dali je doslo do promjene
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
    //--------

    public Partner() {
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        String old = this.naziv;
        this.naziv = naziv;
        changeSupport.firePropertyChange("naziv", old, naziv); //dodano
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        String old = this.ime;
        this.ime = ime;
        changeSupport.firePropertyChange("ime", old, ime); //dodano
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        String old = this.prezime;
        this.prezime = prezime;
        changeSupport.firePropertyChange("prezime", old, prezime); //dodano
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        String old = this.telefon;
        this.telefon = telefon;
        changeSupport.firePropertyChange("telefon", old, telefon); //dodano
    }

    public String getMjesto() {
        return mjesto;
    }

    public void setMjesto(String mjesto) {
        String old = this.mjesto;
        this.mjesto = mjesto;
        changeSupport.firePropertyChange("mjesto", old, mjesto); //dodano
    }

    public Integer getPostBroj() {
        return postBroj;
    }

    public void setPostBroj(Integer postBroj) {
        Integer old = this.postBroj;
        this.postBroj = postBroj;
        changeSupport.firePropertyChange("postBroj", old, postBroj); //dodano
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        String old = this.adresa;
        this.adresa = adresa;
        changeSupport.firePropertyChange("adresa", old, adresa); //dodano
    }

    public String getMaticniBr() {
        return maticniBr;
    }

    public void setMaticniBr(String maticniBr) {
        String old = this.maticniBr;
        this.maticniBr = maticniBr;
        changeSupport.firePropertyChange("maticniBr", old, maticniBr); //dodano
    }

    public String getZiroRac() {
        return ziroRac;
    }

    public void setZiroRac(String ziroRac) {
        String old = this.ziroRac;
        this.ziroRac = ziroRac;
        changeSupport.firePropertyChange("ziroRac", old, ziroRac); //dodano
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPartner != null ? idPartner.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Partner)) {
            return false;
        }
        Partner other = (Partner) object;
        if ((this.idPartner == null && other.idPartner != null) || (this.idPartner != null && !this.idPartner.equals(other.idPartner))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "persistence.Partner[idPartner=" + idPartner + "]";
    }

    public Integer getIdPartner() {
        return idPartner;
    }

    public void setIdPartner(Integer idPartner) {
        Integer old = this.idPartner;
        this.idPartner = idPartner;
        changeSupport.firePropertyChange("idPartner", old, idPartner); //dodano
    }

    public Integer getIdVrsta() {
        return idVrsta;
    }

    public void setIdVrsta(Integer idVrsta) {
        Integer old = this.idVrsta;
        this.idVrsta = idVrsta;
        changeSupport.firePropertyChange("idVrsta", old, idVrsta); //dodano
    }

}
