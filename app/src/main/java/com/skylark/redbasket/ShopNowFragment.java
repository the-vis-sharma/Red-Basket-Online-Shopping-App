package com.skylark.redbasket;


import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class ShopNowFragment extends Fragment {


    public ShopNowFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;
    private CustomCatAdapter adapter;
    private JSONArray catList;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_shop_now, container, false);

        getActivity().setTitle("Home");

        //show process dialog when verifying details
        progressDialog = new ProgressDialog(getContext(), android.R.style.Theme_Material_Light_Dialog);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please Wait...");

        recyclerView = view.findViewById(R.id.rv_cat_list);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        }
        else {
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        }

        adapter = new CustomCatAdapter();
        recyclerView.setAdapter(adapter);

        fetchCatList();

        return view;
    }

    public class CustomCatAdapter extends RecyclerView.Adapter <CustomCatAdapter.CustomViewHolder> {

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.custom_cat_item, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CustomViewHolder holder, final int position) {
            try {
                JSONObject cat = catList.getJSONObject(position);
                Glide.with(getActivity()).load(getString(R.string.host_url) + "img/cat/" +
                        cat.getString("img_name")).into(holder.catIcon);
                holder.catName.setText(cat.getString("cat_name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            if(catList != null) {
                return catList.length();
            }
            return 0;
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {

            private ImageView catIcon;
            private TextView catName;

            public CustomViewHolder(View view) {
                super(view);
                catIcon = view.findViewById(R.id.ic_cat_img);
                catName = view.findViewById(R.id.tv_cat_name);

                catIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FragmentManager manager = getFragmentManager();
                        try {
                            JSONObject cat = catList.getJSONObject(getAdapterPosition());
                            ViewProductsFragment vpf = ViewProductsFragment.newInstance(cat.getString("cat_name"));
                            manager.beginTransaction().replace(R.id.RelativeLayoutHome, vpf, vpf.getTag()).addToBackStack(null).commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                catName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FragmentManager manager = getFragmentManager();
                        try {
                            JSONObject cat = catList.getJSONObject(getAdapterPosition());
                            ViewProductsFragment vpf = ViewProductsFragment.newInstance(cat.getString("cat_name"));
                            manager.beginTransaction().replace(R.id.RelativeLayoutHome, vpf, vpf.getTag()).addToBackStack(null).commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

    }

    private void fetchCatList() {
        progressDialog.show();
        //this is the url where you want to send the request
        //TODO: replace with your own url to send request, as I am using my own localhost for this tutorial
        String url = getString(R.string.host_url) + "fetch_cat.php";

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
                                catList = response.getJSONArray("cat_list");
                                adapter.notifyDataSetChanged();
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
        });

        // Add the request to the RequestQueue.
        RestClient.getInstance(getContext()).addToRequestQueue(stringRequest);

    }

}
