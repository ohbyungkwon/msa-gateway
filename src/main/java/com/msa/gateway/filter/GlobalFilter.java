package com.msa.gateway.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {
    public GlobalFilter(){
        super(Config.class);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Config{
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }

    //주로 JWT 같은 인증, 인가 처리 담당
    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            //Pre Filter Area
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("GLOBAL PRE FILTER AREA - baseMessage: {}", config.getBaseMessage());

            if(config.isPreLogger())
                log.info("GLOBAL PRE FILTER AREA - requestID: {}", request.getId());

            //Post Filter Area
            //기존 chain에 익명 filter를 추가하여 후처리 진행
            //Mono - 비동기 방식 단일값 전달(WebFlux)
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if(config.isPostLogger())
                    log.info("GLOBAL POST FILTER AREA - response status: {}", response.getStatusCode());
            }));
        });
    }
}

