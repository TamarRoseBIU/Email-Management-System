package com.example.myemailapp.utils;

import com.example.myemailapp.data.database.entity.EmailEntity;
import com.example.myemailapp.model.Email;

import java.util.ArrayList;
import java.util.List;

public class EmailMapper {

    /**
     * Convert Email model to EmailEntity for Room database
     */
    public static EmailEntity toEntity(Email email) {
        if (email == null) {
            return null;
        }

        EmailEntity entity = new EmailEntity();
        entity.setId(email.getId());
        entity.setSubject(email.getSubject());
        entity.setSender(email.getSender());
        entity.setContent(email.getContent());
        entity.setTimestamp(email.getTimestamp());
        entity.setRead(email.isRead());
        entity.setStarred(email.isStarred());
        entity.setDeleted(email.isDeleted());
        entity.setSpam(email.isSpam());
        entity.setInTrash(email.isInTrash());
        entity.setLabels(email.getLabels());
        entity.setFolder(email.getFolder());

        return entity;
    }

    /**
     * Convert EmailEntity from Room database to Email model
     */
    public static Email fromEntity(EmailEntity entity) {
        if (entity == null) {
            return null;
        }

        Email email = new Email();
        email.setId(entity.getId());
        email.setSubject(entity.getSubject());
        email.setSender(entity.getSender());
        email.setContent(entity.getContent());
        email.setTimestamp(entity.getTimestamp());
        email.setRead(entity.isRead());
        email.setStarred(entity.isStarred());
        email.setDeleted(entity.isDeleted());
        email.setSpam(entity.isSpam());
        email.setInTrash(entity.isInTrash());
        email.setLabels(entity.getLabels());
        email.setFolder(entity.getFolder());

        return email;
    }

    /**
     * Convert list of Email models to list of EmailEntity
     */
    public static List<EmailEntity> toEntityList(List<Email> emails) {
        if (emails == null) {
            return null;
        }

        List<EmailEntity> entities = new ArrayList<>();
        for (Email email : emails) {
            EmailEntity entity = toEntity(email);
            if (entity != null) {
                entities.add(entity);
            }
        }
        return entities;
    }

    /**
     * Convert list of EmailEntity to list of Email models
     */
    public static List<Email> fromEntityList(List<EmailEntity> entities) {
        if (entities == null) {
            return null;
        }

        List<Email> emails = new ArrayList<>();
        for (EmailEntity entity : entities) {
            Email email = fromEntity(entity);
            if (email != null) {
                emails.add(email);
            }
        }
        return emails;
    }
}