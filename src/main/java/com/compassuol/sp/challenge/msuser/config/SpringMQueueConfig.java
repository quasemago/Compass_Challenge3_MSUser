package com.compassuol.sp.challenge.msuser.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringMQueueConfig {
    @Value("${mq.queues.user-queue}")
    private String userQueueName;

    @Bean
    public Queue userQueue() {
        return new Queue(userQueueName, true);
    }
}
