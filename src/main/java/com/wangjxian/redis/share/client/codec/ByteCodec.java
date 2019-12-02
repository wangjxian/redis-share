package com.wangjxian.redis.share.client.codec;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.nio.ByteBuffer;

/**
 * 二进制byte编解码器
 * @author wangjiaxian
 */
public class ByteCodec extends StringKeyCodec {
    private static ThreadLocal<LinkedBuffer> cacheBuffer = ThreadLocal.withInitial(() -> LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
    private static Schema<RedisObj> schema = RuntimeSchema.getSchema(RedisObj.class);
    private static byte[] EMPTY = new byte[0];

    @Override
    public RedisObj decodeValue(ByteBuffer byteBuffer) {
        int remaining = byteBuffer.remaining();

        if (remaining == 0) {
            return null;
        }

        byte[] data = new byte[remaining];
        byteBuffer.get(data);
        RedisObj obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(data, obj, schema);
        return obj;
    }


    @Override
    public ByteBuffer encodeValue(RedisObj value) {
        if (value == null) {
            return ByteBuffer.wrap(EMPTY);
        }
        LinkedBuffer  linkedBuffer = cacheBuffer.get().clear();
        byte[] bytes = ProtostuffIOUtil.toByteArray(value, schema, linkedBuffer);
        return getByteBuffer(bytes);
    }


}
