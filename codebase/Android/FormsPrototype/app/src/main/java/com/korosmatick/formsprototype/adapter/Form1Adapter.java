package com.korosmatick.formsprototype.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.korosmatick.formsprototype.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by koros on 9/23/15.
 */
public class Form1Adapter extends ArrayAdapter<Long> {

    List<Long> items = new ArrayList<Long>();
    LayoutInflater layoutInflater;

    public Form1Adapter(Context context, int textViewResourceId, List<Long> items){
        super(context, textViewResourceId, items);
        this.items = items;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = layoutInflater.inflate(R.layout.list_view_item, null);
        TextView textView = (TextView)convertView.findViewById(R.id.itemTxt);
        Long houseHold = getItem(position);
        if (houseHold != null){
            //textView.setText(houseHold.getForm_id() + "  " + houseHold.getMeta().getInstanceID());
            textView.setText(houseHold.toString());
        }
        return convertView;
    }

}
