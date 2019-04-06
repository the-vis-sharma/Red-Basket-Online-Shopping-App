package com.skylark.redbasket;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrderPlacedFragment extends Fragment {


    private Button viewOrderDetails;

    public OrderPlacedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_placed, container, false);

        viewOrderDetails = view.findViewById(R.id.btn_place_order_view_order_details);

        viewOrderDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FragmentManager manager = getFragmentManager();
                final CheckoutFragment cof = new CheckoutFragment();
                manager.beginTransaction().replace(R.id.RelativeLayoutHome, cof, cof.getTag()).addToBackStack(null).commit();
            }
        });

        return view;
    }

}
