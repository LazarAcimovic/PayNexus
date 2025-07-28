package api.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import api.dtos.CurrencyExchangeDto;

@FeignClient("currency-exchange")
public interface CurrencyExchangeProxy {
	@GetMapping("/currency-exchange")
	//getCurrencyExchange getExchangeFeign ne mora isti naziv metode
	//mora da se podudara endpoint, tip http zahteva i parametri metode, ne i naziv metode
	//isto tako možemo reći da vraćamo currencyExchangeDTO umesto ?
	ResponseEntity<CurrencyExchangeDto> getExchangeFeign(@RequestParam String from, @RequestParam String to);
}
