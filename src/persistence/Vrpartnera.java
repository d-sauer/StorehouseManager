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
@Table(name = "vrpartnera")
@NamedQueries({@NamedQuery(name = "Vrpartnera.findAll", query = "SELECT v FROM Vrpartnera v"), @NamedQuery(name = "Vrpartnera.findByIdVrsta", query = "SELECT v FROM Vrpartnera v WHERE v.idVrsta = :idVrsta"), @NamedQuery(name = "Vrpartnera.findByVrsta", query = "SELECT v FROM Vrpartnera v WHERE v.vrsta = :vrsta")})
public class Vrpartnera implements Serializable {
    //---rucno dodano: kako bi se prilikom promjene podataka u DataSet-u mogli prikazivati i promjene u jTable
    //      potrebno je i doraditi set metode.. tako da trigeraju changeSupport
    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    //---
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idVrsta")
    private Integer idVrsta;
    @Column(name = "vrsta")
    private String vrsta;

     //--dodano: listener koji osluskuje dali je doslo do promjene
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
    //--------

    public Vrpartnera() {
    }

    public Vrpartnera(Integer idVrsta) {
        this.idVrsta = idVrsta;
    }

    public Integer getIdVrsta() {
        return idVrsta;
    }

    public void setIdVrsta(Integer idVrsta) {
        Integer old = this.idVrsta; //dodano
        this.idVrsta = idVrsta;
        changeSupport.firePropertyChange("idVrsta", old, idVrsta); //dodano
    }

    public String getVrsta() {
        return vrsta;
    }

    public void setVrsta(String vrsta) {
        String old = this.vrsta; //dodano
        this.vrsta = vrsta;
        changeSupport.firePropertyChange("vrsta", old, vrsta); //dodano
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idVrsta != null ? idVrsta.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Vrpartnera)) {
            return false;
        }
        Vrpartnera other = (Vrpartnera) object;
        if ((this.idVrsta == null && other.idVrsta != null) || (this.idVrsta != null && !this.idVrsta.equals(other.idVrsta))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "persistence.Vrpartnera[idVrsta=" + idVrsta + "]";
    }

}
