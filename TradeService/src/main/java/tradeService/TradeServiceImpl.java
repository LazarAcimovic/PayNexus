package tradeService;

import api.dtos.*;
import api.proxies.*;
import api.services.TradeService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import api.dtos.TradeRequestDto;
import api.proxies.BankAccountProxy;
import api.proxies.CryptoExchangeProxy;
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

   /* @Autowired
    private CryptoExchangeProxy cryptoExchangeProxy; //*/

    @Autowired
    private TradeExchangeRepository tradeExchangeRepository;

    private static final List<String> FIAT_CURRENCIES = Arrays.asList("RSD", "EUR", "USD", "GBP", "CHF");
    private static final List<String> CRYPTO_CURRENCIES = Arrays.asList("BTC", "ETH", "LTC", "XRP");

    @Override
    public ResponseEntity<?> trade(String from, String to, BigDecimal quantity, String userEmail) {
        from = from.toUpperCase();
        to = to.toUpperCase();

        boolean isFromFiat = FIAT_CURRENCIES.contains(from);
        boolean isToCrypto = CRYPTO_CURRENCIES.contains(to);
        boolean isFromCrypto = CRYPTO_CURRENCIES.contains(from);
        boolean isToFiat = FIAT_CURRENCIES.contains(to);
        
        if (isFromFiat && isToCrypto) {
            return exchangeFiatToCrypto(from, to, quantity, userEmail);
        } else if (isFromCrypto && isToFiat) {
            return exchangeCryptoToFiat(from, to, quantity, userEmail);
        }

        return ResponseEntity.badRequest().body("Invalid currency combination for trade.");
    }
    
    private ResponseEntity<?> exchangeFiatToCrypto(String from, String to, BigDecimal quantity, String userEmail) {
        // Korak 1: Dohvatanje bankovnog računa
        ResponseEntity<BankAccountDto> bankAccountResponse = bankAccountProxy.getBankAccountByEmail(userEmail);
        if (!bankAccountResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(bankAccountResponse.getStatusCode()).body("Failed to get bank account details.");
        }
        BankAccountDto userBankAccount = bankAccountResponse.getBody();
        if (userBankAccount == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bank account not found for user.");
        }
        
        // Provera stanja
        BigDecimal fiatFromAmount = getFiatAmount(userBankAccount, from);
        if (fiatFromAmount == null || fiatFromAmount.compareTo(quantity) < 0) {
            return ResponseEntity.badRequest().body("Insufficient funds for exchange. Available " + from + ": " + fiatFromAmount);
        }

        // Korak 2: Provera valute
        BigDecimal finalQuantityInBaseFiat;
        String baseFiatCurrency;
        
        if (from.equals("EUR")) {
            finalQuantityInBaseFiat = quantity;
            baseFiatCurrency = "EUR";
        } else if (from.equals("USD")) {
            finalQuantityInBaseFiat = quantity;
            baseFiatCurrency = "USD";
        } else {
            // Kaskadna konverzija RSD -> EUR -> EUR -> BTC (primer)
            ResponseEntity<CurrencyExchangeDto> conversionResponse = currencyExchangeProxy.getExchangeFeign(from, "EUR");
            if (!conversionResponse.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get currency exchange rate for fiat conversion.");
            }
            BigDecimal conversionRate = conversionResponse.getBody().getExchangeRate();
            finalQuantityInBaseFiat = quantity.multiply(conversionRate);
            baseFiatCurrency = "EUR"; // fali i za dolar posle, ovo je za eur samo (opciono)
        }

        // Korak 3: Dohvatanje kursa i izračunavanje kripto količine
        TradeExchangeModel tradeExchange = tradeExchangeRepository.findByFromAndTo(baseFiatCurrency, to);
        if (tradeExchange == null) {
            return ResponseEntity.badRequest().body("Exchange rate not found for " + baseFiatCurrency + " to " + to);
        }
       // System.out.println(finalQuantityInBaseFiat); //100
        //System.out.println(tradeExchange.getExchangeRate()); //0.00
        BigDecimal cryptoAmount = finalQuantityInBaseFiat.multiply(tradeExchange.getExchangeRate());
        //System.out.println(cryptoAmount);

        // Korak 4: Ažuriranje bankovnog računa
        BigDecimal newFromAmount = fiatFromAmount.subtract(quantity);
        setFiatAmount(userBankAccount, from, newFromAmount);
        bankAccountProxy.updateUserBankAccountByEmail(userBankAccount, userEmail);

        // Korak 5: Dohvatanje i ažuriranje kripto novčanika
        ResponseEntity<CryptoWalletDto> cryptoWalletResponse = cryptoWalletProxy.getCryptoWalletByEmail(userEmail);
        if (!cryptoWalletResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(cryptoWalletResponse.getStatusCode()).body("Failed to get crypto wallet details.");
        }
        CryptoWalletDto userCryptoWallet = cryptoWalletResponse.getBody();
        if (userCryptoWallet == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Crypto wallet not found for user.");
        }
        
        BigDecimal currentCryptoAmount = getCryptoAmount(userCryptoWallet, to);
        BigDecimal newCryptoAmount = (currentCryptoAmount != null ? currentCryptoAmount : BigDecimal.ZERO).add(cryptoAmount);
        //System.out.println(newCryptoAmount);
        setCryptoAmount(userCryptoWallet, to, newCryptoAmount);
        cryptoWalletProxy.updateCryptoWallet(userCryptoWallet);

        // Korak 6: Priprema odgovora
        String message = String.format("Successful transaction: Exchanged %s: %s for %s: %s",
                from, quantity, to, cryptoAmount.setScale(8, RoundingMode.HALF_UP));

        TradeResponseDto responseDto = new TradeResponseDto(message, userCryptoWallet);
        return ResponseEntity.ok(responseDto);
    }
    
    private ResponseEntity<?> exchangeCryptoToFiat(String from, String to, BigDecimal quantity, String userEmail) {
        // Korak 1: Dohvatanje kripto novčanika
        ResponseEntity<CryptoWalletDto> cryptoWalletResponse = cryptoWalletProxy.getCryptoWalletByEmail(userEmail);
        if (!cryptoWalletResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(cryptoWalletResponse.getStatusCode()).body("Failed to get crypto wallet details.");
        }
        CryptoWalletDto userCryptoWallet = cryptoWalletResponse.getBody();
        if (userCryptoWallet == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Crypto wallet not found for user.");
        }

        // Provera stanja
        BigDecimal cryptoFromAmount = getCryptoAmount(userCryptoWallet, from);
        if (cryptoFromAmount == null || cryptoFromAmount.compareTo(quantity) < 0) {
            return ResponseEntity.badRequest().body("Insufficient funds for exchange. Available " + from + ": " + cryptoFromAmount);
        }

        // Korak 2: Konverzija u EUR ili USD
        TradeExchangeModel tradeExchange;
        String baseFiatCurrency;
        
        // Specifikacija zahteva konverziju u USD ili EUR pre nego što se pređe na drugu fiat valutu.
        // Odabraćemo EUR kao osnovnu fiat valutu za posrednu konverziju.
        tradeExchange = tradeExchangeRepository.findByFromAndTo(from, "EUR");
        baseFiatCurrency = "EUR";

        if (tradeExchange == null) {
            // Ako kurs za EUR nije pronađen, pokušajte sa USD
            tradeExchange = tradeExchangeRepository.findByFromAndTo(from, "USD");
            baseFiatCurrency = "USD";
        }
        
        if (tradeExchange == null) {
            return ResponseEntity.badRequest().body("Exchange rate not found for " + from + " to " + to);
        }
        
        BigDecimal quantityInBaseFiat = quantity.multiply(tradeExchange.getExchangeRate());
        BigDecimal finalFiatAmount;
        
        // Korak 3: Kaskadna konverzija u željenu fiat valutu (ako je potrebno)
        if (to.equals("EUR") || to.equals("USD")) {
            finalFiatAmount = quantityInBaseFiat; 
        } else {															
            ResponseEntity<CurrencyExchangeDto> conversionResponse = currencyExchangeProxy.getExchangeFeign(baseFiatCurrency, to);
            if (!conversionResponse.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get currency exchange rate for fiat conversion.");
            }
            BigDecimal conversionRate = conversionResponse.getBody().getExchangeRate();
            finalFiatAmount = quantityInBaseFiat.multiply(conversionRate);
        }
        
        // Korak 4: Ažuriranje kripto novčanika
        BigDecimal newCryptoAmount = cryptoFromAmount.subtract(quantity);
        setCryptoAmount(userCryptoWallet, from, newCryptoAmount);
        cryptoWalletProxy.updateCryptoWallet(userCryptoWallet);
        
        // Korak 5: Dohvatanje i ažuriranje bankovnog računa
        ResponseEntity<BankAccountDto> bankAccountResponse = bankAccountProxy.getBankAccountByEmail(userEmail);
        if (!bankAccountResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(bankAccountResponse.getStatusCode()).body("Failed to get bank account details.");
        }
        BankAccountDto userBankAccount = bankAccountResponse.getBody();
        if (userBankAccount == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bank account not found for user.");
        }
        
        BigDecimal currentFiatAmount = getFiatAmount(userBankAccount, to);
        BigDecimal newFiatAmount = (currentFiatAmount != null ? currentFiatAmount : BigDecimal.ZERO).add(finalFiatAmount);
        setFiatAmount(userBankAccount, to, newFiatAmount);
        bankAccountProxy.updateUserBankAccountByEmail(userBankAccount, userEmail);
        
        // Korak 6: Priprema odgovora
        String message = String.format("Successful transaction: Exchanged %s: %s for %s: %s",
                from, quantity.setScale(8, RoundingMode.HALF_UP), to, finalFiatAmount);

        TradeResponseDto responseDto = new TradeResponseDto(message, userBankAccount);
        return ResponseEntity.ok(responseDto);
    }
    
    // Utility metode
    private BigDecimal getFiatAmount(BankAccountDto dto, String currency) {
        return switch (currency.toUpperCase()) {
            case "USD" -> dto.getUsd();
            case "EUR" -> dto.getEur();
            case "RSD" -> dto.getRsd();
            case "GBP" -> dto.getGbp();
            case "CHF" -> dto.getChf();
            default -> null;
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