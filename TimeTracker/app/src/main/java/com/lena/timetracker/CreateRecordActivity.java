package com.lena.timetracker;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.lena.timetracker.dataobjects.DOCategory;

import java.util.ArrayList;

public class CreateRecordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_record);

        Spinner prioritiesSpinner = (Spinner) findViewById(R.id.create_record_category_spinner);
        ArrayList<DOCategory> categories = getCategoriesFromDb();
        ArrayAdapter<DOCategory> arrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, categories);
        //ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(this, R.array.priorities, R.layout.spinner_item);
        prioritiesSpinner.setAdapter(arrayAdapter);

        Button createButton = (Button) findViewById(R.id.create_record_button);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareDataAndInsertIntoDb();
            }
        });
    }

    private void prepareDataAndInsertIntoDb() {
        //prepare
        /*
        String meetingName = ((TextView) findViewById(R.id.meeting_name_edittext)).getText().toString();
            String meetingDescription = ((TextView) findViewById(R.id.meeting_desc_edittext)).getText().toString();
            Spinner spinner = (Spinner) findViewById(R.id.meeting_priority_spinner);
            String meetingPriority = spinner.getSelectedItem().toString();
        */
        String desc = ((TextView) findViewById(R.id.create_record_desc_edittext)).getText().toString();
        Spinner spinner = ((Spinner)findViewById(R.id.create_record_category_spinner));
        DOCategory category = (DOCategory) spinner.getSelectedItem();
        Log.d("Mi", "Record: " + desc + ", categ: id = " + category.getId() + ", name: " + category.getName());

        //insert


    }

    private ArrayList<DOCategory> getCategoriesFromDb() {
        ArrayList<DOCategory> categories = new ArrayList<>();

        TimeTrackerDbHelper dbHelper = new TimeTrackerDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                TimeTrackerContract.Category._ID,
                TimeTrackerContract.Category.NAME
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                TimeTrackerContract.Category.NAME + " ASC";

        Cursor cursor = db.query(
                TimeTrackerContract.Category.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                     // The columns for the WHERE clause
                null,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndex(TimeTrackerContract.Category._ID));
                    String name = cursor.getString(cursor.getColumnIndex(TimeTrackerContract.Category.NAME));
                    categories.add(new DOCategory(id, name));
                    Log.d("Mi", "Categories: id = " + id + ", name: " + name);
                }
                while (cursor.moveToNext());
            }
        }

        cursor.close();
        db.close();

        return categories;
    }
}
