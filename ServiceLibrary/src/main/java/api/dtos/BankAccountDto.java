package api.dtos;

import java.math.BigDecimal;

public class BankAccountDto {

    private String email;
    private BigDecimal USD;
    private BigDecimal EUR;
    private BigDecimal GBP;
    private BigDecimal CHF;
    private BigDecimal RSD;

    public BankAccountDto() {
        this.USD = BigDecimal.ZERO;
        this.EUR = BigDecimal.ZERO;
        this.GBP = BigDecimal.ZERO;
        this.CHF = BigDecimal.ZERO;
        this.RSD = BigDecimal.ZERO;
    }

    public BankAccountDto(String email) {
        this();
        this.email = email;
    }

    public BankAccountDto(String email, BigDecimal USD, BigDecimal EUR, BigDecimal GBP, BigDecimal CHF, BigDecimal RSD) {
        this.email = email;
        this.USD = USD;
        this.EUR = EUR;
        this.GBP = GBP;
        this.CHF = CHF;
        this.RSD = RSD;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getUsd() {
        return USD;
    }

    public void setUsd(BigDecimal USD) {
        this.USD = USD;
    }

    public BigDecimal getEur() {
        return EUR;
    }

    public void setEur(BigDecimal EUR) {
        this.EUR = EUR;
    }

    public BigDecimal getGbp() {
        return GBP;
    }

    public void setGbp(BigDecimal GBP) {
        this.GBP = GBP;
    }

    public BigDecimal getChf() {
        return CHF;
    }

    public void setChf(BigDecimal CHF) {
        this.CHF = CHF;
    }

    public BigDecimal getRsd() {
        return RSD;
    }

    public void setRsd(BigDecimal RSD) {
        this.RSD = RSD;
    }
}
