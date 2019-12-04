package com.wangjxian.redis.share.structure.list;

import com.wangjxian.redis.share.RedisShareApplicationTests;
import com.wangjxian.redis.share.client.codec.RedisObj;
import com.wangjxian.redis.share.client.connection.JsonConnect;
import com.wangjxian.redis.share.structure.Obj;
import io.lettuce.core.KeyValue;
import io.lettuce.core.api.sync.RedisCommands;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author wangjxian
 */
@Slf4j
public class ListTest extends RedisShareApplicationTests {

  @Resource
  private JsonConnect jsonConnect;

  private final static String KEY = "test_que";


  @Test
  public void consume(){
    int timeout = 0;//永不超时
    //list集合 第一个元素为key值，第二个元素为弹出的元素值;当超时返回[null]
    System.out.println("system start ...");
    while(!Thread.interrupted()){
      RedisCommands<String, RedisObj> commands = jsonConnect.getConnect().sync();

      try {
        KeyValue<String, RedisObj> blpop = commands.blpop(timeout, KEY);

        //如果线程一直阻塞在哪里，Redis 的客户端连接就成了闲置连接，闲置过久，服务器一般
        //会主动断开连接，减少闲置资源占用。这个时候 blpop/brpop 会抛出异常来。 所以编写客户端消费者的时候要小心，注意捕获异常，还要重试。...

        if (blpop!=null && blpop.getValue()!=null){

          System.out.println("正在取出 "+blpop.getValue().getData());

        }


      }catch (Exception ignore){
        log.info("connect timeout ... ");
      }
    }
  }

  @Test
  public void generateMsg() {

    RedisCommands<String, RedisObj> commands = jsonConnect.getConnect().sync();

    for (int i=0;i<=9;i++){

      commands.lpush(KEY,RedisObj.of(new Obj(i,"test_que_"+i)));

    }
  }

}
