package com.demoSI.service;

import com.demoSI.model.RawData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DataReadingService {

    private static final Logger log = LoggerFactory.getLogger(DataReadingService.class);

    /**
     * Reads data from source (database, file, API, etc.)
     * In Spring Integration, this method will be called automatically
     * when a message arrives on the input channel
     */
    public RawData readData(String dataId) {
        log.info("Reading data with ID: {}", dataId);

        // Simulate reading from database/file/API
        try {
            Thread.sleep(500); // Simulate I/O delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Determine properties based on dataId
        String source = determineSource(dataId);
        String priority = determinePriority(dataId);
        String category = determineCategory(dataId);

        // In real scenario, you'd query database or call external API
        RawData data = new RawData(
                dataId,
                "Sample content for " + dataId,
                source,      // NEW: source parameter
                priority,    // NEW: priority parameter
                category     // NEW: category parameter
        );

        log.info("Successfully read data: {}", dataId);
        return data;
    }

    /**
     * Batch reading example
     */
    public List<RawData> readBatchData(List<String> dataIds) {
        log.info("Reading batch data, count: {}", dataIds.size());
        return dataIds.stream()
                .map(this::readData)
                .collect(Collectors.toList());
    }

    // Helper methods to determine properties from dataId
    private String determineSource(String dataId) {
        if (dataId.startsWith("DB")) return "DATABASE";
        if (dataId.startsWith("API")) return "API";
        if (dataId.startsWith("FILE")) return "FILE";
        return "DATABASE"; // default
    }

    private String determinePriority(String dataId) {
        if (dataId.contains("URGENT") || dataId.contains("HIGH")) return "HIGH";
        if (dataId.contains("NORMAL") || dataId.contains("MEDIUM")) return "MEDIUM";
        return "LOW"; // default
    }

    private String determineCategory(String dataId) {
        if (dataId.contains("FIN")) return "FINANCIAL";
        if (dataId.contains("PER")) return "PERSONAL";
        if (dataId.contains("BUS")) return "BUSINESS";
        return "BUSINESS"; // default
    }
}