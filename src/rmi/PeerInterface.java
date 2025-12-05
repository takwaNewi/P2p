package rmi;

import models.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface PeerInterface extends Remote {
    List<Question> getQuestions(String subject) throws RemoteException;
    void shareQuestion(Question q) throws RemoteException;
    void receiveGossip(GossipMessage msg) throws RemoteException;
    PeerInfo getPeerInfo() throws RemoteException;
    void registerPeer(PeerInfo peerInfo) throws RemoteException;
    List<PeerInfo> getKnownPeers() throws RemoteException;
    Statistics getStatistics(String studentId) throws RemoteException;
    void updateStatistics(Statistics stats) throws RemoteException;
    boolean ping() throws RemoteException;
}