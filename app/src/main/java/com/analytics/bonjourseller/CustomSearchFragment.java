package com.analytics.bonjourseller;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CustomSearchFragment.OnCustomSearchFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CustomSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomSearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnCustomSearchFragmentInteractionListener mListener;
    ImageView searchFood, searchMerchant, searchRealEstate;
    ArrayList<String> name, description, category_id, image, owner_id, offer_id;
    ListView searchList;

    public CustomSearchFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CustomSearchFragment newInstance(String param1, String param2) {
        CustomSearchFragment fragment = new CustomSearchFragment();
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
        View view = inflater.inflate(R.layout.fragment_custom_search, container, false);
        searchFood = (ImageView) view.findViewById(R.id.imvSearchFood);
        searchMerchant = (ImageView) view.findViewById(R.id.imvSearchMerchant);
        searchRealEstate = (ImageView) view.findViewById(R.id.imvSearchRealEstate);
        searchList = (ListView) view.findViewById(R.id.lvSearchList);
        searchFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findCustomSearch("1");
            }
        });
        searchMerchant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findCustomSearch("3");
            }
        });
        searchRealEstate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findCustomSearch("5");
            }
        });

        findCustomSearch("1");
        return view;
    }

    public void findCustomSearch(final String categoryId) {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "custom_search.php";
        Log.e("url", url + "");
        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {
                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());
                            Log.e("2", "2");

                            String msg = object.getString("msg");
                            if (msg.equalsIgnoreCase("Success")) {
                                name = new ArrayList<String>();
                                description = new ArrayList<String>();
                                category_id = new ArrayList<String>();
                                image = new ArrayList<String>();
                                owner_id = new ArrayList<String>();
                                offer_id = new ArrayList<String>();

                                JSONArray dataArr = object.getJSONArray("data");
                                for (int i = 0; i < dataArr.length(); i++) {
                                    JSONObject dataOb = dataArr.getJSONObject(i);
                                    name.add(dataOb.getString("name"));
                                    description.add(dataOb.getString("description"));
                                    category_id.add(dataOb.getString("category_id"));
                                    owner_id.add(dataOb.getString("owner_id"));
                                    offer_id.add(dataOb.getString("offer_id"));
                                    int img_count = Integer.parseInt(dataOb.getString("image_count"));
                                    //for(int j=0;j<img_count;j++){
                                    if (img_count > 0)
                                        image.add(dataOb.getString("image" + 1));
                                    else
                                        image.add("");
                                    //}

                                }
                                CustomSearchAdapter adapter = new CustomSearchAdapter(getActivity(), name, description, category_id, owner_id, offer_id, image);
                                searchList.setAdapter(adapter);
                            } else {
                                Toast.makeText(getActivity(),
                                        "Oopss! We can't found the any offer for this category",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception error) {
                            mProgressDialog.dismiss();
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                Toast.makeText(getActivity(), "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();

                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getActivity(), "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("category_id", categoryId);

                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public class CustomSearchAdapter extends BaseAdapter {

        LayoutInflater inflater;
        ArrayList<String> name, description, category_id, owner_id, offer_id, image;

        int m = 0;

        public CustomSearchAdapter(Context context, ArrayList<String> name, ArrayList<String> description,
                                   ArrayList<String> category_id, ArrayList<String> owner_id,
                                   ArrayList<String> offer_id, ArrayList<String> image) {
            // TODO Auto-generated constructor stub
            this.name = name;
            this.category_id = category_id;
            this.owner_id = owner_id;
            this.description = description;
            this.offer_id = offer_id;
            this.image = image;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        class ViewHolder {
            TextView txvRestaruntName, txvRestaruntDescription, categoryName, chatText;
            ImageView image, categoryImage, chatImage;
        }

        public void refreshList() {
            super.notifyDataSetChanged();
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            // View view = null;

            ViewHolder holder;
            if (convertView == null) {

                convertView = inflater.inflate(
                        R.layout.row_custom_search, parent, false);
                holder = new ViewHolder();
                holder.txvRestaruntName = (TextView) convertView
                        .findViewById(R.id.txvRestaurantName);
                holder.txvRestaruntDescription = (TextView) convertView
                        .findViewById(R.id.txvRestaurantDescription);
                holder.categoryName = (TextView) convertView
                        .findViewById(R.id.txvCategoryName);
                holder.chatText = (TextView) convertView
                        .findViewById(R.id.txvChat);
                holder.image = (ImageView) convertView
                        .findViewById(R.id.ivRestaurantMainImage);
                holder.categoryImage = (ImageView) convertView
                        .findViewById(R.id.ivCategoryIcon);
                holder.chatImage = (ImageView) convertView
                        .findViewById(R.id.ivChatIcon);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.txvRestaruntDescription.setText(description.get(position));
            if (category_id.get(position).equalsIgnoreCase("1")) {
                holder.categoryImage.setImageResource(R.drawable.ic_category_restarent);
                holder.categoryName.setText("Restaurant");
            } else if (category_id.get(position).equalsIgnoreCase("3")) {
                holder.categoryImage.setImageResource(R.drawable.ic_category_merchant);
                holder.categoryName.setText("Merchant");
            } else if (category_id.get(position).equalsIgnoreCase("5")) {
                holder.categoryImage.setImageResource(R.drawable.ic_real_estate);
                holder.categoryName.setText("Real Estate");
            }
            if (image.get(position).isEmpty() && category_id.get(position).equalsIgnoreCase("1")) {
                holder.image.setImageResource(R.drawable.ic_no_image_restaruent);
            } else if (image.get(position).isEmpty() && category_id.get(position).equalsIgnoreCase("3")) {
                holder.image.setImageResource(R.drawable.ic_no_image_merchant);
            } else if (image.get(position).isEmpty() && category_id.get(position).equalsIgnoreCase("5")) {
                holder.image.setImageResource(R.drawable.ic_no_image_real_estate);
            } else {
                Picasso.with(getActivity())
                        .load(image.get(position))
                        .into(holder.image);
            }
            return convertView;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return name.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }
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
        if (context instanceof OnCustomSearchFragmentInteractionListener) {
            mListener = (OnCustomSearchFragmentInteractionListener) context;
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
    public interface OnCustomSearchFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
