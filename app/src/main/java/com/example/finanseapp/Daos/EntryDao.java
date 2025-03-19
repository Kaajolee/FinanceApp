package com.example.finanseapp.Daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.finanseapp.Entities.Entry;

import java.util.List;

@Dao
public interface EntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Entry entry);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Entry> entries);

    @Update
    void update(Entry entry);

    @Delete
    void delete(Entry entry);

    @Query("DELETE FROM entries")
    void deleteAll();

    @Query("SELECT * FROM entries")
    List<Entry> getAllEntries();

    @Query("SELECT * FROM entries WHERE id = :id LIMIT 1")
    Entry getEntryById(String id);

    @Query("SELECT * FROM entries WHERE accountId = :accountId")
    List<Entry> getEntriesByAccountId(String accountId);
    @Query("SELECT SUM(CASE " +
            "WHEN type = 0 THEN amount " +
            "WHEN type = 1 THEN -amount " +
            "ELSE 0 " +
            "END) AS totalBalance " +
            "FROM entries WHERE accountId = :accountId")
    float getAccountBalance(String accountId);
    @Query("SELECT * FROM entries WHERE date BETWEEN :startDate AND :endDate")
    List<Entry> getEntriesByDateRange(long startDate, long endDate);

    @Query("SELECT SUM(amount) FROM entries WHERE accountId = :accountId")
    float getTotalAmountByAccount(String accountId);
}
