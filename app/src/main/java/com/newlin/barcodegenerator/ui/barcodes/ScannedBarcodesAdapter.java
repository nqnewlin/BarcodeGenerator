package com.newlin.barcodegenerator.ui.barcodes;

import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.newlin.barcodegenerator.DisplayBarcodes;
import com.newlin.barcodegenerator.R;
import com.newlin.barcodegenerator.ui.barcodes.Departments;

import org.w3c.dom.Text;

import java.util.List;

import static android.content.ContentValues.TAG;

public class ScannedBarcodesAdapter extends RecyclerView.Adapter<ScannedBarcodesAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView deptTextView;
        public TextView countTextView;
        public TextView deptNumberTextView;
        public TextView scannedTextView;
        private final Context context;


        public ViewHolder(View itemView) {
            super(itemView);
            //TODO fix after implementing count scanned
            //deptTextView = (TextView) itemView.findViewById(R.id.dept_name);

            //TODO fix after implementing method to count scanned
            countTextView = (TextView) itemView.findViewById(R.id.count_number);

            deptNumberTextView = (TextView) itemView.findViewById(R.id.dept_number);
            //scannedTextView = (TextView) itemView.findViewById(R.id.scanned);
            context = itemView.getContext();

        }
    }

    private List<Departments> mDepartments;

    public ScannedBarcodesAdapter(List<Departments> departments) { mDepartments = departments; }

    @Override
    public ScannedBarcodesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View scannedView = inflater.inflate(R.layout.scanned_departments, parent, false);

        ViewHolder viewHolder = new ViewHolder(scannedView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ScannedBarcodesAdapter.ViewHolder holder, int position) {
        Departments departments = mDepartments.get(position);

        if (departments.getmScanId().matches("0")) {
            TextView numberView = holder.deptNumberTextView;
            numberView.setText("Nothing Scanned");
        } else {
            TextView numberView = holder.deptNumberTextView;
            numberView.setText(departments.getmScanTime());

            //TODO implement method to capture scan counts
            //TextView textView = holder.deptTextView;
            //textView.setText(departments.getmScannedDepts());
            TextView countTextView = holder.countTextView;
            countTextView.setText(departments.getmScannedDepts());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(v.getContext(), DisplayBarcodes.class);
                    int position = holder.getAdapterPosition();
                    String dept_number = departments.getmScanId();
                    intent.putExtra("EXTRA_DEPT_NUMBER", dept_number);
                    String toast = departments.getmScanTime();
                    Toast.makeText(v.getContext(), toast, Toast.LENGTH_SHORT).show();
                    v.getContext().startActivity(intent);
                }

            });
        }



    }

    @Override
    public int getItemCount() { return mDepartments.size(); }
}
