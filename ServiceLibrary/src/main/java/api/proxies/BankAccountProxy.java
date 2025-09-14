package api.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import api.dtos.BankAccountDto;

@FeignClient("bank-account")
public interface BankAccountProxy {

    @PostMapping("/bank-accounts/new")
    ResponseEntity<?> createBankAccount(@RequestBody BankAccountDto dto);

    @DeleteMapping("/bank-accounts/delete")
    ResponseEntity<?> deleteBankAccount(@RequestParam String email);
}