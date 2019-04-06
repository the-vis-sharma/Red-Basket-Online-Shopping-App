package com.skylark.redbasket;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountFragment extends Fragment {


    public MyAccountFragment() {
        // Required empty public constructor
    }

    private TextView changePass;
    private TextView viewAddress;
    private TextView defaultAdd;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);

        getActivity().setTitle("My Account");

        //show process dialog when verifying details
        progressDialog = new ProgressDialog(getContext(), android.R.style.Theme_Material_Light_Dialog);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIcon(R.drawable.red_basket_logo);
        progressDialog.setTitle("Updating List");
        progressDialog.setMessage("Please Wait...");

        final FragmentManager manager = getFragmentManager();

        changePass = view.findViewById(R.id.tv_my_account_change_pass);
        viewAddress = view.findViewById(R.id.tv_my_account_view_more_address);
        defaultAdd = view.findViewById(R.id.tv_my_account_default_address);

        SharedPreferences pref = getContext().getSharedPreferences(getContext().getPackageName(), Context.MODE_PRIVATE);

        TextView fullName = view.findViewById(R.id.tv_my_account_full_name);
        TextView mobile = view.findViewById(R.id.tv_my_account_mobile);
        TextView email = view.findViewById(R.id.tv_my_account_email);

        fullName.setText(pref.getString("fullName", "null"));
        mobile.setText(pref.getString("mobile", "null"));
        email.setText(pref.getString("email", "null"));

        viewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().setTitle("Saved Addresses");
                ViewAddressFragment vaf = new ViewAddressFragment();
                manager.beginTransaction().replace(R.id.RelativeLayoutHome, vaf, vaf.getTag()).addToBackStack(null).commit();
            }
        });

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().setTitle("Change Password");
                ChangePassFragment cpf = new ChangePassFragment();
                manager.beginTransaction().replace(R.id.RelativeLayoutHome, cpf, cpf.getTag()).addToBackStack(null).commit();
            }
        });


        fetchSavedAddress();

        return view;
    }

    private void fetchSavedAddress() {
        progressDialog.show();
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = getString(R.string.host_url) + "fetch_saved_address.php";

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
                                Toast.makeText(getContext(), "List Updated.", Toast.LENGTH_LONG).show();
                                JSONArray saveAddressList = response.getJSONArray("saved_address_list");
                                JSONObject address = saveAddressList.getJSONObject(0);
                                defaultAdd.setText(address.getString("fullName") + "\n"
                                        + address.getString("locality") + ", " + address.getString("city") +
                                        ", " + address.getString("state") + " - " + address.getString("pincode")
                                        + "\n" + address.getString("landmark") + "\n" + address.getString("mobile"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Snackbar.make(changePass, "Something Went Wrong.", Snackbar.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences pref = getContext().getSharedPreferences(getContext().getPackageName(), MODE_PRIVATE);
                params.put("user_id", pref.getString("user_id", "null"));
                return params;
            }
        };

        // Add the request to the RequestQueue.
        RestClient.getInstance(getContext()).addToRequestQueue(stringRequest);

    }

}
