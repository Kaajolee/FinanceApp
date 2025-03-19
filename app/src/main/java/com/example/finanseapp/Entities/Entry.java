package com.example.finanseapp.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "entries",
        foreignKeys = @ForeignKey(
                entity = Account.class,
                parentColumns = "id",
                childColumns =  "accountId",
                onDelete = ForeignKey.CASCADE
        )
)
public class Entry {
    @PrimaryKey(autoGenerate = true)
    private String id;

    @ColumnInfo(name = "accountId")
    private String accountId;

    @ColumnInfo(name = "type")
    private int type;

    @ColumnInfo(name = "amount")
    private float amount;

    @ColumnInfo(name = "date")
    private long date;

    public void setAccountId(String accountId) {
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

    public String getId() {
        return id;
    }

    public String getAccountId() {
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
