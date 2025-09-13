package com.example.myemailapp.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.myemailapp.data.database.converter.StringListConverter;
import com.example.myemailapp.data.database.dao.EmailDao;
import com.example.myemailapp.data.database.entity.EmailEntity;

@Database(
        entities = {EmailEntity.class},
        version = 1,
        exportSchema = false
)
@TypeConverters({StringListConverter.class})
public abstract class EmailDatabase extends RoomDatabase {

    private static volatile EmailDatabase INSTANCE;

    public abstract EmailDao emailDao();

    public static EmailDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (EmailDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            EmailDatabase.class,
                            "email_database"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }

    // For testing purposes
    public static void destroyInstance() {
        INSTANCE = null;
    }
}