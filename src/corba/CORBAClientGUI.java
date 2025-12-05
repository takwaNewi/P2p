package corba;

import StudySystem.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class CORBAClientGUI extends JFrame {
    private JTabbedPane tabbedPane;
    private JTextArea logArea;
    private JLabel statusLabel;
    private JTable questionsTable;
    private DefaultTableModel tableModel;
    
    private Peer peer;
    private String clientId;
    
    public CORBAClientGUI(String clientId, Peer peer) {
        this.clientId = clientId;
        this.peer = peer;
        
        setTitle("CORBA Client - " + clientId);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel supérieur avec info
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Client Info"));
        
        JLabel clientLabel = new JLabel("Client ID: " + clientId);
        clientLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        
        statusLabel = new JLabel("Status: CONNECTED");
        statusLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        statusLabel.setForeground(Color.GREEN);
        
        infoPanel.add(clientLabel);
        infoPanel.add(statusLabel);
        
        // Tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Tab 1: View Questions
        tabbedPane.addTab("View Questions", createQuestionsPanel());
        
        // Tab 2: Answer Questions
        tabbedPane.addTab("Answer Questions", createAnswerPanel());
        
        // Tab 3: Add Question
        tabbedPane.addTab("Add Question", createAddQuestionPanel());
        
        // Tab 4: Activity Log
        tabbedPane.addTab("Activity Log", createLogPanel());
        
        // Assemblage
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        add(mainPanel);
        setVisible(true);
        
        log("Client initialized: " + clientId);
    }
    
    private JPanel createQuestionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Contrôles
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        JLabel subjectLabel = new JLabel("Filter by Subject:");
        JTextField subjectField = new JTextField(15);
        
        JButton filterButton = new JButton("Filter");
        JButton refreshButton = new JButton("Refresh All");
        
        controlPanel.add(subjectLabel);
        controlPanel.add(subjectField);
        controlPanel.add(filterButton);
        controlPanel.add(refreshButton);
        
        // Table
        String[] columns = {"Subject", "Question", "Author", "Timestamp"};
        tableModel = new DefaultTableModel(columns, 0);
        questionsTable = new JTable(tableModel);
        questionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        questionsTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        questionsTable.getColumnModel().getColumn(1).setPreferredWidth(350);
        questionsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        questionsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        
        JScrollPane scrollPane = new JScrollPane(questionsTable);
        
        // Action listeners
        refreshButton.addActionListener(e -> loadAllQuestions());
        filterButton.addActionListener(e -> loadQuestionsBySubject(subjectField.getText()));
        
        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load initial questions
        loadAllQuestions();
        
        return panel;
    }
    
    private JPanel createAddQuestionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Subject
        JLabel subjectLabel = new JLabel("Subject:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(subjectLabel, gbc);
        
        JTextField subjectField = new JTextField(30);
        gbc.gridx = 1;
        panel.add(subjectField, gbc);
        
        // Question content
        JLabel contentLabel = new JLabel("Question:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTH;
        panel.add(contentLabel, gbc);
        
        JTextArea contentArea = new JTextArea(8, 30);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(contentArea);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        panel.add(scrollPane, gbc);
        
        // Answer options
        JLabel answersLabel = new JLabel("Answers (A, B, C, D):");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(answersLabel, gbc);
        
        JPanel answersPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        JTextField[] answerFields = new JTextField[4];
        String[] answerLabels = {"A:", "B:", "C:", "D:"};
        for (int i = 0; i < 4; i++) {
            answersPanel.add(new JLabel(answerLabels[i]));
            answerFields[i] = new JTextField(20);
            answersPanel.add(answerFields[i]);
        }
        gbc.gridx = 1;
        panel.add(answersPanel, gbc);
        
        // Correct answer
        JLabel correctLabel = new JLabel("Correct Answer:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(correctLabel, gbc);
        
        String[] options = {"A", "B", "C", "D"};
        JComboBox<String> correctCombo = new JComboBox<>(options);
        gbc.gridx = 1;
        panel.add(correctCombo, gbc);
        
        // Submit button
        JButton submitButton = new JButton("Add Question");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        submitButton.addActionListener(e -> {
            try {
                Question q = new Question();
                q.subject = subjectField.getText();
                q.content = contentArea.getText();
                q.id = UUID.randomUUID().toString();
                q.authorId = clientId;
                
                String[] answers = new String[4];
                for (int i = 0; i < 4; i++) {
                    answers[i] = answerFields[i].getText();
                }
                q.answers = answers;
                q.correctAnswer = (String) correctCombo.getSelectedItem();
                q.timestamp = System.currentTimeMillis();
                
                peer.shareQuestion(q);
                
                JOptionPane.showMessageDialog(panel, "Question added successfully!");
                log("Question added: " + q.subject);
                
                // Clear fields
                subjectField.setText("");
                contentArea.setText("");
                for (JTextField f : answerFields) f.setText("");
                correctCombo.setSelectedIndex(0);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                log("Error adding question: " + ex.getMessage());
            }
        });
        panel.add(submitButton, gbc);
        
        return panel;
    }
    
    private JPanel createAnswerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Question selector
        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JLabel filterLabel = new JLabel("Filter by Subject:");
        JTextField filterField = new JTextField(15);
        JButton loadButton = new JButton("Load Questions");
        
        selectorPanel.add(filterLabel);
        selectorPanel.add(filterField);
        selectorPanel.add(loadButton);
        
        // Question display area
        JPanel questionPanel = new JPanel(new GridBagLayout());
        questionPanel.setBorder(BorderFactory.createTitledBorder("Question"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel subjectLabel = new JLabel("Subject:");
        JLabel subjectValueLabel = new JLabel("No question selected");
        subjectValueLabel.setFont(new Font("Monospaced", Font.BOLD, 11));
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        questionPanel.add(subjectLabel, gbc);
        gbc.gridx = 1;
        questionPanel.add(subjectValueLabel, gbc);
        
        JLabel contentLabel = new JLabel("Content:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTH;
        questionPanel.add(contentLabel, gbc);
        
        JTextArea contentArea = new JTextArea(5, 40);
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(contentArea);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        questionPanel.add(scrollPane, gbc);
        
        // Answer selection
        JPanel answerPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        answerPanel.setBorder(BorderFactory.createTitledBorder("Select Answer"));
        
        ButtonGroup answerGroup = new ButtonGroup();
        JRadioButton[] answerButtons = new JRadioButton[4];
        JLabel[] answerLabels = new JLabel[4];
        
        String[] answerLetters = {"A", "B", "C", "D"};
        for (int i = 0; i < 4; i++) {
            answerButtons[i] = new JRadioButton();
            answerLabels[i] = new JLabel(answerLetters[i] + ": ");
            answerGroup.add(answerButtons[i]);
            
            JPanel answerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            answerRow.add(answerButtons[i]);
            answerRow.add(answerLabels[i]);
            answerPanel.add(answerRow);
        }
        
        // Questions list
        JPanel listPanel = new JPanel(new BorderLayout(10, 10));
        listPanel.setBorder(BorderFactory.createTitledBorder("Questions List"));
        
        String[] columns = {"Subject", "Author"};
        DefaultTableModel questionListModel = new DefaultTableModel(columns, 0);
        JTable questionListTable = new JTable(questionListModel);
        questionListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScroll = new JScrollPane(questionListTable);
        
        // Submit answer button
        JButton submitAnswerButton = new JButton("Submit Answer");
        JPanel submitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        submitPanel.add(submitAnswerButton);
        
        // Store current question
        final Question[] currentQuestion = new Question[1];
        
        // Load questions action
        loadButton.addActionListener(e -> {
            new Thread(() -> {
                try {
                    String subject = filterField.getText().trim();
                    Question[] questions = peer.getQuestions(subject);
                    SwingUtilities.invokeLater(() -> {
                        questionListModel.setRowCount(0);
                        for (Question q : questions) {
                            Object[] row = {q.subject, q.authorId};
                            questionListModel.addRow(row);
                        }
                        log("Loaded " + questions.length + " questions");
                    });
                } catch (Exception ex) {
                    log("Error loading questions: " + ex.getMessage());
                }
            }).start();
        });
        
        // Select question action
        questionListTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = questionListTable.getSelectedRow();
            if (selectedRow >= 0) {
                new Thread(() -> {
                    try {
                        String subject = (String) questionListModel.getValueAt(selectedRow, 0);
                        Question[] questions = peer.getQuestions(subject);
                        if (questions.length > 0) {
                            currentQuestion[0] = questions[0];
                            SwingUtilities.invokeLater(() -> {
                                subjectValueLabel.setText(currentQuestion[0].subject);
                                contentArea.setText(currentQuestion[0].content);
                                
                                // Clear previous selections
                                for (JRadioButton btn : answerButtons) {
                                    btn.setSelected(false);
                                }
                                
                                // Display answer options
                                if (currentQuestion[0].answers != null) {
                                    for (int i = 0; i < Math.min(4, currentQuestion[0].answers.length); i++) {
                                        answerLabels[i].setText(new String[]{
                                            "A", "B", "C", "D"
                                        }[i] + ": " + currentQuestion[0].answers[i]);
                                    }
                                }
                                log("Question selected: " + currentQuestion[0].subject);
                            });
                        }
                    } catch (Exception ex) {
                        log("Error selecting question: " + ex.getMessage());
                    }
                }).start();
            }
        });
        
        // Submit answer action
        submitAnswerButton.addActionListener(e -> {
            if (currentQuestion[0] == null) {
                JOptionPane.showMessageDialog(panel, "Please select a question first", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String selectedAnswer = null;
            String[] letters = {"A", "B", "C", "D"};
            for (int i = 0; i < 4; i++) {
                if (answerButtons[i].isSelected()) {
                    selectedAnswer = letters[i];
                    break;
                }
            }
            
            if (selectedAnswer == null) {
                JOptionPane.showMessageDialog(panel, "Please select an answer", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean isCorrect = selectedAnswer.equals(currentQuestion[0].correctAnswer);
            String result = isCorrect ? "✓ CORRECT!" : "✗ INCORRECT";
            String message = result + "\nCorrect answer: " + currentQuestion[0].correctAnswer;
            
            log("Answer submitted: " + selectedAnswer + " - " + (isCorrect ? "CORRECT" : "INCORRECT"));
            JOptionPane.showMessageDialog(panel, message, "Answer Result", 
                isCorrect ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
        });
        
        // Assemble panels
        listPanel.add(listScroll, BorderLayout.CENTER);
        
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.add(answerPanel, BorderLayout.CENTER);
        rightPanel.add(submitPanel, BorderLayout.SOUTH);
        
        JSplitPane centerPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listPanel, rightPanel);
        centerPanel.setDividerLocation(400);
        
        panel.add(selectorPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
        logArea.setBackground(Color.BLACK);
        logArea.setForeground(Color.GREEN);
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        
        JButton clearButton = new JButton("Clear Log");
        clearButton.addActionListener(e -> logArea.setText(""));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(clearButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadAllQuestions() {
        new Thread(() -> {
            try {
                Question[] questions = peer.getQuestions("");
                SwingUtilities.invokeLater(() -> {
                    tableModel.setRowCount(0);
                    for (Question q : questions) {
                        Object[] row = {
                            q.subject,
                            q.content,
                            q.authorId,
                            new Date(q.timestamp)
                        };
                        tableModel.addRow(row);
                    }
                    log("Loaded " + questions.length + " questions");
                });
            } catch (Exception e) {
                log("Error loading questions: " + e.getMessage());
            }
        }).start();
    }
    
    private void loadQuestionsBySubject(String subject) {
        new Thread(() -> {
            try {
                Question[] questions = peer.getQuestions(subject);
                SwingUtilities.invokeLater(() -> {
                    tableModel.setRowCount(0);
                    for (Question q : questions) {
                        Object[] row = {
                            q.subject,
                            q.content,
                            q.authorId,
                            new Date(q.timestamp)
                        };
                        tableModel.addRow(row);
                    }
                    log("Filtered: " + questions.length + " questions for subject '" + subject + "'");
                });
            } catch (Exception e) {
                log("Error filtering questions: " + e.getMessage());
            }
        }).start();
    }
    
    public void log(String message) {
        SwingUtilities.invokeLater(() -> {
            if (logArea != null) {
                String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
                logArea.append("[" + timestamp + "] " + message + "\n");
                logArea.setCaretPosition(logArea.getDocument().getLength());
            }
        });
    }
}
