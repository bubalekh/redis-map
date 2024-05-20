package com.example.redismap.model;

import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash("Model")
public class RedisKeyValueModel {

    private String id;
    private Object value;
}
