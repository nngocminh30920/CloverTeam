package com.example.clover_shoes.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.example.clover_shoes.R;
import com.example.clover_shoes.model.Branch;
import com.example.clover_shoes.model.ProductSize;

import java.util.ArrayList;

public class SpinerSizeAdapter implements SpinnerAdapter {
    private Context context;
    private ArrayList<ProductSize> productSizes;

    public SpinerSizeAdapter(Context context, ArrayList<ProductSize> productSizes) {
        this.context = context;
        this.productSizes = productSizes;
    }

    @Override
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.row, viewGroup, false);
        TextView txtSize ;
        txtSize = view.findViewById(R.id.txtView);
        txtSize.setText(productSizes.get(i).getSize()+"");
        return view;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return productSizes.size();
    }

    @Override
    public Object getItem(int i) {
        return productSizes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return productSizes.get(i).getSize();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.row, viewGroup, false);
        TextView txtSize ;
        txtSize = view.findViewById(R.id.txtView);
        txtSize.setText(productSizes.get(i).getSize()+"");
        return view;
    }

    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
