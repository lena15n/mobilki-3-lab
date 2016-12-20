package com.lena.timetracker;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.lena.timetracker.db.TimeTrackerContract;
import com.lena.timetracker.db.TimeTrackerDbHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MostFrequentActivitiesActivity extends AppCompatActivity
                                                    implements TimePickerDialog.OnTimeSetListener,
                                                               DatePickerDialog.OnDateSetListener {

    private int startYear;
    private int startMonthOfYear;
    private int startDayOfMonth;
    private int endYear;
    private int endMonthOfYear;
    private int endDayOfMonth;

    private Date startDate;
    private Date endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_most_frequent_activities);

        TextView textView = (TextView) findViewById(R.id.freq_desc_textView);
        textView.setText(getRecordNameFromDb());

        final TextView startTextView = (TextView) findViewById(R.id.freq_record_set_start_textview);
        startTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getFragmentManager(), getString(R.string.record_start));
            }
        });

        TextView endTextView = (TextView) findViewById(R.id.freq_set_end_textview);
        endTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getFragmentManager(), getString(R.string.record_end));
            }
        });

        Button choosePeriodButton = (Button) findViewById(R.id.choose_period_button);
        choosePeriodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateMostFrequent();
            }
        });
    }


    private void calculateMostFrequent() {
        if (startDate != null && endDate != null) {
            TextView textView = (TextView) findViewById(R.id.freq_desc_textView);
            textView.setText(getRecordNameFromDb(startDate, endDate));

            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy  HH:mm");
            TextView periodTextView = (TextView) findViewById(R.id.freq_period_textView);
            periodTextView.setText(getString(R.string.stat_period) + "  " + dateFormat.format(startDate)
                    + "    -   " + dateFormat.format(endDate));
        }
    }

    private String getRecordNameFromDb() {
        String desc = null;
        TimeTrackerDbHelper dbHelper = new TimeTrackerDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(TimeTrackerContract.Record.SQL_SELECT_MOST_FREQUENT, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    desc = cursor.getString(cursor.getColumnIndex(TimeTrackerContract.Record.DESCRIPTION));
                    //long count = cursor.getLong(cursor.getColumnIndex(TimeTrackerContract.Record.TEMP_COLUMN_NAME));
                }
                while (cursor.moveToNext());
            }
        }
        if (desc == null) {
            desc = getString(R.string.stat_no_records);
        }

        return desc;
    }

    private String getRecordNameFromDb(Date dateStart, Date dateEnd) {
        String desc = null;
        TimeTrackerDbHelper dbHelper = new TimeTrackerDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        DateFormat dateFormat = new SimpleDateFormat(MainActivity.DATE_FORMAT);
        Cursor cursor = db.rawQuery(TimeTrackerContract.Record.SQL_SELECT_MOST_FREQUENT_IN_PERIOD,
                new String[] {dateFormat.format(dateStart), dateFormat.format(dateEnd),
                              dateFormat.format(dateStart), dateFormat.format(dateEnd)});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    desc = cursor.getString(cursor.getColumnIndex(TimeTrackerContract.Record.DESCRIPTION));
                }
                while (cursor.moveToNext());
            }
        }
        if (desc == null) {
            desc = getString(R.string.stat_no_records);
        }

        return desc;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        FragmentManager fragmentManager = getFragmentManager();
        TimePickerFragment timePickerFragment = new TimePickerFragment();

        if (fragmentManager.findFragmentByTag(getString(R.string.record_start)) != null) {
            startYear = year;
            startMonthOfYear = monthOfYear;
            startDayOfMonth = dayOfMonth;
            timePickerFragment.show(fragmentManager, getString(R.string.record_start_time));
        }
        else if (fragmentManager.findFragmentByTag(getString(R.string.record_end)) != null) {
            endYear = year;
            endMonthOfYear = monthOfYear;
            endDayOfMonth = dayOfMonth;
            timePickerFragment.show(fragmentManager, getString(R.string.record_end_time));
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy    HH:mm");
        if (getFragmentManager().findFragmentByTag(getString(R.string.record_start_time)) != null) {
            calendar.set(startYear, startMonthOfYear, startDayOfMonth, hourOfDay, minute);
            startDate = calendar.getTime();
            String startDateString = dateFormat.format(startDate);
            TextView startDateTextView = (TextView) findViewById(R.id.freq_record_set_start_textview);
            startDateTextView.setText(startDateString);
        }
        else if (getFragmentManager().findFragmentByTag(getString(R.string.record_end_time)) != null) {
            calendar.set(endYear, endMonthOfYear, endDayOfMonth, hourOfDay, minute);
            endDate = calendar.getTime();
            String endDateString = dateFormat.format(endDate);
            TextView endDateTextView = (TextView) findViewById(R.id.freq_set_end_textview);
            endDateTextView.setText(endDateString);
        }
    }
}
