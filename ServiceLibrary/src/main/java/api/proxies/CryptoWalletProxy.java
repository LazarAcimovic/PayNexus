package api.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


import api.dtos.CryptoWalletDto;

@FeignClient("crypto-wallet")
public interface CryptoWalletProxy {
	
    @PostMapping("/crypto-wallets/new")
    ResponseEntity<?> createCryptoWallet(@RequestBody CryptoWalletDto dto);

    @DeleteMapping("/crypto-wallets/delete")
    ResponseEntity<?> deleteCryptoWallet(@RequestParam String email);

}
