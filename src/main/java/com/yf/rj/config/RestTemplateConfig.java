package com.yf.rj.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate(SimpleClientHttpRequestFactory simpleFactory) {
        return new RestTemplate(simpleFactory);
    }

    @Bean
    public RestTemplate proxyTemplate(SimpleClientHttpRequestFactory proxyFactory) {
        //TODO 连接池
        return new RestTemplate(proxyFactory);
    }

    @Bean
    public SimpleClientHttpRequestFactory simpleFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        return factory;
    }

    @Bean
    public SimpleClientHttpRequestFactory proxyFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        factory.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7890)));
        return factory;
    }
}