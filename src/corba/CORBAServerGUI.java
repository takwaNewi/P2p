package corba;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CORBAServerGUI extends JFrame {
    private JTextArea logArea;
    private JLabel statusLabel;
    private JLabel serverInfoLabel;
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    public CORBAServerGUI(String peerId, int port) {
        setTitle("CORBA Server - " + peerId);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel supérieur avec infos
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Server Information"));
        
        serverInfoLabel = new JLabel("Peer ID: " + peerId + " | Port: " + port);
        serverInfoLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        
        statusLabel = new JLabel("Status: INITIALIZING...");
        statusLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        statusLabel.setForeground(Color.BLUE);
        
        JLabel timeLabel = new JLabel("Started: " + LocalDateTime.now().format(timeFormatter));
        timeLabel.setFont(new Font("Monospaced", Font.PLAIN, 10));
        
        infoPanel.add(serverInfoLabel);
        infoPanel.add(statusLabel);
        infoPanel.add(timeLabel);
        
        // Area de logs
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
        logArea.setBackground(Color.BLACK);
        logArea.setForeground(Color.GREEN);
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Activity Log"));
        
        // Panel boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        JButton clearButton = new JButton("Clear Log");
        clearButton.addActionListener(e -> logArea.setText(""));
        
        JButton exitButton = new JButton("Exit Server");
        exitButton.addActionListener(e -> System.exit(0));
        
        buttonPanel.add(clearButton);
        buttonPanel.add(exitButton);
        
        // Assemblage
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        setVisible(true);
    }
    
    public void log(String message) {
        SwingUtilities.invokeLater(() -> {
            String timestamp = LocalDateTime.now().format(timeFormatter);
            logArea.append("[" + timestamp + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
    
    public void setStatus(String status, Color color) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Status: " + status);
            statusLabel.setForeground(color);
        });
    }
}
