package api.services;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import api.dtos.BankAccountDto;

public interface BankAccountService {
	
	@PostMapping("/new")
	ResponseEntity<?> createBankAccount(@RequestBody BankAccountDto dto);
	
	@DeleteMapping("/delete")
	ResponseEntity<?> deleteBankAccount(@RequestParam String email);
	
    @GetMapping("/email")
    BankAccountDto getBankAccountByEmail(@RequestHeader("X-User-Email") String userEmail);
	
    @GetMapping("/getAllBankAccounts")
    ResponseEntity<List<BankAccountDto>> getAllBankAccounts();
    
    @PutMapping("/update")
    ResponseEntity<?> updateBankAccount(@RequestBody BankAccountDto dto);
    
    @PutMapping("/update/user")
    ResponseEntity<?> updateUserBankAccountByEmail(
        @RequestBody BankAccountDto dto,
        @RequestHeader("X-User-Email") String userEmail
    );

}