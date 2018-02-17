package com.applications.dk_sky.calcapp;

import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

public class HistoryLayout extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    HistoryDatabase db;
    List<Entry> entries;
    FloatingActionButton fab;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_layout);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fab = findViewById(R.id.float_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the whole data
                // I probably should consider adding a confirmation dialog
                db.dataAccessObject().deleteAll();
                entries.clear();
                adapter = new EntryAdapter(entries);
                recyclerView.setAdapter(adapter);
            }
        });

        db = Room.databaseBuilder(getApplicationContext(), HistoryDatabase.class, "history")
                .allowMainThreadQueries()
                .build();
        entries = db.dataAccessObject().getAllEntries();
        adapter = new EntryAdapter(entries);
        recyclerView.setAdapter(adapter);
    }
}
