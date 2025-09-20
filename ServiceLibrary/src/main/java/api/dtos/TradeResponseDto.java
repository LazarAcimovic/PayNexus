package api.dtos;

public class TradeResponseDto {

    private String transactionMessage;
    private BankAccountDto updatedBankAccount;
    private CryptoWalletDto updatedCryptoWallet;

    public TradeResponseDto() {
    }

 // Constructor for FIAT -> CRYPTO exchange
    public TradeResponseDto(String transactionMessage, CryptoWalletDto updatedCryptoWallet) {
        this.transactionMessage = transactionMessage;
        this.updatedCryptoWallet = updatedCryptoWallet;
        this.updatedBankAccount = null; // We're setting on null because of the project specification
    }

 // Constructor for CRYPTO -> FIAT exchange
    public TradeResponseDto(String transactionMessage, BankAccountDto updatedBankAccount) {
        this.transactionMessage = transactionMessage;
        this.updatedBankAccount = updatedBankAccount;
        this.updatedCryptoWallet = null; // vice versa from TradeResponseDto
    }

    // Getters and Setters
    public String getTransactionMessage() {
        return transactionMessage;
    }

    public void setTransactionMessage(String transactionMessage) {
        this.transactionMessage = transactionMessage;
    }

    public BankAccountDto getUpdatedBankAccount() {
        return updatedBankAccount;
    }

    public void setUpdatedBankAccount(BankAccountDto updatedBankAccount) {
        this.updatedBankAccount = updatedBankAccount;
    }

    public CryptoWalletDto getUpdatedCryptoWallet() {
        return updatedCryptoWallet;
    }

    public void setUpdatedCryptoWallet(CryptoWalletDto updatedCryptoWallet) {
        this.updatedCryptoWallet = updatedCryptoWallet;
    }
}