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
import android.widget.Toast;

import com.lena.timetracker.db.TimeTrackerContract;
import com.lena.timetracker.db.TimeTrackerDbHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

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

        LinkedHashMap<String, Long> activities = getRecordsNamesFromDb(month);
        StringBuilder sbDescription = new StringBuilder();
        StringBuilder sbTime = new StringBuilder();

        if (activities.size() != 0) {
            int i = 1;
            for (Map.Entry<String, Long> recordSumTime : activities.entrySet()) {
                sbDescription.append("" + i + ". " + recordSumTime.getKey() + "\n");
                sbTime.append(recordSumTime.getValue() + "\n");
                i++;
            }
        } else {
            sbDescription.append(getString(R.string.stat_no_records));
        }

        TextView descTextView = (TextView) findViewById(R.id.freq_result_textView);
        descTextView.setText(sbDescription.toString());

        TextView timeTextView = (TextView) findViewById(R.id.freq_sum_result_textView);
        timeTextView.setText(sbTime.toString());

        TextView periodTextView = (TextView) findViewById(R.id.freq_period_textView);
        periodTextView.setText(getString(R.string.stat_for_month));
    }

    private void calculateMostFrequent() {
        if (startDate != null && endDate != null) {
            LinkedHashMap<String, Long> activities = getRecordsNamesFromDb(startDate, endDate);
            StringBuilder sbDescription = new StringBuilder();
            StringBuilder sbTime = new StringBuilder();

            if (activities.size() != 0) {
                int i = 1;
                for (Map.Entry<String, Long> recordSumTime : activities.entrySet()) {
                    sbDescription.append("" + i + ". " + recordSumTime.getKey() + "\n");
                    sbTime.append(recordSumTime.getValue() + "\n");
                    i++;
                }
            } else {
                sbDescription.append(getString(R.string.stat_no_records));
            }

            TextView descTextView = (TextView) findViewById(R.id.freq_result_textView);
            descTextView.setText(sbDescription.toString());

            TextView timeTextView = (TextView) findViewById(R.id.freq_sum_result_textView);
            timeTextView.setText(sbTime.toString());

            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy  HH:mm");
            TextView periodTextView = (TextView) findViewById(R.id.freq_period_textView);
            periodTextView.setText(getString(R.string.stat_period) + "  " + dateFormat.format(startDate)
                    + "    -   " + dateFormat.format(endDate));
        } else {
            Toast.makeText(this, R.string.record_time_not_set, Toast.LENGTH_LONG).show();
        }
    }

    private LinkedHashMap<String, Long> getRecordsNamesFromDb(String month) {
        LinkedHashMap<String, Long> result = new LinkedHashMap<>();
        String desc;
        long count;
        TimeTrackerDbHelper dbHelper = new TimeTrackerDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(TimeTrackerContract.Record.SQL_SELECT_MOST_FREQUENT_FOR_A_MONTH,
                new String[]{month});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    desc = cursor.getString(cursor.getColumnIndex(TimeTrackerContract.Record.DESCRIPTION));
                    count = cursor.getLong(cursor.getColumnIndex(TimeTrackerContract.Record.TEMP_COLUMN_NAME));
                    if (desc != null) {
                        result.put(desc, count);
                    }
                }
                while (cursor.moveToNext());
            }
        }

        return result;
    }

    private LinkedHashMap<String, Long> getRecordsNamesFromDb(Date dateStart, Date dateEnd) {
        LinkedHashMap<String, Long> activities = new LinkedHashMap<>();
        String desc;
        Long count;
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
                    count = cursor.getLong(cursor.getColumnIndex(TimeTrackerContract.Record.TEMP_COLUMN_NAME));
                    if (desc != null) {
                        activities.put(desc, count);
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
