package com.kunlun.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author by hmy
 * @version <0.1>
 * @created on 2018-01-05.
 */
@Configuration
public class FeignConfig {

    @Bean
    Logger.Level feignLog(){
        return Logger.Level.HEADERS;
    }
}
