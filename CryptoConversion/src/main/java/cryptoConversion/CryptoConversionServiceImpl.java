package cryptoConversion;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import api.dtos.CryptoConversionDto;
import api.dtos.CryptoExchangeDto;
import api.dtos.CryptoWalletDto;
import api.dtos.OperationResponseDto;
import api.proxies.CryptoExchangeProxy;
import api.proxies.CryptoWalletProxy;
import api.services.CryptoConversionService;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@RestController
public class CryptoConversionServiceImpl implements CryptoConversionService {

    @Autowired
    private CryptoWalletProxy cryptoWalletProxy;

    @Autowired
    private CryptoExchangeProxy cryptoExchangeProxy;

    @Override
    @CircuitBreaker(name = "cb", fallbackMethod = "fallback")
    public ResponseEntity<?> convertCryptoFeign(
        String from,
        String to,
        BigDecimal quantity,
        String userEmail
    ) {
        // Korak 1: Provera stanja u novčaniku
        ResponseEntity<CryptoWalletDto> walletResponse = cryptoWalletProxy.getCryptoWalletByEmail(userEmail);

        if (!walletResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(walletResponse.getStatusCode()).body("Failed to get crypto wallet details.");
        }

        CryptoWalletDto userWallet = walletResponse.getBody();

        if (userWallet == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Crypto wallet not found for user.");
        }

        BigDecimal fromAmount = getCurrencyAmount(userWallet, from);

        if (fromAmount == null || fromAmount.compareTo(quantity) < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                "Insufficient funds for exchange. Available " + from + ": " + fromAmount
            );
        }

        // Korak 2: Dohvatanje kursa razmene
        ResponseEntity<CryptoExchangeDto> exchangeResponse = cryptoExchangeProxy.getExchangeRate(from, to);

        if (!exchangeResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(exchangeResponse.getStatusCode()).body("Failed to get exchange rate.");
        }

        CryptoExchangeDto exchangeDto = exchangeResponse.getBody();
        BigDecimal conversionRate = exchangeDto.getExchangeRate();

        // Korak 3: Izračunavanje novih vrednosti
        BigDecimal newFromAmount = fromAmount.subtract(quantity);
        BigDecimal convertedToAmount = quantity.multiply(conversionRate);
        BigDecimal toAmount = getCurrencyAmount(userWallet, to);
        BigDecimal newToAmount = toAmount.add(convertedToAmount);

        // Korak 4: Ažuriranje novčanika
        setCurrencyAmount(userWallet, from, newFromAmount);
        setCurrencyAmount(userWallet, to, newToAmount);

        ResponseEntity<OperationResponseDto> updateResponse = cryptoWalletProxy.updateCryptoWallet(userWallet);

        if (!updateResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to update crypto wallet. Transaction rolled back.");
        }

        
        String message = String.format("Successful transaction: Exchanged %s: %s for %s: %s",
            from, quantity, to, convertedToAmount);

        CryptoConversionDto resultDto = new CryptoConversionDto(
            from,
            to,
            quantity,
            convertedToAmount,
            message,
            userWallet 
        );

        return ResponseEntity.ok(resultDto);
    }

    public ResponseEntity<?> fallback(CallNotPermittedException ex){
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body("Crypto conversion service is currently unavailable, Circuit breaker is in OPEN state!");
    }

    private BigDecimal getCurrencyAmount(CryptoWalletDto dto, String currency) {
        return switch (currency.toUpperCase()) {
            case "BTC" -> dto.getBTC();
            case "ETH" -> dto.getETH();
            case "XRP" -> dto.getXRP();
            case "LTC" -> dto.getLTC();
            default -> null;
        };
    }

    private void setCurrencyAmount(CryptoWalletDto dto, String currency, BigDecimal amount) {
        switch (currency.toUpperCase()) {
            case "BTC" -> dto.setBTC(amount);
            case "ETH" -> dto.setETH(amount);
            case "XRP" -> dto.setXRP(amount);
            case "LTC" -> dto.setLTC(amount);
        }
    }
}