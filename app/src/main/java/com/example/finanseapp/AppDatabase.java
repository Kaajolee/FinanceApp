package com.example.finanseapp;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.finanseapp.Daos.AccountDao;
import com.example.finanseapp.Daos.CategoryDao;
import com.example.finanseapp.Daos.EntryDao;
import com.example.finanseapp.Daos.UserDao;
import com.example.finanseapp.Entities.Account;
import com.example.finanseapp.Entities.Category;
import com.example.finanseapp.Entities.Entry;
import com.example.finanseapp.Entities.User;

@Database(entities = {User.class, Account.class, Entry.class, Category.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "finance-db").build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract UserDao userDao();

    public abstract AccountDao accountDao();

    public abstract EntryDao entryDao();

    public abstract CategoryDao categoryDao();

    public int currentAccount = 1;
}

