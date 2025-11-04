package com.demoSI.config;

import com.demoSI.model.ProcessedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableIntegration
public class DataIntegrationConfig {


    private static final Logger log = LoggerFactory.getLogger(DataIntegrationConfig.class);

    /**
     * Main integration flow - connects all services
     * This is the pipeline: Read -> Process -> Write -> Notify
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

    /**
     * Error handler
     * "Hey, whenever a message arrives on this channel, automatically call this method!"
     *
     * It's like setting up a listener or subscriber for a specific message channel.
     * 1. **A message fails** somewhere in your integration flow
     * 2. **Filter sends it** to `errorChannel`
     * 3. **Spring Integration sees** a message on `errorChannel`
     * 4. **Automatically calls** `handleError()` method
     * 5. **You handle the error** (log it, retry, save to DB, etc.)
     */
    @ServiceActivator(inputChannel = "errorChannel")
    public void handleError(Message<?> message) {
        log.error("Error in processing flow: {}", message.getPayload());
        // You can implement retry logic, dead letter queue, etc.
    }
}