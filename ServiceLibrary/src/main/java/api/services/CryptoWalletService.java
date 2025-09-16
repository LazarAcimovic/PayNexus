package api.services;


import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import api.dtos.CryptoWalletDto;

import org.springframework.web.bind.annotation.RequestHeader;


public interface CryptoWalletService {

    @PostMapping("/new")
    ResponseEntity<?> createCryptoWallet(@RequestBody CryptoWalletDto dto);

    @PutMapping("/update")
    ResponseEntity<?> updateCryptoWallet(@RequestBody CryptoWalletDto dto);

    @DeleteMapping("/delete")
    ResponseEntity<?> deleteCryptoWallet(@RequestParam String email);
    
    @GetMapping("/all")
    ResponseEntity<List<CryptoWalletDto>> getAllCryptoWallets();

    @GetMapping("/email")
    ResponseEntity<?> getCryptoWalletByEmail(@RequestHeader("X-User-Email") String userEmail);

}