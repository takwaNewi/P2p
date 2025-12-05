package models;

import java.io.Serializable;

public class PeerInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String peerId;
    private String host;
    private int port;
    private long lastSeen;
    
    public PeerInfo(String peerId, String host, int port) {
        this.peerId = peerId;
        this.host = host;
        this.port = port;
        this.lastSeen = System.currentTimeMillis();
    }
    
    public String getPeerId() { return peerId; }
    public String getHost() { return host; }
    public int getPort() { return port; }
    public long getLastSeen() { return lastSeen; }
    public void updateLastSeen() { 
        this.lastSeen = System.currentTimeMillis(); 
    }
}