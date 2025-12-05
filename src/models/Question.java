package models;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class Question implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String subject;
    private String content;
    private List<String> answers;
    private String correctAnswer;
    private long timestamp;
    private String authorId;
    
    public Question(String id, String subject, String content, 
                   List<String> answers, String correctAnswer, String authorId) {
        this.id = id;
        this.subject = subject;
        this.content = content;
        this.answers = answers;
        this.correctAnswer = correctAnswer;
        this.timestamp = System.currentTimeMillis();
        this.authorId = authorId;
    }
    
    // Getters
    public String getId() { return id; }
    public String getSubject() { return subject; }
    public String getContent() { return content; }
    public List<String> getAnswers() { return answers; }
    public String getCorrectAnswer() { return correctAnswer; }
    public long getTimestamp() { return timestamp; }
    public String getAuthorId() { return authorId; }
}