package com.demoSI.activator;

import com.demoSI.model.ProcessedData;
import com.demoSI.model.RawData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class DataServiceActivator {

    private static final Logger log = LoggerFactory.getLogger(DataServiceActivator.class);

    /**
     * SERVICE ACTIVATOR 1: Database-specific processing
     */
    @ServiceActivator(inputChannel = "databaseProcessingChannel",
            outputChannel = "processedChannel")
    public ProcessedData processDatabaseData(RawData rawData) {
        log.info("SERVICE ACTIVATOR: Processing DATABASE data for ID: {}", rawData.getId());

        ProcessedData result = new ProcessedData();
        result.setId(rawData.getId());
        result.setProcessedContent(rawData.getContent() + " [DB-PROCESSED]");
        result.setStatus("SUCCESS");
        result.setProcessedBy("DatabaseProcessor");
        result.setProcessingTime(500L);

        return result;
    }

    /**
     * SERVICE ACTIVATOR 2: API-specific processing
     */
    @ServiceActivator(inputChannel = "apiProcessingChannel",
            outputChannel = "processedChannel")
    public ProcessedData processApiData(RawData rawData) {
        log.info("SERVICE ACTIVATOR: Processing API data for ID: {}", rawData.getId());

        ProcessedData result = new ProcessedData();
        result.setId(rawData.getId());
        result.setProcessedContent(rawData.getContent() + " [API-PROCESSED]");
        result.setStatus("SUCCESS");
        result.setProcessedBy("ApiProcessor");
        result.setProcessingTime(300L);

        return result;
    }

    /**
     * SERVICE ACTIVATOR 3: File-specific processing
     */
    @ServiceActivator(inputChannel = "fileProcessingChannel",
            outputChannel = "processedChannel")
    public ProcessedData processFileData(RawData rawData) {
        log.info("SERVICE ACTIVATOR: Processing FILE data for ID: {}", rawData.getId());

        ProcessedData result = new ProcessedData();
        result.setId(rawData.getId());
        result.setProcessedContent(rawData.getContent() + " [FILE-PROCESSED]");
        result.setStatus("SUCCESS");
        result.setProcessedBy("FileProcessor");
        result.setProcessingTime(700L);

        return result;
    }

    /**
     * SERVICE ACTIVATOR 4: Data enrichment
     */
    @ServiceActivator(inputChannel = "enrichmentChannel",
            outputChannel = "enrichedChannel")
    public ProcessedData enrichData(ProcessedData processedData) {
        log.info("SERVICE ACTIVATOR: Enriching data for ID: {}", processedData.getId());

        // Add additional metadata
        processedData.setEnrichedInfo("Enriched at: " + System.currentTimeMillis());
        processedData.setValidationResult("VALID");

        return processedData;
    }

    /**
     * SERVICE ACTIVATOR 5: Error handler
     */
    @ServiceActivator(inputChannel = "errorChannel")
    public void handleError(Message<?> message) {
        log.error("SERVICE ACTIVATOR: Error handling for message");
        log.error("Payload: {}", message.getPayload());
        log.error("Headers: {}", message.getHeaders());

        // Save to error database
        // Send alert
        // Implement retry logic
    }

    /**
     * SERVICE ACTIVATOR 6: Invalid data handler
     */
    @ServiceActivator(inputChannel = "invalidDataChannel")
    public void handleInvalidData(RawData rawData) {
        log.warn("SERVICE ACTIVATOR: Handling invalid data for ID: {}", rawData.getId());

        // Save to invalid_records table
        // Send notification to data team
    }

    /**
     * SERVICE ACTIVATOR 7: Old data archiver
     */
    @ServiceActivator(inputChannel = "oldDataChannel")
    public void archiveOldData(RawData rawData) {
        log.info("SERVICE ACTIVATOR: Archiving old data for ID: {}", rawData.getId());

        // Move to archive storage
        // Update metadata
    }

    /**
     * SERVICE ACTIVATOR 8: Audit logger
     */
    @ServiceActivator(inputChannel = "financialAuditChannel")
    public void auditFinancialData(RawData rawData) {
        log.info("SERVICE ACTIVATOR: Auditing financial data for ID: {}", rawData.getId());

        // Log to audit trail
        // Check compliance
        // Generate audit report
    }
}