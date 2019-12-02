package com.wangjxian.redis.share.client;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wangjxian
 */
@Configuration
public class DefaultRedisClientConfiguration {

  @Value("${redis.url}")
  private String redisUrl;

  @Bean
  public RedisClient redisClient(){
    RedisURI uri = RedisURI
        .builder()
        //连接地址
        .withHost(redisUrl)
        //默认库
        .withDatabase(0)
        //默认端口
        .withPort(6379)
        //默认超时时间
        .withTimeout(Duration.of(60, ChronoUnit.SECONDS))
        //密码
        //.withPassword("")
        .build();
    return RedisClient.create(uri);
  }





}
