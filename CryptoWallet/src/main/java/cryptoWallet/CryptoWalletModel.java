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

    @Column(precision = 19, scale = 8)
    private BigDecimal BTC;

    @Column(precision = 19, scale = 8)
    private BigDecimal ETH;

    @Column(precision = 19, scale = 8)
    private BigDecimal XRP;

    @Column(precision = 19, scale = 8)
    private BigDecimal LTC;

    public CryptoWalletModel() {
        this.BTC = BigDecimal.ZERO;
        this.ETH = BigDecimal.ZERO;
        this.XRP = BigDecimal.ZERO;
        this.LTC = BigDecimal.ZERO;
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

    public BigDecimal getBTC() {
        return BTC;
    }

    public void setBTC(BigDecimal BTC) {
        this.BTC = BTC;
    }

    public BigDecimal getETH() {
        return ETH;
    }

    public void setETH(BigDecimal ETH) {
        this.ETH = ETH;
    }

    public BigDecimal getXRP() {
        return XRP;
    }

    public void setXRP(BigDecimal XRP) {
        this.XRP = XRP;
    }

    public BigDecimal getLTC() {
        return LTC;
    }

    public void setLTC(BigDecimal LTC) {
        this.LTC = LTC;
    }
}
