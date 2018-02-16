package com.applications.dk_sky.calcapp;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by livingston on 2/16/18.
 */

@Database(entities = {Entry.class}, version = 1)
public abstract class HistoryDatabase extends RoomDatabase {
    public abstract EntryDataAccessObject dataAccessObject();
}
