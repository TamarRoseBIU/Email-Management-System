package com.example.myemailapp.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myemailapp.data.database.entity.EmailEntity;

import java.util.List;

@Dao
public interface EmailDao {

    // Trash-specific queries
    @Query("SELECT * FROM emails WHERE isInTrash = 1 AND isDeleted = 0 ORDER BY timestamp DESC")
    LiveData<List<EmailEntity>> getTrashEmails();

    @Query("SELECT * FROM emails WHERE isInTrash = 1 AND isDeleted = 0 ORDER BY timestamp DESC")
    List<EmailEntity> getTrashEmailsSync();

    // General email queries
    @Query("SELECT * FROM emails WHERE id = :emailId")
    EmailEntity getEmailById(String emailId);

    @Query("SELECT * FROM emails WHERE id = :emailId")
    LiveData<EmailEntity> getEmailByIdLiveData(String emailId);

    @Query("SELECT * FROM emails WHERE folder = :folder AND isDeleted = 0 AND isInTrash = 0 ORDER BY timestamp DESC")
    LiveData<List<EmailEntity>> getEmailsByFolder(String folder);

    @Query("SELECT * FROM emails WHERE isSpam = 1 AND isDeleted = 0 ORDER BY timestamp DESC")
    LiveData<List<EmailEntity>> getSpamEmails();

    @Query("SELECT * FROM emails WHERE isStarred = 1 AND isDeleted = 0 AND isInTrash = 0 ORDER BY timestamp DESC")
    LiveData<List<EmailEntity>> getStarredEmails();

    // Insert and update operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEmail(EmailEntity email);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEmails(List<EmailEntity> emails);

    @Update
    void updateEmail(EmailEntity email);

    @Delete
    void deleteEmail(EmailEntity email);

    // Update operations for specific actions
    @Query("UPDATE emails SET isRead = :isRead WHERE id = :emailId")
    void updateReadStatus(String emailId, boolean isRead);

    @Query("UPDATE emails SET isStarred = :isStarred WHERE id = :emailId")
    void updateStarStatus(String emailId, boolean isStarred);

    @Query("UPDATE emails SET isInTrash = :isInTrash WHERE id = :emailId")
    void updateTrashStatus(String emailId, boolean isInTrash);

    @Query("UPDATE emails SET isSpam = :isSpam WHERE id = :emailId")
    void updateSpamStatus(String emailId, boolean isSpam);

    @Query("UPDATE emails SET isDeleted = :isDeleted WHERE id = :emailId")
    void updateDeleteStatus(String emailId, boolean isDeleted);

    @Query("UPDATE emails SET labels = :labels WHERE id = :emailId")
    void updateLabels(String emailId, List<String> labels);

    // Restore from trash (move back to original folder)
    @Query("UPDATE emails SET isInTrash = 0 WHERE id = :emailId")
    void restoreFromTrash(String emailId);

    // Permanently delete from trash
    @Query("UPDATE emails SET isDeleted = 1 WHERE id = :emailId")
    void permanentlyDelete(String emailId);

    // Clear all data (for testing or reset)
    @Query("DELETE FROM emails")
    void clearAll();

    // Count queries
    @Query("SELECT COUNT(*) FROM emails WHERE isInTrash = 1 AND isDeleted = 0")
    int getTrashEmailCount();

    @Query("SELECT COUNT(*) FROM emails WHERE folder = :folder AND isDeleted = 0 AND isInTrash = 0")
    int getEmailCountByFolder(String folder);

    @Query("SELECT COUNT(*) FROM emails WHERE isRead = 0 AND isDeleted = 0 AND isInTrash = 0")
    int getUnreadEmailCount();
}