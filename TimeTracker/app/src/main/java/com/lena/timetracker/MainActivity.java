package com.lena.timetracker;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lena.timetracker.dataobjects.CustomRecordObject;
import com.lena.timetracker.db.TimeTrackerContract;
import com.lena.timetracker.db.TimeTrackerDbHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    public static final String DATE_FORMAT = "yyy-MM-dd'T'HH:mm:ss";
    public static final String LOG_TAG = "~Mimi~";
    private ArrayAdapter<CustomRecordObject> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateOrEditRecordActivity.class);
                startActivity(intent);
            }
        });

        showRecords(prepareRecordsToShow());
    }

    private void showRecords(final ArrayList<CustomRecordObject> records) {
         arrayAdapter = new ArrayAdapter<CustomRecordObject>(this,
                android.R.layout.simple_list_item_2, android.R.id.text1, records) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(records.get(position).getDesc());
                text2.setText(records.get(position).getCategory());
                return view;
            }
        };

        ListView allRecordsListView = (ListView) findViewById(R.id.all_records_listview);
        allRecordsListView.setAdapter(arrayAdapter);
        allRecordsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                CustomRecordObject record = arrayAdapter.getItem(position);

                Context context = getApplicationContext();
                Intent intent = new Intent(context, ShowRecordActivity.class);
                Gson gson = new GsonBuilder().setDateFormat(MainActivity.DATE_FORMAT).create();
                intent.putExtra(context.getString(R.string.record), gson.toJson(record));
                startActivity(intent);
            }
        });
    }

    private ArrayList<CustomRecordObject> prepareRecordsToShow() {
        TimeTrackerDbHelper dbHelper = new TimeTrackerDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(TimeTrackerContract.Record.SQL_LEFT_JOIN_PHOTO_THEN_CATEGORIES2,
                new String[]{});

        ArrayList<CustomRecordObject> records = null;

        if (cursor != null) {
            long prevId = -1;
            records = new ArrayList<>();

            if (cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndex(TimeTrackerContract.Record.TEMP_RECORD_ID));
                    String desc = cursor.getString(cursor.getColumnIndex(TimeTrackerContract.Record.DESCRIPTION));
                    String start = cursor.getString(cursor.getColumnIndex(TimeTrackerContract.Record.START_TIME));
                    String end = cursor.getString(cursor.getColumnIndex(TimeTrackerContract.Record.END_TIME));
                    long time = cursor.getLong(cursor.getColumnIndex(TimeTrackerContract.Record.TIME));
                    String categoryName = cursor.getString(cursor.getColumnIndex(TimeTrackerContract.Category.NAME));
                    long photoId = cursor.getLong(cursor.getColumnIndex(TimeTrackerContract.Record.TEMP_PHOTO_ID));
                    String photoUri = cursor.getString(cursor.getColumnIndex(TimeTrackerContract.Photo.URI));

                    if (prevId == id) {
                        int index = records.size() - 1;
                        CustomRecordObject record = records.get(index);
                        HashMap<Long, String> photos = record.getPhotos();
                        photos.put(photoId, photoUri);
                        record.setPhotos(photos);
                        records.set(index, record);
                    } else {
                        HashMap<Long, String> photos = null;

                        if (photoUri != null) {
                            photos = new HashMap<>();
                            photos.put(photoId, photoUri);
                        }
                        DateFormat dateFormat = new SimpleDateFormat(MainActivity.DATE_FORMAT);

                        CustomRecordObject record = null;
                        try {
                            record = new CustomRecordObject(id, desc, categoryName,
                                    dateFormat.parse(start), dateFormat.parse(end), time, photos);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        records.add(record);
                        prevId = id;
                    }

                    String str = "";
                    for (String cn : cursor.getColumnNames()) {
                        str = str.concat(cn + " = " + cursor.getString(cursor.getColumnIndex(cn)) + "; ");
                    }
                    Log.d("Min", str);
                }
                while (cursor.moveToNext());
            }
        }

        return records;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_the_most_frequent: {
                Intent intent = new Intent(this, MostFrequentActivitiesActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.action_the_biggest_total_time: {
                Intent intent = new Intent(this, MaxTotalTimeActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.action_total_time_by_selected_categories: {
                Intent intent = new Intent(this, TotalTimeByCategoriesActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.action_total_time: {
                Intent intent = new Intent(this, PieDiagramActivity.class);
                startActivity(intent);
            }
            break;
        }

        return super.onOptionsItemSelected(item);
    }


}
