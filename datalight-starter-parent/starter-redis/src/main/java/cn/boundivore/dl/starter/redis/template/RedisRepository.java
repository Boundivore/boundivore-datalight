/**
 * Copyright (C) <2023> <Boundivore> <boundivore@foxmail.com>
 * <p>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Apache License, Version 2.0
 * as published by the Apache Software Foundation.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Apache License, Version 2.0 for more details.
 * <p>
 * You should have received a copy of the Apache License, Version 2.0
 * along with this program; if not, you can obtain a copy at
 * http://www.apache.org/licenses/LICENSE-2.0.
 */
package cn.boundivore.dl.starter.redis.template;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationUtils;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Description: RedisRepository
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/18
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
@Slf4j
@SuppressWarnings("unchecked")
public class RedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public RedisConnectionFactory getConnectionFactory() {
        return this.redisTemplate.getConnectionFactory();
    }


    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }


    public void flushDB(RedisClusterNode node) {
        this.redisTemplate.opsForCluster().flushDb(node);
    }

    public void setExpire(final byte[] key, final byte[] value, final long time) {
        redisTemplate.execute((RedisCallback<Long>) connection -> {
            connection.setEx(key, time, value);
            return 1L;
        });
    }


    public void setExpire(final String key, final Object value, final long time, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, time, timeUnit);
    }

    public void setExpire(final String key, final Object value, final long time) {
        this.setExpire(key, value, time, TimeUnit.SECONDS);
    }


    public void setExpire(final String key, final Object value, final long time, final TimeUnit timeUnit, RedisSerializer<Object> valueSerializer) {
        byte[] rawKey = rawKey(key);
        byte[] rawValue = rawValue(value, valueSerializer);

        redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                potentiallyUsePsetEx(connection);
                return null;
            }

            public void potentiallyUsePsetEx(RedisConnection connection) {
                if (!TimeUnit.MILLISECONDS.equals(timeUnit) || !failsafeInvokePsetEx(connection)) {
                    connection.setEx(rawKey, TimeoutUtils.toSeconds(time, timeUnit), rawValue);
                }
            }

            private boolean failsafeInvokePsetEx(RedisConnection connection) {
                boolean failed = false;
                try {
                    connection.pSetEx(rawKey, time, rawValue);
                } catch (UnsupportedOperationException e) {
                    failed = true;
                }
                return !failed;
            }
        }, true);
    }


    public void setExpire(final String[] keys, final Object[] values, final long time) {
        for (int i = 0; i < keys.length; i++) {
            redisTemplate.opsForValue().set(keys[i], values[i], time, TimeUnit.SECONDS);
        }
    }


    public void set(final String[] keys, final Object[] values) {
        for (int i = 0; i < keys.length; i++) {
            redisTemplate.opsForValue().set(keys[i], values[i]);
        }
    }


    public void set(final String key, final Object value) {
        redisTemplate.opsForValue().set(key, value);
    }


    public Set<String> keys(final String keyPatten) {
        return redisTemplate.keys(keyPatten + "*");
    }


    public byte[] get(final byte[] key) {
        return redisTemplate.execute((RedisCallback<byte[]>) connection -> connection.get(key));
    }

    public Object get(final String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public String getAndSet(final String key, String value) {
        String result = null;
        if (StringUtils.isEmpty(key)) {
            log.error("IllegalArguments");
            return null;
        }
        try {
            Object object = redisTemplate.opsForValue().getAndSet(key, value);
            if (object != null) {
                result = object.toString();
            }
        } catch (Exception e) {
            log.error("RedisTemplate operation error", e);
        }
        return result;
    }

    public Object get(final String key, RedisSerializer<Object> valueSerializer) {
        byte[] rawKey = rawKey(key);
        return redisTemplate.execute(connection -> deserializeValue(connection.get(rawKey), valueSerializer), true);
    }


    public HashOperations<String, String, Object> opsForHash() {
        return redisTemplate.opsForHash();
    }


    public void putHashValue(String key, String hashKey, Object hashValue) {
        opsForHash().put(key, hashKey, hashValue);
    }


    public Object getHashValues(String key, String hashKey) {
        return opsForHash().get(key, hashKey);
    }


    public void delHashValues(String key, Object... hashKeys) {
        opsForHash().delete(key, hashKeys);
    }


    public Map<String, Object> getHashValue(String key) {
        return opsForHash().entries(key);
    }


    public void putHashValues(String key, Map<String, Object> map) {
        opsForHash().putAll(key, map);
    }



    public long dbSize() {
        return redisTemplate.execute(RedisServerCommands::dbSize);
    }


    public String flushDB() {
        return redisTemplate.execute((RedisCallback<String>) connection -> {
            connection.flushDb();
            return "ok";
        });
    }

    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }



    public boolean del(final String... keys) {
        boolean result = false;
        for (String key : keys) {
            result = redisTemplate.delete(key);
        }
        return result;
    }

    public long incr(final String key) {
        return redisTemplate.opsForValue().increment(key);
    }


    public ListOperations<String, Object> opsForList() {
        return redisTemplate.opsForList();
    }


    public Long leftPush(String key, Object value) {
        return opsForList().leftPush(key, value);
    }


    public Object leftPop(String key) {
        return opsForList().leftPop(key);
    }


    public Long in(String key, Object value) {
        return opsForList().rightPush(key, value);
    }


    public Object rightPop(String key) {
        return opsForList().rightPop(key);
    }



    public Long length(String key) {
        return opsForList().size(key);
    }



    public void remove(String key, long i, Object value) {
        opsForList().remove(key, i, value);
    }


    public void set(String key, long index, Object value) {
        opsForList().set(key, index, value);
    }


    public List<Object> getList(String key, int start, int end) {
        return opsForList().range(key, start, end);
    }

    public List<Object> getList(String key, int start, int end, RedisSerializer<Object> valueSerializer) {
        byte[] rawKey = rawKey(key);
        return redisTemplate.execute(connection -> deserializeValues(connection.lRange(rawKey, start, end), valueSerializer), true);
    }


    public Long leftPushAll(String key, List<String> list) {
        return opsForList().leftPushAll(key, list);
    }

    public void insert(String key, long index, Object value) {
        opsForList().set(key, index, value);
    }

    private byte[] rawKey(Object key) {
        Assert.notNull(key, "Non-null key required");

        if (key instanceof byte[]) {
            return (byte[]) key;
        }
        RedisSerializer<Object> redisSerializer = (RedisSerializer<Object>) redisTemplate.getKeySerializer();
        return redisSerializer.serialize(key);
    }

    private byte[] rawValue(Object value, RedisSerializer valueSerializer) {
        if (value instanceof byte[]) {
            return (byte[]) value;
        }

        return valueSerializer.serialize(value);
    }

    private List deserializeValues(List<byte[]> rawValues, RedisSerializer<Object> valueSerializer) {
        if (valueSerializer == null) {
            return rawValues;
        }
        return SerializationUtils.deserialize(rawValues, valueSerializer);
    }

    private Object deserializeValue(byte[] value, RedisSerializer<Object> valueSerializer) {
        if (valueSerializer == null) {
            return value;
        }
        return valueSerializer.deserialize(value);
    }
}
