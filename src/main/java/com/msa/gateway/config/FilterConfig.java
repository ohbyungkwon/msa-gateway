//package com.msa.gateway.config;
//
//import com.msa.gateway.filter.CustomFilter;
//import com.msa.gateway.filter.LoggingFilter;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * spring:
// *   cloud:
// *     gateway:
// *       routes:
// *         - id: userService-1
// *           uri: http://localhost:9001
// *           predicates:
// *             - Path=/first-server/**
// *           filters:
// *             - AddRequestHeader=req-header, first-req-header
// *             - AddResponseHeader=res-header, first-res-header
// *             - CustomFilter
// *         - id: userService-2
// *           uri: http://localhost:9002
// *           predicates:
// *             - Path=/second-server/**
// *             - AddRequestHeader=req-header, second-req-header
// *             - AddResponseHeader=res-header, second-res-header
// *             - CustomFilter
// *             - LoggingFilter
// */
//@Configuration
//public class FilterConfig {
//    private CustomFilter customFilter;
//    private LoggingFilter loggigFilter;
//
//    @Autowired
//    public FilterConfig(CustomFilter customFilter, LoggingFilter loggigFilter) {
//        this.customFilter = customFilter;
//        this.loggigFilter = loggigFilter;
//    }
//
//    @Bean
//    public RouteLocator gatewayRouteLocator(RouteLocatorBuilder builder){
//        return builder.routes()
//                .route("userService", r -> r.path("/user-service/**").uri("lb//USERMICROSERVICE"))
//                .route("userService-1", r -> r.path("/first-server/**")
//                        .filters(f -> f.addRequestHeader("req-header", "first-req-header")
//                                .addResponseHeader("res-header", "first-res-header")
//                                .filter((GatewayFilter) customFilter))
//                        .uri("lb//USER-SERVICE-1"))
//                .route("userService-2", r -> r.path("/second-server/**")
//                        .filters(f -> f.addRequestHeader("req-header", "second-req-header")
//                                .addResponseHeader("res-header", "second-res-header")
//                                .filter((GatewayFilter) customFilter)
//                                .filter((GatewayFilter) loggigFilter))
//                        .uri("lb//USER-SERVICE-2"))
//                        //http://localhost:9002 사용하면 단순 라우팅
//                        //(lb//spring.application.name) 사용하면 load balance(user-service-2 서버가 2개일 경우 유용)
//                .build();
//    }
//}
