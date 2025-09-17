package api.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import api.dtos.CryptoExchangeDto;

@FeignClient("crypto-exchange")
public interface CryptoExchangeProxy {
    @GetMapping("/crypto-exchange")
    ResponseEntity<CryptoExchangeDto> getExchangeRate(
        @RequestParam String from,
        @RequestParam String to
    );
}
