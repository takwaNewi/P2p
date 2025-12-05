package rmi;

import models.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class PeerNode extends UnicastRemoteObject implements PeerInterface {
    private String peerId;
    private String host;
    private int port;
    private DataStore dataStore;
    private GossipManager gossipManager;
    private List<PeerInfo> knownPeers;
    private PeerInfo myInfo;
    
    public PeerNode(String peerId, String host, int port) throws RemoteException {
        super();
        this.peerId = peerId;
        this.host = host;
        this.port = port;
        this.dataStore = new DataStore(peerId);
        this.knownPeers = new CopyOnWriteArrayList<>();
        this.myInfo = new PeerInfo(peerId, host, port);
        this.gossipManager = new GossipManager(this);
    }
    
    @Override
    public List<Question> getQuestions(String subject) throws RemoteException {
        return dataStore.getQuestions(subject);
    }
    
    @Override
    public void shareQuestion(Question q) throws RemoteException {
        dataStore.addQuestion(q);
        
        String payload = q.getId() + "|" + q.getSubject() + "|" + 
                        q.getContent() + "|" + q.getAuthorId();
        GossipMessage msg = new GossipMessage("QUESTION", payload, peerId);
        gossipManager.propagate(msg);
        
        System.out.println("✓ Question partagée: " + q.getContent());
    }
    
    @Override
    public void receiveGossip(GossipMessage msg) throws RemoteException {
        gossipManager.receive(msg);
    }
    
    @Override
    public PeerInfo getPeerInfo() throws RemoteException {
        return myInfo;
    }
    
    @Override
    public void registerPeer(PeerInfo peerInfo) throws RemoteException {
        boolean exists = knownPeers.stream()
            .anyMatch(p -> p.getPeerId().equals(peerInfo.getPeerId()));
        
        if (!exists) {
            knownPeers.add(peerInfo);
            System.out.println("✓ Nouveau peer: " + peerInfo.getPeerId());
        }
    }
    
    @Override
    public List<PeerInfo> getKnownPeers() throws RemoteException {
        return new ArrayList<>(knownPeers);
    }
    
    @Override
    public Statistics getStatistics(String studentId) throws RemoteException {
        return dataStore.getStatistics(studentId);
    }
    
    @Override
    public void updateStatistics(Statistics stats) throws RemoteException {
        dataStore.updateStatistics(stats);
        
        String payload = stats.getStudentId() + "|" + stats.getLastUpdate();
        GossipMessage msg = new GossipMessage("STATISTICS", payload, peerId);
        gossipManager.propagate(msg);
    }
    
    @Override
    public boolean ping() throws RemoteException {
        return true;
    }
    
    public DataStore getDataStore() { return dataStore; }
    public List<PeerInfo> getKnownPeersList() { return knownPeers; }
    public String getPeerId() { return peerId; }
    
    public void start() throws Exception {
        Registry registry = LocateRegistry.createRegistry(port);
        registry.rebind("Peer-" + peerId, this);
        gossipManager.startGossipCycle();
        System.out.println(" Peer " + peerId + " démarré sur port " + port);
    }
    
    public void connectToPeer(String host, int port, String peerId) {
        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            PeerInterface remotePeer = (PeerInterface) registry.lookup("Peer-" + peerId);
            
            remotePeer.registerPeer(myInfo);
            PeerInfo remoteInfo = remotePeer.getPeerInfo();
            registerPeer(remoteInfo);
            
            System.out.println("✓ Connecté à: " + peerId);
        } catch (Exception e) {
            System.err.println(" Connexion échouée: " + e.getMessage());
        }
    }
}