package com.example.calllogdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CallLogAdapter extends BaseAdapter {
    Context context;
    ArrayList<MyCallLog> callLogs;
    LayoutInflater inflater;

    public CallLogAdapter(Context context, ArrayList<MyCallLog> callLogs) {
        this.context = context;
        this.callLogs = callLogs;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return callLogs.size();
    }

    @Override
    public MyCallLog getItem(int i) {
        return callLogs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.item_call_log, null);
        ImageView iv_type = view.findViewById(R.id.iv_type);
        TextView tv_phone = view.findViewById(R.id.tv_name_or_phone);
        TextView tv_date = view.findViewById(R.id.tv_date);

        if (callLogs.get(i).getType().equals("1")) {
            iv_type.setImageResource(R.drawable.ic_incoming_call);
        }
        else if (callLogs.get(i).getType().equals("2")) {
            iv_type.setImageResource(R.drawable.ic_outgoing_call);
        }
        else if (callLogs.get(i).getType().equals("5")) {
            iv_type.setImageResource(R.drawable.ic_rejected_call );
        }
        else if (callLogs.get(i).getType().equals("3")) {
            iv_type.setImageResource(R.drawable.ic_missed_call);
        }
        tv_phone.setText(callLogs.get(i).getNameOrNumber());
        tv_date.setText(callLogs.get(i).getDate());
        return view;
    }
    
    public void addItemsToAdapter(ArrayList<MyCallLog> callLogs) {
        this.callLogs.addAll(callLogs);
        notifyDataSetChanged();
    }


}
