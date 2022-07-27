package com.example.clover_shoes.control;

import android.Manifest;
import android.app.AlertDialog;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.clover_shoes.BuildConfig;
import com.example.clover_shoes.R;
import com.example.clover_shoes.adapter.PaymentAdapter;
import com.example.clover_shoes.model.Payment;
import com.example.clover_shoes.util.Constant;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class PaymentActivity extends AppCompatActivity {
    static TextView txtTotal;
    public static int maxNumber;
    Button btnPay, btnScan;
    PaymentAdapter paymentAdapter;
    ListView lvPayment;
    TextView txtName;
    ImageView imgBack,imgLogOut;
    String url = (Constant.KEY_LOCAL + Constant.KEY_PATH + "search.php");
    String orderUrl = (Constant.KEY_LOCAL + Constant.KEY_PATH +"createOrder.php");
    String orderDetailUrl = (Constant.KEY_LOCAL + Constant.KEY_PATH +"OrderDetails.php");
    String updateProductUrl= (Constant.KEY_LOCAL + Constant.KEY_PATH +"updateProduct.php");
    String deleteProductUrl = (Constant.KEY_LOCAL + Constant.KEY_PATH +"deleteProduct.php");
    int pageWidth = 1200;
    Date dateObj;
    DateFormat dateFormat;
    String path;
    String fileName = "Order";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        lvPayment = (ListView) findViewById(R.id.lvPayment);
        txtTotal = (TextView) findViewById(R.id.txtTotal);
        btnPay = (Button) findViewById(R.id.btnpayment);
        btnScan = (Button) findViewById(R.id.btnScan);
        txtName = findViewById(R.id.txtUsertName);
        imgBack = findViewById(R.id.imageBack);
        imgLogOut = findViewById(R.id.imageSignOut);
        Intent intent = getIntent();
        String proId = intent.getStringExtra("result");
        txtName.setText(LoginActivity.fullname);
        paymentEvent();
        ScanProduct(url, proId);
        btnScan.setOnClickListener(new View.OnClickListener() {
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
                Toast.makeText(PaymentActivity.this, "Logging out....", Toast.LENGTH_SHORT).show();
                LoginActivity.accountId =0;
                LoginActivity.id = 0;
                LoginActivity.fullname = "";
                MainActivity.payments.clear();
                PaymentActivity.maxNumber = 0;
                PaymentActivity.txtTotal.setText("");
                Intent intent = new Intent(PaymentActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        if(Build.VERSION.SDK_INT >= 23){
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ){

            }else{
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},110);
            }
        }
    }

    public static void countTotal() {
        int total1 = 0;
        for (int i = 0; i < MainActivity.payments.size(); i++) {
            total1 += MainActivity.payments.get(i).getTotalPrice();
        }
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
        String str1 = currencyVN.format(total1);
        txtTotal.setText(str1);
    }

    private void ScanProduct(String url, String result) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (s != null) {
                            try {
                                JSONArray jsonArray = new JSONArray(s);
                                if (jsonArray.length() == 0) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
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
                                } else {
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    maxNumber = jsonObject.getInt("Quantity");
                                    boolean check = false;
                                    if (MainActivity.payments.size() > 0) {
                                        for (int i = 0; i < MainActivity.payments.size(); i++) {
                                            if (MainActivity.payments.get(i).getId().equals(jsonObject.getString("idProduct"))) {
                                                MainActivity.payments.get(i).setQuantity(MainActivity.payments.get(i).getQuantity() + 1);
                                                check = true;
                                            }
                                        }
                                        if (!check) {
                                            MainActivity.payments.add(new Payment(jsonObject.getString("idProduct"),
                                                    jsonObject.getString("ProductName") + " Size " + jsonObject.getString("Size"),
                                                    1,
                                                    (float) jsonObject.getDouble("ProductPrice"), jsonObject.getString("ProductImage")));
                                        }
                                    } else {
                                        MainActivity.payments.add(new Payment(jsonObject.getString("idProduct"),
                                                jsonObject.getString("ProductName") + " Size " + jsonObject.getString("Size"),
                                                1,
                                                (float) jsonObject.getDouble("ProductPrice"), jsonObject.getString("ProductImage")));
                                    }
                                    paymentAdapter = new PaymentAdapter(PaymentActivity.this, MainActivity.payments);
                                    lvPayment.setAdapter(paymentAdapter);
                                    countTotal();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(PaymentActivity.this, "Xảy ra lỗi", Toast.LENGTH_SHORT).show();
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

    private void paymentEvent() {
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               createPDF();
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                StringRequest stringRequest = new StringRequest(Request.Method.POST, orderUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String Orderid) {
                                if (Integer.parseInt(Orderid) > 0) {
                                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                                    StringRequest request = new StringRequest(Request.Method.POST, orderDetailUrl,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String s) {
                                                    if (s.equals("Success")) {
                                                        MainActivity.payments.clear();
                                                        Toast.makeText(getApplicationContext(), "Payment Successfully!", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError volleyError) {
                                                    Toast.makeText(getApplicationContext(), volleyError.toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            }) {
                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            JSONArray jsonArray = new JSONArray();
                                            for (int i = 0; i < MainActivity.payments.size(); i++) {
                                                JSONObject jsonObject = new JSONObject();
                                                try {
                                                    jsonObject.put("OrderId", Integer.parseInt(Orderid));
                                                    jsonObject.put("ProId", MainActivity.payments.get(i).getId());
                                                    jsonObject.put("Quantity", MainActivity.payments.get(i).getQuantity());
                                                    jsonObject.put("Price", MainActivity.payments.get(i).getTotalPrice());
                                                    RequestQueue queue1 = Volley.newRequestQueue(getApplicationContext());
                                                    int finalI = i;
                                                    StringRequest request1 = new StringRequest(Request.Method.POST, updateProductUrl,
                                                            new Response.Listener<String>() {
                                                                @Override
                                                                public void onResponse(String s) {
                                                                    if(s.equals("Success")){
                                                                        RequestQueue queue2 = Volley.newRequestQueue(getApplicationContext());
                                                                        StringRequest request2 = new StringRequest(Request.Method.POST, deleteProductUrl,
                                                                                new Response.Listener<String>() {
                                                                                    @Override
                                                                                    public void onResponse(String response) {

                                                                                    }
                                                                                }, new Response.ErrorListener() {
                                                                            @Override
                                                                            public void onErrorResponse(VolleyError error) {

                                                                            }
                                                                        }){ @Override
                                                                        protected Map<String, String> getParams() throws AuthFailureError {
                                                                            Map<String, String> params = new HashMap<>();
                                                                            try {
                                                                                jsonObject.put("BranchId",LoginActivity.id+"");
                                                                            } catch (JSONException e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                            params.put("JSON", jsonArray.toString());
                                                                            return params;
                                                                        }};
                                                                        queue2.add(request2);
                                                                    }
                                                                }
                                                            },
                                                            new Response.ErrorListener() {
                                                                @Override
                                                                public void onErrorResponse(VolleyError volleyError) {

                                                                }
                                                            }) {
                                                        @Override
                                                        protected Map<String, String> getParams() throws AuthFailureError {
                                                            Map<String, String> params = new HashMap<>();
                                                            params.put("ID", MainActivity.payments.get(finalI).getId());
                                                            params.put("idBranch", LoginActivity.id+ "");
                                                            params.put("number", MainActivity.payments.get(finalI).getQuantity() + "");
                                                            return params;
                                                        }
                                                    };
                                                    queue1.add(request1);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                jsonArray.put(jsonObject);
                                            }
                                            Map<String, String> params = new HashMap<>();
                                            params.put("JSON", jsonArray.toString());
                                            Log.d("JSONArray", jsonArray.toString());
                                            return params;
                                        }
                                    };
                                    queue.add(request);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Toast.makeText(PaymentActivity.this, "Xảy ra lỗi", Toast.LENGTH_SHORT).show();
                                Log.d("AAA", "Lỗi:\n" + volleyError.toString());

                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        Date date = new Date();
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        String strDate = formatter.format(date);
                        String s = txtTotal.getText().toString().trim();
                        String totals = s.substring(0, s.length()-2).replaceAll("\\.","");
                        int total = Integer.parseInt(totals);
                        params.put("idAccount", LoginActivity.accountId+"");
                        params.put("idBranch", LoginActivity.id+"");
                        params.put("Time", strDate);
                        params.put("TotalPrice", total+"");
                        return params;
                    }
                };
                requestQueue.add(stringRequest);
           }
        });

    }

    private void scanCode() {
        IntentIntegrator integrator = new IntentIntegrator(PaymentActivity.this);
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
            if (result.getContents() != null) {
                Intent intent = new Intent(this, PaymentActivity.class);
                intent.putExtra("result", result.getContents());
                intent.putExtra("fullname", txtName.getText().toString().trim());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Toast.makeText(this, "No Results", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void createPDF(){
        dateObj = new Date();
        Paint myPaint = new Paint();
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        myPaint.setTextAlign(Paint.Align.CENTER);
        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        myPaint.setTextSize(70);
        canvas.drawText("Welcome to Clover Shoes!",pageWidth/2,270,myPaint);
        canvas.drawText("Invoice",pageWidth/2,500,myPaint);

        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(35f);
        canvas.drawText("Staff: " + txtName.getText().toString().trim(),20,590,myPaint);

        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        myPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Date: "+ dateFormat.format(dateObj),pageWidth - 20 , 640, myPaint);

        dateFormat = new SimpleDateFormat("hh:mm:ss");
        canvas.drawText("Time: "+ dateFormat.format(dateObj),pageWidth - 20 , 690, myPaint);

        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(2);
        canvas.drawRect(20,780,pageWidth-20,860,myPaint);

        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setStyle(Paint.Style.FILL);
        canvas.drawText("No.", 40,830,myPaint);
        canvas.drawText("Product Name", 200,830,myPaint);
        canvas.drawText("Price", 700,830,myPaint);
        canvas.drawText("Qty.", 900,830,myPaint);
        canvas.drawText("Total", 1050,830,myPaint);

        canvas.drawLine(180,790,180,840, myPaint);
        canvas.drawLine(680,790,680,840, myPaint);
        canvas.drawLine(880,790,880,840, myPaint);
        canvas.drawLine(1030,790,1030,840, myPaint);

        int y = 850;
        for (int i = 0; i < MainActivity.payments.size(); i++) {
            y+=100;
            canvas.drawText((i+1)+"", 40,y, myPaint);
            canvas.drawText(MainActivity.payments.get(i).getName(), 200,y, myPaint);
            Locale localeVN = new Locale("vi", "VN");
            NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
            canvas.drawText( currencyVN.format(MainActivity.payments.get(i).getPrice()), 700,y, myPaint);
            canvas.drawText(MainActivity.payments.get(i).getQuantity()+"", 900,y, myPaint);
            canvas.drawText(currencyVN.format(MainActivity.payments.get(i).getTotalPrice()), 1030,y, myPaint);
        }
        canvas.drawLine(20,1200,pageWidth-20,1200,myPaint);

        myPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Sub Total: " + txtTotal.getText().toString(),pageWidth-40,1250,myPaint);

        myPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("======Thanks for visit======",pageWidth/2,1500,myPaint);
        document.finishPage(page);
        File filePath = new File(Environment.getExternalStorageDirectory(),"/Download/"+fileName + System.currentTimeMillis() + ".pdf");
        try{
            document.writeTo(new FileOutputStream(filePath));
            Toast.makeText(this, "Download PDF success", Toast.LENGTH_SHORT).show();
        }catch (IOException e){
            e.printStackTrace();
        }
        document.close();
    }
}