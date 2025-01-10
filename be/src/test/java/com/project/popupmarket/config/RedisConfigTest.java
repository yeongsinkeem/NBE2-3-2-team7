package com.project.popupmarket.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisConfigTest {

    @Autowired
    private StringRedisTemplate stringTemplate;

    @Autowired
    private RedisTemplate<String, Object> objectTemplate;

    @DisplayName("String, String 구조에 대한 템플릿 확인")
    @Test
    void stringTemplateTest() {
        // given
        String key = "testKey";
        String expectedValue = "gildong";

        ValueOperations<String, String> ops = stringTemplate.opsForValue();
        ops.set(key, expectedValue, 30L, TimeUnit.SECONDS);

        // when
        String actualValue = ops.get(key);
        System.out.println(actualValue);

        // then
        assertEquals(expectedValue, actualValue);
    }

    @DisplayName("String, Object 구조에 대한 템플릿 확인")
    @Test
    void objectTemplateTest() {
        // given
        String key = "user:info";

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", "gildong");
        userMap.put("age", 25);

        HashOperations<String, String, Object> hashOps = objectTemplate.opsForHash();
        hashOps.putAll(key, userMap);

        // when
        HashMap<String, Object> retrievedUserMap = (HashMap<String, Object>) hashOps.entries(key);
        System.out.println(retrievedUserMap); // 역직렬화해서 가져옴

        // then
        assertEquals(userMap.get("name"), retrievedUserMap.get("name"));
        assertEquals(userMap.get("age"), retrievedUserMap.get("age"));
    }
}