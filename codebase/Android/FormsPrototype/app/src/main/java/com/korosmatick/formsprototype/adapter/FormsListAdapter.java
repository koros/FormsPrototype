package com.korosmatick.formsprototype.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.korosmatick.formsprototype.R;
import com.korosmatick.formsprototype.model.Form;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by koros on 11/23/15.
 */
public class FormsListAdapter extends ArrayAdapter<Form> {

    List<Form> items = new ArrayList<Form>();
    LayoutInflater layoutInflater;

    public FormsListAdapter(Context context, int textViewResourceId, List<Form> items){
        super(context, textViewResourceId, items);
        this.items = items;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = layoutInflater.inflate(R.layout.list_view_item, null);
        TextView textView = (TextView)convertView.findViewById(R.id.itemTxt);
        Form form = getItem(position);
        if (form != null){
            textView.setText(form.getFormName());
        }
        return convertView;
    }
}
