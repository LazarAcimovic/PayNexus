
package apiGateway.authentication;
//map Koristi se za transformaciju vrednosti unutar Mono/Flux-a.
//Koristi se kada tvoja transformacija već vraća Mono/Flux.

import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

public class AddUserRoleHeaderWebFilter implements org.springframework.web.server.WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext() //Mono<SecurityContext>
                .map(context -> context.getAuthentication()) //posle map: Mono<Authentication>
                .filter(Authentication::isAuthenticated)
                .flatMap(authentication -> {
                    String roles = authentication.getAuthorities().stream()
                            .map(Object::toString)
                            .collect(Collectors.joining(","));
                    
                    String email = authentication.getName(); 
                    
                    ServerWebExchange mutatedExchange = exchange.mutate()
                            .request(exchange.getRequest().mutate() //modifying request
                                    .header("X-User-Roles", roles)
                                    .header("X-User-Email", email) 
                                    .build())
                            .build();

                    return chain.filter(mutatedExchange); //prosleđuje se dalje u lanac filtera
                })
                .switchIfEmpty(chain.filter(exchange));
    }
}