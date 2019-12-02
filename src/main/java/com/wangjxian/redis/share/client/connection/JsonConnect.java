package com.wangjxian.redis.share.client.connection;

import com.wangjxian.redis.share.client.codec.JsonCodec;
import com.wangjxian.redis.share.client.codec.RedisObj;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import lombok.Getter;

/**
 * @author wangjxian
 * 自定义的对象json序列化方案的redis连接
 */
public class JsonConnect {


  @Getter
  private StatefulRedisConnection<String, RedisObj> connect;

  public JsonConnect(RedisClient redisClient) {
    this.connect = redisClient.connect(new JsonCodec());
  }

}
