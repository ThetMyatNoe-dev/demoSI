package com.demoSI.filter;

import com.demoSI.model.ProcessedData;
import com.demoSI.model.RawData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.Filter;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class DataFilter {

    private static final Logger log = LoggerFactory.getLogger(DataFilter.class);

    /**
     * FILTER 1: Validate raw data
     * Filters out invalid or empty data
     */
    @Filter(inputChannel = "validationChannel", outputChannel = "validatedChannel",
            discardChannel = "invalidDataChannel")
    public boolean validateRawData(RawData rawData) {
        log.info("FILTER: Validating data for ID: {}", rawData.getId());

        // Check if data is valid
        boolean isValid = rawData.getContent() != null
                && !rawData.getContent().isEmpty()
                && rawData.getContent().length() >= 5;

        if (!isValid) {
            log.warn("FILTER: Data REJECTED - Invalid content for ID: {}", rawData.getId());
        } else {
            log.info("FILTER: Data ACCEPTED for ID: {}", rawData.getId());
        }

        return isValid;
    }

    /**
     * FILTER 2: Filter by timestamp (only recent data)
     * Filters out old data (older than 24 hours)
     */
    @Filter(inputChannel = "timeFilterChannel", outputChannel = "recentDataChannel",
            discardChannel = "oldDataChannel")
    public boolean filterByTimestamp(RawData rawData) {
        log.info("FILTER: Checking timestamp for ID: {}", rawData.getId());

        long twentyFourHoursAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000);
        boolean isRecent = rawData.getTimestamp() > twentyFourHoursAgo;

        if (!isRecent) {
            log.warn("FILTER: Data REJECTED - Too old, ID: {}", rawData.getId());
        }

        return isRecent;
    }

    /**
     * FILTER 3: Filter successful processing only
     * Only allows successfully processed data to continue
     */
    @Filter(inputChannel = "processResultChannel", outputChannel = "successChannel",
            discardChannel = "errorChannel")
    public boolean filterSuccessfulProcessing(ProcessedData processedData) {
        log.info("FILTER: Checking processing status for ID: {}", processedData.getId());

        boolean isSuccess = "SUCCESS".equals(processedData.getStatus());

        if (!isSuccess) {
            log.error("FILTER: Processing FAILED for ID: {}, Status: {}",
                    processedData.getId(), processedData.getStatus());
        }

        return isSuccess;
    }

    /**
     * FILTER 4: Filter by priority (HIGH priority only)
     */
    @Filter(inputChannel = "priorityFilterChannel", outputChannel = "highPriorityChannel",
            discardChannel = "lowPriorityChannel")
    public boolean filterHighPriority(Message<RawData> message) {
        String priority = (String) message.getHeaders().get("businessPriority");
        log.info("FILTER: Checking priority: {}", priority);

        return "HIGH".equals(priority);
    }
}