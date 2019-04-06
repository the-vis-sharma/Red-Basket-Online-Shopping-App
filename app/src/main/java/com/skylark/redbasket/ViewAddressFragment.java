package com.skylark.redbasket;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
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
public class ViewAddressFragment extends Fragment {


    private TextView tvAddNewAddress;
    private RelativeLayout btnAddNewAddress;
    private RecyclerView recyclerView;
    private CustomAddressAdapter adapter;
    private JSONArray saveAddressList;
    private ProgressDialog progressDialog;

    public ViewAddressFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_view_address, container, false);

        getActivity().setTitle("Saved Address");

        //show process dialog when verifying details
        progressDialog = new ProgressDialog(getContext(), android.R.style.Theme_Material_Light_Dialog);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIcon(R.drawable.red_basket_logo);
        progressDialog.setTitle("Updating List");
        progressDialog.setMessage("Please Wait...");

        final FragmentManager manager = getFragmentManager();

        tvAddNewAddress = view.findViewById(R.id.tv_add_new_address);
        btnAddNewAddress = view.findViewById(R.id.btn_add_new_address);
        recyclerView = view.findViewById(R.id.rv_view_address);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        saveAddressList = new JSONArray();
        adapter = new CustomAddressAdapter();
        recyclerView.setAdapter(adapter);

        tvAddNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().setTitle("Add New Address");
                AddNewAddressFragment anaf = new AddNewAddressFragment();
                manager.beginTransaction().replace(R.id.RelativeLayoutHome, anaf, anaf.getTag()).commit();
            }
        });

        btnAddNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().setTitle("Add New Address");
                AddNewAddressFragment anaf = new AddNewAddressFragment();
                manager.beginTransaction().replace(R.id.RelativeLayoutHome, anaf, anaf.getTag()).commit();
            }
        });

        fetchSavedAddress();

        return view;
    }

    public class CustomAddressAdapter extends RecyclerView.Adapter <CustomAddressAdapter.CustomViewHolder> {

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.custom_view_address_item, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final CustomViewHolder holder, final int position) {
            try {
                JSONObject address = saveAddressList.getJSONObject(position);
                holder.fullName.setText(address.getString("fullName"));
                holder.address.setText(address.getString("locality") + ", " + address.getString("city") +
                        ", " + address.getString("state") + " - " + address.getString("pincode"));
                holder.landmark.setText(address.getString("landmark"));
                holder.mobile.setText(address.getString("mobile"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return saveAddressList.length();
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {

            private TextView fullName;
            private TextView address;
            private TextView landmark;
            private TextView mobile;
            private TextView edit;
            private TextView delete;
            private RadioButton select;

            public CustomViewHolder(View view) {
                super(view);
                fullName = view.findViewById(R.id.tv_address_item_full_name);
                address = view.findViewById(R.id.tv_address_item_address);
                landmark = view.findViewById(R.id.tv_address_item_landmark);
                mobile = view.findViewById(R.id.tv_address_item_mobile);
                edit = view.findViewById(R.id.tv_address_item_edit);
                delete = view.findViewById(R.id.tv_address_item_delete);
                select = view.findViewById(R.id.rb_view_address_select);

                select.setVisibility(View.GONE);

                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FragmentManager manager = getFragmentManager();
                        EditSavedAddressFragment esaf = null;
                        try {
                            esaf = EditSavedAddressFragment.newInstance(saveAddressList.get(getAdapterPosition()).toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        manager.beginTransaction().replace(R.id.RelativeLayoutHome, esaf, esaf.getTag()).addToBackStack(null).commit();
                    }
                });

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteSavedAddress(getAdapterPosition());
                    }
                });

            }
        }
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
                                saveAddressList = response.getJSONArray("saved_address_list");
                                adapter.notifyDataSetChanged();
                            } else {
                                Snackbar.make(btnAddNewAddress, response.getString("message"), Snackbar.LENGTH_LONG ).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Snackbar.make(btnAddNewAddress, "Something Went Wrong.", Snackbar.LENGTH_LONG).show();
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

    private void deleteSavedAddress(final int position) {
        progressDialog.setTitle("Deleting");
        progressDialog.show();
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = getString(R.string.host_url) + "delete_saved_address.php";

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
                                Toast.makeText(getContext(), "Address Deleted.", Toast.LENGTH_LONG).show();
                                saveAddressList.remove(position);
                                adapter.notifyDataSetChanged();
                            } else {
                                Snackbar.make(btnAddNewAddress, response.getString("message"), Snackbar.LENGTH_LONG ).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Snackbar.make(btnAddNewAddress, "Something Went Wrong.", Snackbar.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                try {
                    JSONObject address = saveAddressList.getJSONObject(position);
                    params.put("a_id", address.getString("a_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return params;
            }
        };

        // Add the request to the RequestQueue.
        RestClient.getInstance(getContext()).addToRequestQueue(stringRequest);

    }


}
