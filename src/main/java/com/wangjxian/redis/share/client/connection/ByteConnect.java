package com.wangjxian.redis.share.client.connection;

import com.wangjxian.redis.share.client.codec.ByteCodec;
import com.wangjxian.redis.share.client.codec.RedisObj;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import lombok.Getter;

/**
 * @author wangjxian
 * 自定义的对象字节序列化方案的redis连接
 */
public class ByteConnect {

  @Getter
  private StatefulRedisConnection<String, RedisObj> connect;

  public ByteConnect(RedisClient redisClient) {
    this.connect = redisClient.connect(new ByteCodec());
  }

}
