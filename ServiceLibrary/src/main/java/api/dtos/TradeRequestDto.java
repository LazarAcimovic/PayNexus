package api.dtos;


import java.math.BigDecimal;

public class TradeRequestDto {

    private String from;
    private String to;
    private BigDecimal quantity;
    private String userEmail;

    public TradeRequestDto() {
    }

    public TradeRequestDto(String from, String to, BigDecimal quantity, String userEmail) {
        this.from = from;
        this.to = to;
        this.quantity = quantity;
        this.userEmail = userEmail;
    }

    // Getteri i setteri
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

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}