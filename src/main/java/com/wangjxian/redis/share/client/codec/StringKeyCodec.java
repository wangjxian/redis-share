package com.wangjxian.redis.share.client.codec;

import io.lettuce.core.codec.RedisCodec;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author wangjxian
 */
public class StringKeyCodec implements RedisCodec<String, RedisObj> {

  protected final Charset charset = StandardCharsets.UTF_8;
  protected static byte[] EMPTY = new byte[0];

  @Override
  public String decodeKey(ByteBuffer bytes) {
    return Unpooled.wrappedBuffer(bytes).toString(this.charset);
  }


  @Override
  public RedisObj decodeValue(ByteBuffer bytes) {
    return null;
  }



  @Override
  public ByteBuffer encodeKey(String key) {
    if (key == null) {
      return ByteBuffer.wrap(EMPTY);
    } else {
      CharsetEncoder encoder = CharsetUtil.encoder(this.charset);
      ByteBuffer buffer = ByteBuffer.allocate((int) (encoder.maxBytesPerChar() * (float) key.length()));
      ByteBuf byteBuf = Unpooled.wrappedBuffer(buffer);
      byteBuf.clear();
      ByteBufUtil.writeUtf8(byteBuf, key);
      buffer.limit(byteBuf.writerIndex());
      return buffer;
    }
  }

  @Override
  public ByteBuffer encodeValue(RedisObj value) {
    return null;
  }


  protected ByteBuffer getByteBuffer(byte[] bytes) {
    ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
    ByteBuf byteBuf = Unpooled.wrappedBuffer(buffer);
    byteBuf.clear();
    byteBuf.writeBytes(bytes);
    buffer.limit(byteBuf.writerIndex());
    return buffer;
  }
}
