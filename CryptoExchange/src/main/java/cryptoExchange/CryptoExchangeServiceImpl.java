package cryptoExchange;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import api.dtos.CryptoExchangeDto;
import api.services.CryptoExchangeService;


@RestController
public class CryptoExchangeServiceImpl implements CryptoExchangeService {

    @Autowired
    private CryptoExchangeRepository repo;

    @Override
    public ResponseEntity<?> getCryptoExchange(String from, String to) {
        CryptoExchangeModel exchange = repo.findByFromAndTo(from, to);
        
        if (exchange == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Exchange rate not found for " + from + " to " + to);
        }
        
        CryptoExchangeDto dto = new CryptoExchangeDto(
            exchange.getFrom(),
            exchange.getTo(),
            exchange.getExchangeRate()
        );
        
        return ResponseEntity.ok(dto);
    }
}