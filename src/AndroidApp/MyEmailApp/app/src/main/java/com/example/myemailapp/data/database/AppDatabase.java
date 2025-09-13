package com.example.myemailapp.data.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;
import com.example.myemailapp.data.database.entity.Label;
import com.example.myemailapp.data.database.dao.LabelDao;

@Database(
        entities = {Label.class},
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract LabelDao labelDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "email_app_database"
                            )
                            .fallbackToDestructiveMigration() // Add this for development
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}