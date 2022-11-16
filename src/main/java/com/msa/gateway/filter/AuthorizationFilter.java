package com.msa.gateway.filter;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.SerializationUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.PrintWriter;
import java.util.Base64;

@Slf4j
@Component
public class AuthorizationFilter extends AbstractGatewayFilterFactory<AuthorizationFilter.Config> {

    public static class Config{

    }
    public AuthorizationFilter(){
        super(AuthorizationFilter.Config.class);
    }


    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            HttpHeaders httpHeaders = request.getHeaders();

            String authContentKey = "AuthContent";
            String key = HttpHeaders.AUTHORIZATION;
            if(!httpHeaders.containsKey(key)){
                return AuthorizationError(exchange, "need token", HttpStatus.UNAUTHORIZED);
            }
            if(!httpHeaders.containsKey(authContentKey)){
                return AuthorizationError(exchange, "need AuthContent", HttpStatus.UNAUTHORIZED);
            }

            String token = httpHeaders.get(key).get(0);
            String compareToken = httpHeaders.get(authContentKey).get(0);
            if(!isValidateToken(token, compareToken)){
                return AuthorizationError(exchange, "check token", HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange);
        });
    }

    private boolean isValidateToken(String token, String compareToken) {
        try {
            Object subject = Jwts.parser().setSigningKey("user-token")
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            if (subject == null) {
                return false;
            }

            byte[] bytes = Base64.getDecoder().decode(compareToken);
            String compare = new String(bytes);
            if (!ObjectUtils.equals(subject, compare)) {
                return false;
            }
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //비동기 통신 지원
    //Mono, Flux -> Spring5.0 up version(WebFlux)
    private Mono<Void> AuthorizationError(ServerWebExchange exchange, String errMsg, HttpStatus errCode) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(errCode);

        return response.setComplete();
    }
}
