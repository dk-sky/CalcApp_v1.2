package com.applications.dk_sky.calcapp;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface EntryDataAccessObject {
    @Query("SELECT * from entries")
    List<Entry> getAllEntries();

    @Insert
    void insertEntries(Entry... entries);


}
