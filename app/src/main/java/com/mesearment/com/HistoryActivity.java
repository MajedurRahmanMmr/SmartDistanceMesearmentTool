package com.mesearment.com;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        Realm realm = Realm.getDefaultInstance();

        List<RecordModel> data = realm.where(RecordModel.class).findAll();
        recyclerView.setAdapter(new HistoryAdapter(this, data));
    }
}
