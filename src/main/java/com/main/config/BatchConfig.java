package com.main.config;

import java.util.Collections;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.main.model.Document;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean
    public ItemReader<Document> reader() {
        return new ListItemReader<>(Collections.emptyList()); // Placeholder; updated via RabbitMQ
    }

    @Bean
    public ItemProcessor<Document, Document> processor() {
        return document -> document; // Simulate processing
    }

    @Bean
    public ItemWriter<Document> writer() {
        return items -> {}; // Placeholder; documents saved in repository
    }

    @Bean
    public Step documentProcessingStep() {
        return new StepBuilder("documentProcessingStep", jobRepository)
                .<Document, Document>chunk(10, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public Job documentProcessingJob() {
        return new JobBuilder("documentProcessingJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(documentProcessingStep())
                .build();
    }
}