# CORBA GUI Setup Guide

## Quick Start

### Prerequisites
- Java 8+ installed
- `orbd` (CORBA naming service) running

### Step 1: Start the CORBA Naming Service
In a terminal/PowerShell:
```powershell
orbd -ORBInitialPort 1050 -ORBInitialHost localhost
```

### Step 2: Start the CORBA Server
In a new terminal:
```bash
cd c:\Users\lajmi\OneDrive\Desktop\distributed-study-system\P2p
java -cp bin corba.CORBAServer
```

When prompted:
- **ID du peer CORBA**: Enter a peer ID (e.g., `corba1`)
- **Port ORB**: Press Enter for default `1050`

A GUI window will appear showing the server status and activity log.

### Step 3: Connect CORBA Clients
In new terminals, run:
```bash
cd c:\Users\lajmi\OneDrive\Desktop\distributed-study-system\P2p
java -cp bin corba.CORBAClient
```

When prompted:
- **Port ORB**: Press Enter for default `1050`
- **ID du peer à contacter**: Enter the server peer ID (e.g., `corba1`)
- **Your client ID**: Enter a unique client ID (e.g., `client1`)

Each client will get its own GUI window showing messages and connection status.

## GUI Features

### Server GUI
- **Server Information Panel**: Shows Peer ID, Port, and Status
- **Status Indicator**: 
  - INITIALIZING (Blue)
  - RUNNING (Green)
  - ERROR (Red)
- **Activity Log**: Real-time log of all server operations
- **Buttons**: Clear Log, Exit Server

### Client GUI
- **Connection Information**: Shows Client ID, Connected Server, Port
- **Status Indicator**: Similar to server
- **Message Log**: Real-time activity log
- **Buttons**: Refresh, Clear Log, Disconnect

## Console Menu (Both Server & Client)

After GUI starts, you can use the console menu:

### Server Menu:
```
1. Ajouter question    - Add a new question
2. Voir questions      - View all questions
3. Voir peers          - View known peers
4. Test ping           - Test server connectivity
```

### Client Menu:
```
1. Voir questions      - View all questions (filtered by subject)
2. Ajouter question    - Add a new question
3. Info peer           - Get connected server info
4. Voir peers connus   - View known peers
0. Quitter             - Disconnect and exit
```

## Troubleshooting

### "Port already in use"
- Change the port in both commands (e.g., 1051, 1052, etc.)

### "NameService not found"
- Ensure `orbd` is running on the specified port

### "Connection refused"
- Check that server and naming service are running
- Verify ports match

## Architecture

```
┌──────────────────────────────────┐
│  orbd (Naming Service)           │
│  Port: 1050                      │
└────────────┬─────────────────────┘
             │
      ┌──────┴──────┐
      │             │
   ┌──▼──┐      ┌──▼──┐
   │     │      │     │
   │ GUI │      │ GUI │
   │  S  │      │  C  │
   │     │      │     │
   └──┬──┘      └──┬──┘
      │            │
  CORBA Server  CORBA Clients
```

## Files Modified/Created

- `src/corba/CORBAServerGUI.java` - NEW: Server GUI window
- `src/corba/CORBAClientGUI.java` - NEW: Client GUI window
- `src/corba/CORBAServer.java` - MODIFIED: Integrated GUI logging
- `src/corba/CORBAClient.java` - MODIFIED: Integrated GUI logging
