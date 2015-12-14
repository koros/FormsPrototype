package com.korosmatick.formsprototype.fragment;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.korosmatick.formsprototype.R;
import com.korosmatick.formsprototype.database.MySQLiteHelper;
import com.korosmatick.formsprototype.util.SyncManager;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by koros on 11/23/15.
 */
public class PreviewDataFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.tableLayout1) TableLayout tableLayout;

    String tableName = null;
    String[] columnNames = new String[]{};
    LayoutInflater layoutInflater;

    MySQLiteHelper mySQLiteHelper;

    @Bind(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;

    public void setTableName(String tableName){
        this.tableName = tableName;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutInflater = inflater;
        View view = inflater.inflate(R.layout.preview_data_fragment, container, false);
        ButterKnife.bind(this, view);

        swipeRefreshLayout.setOnRefreshListener(this);

        mySQLiteHelper = MySQLiteHelper.getInstance(getActivity());
        columnNames = mySQLiteHelper.getColumnNamesForTable(tableName);

        //TableRow rowTitle = getTableTitleView();
        TableRow tableHeaders = getColumnHeaders();
        //tableLayout.addView(rowTitle);
        tableLayout.addView(tableHeaders);

        addTableBody(tableLayout);

        return view;
    }

    public void refreshListView(){
        tableLayout.post(new Runnable() {
            @Override
            public void run() {
                tableLayout.removeAllViewsInLayout();

                mySQLiteHelper = MySQLiteHelper.getInstance(getActivity());
                columnNames = mySQLiteHelper.getColumnNamesForTable(tableName);

                //TableRow rowTitle = getTableTitleView();
                TableRow tableHeaders = getColumnHeaders();
                //tableLayout.addView(rowTitle);
                tableLayout.addView(tableHeaders);

                addTableBody(tableLayout);

                tableLayout.requestLayout();
            }
        });
    }

    public TableRow getTableTitleView(){
        TableRow rowTitle = new TableRow(getActivity());
        rowTitle.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView title = new TextView(getActivity());
        title.setText(tableName);

        title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        //title.setGravity(Gravity.CENTER);
        title.setTypeface(Typeface.SERIF, Typeface.BOLD);

        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.span = columnNames.length;

        rowTitle.addView(title, params);
        return rowTitle;
    }


    public TableRow getColumnHeaders(){
        TableRow rowDayLabels = new TableRow(getActivity());

        for (int i = 0; i < columnNames.length; i++){
            TextView tv = getHeaderTextView(columnNames[i]);
            rowDayLabels.addView(tv);
        }

        return rowDayLabels;
    }

    protected TextView getHeaderTextView(String text){
        TextView textView = new TextView(getActivity());
        textView.setText(text);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        setMargins(textView, 1);
        textView.setPadding(5, 5, 5, 5);
        textView.setBackgroundColor(Color.GRAY);
        textView.setTypeface(Typeface.SERIF, Typeface.BOLD);
        return textView;
    }

    public void addTableBody(TableLayout table){
        Cursor cursor = mySQLiteHelper.executeQuery(this.buildTableQuery());
        int row = 0;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                TableRow tr = new TableRow(getActivity());
                tr.setGravity(Gravity.CENTER);
                for (int i = 0; i < columnNames.length; i++){
                    String value =  cursor.getString(cursor.getColumnIndex(columnNames[i]));
                    TextView tv = getCellTextView(value, row);
                    tr.addView(tv);
                }
                table.addView(tr);
                row++;
            } while (cursor.moveToNext());
        }
    }

    protected TextView getCellTextView(String text, int row){
        TextView textView = new TextView(getActivity());
        textView.setText(text);
        setMargins(textView, 1);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setBackgroundColor(row % 2 == 0 ? Color.LTGRAY : Color.WHITE);
        return textView;

    }

    // Add more params in future to provide flexibility
    public String buildTableQuery(){
        return "SELECT * FROM " + tableName;
    }

    public static void setMargins (View view, int margin) {
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, Gravity.RIGHT);
        params.setMargins(margin, margin, margin, margin);
        view.setLayoutParams(params);
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        // perform sync
        // showing refresh animation before making http call
        showSyncProgress(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SyncManager syncManager = SyncManager.getInstance(getActivity().getApplicationContext());
                    syncManager.retrieveUnsyncedRows();
                    Thread.sleep(2000);
                } catch (Exception e){
                    e.printStackTrace();
                }
                showSyncProgress(false);
            }
        }).start();
    }

    private void showSyncProgress(final boolean status){
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(status);
            }
        });
    }
}
