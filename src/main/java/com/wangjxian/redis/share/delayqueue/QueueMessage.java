package com.wangjxian.redis.share.delayqueue;

import com.wangjxian.redis.share.client.codec.RedisObj;
import lombok.Data;

/**
 * @author wangjxian
 */
@Data
public class QueueMessage {

  //消息
  private RedisObj message;
  //id
  private Long id;
  //延迟时间
  private Long delayTime;
}
