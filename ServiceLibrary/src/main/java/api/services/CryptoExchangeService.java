package api.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Service
public interface CryptoExchangeService {

	@GetMapping("/crypto-exchange")
    ResponseEntity<?> getCryptoExchange( @RequestParam String from,@RequestParam String to );
          
}