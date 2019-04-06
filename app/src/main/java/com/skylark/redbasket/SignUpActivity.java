package com.skylark.redbasket;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private EditText fullName;
    private EditText mobile;
    private EditText email;
    private EditText pass;
    private TextView signin;
    private TextView signup;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);

        setTitle("Sign-up");

        //show process dialog when verifying details
        progressDialog = new ProgressDialog(SignUpActivity.this, android.R.style.Theme_Material_Light_Dialog);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIcon(R.drawable.red_basket_logo);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("Please Wait...");

        fullName = findViewById(R.id.et_signup_full_name);
        mobile = findViewById(R.id.et_signup_mobile);
        email = findViewById(R.id.et_signup_email);
        pass = findViewById(R.id.et_signup_pass);
        signin = findViewById(R.id.tv_signup_signin);
        signup = findViewById(R.id.tv_signup_signup);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signupUser();
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        pass.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == 0 && keyCode == 66) {
                    signupUser();
                }
                return false;
            }
        });

    }

    private void signupUser() {
        if(validate()) {
            progressDialog.show();
            //this is the url where you want to send the request
            //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
            String url = getString(R.string.host_url) + "signup.php";

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
                                    Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
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
                    params.put("fullName", fullName.getText().toString());
                    params.put("mobile", mobile.getText().toString());
                    params.put("email", email.getText().toString());
                    params.put("password", pass.getText().toString());
                    return params;
                }
            };

            // Add the request to the RequestQueue.
            RestClient.getInstance(this).addToRequestQueue(stringRequest);
        }
    }

    private boolean validate() {
        if(TextUtils.isEmpty(fullName.getText())) {
            fullName.requestFocus();
            fullName.setError("This is required.");
            return false;
        } else if(TextUtils.isEmpty(mobile.getText())) {
            mobile.requestFocus();
            mobile.setError("This is required.");
            return false;
        } else if(TextUtils.isEmpty(email.getText())) {
            email.requestFocus();
            email.setError("This is required.");
            return false;
        } else if(TextUtils.isEmpty(pass.getText())) {
            pass.requestFocus();
            pass.setError("This is required.");
            return false;
        } else if(fullName.getText().length() > 50) {
            fullName.requestFocus();
            fullName.setError("Too long name.");
            return false;
        } else if(email.getText().length() > 50) {
            email.requestFocus();
            email.setError("Too long email address.");
            return false;
        } else if(mobile.getText().length() != 10) {
            mobile.requestFocus();
            mobile.setError("Invalid Mobile Number.");
            return false;
        } else if(pass.getText().length() > 25) {
            pass.requestFocus();
            pass.setError("Maximum 25 Characters are allowed.");
            return false;
        }
        return true;
    }
}
