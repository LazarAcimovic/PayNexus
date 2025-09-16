package cryptoWallet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "crypto_wallet")
public class CryptoWalletModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column
    private BigDecimal btc;
    
    @Column
    private BigDecimal eth;
    
    @Column
    private BigDecimal xrp;
    
    @Column
    private BigDecimal ltc;
    
    public CryptoWalletModel() {
        this.btc = BigDecimal.ZERO;
        this.eth = BigDecimal.ZERO;
        this.xrp = BigDecimal.ZERO;
        this.ltc = BigDecimal.ZERO;
    }


    public CryptoWalletModel(String email) {
        this();
        this.email = email;
    }
    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getBtc() {
        return btc;
    }

    public void setBtc(BigDecimal btc) {
        this.btc = btc;
    }

    public BigDecimal getEth() {
        return eth;
    }

    public void setEth(BigDecimal eth) {
        this.eth = eth;
    }

    public BigDecimal getXrp() {
        return xrp;
    }

    public void setXrp(BigDecimal xrp) {
        this.xrp = xrp;
    }

    public BigDecimal getLtc() {
        return ltc;
    }

    public void setLtc(BigDecimal ltc) {
        this.ltc = ltc;
    }
}