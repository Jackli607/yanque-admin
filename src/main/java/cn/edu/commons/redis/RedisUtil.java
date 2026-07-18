package cn.edu.commons.redis;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类 —— 封装常用操作（String / Hash / List / Set / ZSet）
 */
@Component
public class RedisUtil {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // ==================== Key 操作 ====================

    /** 判断key是否存在 */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /** 设置过期时间（秒） */
    public Boolean expire(String key, long timeout) {
        return redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    /** 设置过期时间，指定时间单位 */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /** 获取剩余过期时间（秒），-1 永久，-2 不存在 */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /** 删除key */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /** 批量删除key */
    public Long delete(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    // ==================== String 操作 ====================

    /** 设置值 */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /** 设置值并指定过期时间（秒） */
    public void set(String key, Object value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /** 设置值并指定过期时间和时间单位 */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /** 获取值 */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) redisTemplate.opsForValue().get(key);
    }

    /** 递增 */
    public Long incr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /** 递减 */
    public Long decr(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    // ==================== Hash 操作 ====================

    /** Hash 设置字段 */
    public void hSet(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    /** Hash 批量设置 */
    public void hSetAll(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /** Hash 获取字段 */
    @SuppressWarnings("unchecked")
    public <T> T hGet(String key, String field) {
        return (T) redisTemplate.opsForHash().get(key, field);
    }

    /** Hash 获取全部字段 */
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /** Hash 删除字段 */
    public Long hDelete(String key, Object... fields) {
        return redisTemplate.opsForHash().delete(key, fields);
    }

    /** Hash 判断字段是否存在 */
    public Boolean hHasKey(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    // ==================== List 操作 ====================

    /** List 左端插入 */
    public Long lPush(String key, Object value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /** List 右端插入 */
    public Long rPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /** List 获取全部（0 到 -1） */
    public List<Object> lGetAll(String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    /** List 获取指定范围 */
    public List<Object> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /** List 右端弹出 */
    @SuppressWarnings("unchecked")
    public <T> T rPop(String key) {
        return (T) redisTemplate.opsForList().rightPop(key);
    }

    /** List 获取长度 */
    public Long lSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    // ==================== Set 操作 ====================

    /** Set 添加元素 */
    public Long sAdd(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /** Set 获取全部成员 */
    public Set<Object> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /** Set 判断是否成员 */
    public Boolean sIsMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /** Set 移除元素 */
    public Long sRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    /** Set 获取大小 */
    public Long sSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    // ==================== ZSet 操作 ====================

    /** ZSet 添加（带分数） */
    public Boolean zAdd(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /** ZSet 获取全部（升序） */
    public Set<Object> zRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    /** ZSet 倒序获取 */
    public Set<Object> zReverseRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /** ZSet 移除元素 */
    public Long zRemove(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    /** ZSet 获取大小 */
    public Long zSize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }
}
