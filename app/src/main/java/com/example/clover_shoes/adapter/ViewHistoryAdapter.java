package com.example.clover_shoes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clover_shoes.R;
import com.example.clover_shoes.model.Order;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ViewHistoryAdapter extends RecyclerView.Adapter<ViewHistoryAdapter.MyViewHolder> {
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    Context context;
    ArrayList<Order> orders;

    public ViewHistoryAdapter(Context context, ArrayList<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.txtTime.setText(order.getTime());
        long totalPrice = (long) order.getTotalPrice();
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
        String str1 = currencyVN.format(totalPrice);
        holder.txtTotal.setText(str1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(holder.recyclerViewRow.getContext(),
                LinearLayoutManager.VERTICAL,false);
        if(order.getList()!=null){
            linearLayoutManager.setInitialPrefetchItemCount(order.getList().size());
        }


        //OrderDetails Adapter
        OrderDetailAdapter orderDetailAdapter = new OrderDetailAdapter(order.getList());
        holder.recyclerViewRow.setLayoutManager(linearLayoutManager);
        holder.recyclerViewRow.setAdapter(orderDetailAdapter);
        holder.recyclerViewRow.setRecycledViewPool(viewPool);

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtTime,txtTotal;
        RecyclerView recyclerViewRow;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtTotal = itemView.findViewById(R.id.txtTotal);
            recyclerViewRow = itemView.findViewById(R.id.recycleViewRow);
        }
    }
}
