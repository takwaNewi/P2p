package models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Statistics implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String studentId;
    private Map<String, Integer> subjectScores;
    private Map<String, Integer> questionsSolved;
    private long lastUpdate;
    
    public Statistics(String studentId) {
        this.studentId = studentId;
        this.subjectScores = new HashMap<>();
        this.questionsSolved = new HashMap<>();
        this.lastUpdate = System.currentTimeMillis();
    }
    
    public void updateScore(String subject, int score) {
        subjectScores.put(subject, 
            subjectScores.getOrDefault(subject, 0) + score);
        questionsSolved.put(subject, 
            questionsSolved.getOrDefault(subject, 0) + 1);
        this.lastUpdate = System.currentTimeMillis();
    }
    
    public String getStudentId() { return studentId; }
    public Map<String, Integer> getSubjectScores() { return subjectScores; }
    public Map<String, Integer> getQuestionsSolved() { return questionsSolved; }
    public long getLastUpdate() { return lastUpdate; }
}