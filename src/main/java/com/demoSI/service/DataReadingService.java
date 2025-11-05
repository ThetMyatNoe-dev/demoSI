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

        // In real scenario, you'd query database or call external API
        RawData data = new RawData(
                dataId,
                "Sample content for " + dataId,
                "DATABASE"
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
}
