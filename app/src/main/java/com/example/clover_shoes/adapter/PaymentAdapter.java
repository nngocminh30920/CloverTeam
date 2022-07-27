package com.example.clover_shoes.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.clover_shoes.R;
import com.example.clover_shoes.control.MainActivity;
import com.example.clover_shoes.control.PaymentActivity;
import com.example.clover_shoes.model.Payment;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class PaymentAdapter extends BaseAdapter {

    Context context;
    ArrayList<Payment> payments;

    public PaymentAdapter(Context context, ArrayList<Payment> payments) {
        this.context = context;
        this.payments = payments;
    }

    @Override
    public int getCount() {
        return payments.size();
    }

    @Override
    public Object getItem(int i) {
        return payments.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    public class ViewHolder{
        public TextView txtProductName,txtPrice,txtTotalPrice;
        public Button btnMinus, btnAdd;
        public EditText edtQuantity;
        public ImageView imageView;

    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(view == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.product_row,null);
            viewHolder.txtProductName =  view.findViewById(R.id.txtproName);
            viewHolder.txtPrice = view.findViewById(R.id.txtproPrice);
            viewHolder.txtTotalPrice =  view.findViewById(R.id.txtTotalPrice);
            viewHolder.btnMinus = view.findViewById(R.id.btnMinus);
            viewHolder.btnAdd =  view.findViewById(R.id.btnAdd);
            viewHolder.edtQuantity =  view.findViewById(R.id.edtQuantity);
            viewHolder.imageView =  view.findViewById(R.id.imageView);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        Payment payment = (Payment) getItem(i);
        long price = (long) payment.getPrice();
        long totalPrice = (long) payment.getTotalPrice();
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
        String str1 = currencyVN.format(price);
        String str2 = currencyVN.format(totalPrice);
        viewHolder.txtProductName.setText(payment.getName());
        viewHolder.txtPrice.setText(str1);
        viewHolder.edtQuantity.setText(payment.getQuantity()+"");
        viewHolder.txtTotalPrice.setText(str2);
        Glide.with(context)
                .load("https://clovershoes123.000webhostapp.com/img/" + payment.getImg())
                .into(viewHolder.imageView);
        int number = Integer.parseInt(viewHolder.edtQuantity.getText().toString());
        if(number == PaymentActivity.maxNumber){
            viewHolder.btnAdd.setVisibility(View.INVISIBLE);
            viewHolder.edtQuantity.setEnabled(false);
        }else if(number ==0){
            MainActivity.payments.remove(i);
        }else{
            viewHolder.btnAdd.setVisibility(View.VISIBLE);
            viewHolder.edtQuantity.setEnabled(true);
        }
        ViewHolder finalViewHolder = viewHolder;
        viewHolder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newNumber = Integer.parseInt(finalViewHolder.edtQuantity.getText().toString().trim()) + 1;
                finalViewHolder.edtQuantity.setText(newNumber+"");
                MainActivity.payments.get(i).setQuantity(newNumber);
                if(newNumber > PaymentActivity.maxNumber){
                    finalViewHolder.btnAdd.setVisibility(View.INVISIBLE);
                    finalViewHolder.edtQuantity.setEnabled(false);
                }else{
                    finalViewHolder.btnAdd.setVisibility(View.VISIBLE);
                    finalViewHolder.edtQuantity.setEnabled(true);
                    MainActivity.payments.get(i).setQuantity(newNumber);
                    PaymentActivity.countTotal();
                }
                notifyDataSetChanged();
            }
        });
        viewHolder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newNumber = Integer.parseInt(finalViewHolder.edtQuantity.getText().toString().trim()) - 1;
                finalViewHolder.edtQuantity.setText(newNumber+"");
                if(newNumber==0){
                    MainActivity.payments.remove(i);
                    if(MainActivity.payments.size() <=0){
                        Intent intent = new Intent(context,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                    }
                }else{
                    MainActivity.payments.get(i).setQuantity(newNumber);
                }
                PaymentActivity.countTotal();
                notifyDataSetChanged();
            }
        });
        viewHolder.edtQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    int newNumber = Integer.parseInt(finalViewHolder.edtQuantity.getText().toString().trim());
                    finalViewHolder.edtQuantity.setText(newNumber + "");
                    MainActivity.payments.get(i).setQuantity(newNumber);
                    if (newNumber > PaymentActivity.maxNumber) {
                        finalViewHolder.btnAdd.setVisibility(View.INVISIBLE);
                        finalViewHolder.edtQuantity.setEnabled(false);
                    } else if (newNumber == 0) {
                        MainActivity.payments.remove(i);
                        if(MainActivity.payments.size() <=0){
                            Intent intent = new Intent(context,MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                        }
                    } else {
                        finalViewHolder.btnAdd.setVisibility(View.VISIBLE);
                        finalViewHolder.edtQuantity.setEnabled(true);
                        MainActivity.payments.get(i).setQuantity(newNumber);
                    }
                    PaymentActivity.countTotal();
                    notifyDataSetChanged();
                }
            }
        });
        return view;
    }
}
