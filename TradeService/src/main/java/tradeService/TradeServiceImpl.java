package tradeService;

import api.dtos.*;

import api.services.TradeService;
import util.exceptions.InsufficientFundsException;
import util.exceptions.InvalidCurrencyCombinationException;
import util.exceptions.InvalidQuantityException;
import util.exceptions.NoDataFoundException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


import api.proxies.BankAccountProxy;

import api.proxies.CryptoWalletProxy;
import api.proxies.CurrencyExchangeProxy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

@RestController
public class TradeServiceImpl implements TradeService {

    @Autowired
    private BankAccountProxy bankAccountProxy;

    @Autowired
    private CryptoWalletProxy cryptoWalletProxy;

    @Autowired
    private CurrencyExchangeProxy currencyExchangeProxy;

    @Autowired
    private TradeExchangeRepository tradeExchangeRepository;

    private static final List<String> FIAT_CURRENCIES = Arrays.asList("RSD", "EUR", "USD", "GBP", "CHF");
    private static final List<String> CRYPTO_CURRENCIES = Arrays.asList("BTC", "ETH", "LTC", "XRP");

    @Override
    public ResponseEntity<?> trade(String from, String to, BigDecimal quantity, String userEmail) {
        from = from.toUpperCase();
        to = to.toUpperCase();
        
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidQuantityException("Quantity must be a positive number.");
        }

        boolean isFromFiat = FIAT_CURRENCIES.contains(from);
        boolean isToCrypto = CRYPTO_CURRENCIES.contains(to);
        boolean isFromCrypto = CRYPTO_CURRENCIES.contains(from);
        boolean isToFiat = FIAT_CURRENCIES.contains(to);
        
        if (isFromFiat && isToCrypto) {
            return exchangeFiatToCrypto(from, to, quantity, userEmail);
        } else if (isFromCrypto && isToFiat) {
            return exchangeCryptoToFiat(from, to, quantity, userEmail);
        }

        // If the currency combination is not valid
        throw new InvalidCurrencyCombinationException("Invalid currency combination for trade. Cannot trade from " + from + " to " + to);
    }
    
    private ResponseEntity<?> exchangeFiatToCrypto(String from, String to, BigDecimal quantity, String userEmail) {
        // Getting account state
        ResponseEntity<BankAccountDto> bankAccountResponse = bankAccountProxy.getBankAccountByEmail(userEmail);
        if (!bankAccountResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to get bank account details.");
        }
        BankAccountDto userBankAccount = bankAccountResponse.getBody();
        if (userBankAccount == null) {
            throw new NoDataFoundException("Bank account not found for user.");
        }
        
        // Checking state
        BigDecimal fiatFromAmount = getFiatAmount(userBankAccount, from);
        if (fiatFromAmount == null || fiatFromAmount.compareTo(quantity) < 0) {
            throw new InsufficientFundsException("Insufficient funds for exchange. Available " + from + ": " + fiatFromAmount);
        }

        // Checking currency
        BigDecimal finalQuantityInBaseFiat;
        String baseFiatCurrency;
        
        if (from.equals("EUR")) {
            finalQuantityInBaseFiat = quantity;
            baseFiatCurrency = "EUR";
        } else if (from.equals("USD")) {
            finalQuantityInBaseFiat = quantity;
            baseFiatCurrency = "USD";
        } else {
            ResponseEntity<CurrencyExchangeDto> conversionResponse = currencyExchangeProxy.getExchangeFeign(from, "EUR");
            if (!conversionResponse.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to get currency exchange rate for fiat conversion.");
            }
            BigDecimal conversionRate = conversionResponse.getBody().getExchangeRate();
            finalQuantityInBaseFiat = quantity.multiply(conversionRate);
            baseFiatCurrency = "EUR"; 
        }

        // Dobijanje kursa
        TradeExchangeModel tradeExchange = tradeExchangeRepository.findByFromAndTo(baseFiatCurrency, to);
        if (tradeExchange == null) {
            throw new NoDataFoundException("Exchange rate not found for " + baseFiatCurrency + " to " + to);
        }

        BigDecimal cryptoAmount = finalQuantityInBaseFiat.multiply(tradeExchange.getExchangeRate());

        // Updating account state
        BigDecimal newFromAmount = fiatFromAmount.subtract(quantity);
        setFiatAmount(userBankAccount, from, newFromAmount);
        bankAccountProxy.updateUserBankAccountByEmail(userBankAccount, userEmail);


        // Getting and updating crypto wallet
        ResponseEntity<CryptoWalletDto> cryptoWalletResponse = cryptoWalletProxy.getCryptoWalletByEmail(userEmail);
        if (!cryptoWalletResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to get crypto wallet details.");
        }
        CryptoWalletDto userCryptoWallet = cryptoWalletResponse.getBody();
        if (userCryptoWallet == null) {
            throw new NoDataFoundException("Crypto wallet not found for user.");
        }
        
        BigDecimal currentCryptoAmount = getCryptoAmount(userCryptoWallet, to);
        BigDecimal newCryptoAmount = (currentCryptoAmount != null ? currentCryptoAmount : BigDecimal.ZERO).add(cryptoAmount);
        setCryptoAmount(userCryptoWallet, to, newCryptoAmount);
        cryptoWalletProxy.updateCryptoWallet(userCryptoWallet);

        String message = String.format("Successful transaction: Exchanged %s: %s for %s: %s",
                from, quantity, to, cryptoAmount.setScale(8, RoundingMode.HALF_UP));

        TradeResponseDto responseDto = new TradeResponseDto(message, userCryptoWallet);
        return ResponseEntity.ok(responseDto);
    }
    
    private ResponseEntity<?> exchangeCryptoToFiat(String from, String to, BigDecimal quantity, String userEmail) {
        // Getting crypto wallet
        ResponseEntity<CryptoWalletDto> cryptoWalletResponse = cryptoWalletProxy.getCryptoWalletByEmail(userEmail);
        if (!cryptoWalletResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to get crypto wallet details.");
        }
        CryptoWalletDto userCryptoWallet = cryptoWalletResponse.getBody();
        if (userCryptoWallet == null) {
            throw new NoDataFoundException("Crypto wallet not found for user.");
        }

        // Checkign state of an account
        BigDecimal cryptoFromAmount = getCryptoAmount(userCryptoWallet, from);
        if (cryptoFromAmount == null || cryptoFromAmount.compareTo(quantity) < 0) {
            throw new InsufficientFundsException("Insufficient funds for exchange. Available " + from + ": " + cryptoFromAmount);
        }

       
        TradeExchangeModel tradeExchange;
        String baseFiatCurrency;
        
        tradeExchange = tradeExchangeRepository.findByFromAndTo(from, "EUR");
        baseFiatCurrency = "EUR";

        if (tradeExchange == null) {
            tradeExchange = tradeExchangeRepository.findByFromAndTo(from, "USD");
            baseFiatCurrency = "USD";
        }
        
        if (tradeExchange == null) {
            throw new NoDataFoundException("Exchange rate not found for " + from + " to " + baseFiatCurrency);
        }
        
        BigDecimal quantityInBaseFiat = quantity.multiply(tradeExchange.getExchangeRate());
        BigDecimal finalFiatAmount;
        
        // if some other currency is present
        if (to.equals("EUR") || to.equals("USD")) {
            finalFiatAmount = quantityInBaseFiat; 
        } else {                                                      
            ResponseEntity<CurrencyExchangeDto> conversionResponse = currencyExchangeProxy.getExchangeFeign(baseFiatCurrency, to);
            if (!conversionResponse.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to get currency exchange rate for fiat conversion.");
            }
            BigDecimal conversionRate = conversionResponse.getBody().getExchangeRate();
            finalFiatAmount = quantityInBaseFiat.multiply(conversionRate);
        }
        
        // Updating crypto wallet
        BigDecimal newCryptoAmount = cryptoFromAmount.subtract(quantity);
        setCryptoAmount(userCryptoWallet, from, newCryptoAmount);
        cryptoWalletProxy.updateCryptoWallet(userCryptoWallet);
        
        //again, getting and updating account state
        ResponseEntity<BankAccountDto> bankAccountResponse = bankAccountProxy.getBankAccountByEmail(userEmail);
        if (!bankAccountResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to get bank account details.");
        }
        BankAccountDto userBankAccount = bankAccountResponse.getBody();
        if (userBankAccount == null) {
            throw new NoDataFoundException("Bank account not found for user.");
        }
        
        BigDecimal currentFiatAmount = getFiatAmount(userBankAccount, to);
        BigDecimal newFiatAmount = (currentFiatAmount != null ? currentFiatAmount : BigDecimal.ZERO).add(finalFiatAmount);
        setFiatAmount(userBankAccount, to, newFiatAmount);
        bankAccountProxy.updateUserBankAccountByEmail(userBankAccount, userEmail);
        
        String message = String.format("Successful transaction: Exchanged %s: %s for %s: %s",
                from, quantity.setScale(8, RoundingMode.HALF_UP), to, finalFiatAmount);

        TradeResponseDto responseDto = new TradeResponseDto(message, userBankAccount);
        return ResponseEntity.ok(responseDto);
    }
    
    // Helper methods
    private BigDecimal getFiatAmount(BankAccountDto dto, String currency) {
        return switch (currency.toUpperCase()) {
            case "USD" -> dto.getUsd();
            case "EUR" -> dto.getEur();
            case "RSD" -> dto.getRsd();
            case "GBP" -> dto.getGbp();
            case "CHF" -> dto.getChf();
            default -> null; // We don't throw an exception because this is used for retrieving the amount, not for validation
        };
    }

    private void setFiatAmount(BankAccountDto dto, String currency, BigDecimal amount) {
        switch (currency.toUpperCase()) {
            case "USD" -> dto.setUsd(amount);
            case "EUR" -> dto.setEur(amount);
            case "RSD" -> dto.setRsd(amount);
            case "GBP" -> dto.setGbp(amount);
            case "CHF" -> dto.setChf(amount);
        }
    }

    private BigDecimal getCryptoAmount(CryptoWalletDto dto, String currency) {
        return switch (currency.toUpperCase()) {
            case "BTC" -> dto.getBTC();
            case "ETH" -> dto.getETH();
            case "LTC" -> dto.getLTC();
            case "XRP" -> dto.getXRP();
            default -> null; 
        };
    }

    private void setCryptoAmount(CryptoWalletDto dto, String currency, BigDecimal amount) {
        switch (currency.toUpperCase()) {
            case "BTC" -> dto.setBTC(amount);
            case "ETH" -> dto.setETH(amount);
            case "LTC" -> dto.setLTC(amount);
            case "XRP" -> dto.setXRP(amount);
        }
    }
}