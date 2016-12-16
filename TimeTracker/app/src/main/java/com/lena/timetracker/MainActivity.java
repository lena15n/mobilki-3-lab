package com.lena.timetracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.lena.timetracker.db.TimeTrackerDbHelper;

public class MainActivity extends AppCompatActivity {

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
                Intent intent = new Intent(getApplicationContext(), CreateRecordActivity.class);
                startActivity(intent);
            }
        });

        TimeTrackerDbHelper mDbHelper = new TimeTrackerDbHelper(this);
        /*
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String title = "My new title!";
        String subtitle = "My new subtitle!";
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_TITLE, title);
        values.put(TimeTrackerContract.FeedEntry.COLUMN_NAME_SUBTITLE, subtitle);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TimeTrackerContract.FeedEntry.TABLE_NAME, null, values);
        db.close();

         SQLiteDatabase db1 = mDbHelper.getReadableDatabase();
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                TimeTrackerContract.FeedEntry._ID,
                COLUMN_NAME_TITLE,
                TimeTrackerContract.FeedEntry.COLUMN_NAME_SUBTITLE
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = {"My new title!"};

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                TimeTrackerContract.FeedEntry.COLUMN_NAME_SUBTITLE + " DESC";

        Cursor cursor = db1.query(
                TimeTrackerContract.FeedEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        String num = "";

        if( cursor != null && cursor.moveToFirst()){
            num = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TITLE));
            cursor.close();
        }

        if(cursor.getCount() > 0){
            // get values from cursor here
            Log.d("Mi", "it>0");
        }

        TextView textView = (TextView) findViewById(R.id.main_header);
        textView.setText(num + " azzza");
        db1.close(); */


       /* cursor.moveToFirst();
        long itemId = 0;
        itemId = cursor.getLong(
                cursor.getColumnIndexOrThrow(TimeTrackerContract.FeedEntry._ID)
        );

        itemId = 1 + itemId;
        db.close();*/
        /*  DELETE
        // Define 'where' part of query.
        String selection = FeedEntry.COLUMN_NAME_TITLE + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {"MyTitle"};
        // Issue SQL statement.
        db.delete(FeedEntry.TABLE_NAME, selection, selectionArgs);*/



        /*  UPDATE
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_TITLE, title);

        // Which row to update, based on the title
        String selection = FeedEntry.COLUMN_NAME_TITLE + " LIKE ?";
        String[] selectionArgs = { "MyTitle" };

        int count = db.update(
            TimeTrackerDbHelper.FeedEntry.TABLE_NAME,
            values,
            selection,
            selectionArgs);
            */
    }

    @Override
    protected void onResume() {
        super.onResume();
        //select records from db
      /*  ListView meetingsListView = (ListView) findViewById(R.id.all_records_listview);
        if (meetingsListView != null) {
            final ArrayAdapter<Meeting> arrayAdapter = new ArrayAdapter<Meeting>(this,
                    android.R.layout.simple_list_item_2, android.R.id.text1, meetings) {
                @NonNull
                @Override
                public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                    TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                    text1.setText(meetings.get(position).getName());
                    text2.setText(meetings.get(position).getDescription());
                    return view;
                }
            };

            meetingsListView.setAdapter(arrayAdapter);
            meetingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                    Meeting currentMeeting = arrayAdapter.getItem(position);

                    Context context = getApplicationContext();
                    Intent intent = new Intent(context, ShowRecordActivity.class);
                    Gson gson = new GsonBuilder().setDateFormat("yyy-MM-dd'T'HH:mm:ss").create();
                    intent.putExtra(context.getString(R.string.meeting), gson.toJson(currentMeeting));
                    startActivity(intent);
                }
            });
        }*/

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
                //
            }
            break;
            case R.id.action_the_biggest_total_time: {
                //

            }
            break;
            case R.id.action_total_time_by_selected_categories: {

            }
            break;
            case R.id.action_total_time: {

            }
            break;
        }

        return super.onOptionsItemSelected(item);
    }


}
