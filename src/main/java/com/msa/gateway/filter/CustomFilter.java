package com.msa.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config> {
    public CustomFilter(){
        super(Config.class);
    }
    public static class Config{

    }

    //주로 JWT 같은 인증, 인가 처리 담당
    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            //Pre Filter Area
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("PRE FILTER AREA - Request id: {}", request.getId());

            //Post Filter Area
            //기존 chain에 익명 filter를 추가하여 후처리 진행
            //Mono - 비동기 방식 단일값 전달(WebFlux)
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("POST FILTER AREA - Response HttpCode: {}", response.getStatusCode());
            }));
        });
    }
}

