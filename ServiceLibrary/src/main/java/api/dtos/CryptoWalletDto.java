package api.dtos;


import java.math.BigDecimal;

public class CryptoWalletDto  {



    private String email;
    private BigDecimal BTC;
    private BigDecimal ETH;
    private BigDecimal XRP;
    private BigDecimal LTC;

    public CryptoWalletDto() {}

    public CryptoWalletDto(String email, BigDecimal BTC, BigDecimal ETH, BigDecimal XRP, BigDecimal LTC) {
        this.email = email;
        this.BTC = BTC;
        this.ETH = ETH;
        this.XRP = XRP;
        this.LTC = LTC;
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
