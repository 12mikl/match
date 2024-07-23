package com.back.partnerback.configure;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: 楼兰
 * @CreateTime: 2024-07-15
 * @Description:
 */
@Configuration
@Data
@ConfigurationProperties("spring.redis")
public class RedissonConfig {

    private String host;
    private int port;
    private String password;

    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        String address = String.format("redis://%s:%d", host, port);
        config.useSingleServer().setAddress(address).setDatabase(3).setPassword(password);
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
