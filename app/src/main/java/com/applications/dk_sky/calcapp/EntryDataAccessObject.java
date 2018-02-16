package com.applications.dk_sky.calcapp;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by livingston on 2/16/18.
 */

@Dao
public interface EntryDataAccessObject {
    @Query("SELECT * from Entry")
    List<Entry> getAllEntries();

    @Insert
    void insertAll(Entry... entries);
}
