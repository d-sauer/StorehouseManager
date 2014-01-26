/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package persistence;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author sheky
 */
@Entity
@Table(name = "stavkaotpremnice")
@NamedQueries({@NamedQuery(name = "Stavkaotpremnice.findAll", query = "SELECT s FROM Stavkaotpremnice s"), @NamedQuery(name = "Stavkaotpremnice.findByIdOtp", query = "SELECT s FROM Stavkaotpremnice s WHERE s.stavkaotpremnicePK.idOtp = :idOtp"), @NamedQuery(name = "Stavkaotpremnice.findByIdStPon", query = "SELECT s FROM Stavkaotpremnice s WHERE s.stavkaotpremnicePK.idStPon = :idStPon")})
public class Stavkaotpremnice implements Serializable {
   
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected StavkaotpremnicePK stavkaotpremnicePK;



    public Stavkaotpremnice() {
    }

    public Stavkaotpremnice(StavkaotpremnicePK stavkaotpremnicePK) {
        this.stavkaotpremnicePK = stavkaotpremnicePK;
    }

    public Stavkaotpremnice(int IdStPon, int idOtp) {
        this.stavkaotpremnicePK = new StavkaotpremnicePK(IdStPon, idOtp);
    }

    public StavkaotpremnicePK getStavkaotpremnicePK() {
        return stavkaotpremnicePK;
    }

    public void setStavkaotpremnicePK(StavkaotpremnicePK stavkaotpremnicePK) {
       
        this.stavkaotpremnicePK = stavkaotpremnicePK;
       
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (stavkaotpremnicePK != null ? stavkaotpremnicePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Stavkaotpremnice)) {
            return false;
        }
        Stavkaotpremnice other = (Stavkaotpremnice) object;
        if ((this.stavkaotpremnicePK == null && other.stavkaotpremnicePK != null) || (this.stavkaotpremnicePK != null && !this.stavkaotpremnicePK.equals(other.stavkaotpremnicePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "persistence.Stavkaotpremnice[stavkaotpremnicePK=" + stavkaotpremnicePK + "]";
    }

}
