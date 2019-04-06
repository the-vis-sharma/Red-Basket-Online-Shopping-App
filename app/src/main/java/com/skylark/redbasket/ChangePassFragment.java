package com.skylark.redbasket;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
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

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePassFragment extends Fragment {


    private RelativeLayout btnChangePass;
    private TextView tvChangePass;
    private EditText currentPass;
    private EditText newPass;
    private EditText conPass;
    private ProgressDialog progressDialog;

    public ChangePassFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_pass, container, false);

        getActivity().setTitle("Change Password");

        //show process dialog when verifying details
        progressDialog = new ProgressDialog(getContext(), android.R.style.Theme_Material_Light_Dialog);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIcon(R.drawable.red_basket_logo);
        progressDialog.setTitle("Validating");
        progressDialog.setMessage("Please Wait...");

        SharedPreferences pref = getContext().getSharedPreferences(getContext().getPackageName(), Context.MODE_PRIVATE);

        TextView fullName = view.findViewById(R.id.tv_change_pass_full_name);
        TextView mobile = view.findViewById(R.id.tv_change_pass_mobile);
        TextView email = view.findViewById(R.id.tv_change_pass_email);

        fullName.setText(pref.getString("fullName", "null"));
        mobile.setText(pref.getString("mobile", "null"));
        email.setText(pref.getString("email", "null"));

        currentPass = view.findViewById(R.id.et_change_pass_cur_pass);
        newPass = view.findViewById(R.id.et_change_pass_new_pass);
        conPass = view.findViewById(R.id.et_change_pass_con_pass);
        tvChangePass = view.findViewById(R.id.tv_change_pass);



        //button for password change
        btnChangePass = view.findViewById(R.id.btn_change_pass);
        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });

        tvChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });

        conPass.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == 0 && keyCode == 66) {
                    changePassword();
                }
                return false;
            }
        });
        return view;
    }

    private void changePassword() {
        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(btnChangePass.getWindowToken(), 0);
        if(validate()) {
            progressDialog.show();
            //this is the url where you want to send the request
            //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
            String url = getString(R.string.host_url) + "change_pass.php";

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
                                    Toast.makeText(getContext(), "Password Changed.", Toast.LENGTH_LONG).show();
                                    FragmentManager manager = getFragmentManager();
                                    MyAccountFragment maf = new MyAccountFragment();
                                    manager.beginTransaction().replace(R.id.RelativeLayoutHome, maf, maf.getTag()).commit();
                                } else {
                                    Snackbar.make(btnChangePass, response.getString("message"), Snackbar.LENGTH_LONG ).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Snackbar.make(btnChangePass, "Something Went Wrong.", Snackbar.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    SharedPreferences pref = getContext().getSharedPreferences(getContext().getPackageName(), MODE_PRIVATE);
                    params.put("user_id", pref.getString("user_id", "null"));
                    params.put("curPass", currentPass.getText().toString());
                    params.put("newPass", newPass.getText().toString());
                    return params;
                }
            };

            // Add the request to the RequestQueue.
            RestClient.getInstance(getContext()).addToRequestQueue(stringRequest);
        }
    }

    private boolean validate() {
        if(TextUtils.isEmpty(currentPass.getText())) {
            currentPass.requestFocus();
            currentPass.setError("This is required.");
            return false;
        } else if(TextUtils.isEmpty(newPass.getText())) {
            newPass.requestFocus();
            newPass.setError("This is required.");
            return false;
        } else if(TextUtils.isEmpty(conPass.getText())) {
            conPass.requestFocus();
            conPass.setError("This is required.");
            return false;
        }  else if(currentPass.getText().length() > 25) {
            currentPass.requestFocus();
            currentPass.setError("Maximum 25 Characters are allowed.");
            return false;
        } else if(newPass.getText().length() > 25) {
            newPass.requestFocus();
            newPass.setError("Maximum 25 Characters are allowed.");
            return false;
        } else if(conPass.getText().length() > 25) {
            conPass.requestFocus();
            conPass.setError("Maximum 25 Characters are allowed.");
            return false;
        } else if(!newPass.getText().toString().equals(conPass.getText().toString())) {
            conPass.requestFocus();
            conPass.setError("New and Confirm Password didn't Matched.");
            return false;
        }
        return true;
    }

}
