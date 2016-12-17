package com.lena.timetracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lena.timetracker.dataobjects.CustomRecordObject;

public class ShowRecordActivity extends AppCompatActivity {
    private CustomRecordObject record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_record);

        String json = getIntent().getStringExtra(getString(R.string.record));
        if (json != null) {
            record = getRecordFromJSON(json);
            updateMeetingView(record);
        }

        Button editButton = (Button) findViewById(R.id.edit_record_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editRecord(record);
            }
        });

        Button deleteButton = (Button) findViewById(R.id.delete_record_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecord(record);
            }
        });
    }

    private CustomRecordObject getRecordFromJSON(String json) {
        Gson gson = new GsonBuilder().setDateFormat("yyy-MM-dd'T'HH:mm:ss").create();
        return gson.fromJson(json, CustomRecordObject.class);
    }

    private void updateMeetingView(CustomRecordObject record) {

    }

    private void editRecord(CustomRecordObject record) {

    }

    private void deleteRecord(CustomRecordObject record) {

    }
}
