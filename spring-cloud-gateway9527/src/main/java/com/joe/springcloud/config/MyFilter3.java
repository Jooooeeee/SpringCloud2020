package com.joe.springcloud.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Component
public class MyFilter3 implements GlobalFilter, Ordered {

    /**
     * 具体过滤逻辑
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println(" 第一个过滤器 ");
        String uname =(String) exchange.getRequest().getQueryParams().getFirst("uname");
        if (uname == null) {
            System.out.println(" 没通过过滤 ");
            exchange.getResponse().setStatusCode(HttpStatus.NOT_ACCEPTABLE);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

    /**
     * order 越小，越先运行，如果order一样时，根据文件顺序运行。
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
