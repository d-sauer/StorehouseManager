/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package persistence;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author sheky
 */
@Embeddable
public class StavkaotpremnicePK implements Serializable {
    @Basic(optional = false)
    @Column(name = "idOtp")
    private int idOtp;
    @Basic(optional = false)
    @Column(name = "idStPon")
    private int idStPon;

    public StavkaotpremnicePK() {
    }

    public StavkaotpremnicePK(int idStPon,int idOtp) {
        this.idOtp = idOtp;
        this.idStPon = idStPon;
    }

    public int getIdOtp() {
        return idOtp;
    }

    public void setIdOtp(int idOtp) {
        this.idOtp = idOtp;
    }

    public int getIdStPon() {
        return idStPon;
    }

    public void setIdStPon(int idStPon) {
        this.idStPon = idStPon;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idStPon;
        hash += (int) idOtp;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StavkaotpremnicePK)) {
            return false;
        }
        StavkaotpremnicePK other = (StavkaotpremnicePK) object;
        if (this.idStPon != other.idStPon) {
            return false;
        }
        if (this.idOtp != other.idOtp) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "persistence.StavkaotpremnicePK[IdStPon=" + idStPon + ", idOtp=" + idOtp + "]";
    }

}
