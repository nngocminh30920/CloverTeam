package com.example.clover_shoes.control;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.clover_shoes.R;
import com.example.clover_shoes.adapter.SpinerSizeAdapter;
import com.example.clover_shoes.model.ProductSize;
import com.example.clover_shoes.util.Constant;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    String url = (Constant.KEY_LOCAL + Constant.KEY_PATH + "search.php");
    TextView txtName, txtProductName, txtPrice, txtPosition, txtQuantity;
    Spinner spnSize;
    ImageView img,imgBack,imgLogOut;
    Button btnScanAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        txtName = findViewById(R.id.textName);
        txtProductName = findViewById(R.id.txtproductName);
        txtPrice = findViewById(R.id.txtPrice);
        img = findViewById(R.id.productImage);
        txtPosition = findViewById(R.id.txtPosition);
        spnSize = findViewById(R.id.spnSize);
        txtQuantity = findViewById(R.id.txtQuantity);
        btnScanAgain = findViewById(R.id.btnScanAgain);
        imgBack = findViewById(R.id.imageBack);
        txtName.setText(LoginActivity.fullname);
        imgLogOut = findViewById(R.id.imageSignOut);
        Intent intent = getIntent();
        String proId = intent.getStringExtra("result");
        String result = proId.substring(0, proId.length() - 2) + "%";

        SearchProduct(url, result, proId);

        spnSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ProductSize productSize = (ProductSize) spnSize.getSelectedItem();
                txtQuantity.setText(productSize.getQuantity() + "");
                txtPosition.setText(productSize.getPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnScanAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
        imgLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SearchActivity.this, "Logging out....", Toast.LENGTH_SHORT).show();
                LoginActivity.accountId =0;
                LoginActivity.id = 0;
                LoginActivity.fullname = "";
                MainActivity.payments.clear();
                PaymentActivity.maxNumber = 0;
                PaymentActivity.txtTotal.setText("");
                Intent intent = new Intent(SearchActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void SearchProduct(String url, String result, String proId) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.d("abc",s);
                        if (s != null) {
                            try {
                                boolean check =false;
                                ArrayList<ProductSize> sizes = new ArrayList<>();
                                JSONArray jsonArray = new JSONArray(s);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String postion = "Shelve: " + jsonObject.getString("shelve") + " - "
                                            + "Row: " + jsonObject.getString("row") + " - "
                                            + "Position: " + jsonObject.getString("positionName");
                                    int size = Integer.parseInt(jsonObject.getString("Size"));
                                    int quantity = Integer.parseInt(jsonObject.getString("Quantity"));
                                    sizes.add(new ProductSize(size, postion, quantity));
                                    if (proId.equals(jsonObject.getString("idProduct"))) {
                                        check= true;
                                        Glide.with(getApplicationContext())
                                                .load("https://clovershoes123.000webhostapp.com/img/" + jsonObject.getString("ProductImage"))
                                                .into(img);
                                        txtProductName.setText(jsonObject.getString("ProductName"));
                                        long price = (long) jsonObject.getDouble("ProductPrice");
                                        Locale localeVN = new Locale("vi", "VN");
                                        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
                                        String str1 = currencyVN.format(price);
                                        txtPrice.setText(str1);
                                        sizes.remove(i);
                                        sizes.add(0, new ProductSize(size, postion, quantity));
                                    }
                                }
                                if(!check){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                                    builder.setMessage("ProductId is not existed in the stock! Please Scan Again!");
                                    builder.setTitle("Scanning Results");
                                    builder.setPositiveButton("Scan Again", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            scanCode();
                                        }
                                    });
                                    builder.setNegativeButton("Finish", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                                SpinerSizeAdapter spinerSizeAdapter = new SpinerSizeAdapter(SearchActivity.this, sizes);
                                spnSize.setAdapter(spinerSizeAdapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(SearchActivity.this, "Xảy ra lỗi", Toast.LENGTH_SHORT).show();
                        Log.d("AAA", "Lỗi:\n" + volleyError.toString());

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("ID", LoginActivity.id + "");
                params.put("productId", result);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void scanCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning code..");
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra("result", result.getContents());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}