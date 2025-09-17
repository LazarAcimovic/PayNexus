package api.services;

import java.math.BigDecimal;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/crypto-conversion")
@Service
public interface CryptoConversionService {
    @GetMapping("/convert")
    ResponseEntity<?> convertCryptoFeign(
        @RequestParam String from,
        @RequestParam String to,
        @RequestParam BigDecimal quantity,
        @RequestHeader(("X-User-Email")) String userEmail
    );
}
