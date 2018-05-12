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
import android.provider.CalendarContract.*;
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
    private final int CALENDAR_ID = 1;
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

        Button fab3 = (Button) findViewById(R.id.buttonQuery);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv.setText("");
                new QueryScheduleTask().execute();
            }
        });

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                tv.append((String) msg.obj);
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
                Message msg = mHandler.obtainMessage(0, "No Permission");
                msg.sendToTarget();
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

                String title = "孕";
                int weeks = count / 7;
                title += weeks;
                title += "周";
                int days = count % 7;
                title += days;
                title += "天";

                String tempTitle = "";
                String tempDescription = "";
//                if (weeks == 12 && days == 0) {
//                    tempTitle = "产检1";
//                    tempDescription = "第一次检查：(13 周之前)" + "\n" +
//                            "建立《深圳市母子保健手册》;尿HCG、妇科检查、血常规、尿常规、心电图、超声常规检查、胎盘成熟度检查、血红蛋白电泳试验(地贫筛查);";
//                } else if (weeks == 16 && days == 0) {
//
//                    tempTitle = "产检2";
//                    tempDescription = "第二次检查：(16—18 周)" + "\n" +
//                            "产前检查(均含胎心多普勒)、血型(ABO、Rh)、血常规、尿常规、肾功能3项(尿素氮、肌酐、尿酸)、" + "\n" +
//                            "肝功能6项(谷草转氨酶、谷丙转氨酶、总蛋白、白蛋白、总胆红素、胆汁酸)、乙肝两对半、丙肝病毒抗体、梅毒血清抗体、血糖、" + "\n" +
//                            "唐氏筛查项目(包括甲胎蛋白、雌三醇、绒毛膜促性腺激素)、甲功三项(备选);";
//                } else if (weeks == 20 && days == 0) {
//                    tempTitle = "产检3";
//                    tempDescription = "第三次检查：(20—24 周)" + "\n" +
//                            "产前检查、尿常规、超声常规检查(包括胎儿产前诊断项目)、胎儿脐血监测和胎盘成熟度检测;";
//                } else if (weeks == 24 && days == 0) {
//                    tempTitle = "产检4";
//                    tempDescription = "第四次检查：(24—28 周)" + "\n" +
//                            "产前检查、尿常规、血糖筛查、抗D滴度检查(备选);";
//                } else if (weeks == 28 && days == 0) {
//                    tempTitle = "产检5";
//                    tempDescription = "第五次检查：(28—30周)" + "\n" +
//                            "产前检查、尿常规、ABO抗体检测;";
//                } else if (weeks == 30 && days == 0) {
//                    tempTitle = "产检6";
//                    tempDescription = "第六次检查：(30—32周)" + "\n" +
//                            " 产前检查、血常规、尿常规、超声常规检查、胎盘成熟度检测;";
//                } else if (weeks == 32 && days == 0) {
//                    tempTitle = "产检7";
//                    tempDescription = " 第七次检查：(32—34周)" + "\n" +
//                            "产前检查、尿常规;";
//                } else if (weeks == 34 && days == 0) {
//                    tempTitle = "产检8";
//                    tempDescription = "第八次检查：(34—36周)" + "\n" +
//                            "产前检查、胎心监测、尿常规;";
//                } else if (weeks == 37 && days == 0) {
//                    tempTitle = "产检9";
//                    tempDescription = "第九次检查：(37周)" + "\n" +
//                            "产前检查、尿常规、超声常规检查、胎盘成熟度检测、血常规、肾功能3项(尿素氮、肌酐、尿酸)、肝功能6项(谷草转氨酶、谷丙转氨酶、总蛋白、白蛋白、总胆红素、胆汁酸)、胎心监测;";
//                } else if (weeks == 38 && days == 0) {
//                    tempTitle = "产检10";
//                    tempDescription = "第十次检查：(38周)" + "\n" +
//                            " 产前检查、胎心监测、尿常规;";
//                } else if (weeks == 39 && days == 0) {
//                    tempTitle = "产检11";
//                    tempDescription = "第十一次检查：(39周)" + "\n" +
//                            "产前检查、尿常规、超声常规检查、胎盘成熟度检测、胎心监测;";
//                } else if (weeks == 40 && days == 0) {
//                    tempTitle = "产检12";
//                    tempDescription = "第十二次检查：(40 周)" + "\n" +
//                            "产前检查、胎心监测、尿常规。";
//                }
                if (tempTitle != "")
                    addOneEvent(beginTime, endTime, tempTitle, tempDescription);

                ContentResolver cr = getContentResolver();
                ContentValues values = new ContentValues();
                values.put(CalendarContract.Events.DTSTART, startMillis);
                values.put(CalendarContract.Events.DTEND, endMillis);
                values.put(CalendarContract.Events.TITLE, title);
                values.put(CalendarContract.Events.DESCRIPTION, "宝宝健康聪明！\n我是超级奶爸，\n照顾好妻子+努力赚钱！");
                values.put(CalendarContract.Events.CALENDAR_ID, CALENDAR_ID);
                values.put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PUBLIC);
                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Hong_Kong");

//                Calendars.ACCOUNT_NAME,                  // 1
//                        Calendars.CALENDAR_DISPLAY_NAME,         // 2
//                        Calendars.OWNER_ACCOUNT
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    Message msg = mHandler.obtainMessage(0, "No Permission");
                    msg.sendToTarget();
                    return -1L;

                }
                Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

                SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                publishProgress(format1.format(beginTime.getTime()) + ", " + title + "\n");

                beginTime.add(Calendar.DATE, 1);
                endTime.add(Calendar.DATE, 1);
                count++;

            }

            //bind the events with calendar account
//            ContentValues values = new ContentValues();
//// The new display name for the calendar
//            values.put(CalendarContract.Calendars.ACCOUNT_NAME, "Phone");
//            values.put(CalendarContract.Calendars.OWNER_ACCOUNT, "Phone");
//            values.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, "产检");
//            values.put(CalendarContract.Calendars.ACCOUNT_TYPE, "com.android.huawei.phone");
//            Uri updateUri = ContentUris.withAppendedId(Calendars.CONTENT_URI, CALENDAR_ID);
//            int rows = getContentResolver().update(updateUri, values, null, null);
//            publishProgress("updated " + rows + " rows");

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

    private void addOneEvent(Calendar beginTime, Calendar endTime, String title, String description) {
        long startMillis = 0;
        long endMillis = 0;
        startMillis = beginTime.getTimeInMillis();
        endMillis = endTime.getTimeInMillis();


        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, description);
        values.put(CalendarContract.Events.CALENDAR_ID, CALENDAR_ID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Hong_Kong");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        Message msg = mHandler.obtainMessage(0, "Add Success" + title + "\n");
        msg.sendToTarget();

    }


    private class QueryScheduleTask extends AsyncTask<URL, String, Long> {
        protected Long doInBackground(URL... urls) {
            final String[] EVENT_PROJECTION = new String[]{
                    CalendarContract.Calendars._ID,                           // 0
                    CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
                    CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
                    CalendarContract.Calendars.OWNER_ACCOUNT,                  // 3
                    CalendarContract.Calendars.ACCOUNT_TYPE                  // 4
            };

// The indices for the projection array above.
            final int PROJECTION_ID_INDEX = 0;
            final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
            final int PROJECTION_DISPLAY_NAME_INDEX = 2;
            final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
            final int PROJECTION_OWNER_ACCOUNT_TYPE = 4;

            // Run query
            Cursor cur = null;
            ContentResolver cr = getContentResolver();
            Uri uri = Calendars.CONTENT_URI;
            String selection = "(" + Calendars.ACCOUNT_NAME + " = ?";
            String[] selectionArgs = new String[]{"xinanadu@gmail.com"};
            //            String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND ("
//                    + Calendars.ACCOUNT_TYPE + " = ?) AND ("
//                    + Calendars.OWNER_ACCOUNT + " = ?))";
//            String[] selectionArgs = new String[] {"hera@example.com", "com.example",
//                    "hera@example.com"};

// Submit the query and get a Cursor object back.
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                Message msg = mHandler.obtainMessage(0, "No Permission");
                msg.sendToTarget();
                return -1L;

            }
            cur = cr.query(uri, EVENT_PROJECTION, null, null, null);

            // Use the cursor to step through the returned records
            while (cur != null && cur.moveToNext()) {
                // Get the field values
                long calID = cur.getLong(PROJECTION_ID_INDEX);
                String displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
                String accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
                String ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
                String accountType = cur.getString(PROJECTION_OWNER_ACCOUNT_TYPE);

                // Do something with the values...
                publishProgress("calID:" + calID + "\ndisplayName:" + displayName + "\naccountName:" + accountName + "\nownerName:" + ownerName + "\naccountType:" + accountType + "\n\n");
            }

            return 0L;
        }

        protected void onProgressUpdate(String... events) {
            tv.append(events[0]);
            mScrollView.post(new Runnable() {
                @Override
                public void run() {
                    mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }

        protected void onPostExecute(Long result) {
            if (result > -1)
                tv.append("Query Success");
        }
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
