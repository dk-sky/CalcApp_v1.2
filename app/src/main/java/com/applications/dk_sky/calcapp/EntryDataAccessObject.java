package com.applications.dk_sky.calcapp;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by livingston on 2/16/18.
 */

@Dao
public interface EntryDataAccessObject {
    @Query("SELECT * from entries")
    List<Entry> getAllEntries();

    @Insert
    void insertEntries(Entry... entries);


}
