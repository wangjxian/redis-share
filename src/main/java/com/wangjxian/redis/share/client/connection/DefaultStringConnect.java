package com.wangjxian.redis.share.client.connection;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import lombok.Getter;


/**
 * @author wangjxian
 * 默认的使用string序列化方案的redis连接
 */
public class DefaultStringConnect {


  @Getter
  private StatefulRedisConnection<String, String> connect;

  public DefaultStringConnect(RedisClient redisClient) {
    this.connect = redisClient.connect();
  }
}
