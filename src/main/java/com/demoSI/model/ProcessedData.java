package com.demoSI.model;

public class ProcessedData {
    private String id;
    private String processedContent;
    private String status;
    private Long processingTime;

    public ProcessedData() {}

    public ProcessedData(String id, String processedContent, String status, Long processingTime) {
        this.id = id;
        this.processedContent = processedContent;
        this.status = status;
        this.processingTime = processingTime;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getProcessedContent() { return processedContent; }
    public void setProcessedContent(String content) { this.processedContent = content; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getProcessingTime() { return processingTime; }
    public void setProcessingTime(Long time) { this.processingTime = time; }
}