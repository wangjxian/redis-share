package com.wangjxian.redis.share.structure.delayqueue;

import com.alibaba.fastjson.JSON;
import com.wangjxian.redis.share.RedisShareApplicationTests;
import com.wangjxian.redis.share.client.codec.RedisObj;
import com.wangjxian.redis.share.delayqueue.QueueMessage;
import com.wangjxian.redis.share.delayqueue.RedisDelayQueue;
import com.wangjxian.redis.share.structure.Obj;
import java.time.Instant;
import javax.annotation.Resource;
import org.junit.jupiter.api.Test;

/**
 * @author wangjxian
 */
public class DelayQueueTest extends RedisShareApplicationTests {

  @Resource
  private RedisDelayQueue redisDelayQueue;


  @Test
  public void testPush(){

    QueueMessage queueMessage = new QueueMessage();
    queueMessage.setDelayTime(2000L);
    queueMessage.setId(2L);
    queueMessage.setMessage(RedisObj.of(new Obj(2,"redis_delay_queue")));
    redisDelayQueue.put(queueMessage);

  }

  @Test
  public void testPoll(){
    //不断轮询
    while (!Thread.interrupted()){

      RedisObj result;

      if ((result = redisDelayQueue.poll())!=null){
        Object data = result.getData();

        //先拿锁
        //执行业务
        System.out.println("执行任务："+JSON.toJSONString(data)+"当前时间"+ Instant.now().toEpochMilli());
        //释放锁
      }else {
        System.out.println("没有可执行资源，睡眠中");
        try {
          Thread.sleep(1000L);
        } catch (InterruptedException e) {
          e.printStackTrace();
          break;
        }
      }
    }
  }
}
