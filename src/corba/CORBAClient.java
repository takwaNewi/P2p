package corba;

import StudySystem.*;
import org.omg.CORBA.*;
import org.omg.CosNaming.*;
import java.util.*;

public class CORBAClient {
    public static void main(String[] args) {
        try {
            System.out.println("╔════════════════════════════════════╗");
            System.out.println("║  CORBA Client - Question Manager   ║");
            System.out.println("╚════════════════════════════════════╝\n");
            
            Scanner scanner = new Scanner(System.in);
            
            System.out.print("Your client ID (ex: student1): ");
            String clientId = scanner.nextLine().trim();
            
            System.out.print("Port ORB (défaut: 1050): ");
            String portStr = scanner.nextLine();
            int port = portStr.isEmpty() ? 1050 : Integer.parseInt(portStr);
            
            System.out.println("\n➜ Connecting to CORBA server...");
            
            // Initialisation de l'ORB
            Properties props = new Properties();
            props.put("org.omg.CORBA.ORBInitialPort", String.valueOf(port));
            props.put("org.omg.CORBA.ORBInitialHost", "localhost");
            
            ORB orb = ORB.init(args, props);
            System.out.println("✓ ORB initialized");
            
            // Obtention du naming context
            org.omg.CORBA.Object objRef = 
                orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            System.out.println("✓ Naming context obtained");
            
            // Résolution du peer (QuestionServer)
            String name = "Peer-QuestionServer";
            objRef = ncRef.resolve_str(name);
            Peer peer = PeerHelper.narrow(objRef);
            
            System.out.println("✓ Connected to QuestionServer");
            
            // Test de connexion
            if (peer.ping()) {
                System.out.println("✓ Server is active\n");
            }
            
            // Créer et afficher la GUI
            CORBAClientGUI gui = new CORBAClientGUI(clientId, peer);
            gui.log("Connected successfully to QuestionServer");
            
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}