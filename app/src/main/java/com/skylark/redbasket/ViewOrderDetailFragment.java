package com.skylark.redbasket;


import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewOrderDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewOrderDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private JSONObject mOrder;

    private TextView orderId;
    private ImageView icon;
    private TextView productName;
    private TextView orderDate;
    private TextView deliveryDate;
    private TextView status;
    private TextView qty;

    private TextView MRP;
    private TextView discount;
    private TextView sellingPrice;
    private TextView totalPrice;

    private TextView fullName;
    private TextView locality;
    private TextView landmark;
    private TextView mobile;

    public ViewOrderDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ViewOrderDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewOrderDetailFragment newInstance(String param1) {
        ViewOrderDetailFragment fragment = new ViewOrderDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                mOrder = new JSONObject(getArguments().getString(ARG_PARAM1));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_order_detail, container, false);

        orderId = view.findViewById(R.id.tv_order_detail_id);
        icon = view.findViewById(R.id.ic_order_detail_item);
        productName = view.findViewById(R.id.tv_order_detail_product_name);
        orderDate = view.findViewById(R.id.tv_order_detail_order_date);
        deliveryDate = view.findViewById(R.id.tv_order_detail_delivery_date);
        status = view.findViewById(R.id.tv_order_detail_status);
        qty = view.findViewById(R.id.tv_order_detail_qty);

        MRP = view.findViewById(R.id.tv_order_detail_MRP_value);
        discount = view.findViewById(R.id.tv_order_detail_discount_value);
        sellingPrice = view.findViewById(R.id.tv_order_detail_selling_price_value);
        totalPrice = view.findViewById(R.id.tv_order_detail_total_price_value);

        fullName = view.findViewById(R.id.tv_order_detail_full_name);
       /* locality = view.findViewById(R.id.tv_order_detail_address);
        landmark = view.findViewById(R.id.tv_order_detail_landmark);
        mobile = view.findViewById(R.id.tv_order_detail_mobile);*/

        try {
            orderId.setText("Order Id #" + mOrder.getString("order_id"));
            Glide.with(getContext()).load(getString(R.string.host_url) + "img/" + mOrder.getString("img")).into(icon);
            productName.setText(mOrder.getString("name"));
            orderDate.setText("Order Date: " + mOrder.getString("timeStamp"));

            String s = mOrder.getString("status");
            if(s.equals("delivered")) {
                deliveryDate.setText("Delivery Date: " + mOrder.getString("delivery_date"));
                status.setText("delivered");
                status.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary));
            }

            qty.setText("Quantity: " + mOrder.getString("qty"));

            float m = Float.parseFloat(mOrder.getString("mrp"));
            float sp = Float.parseFloat(mOrder.getString("selling_price"));
            int q = Integer.parseInt(mOrder.getString("qty"));

            MRP.setText("₹ " + String.valueOf(m*q));
            discount.setText("₹ " + String.valueOf((m-sp)*q));
            sellingPrice.setText("₹ " + String.valueOf(sp*q));
            totalPrice.setText("₹ " + String.valueOf(sp*q));

            fullName.setText(mOrder.getString("address"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

}
