package api.dtos;

public class TradeResponseDto {

    private String transactionMessage;
    private BankAccountDto updatedBankAccount;
    private CryptoWalletDto updatedCryptoWallet;

    public TradeResponseDto() {
    }

    // Konstruktor za razmenu FIAT -> CRYPTO
    public TradeResponseDto(String transactionMessage, CryptoWalletDto updatedCryptoWallet) {
        this.transactionMessage = transactionMessage;
        this.updatedCryptoWallet = updatedCryptoWallet;
        this.updatedBankAccount = null; // Postavljamo na null da ne bi bilo duplih informacija
    }

    // Konstruktor za razmenu CRYPTO -> FIAT
    public TradeResponseDto(String transactionMessage, BankAccountDto updatedBankAccount) {
        this.transactionMessage = transactionMessage;
        this.updatedBankAccount = updatedBankAccount;
        this.updatedCryptoWallet = null; // Postavljamo na null
    }

    // Getteri i setteri
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