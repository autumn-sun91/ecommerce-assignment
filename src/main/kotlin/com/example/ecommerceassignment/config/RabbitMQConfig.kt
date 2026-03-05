package com.example.ecommerceassignment.config

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfig {
    @Value("\${order.queue.name}")
    private lateinit var queueName: String

    @Value("\${order.queue.exchange}")
    private lateinit var exchangeName: String

    @Value("\${order.queue.routing-key}")
    private lateinit var routingKey: String

    // 신규 order.confirmed 설정
    @Value("\${order.confirmed.queue}")
    private lateinit var confirmedQueueName: String

    @Value("\${order.confirmed.exchange}")
    private lateinit var confirmedExchangeName: String

    @Value("\${order.confirmed.routing-key}")
    private lateinit var confirmedRoutingKey: String

    @Bean
    fun orderQueue(): Queue = Queue(queueName, true) // durable=true

    @Bean
    fun orderExchange(): DirectExchange = DirectExchange(exchangeName)

    @Bean
    fun orderBinding(): Binding =
        BindingBuilder
            .bind(orderQueue())
            .to(orderExchange())
            .with(routingKey)

    @Bean fun confirmedQueue(): Queue = Queue(confirmedQueueName, true)

    @Bean fun confirmedExchange(): DirectExchange = DirectExchange(confirmedExchangeName)

    @Bean fun confirmedBinding(): Binding =
        BindingBuilder
            .bind(confirmedQueue())
            .to(confirmedExchange())
            .with(confirmedRoutingKey)

    @Bean
    fun messageConverter(): Jackson2JsonMessageConverter = Jackson2JsonMessageConverter()

    @Bean
    fun rabbitTemplate(
        connectionFactory: ConnectionFactory,
        messageConverter: Jackson2JsonMessageConverter,
    ): RabbitTemplate =
        RabbitTemplate(connectionFactory).apply {
            this.messageConverter = messageConverter
        }
}
