package com.example.clover_shoes.control;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.clover_shoes.R;
import com.example.clover_shoes.model.Payment;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView txtFullName;
   ImageView searchview, paymentview, orderHistoryView;
    Boolean isScanClick =false;
    Boolean isPaymentClick = false;
    ImageView imgLogOut;
    public static ArrayList<Payment> payments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(payments !=null){

        }else{
            payments = new ArrayList<>();
        }
         txtFullName = findViewById(R.id.textName);
         searchview = findViewById(R.id.searchView);
         paymentview = findViewById(R.id.paymentView);
         orderHistoryView = findViewById(R.id.viewOrderHistory);
        txtFullName.setText(LoginActivity.fullname);
        imgLogOut = findViewById(R.id.imageSignOut);
        paymentview.setOnClickListener(view -> {
            scanCode();
            isScanClick = false;
            isPaymentClick = true;
        });
       searchview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode();
                isScanClick = true;
                isPaymentClick = false;
            }
        });
       orderHistoryView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(getApplicationContext(), ViewHistoryActivity.class);
              // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
               startActivity(intent);
           }
       });
       imgLogOut.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Toast.makeText(MainActivity.this, "Logging out....", Toast.LENGTH_SHORT).show();
               LoginActivity.accountId =0;
               LoginActivity.id = 0;
               LoginActivity.fullname = "";
               payments.clear();
               PaymentActivity.maxNumber = 0;
               PaymentActivity.txtTotal.setText("");
               Intent intent = new Intent(MainActivity.this, LoginActivity.class);
               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
               startActivity(intent);
           }
       });
    }
    private void scanCode(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning code..");
        integrator.initiateScan();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents() != null){
                if(isScanClick) {
                    Intent intent = new Intent(this, SearchActivity.class);
                    intent.putExtra("result", result.getContents());
                  //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                if (isPaymentClick){
                    Intent intent = new Intent(this, PaymentActivity.class);
                    intent.putExtra("result", result.getContents());
                  //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
            else {
                Toast.makeText(this, "No Results", Toast.LENGTH_LONG).show();
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}