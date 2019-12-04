package com.wangjxian.redis.share.delayqueue;

import com.wangjxian.redis.share.client.codec.RedisObj;
import com.wangjxian.redis.share.client.connection.JsonConnect;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @description: redis实现的简单的延迟队列
 * @author: wangjxian
 */
@Slf4j
@Component
public class RedisDelayQueue {

  private static final String KEY = "redis-delay-queue";

  @Resource
  private JsonConnect jsonConnect;


  private RedisCommands<String, RedisObj> redisCommands;


  @PostConstruct
  public void init(){
    StatefulRedisConnection<String, RedisObj> connect = jsonConnect.getConnect();
    this.redisCommands = connect.sync();
  }

  /* lua脚本，保证ZRANGEBYSCORE和ZREM的原子性 */
  private static final String SCRIPT =
      "local table = redis.call('ZRANGEBYSCORE', KEYS[1], KEYS[2], KEYS[3]);\n" +
          "\n" +
          "local key = table[1];\n" +
          "\n" +
          "if key == nil then\n" +
          "\treturn nil;\n" +
          "else\n" +
          "\tredis.call('ZREM', KEYS[1], key);\n" +
          "\treturn key;\n" +
          "end";

  /**
   * 向延迟队列中添加元素
   *
   * @return
   * @param: message
   * @param: delayTime
   * @param: unit
   */
  public void put(QueueMessage queueMessage) {
    long timeout = TimeUnit.MILLISECONDS
        .convert(queueMessage.getDelayTime(), TimeUnit.MILLISECONDS);
    redisCommands.zadd(KEY, Instant.now().toEpochMilli() + timeout,
        queueMessage.getMessage());
    log.info("已向延迟队列中，添加 任务{} 当前时间 current[{}]", queueMessage,Instant.now().toEpochMilli());
  }

  /**
   * 取出延迟队列中元素
   *
   * @return
   * @param:
   */
  public RedisObj poll() {
    String[] keys = new String[3];
    keys[0] = KEY;
    keys[1] = "0";
    keys[2] = String.valueOf(Instant.now().toEpochMilli());
    return redisCommands.eval(SCRIPT, ScriptOutputType.VALUE, keys);
  }

}
