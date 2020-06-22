package com.sec.datacheck.checkdata.view.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewAnimator;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.esri.arcgisruntime.data.CodedValue;
import com.esri.arcgisruntime.data.CodedValueDomain;
import com.sec.datacheck.R;
import com.sec.datacheck.checkdata.view.POJO.FieldModel;
import com.sec.datacheck.checkdata.view.activities.map.MapActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UpdateFragRecAdapter extends RecyclerView.Adapter<UpdateFragRecAdapter.viewHolder> {

    private static final String TAG = "UpdateFragRecAdapter";
    private ArrayList<FieldModel> data;
    private MapActivity mCurrent;
    private List<Integer> domainCheckPositions;

    public UpdateFragRecAdapter(ArrayList<FieldModel> data, MapActivity mCurrent) {
        this.data = data;
        this.mCurrent = mCurrent;
        this.domainCheckPositions = new ArrayList<>();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = null;
        try {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.update_frag_rec_row_item, parent, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        try {
            FieldModel field = data.get(position);
            if (field.getType() == 1) {
                holder.viewAnimator.setDisplayedChild(1);
                setDomainViews(holder, field, position);
                domainCheckPositions.add(position);

            } else if (field.getType() == 2) {
                holder.viewAnimator.setDisplayedChild(0);
                setTextFieldViews(holder, field, position);
            } else if (field.getType() == 3) {
                holder.viewAnimator.setDisplayedChild(2);
                setEditableDomainViews(holder, field, position);
                domainCheckPositions.add(position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Integer> getDomainCheckPositions() {
        return domainCheckPositions;
    }

    public ArrayList<FieldModel> getFields() {
        return data;
    }

    private void setTextFieldViews(viewHolder holder, FieldModel field, int position) {
        try {
            holder.textFieldTitle.setText(field.getAlias());
            holder.textFieldValue.setText(field.getTextValue());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initSpinner(Spinner spinner, FieldModel fieldModel) {
        try {
            ArrayList<String> typesList = new ArrayList<>();
            ArrayList<String> codeList = new ArrayList<>();

            CodedValueDomain typeDomain;
            List<CodedValue> codedValues;
            typeDomain = fieldModel.getChoiceDomain();
            codedValues = typeDomain.getCodedValues();

            for (CodedValue codedValue : codedValues) {
                typesList.add(codedValue.getName());
                codeList.add(codedValue.getCode().toString());
            }
            ArrayAdapter adapter = new ArrayAdapter<String>(mCurrent, android.R.layout.simple_spinner_dropdown_item, typesList);
            spinner.setAdapter(adapter);

            if (fieldModel.getSelectedDomainIndex() != null && (int) fieldModel.getSelectedDomainIndex() != 0) {
                spinner.setSelection(((Integer) fieldModel.getSelectedDomainIndex()) - 1);
            } else {
                spinner.setSelection(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDomainViews(viewHolder holder, FieldModel field, int position) {
        try {
//            holder.domainFieldTitle.setText(field.getTitle());
            initSpinner(holder.textFieldSpinner, field);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setEditableDomainViews(viewHolder holder, FieldModel field, int position) {
        try {
            holder.editableDomainTitle.setText(field.getTitle());
            initSpinner(holder.editableDomainSpinner, field);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder implements AdapterView.OnItemSelectedListener {

        @BindView(R.id.ufrri_view_animator)
        ViewAnimator viewAnimator;

        //type 1
        @BindView(R.id.ufrri_domain_field_title_lbl)
        TextView domainFieldTitle;

        @BindView(R.id.ufrri_text_field_spinner)
        Spinner textFieldSpinner;

        //type 2
        @BindView(R.id.text_field_container)
        ConstraintLayout textFieldContainer;//text field layout

        @BindView(R.id.ufrri_text_field_title_lbl)
        TextView textFieldTitle;

        @BindView(R.id.ufrri_text_field_value_lbl)
        TextView textFieldValue;

        //type 3
        @BindView(R.id.ufrri_domain_field_header_lbl)
        TextView editableDomainTitle;

        @BindView(R.id.ufrri_domain_field_value_lbl)
        Spinner editableDomainSpinner;

        viewHolder(@NonNull View itemView) {
            super(itemView);

            try {
                ButterKnife.bind(this, itemView);

                textFieldSpinner.setOnItemSelectedListener(this);
                editableDomainSpinner.setOnItemSelectedListener(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            try {
                Log.i(TAG, "onItemSelected: selected position = " + position);
                Log.i(TAG, "onItemSelected: adapter position = " + getAdapterPosition());
                data.get(getAdapterPosition()).setSelectedDomainIndex(position + 1);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
