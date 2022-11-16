package com.msa.gateway.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Component
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {
    public LoggingFilter(){
        super(Config.class);
    }

    public static class Config{
    }

    @Override
    public GatewayFilter apply(Config config) {
        //exchange: 비동식 방식의 request, response 기능을 제공(동기 방식과 다름)
        //chain: filter chain으로 필터 연결 집합
        return new OrderedGatewayFilter((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();//HttpServletRequest 와 유사
            ServerHttpResponse response = exchange.getResponse();

            HttpMethod method = Optional.ofNullable(request.getMethod()).orElse(HttpMethod.GET);
            log.info("LOGGING PRE FILTER AREA - HEADER: {}", request.getHeaders());
            log.info("LOGGING PRE FILTER AREA - METHOD: {}", request.getMethod());
            if(method.equals(HttpMethod.GET)){
                log.info("LOGGING PRE FILTER AREA - PARAM: {}", request.getQueryParams());
            } else {
                log.info("LOGGING PRE FILTER AREA - BODY: {}", request.getBody());
            }
            log.info("LOGGING PRE FILTER AREA - URI: {}", request.getURI());
            log.info("LOGGING PRE FILTER AREA - PATH: {}", request.getPath());

            //Post Filter Area
            //기존 chain에 익명 filter를 추가하여 후처리 진행
            //Mono - 비동기 방식 단일값 전달(WebFlux)
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("LOGGING POST FILTER AREA - response header: {}", response.getHeaders());
                log.info("LOGGING POST FILTER AREA - response status: {}", response.getStatusCode());
            }));
        }, Ordered.LOWEST_PRECEDENCE);
    }
}

