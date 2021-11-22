package com.example.blackbox_v10;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CaptureListAdapter extends ArrayAdapter<String> {
    LayoutInflater inflater;
    ArrayList<String> items;

    public CaptureListAdapter(Context context, int capture_list_row, ArrayList<String> lv_list) {
        super(context, capture_list_row, lv_list);

        items = lv_list;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;    // view = 해당 인덱스의 뷰

        if(view==null){
            view=inflater.inflate(R.layout.capture_list_row, null);  // 해당 뷰의 레이아웃을 row.xml로
        }

        TextView listitem_tv = (TextView) view.findViewById(R.id.listitem_tv);
        listitem_tv.setText(items.get(position));

        return view;
    }
}
