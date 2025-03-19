package com.example.finanseapp.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "accounts",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns =  "userId",
                onDelete = ForeignKey.CASCADE
        )
)
public class Account {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "userId")
    private String userId;

    @ColumnInfo(name = "balance")
    private float balance;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getBalance() {
        return balance;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
