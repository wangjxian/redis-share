package com.wangjxian.redis.share.client.codec;

import lombok.Data;

/**
 * @author wangjxian
 */
@Data
public class RedisObj {

  private Object data;


  public static RedisObj of(Object obj){
    RedisObj redisObj = new RedisObj();
    redisObj.setData(obj);
    return redisObj;
  }

}
