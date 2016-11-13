package com.analytics.bonjourseller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
 * {@link MainOfferListFragment.OnOfferListFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainOfferListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainOfferListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ListView offerList;
    Context mContext;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnOfferListFragmentInteractionListener mListener;

    public MainOfferListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainOfferListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainOfferListFragment newInstance(String param1, String param2) {
        MainOfferListFragment fragment = new MainOfferListFragment();
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
        View view = inflater.inflate(R.layout.fragment_main_offer_list, container, false);
        mContext = getActivity();
        offerList = (ListView) view.findViewById(R.id.lvOfferList);
        getOfferList();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getOfferList();
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
        if (context instanceof OnOfferListFragmentInteractionListener) {
            mListener = (OnOfferListFragmentInteractionListener) context;
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

    public void getOfferList() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "seller_home.php";
        Log.e("url", url + "");
        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {
                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());
                            Log.e("response", "response:" + response);

                            String msg = object.getString("msg");
                            if (msg.equalsIgnoreCase("Success")) {
                                ArrayList<String> brandname = new ArrayList<String>();
                                ArrayList<String> description = new ArrayList<String>();
                                ArrayList<String> category_id = new ArrayList<String>();
                                HashMap<String, ArrayList<String>> image = new HashMap<String, ArrayList<String>>();
                                ArrayList<String> latitude = new ArrayList<String>();
                                ArrayList<String> longitude = new ArrayList<String>();
                                ArrayList<String> offer_id = new ArrayList<String>();
                                ArrayList<String> ftypeOffood = new ArrayList<String>();
                                ArrayList<String> offerstatus = new ArrayList<String>();
                                ArrayList<String> ffoodType = new ArrayList<String>();
                                ArrayList<String> festablishment = new ArrayList<String>();
                                ArrayList<String> address = new ArrayList<String>();
                                ArrayList<String> rtypeofProperty = new ArrayList<String>();
                                ArrayList<String> rpurpose = new ArrayList<String>();
                                ArrayList<String> rcomplexname = new ArrayList<String>();
                                ArrayList<String> rhouseno = new ArrayList<String>();
                                ArrayList<String> rfurnishstatus = new ArrayList<String>();
                                ArrayList<String> rbhk = new ArrayList<String>();
                                ArrayList<String> roldp = new ArrayList<String>();
                                ArrayList<String> rsqft = new ArrayList<String>();
                                ArrayList<String> rproperty_type = new ArrayList<String>();
                                ArrayList<String> category_name = new ArrayList<String>();

                                JSONArray dataArr = object.getJSONArray("data");
                                for (int i = 0; i < dataArr.length(); i++) {
                                    JSONObject dataOb = dataArr.getJSONObject(i);
                                    if (dataOb.getString("category_id").equalsIgnoreCase("5")) {
                                        //realestate
                                        brandname.add(dataOb.getString("name_of_society"));
                                        rtypeofProperty.add(dataOb.getString("type_of_property"));//resident or commercial
                                        rpurpose.add(dataOb.getString("purpose"));//sale or rent
                                        rcomplexname.add(dataOb.getString("complex_name"));
                                        rhouseno.add(dataOb.getString("house_no"));
                                        rfurnishstatus.add(dataOb.getString("furnish_status"));
                                        rbhk.add(dataOb.getString("bhk"));
                                        roldp.add(dataOb.getString("oldp"));
                                        rsqft.add(dataOb.getString("sqft"));
                                        rproperty_type.add(dataOb.getString("property_type"));
                                        address.add(dataOb.getString("address"));
                                        ftypeOffood.add("");
                                        ffoodType.add("");
                                        festablishment.add("");
                                        category_name.add("");
                                    } else if (dataOb.getString("category_id").equalsIgnoreCase("3")) {
                                        //merchant
                                        brandname.add(dataOb.getString("brand_name"));
                                        address.add(dataOb.getString("address"));
                                        rtypeofProperty.add("");//resident or commercial
                                        rpurpose.add("");//sale or rent
                                        rcomplexname.add("");
                                        rhouseno.add("");
                                        rfurnishstatus.add("");
                                        rbhk.add("");
                                        roldp.add("");
                                        rsqft.add("");
                                        rproperty_type.add("");
                                        ftypeOffood.add("");
                                        ffoodType.add("");
                                        festablishment.add("");
                                        category_name.add(dataOb.getString("category_name"));
                                    } else if (dataOb.getString("category_id").equalsIgnoreCase("1")) {
                                        //food
                                        brandname.add(dataOb.getString("brand_name"));
                                        ftypeOffood.add(dataOb.getString("type_of_food"));
                                        ffoodType.add(dataOb.getString("food_type"));
                                        festablishment.add(dataOb.getString("establish_type"));
                                        address.add(dataOb.getString("address"));
                                        rtypeofProperty.add("");//resident or commercial
                                        rpurpose.add("");//sale or rent
                                        rcomplexname.add("");
                                        rhouseno.add("");
                                        rfurnishstatus.add("");
                                        rbhk.add("");
                                        roldp.add("");
                                        rsqft.add("");
                                        rproperty_type.add("");
                                        category_name.add("");
                                    }
                                    latitude.add(dataOb.getString("latitude"));
                                    longitude.add(dataOb.getString("longitude"));
                                    offerstatus.add(dataOb.getString("offer_status"));
                                    description.add(dataOb.getString("description"));
                                    category_id.add(dataOb.getString("category_id"));
                                    offer_id.add(dataOb.getString("offer_id"));
                                    JSONArray imgArr = dataOb.getJSONArray("image_array");
                                    int img_count = imgArr.length();
                                    ArrayList<String> imageArr = new ArrayList<String>();
                                    for (int j = 0; j < img_count; j++) {
                                        JSONObject imgOb = imgArr.getJSONObject(j);
                                        imageArr.add(imgOb.getString("image_url"));

                                    }
                                    image.put(dataOb.getString("category_id") + dataOb.getString("offer_id"), imageArr);
                                }
                                OfferListAdapter adapter = new OfferListAdapter(brandname, description,
                                        category_id, offer_id, image, latitude, longitude, offerstatus,
                                        ftypeOffood, ffoodType, festablishment, rtypeofProperty, rpurpose,
                                        rcomplexname, rhouseno, rfurnishstatus, rbhk, roldp, rsqft, rproperty_type, address, category_name);
                                offerList.setAdapter(adapter);
                            } else {
                                Toast.makeText(mContext,
                                        "Oopss! Before you have not added any offer",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception error) {
                            error.printStackTrace();
                            mProgressDialog.dismiss();
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                Toast.makeText(mContext, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(mContext, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(mContext, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences mPrefs = mContext.getSharedPreferences(
                        "LOGIN_DETAIL", mContext.MODE_PRIVATE);
                Map<String, String> params = new HashMap<String, String>();
                params.put("seller_id", mPrefs.getString("USER_ID", ""));

                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnOfferListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class OfferListAdapter extends BaseAdapter {

        LayoutInflater inflater;
        ArrayList<String> NAME, DESCRIPTION, CATEGORYID, OFFERID, LATITUDE, LONGITUDE, OFFERSTATUS,
                FTYPEOFFOOD, FFOODTYPE, FESTABLISHMENT, RTYPEOFPROPERTY, RPURPOSE, RCOMPLEXNAME, RHOUSENO,
                RFURNISHSTATUS, RBHK, ROLDP, RSQFT, RPROPERTY_TYPE, ADDRESS, CATEGORY_NAME;
        HashMap<String, ArrayList<String>> IMAGELIST;
        int m = 0;

        public OfferListAdapter(ArrayList<String> name, ArrayList<String> description,
                                ArrayList<String> category_id,// ArrayList<String> owner_id,
                                ArrayList<String> offer_id, HashMap<String, ArrayList<String>> image,
                                ArrayList<String> latitude, ArrayList<String> longitude, ArrayList<String> offerstatus,
                                ArrayList<String> ftypeOffood, ArrayList<String> ffoodType, ArrayList<String> festablishment,
                                ArrayList<String> rtypeofProperty, ArrayList<String> rpurpose,
                                ArrayList<String> rcomplexname, ArrayList<String> rhouseno, ArrayList<String> rfurnishstatus,
                                ArrayList<String> rbhk, ArrayList<String> roldp, ArrayList<String> rsqft,
                                ArrayList<String> rproperty_type, ArrayList<String> address, ArrayList<String> category_name) {
            // TODO Auto-generated constructor stub
            NAME = name;
            CATEGORYID = category_id;
            DESCRIPTION = description;
            OFFERID = offer_id;
            IMAGELIST = image;
            LATITUDE = latitude;
            LONGITUDE = longitude;
            OFFERSTATUS = offerstatus;
            FTYPEOFFOOD = ftypeOffood;
            FFOODTYPE = ffoodType;
            FESTABLISHMENT = festablishment;
            RTYPEOFPROPERTY = rtypeofProperty;
            RPURPOSE = rpurpose;
            RCOMPLEXNAME = rcomplexname;
            RHOUSENO = rhouseno;
            RFURNISHSTATUS = rfurnishstatus;
            RBHK = rbhk;
            ROLDP = roldp;
            RSQFT = rsqft;
            RPROPERTY_TYPE = rproperty_type;
            ADDRESS = address;
            CATEGORY_NAME = category_name;
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                holder.imvArrow = (ImageView) convertView.findViewById(R.id.imvArrow);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (CATEGORYID.get(position).equalsIgnoreCase("1")) {
                holder.categoryImage.setImageResource(R.drawable.ic_category_restarent);
                holder.categoryName.setText("Restaurant");

                holder.txvRestaruntName.setText(NAME.get(position)+","+FTYPEOFFOOD.get(position));
                holder.txvRestaruntDescription.setText(FFOODTYPE.get(position)+","+DESCRIPTION.get(position));
            } else if (CATEGORYID.get(position).equalsIgnoreCase("3")) {
                holder.categoryImage.setImageResource(R.drawable.ic_category_merchant);
                holder.categoryName.setText("Merchant");
                holder.txvRestaruntName.setText(NAME.get(position)+","+ADDRESS.get(position));
                holder.txvRestaruntDescription.setText(DESCRIPTION.get(position));
            } else if (CATEGORYID.get(position).equalsIgnoreCase("5")) {
                holder.categoryImage.setImageResource(R.drawable.ic_real_estate);
                holder.categoryName.setText("Real Estate");
                holder.txvRestaruntName.setText(RHOUSENO.get(position)+","+NAME.get(position)+","+RCOMPLEXNAME.get(position));
                holder.txvRestaruntDescription.setText(ADDRESS.get(position)+","+RBHK.get(position)+" BHK,"+ROLDP.get(position)+" YEAR OLD,"+RSQFT.get(position)+"SQFT");
            }
            if (IMAGELIST.get(CATEGORYID.get(position) + OFFERID.get(position)).size() == 0 && CATEGORYID.get(position).equalsIgnoreCase("1")) {
                holder.image.setImageResource(R.drawable.ic_no_image_restaruent);
            } else if (IMAGELIST.get(CATEGORYID.get(position) + OFFERID.get(position)).size() == 0 && CATEGORYID.get(position).equalsIgnoreCase("3")) {
                holder.image.setImageResource(R.drawable.ic_no_image_merchant);
            } else if (IMAGELIST.get(CATEGORYID.get(position) + OFFERID.get(position)).size() == 0 && CATEGORYID.get(position).equalsIgnoreCase("5")) {
                holder.image.setImageResource(R.drawable.ic_no_image_real_estate);
            } else {
                Picasso.with(getActivity())
                        .load(IMAGELIST.get(CATEGORYID.get(position) + OFFERID.get(position)).get(0))
                        .into(holder.image);
            }
            holder.txvRestaruntName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callOffer(position);
                }
            });

            holder.txvRestaruntDescription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callOffer(position);
                }
            });
            holder.imvArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callOffer(position);
                }
            });
            holder.txvRestaruntDescription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callOffer(position);
                }
            });

            holder.chatText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), ChatUserHistoryActivity.class);
                    i.putExtra("STATUS", "SELLER");
                    i.putExtra("OFFER_ID", OFFERID.get(position) + "");
                    i.putExtra("CATEGORY_ID", CATEGORYID.get(position) + "");
                    startActivity(i);
                }
            });
            holder.chatImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            return convertView;
        }

        public void callOffer(int position) {
            if (CATEGORYID.get(position).equalsIgnoreCase("1")) {
                Intent i = new Intent(getActivity(), DisplayFoodOfferActivity.class);
                i.putExtra("NAME", NAME.get(position));
                i.putExtra("ADDRESS", ADDRESS.get(position));
                i.putExtra("TYPE_OF_FOOD", FTYPEOFFOOD.get(position));
                i.putExtra("FOOD_TYPE", FFOODTYPE.get(position));
                i.putExtra("LATITUDE", LATITUDE.get(position));
                i.putExtra("LONGITUDE", LONGITUDE.get(position));
                i.putExtra("ESTABLISH_TYPE", FESTABLISHMENT.get(position));
                i.putExtra("OFFER_STATUS", OFFERSTATUS.get(position));
                i.putExtra("OFFER_ID", OFFERID.get(position));
                i.putStringArrayListExtra("IMAGE", IMAGELIST.get(CATEGORYID.get(position) + OFFERID.get(position)));
                startActivityForResult(i, 41);
            } else if (CATEGORYID.get(position).equalsIgnoreCase("3")) {
                Intent i = new Intent(getActivity(), DisplayShopesAndMerchantActivity.class);
                i.putExtra("NAME", NAME.get(position));
                i.putExtra("ADDRESS", ADDRESS.get(position));
                i.putExtra("DESCRIPTION", DESCRIPTION.get(position));
                i.putExtra("LATITUDE", LATITUDE.get(position));
                i.putExtra("LONGITUDE", LONGITUDE.get(position));
                i.putExtra("CATEGORY_NAME", CATEGORY_NAME.get(position));
                i.putExtra("OFFER_STATUS", OFFERSTATUS.get(position));
                i.putExtra("OFFER_ID", OFFERID.get(position));
                i.putStringArrayListExtra("IMAGE", IMAGELIST.get(CATEGORYID.get(position) + OFFERID.get(position)));
                startActivityForResult(i, 42);
            } else if (CATEGORYID.get(position).equalsIgnoreCase("5")) {
                Intent i = new Intent(getActivity(), DisplayRealEstateActivity.class);
                i.putExtra("NAME", NAME.get(position));
                i.putExtra("TYPEOFPROPERTY", RTYPEOFPROPERTY.get(position));
                i.putExtra("PURPOSE", RPURPOSE.get(position));
                i.putExtra("COMPLEX_NAME", RCOMPLEXNAME.get(position));
                i.putExtra("HOUSE_NO", RHOUSENO.get(position));
                i.putExtra("ADDRESS", ADDRESS.get(position));
                i.putExtra("FURNISH_STATUS", RFURNISHSTATUS.get(position));
                i.putExtra("BHK", RBHK.get(position));
                i.putExtra("YEAR_OLD_PROPERTY", ROLDP.get(position));
                i.putExtra("SQFT", RSQFT.get(position));
                i.putExtra("PROPERTY_TYPE", RPROPERTY_TYPE.get(position));
                i.putExtra("LATITUDE", LATITUDE.get(position));
                i.putExtra("LONGITUDE", LONGITUDE.get(position));
                i.putExtra("OFFER_STATUS", OFFERSTATUS.get(position));
                i.putExtra("OFFER_ID", OFFERID.get(position));
                i.putStringArrayListExtra("IMAGE", IMAGELIST.get(CATEGORYID.get(position) + OFFERID.get(position)));
                i.putExtra("DESCRIPTION", DESCRIPTION.get(position));
                startActivityForResult(i, 43);
            }
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return OFFERID.size();
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

        class ViewHolder {
            TextView txvRestaruntName, txvRestaruntDescription, categoryName, chatText;
            ImageView image, categoryImage, chatImage, imvArrow;
        }
    }
}
