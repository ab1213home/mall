/*
 * Copyright (c) 2024 Jiang RongJun
 * Jiang Mall is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan
 * PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY
 * KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.jiang.mall.service.impl;

import com.jiang.mall.service.IRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements IRedisService {

	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
	    this.redisTemplate = redisTemplate;
	}

	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
	    this.stringRedisTemplate = stringRedisTemplate;
	}

    // String 操作实现
    /**
     * 设置一个键值对
     *
     * @param key 键，用于唯一标识一个值
     * @param value 值，与键关联的数据
     */
    @Override
    public void setString(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }


    /**
     * 将给定的键值对存储在某个数据结构或存储系统中，并设置过期时间
     *
     * @param key 键，用于唯一标识存储的值
     * @param value 值，与键关联存储的数据
     * @param timeout 过期时间，单位毫秒，表示值将在多久之后过期
     * @param unit 时间单位，用于指定过期时间
     */
    @Override
    public void setString(String key, String value, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 根据键获取对应的字符串值
     *
     * @param key 字符串的键，用于唯一标识一个字符串值
     * @return 与键关联的字符串值，如果键不存在，则返回null或默认值
     */
    @Override
    public String getString(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 检查给定的键是否存在于当前数据结构中
     *
     * @param key 要检查的键
     * @return 如果键存在，则返回true；否则返回false
     */
    @Override
    public Boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * 设置指定键的过期时间
     * <p>
     * 此方法用于为给定的键设置过期时间当键过期时，它将不再在数据库中可用此方法常用于缓存场景，
     * 以确保数据不会永久存储，并且可以自动清除旧的或不再需要的数据
     *
     * @param key   要设置过期时间的键不能为空
     * @param timeout  键的过期时间，以秒为单位如果值为0，键将被持久化，不会过期
     * @return      如果操作成功，返回true；否则返回false可能的原因包括但不限于键不存在或者数据库执行操作失败
     */
    @Override
    public Boolean expire(String key, long timeout) {
        return stringRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 获取指定键的剩余过期时间
     *
     * @param key 要获取过期时间的键
     * @return 剩余过期时间，以秒为单位，如果键不存在或者没有设置过期时间，则返回null
     */
    @Override
    public Long getExpire(String key) {
        return stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 删除指定键对应的数据
     *
     * @param key 要删除数据的键
     * @return 如果删除成功，返回true；否则返回false
     */
    @Override
    public Boolean deleteKey(String key) {
        return stringRedisTemplate.delete(key);
    }

    // Hash 操作实现
    @Override
    public void setHash(String key, Map<String, Object> hash) {
        redisTemplate.opsForHash().putAll(key, hash);
    }

    /**
     * 将给定的键与一个哈希映射关联起来，并设置过期时间
     * 此方法用于在某种数据结构或存储系统中设置一个哈希表项，键值对会覆盖已有键的值，并且为该键设置过期时间
     *
     * @param key     键，用于标识哈希映射的唯一性不能为空
     * @param hash    包含键值对的哈希映射，用于设置或更新数据不能为空
     * @param timeout 过期时间，以秒为单位，如果值为0，键将被持久化，不会过期
     */
    @Override
    public void setHash(String key, Map<String, Object> hash, long timeout) {
        redisTemplate.opsForHash().putAll(key, hash);
        redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    @Override
    public Object getHashValue(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    @Override
    public Map<Object, Object> getHashAllEntries(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    @Override
    public Boolean hasHashKey(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    @Override
    public Long deleteHashField(String key, String field) {
        return redisTemplate.opsForHash().delete(key, field);
    }


    // List 操作实现
    /**
     * 在列表的右侧（尾部）添加一个元素
     * 如果列表不存在，会创建一个新的列表并将元素添加到尾部
     *
     * @param key   列表的键，用于标识列表
     * @param value 要添加的元素，可以是任意对象
     * @return 返回列表的新长度如果操作失败，返回null
     */
    @Override
    public Long listRightPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 在列表的左侧（头部）添加一个元素
     * 如果列表不存在，会创建一个新的列表并将元素添加到头部
     *
     * @param key   列表的键，用于标识列表
     * @param value 要添加的元素，可以是任意对象
     * @return 返回列表的新长度如果操作失败，返回null
     */
    @Override
    public Long listLeftPush(String key, Object value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 获取列表的最后一个元素，并从列表中移除它
     * 如果列表为空，返回null
     *
     * @param key 列表的键，用于标识列表
     * @return 返回被移除的元素，如果列表为空，返回null
     */
    @Override
    public Object listRightPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    /**
     * 获取列表的第一个元素，并从列表中移除它
     * 如果列表为空，返回null
     *
     * @param key 列表的键，用于标识列表
     * @return 返回被移除的元素，如果列表为空，返回null
     */
    @Override
    public Object listLeftPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 返回列表中指定范围内的元素
     *
     * @param key 列表的键，用于标识列表
     * @param start 起始索引，包含该索引对应的元素，从0开始
     * @param end 结束索引，包含该索引对应的元素，从0开始
     * @return 返回指定范围内的元素列表，如果列表为空，返回空列表
     */
    @Override
    public List<Object> listRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 获取列表中指定索引位置的元素
     *
     * @param key   列表的键，用于标识列表
     * @param index 索引位置，从0开始
     * @return 返回指定索引位置的元素，如果索引越界，返回null
     */
    @Override
    public Object listGet(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    /**
     * 获取列表的长度
     *
     * @param key 列表的键，用于标识列表
     * @return 返回列表的长度，如果列表不存在，返回0
     */
    @Override
    public Long listLength(String key) {
        return redisTemplate.opsForList().size(key);
    }


    // Set 操作实现
    @Override
    public Long setAdd(String key, Object values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    @Override
    public Set<Object> setMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    @Override
    public Boolean setIsMember(String key, String value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    @Override
    public Long setRemove(String key, Object values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    // Sorted Set 操作实现
    @Override
    public Boolean zSetAdd(String key, double score, String member) {
        return redisTemplate.opsForZSet().add(key, member, score);
    }

    @Override
    public Double zSetScore(String key, String member) {
        return redisTemplate.opsForZSet().score(key, member);
    }

    @Override
    public Set<Object> zSetRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    @Override
    public Long zSetRemove(String key, String member) {
        return redisTemplate.opsForZSet().remove(key, member);
    }
}
