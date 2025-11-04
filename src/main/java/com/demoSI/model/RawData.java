package com.demoSI.model;

public class RawData {
    private String id;
    private String content;
    private String source;
    private Long timestamp;

    // Constructors, getters, setters
    public RawData() {}

    public RawData(String id, String content, String source) {
        this.id = id;
        this.content = content;
        this.source = source;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
}