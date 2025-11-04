package com.demoSI.transformer;

import com.demoSI.model.RawData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class DataTransformer {

    private static final Logger log = LoggerFactory.getLogger(DataTransformer.class);

    /**
     * TRANSFORMER 1: Convert String ID to RawData object
     * Transforms message payload from String to RawData
     */
    @Transformer(inputChannel = "dataInputChannel", outputChannel = "transformedChannel")
    public RawData transformStringToRawData(String dataId) {
        log.info("TRANSFORMER: Converting dataId {} to RawData object", dataId);

        // Transform string to object
        RawData rawData = new RawData(
                dataId,
                "Sample content for " + dataId,
                determineSource(dataId),
                determinePriority(dataId),
                determineCategory(dataId)
        );

        log.info("TRANSFORMER: Created RawData with source={}, priority={}",
                rawData.getSource(), rawData.getBusinessPriority());

        return rawData;
    }

    /**
     * TRANSFORMER 2: Normalize data format
     * Cleans and standardizes data
     */
    @Transformer(inputChannel = "normalizationChannel", outputChannel = "normalizedChannel")
    public RawData normalizeData(RawData rawData) {
        log.info("TRANSFORMER: Normalizing data for ID: {}", rawData.getId());

        // Clean and standardize
        rawData.setContent(rawData.getContent().trim().toUpperCase());
        rawData.setSource(rawData.getSource().toUpperCase());
        rawData.setBusinessPriority(rawData.getBusinessPriority().toUpperCase());

        return rawData;
    }

    /**
     * TRANSFORMER 3: Add headers to message
     * Enriches message with metadata
     */
    @Transformer(inputChannel = "headerEnrichmentChannel", outputChannel = "enrichedHeaderChannel")
    public Message<RawData> addHeaders(RawData rawData) {
        log.info("TRANSFORMER: Adding headers to message for ID: {}", rawData.getId());

        return MessageBuilder.withPayload(rawData)
                .setHeader("processingTimestamp", System.currentTimeMillis())
                .setHeader("source", rawData.getSource())
                .setHeader("businessPriority", rawData.getBusinessPriority().toString())
                .setHeader("category", rawData.getCategory())
                .setHeader("version", "1.0")
                .build();
    }

    private String determineSource(String dataId) {
        if (dataId.startsWith("DB")) return "DATABASE";
        if (dataId.startsWith("API")) return "API";
        return "FILE";
    }

    private String determinePriority(String dataId) {
        if (dataId.contains("URGENT")) return "HIGH";
        if (dataId.contains("NORMAL")) return "MEDIUM";
        return "LOW";
    }

    private String determineCategory(String dataId) {
        if (dataId.contains("FIN")) return "FINANCIAL";
        if (dataId.contains("PER")) return "PERSONAL";
        return "BUSINESS";
    }
}