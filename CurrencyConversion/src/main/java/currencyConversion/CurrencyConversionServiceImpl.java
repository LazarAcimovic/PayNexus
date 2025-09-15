package currencyConversion;

import java.math.BigDecimal;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import api.dtos.BankAccountDto;
import api.dtos.CurrencyConversionDto;
import api.dtos.CurrencyExchangeDto;
import api.proxies.BankAccountProxy;
import api.proxies.CurrencyExchangeProxy;
import api.services.CurrencyConversionService;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import util.exceptions.InvalidQuantityException;

@RestController
public class CurrencyConversionServiceImpl implements CurrencyConversionService {

	private RestTemplate template = new RestTemplate();
	
	@Autowired
	private CurrencyExchangeProxy proxy;
	
    @Autowired
    private BankAccountProxy bankAccountProxy;
	
	Retry retry;
	CurrencyExchangeDto response;
	
	public CurrencyConversionServiceImpl(RetryRegistry registry) {
		retry = registry.retry("default");
	}
	
	@Override
	@CircuitBreaker(name = "cb", fallbackMethod = "fallback")
	public ResponseEntity<?> getConversionFeign(
	    @RequestParam String from, 
	    @RequestParam String to, 
	    @RequestParam BigDecimal quantity,
	    @RequestHeader("X-User-Email") String userEmail
	) {
	    // getting user bank account state
	    BankAccountDto userAccount = bankAccountProxy.getBankAccountByEmail(userEmail);

	    if (userAccount == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bank account not found for user.");
	    }
	    
	    // current state from from value
	    BigDecimal fromAmount = getCurrencyAmount(userAccount, from);

	    
	    if (fromAmount == null || fromAmount.compareTo(quantity) < 0) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
	            "Insufficient funds for exchange. Available " + from + ": " + fromAmount
	        );
	    }

	    // getting course
	    ResponseEntity<CurrencyExchangeDto> exchangeResponse = proxy.getExchangeFeign(from, to);
	    CurrencyExchangeDto exchangeDto = exchangeResponse.getBody();
	    BigDecimal conversionRate = exchangeDto.getExchangeRate();

	   
	    BigDecimal newFromAmount = fromAmount.subtract(quantity);
	    BigDecimal convertedToAmount = quantity.multiply(conversionRate);

	    // getting currently state from to value
	    BigDecimal toAmount = getCurrencyAmount(userAccount, to);
	    BigDecimal newToAmount = toAmount.add(convertedToAmount);

	    // updating user account
	    setCurrencyAmount(userAccount, from, newFromAmount);
	    setCurrencyAmount(userAccount, to, newToAmount);

	    
	    bankAccountProxy.updateUserBankAccountByEmail(userAccount, userEmail);

	   
	    String message = String.format("Successful transaction: Exchanged %s: %s for %s: %s",
	        from, quantity, to, convertedToAmount);

	    return ResponseEntity.ok(message);
	}
	
	@Override
	@GetMapping("/currency-conversion")
	public ResponseEntity<?> getConversion(@RequestParam String from, @RequestParam String to, @RequestParam BigDecimal quantity) {
		if(quantity.compareTo(BigDecimal.valueOf(300.0)) == 1) {
			throw new InvalidQuantityException(String.format("Quantity of %s is too large", quantity));
		}
		
		String endPoint = "http://localhost:8000/currency-exchange?from=" + from + "&to=" + to;
		ResponseEntity<CurrencyExchangeDto> response;
		response = template.getForEntity(endPoint, CurrencyExchangeDto.class);
	
		
		return ResponseEntity.ok(new CurrencyConversionDto(response.getBody(), quantity));
	}
	
	public ResponseEntity<?> fallback(CallNotPermittedException ex){
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.body("Currency conversion service is currently unavailbale, Circuit breaker is in OPEN state!");
	}
	
	private BigDecimal getCurrencyAmount(BankAccountDto dto, String currency) {
	    return switch (currency) {
	        case "USD" -> dto.getUsd();
	        case "EUR" -> dto.getEur();
	        case "GBP" -> dto.getGbp();
	        case "CHF" -> dto.getChf();
	        case "RSD" -> dto.getRsd();
	        default -> null;
	    };
	}

	private void setCurrencyAmount(BankAccountDto dto, String currency, BigDecimal amount) {
	    switch (currency) {
	        case "USD" -> dto.setUsd(amount);
	        case "EUR" -> dto.setEur(amount);
	        case "GBP" -> dto.setGbp(amount);
	        case "CHF" -> dto.setChf(amount);
	        case "RSD" -> dto.setRsd(amount);
	    }
	}
}