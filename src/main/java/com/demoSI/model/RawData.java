package com.demoSI.model;

public class RawData {
    private String id;
    private String content;
    private String source; // DATABASE, API, FILE
    private String businessPriority; // HIGH, MEDIUM, LOW
    private Long timestamp;
    private String category; // FINANCIAL, PERSONAL, BUSINESS

    public RawData() {}

    public RawData(String id, String content, String source, String businessPriority, String category) {
        this.id = id;
        this.content = content;
        this.source = source;
        this.businessPriority = businessPriority;
        this.category = category;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getBusinessPriority() { return businessPriority; }
    public void setBusinessPriority(String businessPriority) { this.businessPriority = businessPriority; }
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}