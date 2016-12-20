package com.lena.timetracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lena.timetracker.dataobjects.CustomRecordObject;
import com.lena.timetracker.db.TimeTrackerContract;
import com.lena.timetracker.db.TimeTrackerDbHelper;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class ShowRecordActivity extends AppCompatActivity {
    private CustomRecordObject record;
    private static final int REQUEST_PERMISSION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_record);

        final String json = getIntent().getStringExtra(getString(R.string.record));
        if (json != null) {
            record = getRecordFromJSON(json);
            updateMeetingView(record);
        }

        Button editButton = (Button) findViewById(R.id.edit_record_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editRecord(json);
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
        Gson gson = new GsonBuilder().setDateFormat(MainActivity.DATE_FORMAT).create();
        return gson.fromJson(json, CustomRecordObject.class);
    }

    private void updateMeetingView(CustomRecordObject record) {
        TextView descTextView = (TextView) findViewById(R.id.show_record_desc);
        descTextView.setText(record.getDesc());

        TextView categoryTextView = (TextView) findViewById(R.id.show_record_category);
        categoryTextView.setText(record.getCategory());

        TextView startTextView = (TextView) findViewById(R.id.show_record_set_start_textview);
        Date startTime = record.getStartTime();
        setTimeTextView(startTextView, startTime);

        TextView endTextView = (TextView) findViewById(R.id.show_record_set_end_textview);
        Date endTime = record.getEndTime();
        setTimeTextView(endTextView, endTime);

        TextView timeTextView = (TextView) findViewById(R.id.show_record_set_time);
        setTimePeriodTextView(timeTextView, record.getTime());

        addImageViews(record);
    }

    private void addImageViews(CustomRecordObject record) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        } else {
            showImages();
        }
    }

    private void showImages() {
        for (Map.Entry<Long, String> photoObj : record.getPhotos().entrySet()) {
            final Uri path = Uri.parse(photoObj.getValue());
            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(path);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap photo = BitmapFactory.decodeStream(imageStream);
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new android.view.ViewGroup.LayoutParams(200, 200));
            imageView.setImageBitmap(photo);
            imageView.setPadding(10, 10, 5, 0);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(path, "image/*");
                    startActivity(intent);
                }
            });
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.show_imageviews_layout);
            linearLayout.addView(imageView);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    showImages();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    private void setTimePeriodTextView(TextView textView, long timePeriod) {
        int day = (int) (timePeriod / (24 * 60));
        int hour = (int) ((timePeriod - day * 24 * 60) / 60);
        int minute = (int) (timePeriod - day * 24 * 60 - hour * 60);
        String dayStr = Integer.toString(day);
        if (day < 10) {
            dayStr = "0" + dayStr;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(0, 0, 0, hour, minute);
        Date temp = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        textView.setText(dayStr + ":" + dateFormat.format(temp));
    }

    private void setTimeTextView(TextView textView, Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy    HH:mm");
        String dateString = dateFormat.format(date);

        textView.setText(dateString);
    }

    private void editRecord(String json) {
        Intent intent = new Intent(this, CreateOrEditRecordActivity.class);
        intent.putExtra(getString(R.string.edit_activity), true);
        intent.putExtra(getString(R.string.edit_meeting_json), json);
        startActivity(intent);
    }

    private void deleteRecord(CustomRecordObject record) {
        TimeTrackerDbHelper dbHelper = new TimeTrackerDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (Map.Entry<Long, String> photo : record.getPhotos().entrySet()) {
            int delCount = db.delete(TimeTrackerContract.Photo.TABLE_NAME,
                    TimeTrackerContract.Photo._ID + " = " + photo.getKey(), null);
        }

        int delCount = db.delete(TimeTrackerContract.Record.TABLE_NAME,
                TimeTrackerContract.Record._ID + " = " + record.getId(), null);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
