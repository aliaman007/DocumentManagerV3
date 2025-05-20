package com.main.service;

import com.main.config.RabbitMQConfig;
import com.main.model.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class BatchService {
    private static final Logger logger = LoggerFactory.getLogger(BatchService.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Retryable(
        value = { org.springframework.amqp.AmqpIOException.class, org.springframework.amqp.AmqpConnectException.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void processDocument(Document document) {
        try {
            logger.info("Sending document to queue: {}", document.getId());
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                document
            );
            logger.info("Document sent successfully: {}", document.getId());
        } catch (Exception e) {
            logger.error("Failed to send document to queue: {}", document.getId(), e);
            throw new RuntimeException("Failed to send document to queue: " + e.getMessage(), e);
        }
    }
}