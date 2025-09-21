package cryptoExchange;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import api.dtos.CryptoExchangeDto;
import api.services.CryptoExchangeService;
import util.exceptions.CurrencyDoesntExistException;
import util.exceptions.NoDataFoundException;


@RestController
public class CryptoExchangeServiceImpl implements CryptoExchangeService {

    @Autowired
    private CryptoExchangeRepository repo;

    @Override
    public ResponseEntity<?> getCryptoExchange(String from, String to) {
    	validateCurrencies(from.toUpperCase(), to.toUpperCase());
        CryptoExchangeModel exchange = repo.findByFromAndTo(from.toUpperCase(), to.toUpperCase());
        
        if (exchange == null) {
            throw new NoDataFoundException("Exchange rate not found for " + from.toUpperCase() + " to " + to.toUpperCase(), List.of(from.toUpperCase(), to.toUpperCase()));
        }
        
        CryptoExchangeDto dto = new CryptoExchangeDto(
            exchange.getFrom(),
            exchange.getTo(),
            exchange.getExchangeRate()
        );
        
        return ResponseEntity.ok(dto);
    }
    
    private void validateCurrencies(String... currencies) {
        List<String> supportedCurrencies = List.of("BTC", "ETH", "LTC");
        for (String currency : currencies) {
            if (!supportedCurrencies.contains(currency.toUpperCase())) {
                throw new CurrencyDoesntExistException("Currency '" + currency + "' doesn't exist.", supportedCurrencies);
            }
        }
    }
}