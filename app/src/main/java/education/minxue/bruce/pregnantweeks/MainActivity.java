package education.minxue.bruce.pregnantweeks;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private TextView tv;
    private Handler mHandler;
    private final int CALENDAR_ID = 2;
    private ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        tv = (TextView) findViewById(R.id.text);
        setSupportActionBar(toolbar);

        Button fab = (Button) findViewById(R.id.buttonAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv.setText("");
                new CreateScheduleTask().execute();
            }
        });

        Button fab2 = (Button) findViewById(R.id.buttonDel);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv.setText("");
                new DeleteScheduleTask().execute();
            }
        });

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                tv.setText("No Permission");
            }
        };
    }

    private class DeleteScheduleTask extends AsyncTask<URL, String, Long> {
        protected Long doInBackground(URL... urls) {
            final String[] EVENT_PROJECTION = new String[]{
                    CalendarContract.Calendars._ID,
                    CalendarContract.Events.CALENDAR_ID,
                    CalendarContract.Events.TITLE
            };

            Cursor cur = null;
            ContentResolver cr = getContentResolver();
            Uri uri = CalendarContract.Events.CONTENT_URI;
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                mHandler.sendEmptyMessage(0);
                return -1L;
            }
            String selection = CalendarContract.Events.CALENDAR_ID + " = " + CALENDAR_ID;

            Log.e("Calendar pregnancy", "selection:" + selection);
// Submit the query and get a Cursor object back.
            cur = cr.query(uri, EVENT_PROJECTION, selection, null, null);

            while (cur != null && cur.moveToNext()) {
                long calID = 0;

                // Get the field values
                calID = cur.getLong(0);

                // Do something with the values...
                Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, calID);
                int rows = getContentResolver().delete(deleteUri, null, null);
                if (rows > 0)
                    publishProgress(cur.getString(2) + " Deleted" + "\n");
            }


            return 0L;
        }


        protected void onProgressUpdate(String... date) {
            tv.append(date[0]);
            mScrollView.post(new Runnable() {
                @Override
                public void run() {
                    mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }

        protected void onPostExecute(Long result) {
            tv.append("Del Success");
        }
    }

    private class CreateScheduleTask extends AsyncTask<URL, String, Long> {
        protected Long doInBackground(URL... urls) {
            Calendar dueTime = Calendar.getInstance();
            dueTime.set(2018, 10, 4, 0, 02);
            Calendar beginTime = Calendar.getInstance();
            beginTime.set(2018, 0, 29, 0, 01);
            Calendar endTime = Calendar.getInstance();
            endTime.set(2018, 0, 29, 0, 11);
            int count = 1;
            while (beginTime.before(dueTime)) {
                long startMillis = 0;
                long endMillis = 0;
                startMillis = beginTime.getTimeInMillis();
                endMillis = endTime.getTimeInMillis();

                String str = "孕";
                str += count / 7;
                str += "周";
                str += count % 7;
                str += "天";


                ContentResolver cr = getContentResolver();
                ContentValues values = new ContentValues();
                values.put(CalendarContract.Events.DTSTART, startMillis);
                values.put(CalendarContract.Events.DTEND, endMillis);
                values.put(CalendarContract.Events.TITLE, str);
                values.put(CalendarContract.Events.DESCRIPTION, "陪伴照顾老婆，宽容因怀孕不正常的脾气，保证老婆每天都开开心心的。\n" +
                        "我们一定生个健康聪明的宝宝！");
                values.put(CalendarContract.Events.CALENDAR_ID, CALENDAR_ID);
                values.put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PUBLIC);
                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Hong_Kong");
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    mHandler.sendEmptyMessage(0);
                    return -1L;

                }
                Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                publishProgress(format1.format(beginTime.getTime()) + ", " + str + "\n");

                beginTime.add(Calendar.DATE, 1);
                endTime.add(Calendar.DATE, 1);
                count++;
            }

            return 0L;
        }

        protected void onProgressUpdate(String... date) {
            tv.append(date[0]);
            mScrollView.post(new Runnable() {
                @Override
                public void run() {
                    mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }

        protected void onPostExecute(Long result) {
            if (result > -1)
                tv.append("Add Success");
        }
    }

    private void addOneEvent() {
        long calID = 3;
        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2018, 0, 21, 0, 01);
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(2018, 0, 21, 0, 11);
        endMillis = endTime.getTimeInMillis();


        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, "孕0周1天");
        values.put(CalendarContract.Events.DESCRIPTION, "陪伴照顾老婆，宽容因怀孕不正常的脾气，保证老婆每天都开开心心的。\n" +
                "我们一定生个健康聪明的宝宝！");
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Hong_Kong");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            tv.setText("No Permission");
            return;
        }
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        tv.setText("Success");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
