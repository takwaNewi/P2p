package corba;

import StudySystem.*;
import org.omg.CORBA.*;
import org.omg.CosNaming.*;
import org.omg.PortableServer.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

// Implémentation du Peer CORBA
class PeerImpl extends PeerPOA {
    private List<Question> questions = new CopyOnWriteArrayList<>();
    private List<PeerInfo> knownPeers = new CopyOnWriteArrayList<>();
    private PeerInfo myInfo;
    
    public PeerImpl(String peerId, String host, int port) {
        this.myInfo = new PeerInfo();
        this.myInfo.peerId = peerId;
        this.myInfo.host = host;
        this.myInfo.port = port;
        this.myInfo.lastSeen = System.currentTimeMillis();
    }
    
    @Override
    public Question[] getQuestions(String subject) {
        if (subject.isEmpty()) {
            return questions.toArray(new Question[0]);
        }
        
        List<Question> filtered = new ArrayList<>();
        for (Question q : questions) {
            if (q.subject.equals(subject)) {
                filtered.add(q);
            }
        }
        return filtered.toArray(new Question[0]);
    }
    
    @Override
    public void shareQuestion(Question q) {
        q.timestamp = System.currentTimeMillis();
        questions.add(q);
        System.out.println("[Server] Question reçue: " + q.subject + " from " + q.authorId);
    }
    
    @Override
    public void receiveGossip(GossipMessage msg) {
        System.out.println("[Server] Gossip message: " + msg.type);
    }
    
    @Override
    public PeerInfo getPeerInfo() {
        return myInfo;
    }
    
    @Override
    public void registerPeer(PeerInfo peerInfo) {
        for (PeerInfo p : knownPeers) {
            if (p.peerId.equals(peerInfo.peerId)) {
                return; 
            }
        }
        knownPeers.add(peerInfo);
        System.out.println("[Server] Peer registered: " + peerInfo.peerId);
    }
    
    @Override
    public PeerInfo[] getKnownPeers() {
        return knownPeers.toArray(new PeerInfo[0]);
    }
    
    @Override
    public Statistics getStatistics(String studentId) {
        Statistics stats = new Statistics();
        stats.studentId = studentId;
        stats.subjectScores = new ScoreEntry[0];
        stats.lastUpdate = System.currentTimeMillis();
        return stats;
    }
    
    @Override
    public void updateStatistics(Statistics stats) {
        System.out.println("[Server] Statistics updated: " + stats.studentId);
    }
    
    @Override
    public boolean ping() {
        return true;
    }
}

// Serveur CORBA principal - Simple question hub
public class CORBAServer {
    public static void main(String[] args) {
        try {
            System.out.println("╔════════════════════════════════════╗");
            System.out.println("║  CORBA Server - Questions Hub      ║");
            System.out.println("╚════════════════════════════════════╝\n");
            
            String peerId = "QuestionServer";
            int port = 1050;
            
            System.out.println("➜ Starting CORBA Server...");
            System.out.println("  Peer ID: " + peerId);
            System.out.println("  Port: " + port);
            System.out.println("  Host: localhost\n");
            
            // Initialisation de l'ORB
            Properties props = new Properties();
            props.put("org.omg.CORBA.ORBInitialPort", String.valueOf(port));
            props.put("org.omg.CORBA.ORBInitialHost", "localhost");
            
            ORB orb = ORB.init(args, props);
            System.out.println("✓ ORB initialized");
            
            // Activation du POA
            POA rootpoa = POAHelper.narrow(
                orb.resolve_initial_references("RootPOA")
            );
            rootpoa.the_POAManager().activate();
            System.out.println(" POA activated");
            
            // Création de l'implémentation
            PeerImpl peerImpl = new PeerImpl(peerId, "localhost", port);
            
            // Enregistrement dans le naming service
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(peerImpl);
            Peer peerRef = PeerHelper.narrow(ref);
            
            org.omg.CORBA.Object objRef = 
                orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            
            String name = "Peer-" + peerId;
            NameComponent[] path = ncRef.to_name(name);
            ncRef.rebind(path, peerRef);
            
            System.out.println("✓ Peer registered: " + name);
            System.out.println("\n[*] Server is running. Waiting for clients...\n");
            
            // Attente des requêtes
            orb.run();
            
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}