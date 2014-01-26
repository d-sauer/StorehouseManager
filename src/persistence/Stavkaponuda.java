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
@Table(name = "stavkaponuda")
@NamedQueries({@NamedQuery(name = "Stavkaponuda.findAll", query = "SELECT s FROM Stavkaponuda s"), @NamedQuery(name = "Stavkaponuda.findByIdStPon", query = "SELECT s FROM Stavkaponuda s WHERE s.idStPon = :idStPon"), @NamedQuery(name = "Stavkaponuda.findByIdPonuda", query = "SELECT s FROM Stavkaponuda s WHERE s.idPonuda = :idPonuda"), @NamedQuery(name = "Stavkaponuda.findByOpis", query = "SELECT s FROM Stavkaponuda s WHERE s.opis = :opis"), @NamedQuery(name = "Stavkaponuda.findByDimenzije", query = "SELECT s FROM Stavkaponuda s WHERE s.dimenzije = :dimenzije"), @NamedQuery(name = "Stavkaponuda.findByKolicina", query = "SELECT s FROM Stavkaponuda s WHERE s.kolicina = :kolicina"), @NamedQuery(name = "Stavkaponuda.findByJedCijena", query = "SELECT s FROM Stavkaponuda s WHERE s.jedCijena = :jedCijena")})
public class Stavkaponuda implements Serializable {
    //---rucno dodano: kako bi se prilikom promjene podataka u DataSet-u mogli prikazivati i promjene u jTable
    //      potrebno je i doraditi set metode.. tako da trigeraju changeSupport
    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    //---
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idStPon")
    private Integer idStPon;
    @Column(name = "idPonuda")
    private Integer idPonuda;
    @Column(name = "idRacuna")
    private Integer idRacuna;
    @Basic(optional = false)
    @Column(name = "opis")
    private String opis;
    @Column(name = "dimenzije")
    private String dimenzije;
    @Basic(optional = false)
    @Column(name = "kolicina")
    private int kolicina;
    @Basic(optional = false)
    @Column(name = "jedCijena")
    private float jedCijena;


    //--dodano: listener koji osluskuje dali je doslo do promjene
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
    //--------


    public Stavkaponuda() {
    }

    public Stavkaponuda(Integer idStPon) {
        this.idStPon = idStPon;
    }

    public Stavkaponuda(Integer idStPon, String opis, int kolicina, float jedCijena) {
        this.idStPon = idStPon;
        this.opis = opis;
        this.kolicina = kolicina;
        this.jedCijena = jedCijena;
    }

    public Integer getIdStPon() {
        return idStPon;
    }

    public void setIdStPon(Integer idStPon) {
        Integer old = this.idStPon;
        this.idStPon = idStPon;
        changeSupport.firePropertyChange("idStPon", old, idStPon); //dodano
    }

    public Integer getIdPonuda() {
        return idPonuda;
    }

    public void setIdPonuda(Integer idPonuda) {
        Integer old = this.idPonuda;
        this.idPonuda = idPonuda;
        changeSupport.firePropertyChange("idPonuda", old, idPonuda); //dodano
    }

    public Integer getIdRacuna() {
        return idRacuna;
    }

    public void setIdRacuna(Integer idRacuna) {
        Integer old = this.idRacuna;
        this.idRacuna = idRacuna;
        changeSupport.firePropertyChange("idRacuna", old, idRacuna); //dodano
    }


    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        String old = this.opis;
        this.opis = opis;
        changeSupport.firePropertyChange("opis", old, opis); //dodano
    }

    public String getDimenzije() {
        return dimenzije;
    }

    public void setDimenzije(String dimenzije) {
        String old = this.dimenzije;
        this.dimenzije = dimenzije;
        changeSupport.firePropertyChange("dimenzije", old, dimenzije); //dodano
    }

    public int getKolicina() {
        return kolicina;
    }

    public void setKolicina(int kolicina) {
        int old = this.kolicina;
        this.kolicina = kolicina;
        changeSupport.firePropertyChange("kolicina", old, kolicina); //dodano
    }

    public float getJedCijena() {
        return jedCijena;
    }

    public void setJedCijena(float jedCijena) {
        float old = this.jedCijena;
        this.jedCijena = jedCijena;
        changeSupport.firePropertyChange("jedCijena", old,jedCijena); //dodano
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idStPon != null ? idStPon.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Stavkaponuda)) {
            return false;
        }
        Stavkaponuda other = (Stavkaponuda) object;
        if ((this.idStPon == null && other.idStPon != null) || (this.idStPon != null && !this.idStPon.equals(other.idStPon))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "persistence.Stavkaponuda[idStPon=" + idStPon + "]";
    }

}
