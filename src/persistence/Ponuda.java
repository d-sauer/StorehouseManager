/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package persistence;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author sheky
 */
@Entity
@Table(name = "ponuda")
@NamedQueries({@NamedQuery(name = "Ponuda.findAll", query = "SELECT p FROM Ponuda p"), @NamedQuery(name = "Ponuda.findByIdPonuda", query = "SELECT p FROM Ponuda p WHERE p.idPonuda = :idPonuda"), @NamedQuery(name = "Ponuda.findByIdPartner", query = "SELECT p FROM Ponuda p WHERE p.idPartner = :idPartner"), @NamedQuery(name = "Ponuda.findByDatumKreiranja", query = "SELECT p FROM Ponuda p WHERE p.datumKreiranja = :datumKreiranja"), @NamedQuery(name = "Ponuda.findByMjestoKreiranja", query = "SELECT p FROM Ponuda p WHERE p.mjestoKreiranja = :mjestoKreiranja"), @NamedQuery(name = "Ponuda.findByStat", query = "SELECT p FROM Ponuda p WHERE p.stat = :stat")})
public class Ponuda implements Serializable {
    //---rucno dodano: kako bi se prilikom promjene podataka u DataSet-u mogli prikazivati i promjene u jTable
    //      potrebno je i doraditi set metode.. tako da trigeraju changeSupport
    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    //---
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idPonuda")
    private Integer idPonuda;
    @Basic(optional = false)
    @Column(name = "idPartner")
    private int idPartner;
    @Column(name = "datumKreiranja")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datumKreiranja;
    @Column(name = "mjestoKreiranja")
    private String mjestoKreiranja;
    @Lob
    @Column(name = "opisHeader")
    private String opisHeader;
    @Lob
    @Column(name = "opisFooter")
    private String opisFooter;
    @Column(name = "stat")
    private Integer stat;
    
    //@OneToMany(cascade = CascadeType.ALL, mappedBy = "idPonuda")
    //private Collection<Stavkaponuda> stavkaPonudaCollection;

    //--dodano: listener koji osluskuje dali je doslo do promjene
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
    //--------

    public Ponuda() {
    }

    public Ponuda(Integer idPonuda) {
        this.idPonuda = idPonuda;
    }

    public Ponuda(Integer idPonuda, int idPartner) {
        this.idPonuda = idPonuda;
        this.idPartner = idPartner;
    }

    public Integer getIdPonuda() {
        return idPonuda;
    }

    public void setIdPonuda(Integer idPonuda) {
        this.idPonuda = idPonuda;
    }

    public int getIdPartner() {
        return idPartner;
    }

    public void setIdPartner(int idPartner) {
        this.idPartner = idPartner;
    }

    public Date getDatumKreiranja() {
        return datumKreiranja;
    }

    public void setDatumKreiranja(Date datumKreiranja) {
        this.datumKreiranja = datumKreiranja;
    }

    public String getMjestoKreiranja() {
        return mjestoKreiranja;
    }

    public void setMjestoKreiranja(String mjestoKreiranja) {
        this.mjestoKreiranja = mjestoKreiranja;
    }

    public String getOpisHeader() {
        return opisHeader;
    }

    public void setOpisHeader(String opisHeader) {
        this.opisHeader = opisHeader;
    }

    public String getOpisFooter() {
        return opisFooter;
    }

    public void setOpisFooter(String opisFooter) {
        this.opisFooter = opisFooter;
    }

    public Integer getStat() {
        return stat;
    }

    public void setStat(Integer stat) {
        Integer old = this.stat;
        this.stat = stat;
        changeSupport.firePropertyChange("stat", old, stat); //dodano
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPonuda != null ? idPonuda.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ponuda)) {
            return false;
        }
        Ponuda other = (Ponuda) object;
        if ((this.idPonuda == null && other.idPonuda != null) || (this.idPonuda != null && !this.idPonuda.equals(other.idPonuda))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "persistence.Ponuda[idPonuda=" + idPonuda + "]";
    }

}
