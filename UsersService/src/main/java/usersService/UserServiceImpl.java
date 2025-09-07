package usersService;

import java.util.ArrayList;
import java.util.List;
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
		List<UserModel> models = repo.findAll();
		List<UserDto> dtos = new ArrayList<UserDto>();
		
		for(UserModel model: models) {
			dtos.add(convertModelToDto(model));
		}
		
		return dtos;
	}

	@Override
	public UserDto getUserByEmail(String email) {
		return convertModelToDto(repo.findByEmail(email));
	}

	@Override
	public ResponseEntity<?> createAdmin(UserDto dto) {
		if(repo.findByEmail(dto.getEmail()) == null) {
			dto.setRole("ADMIN");
			UserModel model = convertDtoToModel(dto);
			return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(model));
		} 
		else {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Admin with passed email already exists");
		}
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
	public ResponseEntity<?> updateUser(UserDto dto) {
		if(repo.findByEmail(dto.getEmail()) != null) {
			repo.updateUser(dto.getEmail(), dto.getPassword(), dto.getRole());
			return ResponseEntity.status(HttpStatus.OK).body(dto);
		} 
		else {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Admin with passed email does not exists");
		}
	}
	
	public UserDto convertModelToDto(UserModel user) {
		return new UserDto(user.getEmail(), user.getPassword(), user.getRole());
	}
	
	public UserModel convertDtoToModel(UserDto dto) {
		return new UserModel(dto.getEmail(), dto.getPassword(), dto.getRole());
	}

}