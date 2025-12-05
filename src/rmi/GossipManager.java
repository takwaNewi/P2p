package rmi;

import models.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.concurrent.*;

public class GossipManager {
    private final PeerNode localPeer;
    private final Map<String, GossipMessage> messageCache;
    private final Map<String, Long> messageVersions;
    private final ScheduledExecutorService scheduler;
    
    private static final int GOSSIP_INTERVAL_MS = 5000;
    private static final int FANOUT = 3;
    private static final int MESSAGE_TTL_MS = 300000;
    
    public GossipManager(PeerNode localPeer) {
        this.localPeer = localPeer;
        this.messageCache = new ConcurrentHashMap<>();
        this.messageVersions = new ConcurrentHashMap<>();
        this.scheduler = Executors.newScheduledThreadPool(2);
    }
    
    public void startGossipCycle() {
        scheduler.scheduleAtFixedRate(
            this::performGossipRound,
            0,
            GOSSIP_INTERVAL_MS,
            TimeUnit.MILLISECONDS
        );
        
        scheduler.scheduleAtFixedRate(
            this::cleanupOldMessages,
            60000,
            60000,
            TimeUnit.MILLISECONDS
        );
        
        System.out.println("✓ Protocole Gossip démarré (intervalle: " + GOSSIP_INTERVAL_MS + "ms)");
    }
    
    private void performGossipRound() {
        try {
            List<PeerInfo> peers = localPeer.getKnownPeersList();
            if (peers.isEmpty()) return;
            
            Collections.shuffle(peers);
            int targetsCount = Math.min(FANOUT, peers.size());
            List<PeerInfo> targets = peers.subList(0, targetsCount);
            
            for (PeerInfo peer : targets) {
                sendGossipToPeer(peer);
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur gossip: " + e.getMessage());
        }
    }
    
    private void sendGossipToPeer(PeerInfo peer) {
        try {
            Registry registry = LocateRegistry.getRegistry(peer.getHost(), peer.getPort());
            PeerInterface remotePeer = (PeerInterface) registry.lookup("Peer-" + peer.getPeerId());
            
            if (!remotePeer.ping()) return;
            
            int messagesSent = 0;
            for (GossipMessage msg : messageCache.values()) {
                if (shouldPropagate(msg)) {
                    remotePeer.receiveGossip(msg);
                    messagesSent++;
                }
            }
            
            if (messagesSent > 0) {
                System.out.println("→ Gossip envoyé à " + peer.getPeerId() + ": " + messagesSent + " msg");
            }
            
            peer.updateLastSeen();
        } catch (Exception e) {
            // Peer non disponible
        }
    }
    
    public void receive(GossipMessage msg) {
        String msgId = msg.getMessageId();
        
        Long existingVersion = messageVersions.get(msgId);
        if (existingVersion != null && existingVersion >= msg.getVersion()) {
            return;
        }
        
        messageCache.put(msgId, msg);
        messageVersions.put(msgId, msg.getVersion());
        
        processMessage(msg);
        
        System.out.println("← Message reçu: " + msg.getType() + " de " + msg.getSourceId());
    }
    
    private void processMessage(GossipMessage msg) {
        try {
            if ("QUESTION".equals(msg.getType())) {
                Question q = deserializeQuestion(msg.getPayload());
                localPeer.getDataStore().addQuestion(q);
            } else if ("STATISTICS".equals(msg.getType())) {
                Statistics stats = deserializeStatistics(msg.getPayload());
                localPeer.getDataStore().updateStatistics(stats);
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur traitement: " + e.getMessage());
        }
    }
    
    private boolean shouldPropagate(GossipMessage msg) {
        long age = System.currentTimeMillis() - msg.getTimestamp();
        return age < 60000;
    }
    
    private void cleanupOldMessages() {
        long cutoff = System.currentTimeMillis() - MESSAGE_TTL_MS;
        messageCache.entrySet().removeIf(e -> e.getValue().getTimestamp() < cutoff);
    }
    
    public void propagate(GossipMessage msg) {
        msg.setVersion(messageVersions.getOrDefault(msg.getMessageId(), 0L) + 1);
        messageCache.put(msg.getMessageId(), msg);
        messageVersions.put(msg.getMessageId(), msg.getVersion());
    }
    
    private Question deserializeQuestion(String payload) {
        String[] parts = payload.split("\\|");
        return new Question(parts[0], parts[1], parts[2], 
            Arrays.asList("A", "B", "C", "D"), "A", parts[3]);
    }
    
    private Statistics deserializeStatistics(String payload) {
        String[] parts = payload.split("\\|");
        return new Statistics(parts[0]);
    }
    
    public void shutdown() {
        scheduler.shutdown();
    }
}