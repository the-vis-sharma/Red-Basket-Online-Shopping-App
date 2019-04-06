package com.skylark.redbasket;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyOrderFragment extends Fragment {

    private RecyclerView recyclerView;
    private CustomProductAdapter adapter;
    private JSONArray productList;
    private ProgressDialog progressDialog;

    public MyOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_order, container, false);

        getActivity().setTitle("My Orders");

        productList = new JSONArray();

        //show process dialog when verifying details
        progressDialog = new ProgressDialog(getContext(), android.R.style.Theme_Material_Light_Dialog);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please Wait...");

        recyclerView = view.findViewById(R.id.rv_order_history_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new CustomProductAdapter();
        recyclerView.setAdapter(adapter);

        fetchProductList();

        return view;
    }

    public class CustomProductAdapter extends RecyclerView.Adapter <CustomProductAdapter.CustomViewHolder> {

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.custom_products_item, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, final int position) {
            try {
                JSONObject product = productList.getJSONObject(position);
                Glide.with(getActivity()).load(getString(R.string.host_url) + "img/" +
                        product.getString("img")).into(holder.icon);
                holder.name.setText(product.getString("name"));

                holder.timeStamp.setText(product.getString("timeStamp"));
                holder.status.setText(product.getString("status"));
                try {
                    if(product.getString("status").equals("placed")) {
                        holder.status.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    }
                    else {
                        holder.status.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                int dis = 100 - (int)((Float.parseFloat(product.getString("selling_price")) * 100)/Float.parseFloat(product.getString("mrp")));

                holder.discount.setText(dis + "%");

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            if(productList != null) {
                return productList.length();
            }
            return 0;
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {

            private ImageView icon;
            private TextView name;
            private TextView timeStamp;
            private TextView status;
            private TextView qty;
            private Button dec;
            private Button inc;
            private TextView discount;

            public CustomViewHolder(View view) {
                super(view);
                icon = view.findViewById(R.id.ic_product_item);
                name = view.findViewById(R.id.tv_product_name);
                timeStamp = view.findViewById(R.id.tv_product_mrp);
                status = view.findViewById(R.id.tv_product_selling_price);
                qty = view.findViewById(R.id.tv_product_quantity);
                inc = view.findViewById(R.id.btn_product_inc);
                dec = view.findViewById(R.id.btn_product_dec);
                discount = view.findViewById(R.id.tv_product_discount);

                timeStamp.setBackgroundColor(getResources().getColor(R.color.cardview_light_background));
                status.setTextColor(getResources().getColor(R.color.cardview_light_background));
                status.setPadding(8, 8, 8, 8);
                status.setAllCaps(true);

                final FragmentManager manager = getFragmentManager();

                icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            ViewOrderDetailFragment vodf = ViewOrderDetailFragment.newInstance(productList.get(getAdapterPosition()).toString());
                            manager.beginTransaction().replace(R.id.RelativeLayoutHome, vodf, vodf.getTag()).addToBackStack(null).commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            ViewOrderDetailFragment vodf = ViewOrderDetailFragment.newInstance(productList.get(getAdapterPosition()).toString());
                            manager.beginTransaction().replace(R.id.RelativeLayoutHome, vodf, vodf.getTag()).addToBackStack(null).commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            ViewOrderDetailFragment vodf = ViewOrderDetailFragment.newInstance(productList.get(getAdapterPosition()).toString());
                            manager.beginTransaction().replace(R.id.RelativeLayoutHome, vodf, vodf.getTag()).addToBackStack(null).commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                timeStamp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            ViewOrderDetailFragment vodf = ViewOrderDetailFragment.newInstance(productList.get(getAdapterPosition()).toString());
                            manager.beginTransaction().replace(R.id.RelativeLayoutHome, vodf, vodf.getTag()).addToBackStack(null).commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                discount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            ViewOrderDetailFragment vodf = ViewOrderDetailFragment.newInstance(productList.get(getAdapterPosition()).toString());
                            manager.beginTransaction().replace(R.id.RelativeLayoutHome, vodf, vodf.getTag()).addToBackStack(null).commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                inc.setVisibility(View.GONE);
                dec.setVisibility(View.GONE);
                qty.setVisibility(View.GONE);

            }
        }

    }

    private void fetchProductList() {
        progressDialog.show();
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = getString(R.string.host_url) + "fetch_order_history.php";

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
                                productList = response.getJSONArray("product_list");
                            } else {
                                Snackbar.make(recyclerView, response.getString("message"), Snackbar.LENGTH_LONG ).show();
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Snackbar.make(recyclerView, "Something Went Wrong.", Snackbar.LENGTH_LONG).show();
                adapter.notifyDataSetChanged();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences pref = getContext().getSharedPreferences(getContext().getPackageName(), Context.MODE_PRIVATE);
                params.put("user_id", pref.getString("user_id", "null"));
                return params;
            }
        };

        // Add the request to the RequestQueue.
        RestClient.getInstance(getContext()).addToRequestQueue(stringRequest);

    }

}
