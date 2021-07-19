package com.newlin.barcodegenerator.ui.barcodes;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import Database.DepartmentInfo;

public class ScanListAdapter extends ListAdapter<DepartmentInfo, ScanViewHolder> {
    public ScanListAdapter(@NonNull DiffUtil.ItemCallback<DepartmentInfo> diffCallback) {
        super(diffCallback);
    }

    @Override
    public ScanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ScanViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(ScanViewHolder holder, int position) {
        DepartmentInfo current = getItem(position);
        holder.bind("test");
    }

    static class ScanDiff extends DiffUtil.ItemCallback<DepartmentInfo> {
        @Override
        public boolean areItemsTheSame(@NonNull DepartmentInfo oldItem, @NonNull DepartmentInfo newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull DepartmentInfo oldItem, @NonNull DepartmentInfo newItem) {
            return oldItem.getScanTime().equals(newItem.getScanTime());
        }
    }

}
