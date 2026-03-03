package com.example.ecommerceassignment.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {
    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, String> =
        RedisTemplate<String, String>().apply {
            this.connectionFactory = connectionFactory
            keySerializer = StringRedisSerializer()
            valueSerializer = StringRedisSerializer()
            hashKeySerializer = StringRedisSerializer()
            hashValueSerializer = StringRedisSerializer()
        }

    @Bean
    fun orderLuaScript(): RedisScript<Long> {
        val script =
            """
            -- 디버깅용 로그 (Redis 서버 로그에 출력)
            redis.log(redis.LOG_WARNING, "=== Lua Script 실행 시작 ===")
            redis.log(redis.LOG_WARNING, "KEYS[1]: " .. KEYS[1])
            redis.log(redis.LOG_WARNING, "KEYS[2]: " .. KEYS[2])
            redis.log(redis.LOG_WARNING, "ARGV[1]: " .. ARGV[1])
            redis.log(redis.LOG_WARNING, "ARGV[2]: " .. ARGV[2])

            local user_key = KEYS[1]
            local stock_key = KEYS[2]
            local quantity = tonumber(ARGV[1])
            local limit = tonumber(ARGV[2])

            local current_user_count = tonumber(redis.call('GET', user_key) or 0)
            local stock = tonumber(redis.call('GET', stock_key) or 0)

            redis.log(redis.LOG_WARNING, "current_user_count: " .. tostring(current_user_count))
            redis.log(redis.LOG_WARNING, "stock: " .. tostring(stock))
            redis.log(redis.LOG_WARNING, "quantity: " .. tostring(quantity))
            redis.log(redis.LOG_WARNING, "limit: " .. tostring(limit))

            if current_user_count + quantity > limit then
                redis.log(redis.LOG_WARNING, "결과: EXCEED_LIMIT(-1)")
                return -1
            end

            if stock < quantity then
                redis.log(redis.LOG_WARNING, "결과: OUT_OF_STOCK(-2)")
                return -2
            end

            redis.call('INCRBY', user_key, quantity)
            redis.call('DECRBY', stock_key, quantity)

            redis.log(redis.LOG_WARNING, "결과: SUCCESS(1)")
            return 1
            """.trimIndent()

        return RedisScript.of(script, Long::class.java)
    }
}
