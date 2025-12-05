package rmi;

import models.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        try {
            System.out.println("╔══════════════════════════════════════╗");
            System.out.println("║  Système d'Étude Distribué P2P      ║");
            System.out.println("╚══════════════════════════════════════╝\n");
            
            System.out.print("ID du peer (ex: student1): ");
            String peerId = scanner.nextLine();
            
            System.out.print("Host (défaut: localhost): ");
            String host = scanner.nextLine();
            if (host.isEmpty()) host = "localhost";
            
            System.out.print("Port RMI (ex: 1099): ");
            int port = Integer.parseInt(scanner.nextLine());
            
            PeerNode peer = new PeerNode(peerId, host, port);
            peer.start();
            
            boolean running = true;
            while (running) {
                System.out.println("\n╔════════════ MENU ════════════╗");
                System.out.println("║ 1. Ajouter une question      ║");
                System.out.println("║ 2. Voir les questions        ║");
                System.out.println("║ 3. Connecter à un peer       ║");
                System.out.println("║ 4. Voir les peers connus     ║");
                System.out.println("║ 5. Voir statistiques         ║");
                System.out.println("║ 6. Mettre à jour stats       ║");
                System.out.println("║ 0. Quitter                   ║");
                System.out.println("╚══════════════════════════════╝");
                System.out.print("Choix: ");
                
                int choice = Integer.parseInt(scanner.nextLine());
                
                switch (choice) {
                    case 1: addQuestion(scanner, peer); break;
                    case 2: viewQuestions(scanner, peer); break;
                    case 3: connectToPeer(scanner, peer); break;
                    case 4: viewKnownPeers(peer); break;
                    case 5: viewStatistics(scanner, peer); break;
                    case 6: updateStatistics(scanner, peer); break;
                    case 0: running = false; break;
                    default: System.out.println(" Choix invalide");
                }
            }
            
            System.out.println("\nArrêt du peer...");
            
        } catch (Exception e) {
            System.err.println(" ERREUR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void addQuestion(Scanner scanner, PeerNode peer) {
        try {
            System.out.print("Sujet: ");
            String subject = scanner.nextLine();
            
            System.out.print("Question: ");
            String content = scanner.nextLine();
            
            Question q = new Question(
                UUID.randomUUID().toString(),
                subject, content,
                Arrays.asList("A", "B", "C", "D"),
                "A",
                peer.getPeerId()
            );
            
            peer.shareQuestion(q);
            System.out.println(" Question ajoutée et propagée!");
            
        } catch (Exception e) {
            System.err.println(" Erreur: " + e.getMessage());
        }
    }
    
    private static void viewQuestions(Scanner scanner, PeerNode peer) {
        try {
            System.out.print("Sujet (vide pour tous): ");
            String subject = scanner.nextLine();
            
            List<Question> questions = subject.isEmpty() 
                ? peer.getDataStore().getAllQuestions()
                : peer.getQuestions(subject);
            
            System.out.println("\n╔════════ Questions (" + questions.size() + ") ════════╗");
            for (Question q : questions) {
                System.out.println("├─ ID: " + q.getId());
                System.out.println("│  Sujet: " + q.getSubject());
                System.out.println("│  Question: " + q.getContent());
                System.out.println("│  Auteur: " + q.getAuthorId());
                System.out.println("└────────────────────────────");
            }
            
        } catch (Exception e) {
            System.err.println(" Erreur: " + e.getMessage());
        }
    }
    
    private static void connectToPeer(Scanner scanner, PeerNode peer) {
        System.out.print("Host du peer: ");
        String host = scanner.nextLine();
        
        System.out.print("Port du peer: ");
        int port = Integer.parseInt(scanner.nextLine());
        
        System.out.print("ID du peer: ");
        String peerId = scanner.nextLine();
        
        peer.connectToPeer(host, port, peerId);
    }
    
    private static void viewKnownPeers(PeerNode peer) {
        try {
            List<PeerInfo> peers = peer.getKnownPeers();
            System.out.println("\n╔═══ Peers Connus (" + peers.size() + ") ═══╗");
            for (PeerInfo p : peers) {
                System.out.println("├─ " + p.getPeerId() + " @ " + 
                    p.getHost() + ":" + p.getPort());
            }
            System.out.println("└─────────────────────────");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }
    
    private static void viewStatistics(Scanner scanner, PeerNode peer) {
        try {
            System.out.print("ID étudiant: ");
            String studentId = scanner.nextLine();
            
            Statistics stats = peer.getStatistics(studentId);
            System.out.println("\n╔═══ Statistiques: " + studentId + " ═══╗");
            System.out.println("Scores: " + stats.getSubjectScores());
            System.out.println("Questions: " + stats.getQuestionsSolved());
            System.out.println("└───────────────────────────");
            
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }
    
    private static void updateStatistics(Scanner scanner, PeerNode peer) {
        try {
            System.out.print("ID étudiant: ");
            String studentId = scanner.nextLine();
            
            System.out.print("Sujet: ");
            String subject = scanner.nextLine();
            
            System.out.print("Score: ");
            int score = Integer.parseInt(scanner.nextLine());
            
            Statistics stats = peer.getStatistics(studentId);
            stats.updateScore(subject, score);
            peer.updateStatistics(stats);
            
            System.out.println(" Statistiques mises à jour!");
            
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }
}