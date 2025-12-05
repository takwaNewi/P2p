package rmi;

import models.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore {
    private String ownerId;
    private Map<String, Question> questions;
    private Map<String, List<Question>> questionsBySubject;
    private Map<String, Statistics> statistics;
    private Map<String, Long> questionVersions;
    
    public DataStore(String ownerId) {
        this.ownerId = ownerId;
        this.questions = new ConcurrentHashMap<>();
        this.questionsBySubject = new ConcurrentHashMap<>();
        this.statistics = new ConcurrentHashMap<>();
        this.questionVersions = new ConcurrentHashMap<>();
    }
    
    public void addQuestion(Question q) {
        String qId = q.getId();
        
        Long existingVersion = questionVersions.get(qId);
        if (existingVersion != null && existingVersion >= q.getTimestamp()) {
            return;
        }
        
        questions.put(qId, q);
        questionVersions.put(qId, q.getTimestamp());
        
        questionsBySubject.computeIfAbsent(q.getSubject(), k -> new ArrayList<>())
            .add(q);
        
        System.out.println("Question ajoutée: " + q.getId());
    }
    
    public List<Question> getQuestions(String subject) {
        return questionsBySubject.getOrDefault(subject, new ArrayList<>());
    }
    
    public List<Question> getAllQuestions() {
        return new ArrayList<>(questions.values());
    }
    
    public void updateStatistics(Statistics stats) {
        String studentId = stats.getStudentId();
        Statistics existing = statistics.get(studentId);
        
        if (existing == null || stats.getLastUpdate() > existing.getLastUpdate()) {
            statistics.put(studentId, stats);
            System.out.println(" Statistiques mises à jour: " + studentId);
        }
    }
    
    public Statistics getStatistics(String studentId) {
        return statistics.getOrDefault(studentId, new Statistics(studentId));
    }
    
    public int getQuestionsCount() {
        return questions.size();
    }
}