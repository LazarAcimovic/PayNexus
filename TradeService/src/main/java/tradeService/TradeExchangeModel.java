package tradeService;

import java.io.Serializable;
import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "trade_exchange")
public class TradeExchangeModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private int id;

    @Column(name = "currency_from")
    private String from;

    @Column(name = "currency_to")
    private String to;

    @Column(precision = 19, scale = 8)
    private BigDecimal exchange_rate;

    public TradeExchangeModel() {
    }

    public TradeExchangeModel(int id, String from, String to, BigDecimal exchangeRate) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.exchange_rate = exchangeRate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public BigDecimal getExchangeRate() {
        return exchange_rate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchange_rate = exchangeRate;
    }
}