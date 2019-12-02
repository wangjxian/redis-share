package com.wangjxian.redis.share.client.codec;

import com.alibaba.fastjson.JSON;
import java.nio.ByteBuffer;

/**
 * fast json编解码器
 * @author wangjxian
 */
public class JsonCodec extends StringKeyCodec {


  @Override
  public RedisObj decodeValue(ByteBuffer bytes) {
    int remaining = bytes.remaining();

    if (remaining == 0) {
      return null;
    }

    String string = charset.decode(bytes).toString();

    return JSON.parseObject(string, RedisObj.class);
  }


  @Override
  public ByteBuffer encodeValue(RedisObj value) {
    if (value == null) {
      return ByteBuffer.wrap(EMPTY);
    }
    byte[] bytes = JSON.toJSONString(value).getBytes();
    return getByteBuffer(bytes);
  }
}
