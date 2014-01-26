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
@Table(name = "otpremnica")
@NamedQueries({@NamedQuery(name = "Otpremnica.findAll", query = "SELECT o FROM Otpremnica o"), @NamedQuery(name = "Otpremnica.findByIdOtp", query = "SELECT o FROM Otpremnica o WHERE o.idOtp = :idOtp"), @NamedQuery(name = "Otpremnica.findByIdRacuna", query = "SELECT o FROM Otpremnica o WHERE o.IdRacuna = :IdRacuna"), @NamedQuery(name = "Otpremnica.findByDatum", query = "SELECT o FROM Otpremnica o WHERE o.datum = :datum"), @NamedQuery(name = "Otpremnica.findByIzdao", query = "SELECT o FROM Otpremnica o WHERE o.izdao = :izdao")})
public class Otpremnica implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idOtp")
    private Integer idOtp;
    @Basic(optional = false)
    @Column(name = "idRacuna")
    private int IdRacuna;
    @Column(name = "datum")
    @Temporal(TemporalType.DATE)
    private Date datum;
    @Column(name = "izdao")
    private Integer izdao;
    @Column(name = "nacin")
    private String nacin;

    public Otpremnica() {
    }

    public Otpremnica(Integer idOtp) {
        this.idOtp = idOtp;
    }

    public Otpremnica(Integer idOtp, int IdRacuna) {
        this.idOtp = idOtp;
        this.IdRacuna = IdRacuna;
    }

    public String getNacin() {
        return nacin;
    }

    public void setNacin(String nacin) {
        this.nacin = nacin;
    }

    public Integer getIdOtp() {
        return idOtp;
    }

    public void setIdOtp(Integer idOtp) {
        this.idOtp = idOtp;
    }

    public int getIdRacuna() {
        return IdRacuna;
    }

    public void setIdRacuna(int IdRacuna) {
        this.IdRacuna = IdRacuna;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public Integer getIzdao() {
        return izdao;
    }

    public void setIzdao(Integer izdao) {
        this.izdao = izdao;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idOtp != null ? idOtp.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Otpremnica)) {
            return false;
        }
        Otpremnica other = (Otpremnica) object;
        if ((this.idOtp == null && other.idOtp != null) || (this.idOtp != null && !this.idOtp.equals(other.idOtp))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "persistence.Otpremnica[idOtp=" + idOtp + "]";
    }

}
