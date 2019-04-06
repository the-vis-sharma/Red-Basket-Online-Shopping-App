package com.skylark.redbasket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private TextView skip;
    private EditText username;
    private EditText pass;
    private TextView signin;
    private TextView signup;
    private TextView forgotPass;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        setTitle("Login");

        //show process dialog when verifying details
        progressDialog = new ProgressDialog(LoginActivity.this, android.R.style.Theme_Material_Light_Dialog);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIcon(R.drawable.red_basket_logo);
        progressDialog.setTitle("Checking");
        progressDialog.setMessage("Please Wait...");

        username = findViewById(R.id.et_login_username);
        pass = findViewById(R.id.et_login_userpass);
        signin = findViewById(R.id.tv_login_signin);
        signup = findViewById(R.id.tv_login_signup);
        forgotPass = findViewById(R.id.tv_login_forgot_pass);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signinUser();
            }
        });

        pass.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == 0 && keyCode == 66) {
                    signinUser();
                }
                return false;
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        skip = findViewById(R.id.tv_login_skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void signinUser() {
        if(validate()) {
            progressDialog.show();
            //this is the url where you want to send the request
            //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
            String url = getString(R.string.host_url) + "login.php";

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String result) {
                            // Display the response string.
                            progressDialog.dismiss();
                            try {
                                JSONObject response = new JSONObject(result);
                                if(response.getBoolean("success")) {
                                    SharedPreferences pref = getSharedPreferences(getPackageName(), MODE_PRIVATE);
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putString("user_id", response.getString("user_id"));
                                    editor.putString("fullName", response.getString("fullName"));
                                    editor.putString("mobile", response.getString("mobile"));
                                    editor.putString("email", response.getString("email"));
                                    editor.putString("isLogin", "true");
                                    editor.putString("lastLoginTime", new Date().toString());
                                    editor.commit();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Snackbar.make(signin, response.getString("message"), Snackbar.LENGTH_LONG ).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Snackbar.make(signin, "Error: " + error.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("email", username.getText().toString());
                        params.put("password", pass.getText().toString());
                        return params;
                    }
                };

            // Add the request to the RequestQueue.
            RestClient.getInstance(this).addToRequestQueue(stringRequest);
        }
    }

    private boolean validate() {
        if(TextUtils.isEmpty(username.getText())) {
            username.requestFocus();
            username.setError("This is required.");
            return false;
        } else if(TextUtils.isEmpty(pass.getText())) {
            pass.requestFocus();
            pass.setError("This is required.");
            return false;
        } else if(username.getText().length() > 50) {
            username.requestFocus();
            username.setError("Too long email address.");
            return false;
        } else if(pass.getText().length() > 25) {
            pass.requestFocus();
            pass.setError("Maximum 25 Characters are allowed.");
            return false;
        }
        return true;
    }

}

