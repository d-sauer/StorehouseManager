/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package persistence;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author sheky
 */
@Entity
@Table(name = "privilegije")
@NamedQueries({@NamedQuery(name = "Privilegije.findAll", query = "SELECT p FROM Privilegije p"), @NamedQuery(name = "Privilegije.findByIdPristup", query = "SELECT p FROM Privilegije p WHERE p.idPristup = :idPristup"), @NamedQuery(name = "Privilegije.findByNaziv", query = "SELECT p FROM Privilegije p WHERE p.naziv = :naziv")})
public class Privilegije implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "idPristup")
    private Integer idPristup;
    @Column(name = "naziv")
    private String naziv;

    public Privilegije() {
    }

    public Privilegije(Integer idPristup) {
        this.idPristup = idPristup;
    }

    public Integer getIdPristup() {
        return idPristup;
    }

    public void setIdPristup(Integer idPristup) {
        this.idPristup = idPristup;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPristup != null ? idPristup.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Privilegije)) {
            return false;
        }
        Privilegije other = (Privilegije) object;
        if ((this.idPristup == null && other.idPristup != null) || (this.idPristup != null && !this.idPristup.equals(other.idPristup))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "persistence.Privilegije[idPristup=" + idPristup + "]";
    }

}
