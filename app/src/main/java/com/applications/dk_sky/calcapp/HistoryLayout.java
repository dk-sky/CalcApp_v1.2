package com.applications.dk_sky.calcapp;

import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Created by livingston on 2/16/18.
 */

public class HistoryLayout extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<Entry> entries;
    HistoryDatabase db;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_layout);

        db = Room.databaseBuilder(getApplicationContext(), HistoryDatabase.class, "history")
                .allowMainThreadQueries()
                .build();
        entries = db.dataAccessObject().getAllEntries();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EntryAdapter(entries);
        recyclerView.setAdapter(adapter);
    }
}
