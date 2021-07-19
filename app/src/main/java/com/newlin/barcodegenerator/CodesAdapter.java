package com.newlin.barcodegenerator;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CodesAdapter extends RecyclerView.Adapter<CodesAdapter.ViewHolder> {
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public Button messageButton;
        public ImageView codeImageView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.item_name);
            codeImageView = (ImageView) itemView.findViewById(R.id.item_image);
        }
    }

    private List<Upc> mUpcs;

    public CodesAdapter(List<Upc> upcs) {
        mUpcs = upcs;
    }

    @Override
    public CodesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View upcView = inflater.inflate(R.layout.item_codes, parent, false);

        ViewHolder viewHolder = new ViewHolder(upcView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CodesAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        Upc upc = mUpcs.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.nameTextView;
        textView.setText(upc.getmItemCode());
        ImageView imageView = holder.codeImageView;
        imageView.setImageBitmap(upc.getImage());
    }

    @Override
    public int getItemCount() {
        return mUpcs.size();
    }


}