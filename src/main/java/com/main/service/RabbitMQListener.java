package com.main.service;

import com.main.config.RabbitMQConfig;
import com.main.model.Document;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQListener {
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void processDocument(Document document) {
        // Simulate async processing
        System.out.println("Processed document: " + document.getMetadata().getName());
    }
}