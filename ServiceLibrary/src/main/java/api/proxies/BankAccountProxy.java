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

import api.dtos.BankAccountDto;

@FeignClient("bank-account")
public interface BankAccountProxy {

    @PostMapping("/bank-accounts/new")
    ResponseEntity<?> createBankAccount(@RequestBody BankAccountDto dto);

    @DeleteMapping("/bank-accounts/delete")
    ResponseEntity<?> deleteBankAccount(@RequestParam(value="email") String email);
    
    @PutMapping("/bank-accounts/update/user")
    ResponseEntity<BankAccountDto> updateUserBankAccountByEmail(
        @RequestBody BankAccountDto dto,
        @RequestHeader("X-User-Email") String userEmail
    );
    
    @GetMapping("/bank-accounts/email")
    ResponseEntity<BankAccountDto> getBankAccountByEmail(@RequestHeader("X-User-Email") String userEmail);
}