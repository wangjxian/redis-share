package com.wangjxian.redis.share.client;

import com.wangjxian.redis.share.client.connection.DefaultStringConnect;
import com.wangjxian.redis.share.client.connection.JsonConnect;
import com.wangjxian.redis.share.client.connection.ByteConnect;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Value("${redis.database}")
  private Integer dataBase;

  @Value("${redis.port}")
  private Integer port;

  @Value("${redis.timeout}")
  private Integer timeout;


  @Bean
  public RedisClient redisClient(){
    RedisURI uri = RedisURI
        .builder()
        //连接地址
        .withHost(redisUrl)
        //默认库
        .withDatabase(dataBase)
        //默认端口
        .withPort(port)
        //默认超时时间
        .withTimeout(Duration.of(timeout, ChronoUnit.SECONDS))
        //密码
        //.withPassword("")
        .build();
    return RedisClient.create(uri);
  }

  @Bean
  public DefaultStringConnect defaultStringConnect(@Autowired RedisClient redisClient){
    return new DefaultStringConnect(redisClient);
  }

  @Bean
  public ByteConnect objectConnect(@Autowired RedisClient redisClient){
    return new ByteConnect(redisClient);
  }

  @Bean
  public JsonConnect jsonCodec(@Autowired RedisClient redisClient){
    return new JsonConnect(redisClient);
  }

}
