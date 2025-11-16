package com.quizSystem.RTS.config;

import org.springframework.beans.factory.annotation.Value;            
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory; 
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory(
            @Value("${spring.data.redis.host:localhost}") String host,
            @Value("${spring.data.redis.port:6379}") int port) {
        // Use Spring properties (overridden by env in Docker)
        return new LettuceConnectionFactory(host, port);
    }

//    @Bean
//    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(connectionFactory);
//        // Minimal serializers; adjust later if you store complex objects
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(new StringRedisSerializer());
//        return template;
//    }
@Bean
public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
  RedisTemplate<String, Object> template = new RedisTemplate<>();
  template.setConnectionFactory(connectionFactory);

  StringRedisSerializer string = new StringRedisSerializer();
  template.setKeySerializer(string);
  template.setValueSerializer(string);
  template.setHashKeySerializer(string);
  template.setHashValueSerializer(string);

  template.setEnableDefaultSerializer(false);
  template.afterPropertiesSet();
  return template;
}


  //@Bean
//public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
//  RedisTemplate<String, String> template = new RedisTemplate<>();
//  template.setConnectionFactory(connectionFactory);
//
//  StringRedisSerializer string = new StringRedisSerializer();
//  template.setKeySerializer(string);
//  template.setValueSerializer(string);
//  template.setHashKeySerializer(string);
//  template.setHashValueSerializer(string);
//
//  // Disable default serializer to avoid surprises
//  template.setEnableDefaultSerializer(false);
//
//  template.afterPropertiesSet();
//  return template;
//}

  @Bean
  public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
    return new StringRedisTemplate(connectionFactory);
  }
}
