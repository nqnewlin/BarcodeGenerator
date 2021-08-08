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
import com.newlin.barcodegenerator.Upc;
import com.newlin.barcodegenerator.ui.barcodes.Departments;

import org.w3c.dom.Text;

import java.util.List;

import static android.content.ContentValues.TAG;

public class ScannedBarcodesAdapter extends RecyclerView.Adapter<ScannedBarcodesAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView deptTextView;
        public TextView countTextView;
        public TextView timeTextView;
        public TextView sourceTextView;
        public TextView deptHolderView;
        public ImageView screenSource;
        public ImageView cameraSource;
        private final Context context;


        public ViewHolder(View itemView) {
            super(itemView);

            deptTextView = (TextView) itemView.findViewById(R.id.scan_departments);
            timeTextView = (TextView) itemView.findViewById(R.id.scan_time);
            countTextView = (TextView) itemView.findViewById(R.id.scan_count);
            sourceTextView = (TextView) itemView.findViewById(R.id.scan_source);
            deptHolderView = (TextView) itemView.findViewById(R.id.department);

            screenSource = (ImageView) itemView.findViewById(R.id.source_screen);
            cameraSource = (ImageView) itemView.findViewById(R.id.source_camera);

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
            TextView deptView = holder.deptTextView;
            deptView.setText("Nothing Scanned");
        } else {
            TextView deptView = holder.deptTextView;
            deptView.setText(departments.getmScannedDepts());
            if (departments.getmScannedDepts().contentEquals("")) {
                TextView holderView = holder.deptHolderView;
                holderView.setVisibility(View.INVISIBLE);
            }
            TextView sourceView = holder.sourceTextView;
            sourceView.setText(departments.getmScanSource());

            TextView timeView = holder.timeTextView;
            timeView.setText(departments.getmScanTime());

            TextView countView = holder.countTextView;
            countView.setText(departments.getmScanCount());

            if (departments.getmScanSource().contentEquals("Screen Scanner")) {
                ImageView screenScan = holder.screenSource;
                sourceView.setText("Screen");
                screenScan.setVisibility(View.VISIBLE);
            } else if (departments.getmScanSource().contentEquals("Camera Scanner")) {
                ImageView cameraScan = holder.cameraSource;
                sourceView.setText("Camera");
                cameraScan.setVisibility(View.VISIBLE);
            }



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
    public int getItemCount() {
        if (mDepartments != null) {
            return mDepartments.size();
        }
        return 0;
    }

    public void removeItem(int position) {
        mDepartments.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Departments item, int position) {
        mDepartments.add(position, item);
        notifyItemInserted(position);
    }

    public List<Departments> getData() {
        return mDepartments;
    }
}
