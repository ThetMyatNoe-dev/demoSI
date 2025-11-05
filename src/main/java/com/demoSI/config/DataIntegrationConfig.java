package com.demoSI.config;

import com.demoSI.model.ProcessedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableIntegration
public class DataIntegrationConfig {


    private static final Logger log = LoggerFactory.getLogger(DataIntegrationConfig.class);


    /**
     * Main integration flow - connects all services
     * This is like  pipeline: Read -> Process -> Write -> Notify
     */
    @Bean
    public IntegrationFlow dataProcessingFlow() {
        return IntegrationFlow
                .from("dataInputChannel")

                // Step 1: Read data
                .handle("dataReadingService", "readData")
                .channel("readDataChannel")

                // Step 2: Process data
                .handle("dataProcessingService", "process")
                .channel("processedDataChannel")

                // Step 3: Filter out failed processing
                /*
                This filter acts as a checkpoint in your integration flow. It checks if the data processing was successful and decides what happens next.
                The filter ensures only successfully processed data moves forward in your pipeline. Failed processing gets routed to error handling instead of being written to your database.
                Think of it as a quality gate - only good data passes through! 🚦
                 */
                .filter(ProcessedData.class,
                        p -> "SUCCESS".equals(p.getStatus()),
                        e -> e.discardChannel("errorChannel"))

                // Step 4: Write data
                .handle("dataWritingService", "write")
                .channel("writtenDataChannel")

                // Step 5: Send notification
                .handle("notificationService", "notify")

                .get();
    }

    /**
     * Channel definitions
     */
    @Bean
    public MessageChannel dataInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel readDataChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel processedDataChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel writtenDataChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel errorChannel() {
        return new QueueChannel(100); // Queue for failed messages
    }

    @Bean
    public MessageChannel retryChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel deadLetterChannel() {
        return new QueueChannel(100);
    }


    /**
     * Error handler with retry logic
     */
    @ServiceActivator(inputChannel = "errorChannel")
    public void handleError(Message<?> message) {
        ProcessedData failedData = (ProcessedData) message.getPayload();
        int retryCount = (int) message.getHeaders().getOrDefault("retryCount", 0);

        if (retryCount < 3) {
            log.info("Retrying processing for ID: {} (Attempt {})",
                    failedData.getId(), retryCount + 1);

            // Send the dataId (String) not the ProcessedData object
            Message<String> retryMessage = MessageBuilder
                    .withPayload(failedData.getId())  // Extract String ID
                    .setHeader("retryCount", retryCount + 1)
                    .build();

            retryChannel().send(retryMessage);
        } else {
            log.error("Max retries reached for ID: {}", failedData.getId());
            deadLetterChannel().send(message);
        }
    }

    /**
     * Retry flow - processes messages from retry channel
     */
    @Bean
    public IntegrationFlow retryFlow() {
        return IntegrationFlow
                .from("retryChannel")
                // Send back to the beginning of main flow
                .channel("dataInputChannel")
                .get();
    }

    /**
     * Dead letter handler
     */
    @ServiceActivator(inputChannel = "deadLetterChannel")
    public void handleDeadLetter(Message<?> message) {
        ProcessedData failedData = (ProcessedData) message.getPayload();
        log.error("DEAD LETTER: Failed to process data after max retries: {}", failedData);

    }
}