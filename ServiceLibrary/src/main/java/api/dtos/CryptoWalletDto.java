package api.dtos;

import java.io.Serializable;
import java.math.BigDecimal;

public class CryptoWalletDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String email;
    private BigDecimal btc;
    private BigDecimal eth;
    private BigDecimal xrp;
    private BigDecimal ltc;

    public CryptoWalletDto() {}

    public CryptoWalletDto(String email, BigDecimal btc, BigDecimal eth, BigDecimal xrp, BigDecimal ltc) {
        this.email = email;
        this.btc = btc;
        this.eth = eth;
        this.xrp = xrp;
        this.ltc = ltc;
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