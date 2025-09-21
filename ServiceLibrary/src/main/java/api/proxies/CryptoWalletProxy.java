package api.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


import api.dtos.CryptoWalletDto;
import api.dtos.OperationResponseDto;

@FeignClient("crypto-wallet")
public interface CryptoWalletProxy {
	
    @PostMapping("/crypto-wallets/new")
    ResponseEntity<?> createCryptoWallet(@RequestBody CryptoWalletDto dto);

    @DeleteMapping("/crypto-wallets/delete")
    ResponseEntity<?> deleteCryptoWallet(@RequestParam(value="email") String email);
    
    @GetMapping("/crypto-wallets/email")
    ResponseEntity<CryptoWalletDto> getCryptoWalletByEmail(@RequestHeader("X-User-Email") String userEmail);

    @PutMapping("/crypto-wallets/update")
    ResponseEntity<OperationResponseDto> updateCryptoWallet(@RequestBody CryptoWalletDto dto);

}
