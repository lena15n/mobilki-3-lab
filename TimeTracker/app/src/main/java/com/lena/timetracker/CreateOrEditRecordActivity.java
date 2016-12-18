package com.lena.timetracker;

import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lena.timetracker.dataobjects.CustomRecordObject;
import com.lena.timetracker.dataobjects.DOCategory;
import com.lena.timetracker.db.TimeTrackerContract;
import com.lena.timetracker.db.TimeTrackerDbHelper;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CreateOrEditRecordActivity extends AppCompatActivity  {
    private static final int ATTACH_PHOTO = 1;
    private Date startTime;
    private Date endTime;
    private ArrayList<String> photoPathes;
    private String mode;
    private ArrayList<DOCategory> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_record);

        photoPathes = new ArrayList<>();

        Spinner prioritiesSpinner = (Spinner) findViewById(R.id.create_record_category_spinner);
        categories = getCategoriesFromDb();
        ArrayAdapter<DOCategory> arrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, categories);
        prioritiesSpinner.setAdapter(arrayAdapter);

        final TextView startTextView = (TextView) findViewById(R.id.create_record_set_start_textview);
        startTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(fragmentManager, getString(R.string.record_start));
            }
        });

        TextView endTextView = (TextView) findViewById(R.id.create_record_set_end_textview);
        endTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                TimePickerFragment datePickerFragment = new TimePickerFragment();
                datePickerFragment.show(fragmentManager, getString(R.string.record_end));
            }
        });

        Button attachButton = (Button) findViewById(R.id.attach_button);
        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachImage();
            }
        });

        Button clearButton = (Button) findViewById(R.id.clear_images_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearImages();
            }
        });

        if (getIntent().getBooleanExtra(getString(R.string.edit_activity), false)){
            mode = getString(R.string.mode_edit);
        }
        else {
            mode = getString(R.string.mode_create);
        }

        if (mode.equals(getString(R.string.mode_create))) {
            Button createButton = (Button) findViewById(R.id.create_record_button);
            createButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    insertIntoDb();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            Button saveButton = (Button) findViewById(R.id.create_record_button);
            saveButton.setText(R.string.edit_save_button);
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateDb();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            });
        }

        if (mode.equals(getString(R.string.mode_edit))){
            prepareEditActivity(getIntent().getStringExtra(getString(R.string.edit_meeting_json)));
        }
    }

    private void prepareEditActivity(String json) {
        Gson gson = new GsonBuilder().setDateFormat("yyy-MM-dd'T'HH:mm:ss").create();
        CustomRecordObject record = gson.fromJson(json, CustomRecordObject.class);

        EditText descEditText = (EditText) findViewById(R.id.create_record_desc_edittext);
        descEditText.setText(record.getDesc());

        Spinner categorySpinner = (Spinner) findViewById(R.id.create_record_category_spinner);
        DOCategory chosenCategory = null;
        for (DOCategory category : categories) {
            if (category.getName().equals(record.getCategory())){
                chosenCategory = category;
            }
        }
        categorySpinner.setSelection(categories.indexOf(chosenCategory));

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        TextView startTextView = (TextView) findViewById(R.id.create_record_set_start_textview);
        startTextView.setText(format.format(record.getStartTime()));
        TextView endTextView = (TextView) findViewById(R.id.create_record_set_end_textview);
        endTextView.setText(format.format(record.getEndTime()));

        for (String photoPath : record.getPhotos().values()) {
            addImageOnViewByUri(Uri.parse(photoPath));
        }
    }

    private void updateDb() {
        //prepare


    }

    private void insertIntoDb() {
        //prepare
        String desc = ((TextView) findViewById(R.id.create_record_desc_edittext)).getText().toString();
        Spinner spinner = ((Spinner)findViewById(R.id.create_record_category_spinner));
        DOCategory category = (DOCategory) spinner.getSelectedItem();
        Log.d("Mi", "Record: " + desc + ", categ: id = " + category.getId() + ", name: " + category.getName());
        long time = getTimeDiff(startTime, endTime);
        DateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd'T'HH:mm:ss");

        TimeTrackerDbHelper mDbHelper = new TimeTrackerDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //insert into Record
        ContentValues values = new ContentValues();
        values.put(TimeTrackerContract.Record.DESCRIPTION, desc);
        values.put(TimeTrackerContract.Record.CATEGORY_ID, category.getId());
        values.put(TimeTrackerContract.Record.START_TIME, dateFormat.format(startTime));
        values.put(TimeTrackerContract.Record.END_TIME, dateFormat.format(endTime));
        values.put(TimeTrackerContract.Record.TIME, time);
        long newRecordId = db.insert(TimeTrackerContract.Record.TABLE_NAME, null, values);

        //insert into Photo
        for (String path : photoPathes) {
            values = new ContentValues();
            values.put(TimeTrackerContract.Photo.URI, path);
            values.put(TimeTrackerContract.Photo.RECORD_ID, newRecordId);
            db.insert(TimeTrackerContract.Photo.TABLE_NAME, null, values);
        }

        db.close();
        verify();
    }

    private void verify() {
        TimeTrackerDbHelper dbHelper = new TimeTrackerDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                TimeTrackerContract.Record._ID,
                TimeTrackerContract.Record.DESCRIPTION,
                TimeTrackerContract.Record.CATEGORY_ID,
                TimeTrackerContract.Record.START_TIME,
                TimeTrackerContract.Record.END_TIME,
                TimeTrackerContract.Record.TIME
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                TimeTrackerContract.Record._ID + " ASC";

        Cursor cursor = db.query(
                TimeTrackerContract.Record.TABLE_NAME,  // The table to query
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
                    long id = cursor.getLong(cursor.getColumnIndex(TimeTrackerContract.Record._ID));
                    String desc = cursor.getString(cursor.getColumnIndex(TimeTrackerContract.Record.DESCRIPTION));
                    String cat = cursor.getString(cursor.getColumnIndex(TimeTrackerContract.Record.CATEGORY_ID));
                    String start = cursor.getString(cursor.getColumnIndex(TimeTrackerContract.Record.START_TIME));
                    String end = cursor.getString(cursor.getColumnIndex(TimeTrackerContract.Record.END_TIME));
                    String time = cursor.getString(cursor.getColumnIndex(TimeTrackerContract.Record.TIME));



                    Log.d("Mi", "Record: id = " + id + ", name: " + desc + ", cat: " + cat +
                    "\nstart: " + start + ",end: " + end + ", time: " + time);
                }
                while (cursor.moveToNext());
            }
        }


        String[] projection1 = {
                TimeTrackerContract.Photo._ID,
                TimeTrackerContract.Photo.URI,
                TimeTrackerContract.Photo.RECORD_ID
        };

        // How you want the results sorted in the resulting Cursor
        sortOrder =
                TimeTrackerContract.Photo._ID + " ASC";

        cursor = db.query(
                TimeTrackerContract.Photo.TABLE_NAME,  // The table to query
                projection1,                               // The columns to return
                null,                                     // The columns for the WHERE clause
                null,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndex(TimeTrackerContract.Photo._ID));
                    String uri = cursor.getString(cursor.getColumnIndex(TimeTrackerContract.Photo.URI));
                    String recId = cursor.getString(cursor.getColumnIndex(TimeTrackerContract.Photo.RECORD_ID));

                    Log.d("Mi", "Photo: id = " + id + ", uri: " + uri + ", rec id: " + recId);
                }
                while (cursor.moveToNext());
            }
        }

        cursor.close();
        db.close();
    }

    private void clearImages() {
        photoPathes = new ArrayList<>();

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.imageviews_layout);
        if (linearLayout.getChildCount() > 0) {
            linearLayout.removeAllViews();
        }
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
                }
                while (cursor.moveToNext());
            }
        }

        cursor.close();
        db.close();

        return categories;
    }

    public void onStartTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(0, 0, 0, hourOfDay, minute);
        startTime = calendar.getTime();
        String hourString = Integer.toString(hourOfDay);
        String minuteString = Integer.toString(minute);

        if (hourOfDay < 10) {
            hourString = "0" + hourString;
        }
        if (minute < 10) {
            minuteString = "0" + minuteString;
        }

        TextView startTextView = (TextView) findViewById(R.id.create_record_set_start_textview);
        startTextView.setText(hourString + ":" + minuteString);
    }

    public void onEndTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(0, 0, 0, hourOfDay, minute);
        endTime = calendar.getTime();
        String hourString = Integer.toString(hourOfDay);
        String minuteString = Integer.toString(minute);

        if (hourOfDay < 10) {
            hourString = "0" + hourString;
        }
        if (minute < 10) {
            minuteString = "0" + minuteString;
        }

        TextView endTextView = (TextView) findViewById(R.id.create_record_set_end_textview);
        endTextView.setText(hourString + ":" + minuteString);
    }

    private void attachImage() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select PImage");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, ATTACH_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case ATTACH_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = intent.getData();
                    addImageOnViewByUri(selectedImage);
                }
        }
    }

    private void addImageOnViewByUri(Uri uri){
        InputStream imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap photo = BitmapFactory.decodeStream(imageStream);
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new android.view.ViewGroup.LayoutParams(100, 120));
        imageView.setImageBitmap(photo);
        imageView.setPadding(10, 10, 5, 0);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.imageviews_layout);
        linearLayout.addView(imageView);

        photoPathes.add(uri.toString());
    }

    /**
     * Get a diff between two dates
     * @param date1 the oldest date
     * @param date2 the newest date
     * @return the diff value in minutes
     */
    public static long getTimeDiff(Date date1, Date date2) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
}
