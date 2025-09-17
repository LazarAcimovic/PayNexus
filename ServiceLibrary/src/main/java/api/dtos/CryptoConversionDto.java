package api.dtos;
import java.math.BigDecimal;

public class CryptoConversionDto {

    private String from;
    private String to;
    private BigDecimal quantity;
    private BigDecimal convertedAmount;
    private String transactionMessage;
    private CryptoWalletDto updatedWalletState; // Dodajemo stanje novčanika

    public CryptoConversionDto() {
    }

    public CryptoConversionDto(String from, String to, BigDecimal quantity, BigDecimal convertedAmount, String transactionMessage, CryptoWalletDto updatedWalletState) {
        this.from = from;
        this.to = to;
        this.quantity = quantity;
        this.convertedAmount = convertedAmount;
        this.transactionMessage = transactionMessage;
        this.updatedWalletState = updatedWalletState;
    }

    // Getteri i Setteri
    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }
    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getConvertedAmount() { return convertedAmount; }
    public void setConvertedAmount(BigDecimal convertedAmount) { this.convertedAmount = convertedAmount; }
    public String getTransactionMessage() { return transactionMessage; }
    public void setTransactionMessage(String transactionMessage) { this.transactionMessage = transactionMessage; }
    public CryptoWalletDto getUpdatedWalletState() { return updatedWalletState; }
    public void setUpdatedWalletState(CryptoWalletDto updatedWalletState) { this.updatedWalletState = updatedWalletState; }
}
