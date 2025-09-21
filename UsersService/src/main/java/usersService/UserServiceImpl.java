package usersService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import api.dtos.BankAccountDto;
import api.dtos.CryptoWalletDto;
import api.dtos.UserDto;
import api.proxies.BankAccountProxy;
import api.proxies.CryptoWalletProxy;
import api.services.UsersService;
import util.exceptions.ConflictException;
import util.exceptions.ForbiddenOperationException;
import util.exceptions.NoDataFoundException;



@RestController

public class UserServiceImpl implements UsersService{
	
	@Autowired
	private UserRepository repo;
	
    @Autowired
    private BankAccountProxy bankAccountProxy;
    
    @Autowired
    private CryptoWalletProxy cryptoWalletProxy;

	@Override
	public List<UserDto> getUsers() {
	    List<UserModel> listOfModels = repo.findAll();
	    ArrayList<UserDto> listOfDtos = new ArrayList<UserDto>();
	    for(UserModel model: listOfModels) {
	        listOfDtos.add(convertModelToDto(model));
	    }
	    return listOfDtos;
	}

	@Override
	public UserDto getUserByEmail(String email) {
        UserModel user = repo.findByEmail(email);
        if (user == null) {
            throw new NoDataFoundException("User with email " + email + " not found.");
        }
		return convertModelToDto(user);
	}
	
    @Override										
    public ResponseEntity<?> createOwner(UserDto dto) {
        if (repo.findByEmail(dto.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Owner with passed email already exists");
        }
        
        if (repo.findAll().stream().anyMatch(user -> "OWNER".equals(user.getRole()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only one OWNER can exist in the system.");
        }
        
        dto.setRole("OWNER");
        UserModel model = convertDtoToModel(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(model));
    }
    

    @Override
    public ResponseEntity<?> createAdmin(UserDto dto) {
        if (repo.findByEmail(dto.getEmail()) != null) {
        	 throw new ConflictException("Admin with passed email already exists.");
        }
        
        dto.setRole("ADMIN");
        UserModel model = convertDtoToModel(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(model));
    }
    
    @Override
    public ResponseEntity<?> createUser(UserDto dto) {
        if (repo.findByEmail(dto.getEmail()) == null) {
            dto.setRole("USER");
            UserModel model = convertDtoToModel(dto);
            UserModel savedUser = repo.save(model);
            
            if ("USER".equals(savedUser.getRole())) {
                BankAccountDto accountDto = new BankAccountDto(savedUser.getEmail());
                ResponseEntity<?> bankAccountResponse = bankAccountProxy.createBankAccount(accountDto);

                if (bankAccountResponse.getStatusCode().is2xxSuccessful()) {
                    CryptoWalletDto walletDto = new CryptoWalletDto(
                            savedUser.getEmail(),
                            BigDecimal.ZERO.setScale(8),
                            BigDecimal.ZERO.setScale(8),
                            BigDecimal.ZERO.setScale(8),
                            BigDecimal.ZERO.setScale(8)
                        );
                    ResponseEntity<?> walletResponse = cryptoWalletProxy.createCryptoWallet(walletDto);
                    
                    if (!walletResponse.getStatusCode().is2xxSuccessful()) {
                    	// If creating the wallet fails, we delete both the bank account and the user
                        bankAccountProxy.deleteBankAccount(savedUser.getEmail());
                        repo.delete(savedUser);
                        throw new RuntimeException("User created, but failed to create crypto wallet. User and bank account deleted.");
                    }
                    return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
                } else {
                	// If creating the bank account fails, we only delete the user
                    repo.delete(savedUser); 
                    throw new RuntimeException("User created, but failed to create bank account. User deleted.");
                }
            }
            

            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } 
        else {
        	throw new ConflictException("User with passed email already exists.");
        }
    }

	@Override
	public ResponseEntity<?> updateUser(UserDto dto, String roles) {
	    UserModel userToUpdate = repo.findByEmail(dto.getEmail());
	    if (userToUpdate == null) {
	        throw new NoDataFoundException("User with passed email does not exist.");
	    }

	  
	    boolean isOwner = roles.contains("ROLE_OWNER");
	    boolean isAdmin = roles.contains("ROLE_ADMIN");

	 
	    if (isOwner) {
	        repo.updateUser(dto.getEmail(), dto.getPassword(), dto.getRole());
	        return ResponseEntity.status(HttpStatus.OK).body(dto);
	    } 
	  
	    else if (isAdmin) {
	        if ("USER".equals(userToUpdate.getRole())) {
	        	
	            if (dto.getEmail().contains("owner")) {
	                throw new ForbiddenOperationException("Admin cannot update users with 'owner' in their email.");
	            }
	        	
	            dto.setRole(userToUpdate.getRole());
	            repo.updateUser(dto.getEmail(), dto.getPassword(), dto.getRole());
	            return ResponseEntity.status(HttpStatus.OK).body(dto);
	        } else {
	        	throw new ForbiddenOperationException("Admin cannot update users with role " + userToUpdate.getRole());
	        }
	    }
	    
	    // if some other role is present...
	    throw new ForbiddenOperationException("You do not have permission to update this user.");
	}
	
    @Override
    public ResponseEntity<?> deleteUser(String email) {
        UserModel user = repo.findByEmail(email);
        if (user == null) {
        	 throw new NoDataFoundException("User with email " + email + " does not exist.");
        }
        
        if ("USER".equals(user.getRole())) {
            // attempt to delete casual bank account
            ResponseEntity<?> bankAccountResponse = bankAccountProxy.deleteBankAccount(user.getEmail());
            if (!bankAccountResponse.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to delete user's bank account. User was not deleted.");
            }

            // attempt to delete crypto bank account
            ResponseEntity<?> cryptoWalletResponse = cryptoWalletProxy.deleteCryptoWallet(user.getEmail());
            if (!cryptoWalletResponse.getStatusCode().is2xxSuccessful()) {
            	 throw new RuntimeException("Failed to delete user's crypto wallet. User was not deleted.");
            }
        }
        
        repo.delete(user);
        return ResponseEntity.status(HttpStatus.OK).body("User " + email + " successfully deleted.");
    }
	
	public UserDto convertModelToDto(UserModel user) {
		return new UserDto(user.getEmail(), user.getPassword(), user.getRole());
	}
	
	public UserModel convertDtoToModel(UserDto dto) {
		return new UserModel(dto.getEmail(), dto.getPassword(), dto.getRole());
	}
	
	

}