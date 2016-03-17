package com.korosmatick.formsprototype.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.korosmatick.formsprototype.R;
import com.korosmatick.formsprototype.model.OptionsItem;

import java.util.List;

/**
 * Created by koros on 3/6/16.
 */
public class OptionsAdapter extends ArrayAdapter<OptionsItem> {

    List<OptionsItem> nItems;
    LayoutInflater layoutInflater = null;
    boolean isShareMenu;
    private Context context;

    public OptionsAdapter(Context context, int textViewResourceId, List<OptionsItem> items) {
        this(context, textViewResourceId, items, false);
    }

    public OptionsAdapter(Context context, int textViewResourceId, List<OptionsItem> items, boolean isShareMenu) {
        super(context, R.layout.list_view_item, items);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.isShareMenu = isShareMenu;
        this.context = context;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OptionsItem item = (OptionsItem) getItem(position);

        convertView = layoutInflater.inflate(R.layout.list_view_item, null);
        TextView text1 = (TextView) convertView.findViewById(R.id.itemTxt);
        text1.setText(item.getDisplayName());

        return convertView;
    }

    public void setItems(List<OptionsItem> items) {
        this.nItems = items;
    }
}
