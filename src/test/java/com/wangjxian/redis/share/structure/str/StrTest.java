package com.wangjxian.redis.share.structure.str;

import com.wangjxian.redis.share.RedisShareApplicationTests;
import com.wangjxian.redis.share.client.codec.RedisObj;
import com.wangjxian.redis.share.client.connection.ByteConnect;
import com.wangjxian.redis.share.client.connection.DefaultStringConnect;
import com.wangjxian.redis.share.client.connection.JsonConnect;
import io.lettuce.core.api.StatefulRedisConnection;
import javax.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

/**
 * @author wangjxian
 */
public class StrTest extends RedisShareApplicationTests {

  @Resource
  private DefaultStringConnect defaultStringConnect;

  @Resource
  private ByteConnect ByteConnect;

  @Resource
  private JsonConnect jsonConnect;

  @Test
  public void testSetStr(){
    StatefulRedisConnection<String, String> connect = defaultStringConnect.getConnect();
    String result = connect.sync().set("test", "test");
    System.out.println(result);
    String test = connect.sync().get("test");
    System.out.println(test);
  }




  @Test
  public void testSetObjJson(){
    StatefulRedisConnection<String, RedisObj> connect = jsonConnect.getConnect();

    String result = connect.sync().set("test_json", RedisObj.of(new Obj(1, "obj")));
    System.out.println(result);
    Object test = connect.sync().get("test_json");
    System.out.println(test);
  }


  @Test
  public void testSetObj(){
    StatefulRedisConnection<String, RedisObj> connect = ByteConnect.getConnect();

    String result = connect.sync().set("test_obj", RedisObj.of(new Obj(1, "obj")));
    System.out.println(result);
    Object test = connect.sync().get("test_obj");
    System.out.println(test);
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Obj{
    private int id;
    private String name;
  }

}
