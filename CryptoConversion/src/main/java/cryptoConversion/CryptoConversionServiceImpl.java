package cryptoConversion;

import java.math.BigDecimal;
import java.util.List;

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
import util.exceptions.CurrencyDoesntExistException;
import util.exceptions.InsufficientFundsException;
import util.exceptions.InvalidQuantityException;
import util.exceptions.NoDataFoundException;

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
    	
		if(quantity.compareTo(BigDecimal.valueOf(300.0)) == 1) {
			throw new InvalidQuantityException(String.format("Quantity of %s is too large", quantity));
		}
        // CHecking account state
        ResponseEntity<CryptoWalletDto> walletResponse = cryptoWalletProxy.getCryptoWalletByEmail(userEmail);

        if (!walletResponse.getStatusCode().is2xxSuccessful()) {
        	throw new NoDataFoundException("Crypto wallet not found for user.");
        }

        CryptoWalletDto userWallet = walletResponse.getBody();

        if (userWallet == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Crypto wallet details not found for user.");
        }

        BigDecimal fromAmount = getCurrencyAmount(userWallet, from);

        if (fromAmount == null || fromAmount.compareTo(quantity) < 0) {
        	throw new InsufficientFundsException("Insufficient funds for exchange. Available " + from + ": " + fromAmount);

        }

        // Getting exchange rate
        ResponseEntity<CryptoExchangeDto> exchangeResponse = cryptoExchangeProxy.getExchangeRate(from, to);

        if (!exchangeResponse.getStatusCode().is2xxSuccessful()) {
        	throw new RuntimeException("Failed to get exchange rate.");
        }

        CryptoExchangeDto exchangeDto = exchangeResponse.getBody();
        BigDecimal conversionRate = exchangeDto.getExchangeRate();

        BigDecimal newFromAmount = fromAmount.subtract(quantity);
        BigDecimal convertedToAmount = quantity.multiply(conversionRate);
        BigDecimal toAmount = getCurrencyAmount(userWallet, to);
        BigDecimal newToAmount = toAmount.add(convertedToAmount);

        // Updating account
        setCurrencyAmount(userWallet, from, newFromAmount);
        setCurrencyAmount(userWallet, to, newToAmount);

        ResponseEntity<OperationResponseDto> updateResponse = cryptoWalletProxy.updateCryptoWallet(userWallet);

        if (!updateResponse.getStatusCode().is2xxSuccessful()) {
        	throw new RuntimeException("Failed to update crypto wallet. Transaction not completed.");
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
    	List<String> supportedCurrencies = List.of("BTC", "ETH", "XRP", "LTC");
        return switch (currency.toUpperCase()) {
            case "BTC" -> dto.getBTC();
            case "ETH" -> dto.getETH();
            case "XRP" -> dto.getXRP();
            case "LTC" -> dto.getLTC();
            default -> {
                throw new CurrencyDoesntExistException(
                    "Currency '" + currency + "' doesn't exist.",
                    supportedCurrencies
                );
            }
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