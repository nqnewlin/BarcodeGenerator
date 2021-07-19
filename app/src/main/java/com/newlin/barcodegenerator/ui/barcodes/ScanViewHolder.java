package com.newlin.barcodegenerator.ui.barcodes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.newlin.barcodegenerator.R;

import androidx.recyclerview.widget.RecyclerView;

public class ScanViewHolder extends RecyclerView.ViewHolder {
    private final TextView scanItemView;

    private ScanViewHolder(View itemView) {
        super(itemView);
        scanItemView = itemView.findViewById(R.id.dept_number);
    }

    public void bind(String text) {
        scanItemView.setText(text);
    }

    static ScanViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_barcodes, parent, false);
        return new ScanViewHolder(view);
    }
}
