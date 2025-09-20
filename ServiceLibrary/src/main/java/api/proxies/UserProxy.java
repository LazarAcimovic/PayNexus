package api.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;

import api.dtos.UserDto;

@FeignClient("users-service") 
public interface UserProxy {

    @GetMapping("/users/email")
    ResponseEntity<UserDto> getUserByEmail(@RequestParam String email);
}