package api.services;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;





public interface TradeService {

    @GetMapping("/trade-service")
    ResponseEntity<?> trade(
        @RequestParam String from,
        @RequestParam String to,
        @RequestParam BigDecimal quantity,
        @RequestHeader("X-User-Email") String userEmail
    );
}