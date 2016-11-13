package com.analytics.bonjourseller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DisplayRealEstateActivity extends AppCompatActivity {
    TextView txvDisplayPostingPurposeValue, txvDisplayTypeOfPropertyValue, txvDisplayPropertyValue,
            txvDisplayNameOfSocietyValue, txvDisplayHouseNoValue, txvDisplayAddressValue,
            txvDisplayBHKValue, txvDisplayHowYearOldPropertyValue, txvDisplayAreaOfSQFTValue, txvDisplayFurnishStatusValue;
    TextView txvDisplayNameOfSociety;
    Button btnRunOffer, btnDisplayOffer, btnEdit, btnStop, btnDelete;
    ImageView imvEdit, imvStop, imvDelete;
    LinearLayout llbhk, llHouseNo;
    View vbhk, viewHouseNo;
    UploagImageListAdapter ImageAdapter;
    ExpandableHeightGridView ehgDisplayUploadedImage;
    static String offer_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_real_estate);

        txvDisplayNameOfSociety = (TextView) findViewById(R.id.txvDisplayNameOfSociety);
        txvDisplayPostingPurposeValue = (TextView) findViewById(R.id.txvDisplayPostingPurposeValue);
        txvDisplayTypeOfPropertyValue = (TextView) findViewById(R.id.txvDisplayTypeOfPropertyValue);
        txvDisplayPropertyValue = (TextView) findViewById(R.id.txvDisplayPropertyValue);
        txvDisplayNameOfSocietyValue = (TextView) findViewById(R.id.txvDisplayNameOfSocietyValue);
        txvDisplayHouseNoValue = (TextView) findViewById(R.id.txvDisplayHouseNoValue);
        txvDisplayAddressValue = (TextView) findViewById(R.id.txvDisplayAddressValue);
        txvDisplayBHKValue = (TextView) findViewById(R.id.txvDisplayBHKValue);
        txvDisplayHowYearOldPropertyValue = (TextView) findViewById(R.id.txvDisplayHowYearOldPropertyValue);
        txvDisplayAreaOfSQFTValue = (TextView) findViewById(R.id.txvDisplayAreaOfSQFTValue);
        txvDisplayFurnishStatusValue = (TextView) findViewById(R.id.txvDisplayFurnishStatusValue);
        btnRunOffer = (Button) findViewById(R.id.btnRunOffer);
        btnDisplayOffer = (Button) findViewById(R.id.btnDisplayOffer);
        btnEdit = (Button) findViewById(R.id.btnEdit);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        imvEdit = (ImageView) findViewById(R.id.imvEdit);
        imvStop = (ImageView) findViewById(R.id.imvStop);
        imvDelete = (ImageView) findViewById(R.id.imvDelete);
        llbhk = (LinearLayout) findViewById(R.id.llBHK);
        llHouseNo = (LinearLayout) findViewById(R.id.llHouseNo);
        vbhk = findViewById(R.id.viewBHK);
        viewHouseNo = findViewById(R.id.viewHouseNo);
        ehgDisplayUploadedImage = (ExpandableHeightGridView) findViewById(R.id.ehgDisplayUploadedImage);
        ehgDisplayUploadedImage.setExpanded(true);

        Intent i = getIntent();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(i.getStringExtra("NAME").toUpperCase() + " Details");
        final String name = i.getStringExtra("NAME");
        final String typeofproperty = i.getStringExtra("TYPEOFPROPERTY");
        final String purpose = i.getStringExtra("PURPOSE");
        final String complex_name = i.getStringExtra("COMPLEX_NAME");
        final String Houseno = i.getStringExtra("HOUSE_NO");
        final String address = i.getStringExtra("ADDRESS");
        final String furnish_status = i.getStringExtra("FURNISH_STATUS");
        final String bhk = i.getStringExtra("BHK");
        final String yearoldproperty = i.getStringExtra("YEAR_OLD_PROPERTY");
        final String sqft = i.getStringExtra("SQFT");
        final String property_type = i.getStringExtra("PROPERTY_TYPE");
        final String latitude = i.getStringExtra("LATITUDE");
        final String longitude = i.getStringExtra("LONGITUDE");
        final String offer_status = i.getStringExtra("OFFER_STATUS");
        final ArrayList<String> ImageList = i.getStringArrayListExtra("IMAGE");
        final String description = i.getStringExtra("DESCRIPTION");
        offer_id = i.getStringExtra("OFFER_ID");

        txvDisplayPostingPurposeValue.setText(purpose);
        txvDisplayTypeOfPropertyValue.setText(typeofproperty);
        txvDisplayPropertyValue.setText(property_type);
        txvDisplayHouseNoValue.setText(Houseno);
        txvDisplayAddressValue.setText(address);
        txvDisplayBHKValue.setText(bhk);
        txvDisplayHowYearOldPropertyValue.setText(yearoldproperty);
        txvDisplayAreaOfSQFTValue.setText(sqft);
        txvDisplayFurnishStatusValue.setText(furnish_status);


        ImageAdapter = new UploagImageListAdapter(ImageList);
        ehgDisplayUploadedImage.setAdapter(ImageAdapter);

        if (typeofproperty.equalsIgnoreCase("Resident")) {
            txvDisplayNameOfSocietyValue.setText(name);
            llbhk.setVisibility(View.VISIBLE);
            vbhk.setVisibility(View.VISIBLE);
            llHouseNo.setVisibility(View.VISIBLE);
            viewHouseNo.setVisibility(View.VISIBLE);
        } else {
            txvDisplayNameOfSociety.setText("Complex Name:");
            txvDisplayNameOfSocietyValue.setText(complex_name);
            llbhk.setVisibility(View.GONE);
            vbhk.setVisibility(View.GONE);
            llHouseNo.setVisibility(View.GONE);
            viewHouseNo.setVisibility(View.GONE);
        }

        txvDisplayPostingPurposeValue.setText(purpose);
        txvDisplayTypeOfPropertyValue.setText(typeofproperty);
        txvDisplayPropertyValue.setText(property_type);
        txvDisplayNameOfSocietyValue.setText(name);
        txvDisplayHouseNoValue.setText(Houseno);
        txvDisplayAddressValue.setText(address);
        txvDisplayBHKValue.setText(bhk);
        txvDisplayHowYearOldPropertyValue.setText(yearoldproperty);
        txvDisplayAreaOfSQFTValue.setText(sqft);
        txvDisplayFurnishStatusValue.setText(furnish_status);

        if (!offer_status.equalsIgnoreCase("tag")) {
            btnRunOffer.setBackgroundResource(R.drawable.set_add_offer_location_background);
            btnDisplayOffer.setBackgroundResource(R.drawable.set_add_offer_location_border);
        } else {
            btnDisplayOffer.setBackgroundResource(R.drawable.set_add_offer_location_background);
            btnRunOffer.setBackgroundResource(R.drawable.set_add_offer_location_border);
        }
        btnDisplayOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetOfferTag();
            }
        });
        btnRunOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(DisplayRealEstateActivity.this,RunOfferActivity.class);
                i.putExtra("OFFER_ID",offer_id);
                i.putExtra("CATEGORY","REAL_ESATE");
                startActivityForResult(i,33);
            }
        });
        imvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DisplayRealEstateActivity.this, PostRealEstateActivity.class);
                i.putExtra("TYPE", "EDIT");
                i.putExtra("NAME", name);
                i.putExtra("TYPEOFPROPERTY", typeofproperty);
                i.putExtra("PURPOSE", purpose);
                i.putExtra("COMPLEX_NAME", complex_name);
                i.putExtra("HOUSE_NO", Houseno);
                i.putExtra("ADDRESS", address);
                i.putExtra("FURNISH_STATUS", furnish_status);
                i.putExtra("BHK", bhk);
                i.putExtra("YEAR_OLD_PROPERTY", yearoldproperty);
                i.putExtra("SQFT", sqft);
                i.putExtra("PROPERTY_TYPE", property_type);
                i.putExtra("LATITUDE", latitude);
                i.putExtra("LONGITUDE", longitude);
                i.putExtra("OFFER_STATUS", offer_status);
                i.putExtra("OFFER_ID", offer_id);
                i.putStringArrayListExtra("IMAGE", ImageList);
                i.putExtra("DESCRIPTION", description);
                startActivityForResult(i, 39);
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DisplayRealEstateActivity.this, PostRealEstateActivity.class);
                i.putExtra("TYPE", "EDIT");
                i.putExtra("NAME", name);
                i.putExtra("TYPEOFPROPERTY", typeofproperty);
                i.putExtra("PURPOSE", purpose);
                i.putExtra("COMPLEX_NAME", complex_name);
                i.putExtra("HOUSE_NO", Houseno);
                i.putExtra("ADDRESS", address);
                i.putExtra("FURNISH_STATUS", furnish_status);
                i.putExtra("BHK", bhk);
                i.putExtra("YEAR_OLD_PROPERTY", yearoldproperty);
                i.putExtra("SQFT", sqft);
                i.putExtra("PROPERTY_TYPE", property_type);
                i.putExtra("LATITUDE", latitude);
                i.putExtra("LONGITUDE", longitude);
                i.putExtra("OFFER_STATUS", offer_status);
                i.putExtra("OFFER_ID", offer_id);
                i.putStringArrayListExtra("IMAGE", ImageList);
                i.putExtra("DESCRIPTION", description);
                startActivityForResult(i, 39);
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetOfferDelete();
            }
        });
        imvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetOfferDelete();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetOfferStop();
            }
        });
        imvStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetOfferStop();
            }
        });
    }
    public void SetOfferTag() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "add_tag_seller.php";
        Log.e("url", url + "");
        final ProgressDialog mProgressDialog = new ProgressDialog(DisplayRealEstateActivity.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("response", "" + response);
                        try {
                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());
                            Log.e("2", "2");

                            String msg = object.getString("msg");
                            if (msg.equalsIgnoreCase("Success")) {

                                Toast.makeText(getApplicationContext(),
                                        "Your offer tagged successfully",
                                        Toast.LENGTH_LONG).show();
                                btnDisplayOffer.setBackgroundResource(R.drawable.set_add_offer_location_background);
                                btnRunOffer.setBackgroundResource(R.drawable.set_add_offer_location_border);

                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Oops! Your offer not deleted please try again",
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception error) {
                            mProgressDialog.dismiss();
                            error.printStackTrace();
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                Toast.makeText(DisplayRealEstateActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(DisplayRealEstateActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(DisplayRealEstateActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(DisplayRealEstateActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("seller_id", getSharedPreferences(
                        "LOGIN_DETAIL", MODE_PRIVATE).getString("USER_ID", ""));
                params.put("offer_id", "" + offer_id);
                params.put("category_id", "5");
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            if(requestCode==33){
                Intent i = new Intent();
                i.putExtra("msg", "Success");
                setResult(43, i);
                finish();
            }
            if(requestCode==39){
                Intent i = new Intent();
                i.putExtra("msg", "Success");
                setResult(43, i);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        i.putExtra("msg", "Success");
        setResult(43, i);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                Intent i = new Intent();
                i.putExtra("msg", "Success");
                setResult(43, i);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void SetOfferDelete() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "delete_offer.php";
        Log.e("url", url + "");
        final ProgressDialog mProgressDialog = new ProgressDialog(DisplayRealEstateActivity.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("response", "" + response);
                        try {
                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());
                            Log.e("2", "2");

                            String msg = object.getString("msg");
                            if (msg.equalsIgnoreCase("Success")) {

                                Toast.makeText(getApplicationContext(),
                                        "Your offer deleted successfully",
                                        Toast.LENGTH_LONG).show();
                                Intent i = new Intent();
                                i.putExtra("msg", "Success");
                                setResult(43, i);
                                finish();

                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Oops! Your offer not deleted please try again",
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception error) {
                            mProgressDialog.dismiss();
                            error.printStackTrace();
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                Toast.makeText(DisplayRealEstateActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(DisplayRealEstateActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(DisplayRealEstateActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(DisplayRealEstateActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", getSharedPreferences(
                        "LOGIN_DETAIL", MODE_PRIVATE).getString("USER_ID", ""));
                params.put("offer_id", "" + offer_id);
                params.put("category_id", "5");
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void SetOfferStop() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "stop_offer.php";
        Log.e("url", url + "");
        final ProgressDialog mProgressDialog = new ProgressDialog(DisplayRealEstateActivity.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("response", "" + response);
                        try {
                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());
                            Log.e("2", "2");

                            String msg = object.getString("msg");
                            if (msg.equalsIgnoreCase("Success")) {

                                Toast.makeText(getApplicationContext(),
                                        "Your offer stopped successfully",
                                        Toast.LENGTH_LONG).show();


                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Oops! Your offer not stopped please try again",
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception error) {
                            mProgressDialog.dismiss();
                            error.printStackTrace();
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                Toast.makeText(DisplayRealEstateActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(DisplayRealEstateActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(DisplayRealEstateActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(DisplayRealEstateActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", getSharedPreferences(
                        "LOGIN_DETAIL", MODE_PRIVATE).getString("USER_ID", ""));
                params.put("offer_id", "" + offer_id);
                params.put("category_id", "5");
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public class UploagImageListAdapter extends BaseAdapter {
        LayoutInflater inflater;
        Context context;
        ArrayList<String> ImageList;

        public UploagImageListAdapter(ArrayList<String> imagelist) {
            ImageList = imagelist;
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        class ViewHolder {
            ImageView imageGridview;
        }

        @Override
        public int getCount() {
            return ImageList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.row_gridview_image, null);
                holder = new ViewHolder();
                holder.imageGridview = (ImageView) convertView.findViewById(R.id.imvGridImage);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            try {
                Picasso.with(DisplayRealEstateActivity.this)
                        .load(ImageList.get(position))
                        .into(holder.imageGridview);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return convertView;
        }

    }
}
