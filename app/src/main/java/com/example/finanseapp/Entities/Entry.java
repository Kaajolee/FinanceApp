package com.example.finanseapp.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "entries",
        foreignKeys = @ForeignKey(
                entity = Account.class,
                parentColumns = "id",
                childColumns = "accountId",
                onDelete = ForeignKey.CASCADE
        )
)
public class Entry {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "accountId")
    private int accountId;


    //0 income 1 expense
    @ColumnInfo(name = "type")
    private int type;

    @ColumnInfo(name = "amount")
    private float amount;

    @ColumnInfo(name = "date")
    private long date;

    public Entry(String name, int accountId, int type, float amount, long date) {
        this.name = name;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.date = date;
    }

    public Entry() {}

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getAccountId() {
        return accountId;
    }

    public int getType() {
        return type;
    }

    public float getAmount() {
        return amount;
    }

    public long getDate() {
        return date;
    }
}
