package com.spring.data_flow_readers.http_reader.configuration;


import com.spring.data_flow_readers.http_reader.processor.CustomProcessor;
import com.spring.data_flow_readers.http_reader.processor.ProcessedResponse;
import com.spring.data_flow_readers.http_reader.reader.HttpItemReader;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@Slf4j
public class BatchConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    private HttpItemReader httpItemReader;
    @Autowired
    private Gson gson;

    @Autowired
    public BatchConfiguration(final JobBuilderFactory jobBuilderFactory,
                              final StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }



    @Bean
    public Step httpReaderStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return stepBuilderFactory.get("fetchAPIDataAndParse")
                .<JsonObject, ProcessedResponse<?>>chunk(1)
                .reader(httpItemReader)
                .processor(processor())
                .writer(writer())
                .faultTolerant() // Enable fault tolerance for the step
                .noRetry(Exception.class) // Disable retry for all exceptions
                .build();
    }

    @Bean
    public ItemWriter<ProcessedResponse<?>> writer() {
        //todo
        return items -> {
            for (ProcessedResponse<?> item : items) {
                System.out.println("------------------WRITER----------------------");
                System.out.println(item);

            }
        };
    }

    @Bean
    public ItemProcessor<JsonObject, ProcessedResponse<?>> processor() {
        return new CustomProcessor();
    }

    @Bean(name = "httpReaderJob")
    public Job httpJob(JobRepository jobRepository, Step dmnUpdateStep) {
        return jobBuilderFactory.get("httpReaderJob")
                .start(dmnUpdateStep)
                .build();
    }
}
