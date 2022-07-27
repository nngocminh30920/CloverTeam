package com.example.clover_shoes.control;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.clover_shoes.R;
import com.example.clover_shoes.adapter.ViewHistoryAdapter;
import com.example.clover_shoes.model.Order;
import com.example.clover_shoes.model.OrderDetail;
import com.example.clover_shoes.util.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewHistoryActivity extends AppCompatActivity {
    RecyclerView orderHistoryView;
    ArrayList<OrderDetail> orderDetails;
    ArrayList<Order> orders = new ArrayList<>();;
    TextView txtName;
    ImageView imgBack;
    ImageView imgLogOut;
    String orderUrl = (Constant.KEY_LOCAL + Constant.KEY_PATH + "getOrder.php");
    String orderDetailUrl = (Constant.KEY_LOCAL + Constant.KEY_PATH + "getOrderDetails.php");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        orderHistoryView = findViewById(R.id.orderHistoryView);
        txtName = findViewById(R.id.textName);
        imgBack = findViewById(R.id.imageBack);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        orderHistoryView.setLayoutManager(linearLayoutManager);
        imgLogOut = findViewById(R.id.imageSignOut);
        txtName.setText(LoginActivity.fullname);
        viewOrderHistory();
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
        imgLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ViewHistoryActivity.this, "Logging out....", Toast.LENGTH_SHORT).show();
                LoginActivity.accountId =0;
                LoginActivity.id = 0;
                LoginActivity.fullname = "";
                MainActivity.payments.clear();
                PaymentActivity.maxNumber = 0;
                PaymentActivity.txtTotal.setText("");
                Intent intent = new Intent(ViewHistoryActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void viewOrderHistory() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, orderUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                if (jsonArray.length() > 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject  jsonObject = jsonArray.getJSONObject(i);
                                        int id = jsonObject.getInt("idOrder");
                                        orders.add(new Order(id,jsonObject.getString("Time"),
                                                                        jsonObject.getInt("TotalPrice")));

//                                        RequestQueue queue = Volley.newRequestQueue(ViewHistoryActivity.this);
//                                        StringRequest request = new StringRequest(Request.Method.POST, orderDetailUrl,
//                                                new Response.Listener<String>() {
//                                                    @Override
//                                                    public void onResponse(String s) {
//                                                        if (s != null) {;
//                                                            Log.d("ab", id+ "");
//                                                            try {
//                                                                JSONArray array = new JSONArray(s);
//                                                                orderDetails = new ArrayList<>();
//                                                                for (int j = 0; j < array.length(); j++) {
//                                                                    JSONObject jsonObject1 = array.getJSONObject(j);
//                                                                    orderDetails.add(new OrderDetail(jsonObject1.getString("ProductName"),
//                                                                            jsonObject1.getInt("Quantity"), jsonObject1.getInt("ProductPrice"),
//                                                                            jsonObject1.getInt("TotalPrice")));
//                                                                }
//                                                                orders.add(new Order(jsonObject.getString("Time"),
//                                                                        jsonObject.getInt("TotalPrice"), orderDetails));
//                                                                ViewHistoryAdapter viewHistoryAdapter = new ViewHistoryAdapter(ViewHistoryActivity.this, orders);
//                                                                orderHistoryView.setAdapter(viewHistoryAdapter);
//                                                                viewHistoryAdapter.notifyDataSetChanged();
//                                                            } catch (JSONException e) {
//                                                                e.printStackTrace();
//                                                            }
//
//                                                        }
//                                                    }
//                                                }, new Response.ErrorListener() {
//                                            @Override
//                                            public void onErrorResponse(VolleyError error) {
//                                                Toast.makeText(ViewHistoryActivity.this, "Something wrong is happening", Toast.LENGTH_SHORT).show();
//                                                Log.d("AAA", "Error:\n" + error.toString());
//
//                                            }
//                                        }) {
//                                            @Override
//                                            protected Map<String, String> getParams() throws AuthFailureError {
//                                                Map<String, String> params = new HashMap<>();
//                                                params.put("idOrder", id + "");
//                                                return params;
//                                            }
//                                        };
//                                        queue.add(request);
                                   }
                                    for (Order o:orders
                                         ) {
                                         RequestQueue queue = Volley.newRequestQueue(ViewHistoryActivity.this);
                                        StringRequest request = new StringRequest(Request.Method.POST, orderDetailUrl,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String s) {
                                                        if (s != null) {;

                                                            try {
                                                                JSONArray array = new JSONArray(s);
                                                                orderDetails = new ArrayList<>();
                                                                for (int j = 0; j < array.length(); j++) {
                                                                    JSONObject jsonObject1 = array.getJSONObject(j);
                                                                    orderDetails.add(new OrderDetail(jsonObject1.getString("ProductName"),
                                                                            jsonObject1.getInt("Quantity"), jsonObject1.getInt("ProductPrice"),
                                                                            jsonObject1.getInt("TotalPrice")));
                                                                }
                                                                o.setList(orderDetails);
                                                                ViewHistoryAdapter viewHistoryAdapter = new ViewHistoryAdapter(ViewHistoryActivity.this, orders);
                                                                orderHistoryView.setAdapter(viewHistoryAdapter);
                                                                viewHistoryAdapter.notifyDataSetChanged();
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }

                                                        }
                                                    }
                                                }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(ViewHistoryActivity.this, "Something wrong is happening", Toast.LENGTH_SHORT).show();
                                                Log.d("AAA", "Error:\n" + error.toString());

                                            }
                                        }) {
                                            @Override
                                            protected Map<String, String> getParams() throws AuthFailureError {
                                                Map<String, String> params = new HashMap<>();
                                                params.put("idOrder", o.getId()+"");
                                                return params;
                                            }
                                        };
                                        queue.add(request);
                                    }
                                   }

                                } catch(JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ViewHistoryActivity.this, "Something wrong is happening", Toast.LENGTH_SHORT).show();
                Log.d("AAA", "Error:\n" + error.toString());

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("idBranch", LoginActivity.id + "");
                params.put("idAccount", LoginActivity.accountId + "");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}