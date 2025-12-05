# CORBA Client-Server Question System - Quick Start

## Architecture

```
┌─────────────────────────┐
│  CORBA Server           │
│  (Receives/Resends Q's) │
│  Console Only - No GUI  │
└──────────┬──────────────┘
           │
    ┌──────┴──────┐
    │             │
┌───▼──┐      ┌───▼──┐
│ GUI  │      │ GUI  │
│      │      │      │
│Client│      │Client│
└──────┘      └──────┘
```

## Prerequisites

- Java 8+ installed
- `orbd` (CORBA naming service) installed

## Step 1: Start the CORBA Naming Service

In a terminal:
```bash
orbd -ORBInitialPort 1050 -ORBInitialHost localhost
```

**Output:**
```
ORBD is now running
```

## Step 2: Start the CORBA Server (No GUI)

In a new terminal:
```bash
cd "c:\Users\lajmi\OneDrive\Desktop\distributed-study-system\P2p"
java -cp bin corba.CORBAServer
```

**Output:**
```
╔════════════════════════════════════╗
║  CORBA Server - Questions Hub      ║
╚════════════════════════════════════╝

➜ Starting CORBA Server...
  Peer ID: QuestionServer
  Port: 1050
  Host: localhost

✓ ORB initialized
✓ POA activated
✓ Peer registered: Peer-QuestionServer

[*] Server is running. Waiting for clients...
```

The server will log each client connection and question sharing:
```
[Server] Question received: Biology from student1
[Server] Peer registered: student1
```

## Step 3: Run Client Applications (With GUI)

In new terminals (one per client):
```bash
cd "c:\Users\lajmi\OneDrive\Desktop\distributed-study-system\P2p"
java -cp bin corba.CORBAClient
```

**Console Prompts:**
```
╔════════════════════════════════════╗
║  CORBA Client - Question Manager   ║
╚════════════════════════════════════╝

Your client ID (ex: student1): student1
Port ORB (défaut: 1050): 
```

After entering your client ID and port, a GUI window opens with four tabs:

### Client GUI Features

#### Tab 1: View Questions
- **Filter by Subject:** Search for questions by subject
- **Refresh All:** Load all questions from server
- **Table Display:** Shows Subject, Question, Author, Timestamp

#### Tab 2: Answer Questions ⭐ NEW
- **Filter by Subject:** Select questions to answer
- **Load Questions:** Load filtered questions
- **Question Display:** Shows selected question with content
- **Answer Selection:** Multiple choice (A, B, C, D) with radio buttons
- **Submit Answer:** Submit your answer and get immediate feedback
- **Scoring:** See if your answer is correct or incorrect
- **Activity Log:** Track all answered questions

#### Tab 3: Add Question
- **Subject:** Question subject/topic
- **Question:** Full question text
- **Answers:** Four answer options (A, B, C, D)
- **Correct Answer:** Select which is correct
- **Add Question:** Submit to server

#### Tab 4: Activity Log
- Real-time log of all client operations
- Clear Log button to reset
- Shows timestamps for all actions

## Example Workflow

### Client 1 (student1):
1. Run client as shown above
2. Go to "Add Question" tab
3. Add: Subject="Biology", Question="What is photosynthesis?", Answers=[Decomposition, Photosynthesis, Respiration, Fermentation], Correct=B
4. Click "Add Question"
5. Watch Activity Log for confirmation

### Client 2 (student2):
1. Run client in another terminal
2. Go to "View Questions" tab
3. Click "Refresh All" to see student1's question
4. Go to "Answer Questions" tab
5. Click "Load Questions" to load Biology questions
6. Click on the Biology question in the list
7. The question appears on the right with 4 answer options
8. Select answer B (Photosynthesis)
9. Click "Submit Answer"
10. See result: ✓ CORRECT!
4. Add your own questions
5. Other clients can immediately see them

## System Behavior

- **Server:** Accepts questions from clients, stores them, serves them to all clients
- **Clients:** Connect to server, can add/retrieve questions via GUI
- **Real-time:** Questions are immediately visible to all connected clients
- **Persistent:** Questions remain on server while it's running

## Troubleshooting

### "NameService not found"
- Make sure `orbd` is running on port 1050

### "Connection refused"
- Check that CORBAServer is running
- Verify port numbers match (1050)

### GUI doesn't appear
- Check console for error messages
- Ensure Java version supports Swing (Java 8+)

### Questions not appearing
- Click "Refresh All" in View Questions tab
- Check server console for any error messages

## Stopping the System

1. Close client windows (one by one)
2. Close server console
3. Stop orbd (Ctrl+C in naming service terminal)

## Notes

- Server has NO GUI (console only) - it just receives and resends questions
- Each client gets its own independent GUI window
- Questions are stored in server memory (lost when server stops)
- Multiple clients can run simultaneously
