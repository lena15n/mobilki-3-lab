package com.lena.timetracker;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lena.timetracker.dataobjects.DOCategory;
import com.lena.timetracker.date.time.DatePickerFragment;
import com.lena.timetracker.date.time.TimePickerFragment;
import com.lena.timetracker.db.TimeTrackerContract;
import com.lena.timetracker.db.TimeTrackerDbHelper;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;

import static com.lena.timetracker.db.TimeTrackerContract.Record.TEMP_CATEGORY_ID;

public class PieDiagramActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,
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
        setContentView(R.layout.activity_pie_diagram);

        final TextView startTextView = (TextView) findViewById(R.id.chart_record_set_start_textview);
        startTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getFragmentManager(), getString(R.string.record_start));
            }
        });

        TextView endTextView = (TextView) findViewById(R.id.chart_set_end_textview);
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
                showPieChart();
            }
        });

        Button forAMonthButton = (Button) findViewById(R.id.for_a_month_button);
        forAMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPieChartForMonth();
            }
        });
    }

    private void showPieChartForMonth() {
        Calendar calendar = Calendar.getInstance();
        Date currDate = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat(MainActivity.DATE_FORMAT);
        String temp = dateFormat.format(currDate);
        String month = temp.substring(temp.indexOf("-") + 1, temp.lastIndexOf("-"));
        ArrayList<DOCategory> tempCategories = CreateOrEditRecordActivity.getCategoriesFromDb(this);
        if (tempCategories.size() > 0) {
            ArrayList<Long> ids = new ArrayList<>();
            for (DOCategory tempCategory : tempCategories) {
                ids.add(tempCategory.getId());
            }
            LinkedHashMap<String, Long> data = getDataFromDb(month, ids);
            Type type = new TypeToken<LinkedHashMap<String, Long>>(){}.getType();

            Intent intent = new Intent(this, ShowChartActivity.class);
            Bundle extras = new Bundle();
            extras.putString(getString(R.string.data_for_chart), new Gson().toJson(data, type));
            extras.putString(getString(R.string.desc_for_chart), getString(R.string.chart_header) + " a month");
            intent.putExtras(extras);
            startActivity(intent);
        }
        else {
            Toast.makeText(this, R.string.stat_no_records, Toast.LENGTH_LONG).show();
        }
    }


    private void showPieChart() {
        if (startDate != null && endDate != null) {
            ArrayList<DOCategory> tempCategories = CreateOrEditRecordActivity.getCategoriesFromDb(this);
            if (tempCategories.size() > 0) {
                ArrayList<Long> ids = new ArrayList<>();
                for (DOCategory tempCategory : tempCategories) {
                    ids.add(tempCategory.getId());
                }
                LinkedHashMap<String, Long> data = getDataFromDb(startDate, endDate, ids);
                Type type = new TypeToken<LinkedHashMap<String, Long>>(){}.getType();

                DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy  HH:mm");
                String description = getString(R.string.chart_header) + " period: \n" + dateFormat.format(startDate) +
                        "    -    " + dateFormat.format(endDate);

                Intent intent = new Intent(this, ShowChartActivity.class);
                Bundle extras = new Bundle();
                extras.putString(getString(R.string.data_for_chart), new Gson().toJson(data, type));
                extras.putString(getString(R.string.desc_for_chart), description);
                intent.putExtras(extras);
                startActivity(intent);
            }
            else {
                Toast.makeText(this, R.string.stat_no_records, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, R.string.record_time_not_set, Toast.LENGTH_LONG).show();
        }
    }

    private LinkedHashMap<String, Long> getDataFromDb(String month, ArrayList<Long> categoriesIds) {
        LinkedHashMap<String, Long> result = new LinkedHashMap<>();
        String category, id;
        TimeTrackerDbHelper dbHelper = new TimeTrackerDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        StringBuilder where = new StringBuilder();
        where.append(" WHERE ");
        int i = 0;
        for (Long categoryId : categoriesIds) {
            where.append(TEMP_CATEGORY_ID + " = " + categoryId + " ");
            if (i < categoriesIds.size() - 1){
                where.append(" OR ");
            }
            i++;
        }

        String query = TimeTrackerContract.Record.SQL_SELECT_MAX_TOTAL_FOR_A_MONTH + where.toString();
        Log.d(MainActivity.LOG_TAG, query);
        Cursor cursor = db.rawQuery(query, new String[]{month, month});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    category = cursor.getString(cursor.getColumnIndex(TimeTrackerContract.Category.NAME));
                    id = cursor.getString(cursor.getColumnIndex(TimeTrackerContract.Record.TEMP_COLUMN_NAME));
                    if (category != null) {
                        result.put(category, Long.parseLong(id));
                    }
                }
                while (cursor.moveToNext());
            }
        }

        return result;
    }

    private LinkedHashMap<String, Long> getDataFromDb(Date dateStart, Date dateEnd, ArrayList<Long> categoriesIds) {
        LinkedHashMap<String, Long> result = new LinkedHashMap<>();
        String category, id;
        TimeTrackerDbHelper dbHelper = new TimeTrackerDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        StringBuilder where = new StringBuilder();
        where.append(" WHERE ");
        int i = 0;
        for (Long categoryId : categoriesIds) {
            where.append(TEMP_CATEGORY_ID + " = " + categoryId + " ");
            if (i < categoriesIds.size() - 1){
                where.append(" OR ");
            }
            i++;
        }
        DateFormat dateFormat = new SimpleDateFormat(MainActivity.DATE_FORMAT);
        String query = TimeTrackerContract.Record.SQL_SELECT_MAX_TOTAL_IN_PERIOD + where.toString();
        Log.d(MainActivity.LOG_TAG, query);
        Cursor cursor = db.rawQuery(query,
                new String[] {dateFormat.format(dateStart), dateFormat.format(dateEnd)});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    category = cursor.getString(cursor.getColumnIndex(TimeTrackerContract.Category.NAME));
                    id = cursor.getString(cursor.getColumnIndex(TimeTrackerContract.Record.TEMP_COLUMN_NAME));
                    if (category != null) {
                        result.put(category, Long.parseLong(id));
                    }
                }
                while (cursor.moveToNext());
            }
        }

        return result;
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
            TextView startDateTextView = (TextView) findViewById(R.id.chart_record_set_start_textview);
            startDateTextView.setText(startDateString);
        } else if (getFragmentManager().findFragmentByTag(getString(R.string.record_end_time)) != null) {
            calendar.set(endYear, endMonthOfYear, endDayOfMonth, hourOfDay, minute);
            endDate = calendar.getTime();
            String endDateString = dateFormat.format(endDate);
            TextView endDateTextView = (TextView) findViewById(R.id.chart_set_end_textview);
            endDateTextView.setText(endDateString);
        }
    }
}

