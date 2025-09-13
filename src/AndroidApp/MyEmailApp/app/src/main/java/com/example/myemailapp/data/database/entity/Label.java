package com.example.myemailapp.data.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "labels")
public class Label {
    @PrimaryKey
    @NonNull
    @SerializedName("_id") // This tells Gson to map "_id" from JSON to this field
    private String id;

    @NonNull
    private String name;

    private long lastUpdated;
    public Label() {
        this.id = " ";
        this.name = " ";
        this.lastUpdated = System.currentTimeMillis();
    }

    public Label(@NonNull String id, @NonNull String name) {
        this.id = id;
        this.name = name;
        this.lastUpdated = System.currentTimeMillis();
    }

    // Getters and setters
    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return name;
    }
}