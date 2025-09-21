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
import api.dtos.UserDto;
import api.proxies.UserProxy;
import api.services.BankAccountService;
import util.exceptions.ConflictException;
import util.exceptions.NoDataFoundException;


@RequestMapping("/bank-accounts")
@RestController
public class BankAccountServiceImpl implements BankAccountService {
	//
	@Autowired
	private BankAccountRepository repo;
	
    @Autowired
    private UserProxy usersProxy;
	
    @Override
    public ResponseEntity<?> createBankAccount(BankAccountDto dto) {
        

        ResponseEntity<UserDto> userResponse = usersProxy.getUserByEmail(dto.getEmail());
        
        if (!userResponse.getStatusCode().is2xxSuccessful() || userResponse.getBody() == null) {
            throw new NoDataFoundException("User with email " + dto.getEmail() + " not found.");
        }

        if (repo.findByEmail(dto.getEmail()) != null) {
            throw new ConflictException("Bank account for this user already exists.");
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
			throw new NoDataFoundException("Bank account for this user does not exist.", null);
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
        	throw new NoDataFoundException("Bank account for user with email " + dto.getEmail() + " does not exist.", null);
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
        	throw new NoDataFoundException("Bank account not found for user: " + userEmail, null);
        }
        return convertModelToDto(model);
    }
    
    @Override
    public ResponseEntity<?> updateUserBankAccountByEmail(BankAccountDto dto, String userEmail) {
    	// Check if the email in the DTO matches the email from the header
        if (!dto.getEmail().equals(userEmail)) {
        	throw new RuntimeException("You can only update your own bank account.");
        }

        BankAccountModel existingAccount = repo.findByEmail(dto.getEmail());
        if (existingAccount == null) {
        	throw new NoDataFoundException("Bank account for user with email " + dto.getEmail() + " does not exist.", null);
        }

        existingAccount.setUsd(dto.getUsd());
        existingAccount.setEur(dto.getEur());
        existingAccount.setGbp(dto.getGbp());
        existingAccount.setChf(dto.getChf());
        existingAccount.setRsd(dto.getRsd());

        repo.save(existingAccount);
        return ResponseEntity.ok(convertModelToDto(existingAccount));
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