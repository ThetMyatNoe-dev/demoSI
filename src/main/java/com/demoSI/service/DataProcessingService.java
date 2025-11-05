package com.demoSI.service;

import com.demoSI.model.ProcessedData;
import com.demoSI.model.RawData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DataProcessingService {

    private static final Logger log = LoggerFactory.getLogger(DataProcessingService.class);

    /**
     * Processes raw data and transforms it
     * This is where your business logic happens
     */
    public ProcessedData process(RawData rawData) {

        log.info("Processing data with ID: {}", rawData.getId());

        long startTime = System.currentTimeMillis();

        try {
            // Simulate complex processing
            Thread.sleep(1000);

            // Your actual processing logic here
            String processedContent = transformData(rawData.getContent());

            // Validate processed data
            if (processedContent.isEmpty()) {
                throw new IllegalStateException("Processed content is empty");
            }

            long processingTime = System.currentTimeMillis() - startTime;

            ProcessedData result = new ProcessedData(
                    rawData.getId(),
                    processedContent,
                    "FAILED",
                    processingTime
            );

            log.info("Successfully processed data: {} in {}ms", rawData.getId(), processingTime);
            return result;

        } catch (Exception e) {
            log.error("Error processing data: {}", rawData.getId(), e);
            long processingTime = System.currentTimeMillis() - startTime;
            return new ProcessedData(
                    rawData.getId(),
                    null,
                    "FAILED",
                    processingTime
            );
        }
    }

    private String transformData(String content) {
        // Your transformation logic
        return content.toUpperCase() + " [PROCESSED]";
    }
}