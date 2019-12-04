package com.wangjxian.redis.share.lock;


import com.wangjxian.redis.share.client.codec.RedisObj;
import com.wangjxian.redis.share.client.connection.JsonConnect;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.SetArgs;
import io.lettuce.core.SetArgs.Builder;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @description: redis 单机模式下的分布式锁实现
 * @author: wangjxian
 */
@Slf4j
@Component
public class RedisLock {

    private static final String KEY="lock";
    private static final String LOCK_SUCCESS = "OK";
    private static final Long RELEASE_SUCCESS = 1L;
    private static final String LOCK_VALUE = "1";


    private final static String SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    @Resource
    private JsonConnect jsonConnect;


    private RedisCommands<String, RedisObj> redisCommands;


    @PostConstruct
    public void init(){
        StatefulRedisConnection<String, RedisObj> connect = jsonConnect.getConnect();
        this.redisCommands = connect.sync();
    }

    public boolean acquireDistributedLockWithRetry(String id, long timeout){

        SetArgs args = Builder.px(timeout).nx();

        if (acquireDistributedLock(id,timeout)){
            return Boolean.TRUE;
        }

        String key = KEY + id;

        int count = 0;
        //不断重试去获得锁
        while (!Thread.interrupted()) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String result= redisCommands.set(key, RedisObj.of(LOCK_VALUE), args );

            if (!LOCK_SUCCESS.equals(result)){
                count++;
                log.info("try [{}] times to acquire lock for product, id=[{}]...",count,id);
                continue;
            }

            log.info("success to acquire lock for product, id=[{}] ,after try [{}] times...",id,count);
            break;
        }

        return Boolean.FALSE;

    }

    /**
     * 尝试获取分布式锁
     * @return 是否获取成功
     */
    public boolean acquireDistributedLock(String id, long timeout) {

        String key = KEY + id;

        SetArgs args = Builder.px(timeout).nx();

        String result= redisCommands.set(key, RedisObj.of(LOCK_VALUE), args );

        if (LOCK_SUCCESS.equals(result)){
            log.info("success to acquire lock for product, id=[{}]",id);
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
      }

    /**
     * 释放分布式锁
     * @return 是否释放成功
     */
    public  boolean releaseDistributedLock(String id) {
        String key = KEY + id;

        String[] keys = new String[1];
        keys[0] = key;

        try {
            Object result = redisCommands.eval(SCRIPT, ScriptOutputType.VALUE, keys);
            if (RELEASE_SUCCESS.equals(result)) {
                log.info("success to release lock for product, id=[{}]",id);
                return true;
            }
        }catch (Exception ignore){


        }
        return false;
    }
}


