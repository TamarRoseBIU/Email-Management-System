package com.example.myemailapp.data.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.myemailapp.data.database.converter.StringListConverter;

import java.util.List;

@Entity(tableName = "emails")
@TypeConverters({StringListConverter.class})
public class EmailEntity {
    @PrimaryKey
    private String id;

    private String subject;
    private String sender;
    private String content;
    private String timestamp;
    private boolean isRead;
    private boolean isStarred;
    private boolean isDeleted;
    private boolean isSpam;
    private boolean isInTrash;
    private List<String> labels;
    private String folder; // inbox, sent, drafts, etc.

    // Constructors
    public EmailEntity() {}

    public EmailEntity(String id, String subject, String sender, String content,
                       String timestamp, boolean isRead, boolean isStarred,
                       boolean isDeleted, boolean isSpam, boolean isInTrash,
                       List<String> labels, String folder) {
        this.id = id;
        this.subject = subject;
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
        this.isRead = isRead;
        this.isStarred = isStarred;
        this.isDeleted = isDeleted;
        this.isSpam = isSpam;
        this.isInTrash = isInTrash;
        this.labels = labels;
        this.folder = folder;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public boolean isStarred() { return isStarred; }
    public void setStarred(boolean starred) { isStarred = starred; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    public boolean isSpam() { return isSpam; }
    public void setSpam(boolean spam) { isSpam = spam; }

    public boolean isInTrash() { return isInTrash; }
    public void setInTrash(boolean inTrash) { isInTrash = inTrash; }

    public List<String> getLabels() { return labels; }
    public void setLabels(List<String> labels) { this.labels = labels; }

    public String getFolder() { return folder; }
    public void setFolder(String folder) { this.folder = folder; }
}