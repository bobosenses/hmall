package com.hmall.api.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;

/**
 * @Author： liwb
 * @Date： 2024/6/5 1:24
 * @Describe：
 */
public class DefaultFeignConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
