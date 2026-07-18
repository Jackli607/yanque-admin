package cn.edu.commons.redis;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.StandardCharsets;

/**
 * Redis配置类 —— 自定义Key/Value序列化策略
 */
@Configuration
public class RedisConfig {

    /**
     * 注入RedisTemplate
     * Key  → String序列化（可读性好）
     * Value → FastJSON2序列化（自动JSON转换）
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key序列化：String
        RedisSerializer<String> stringSerializer = RedisSerializer.string();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // Value序列化：FastJSON2
        FastJson2RedisSerializer<Object> jsonSerializer =
                new FastJson2RedisSerializer<>(Object.class);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 注入StringRedisTemplate —— 纯字符串场景用
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    /**
     * FastJSON2 序列化器
     */
    static class FastJson2RedisSerializer<T> implements RedisSerializer<T> {
        private final Class<T> clazz;

        FastJson2RedisSerializer(Class<T> clazz) {
            this.clazz = clazz;
        }

        @Override
        public byte[] serialize(T t) throws SerializationException {
            if (t == null) {
                return new byte[0];
            }
            // 写入类名，反序列化时可自动识别类型
            return JSON.toJSONString(t, JSONWriter.Feature.WriteClassName)
                    .getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public T deserialize(byte[] bytes) throws SerializationException {
            if (bytes == null || bytes.length == 0) {
                return null;
            }
            // 根据序列化时写入的类名自动推断类型
            return JSON.parseObject(new String(bytes, StandardCharsets.UTF_8),
                    clazz, JSONReader.Feature.SupportAutoType);
        }
    }
}
