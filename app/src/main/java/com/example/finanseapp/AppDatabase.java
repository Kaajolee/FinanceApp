package com.example.finanseapp;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.finanseapp.Daos.AccountDao;
import com.example.finanseapp.Daos.EntryDao;
import com.example.finanseapp.Daos.UserDao;
import com.example.finanseapp.Entities.Account;
import com.example.finanseapp.Entities.Entry;
import com.example.finanseapp.Entities.User;

@Database(entities = {User.class, Account.class, Entry.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase Instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (Instance == null) {
            Instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "my_database.db")
                            .build();
        }
        return Instance;
    }

    public abstract UserDao userDao();
    public abstract AccountDao accountDao();
    public abstract EntryDao entryDao();
}
