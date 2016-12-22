package com.lena.timetracker;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.lena.timetracker.dataobjects.DOCategory;
import com.lena.timetracker.date.time.DatePickerFragment;
import com.lena.timetracker.date.time.TimePickerFragment;
import com.lena.timetracker.db.TimeTrackerContract;
import com.lena.timetracker.db.TimeTrackerDbHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.lena.timetracker.db.TimeTrackerContract.Record.TEMP_CATEGORY_ID;

public class TotalTimeByCategoriesActivity extends AppCompatActivity
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
    private ArrayList<DOCategory> categories;
    private CustomArrayAdapter customArrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_time_by_categories);

        showAvailableCategories();

        final TextView startTextView = (TextView) findViewById(R.id.total_record_set_start_textview);
        startTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getFragmentManager(), getString(R.string.record_start));
            }
        });

        TextView endTextView = (TextView) findViewById(R.id.total_set_end_textview);
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
                calculateTotalTimeByCategories();
            }
        });

        Button forAMonthButton = (Button) findViewById(R.id.for_a_month_button);
        forAMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateTotalTimeByCategoriesForAMonth();
            }
        });

    }

    public class CustomArrayAdapter extends ArrayAdapter<DOCategory> {
        private DOCategory[] myObjects;
        private boolean[] checkboxesStates;

        CustomArrayAdapter(Context context, int textViewResourceId,
                           DOCategory[] objects) {
            super(context, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
            this.myObjects = objects;
            checkboxesStates = new boolean[myObjects.length];
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        private View getCustomView(final int position, View convertView,
                                   ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.category_with_checkbox_item, parent, false);
            TextView label = (TextView) row.findViewById(R.id.item_textview);
            label.setText(myObjects[position].getName());
            final CheckBox cb = (CheckBox) row.findViewById(R.id.checkBox);

            // Register listener
            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkboxesStates[position] = cb.isChecked();
                }
            });

            return row;
        }

        public ArrayList<DOCategory> getAllChecked() {
            ArrayList<DOCategory> allChecked = new ArrayList<>();
            for (int pos = 0; pos < checkboxesStates.length; pos++) {
                if (checkboxesStates[pos]) {
                    allChecked.add(myObjects[pos]);
                }
            }
            return allChecked;
        }
    }

    private void showAvailableCategories() {
        ArrayList<DOCategory> categories = CreateOrEditRecordActivity.getCategoriesFromDb(this);
        CustomArrayAdapter arrayAdapter = new CustomArrayAdapter(this, R.layout.spinner_item,
                categories.toArray(new DOCategory[categories.size()]));
        ListView categoriesListView = (ListView) findViewById(R.id.total_listview);
        categoriesListView.setAdapter(arrayAdapter);
        customArrayAdapter = arrayAdapter;
    }

    private void calculateTotalTimeByCategoriesForAMonth() {
        Calendar calendar = Calendar.getInstance();
        Date currDate = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat(MainActivity.DATE_FORMAT);
        String temp = dateFormat.format(currDate);
        String month = temp.substring(temp.indexOf("-") + 1, temp.lastIndexOf("-"));
        ArrayList<DOCategory> tempCategories = customArrayAdapter.getAllChecked();
        if (tempCategories.size() > 0) {
            ArrayList<Long> ids = new ArrayList<>();
            for (DOCategory tempCategory : tempCategories) {
                ids.add(tempCategory.getId());
            }
            LinkedHashMap<String, Long> data = getDataFromDb(month, ids);
            StringBuilder sbCategory = new StringBuilder();
            StringBuilder sbTime = new StringBuilder();

            if (data.size() != 0) {
                int i = 1;
                for (Map.Entry<String, Long> categorySum : data.entrySet()) {
                    sbCategory.append("" + i + ". " + categorySum.getKey() + "\n");
                    sbTime.append(ShowRecordActivity.timePeriodToString(categorySum.getValue()) + "\n");
                    i++;
                }
            } else {
                sbCategory.append(getString(R.string.stat_no_records));
            }

            TextView categTextView = (TextView) findViewById(R.id.total_result_textView);
            categTextView.setText(sbCategory.toString());

            TextView timeTextView = (TextView) findViewById(R.id.total_sum_result_textView);
            timeTextView.setText(sbTime.toString());

            TextView periodTextView = (TextView) findViewById(R.id.total_period_textView);
            periodTextView.setText(getString(R.string.stat_for_month));
        }
        else {
            Toast.makeText(this, R.string.stat_no_checked_categories, Toast.LENGTH_LONG).show();
        }
    }

    private void calculateTotalTimeByCategories() {
        if (startDate != null && endDate != null) {
            ArrayList<DOCategory> tempCategories = customArrayAdapter.getAllChecked();
            if (tempCategories.size() > 0) {
                ArrayList<Long> ids = new ArrayList<>();
                for (DOCategory tempCategory : tempCategories) {
                    ids.add(tempCategory.getId());
                }
                LinkedHashMap<String, Long> data = getDataFromDb(startDate, endDate, ids);
                StringBuilder sbCategory = new StringBuilder();
                StringBuilder sbTime = new StringBuilder();

                if (data.size() != 0) {
                    int i = 1;
                    for (Map.Entry<String, Long> categorySum : data.entrySet()) {
                        sbCategory.append("" + i + ". " + categorySum.getKey() + "\n");
                        sbTime.append(ShowRecordActivity.timePeriodToString(categorySum.getValue()) + "\n");
                        i++;
                    }
                } else {
                    sbCategory.append(getString(R.string.stat_no_records));
                }

                TextView categTextView = (TextView) findViewById(R.id.total_result_textView);
                categTextView.setText(sbCategory.toString());

                TextView timeTextView = (TextView) findViewById(R.id.total_sum_result_textView);
                timeTextView.setText(sbTime.toString());

                DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy  HH:mm");
                TextView periodTextView = (TextView) findViewById(R.id.total_period_textView);
                periodTextView.setText(getString(R.string.stat_period) + "  " + dateFormat.format(startDate)
                        + "    -   " + dateFormat.format(endDate));
            }
            else {
                Toast.makeText(this, R.string.stat_no_checked_categories, Toast.LENGTH_LONG).show();
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
            TextView startDateTextView = (TextView) findViewById(R.id.total_record_set_start_textview);
            startDateTextView.setText(startDateString);
        } else if (getFragmentManager().findFragmentByTag(getString(R.string.record_end_time)) != null) {
            calendar.set(endYear, endMonthOfYear, endDayOfMonth, hourOfDay, minute);
            endDate = calendar.getTime();
            String endDateString = dateFormat.format(endDate);
            TextView endDateTextView = (TextView) findViewById(R.id.total_set_end_textview);
            endDateTextView.setText(endDateString);
        }
    }
}
