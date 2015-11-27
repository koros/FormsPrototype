package com.korosmatick.formsprototype.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.korosmatick.formsprototype.R;
import com.korosmatick.formsprototype.adapter.FormsListAdapter;
import com.korosmatick.formsprototype.database.MySQLiteHelper;
import com.korosmatick.formsprototype.model.Form;
import com.korosmatick.formsprototype.util.FormsHttpService;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * Created by koros on 11/23/15.
 */
public class HomeActivity extends ActionBarActivity {

    @Bind(R.id.listView)
    ListView listView;

    FormsListAdapter adapter;
    List<Form> items = new ArrayList<Form>();
    MySQLiteHelper mySQLiteHelper = null;
    String formsEndpoint = "http://10.20.20.13:8080/sample/rest/forms";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        ButterKnife.bind(this);
        mySQLiteHelper = MySQLiteHelper.getInstance(getApplicationContext());
        adapter = new FormsListAdapter(this, R.layout.list_view_item, items);
        listView.setAdapter(adapter);
        reloadListView();
        new FetchFormsTask().execute();
    }

    public void reloadListView(){
        List<Form> queryItems = mySQLiteHelper.retrieveAllForms();
        if (queryItems != null){
            items.clear();
            items.addAll(queryItems);
            listView.post(new Runnable(){
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    private class FetchFormsTask extends AsyncTask<String, Void, List<Form>> {

        @Override
        protected List<Form> doInBackground(String... params) {
            FormsHttpService formsHttpService = new FormsHttpService(getApplicationContext());
            List<Form> forms = formsHttpService.getAllForms(formsEndpoint);
            return forms;
        }

        @Override
        protected void onPostExecute(List<Form> result) {
            if (result != null){
                MySQLiteHelper mySQLiteHelper = MySQLiteHelper.getInstance(getApplicationContext());
                for (Form form : result){
                    mySQLiteHelper.saveForm(form);
                    reloadListView();
                }
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnItemClick(R.id.listView) void onItemClick(int position) {
        try{
            Form form = adapter.getItem(position);
            if (form != null){
                Intent intent = new Intent(this, FormDataActivity.class);
                Bundle bundle = new Bundle();
                bundle.putLong("id", form.getId());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
