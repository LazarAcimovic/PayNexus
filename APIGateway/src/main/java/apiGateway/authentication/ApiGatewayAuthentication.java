package apiGateway.authentication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.client.WebClient;

import api.dtos.UserDto;

@Configuration
@EnableWebFluxSecurity
public class ApiGatewayAuthentication {
	
	@Bean
	SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
		http
		.csrf(csrf -> csrf.disable())
		.authorizeExchange(exchange -> exchange
				.pathMatchers("/bank-accounts/getAllBankAccounts").hasRole("ADMIN")
				.pathMatchers("/bank-accounts/email").hasRole("USER") 
				.pathMatchers("/bank-accounts/delete").hasRole("USER") 
				.pathMatchers("/bank-accounts/new").hasRole("ADMIN")
				.pathMatchers("/bank-accounts/update").hasRole("ADMIN")
				.pathMatchers("/bank-accounts/update/user").hasRole("USER")
				
				.pathMatchers(HttpMethod.POST, "/users/newOwner").permitAll()
				.pathMatchers(HttpMethod.POST, "/users/newAdmin").hasRole("OWNER")
				.pathMatchers(HttpMethod.POST, "/users/newUser").hasAnyRole("OWNER", "ADMIN")
				.pathMatchers(HttpMethod.DELETE, "/users").hasRole("OWNER")
				.pathMatchers(HttpMethod.PUT, "/users").hasAnyRole("OWNER", "ADMIN")
				.pathMatchers(HttpMethod.GET, "/users").hasAnyRole("OWNER", "ADMIN")

				.pathMatchers("/currency-exchange").permitAll()
				.pathMatchers("/currency-conversion-feign").hasRole("USER")
				.pathMatchers("/trade-service").hasRole("USER")
				
				
				
				
				
				
				
		)
		.addFilterAfter(new AddUserRoleHeaderWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
		.httpBasic(Customizer.withDefaults());
		
		return http.build();
	}
	
	@Bean
	ReactiveUserDetailsService reactiveUserDetailsService(WebClient.Builder webClientBuilder, BCryptPasswordEncoder encoder) {
	    WebClient client = webClientBuilder.baseUrl("http://localhost:8770").build();
	    
	    return user -> client.get()
	            .uri(uriBuilder -> uriBuilder
	                    .path("/users/email")
	                    .queryParam("email", user)
	                    .build()
	            )
	            .retrieve()
	            .bodyToMono(UserDto.class)
	            .map(dto -> User.withUsername(dto.getEmail())
	                    .password(encoder.encode(dto.getPassword())) // zatvorena zagrada ovde
	                    .roles(dto.getRole())
	                    .build()
	            );
	}

	
	@Bean
	BCryptPasswordEncoder getEncoder() {
		return new BCryptPasswordEncoder();
	}
	

}