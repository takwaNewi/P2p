package models;

import java.io.Serializable;
import java.util.UUID;

public class GossipMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String messageId;
    private String type;
    private String payload;
    private long timestamp;
    private long version;
    private String sourceId;
    
    public GossipMessage(String type, String payload, String sourceId) {
        this.messageId = UUID.randomUUID().toString();
        this.type = type;
        this.payload = payload;
        this.timestamp = System.currentTimeMillis();
        this.version = 1;
        this.sourceId = sourceId;
    }
    
    // Getters et Setters
    public String getMessageId() { return messageId; }
    public String getType() { return type; }
    public String getPayload() { return payload; }
    public long getTimestamp() { return timestamp; }
    public long getVersion() { return version; }
    public void setVersion(long version) { this.version = version; }
    public String getSourceId() { return sourceId; }
}