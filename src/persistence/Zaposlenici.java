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
@Table(name = "zaposlenici")
@NamedQueries({@NamedQuery(name = "Zaposlenici.findAll", query = "SELECT z FROM Zaposlenici z"), @NamedQuery(name = "Zaposlenici.findByIdZap", query = "SELECT z FROM Zaposlenici z WHERE z.idZap = :idZap"), @NamedQuery(name = "Zaposlenici.findByIme", query = "SELECT z FROM Zaposlenici z WHERE z.ime = :ime"), @NamedQuery(name = "Zaposlenici.findByPrezime", query = "SELECT z FROM Zaposlenici z WHERE z.prezime = :prezime"), @NamedQuery(name = "Zaposlenici.findByTelefon", query = "SELECT z FROM Zaposlenici z WHERE z.telefon = :telefon"), @NamedQuery(name = "Zaposlenici.findByMjesto", query = "SELECT z FROM Zaposlenici z WHERE z.mjesto = :mjesto"), @NamedQuery(name = "Zaposlenici.findByPostBroj", query = "SELECT z FROM Zaposlenici z WHERE z.postBroj = :postBroj"), @NamedQuery(name = "Zaposlenici.findByAdresa", query = "SELECT z FROM Zaposlenici z WHERE z.adresa = :adresa"), @NamedQuery(name = "Zaposlenici.findByMatBroj", query = "SELECT z FROM Zaposlenici z WHERE z.matBroj = :matBroj"), @NamedQuery(name = "Zaposlenici.findByZiroRacun", query = "SELECT z FROM Zaposlenici z WHERE z.ziroRacun = :ziroRacun")})
public class Zaposlenici implements Serializable {
    //---rucno dodano: kako bi se prilikom promjene podataka u DataSet-u mogli prikazivati i promjene u jTable
    //      potrebno je i doraditi set metode.. tako da trigeraju changeSupport
    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    //---
    private static final long serialVersionUID = 1L;
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idZap")
    private Integer idZap;
    @Basic(optional = false)
    @Column(name = "idPristup")
    private int idPristup;
    @Basic(optional = false)
    @Column(name = "korIme")
    private String korIme;
    @Basic(optional = false)
    @Column(name = "lozinka")
    private String lozinka;
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
    @Column(name = "matBroj")
    private String matBroj;
    @Column(name = "ziroRacun")
    private String ziroRacun;

    //--dodano: listener koji osluskuje dali je doslo do promjene
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
    //--------

    public Zaposlenici() {
    }

    public Zaposlenici(Integer idZap) {
        this.idZap = idZap;
    }

    public Zaposlenici(Integer idZap, int idPristup, String korIme, String lozinka) {
        this.idZap = idZap;
        this.idPristup = idPristup;
        this.korIme = korIme;
        this.lozinka = lozinka;
    }

    public int getIdPristup() {
        return idPristup;
    }

    public void setIdPristup(int idPristup) {
        int old = this.idPristup;
        this.idPristup = idPristup;
        changeSupport.firePropertyChange("idPristup", old, idPristup); //dodano
    }

     public String getKorIme() {
        return korIme;
    }

    public void setKorIme(String korIme) {
        String old = this.korIme;
        this.korIme = korIme;
        changeSupport.firePropertyChange("korIme", old, korIme); //dodano
    }
    
    public String getLozinka() {
        return lozinka;
    }

    public void setLozinka(String lozinka) {
        String old = this.lozinka;
        this.lozinka = lozinka;
        changeSupport.firePropertyChange("lozinka", old, lozinka); //dodano
    }

    public Integer getIdZap() {
        return idZap;
    }

    public void setIdZap(Integer idZap) {
        Integer old = this.idZap;  //dodano
        this.idZap = idZap;
        changeSupport.firePropertyChange("idZap", old, idZap); //dodano
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        String old = this.ime;
        this.ime = ime;
        changeSupport.firePropertyChange("ime", old, ime);
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        String old = this.prezime;
        this.prezime = prezime;
        changeSupport.firePropertyChange("prezime", old, prezime);
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        String old = this.telefon;
        this.telefon = telefon;
        changeSupport.firePropertyChange("telefon", old, telefon);
    }

    public String getMjesto() {
        return mjesto;
    }

    public void setMjesto(String mjesto) {
        String old = this.mjesto;
        this.mjesto = mjesto;
        changeSupport.firePropertyChange("mjesto", old, mjesto);
    }

    public Integer getPostBroj() {
        return postBroj;
    }

    public void setPostBroj(Integer postBroj) {
        Integer old = this.postBroj;
        this.postBroj = postBroj;
        changeSupport.firePropertyChange("postBroj", old, postBroj);
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        String old = this.adresa;
        this.adresa = adresa;
        changeSupport.firePropertyChange("adresa", old, adresa);
    }

    public String getMatBroj() {
        return matBroj;
    }

    public void setMatBroj(String matBroj) {
        String old = this.matBroj;
        this.matBroj = matBroj;
        changeSupport.firePropertyChange("matBroj", old, matBroj);
    }

    public String getZiroRacun() {
        return ziroRacun;
    }

    public void setZiroRacun(String ziroRacun) {
        String old = this.ziroRacun;
        this.ziroRacun = ziroRacun;
        changeSupport.firePropertyChange("ziroRacun", old, ziroRacun);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idZap != null ? idZap.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Zaposlenici)) {
            return false;
        }
        Zaposlenici other = (Zaposlenici) object;
        if ((this.idZap == null && other.idZap != null) || (this.idZap != null && !this.idZap.equals(other.idZap))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "persistence.Zaposlenici[idZap=" + idZap + "]";
    }

}
