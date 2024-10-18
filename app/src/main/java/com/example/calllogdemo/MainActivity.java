package com.example.calllogdemo;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView lvCallLog;
    CallLogAdapter callLogAdapter;
    Handler myHandler;
    View ftView;
    boolean isLoading = false;
    int currentPosition = 20;
    int loadQuantity = 20;
    ArrayList<MyCallLog> allCallLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lvCallLog = findViewById(R.id.lv_call_log);
        allCallLogs = getCallLogs();
        ArrayList<MyCallLog> callLogs = getMoreCallLogs(0, currentPosition);
        callLogAdapter = new CallLogAdapter(this, callLogs);
        lvCallLog.setAdapter(callLogAdapter);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ftView = layoutInflater.inflate(R.layout.footer_view, null);
        myHandler = new MyHandler();

        lvCallLog.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //Check when scroll to last item in listview. The 10 is the number of init data in listview
                if (absListView.getLastVisiblePosition() == totalItemCount - 1 && lvCallLog.getCount() >= 10 && !isLoading) {
                    isLoading = true;
                    Thread thread = new ThreadGetMoreCallLogs();
                    thread.start();
                }
            }
        });
    }

    public ArrayList<MyCallLog> getCallLogs() {
        ArrayList<MyCallLog> callLogs = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG}, 0);
            return callLogs;
        }

        String[] projection = new String[] {
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE
        };
         String sortOrder = CallLog.Calls.DATE + " COLLATE LOCALIZED DESC";

        Cursor cursor =  this.getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, null, null, sortOrder);
        while (cursor.moveToNext()) {
            String nameOrNumber = cursor.getString(0);
            String number = cursor.getString(1);
            String type = cursor.getString(2); // https://developer.android.com/reference/android/provider/CallLog.Calls.html#TYPE
            String callDate = cursor.getString(3); // epoch time - https://developer.android.com/reference/java/text/DateFormat.html#parse(java.lang.String

            Log.i("TYPE", type);

            if (nameOrNumber.isEmpty()) {
                nameOrNumber = number;
            }

            long seconds = Long.parseLong(callDate);
            SimpleDateFormat formatter = new SimpleDateFormat("E, dd-MM-yy HH:mm");
            String dateString = formatter.format(new Date(seconds));
            callLogs.add(new MyCallLog(type, nameOrNumber, dateString));
        }
        cursor.close();

        return callLogs;
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //Add loading view during search processing
                    lvCallLog.addFooterView(ftView);
                    break;
                case 1:
                    //Update data adapter and UI
                    callLogAdapter.addItemsToAdapter((ArrayList<MyCallLog>) msg.obj);
                    lvCallLog.removeFooterView(ftView);
                    isLoading = false;
                    break;
                default:
                    break;
            }
        }
    }

    public ArrayList<MyCallLog> getMoreCallLogs(int start, int end) {
        List<MyCallLog> listCallLogs = allCallLogs.subList(start, end);
        ArrayList<MyCallLog> moreCallLogs = new ArrayList<>(listCallLogs.size());
        moreCallLogs.addAll(listCallLogs);
        //Get new data here...

        return moreCallLogs;
    }

    public class ThreadGetMoreCallLogs extends Thread {
        @Override
        public void run() {
            //Add footer view after get data
            myHandler.sendEmptyMessage(0);

            //Search more data
            ArrayList<MyCallLog> moreCallLogs = getMoreCallLogs(++currentPosition, currentPosition += loadQuantity);

            //Delay time to show loading footer when loading, remove it when release
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //Send result to Handle
            Message msg = myHandler.obtainMessage(1, moreCallLogs);
            myHandler.sendMessage(msg);

        }
    }
}