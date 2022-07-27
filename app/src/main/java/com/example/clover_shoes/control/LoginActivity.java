package com.example.clover_shoes.control;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.clover_shoes.R;
import com.example.clover_shoes.adapter.MySpinnerAđapter;
import com.example.clover_shoes.model.Branch;
import com.example.clover_shoes.util.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    String url = (Constant.KEY_LOCAL+Constant.KEY_PATH+"login.php");
    String loadBranchUrl = (Constant.KEY_LOCAL+Constant.KEY_PATH+"getBranch.php");
    EditText edtUserName , edtPassword;
    Button btnLogin;
    ProgressBar progressBar;
    Spinner spinner;
    static int id;
    static int accountId;
    static String fullname = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        edtUserName = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPasswword);
        btnLogin = findViewById(R.id.buttonLogin);
        progressBar = findViewById(R.id.progessBar);
        spinner = findViewById(R.id.spnBranch);
        setSpinnerValues(loadBranchUrl);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Validate()){
                   getUsers(url);
                }else{
                    loading(false);
                }
            }
        });
    }

    private void setSpinnerValues(String url){
        ArrayList<Branch> branches = new ArrayList<>();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if(s != null){
                            try {
                                branches.add(new Branch(-1,"-Select your working branch-"));
                                JSONArray jsonArray = new JSONArray(s);
                                for (int i = 0; i <jsonArray.length() ; i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                  Branch branch = new Branch(jsonObject.getInt("idBranch"),jsonObject.getString("BranchName"));
                                  branches.add(branch);
                                }
                                MySpinnerAđapter mySpinnerAđapter = new MySpinnerAđapter(getApplicationContext(), branches);
                                spinner.setAdapter(mySpinnerAđapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(LoginActivity.this, "Something wrong is happening", Toast.LENGTH_SHORT).show();
                        Log.d("AAA","Error:\n" + volleyError.toString());

                    }
                });
        requestQueue.add(stringRequest);
    }

    private void getUsers(String url){
       id = (int) spinner.getSelectedItemId();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        boolean check = false;
                        if(s != null){
                            try {
                                JSONArray jsonArray = new JSONArray(s);
                                for (int i = 0; i <jsonArray.length() ; i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    if(edtUserName.getText().toString().trim().equals(jsonObject.getString("UserName")) &&
                                            edtPassword.getText().toString().trim().equals(jsonObject.getString("Password"))){
                                        check = true;
                                        fullname = jsonObject.getString("FullName");
                                        accountId = jsonObject.getInt("idAccount");
                                    }
                                }
                                if(!check){
                                    showAlert("Login Fail! Please Re-Enter!");
                                }else{
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.putExtra("branchId", id);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
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
                        Toast.makeText(LoginActivity.this, "Something wrong is happening", Toast.LENGTH_SHORT).show();
                        Log.d("AAA","Error:\n" + volleyError.toString());

                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("ID",id+"");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private Boolean Validate(){
        if(edtUserName.getText().toString().trim().isEmpty()){
            showAlert("UserName cannot be empty! Please enter username");
            return false;
        } else if(edtPassword.getText().toString().trim().isEmpty()){
            showAlert("Password cannot be empty! Please enter password");
            return false;
        }else if(edtPassword.getText().toString().trim().length() < 8){
            showAlert("Password can not be less than 8 characters");
            return false;
        }else if(spinner.getSelectedItemId() == -1){
            showAlert("Please choose your working branch!");
            return false;
        }else{
            return true;
        }
    }
    private void showAlert(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle("Notification");
        builder.setIcon(R.drawable.clover);
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }
    private void loading(Boolean isLoading){
        if(isLoading){
            btnLogin.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }else{
            btnLogin.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}