package cc.moreluck.quickboot.config;

import jodd.util.StringUtil;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: huangqi
 * @CreateTime: 2025-04-17
 * @Description:
 */
@Configuration
public class RedissonClientConfig {

    @Value("${spring.data.redis.host}")
    private String redisAddress;

    @Value("${spring.data.redis.password}")
    private String redisPassword;


    @Value("${spring.data.redis.port}")
    private String port;

    public final static String adder = "redis://%s:%s";

    @Bean
    RedissonClient redisson() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(String.format(adder, redisAddress, port));
        if (!StringUtil.isBlank(redisPassword)) {
            config.useSingleServer().setPassword(redisPassword);
        }
        return Redisson.create(config);
    }
}
