package api.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import api.dtos.CurrencyExchangeDto;

@FeignClient("currency-exchange")
public interface CurrencyExchangeProxy {
	@GetMapping("/currency-exchange")
	// getCurrencyExchange and getExchangeFeign don’t need to have the same method name  
	// the endpoint, HTTP request type, and method parameters must match, but not the method name  
	// likewise, we can say that we return currencyExchangeDTO instead of ?

	ResponseEntity<CurrencyExchangeDto> getExchangeFeign(@RequestParam(value="from") String from, @RequestParam(value="to") String to);
}
