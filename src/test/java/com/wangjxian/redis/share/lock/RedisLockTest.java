package com.wangjxian.redis.share.lock;

import com.wangjxian.redis.share.RedisShareApplicationTests;
import java.util.UUID;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author wangjxian
 */
@Slf4j
public class RedisLockTest extends RedisShareApplicationTests {


  private  int n  =10;

  @Resource
  private RedisLock redisLock;

  @Test
  public void getLock() {
    boolean result = redisLock.acquireDistributedLock("1",100000);
    System.out.println(result);
  }

  @Test
  public void releaseLock() {
    boolean result = redisLock.releaseDistributedLock("1");
    System.out.println(result);
  }


  @Test
  public void test() throws InterruptedException {

    for (int i=0 ; i < 2 ;i++){
      Thread thread = new Thread(() -> {

        if (redisLock.acquireDistributedLockWithRetry("1",2000)) {
          log.info("线程{}抢到了锁=======", Thread.currentThread().getId());
          log.info("======业务执行中======= {}",n--);
          redisLock.releaseDistributedLock("1");
        } else {
          log.info("线程{}没有抢到锁，等待中。。。。", Thread.currentThread().getId());
        }

      });

      log.info("current runnable thread name:[{}th task,{}]",i,thread.getName());
      log.info("======业务执行中======= {}",n--);
      thread.start();
      thread.join();

    }
  }
}
