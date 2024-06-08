package com.hmall.cart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author： liwb
 * @Date： 2024/6/8 14:23
 * @Describe：
 */
@Data
@Component
@ConfigurationProperties(prefix = "hm.cart")
public class CartProperties {

    private Integer maxItems;
}
