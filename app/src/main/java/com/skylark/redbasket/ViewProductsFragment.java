package com.skylark.redbasket;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
 * Use the {@link ViewProductsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewProductsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String catName;
    private RecyclerView recyclerView;
    private CustomProductAdapter adapter;
    private JSONArray productList;
    private ProgressDialog progressDialog;


    public ViewProductsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ViewProductsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewProductsFragment newInstance(String param1) {
        ViewProductsFragment fragment = new ViewProductsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            catName = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_products, container, false);

        getActivity().setTitle("Products");

        productList = new JSONArray();

        //show process dialog when verifying details
        progressDialog = new ProgressDialog(getContext(), android.R.style.Theme_Material_Light_Dialog);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please Wait...");

        recyclerView = view.findViewById(R.id.rv_view_product_list);
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

                holder.mrp.setText(product.getString("mrp"));
                holder.price.setText(product.getString("selling_price"));

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
            private TextView mrp;
            private TextView price;
            private TextView qty;
            private Button dec;
            private Button inc;
            private TextView discount;

            public CustomViewHolder(View view) {
                super(view);
                icon = view.findViewById(R.id.ic_product_item);
                name = view.findViewById(R.id.tv_product_name);
                mrp = view.findViewById(R.id.tv_product_mrp);
                price = view.findViewById(R.id.tv_product_selling_price);
                qty = view.findViewById(R.id.tv_product_quantity);
                inc = view.findViewById(R.id.btn_product_inc);
                dec = view.findViewById(R.id.btn_product_dec);
                discount = view.findViewById(R.id.tv_product_discount);

                if(qty.getText().toString().equals("0")) {
                    dec.setEnabled(false);
                }
                else {
                    dec.setEnabled(true);
                }

                /*
                    ACTION_ADD = 0
                    ACTION_DELETE = 1
                    ACTION_INC = 2
                    ACTION_DEC = 3
                 */


                inc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences pref = getContext().getSharedPreferences(getContext().getPackageName(), Context.MODE_PRIVATE);

                        if(pref.getString("isLogin", "false").equals("true")) {

                            if (qty.getText().toString().equals("0")) {
                                updateQty(inc, dec, 0, qty, getAdapterPosition()); //add
                            } else {
                                updateQty(inc, dec, 2, qty, getAdapterPosition()); //inc
                            }
                        } else {
                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Light_Dialog_Alert);
                            } else {
                                builder = new AlertDialog.Builder(getActivity());
                            }
                            builder.setTitle("Login Required")
                                    .setMessage("Login First to get personalized experince of shopping.")
                                    .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                                            startActivity(intent);
                                            getActivity().finish();
                                        }
                                    })
                                    .setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            //do nothing
                                        }
                                    })
                                    .create()
                                    .show();
                        }
                    }
                });

                dec.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(qty.getText().toString().equals("1")) {
                            updateQty(inc, dec,1, qty, getAdapterPosition()); //delete
                        }
                        else {
                            updateQty(inc, dec,3, qty, getAdapterPosition()); //dec
                        }
                    }
                });
            }
        }

    }

    private void updateQty(final Button inc, final Button dec, final int action, final TextView qty, final int position) {
        progressDialog.show();

        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = getString(R.string.host_url) + "cart_opr.php";

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
                                int quantity = Integer.parseInt(response.getString("qty"));
                                qty.setText(String.valueOf(quantity));
                                if(quantity != 0) {
                                    dec.setEnabled(true);
                                } else {
                                    dec.setEnabled(false);
                                }
                            } else {
                                Snackbar.make(recyclerView, response.getString("message"), Snackbar.LENGTH_LONG ).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Snackbar.make(recyclerView, "Something Went Wrong.", Snackbar.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences pref = getContext().getSharedPreferences(getContext().getPackageName(), Context.MODE_PRIVATE);
                params.put("user_id", pref.getString("user_id", "null"));
                try {
                    params.put("p_id", productList.getJSONObject(position).getString("p_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String act = null;

                switch (action) {
                    case 0: act = "add";
                        break;

                    case 1: act = "delete";
                        break;

                    case 2: act = "inc";
                        break;

                    case 3: act = "dec";
                        break;

                    default: act = "null";
                }
                params.put("action", act);
                return params;
            }
        };

        // Add the request to the RequestQueue.
        RestClient.getInstance(getContext()).addToRequestQueue(stringRequest);
    }

    private void fetchProductList() {
        progressDialog.show();
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = getString(R.string.host_url) + "fetch_cat_wise_products.php";

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
                params.put("category", catName);
                return params;
            }
        };

        // Add the request to the RequestQueue.
        RestClient.getInstance(getContext()).addToRequestQueue(stringRequest);

    }

}
