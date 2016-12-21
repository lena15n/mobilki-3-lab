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
import java.util.ArrayList;
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

        calculateMostFrequentForAMonth();

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

    private void calculateMostFrequentForAMonth() {
        Calendar calendar = Calendar.getInstance();
        Date currDate = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat(MainActivity.DATE_FORMAT);
        String temp = dateFormat.format(currDate);
        String month = temp.substring(temp.indexOf("-") + 1, temp.lastIndexOf("-"));

        ArrayList<String> activities = getRecordsNamesFromDb(month);
        StringBuilder sb = new StringBuilder();

        if (activities.size() != 0) {
            int i = 1;
            for (String activity : activities) {
                sb.append("" + i + ". " + activity + "\n");
                i++;
            }
        } else {
            sb.append(getString(R.string.stat_no_records));
        }
        TextView descTextView = (TextView) findViewById(R.id.freq_desc_textView);
        descTextView.setText(sb.toString());

        TextView periodTextView = (TextView) findViewById(R.id.freq_period_textView);
        periodTextView.setText(getString(R.string.stat_for_month));
    }

    private void calculateMostFrequent() {
        if (startDate != null && endDate != null) {
            TextView textView = (TextView) findViewById(R.id.freq_desc_textView);
            StringBuilder sb = new StringBuilder();
            int i = 1;
            ArrayList<String> activities = getRecordsNamesFromDb(startDate, endDate);
            if (activities.size() != 0) {
                for (String activity : activities) {
                    sb.append("" + i + ". " + activity + "\n");
                    i++;
                }
            } else {
                sb.append(getString(R.string.stat_no_records));
            }
            textView.setText(sb.toString());

            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy  HH:mm");
            TextView periodTextView = (TextView) findViewById(R.id.freq_period_textView);
            periodTextView.setText(getString(R.string.stat_period) + "  " + dateFormat.format(startDate)
                    + "    -   " + dateFormat.format(endDate));
        }
    }

    private ArrayList<String> getRecordsNamesFromDb(String month) {
        ArrayList<String> activities = new ArrayList<>();
        String desc;
        TimeTrackerDbHelper dbHelper = new TimeTrackerDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(TimeTrackerContract.Record.SQL_SELECT_MOST_FREQUENT_FOR_A_MONTH,
                new String[]{month});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    desc = cursor.getString(cursor.getColumnIndex(TimeTrackerContract.Record.DESCRIPTION));
                    if (desc != null) {
                        activities.add(desc);
                    }
                }
                while (cursor.moveToNext());
            }
        }

        return activities;
    }

    private ArrayList<String> getRecordsNamesFromDb(Date dateStart, Date dateEnd) {
        ArrayList<String> activities = new ArrayList<>();
        String desc;
        TimeTrackerDbHelper dbHelper = new TimeTrackerDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        DateFormat dateFormat = new SimpleDateFormat(MainActivity.DATE_FORMAT);
        Cursor cursor = db.rawQuery(TimeTrackerContract.Record.SQL_SELECT_MOST_FREQUENT_IN_PERIOD,
                new String[]{dateFormat.format(dateStart), dateFormat.format(dateEnd),
                        dateFormat.format(dateStart), dateFormat.format(dateEnd)});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    desc = cursor.getString(cursor.getColumnIndex(TimeTrackerContract.Record.DESCRIPTION));
                    if (desc != null) {
                        activities.add(desc);
                    }
                }
                while (cursor.moveToNext());
            }
        }

        return activities;
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
        } else if (fragmentManager.findFragmentByTag(getString(R.string.record_end)) != null) {
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
        } else if (getFragmentManager().findFragmentByTag(getString(R.string.record_end_time)) != null) {
            calendar.set(endYear, endMonthOfYear, endDayOfMonth, hourOfDay, minute);
            endDate = calendar.getTime();
            String endDateString = dateFormat.format(endDate);
            TextView endDateTextView = (TextView) findViewById(R.id.freq_set_end_textview);
            endDateTextView.setText(endDateString);
        }
    }
}
