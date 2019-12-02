package com.wangjxian.redis.share;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.wangjxian.redis.share")
public class RedisShareApplication {

  public static void main(String[] args) {
    SpringApplication.run(RedisShareApplication.class, args);
  }

}
