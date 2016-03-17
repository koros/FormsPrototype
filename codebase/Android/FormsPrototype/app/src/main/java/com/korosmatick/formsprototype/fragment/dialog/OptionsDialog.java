package com.korosmatick.formsprototype.fragment.dialog;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.widget.AdapterView.OnItemClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.korosmatick.formsprototype.R;
import com.korosmatick.formsprototype.activity.FormDataActivity;
import com.korosmatick.formsprototype.adapter.OptionsAdapter;
import com.korosmatick.formsprototype.model.Form;
import com.korosmatick.formsprototype.model.OptionsItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by koros on 3/6/16.
 */
public class OptionsDialog extends DialogFragment implements OnItemClickListener {

    public static final String TAG = "OptionsDialog";
    private ListView list = null;
    private OptionsAdapter listAdapter = null;
    private String title = "EDIT OPTIONS";

    List<Form> childForms = new ArrayList<Form>();
    Form baseForm;
    Long clickedFormId;

    public OptionsDialog() {
    }

    public static OptionsDialog newInstance(List<Form> childForms, Form baseForm, Long clickedFormId) {
        OptionsDialog optionsDialog = new OptionsDialog();
        optionsDialog.baseForm = baseForm;
        optionsDialog.childForms = childForms;
        optionsDialog.clickedFormId = clickedFormId;
        return optionsDialog;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list, container);
        list = (ListView) view.findViewById(R.id.listView);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        ViewGroup header = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.dialog_header_view, list, false);
        TextView titleView = (TextView) header.findViewById(R.id.header);
        titleView.setText(title);
        //titleView.setTextColor(getResources().getColor(R.color.blue_dialog));
        list.addHeaderView(header, null, false);

        listAdapter = new OptionsAdapter(getActivity(), R.layout.list_view_item, getOptions());
        list.setAdapter(listAdapter);
        list.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final OptionsItem item = (OptionsItem) parent.getItemAtPosition(position);
        Long formId = item.getFormId();
        ((FormDataActivity)getActivity()).showForm(formId, clickedFormId);
        closeDialog();
    }

    protected void closeDialog() {
        this.dismiss();
    }

    private List<OptionsItem> getOptions() {
        List<OptionsItem> list = new ArrayList<OptionsItem>();

        for (Form form : childForms){
            OptionsItem item = new OptionsItem();
            String display = form.getFormName().equalsIgnoreCase(baseForm.getFormName()) ? "Edit Row" : form.getFormName();
            item.setDisplayName(display);
            item.setFormName(form.getFormName());
            item.setFormId(form.getId());
            list.add(item);
        }

        return list;
    }

}