package com.kabi.auth_msc.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    @Bean
    public TopicExchange userTopicExchange() {
        return new TopicExchange("user.events", true, false);
    }

    @Bean
    public Queue userProvisionQueue() {
        return new Queue("user.queue.created", true);
    }

    @Bean
    public Binding userTopicBind(Queue userProvisionQueue, TopicExchange userTopicExchange) {
        return BindingBuilder.bind(userProvisionQueue).to(userTopicExchange).with("user.created");
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
