package dv606.my222au.assignment2.myCountriesCalendar;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

;
import dv606.my222au.assignment2.R;

public class MyCountriesCalendar extends AppCompatActivity implements CalendarProviderClient {
    private static final String TAG = MyCountriesCalendar.class.getSimpleName();
    private static final int UPPDATE = 1;
    private static final int DELETE = 2;
    private static final int SORT_BY_YEAR_DESC = 3;
    private static final int SORT_BY_YEAR_ASC = 4;
    private static final int SORT_BY_COUNTRY_DESC = 5;
    private static final int SORT_BY_COUNTRY_ASC = 6;
    private static final String SORT_ORDER = "sortorder";
    private static final String SORT_PREF = "sortprefname";

    private ListView mListView;
    private String mCountry;  // chagne name to country and name
    private int mYear;
    private TextView mWelcomTv;
    private ImageView mArrow;
    private LinearLayout mLinearLayout;
    private SimpleCursorAdapter mSimpleCursorAdapter;
    private RelativeLayout mlayout;
    private int mEventId;

    private String sortOrder;


    //TODO Create a List for the Cursor


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_countries_calender);

        mListView = (ListView) findViewById(R.id.list);
        mWelcomTv = (TextView) findViewById(R.id.welcomeLabel);
        mArrow = (ImageView) findViewById(R.id.iconImageView);
        mLinearLayout = (LinearLayout) findViewById(R.id.myLinear);
        mlayout = (RelativeLayout) findViewById(R.id.activityLayout);

        if (checkCalendarExist()) {  // this returns false which means that the calandar dosent exist in Calendar list
            creatCountriesCalendar(); // create calender if its not there
            Log.i(TAG, " Calendar Created");
        } else
            Log.i(TAG, "Calendar AlReady Exist");

        adaptToListview();
        setEventId();
        registerForContextMenu(mListView);


        // resorts the list with last sort order the user applied fomr sharedpref
        Bundle bundle = new Bundle();
        SharedPreferences sortPref = getSharedPreferences(SORT_PREF, MODE_PRIVATE);
        String order = sortPref.getString(SORT_ORDER, "");
        if (order != null) {
            bundle.putString(SORT_ORDER, order);
            getLoaderManager().restartLoader(LOADER_MANAGER_ID, bundle, this);
        }


    }


    public void onResume() {
        prefrenceSettings();
        super.onResume();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        menu.add(0, SORT_BY_YEAR_DESC, 0, "Sort by year desceinding order");
        menu.add(0, SORT_BY_YEAR_ASC, 1, "sort by Year ascending order");
        menu.add(0, SORT_BY_COUNTRY_DESC, 2, "sort by Country desceinding order");
        menu.add(0, SORT_BY_COUNTRY_ASC, 3, "sort by  Country ascending order");
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Bundle bundle = new Bundle();
        switch (item.getItemId()) {
            case R.id.addbutton:
                Intent intent = new Intent(this, AddCountry.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, 0);
                mLinearLayout.setVisibility(View.VISIBLE);
                return true;

            case SORT_BY_YEAR_DESC:
                Sort(SORT_BY_YEAR_DESC);
                break;

            case SORT_BY_YEAR_ASC:
                Sort(SORT_BY_YEAR_ASC);
                break;


            case SORT_BY_COUNTRY_DESC:
                Sort(SORT_BY_COUNTRY_DESC);
                break;

            case SORT_BY_COUNTRY_ASC:
                Sort(SORT_BY_COUNTRY_ASC);
                break;

            case R.id.settings:
                Intent prefrenceIntent = new Intent(this, Preferences.class);
                prefrenceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(prefrenceIntent);
                break;


        }
        return super.onOptionsItemSelected(item);
    }


    private void Sort(int id) {
        Bundle bundle = new Bundle();
        switch (id) {
            case SORT_BY_YEAR_DESC:
                sortOrder = CalendarContract.Events.DTSTART + " DESC";
                break;


            case SORT_BY_YEAR_ASC:
                sortOrder = CalendarContract.Events.DTSTART + " ASC";
                break;


            case SORT_BY_COUNTRY_DESC:
                sortOrder = CalendarContract.Events.TITLE + " DESC";
                break;


            case SORT_BY_COUNTRY_ASC:
                sortOrder = CalendarContract.Events.TITLE + " ASC";
                break;


            default:
                sortOrder = CalendarContract.Events.DTSTART + " DESC";
                break;

        }
        bundle.putString(SORT_ORDER, sortOrder);
        SharedPreferences sortpref = getSharedPreferences(SORT_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sortpref.edit();
        editor.putString(SORT_ORDER, sortOrder);
        editor.commit();
        getLoaderManager().restartLoader(LOADER_MANAGER_ID, bundle, this);

    }


    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Options");
        menu.add(0, UPPDATE, 0, "Uppdate");
        menu.add(0, DELETE, 0, "Delete");
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                mCountry = data.getStringExtra("result");
                mYear = data.getIntExtra("result2", 0);

                addNewEvent(mYear, mCountry);
                mLinearLayout.setVisibility(View.VISIBLE);
            }
        }

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                mCountry = data.getStringExtra("result");
                mYear = data.getIntExtra("result2", 0);
                updateEvent(setEventId(), mYear, mCountry);

            }
        }

    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case UPPDATE:
                Intent intent = new Intent(this, AddCountry.class);
                intent.putExtra("country", mCountry);
                intent.putExtra("year", mYear);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, 1);


                return true;
            case DELETE:
                deleteEvent(setEventId());

                return true;


            default:
                return super.onContextItemSelected(item);
        }
    }


    // using simplecursor to add the list view with binding between the values and the widgets
    private void adaptToListview() {

        String[] bindFrom = {CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART};
        int[] bindTo = {R.id.country, R.id.yearDate};
        mSimpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.country_item_list, null, bindFrom, bindTo, 0);
        mSimpleCursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == R.id.yearDate) {
                    long year = cursor.getLong(cursor.getColumnIndex(CalendarContract.Events.DTSTART));
                    TextView tv = (TextView) view;
                    tv.setText(CalendarUtils.getEventYear(year) + "");

                    return true;
                }
                return false;
            }
        });

        mListView.setAdapter(mSimpleCursorAdapter);
        getLoaderManager().initLoader(LOADER_MANAGER_ID, null, this);

    }


    @Override
    public long getMyCountriesCalendarId() {
        String[] projection = new String[]{CalendarContract.Calendars._ID};
        String selection = CALENDARS_LIST_SELECTION;

        String[] secletionArgs = CALENDARS_LIST_SELECTION_ARGS;

        Cursor cursor = getContentResolver().query(CALENDARS_LIST_URI, projection, selection, secletionArgs, null);
        if (cursor.moveToFirst()) {
            return cursor.getLong(0);
        }
        return -1;
    }


    @Override
    public void addNewEvent(int year, String country) {
        long calID = getMyCountriesCalendarId();
        ContentResolver cr = getContentResolver();
        ContentValues cValues = new ContentValues();
        cValues.put(CalendarContract.Events.DTSTART, CalendarUtils.getEventStart(year));
        cValues.put(CalendarContract.Events.DTEND, CalendarUtils.getEventEnd(year));
        cValues.put(CalendarContract.Events.TITLE, country);
        cValues.put(CalendarContract.Events.CALENDAR_ID, calID);
        cValues.put(CalendarContract.Events.EVENT_TIMEZONE, CalendarUtils.getTimeZoneId());
        Uri uri = cr.insert(EVENTS_LIST_URI, cValues);


        Toast.makeText(this, "Created Calendar Event " + uri.getLastPathSegment(),
                Toast.LENGTH_SHORT).show();
    }


    @Override
    public void updateEvent(int eventId, int year, String country) {
        ContentResolver cr = getContentResolver();
        ContentValues cValues = new ContentValues();
        eventId = setEventId();
        cValues.put(CalendarContract.Events.TITLE, country);
        Uri updateUri = null;
        cValues.put(CalendarContract.Events.DTSTART, CalendarUtils.getEventStart(year));
        cValues.put(CalendarContract.Events.DTEND, CalendarUtils.getEventEnd(year));
        updateUri = ContentUris.withAppendedId(EVENTS_LIST_URI, eventId);
        Log.i(TAG, eventId + "");
        cr.update(updateUri, cValues, null, null);
        int rows = cr.update(updateUri, cValues, null, null);
        Log.i(TAG, "Rows updated: " + rows);
        getLoaderManager().restartLoader(LOADER_MANAGER_ID, null, this);
    }

    @Override
    public void deleteEvent(int eventId) {

        Uri deleteUri = null;
        deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
        int rows = getContentResolver().delete(deleteUri, null, null);
        Log.i(TAG, "Rows deleted: " + rows + "" + eventId);
        getLoaderManager().restartLoader(LOADER_MANAGER_ID, null, this);


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = CalendarContract.Events.CALENDAR_ID + "=" + getMyCountriesCalendarId();  /// Choses only my
        String[] slectionArgs = new String[]{"" + getMyCountriesCalendarId()};
        Log.d("TEST", "Loader Created");
        if (args != null) {
            String sort = args.getString(SORT_ORDER);
            return new CursorLoader(this, EVENTS_LIST_URI, EVENTS_LIST_PROJECTION, selection, null, sort);
        } else
            return new CursorLoader(this, EVENTS_LIST_URI, EVENTS_LIST_PROJECTION, selection, null, null);


    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mSimpleCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d("TEST", " Loader Restarted");

        mSimpleCursorAdapter.swapCursor(null);

    }


    private static Uri asSyncAdapter(Uri uri, String account, String accountType) {
        return uri.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, account)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, accountType).build();
    }


    // Creates a new Calender by using syncAdpater  and content resolver.

    private void creatCountriesCalendar() {

        Uri newCalendarUri;

        ContentValues cValues = new ContentValues();

        cValues.put(CalendarContract.Calendars.ACCOUNT_NAME, ACCOUNT_TITLE);
        cValues.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        cValues.put(CalendarContract.Calendars.NAME, CALENDAR_TITLE);
        cValues.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDAR_TITLE);
        cValues.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_READ);
        cValues.put(CalendarContract.Calendars.OWNER_ACCOUNT, ACCOUNT_TITLE);
        cValues.put(CalendarContract.Calendars.VISIBLE, 1);  // Show the calender
        cValues.put(CalendarContract.Calendars.SYNC_EVENTS, 1); // store the events in the device

        newCalendarUri = asSyncAdapter(CALENDARS_LIST_URI, ACCOUNT_TITLE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        ContentResolver cr = this.getContentResolver();
        cr.insert(newCalendarUri, cValues);

    }



    private boolean checkCalendarExist() {
        String[] projection = new String[]{CalendarContract.Calendars._ID,
                CalendarContract.Calendars.NAME,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.ACCOUNT_TYPE};
        long calID = 0;


            Cursor cursor =
                    getContentResolver().
                            query(CalendarContract.Calendars.CONTENT_URI, projection, CalendarContract.Calendars.VISIBLE + "=1", null, null);
            while (cursor.moveToNext()) {
                // Get the field value
                calID = cursor.getLong(PROJ_CALENDARS_LIST_ID_INDEX);
            }


            if (getMyCountriesCalendarId() != calID) {
                return true;
            }

        return false;

    }

    private int setEventId() {
        String selection = "((" + CalendarContract.Events.CALENDAR_ID + "= ?))";  /// Choses only my
        String[] slectionArgs = new String[]{"" + getMyCountriesCalendarId()};
        Log.d("TEST", "Loader Created");
        Cursor cursor = getContentResolver().query(EVENTS_LIST_URI, EVENTS_LIST_PROJECTION,
                selection, slectionArgs, null);

        while (cursor.moveToNext()) {
            String title = null;
            int eventID = 0;
            long beginVal = 0;

            mEventId = cursor.getInt(PROJ_EVENTS_LIST_ID_INDEX);
        }
        return mEventId;

    }




    private void  prefrenceSettings (){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String background = pref.getString("background","");
        Log.d(TAG, "PrefrenceSettings: "+ background);
        int color;
        switch (background){
            case "blue":
                color= getResources().getColor(R.color.blue);
                break;
            case "yellow":
                color=getResources().getColor(R.color.yellow);
                mWelcomTv.setTextColor(Color.BLACK);
                mArrow.setColorFilter(Color.BLACK);
                break;
            case "red":
                color = getResources().getColor(R.color.red);
                break;
            case"default":
                color =getResources().getColor(R.color.defult);

            default:
                color= getResources().getColor(R.color.defult);
                break;
        }

        mlayout.setBackgroundColor(color);



    }

}














