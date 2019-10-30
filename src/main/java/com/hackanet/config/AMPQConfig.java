package com.hackanet.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackanet.services.push.RabbitMQPushNotificationService;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * @author Iskander Valiev
 * created by isko
 * on 10/30/19
 */
@Configuration
public class AMPQConfig {

    public final static String QUEUE_NAME = "hackanet-phone-push-queue";
    public static final String X_MAX_PRIORITY_PARAM = "x-max-priority";

    @Autowired
    private RabbitMQPushNotificationService pushNotificationService;

    @Bean
    Queue queue() {
        HashMap<String, Object> arguments = new HashMap<>();
        arguments.put(X_MAX_PRIORITY_PARAM, 10);
        return new Queue(QUEUE_NAME, true, false, false, arguments);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange("spring-boot-exchange");
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(QUEUE_NAME);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setRoutingKey(QUEUE_NAME);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(QUEUE_NAME);
        container.setMessageListener(pushNotificationService);
        container.setMessageConverter(jsonMessageConverter());
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        return container;
    }

    @Bean(name = "rabbitObjectMapper")
    public ObjectMapper rabbitObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        return objectMapper;
    }
}
