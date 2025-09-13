package com.example.myemailapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.ArrayList;

public class Email {
    @SerializedName("id")
    private String id;

    @SerializedName("subject")
    private String subject;

    @SerializedName("from")
    private String sender;

    @SerializedName("to")
    private List<String> to;

    @SerializedName("body")
    private String content;

    @SerializedName("timeStamp")
    private String timestamp;

    @SerializedName("isRead")
    private boolean isRead;

    @SerializedName("isStarred")
    private boolean isStarred;

    @SerializedName("isDeleted")
    private boolean isDeleted;

    @SerializedName("isSpam")
    private boolean isSpam;

    @SerializedName("isInTrash")
    private boolean isInTrash;

    @SerializedName("labels")
    private List<String> labels;

    @SerializedName("trashSource")
    private String trashSource;

    private String folder; // inbox, sent, drafts, etc.

    // Constructors
    public Email() {}

    public Email(String id, String subject, String sender, List<String> to, String content,
                 String timestamp, boolean isRead, boolean isStarred,
                 boolean isDeleted, boolean isSpam, boolean isInTrash,
                 List<String> labels, String folder) {
        this.id = id;
        this.subject = subject;
        this.sender = sender;
        this.to = to != null ? new ArrayList<>(to) : null;
        this.content = content;
        this.timestamp = timestamp;
        this.isRead = isRead;
        this.isStarred = isStarred;
        this.isDeleted = isDeleted;
        this.isSpam = isSpam;
        this.isInTrash = isInTrash;
        this.labels = labels != null ? new ArrayList<>(labels) : null;
        this.folder = folder;
    }

    // Constructor with trashSource
    public Email(String id, String subject, String sender, List<String> to, String content,
                 String timestamp, boolean isRead, boolean isStarred,
                 boolean isDeleted, boolean isSpam, boolean isInTrash,
                 List<String> labels, String folder, String trashSource) {
        this(id, subject, sender, to, content, timestamp, isRead, isStarred,
                isDeleted, isSpam, isInTrash, labels, folder);
        this.trashSource = trashSource;
    }

    // Copy constructor
    public Email(Email other) {
        this.id = other.id;
        this.subject = other.subject;
        this.sender = other.sender;
        this.to = other.to != null ? new ArrayList<>(other.to) : null;
        this.content = other.content;
        this.timestamp = other.timestamp;
        this.isRead = other.isRead;
        this.isStarred = other.isStarred;
        this.isDeleted = other.isDeleted;
        this.isSpam = other.isSpam;
        this.isInTrash = other.isInTrash;
        this.labels = other.labels != null ? new ArrayList<>(other.labels) : null;
        this.folder = other.folder;
        this.trashSource = other.trashSource;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getFrom() { return sender; }

    public List<String> getTo() { return to; }
    public void setTo(List<String> to) { this.to = to != null ? new ArrayList<>(to) : null; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getBody() { return content; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getTimeStamp() { return String.valueOf(timestamp); }

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
    public void setLabels(List<String> labels) { this.labels = labels != null ? new ArrayList<>(labels) : null; }

    public String getFolder() { return folder; }
    public void setFolder(String folder) { this.folder = folder; }

    public String getTrashSource() { return trashSource; }
    public void setTrashSource(String trashSource) { this.trashSource = trashSource; }

    public boolean isFromTrash() {
        return trashSource != null;
    }

    @Override
    public String toString() {
        return "Email{" +
                "id='" + id + '\'' +
                ", from='" + sender + '\'' +
                ", subject='" + subject + '\'' +
                ", timeStamp=" + timestamp +
                ", isRead=" + isRead +
                ", isStarred=" + isStarred +
                ", isDeleted=" + isDeleted +
                ", isSpam=" + isSpam +
                ", isInTrash=" + isInTrash +
                ", labels=" + labels +
                ", folder='" + folder + '\'' +
                ", trashSource=" + trashSource +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return timestamp == email.timestamp &&
                isRead == email.isRead &&
                isStarred == email.isStarred &&
                isDeleted == email.isDeleted &&
                isSpam == email.isSpam &&
                isInTrash == email.isInTrash &&
                java.util.Objects.equals(id, email.id) &&
                java.util.Objects.equals(subject, email.subject) &&
                java.util.Objects.equals(sender, email.sender) &&
                java.util.Objects.equals(to, email.to) &&
                java.util.Objects.equals(content, email.content) &&
                java.util.Objects.equals(labels, email.labels) &&
                java.util.Objects.equals(folder, email.folder) &&
                java.util.Objects.equals(trashSource, email.trashSource);
    }
}