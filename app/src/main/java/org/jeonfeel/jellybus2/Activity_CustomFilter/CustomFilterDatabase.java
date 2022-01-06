package org.jeonfeel.jellybus2.Activity_CustomFilter;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {CustomFilter.class},version = 1)
public abstract class CustomFilterDatabase extends RoomDatabase {
    public abstract CustomFilterDao customFilterDao();
    private static CustomFilterDatabase instance;
    private static final Object sLock = new Object();

    //create Room DB instance using singleTon
    public static CustomFilterDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (instance == null) {
                instance = Room.databaseBuilder(context.getApplicationContext()
                        , CustomFilterDatabase.class
                        , "CustomFilterDatabase")
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build();
            }
            return instance;
        }
    }

}
