package com.example.myemailapp.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.myemailapp.data.database.entity.Label;
import java.util.List;

@Dao
public interface LabelDao {

    @Query("SELECT * FROM labels ORDER BY name ASC")
    LiveData<List<Label>> getAllLabels();

    @Query("SELECT * FROM labels ORDER BY name ASC")
    List<Label> getAllLabelsSync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLabels(List<Label> labels);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLabel(Label label);

    @Update
    void updateLabel(Label label);

    @Query("DELETE FROM labels")
    void deleteAllLabels();

    @Query("DELETE FROM labels WHERE id = :id")
    void deleteLabel(String id);

    @Query("SELECT COUNT(*) FROM labels")
    int getLabelsCount();
}