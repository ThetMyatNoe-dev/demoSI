package com.demoSI.service;

import com.demoSI.model.ProcessedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataWritingService {

    private static final Logger log = LoggerFactory.getLogger(DataWritingService.class);

    /**
     * Writes processed data to database/file/external system
     */
    public ProcessedData write(ProcessedData processedData) {
        log.info("Writing data with ID: {}", processedData.getId());

        try {
            // Simulate database write
            Thread.sleep(300);

            // In real scenario: save to database
            // repository.save(processedData);
            // or write to file, send to external API, etc.

            log.info("Successfully wrote data: {}", processedData.getId());
            return processedData;

        } catch (Exception e) {
            log.error("Error writing data: {}", processedData.getId(), e);
            throw new RuntimeException("Failed to write data", e);
        }
    }

    /**
     * Batch writing
     */
    public void writeBatch(List<ProcessedData> dataList) {
        log.info("Writing batch data, count: {}", dataList.size());
        dataList.forEach(this::write);
    }
}