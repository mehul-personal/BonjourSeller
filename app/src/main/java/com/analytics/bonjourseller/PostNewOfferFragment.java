package com.analytics.bonjourseller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PostNewOfferFragment.OnPostOfferFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PostNewOfferFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostNewOfferFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
ImageView imvFood,imvMerchant,imvRealEstate,imvFoodArrow,imvMerchantArrow,imvRealEstateArrow;
    TextView txvFood,txvMerchant,txvRealEstate;
    private OnPostOfferFragmentInteractionListener mListener;

    public PostNewOfferFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static PostNewOfferFragment newInstance(String param1, String param2) {
        PostNewOfferFragment fragment = new PostNewOfferFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_post_new_offer, container, false);
        imvFood=(ImageView) view.findViewById(R.id.imvFood);
        imvMerchant=(ImageView) view.findViewById(R.id.imvMerchant);
        imvRealEstate=(ImageView) view.findViewById(R.id.imvRealEstate);
        imvFoodArrow=(ImageView) view.findViewById(R.id.imvFoodArrow);
        imvMerchantArrow=(ImageView) view.findViewById(R.id.imvMerchantArrow);
        imvRealEstateArrow=(ImageView) view.findViewById(R.id.imvRealEstateArrow);

        txvFood=(TextView) view.findViewById(R.id.txvFood);
        txvMerchant=(TextView) view.findViewById(R.id.txvMerchant);
        txvRealEstate=(TextView) view.findViewById(R.id.txvRealEstate);

        imvFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),PostFoodRestaurantActivity.class);
                i.putExtra("TYPE","ADD");
                startActivity(i);
            }
        });
        imvFoodArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),PostFoodRestaurantActivity.class);
                i.putExtra("TYPE","ADD");
                startActivity(i);
            }
        });
        txvFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),PostFoodRestaurantActivity.class);
                i.putExtra("TYPE","ADD");
                startActivity(i);
            }
        });

        imvRealEstate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),PostRealEstateActivity.class);
                i.putExtra("TYPE","ADD");
                startActivity(i);
            }
        });
        imvRealEstateArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),PostRealEstateActivity.class);
                i.putExtra("TYPE","ADD");
                startActivity(i);
            }
        });
        txvRealEstate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),PostRealEstateActivity.class);
                i.putExtra("TYPE","ADD");
                startActivity(i);
            }
        });
        imvMerchant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),PostShopesAndMerchantActivity.class);
                i.putExtra("TYPE","ADD");
                startActivity(i);
            }
        });
        imvMerchantArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),PostShopesAndMerchantActivity.class);
                i.putExtra("TYPE","ADD");
                startActivity(i);
            }
        });
        txvMerchant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),PostShopesAndMerchantActivity.class);
                i.putExtra("TYPE","ADD");
                startActivity(i);
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPostOfferFragmentInteractionListener) {
            mListener = (OnPostOfferFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnPostOfferFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
