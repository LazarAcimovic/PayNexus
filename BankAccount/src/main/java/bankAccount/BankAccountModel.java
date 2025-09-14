package bankAccount;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bank_account")
public class BankAccountModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column
    private BigDecimal usd;

    @Column
    private BigDecimal eur;

    @Column
    private BigDecimal gbp;

    @Column
    private BigDecimal chf;

    @Column
    private BigDecimal rsd;
    


    public BankAccountModel() {
        this.usd = BigDecimal.ZERO;
        this.eur = BigDecimal.ZERO;
        this.gbp = BigDecimal.ZERO;
        this.chf = BigDecimal.ZERO;
        this.rsd = BigDecimal.ZERO;
    }

    public BankAccountModel(String email) {
        this();
        this.email = email;
    }
    
    public BankAccountModel( String email, BigDecimal usd, BigDecimal eur, BigDecimal gbp, BigDecimal chf, BigDecimal rsd) {
    	super();
        this.email = email;
        this.usd = usd;
        this.eur = eur;
        this.gbp = gbp;
        this.chf = chf;
        this.rsd = rsd;
    }

    public BankAccountModel(int id, String email, BigDecimal usd, BigDecimal eur, BigDecimal gbp, BigDecimal chf, BigDecimal rsd) {
    	super();
        this.id = id;
        this.email = email;
        this.usd = usd;
        this.eur = eur;
        this.gbp = gbp;
        this.chf = chf;
        this.rsd = rsd;
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

    public BigDecimal getUsd() {
        return usd;
    }

    public void setUsd(BigDecimal usd) {
        this.usd = usd;
    }

    public BigDecimal getEur() {
        return eur;
    }

    public void setEur(BigDecimal eur) {
        this.eur = eur;
    }

    public BigDecimal getGbp() {
        return gbp;
    }

    public void setGbp(BigDecimal gbp) {
        this.gbp = gbp;
    }

    public BigDecimal getChf() {
        return chf;
    }

    public void setChf(BigDecimal chf) {
        this.chf = chf;
    }

    public BigDecimal getRsd() {
        return rsd;
    }

    public void setRsd(BigDecimal rsd) {
        this.rsd = rsd;
    }

    public BigDecimal getBalance(String currency) {
        String currencyUpper = currency.toUpperCase();
        switch (currencyUpper) {
            case "USD":
                return usd;
            case "EUR":
                return eur;
            case "GBP":
                return gbp;
            case "CHF":
                return chf;
            case "RSD":
                return rsd;
            default:
                return null;
        }
    }

    public boolean setBalance(String currency, BigDecimal amount) {
        String currencyUpper = currency.toUpperCase();
        switch (currencyUpper) {
            case "USD":
                this.usd = amount;
                break;
            case "EUR":
                this.eur = amount;
                break;
            case "GBP":
                this.gbp = amount;
                break;
            case "CHF":
                this.chf = amount;
                break;
            case "RSD":
                this.rsd = amount;
                break;
            default:
                return false;
        }
        return true;
    }
}