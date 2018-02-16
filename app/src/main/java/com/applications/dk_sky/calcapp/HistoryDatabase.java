package com.applications.dk_sky.calcapp;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;


@Database(entities = {Entry.class}, version = 1)
abstract class HistoryDatabase extends RoomDatabase {
    abstract EntryDataAccessObject dataAccessObject();
}
