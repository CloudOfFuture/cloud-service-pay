package com.kunlun.config;

import feign.Request;
import feign.Retryer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * @author by kunlun
 * @version <0.1>
 * @created on 2017/12/21.
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


    @Bean
    Request.Options feignOptions(){
        return new Request.Options(/***connectTimeoutMills***/10 * 1000,/***readTimeoutMills***/10 * 1000);
    }

    @Bean
    Retryer feignRetryer(){
        return Retryer.NEVER_RETRY;
    }

    /**
     * 消息转换
     *
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        converters.add(mappingJackson2HttpMessageConverter);
    }

    /**
     * 解决跨域问题
     *
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*");
        super.addCorsMappings(registry);
    }
    
}
