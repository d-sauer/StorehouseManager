/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package persistence;

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

/**
 *
 * @author sheky
 */
@Entity
@Table(name = "racun")
@NamedQueries({@NamedQuery(name = "Racun.findAll", query = "SELECT r FROM Racun r"), @NamedQuery(name = "Racun.findByIdRacuna", query = "SELECT r FROM Racun r WHERE r.idRacuna = :idRacuna"), @NamedQuery(name = "Racun.findByidPartner", query = "SELECT r FROM Racun r WHERE r.idPartner = :idPartner"), @NamedQuery(name = "Racun.findByDatumIzdavanja", query = "SELECT r FROM Racun r WHERE r.datumIzdavanja = :datumIzdavanja"), @NamedQuery(name = "Racun.findByMjestoIzdavanja", query = "SELECT r FROM Racun r WHERE r.mjestoIzdavanja = :mjestoIzdavanja"), @NamedQuery(name = "Racun.findByIznos", query = "SELECT r FROM Racun r WHERE r.iznos = :iznos")})
public class Racun implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idRacuna")
    private Integer idRacuna;
    @Basic(optional = false)
    @Column(name = "idPartner")
    private int idPartner;
    @Column(name = "datumIzdavanja")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datumIzdavanja;
    @Column(name = "mjestoIzdavanja")
    private String mjestoIzdavanja;
    @Lob
    @Column(name = "opisHeader")
    private String opisHeader;
    @Lob
    @Column(name = "opisFooter")
    private String opisFooter;
    @Column(name = "iznos")
    private Float iznos;

    public Racun() {
    }

    public Racun(Integer idRacuna) {
        this.idRacuna = idRacuna;
    }

    public Racun(Integer idRacuna, int idPartner) {
        this.idRacuna = idRacuna;
        this.idPartner = idPartner;
    }

    public Integer getIdRacuna() {
        return idRacuna;
    }

    public void setIdRacuna(Integer idRacuna) {
        this.idRacuna = idRacuna;
    }

    public int getIdPartner() {
        return idPartner;
    }

    public void setIdPartner(int idPartner) {
        this.idPartner = idPartner;
    }

    public Date getDatumIzdavanja() {
        return datumIzdavanja;
    }

    public void setDatumIzdavanja(Date datumIzdavanja) {
        this.datumIzdavanja = datumIzdavanja;
    }

    public String getMjestoIzdavanja() {
        return mjestoIzdavanja;
    }

    public void setMjestoIzdavanja(String mjestoIzdavanja) {
        this.mjestoIzdavanja = mjestoIzdavanja;
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

    public Float getIznos() {
        return iznos;
    }

    public void setIznos(Float iznos) {
        this.iznos = iznos;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idRacuna != null ? idRacuna.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Racun)) {
            return false;
        }
        Racun other = (Racun) object;
        if ((this.idRacuna == null && other.idRacuna != null) || (this.idRacuna != null && !this.idRacuna.equals(other.idRacuna))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pu.Racun[idRacuna=" + idRacuna + "]";
    }

}
