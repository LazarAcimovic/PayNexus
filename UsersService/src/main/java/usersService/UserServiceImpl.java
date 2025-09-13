package usersService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RestController;

import api.dtos.UserDto;
import api.services.UsersService;



@RestController
public class UserServiceImpl implements UsersService{
	
	@Autowired
	private UserRepository repo;

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
		return convertModelToDto(repo.findByEmail(email));
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
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Admin with passed email already exists");
        }
        
        dto.setRole("ADMIN");
        UserModel model = convertDtoToModel(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(model));
    }
    
	@Override
	public ResponseEntity<?> createUser(UserDto dto) {
		if(repo.findByEmail(dto.getEmail()) == null) {
			dto.setRole("USER");
			UserModel model = convertDtoToModel(dto);
			return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(model));
		} 
		else {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("User with passed email already exists");
		}
	}

	@Override
	public ResponseEntity<?> updateUser(UserDto dto, String roles) {
	    UserModel userToUpdate = repo.findByEmail(dto.getEmail());
	    if (userToUpdate == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with passed email does not exist.");
	    }

	  
	    boolean isOwner = roles.contains("ROLE_OWNER");
	    boolean isAdmin = roles.contains("ROLE_ADMIN");

	 
	    if (isOwner) {
	        repo.updateUser(dto.getEmail(), dto.getPassword(), dto.getRole());
	        return ResponseEntity.status(HttpStatus.OK).body(dto);
	    } 
	  
	    else if (isAdmin) {
	        if ("USER".equals(userToUpdate.getRole())) {
	            //admin cannot change the role!
	            dto.setRole(userToUpdate.getRole());
	            repo.updateUser(dto.getEmail(), dto.getPassword(), dto.getRole());
	            return ResponseEntity.status(HttpStatus.OK).body(dto);
	        } else {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin cannot update users with role " + userToUpdate.getRole());
	        }
	    }
	    
	    // if some other role is present...
	    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to update this user.");
	}
	
    @Override
    public ResponseEntity<?> deleteUser(String email) {
        UserModel userToDelete = repo.findByEmail(email);
        if (userToDelete == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with passed email does not exist.");
        }
        if ("OWNER".equals(userToDelete.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("OWNER cannot be deleted.");
        }

        repo.deleteByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body("User " + email + " successfully deleted.");
    }
	
	public UserDto convertModelToDto(UserModel user) {
		return new UserDto(user.getEmail(), user.getPassword(), user.getRole());
	}
	
	public UserModel convertDtoToModel(UserDto dto) {
		return new UserModel(dto.getEmail(), dto.getPassword(), dto.getRole());
	}
	
	

}