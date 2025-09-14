package bankAccount;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import api.dtos.BankAccountDto;
import api.dtos.OperationResponseDto;
import api.services.BankAccountService;


@RequestMapping("/bank-accounts")
@RestController
public class BankAccountServiceImpl implements BankAccountService {
	//
	@Autowired
	private BankAccountRepository repo;
	
	@Override
	public ResponseEntity<?> createBankAccount(BankAccountDto dto) {
		if (repo.findByEmail(dto.getEmail()) != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Bank account for this user already exists.");
		}
		
		BankAccountModel newAccount = new BankAccountModel(
			dto.getEmail(),
			BigDecimal.ZERO,
			BigDecimal.ZERO,
			BigDecimal.ZERO,
			BigDecimal.ZERO,
			BigDecimal.ZERO
		);
		
		repo.save(newAccount);
		return ResponseEntity.status(HttpStatus.CREATED).body(newAccount);
	}
	
	@Override
	public ResponseEntity<?> deleteBankAccount(String email) {
		BankAccountModel account = repo.findByEmail(email);
		if (account == null) {
		    return ResponseEntity
		        .status(HttpStatus.NOT_FOUND)
		        .body(new OperationResponseDto("Bank account for this user does not exist."));
		}
		
		repo.deleteByEmail(email);
	    OperationResponseDto responseDto = new OperationResponseDto("Bank account for " + email + " successfully deleted.");
	    
	    return ResponseEntity.status(HttpStatus.OK).body(responseDto);
	}
	
    @Override
    public ResponseEntity<List<BankAccountDto>> getAllBankAccounts() {
        List<BankAccountModel> accounts = repo.findAll();
        List<BankAccountDto> dtos = accounts.stream()
            .map(this::convertModelToDto)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
    
    @Override
    public ResponseEntity<?> updateBankAccount(@RequestBody BankAccountDto dto) {
        BankAccountModel existingAccount = repo.findByEmail(dto.getEmail());
        if (existingAccount == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Bank account for user with email " + dto.getEmail() + " does not exist.");
        }
        

        existingAccount.setUsd(dto.getUsd());
        existingAccount.setEur(dto.getEur());
        existingAccount.setGbp(dto.getGbp());
        existingAccount.setChf(dto.getChf());
        existingAccount.setRsd(dto.getRsd());
        
        repo.save(existingAccount);
        return ResponseEntity.ok(convertModelToDto(existingAccount));
    }
	
    @Override
    public BankAccountDto getBankAccountByEmail(String userEmail) {
        
        BankAccountModel model = repo.findByEmail(userEmail);
        if (model == null) {
            return null; 
        }
        return convertModelToDto(model);
    }
	
	private BankAccountDto convertModelToDto(BankAccountModel model) {
		return new BankAccountDto(
			model.getEmail(),
			model.getUsd(),
			model.getEur(),
			model.getGbp(),
			model.getChf(),
			model.getRsd()
		);
	}
}