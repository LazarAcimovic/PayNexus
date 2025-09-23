package api.dtos;

public class ConversionResponseDto {
    private String transactionMessage;
    private BankAccountDto bankAccountState;

    // Constructors
    public ConversionResponseDto(String transactionMessage, BankAccountDto bankAccountState) {
        this.transactionMessage = transactionMessage;
        this.bankAccountState = bankAccountState;
    }

    // Getters and setters
    public String getTransactionMessage() {
        return transactionMessage;
    }

    public void setTransactionMessage(String transactionMessage) {
        this.transactionMessage = transactionMessage;
    }

    public BankAccountDto getBankAccountState() {
        return bankAccountState;
    }

    public void setBankAccountState(BankAccountDto bankAccountState) {
        this.bankAccountState = bankAccountState;
    }
}