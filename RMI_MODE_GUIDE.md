# RMI Mode - Quick Start Guide

## Overview

The RMI (Remote Method Invocation) mode provides a console-based interface for running the distributed P2P study system. It allows peers to connect to each other and share questions using Java RMI technology.

## Architecture

```
┌─────────────────┐      ┌─────────────────┐
│  RMI Peer 1     │      │  RMI Peer 2     │
│  (Console UI)   │◄─────►│  (Console UI)   │
│  Port: 1099     │      │  Port: 1100     │
└─────────────────┘      └─────────────────┘
        │                       │
        └───────────┬───────────┘
                    │
           RMI Registry (1099)
```

## Prerequisites

- Java 8+ installed
- Two or more terminal windows
- Ports 1099+ available

## Step 1: Start RMI Registry

In a terminal:
```bash
cd "c:\Users\lajmi\OneDrive\Desktop\distributed-study-system\P2p"
rmiregistry 1099
```

**Output:**
```
(runs silently - registry is ready)
```

## Step 2: Compile the Project

```bash
cd "c:\Users\lajmi\OneDrive\Desktop\distributed-study-system\P2p"
javac -encoding UTF-8 -d bin src/models/*.java src/rmi/*.java
```

## Step 3: Run First Peer

In a new terminal:
```bash
cd "c:\Users\lajmi\OneDrive\Desktop\distributed-study-system\P2p"
java -cp bin rmi.Main
```

**Interactive Prompts:**
```
╔══════════════════════════════════════╗
║  Système d'Étude Distribué P2P      ║
╚══════════════════════════════════════╝

ID du peer (ex: student1): student1
Host (défaut: localhost): localhost
Port RMI (ex: 1099): 1099
```

After entering details, you'll see:
```
╔════════════ MENU ════════════╗
║ 1. Ajouter une question      ║
║ 2. Voir les questions        ║
║ 3. Connecter à un peer       ║
║ 4. Voir les peers connus     ║
║ 5. Voir statistiques         ║
║ 6. Mettre à jour stats       ║
║ 0. Quitter                   ║
╚══════════════════════════════╝
Choix: 
```

## Step 4: Run Additional Peers

In new terminals, repeat Step 3 but use different:
- **Peer ID**: student2, student3, etc.
- **Port**: 1100, 1101, etc. (increment each time)

```bash
java -cp bin rmi.Main
ID du peer (ex: student1): student2
Host (défaut: localhost): localhost
Port RMI (ex: 1099): 1100
```

## Menu Operations

### 1. Add Question (Menu Option 1)
```
Choix: 1
Sujet: Biology
Question: What is photosynthesis?
Question ajoutée et propagée!
```

### 2. View Questions (Menu Option 2)
```
Choix: 2
Sujet (vide = tous): Biology
```

Shows all questions with:
- ID
- Subject
- Content
- Author
- Timestamp

### 3. Connect to Peer (Menu Option 3)
```
Choix: 3
Host du peer: localhost
Port du peer: 1100
ID du peer à utiliser: student1
```

Connects your peer to another peer to exchange questions.

### 4. View Known Peers (Menu Option 4)
```
Choix: 4
```

Displays:
- Peer ID
- Host
- Port
- Last seen timestamp

### 5. View Statistics (Menu Option 5)
```
Choix: 5
ID étudiant: student1
```

Shows:
- Student ID
- Subject scores
- Last update time

### 6. Update Statistics (Menu Option 6)
```
Choix: 6
ID étudiant: student1
Sujet: Biology
Score: 85
```

Updates performance records.

### 0. Exit (Menu Option 0)
```
Choix: 0
Arrêt du peer...
```

Cleanly shuts down the peer.

## Example Workflow

### Terminal 1 - Start Registry:
```bash
rmiregistry 1099
```

### Terminal 2 - First Student:
```bash
java -cp bin rmi.Main
ID du peer: student1
Host: localhost
Port RMI: 1099

Choix: 1
Sujet: Mathematics
Question: What is 2+2?
→ Question ajoutée et propagée!

Choix: 0
```

### Terminal 3 - Second Student:
```bash
java -cp bin rmi.Main
ID du peer: student2
Host: localhost
Port RMI: 1100

Choix: 3
Host du peer: localhost
Port du peer: 1099
ID du peer: student1
→ Connecté à student1

Choix: 2
Sujet: Mathematics
→ Voit la question de student1

Choix: 0
```

## RMI vs CORBA Comparison

| Feature | RMI | CORBA |
|---------|-----|-------|
| UI | Console Menu | GUI (Client), Console (Server) |
| Setup | Simple (just rmiregistry) | Complex (orbd required) |
| Complexity | Lower (Java-native) | Higher (cross-platform) |
| Protocol | Java RMI | CORBA/IIOP |
| Answering | Manual entry | Interactive GUI |
| Peer to Peer | Peer-to-peer mesh | Client-Server hub |

## Important Notes

### Port Management
- Registry runs on 1099
- Each peer needs a unique port
- Make sure ports aren't in use: `netstat -ano | findstr :1099`

### Host Configuration
- **localhost**: For local testing
- **IP address**: For network testing (e.g., 192.168.1.100)
- Must be reachable by other peers

### Peer Connection
- Connect peer-to-peer through menu option 3
- Questions automatically sync when connected
- Disconnecting doesn't lose data

### Data Persistence
- All data stored in memory
- Lost when peer stops
- No database persistence

## Troubleshooting

### "Registry not found"
```bash
# Make sure rmiregistry is running
rmiregistry 1099
```

### "Port already in use"
```bash
# Use a different port or kill the process using it
netstat -ano | findstr :1099
taskkill /PID <PID> /F
```

### "Connection refused"
- Check peer is running
- Verify host and port are correct
- Check firewall settings

### "ClassNotFoundException"
- Ensure you compiled with: `javac -encoding UTF-8 -d bin src/**/*.java`
- Check classpath includes bin directory

## Running Multiple Instances

Create a batch script `run_peer.bat`:
```batch
@echo off
cd "c:\Users\lajmi\OneDrive\Desktop\distributed-study-system\P2p"
set /p PEER_ID="Enter Peer ID: "
set /p PORT="Enter RMI Port: "
java -cp bin rmi.Main <<EOF
%PEER_ID%
localhost
%PORT%
EOF
```

Then run multiple times:
```bash
run_peer.bat
```

## Differences from CORBA Mode

**RMI Mode (Console-based):**
- ✓ Pure Java implementation
- ✓ Simpler setup
- ✓ Direct peer-to-peer mesh
- ✗ Manual input (no GUI)
- ✗ Text-based answers

**CORBA Mode (GUI-based):**
- ✓ Interactive GUI interface
- ✓ Visual question answering
- ✓ Better user experience
- ✗ Complex setup (orbd)
- ✗ Client-Server architecture (not P2P)

## Next Steps

- Run RMI peers and create questions
- Connect multiple peers and verify synchronization
- Track statistics and updates
- For GUI experience, use CORBA mode instead
