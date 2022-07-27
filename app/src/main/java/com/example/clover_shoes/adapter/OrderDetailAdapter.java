package com.example.clover_shoes.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clover_shoes.R;
import com.example.clover_shoes.model.OrderDetail;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.MyViewHolder> {
    ArrayList<OrderDetail> orderDetails;

    public OrderDetailAdapter(ArrayList<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        OrderDetail orderDetail = orderDetails.get(position);
        holder.txtProductName.setText(orderDetail.getProductName());
        long price = (long) orderDetail.getPrice();
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
        String str1 = currencyVN.format(price);
        holder.txtPrice.setText(str1);
        holder.txtQuantity.setText(orderDetail.getQuantity()+"");
        holder.txtTotalPrice.setText(orderDetail.getTotalPrice()+"");
    }

    @Override
    public int getItemCount() {
        if(orderDetails==null){
            return 0;}
        else{ return orderDetails.size();

        }

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductName, txtPrice, txtQuantity,txtTotalPrice;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.txtproName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtTotalPrice = itemView.findViewById(R.id.txtTotal);
        }
    }
}
