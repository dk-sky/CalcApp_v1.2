package com.applications.dk_sky.calcapp;

import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.List;

public class HistoryLayout extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    HistoryDatabase db;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_layout);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = Room.databaseBuilder(getApplicationContext(), HistoryDatabase.class, "history")
                .allowMainThreadQueries()
                .build();
        List<Entry> entries = db.dataAccessObject().getAllEntries();
        for (Entry entry : entries) {
            Log.i("DATA", String.valueOf(entry.getResult()));
        }

        adapter = new EntryAdapter(entries);
        recyclerView.setAdapter(adapter);
    }
}
