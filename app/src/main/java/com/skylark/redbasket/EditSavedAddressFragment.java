package com.skylark.redbasket;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
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

public class EditSavedAddressFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private JSONObject address;
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

    public EditSavedAddressFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment EditSavedAddressFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditSavedAddressFragment newInstance(String param1) {
        EditSavedAddressFragment fragment = new EditSavedAddressFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            try {
                address = new JSONObject(mParam1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_saved_address, container, false);

        getActivity().setTitle("Edit Saved Address");

        progressDialog = new ProgressDialog(getContext(), android.R.style.Theme_Material_Light_Dialog);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIcon(R.drawable.red_basket_logo);
        progressDialog.setTitle("Updating Address");
        progressDialog.setMessage("Please Wait...");

        fullName = view.findViewById(R.id.et_edit_address_full_name);
        mobile = view.findViewById(R.id.et_edit_address_mobile);
        locality = view.findViewById(R.id.et_edit_address_locality);
        city = view.findViewById(R.id.et_edit_address_city);
        state = view.findViewById(R.id.et_edit_address_state);
        pin = view.findViewById(R.id.et_edit_address_pincode);
        landmark = view.findViewById(R.id.et_edit_address_landmark);
        btnSaveAddress = view.findViewById(R.id.btn_save_address);
        tvSaveAddress = view.findViewById(R.id.tv_save_address);

        try {
            fullName.setText(address.getString("fullName"));
            mobile.setText(address.getString("mobile"));
            locality.setText(address.getString("locality"));
            city.setText(address.getString("city"));
            state.setText(address.getString("state"));
            pin.setText(address.getString("pincode"));
            landmark.setText(address.getString("landmark"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btnSaveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSavedAddress();
            }
        });

        tvSaveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSavedAddress();
            }
        });

        return view;
    }

    private void updateSavedAddress() {
        if(validate()) {
            progressDialog.show();
            //this is the url where you want to send the request
            //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
            String url = getString(R.string.host_url) + "update_saved_address.php";

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
                                    Toast.makeText(getContext(), "Address Updated.", Toast.LENGTH_LONG).show();
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
                    try {
                        params.put("a_id", address.getString("a_id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
