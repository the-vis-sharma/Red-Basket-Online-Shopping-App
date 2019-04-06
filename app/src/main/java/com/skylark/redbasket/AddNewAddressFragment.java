package com.skylark.redbasket;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */

public class AddNewAddressFragment extends Fragment {

    public AddNewAddressFragment() {
        // Required empty public constructor
    }


    private EditText fullName;
    private EditText mobile;
    private EditText locality;
    private EditText city;
    private EditText state;
    private EditText pin;
    private EditText landmark;
    private RelativeLayout btnSaveAddress;
    private TextView tvSaveAddress;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_new_address, container, false);

        getActivity().setTitle("Add New Address");

        progressDialog = new ProgressDialog(getContext(), android.R.style.Theme_Material_Light_Dialog);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIcon(R.drawable.red_basket_logo);
        progressDialog.setTitle("Saving Address");
        progressDialog.setMessage("Please Wait...");

        fullName = view.findViewById(R.id.et_add_address_full_name);
        mobile = view.findViewById(R.id.et_add_address_mobile);
        locality = view.findViewById(R.id.et_add_address_locality);
        city = view.findViewById(R.id.et_add_address_city);
        state = view.findViewById(R.id.et_add_address_state);
        pin = view.findViewById(R.id.et_add_address_pincode);
        landmark = view.findViewById(R.id.et_add_address_landmark);
        btnSaveAddress = view.findViewById(R.id.btn_save_address);
        tvSaveAddress = view.findViewById(R.id.tv_save_address);

        btnSaveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAddress();
            }
        });

        tvSaveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAddress();
            }
        });

        return view;
    }

    private void saveAddress() {
        if(validate()) {
            progressDialog.show();
            //this is the url where you want to send the request
            //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
            String url = getString(R.string.host_url) + "add_new_address.php";

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
                                    Toast.makeText(getContext(), "Added Successfully.", Toast.LENGTH_LONG).show();
                                    FragmentManager manager = getFragmentManager();
                                    ViewAddressFragment vaf = new ViewAddressFragment();
                                    manager.beginTransaction().replace(R.id.RelativeLayoutHome, vaf, vaf.getTag()).commit();
                                } else {
                                    Snackbar.make(btnSaveAddress, response.getString("message"), Snackbar.LENGTH_LONG ).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Snackbar.make(btnSaveAddress, "Something Went Wrong.", Snackbar.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    SharedPreferences pref = getContext().getSharedPreferences(getContext().getPackageName(), MODE_PRIVATE);
                    params.put("user_id", pref.getString("user_id", "null"));
                    params.put("fullName", fullName.getText().toString());
                    params.put("mobile", mobile.getText().toString());
                    params.put("locality", locality.getText().toString());
                    params.put("city", city.getText().toString());
                    params.put("state", state.getText().toString());
                    params.put("pincode", pin.getText().toString());
                    params.put("landmark", landmark.getText().toString());
                    return params;
                }
            };

            // Add the request to the RequestQueue.
            RestClient.getInstance(getContext()).addToRequestQueue(stringRequest);
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
        } else if(locality.getText().length() > 50) {
            locality.requestFocus();
            locality.setError("Too long email address.");
            return false;
        } else if(city.getText().length() > 25) {
            city.requestFocus();
            city.setError("Maximum 25 Characters are allowed.");
            return false;
        }
        return true;
    }

}
