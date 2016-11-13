package com.analytics.bonjourseller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
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

public class DisplayFoodOfferActivity extends AppCompatActivity {
    TextView txvDisplayBrandNameValue, txvDisplayTypeOfFoodValue, txvDisplayFoodTypeValue,
            txvDisplayTypeOfEstablishmentValue, txvDisplayAddress, txvDisplayAddressValue;
    ExpandableHeightGridView ehgDisplayUploadedImage;
    UploagImageListAdapter ImageAdapter;
    Button btnRunOffer, btnDisplayOffer, btnEdit, btnStop, btnDelete;
    ImageView imvEdit, imvStop, imvDelete;
    static String offer_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_food_offer);

        txvDisplayBrandNameValue = (TextView) findViewById(R.id.txvDisplayBrandNameValue);
        txvDisplayTypeOfFoodValue = (TextView) findViewById(R.id.txvDisplayTypeOfFoodValue);
        txvDisplayFoodTypeValue = (TextView) findViewById(R.id.txvDisplayFoodTypeValue);
        txvDisplayTypeOfEstablishmentValue = (TextView) findViewById(R.id.txvDisplayTypeOfEstablishmentValue);
        ehgDisplayUploadedImage = (ExpandableHeightGridView) findViewById(R.id.ehgDisplayUploadedImage);
        ehgDisplayUploadedImage.setExpanded(true);
        txvDisplayAddress = (TextView) findViewById(R.id.txvDisplayAddress);
        txvDisplayAddressValue = (TextView) findViewById(R.id.txvDisplayAddressValue);
        btnRunOffer = (Button) findViewById(R.id.btnRunOffer);
        btnDisplayOffer = (Button) findViewById(R.id.btnDisplayOffer);
        btnEdit = (Button) findViewById(R.id.btnEdit);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        imvEdit = (ImageView) findViewById(R.id.imvEdit);
        imvStop = (ImageView) findViewById(R.id.imvStop);
        imvDelete = (ImageView) findViewById(R.id.imvDelete);

        Intent i = getIntent();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(i.getStringExtra("NAME").toUpperCase() + " Details");
        final String name = i.getStringExtra("NAME");
        final String address = i.getStringExtra("ADDRESS");
        final String type_of_food = i.getStringExtra("TYPE_OF_FOOD");
        final String food_type = i.getStringExtra("FOOD_TYPE");
        final String latitude = i.getStringExtra("LATITUDE");
        final String longitude = i.getStringExtra("LONGITUDE");
        final String establish_type = i.getStringExtra("ESTABLISH_TYPE");
        final String offer_status = i.getStringExtra("OFFER_STATUS");
        offer_id = i.getStringExtra("OFFER_ID");
        final ArrayList<String> ImageList = i.getStringArrayListExtra("IMAGE");

        txvDisplayBrandNameValue.setText(name);
        txvDisplayTypeOfFoodValue.setText(type_of_food);
        txvDisplayFoodTypeValue.setText(establish_type);
        txvDisplayTypeOfEstablishmentValue.setText(food_type);
        txvDisplayAddressValue.setText(address);

        ImageAdapter = new UploagImageListAdapter(ImageList);
        ehgDisplayUploadedImage.setAdapter(ImageAdapter);

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
                Intent i=new Intent(DisplayFoodOfferActivity.this,RunOfferActivity.class);
                i.putExtra("OFFER_ID",offer_id);
                i.putExtra("CATEGORY","FOOD");
                startActivityForResult(i,31);
            }
        });
        imvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DisplayFoodOfferActivity.this, PostFoodRestaurantActivity.class);
                i.putExtra("TYPE", "EDIT");
                i.putExtra("NAME", name);
                i.putExtra("ADDRESS", address);
                i.putExtra("TYPE_OF_FOOD", type_of_food);
                i.putExtra("FOOD_TYPE", food_type);
                i.putExtra("LATITUDE", latitude);
                i.putExtra("LONGITUDE", longitude);
                i.putExtra("ESTABLISH_TYPE", establish_type);
                i.putExtra("OFFER_STATUS", offer_status);
                i.putExtra("OFFER_ID", offer_id);
                i.putStringArrayListExtra("IMAGE", ImageList);
                startActivityForResult(i, 38);
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DisplayFoodOfferActivity.this, PostFoodRestaurantActivity.class);
                i.putExtra("TYPE", "EDIT");
                i.putExtra("NAME", name);
                i.putExtra("ADDRESS", address);
                i.putExtra("TYPE_OF_FOOD", type_of_food);
                i.putExtra("FOOD_TYPE", food_type);
                i.putExtra("LATITUDE", latitude);
                i.putExtra("LONGITUDE", longitude);
                i.putExtra("ESTABLISH_TYPE", establish_type);
                i.putExtra("OFFER_STATUS", offer_status);
                i.putExtra("OFFER_ID", offer_id);
                i.putStringArrayListExtra("IMAGE", ImageList);
                startActivityForResult(i, 38);
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
        final ProgressDialog mProgressDialog = new ProgressDialog(DisplayFoodOfferActivity.this);
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
                                Toast.makeText(DisplayFoodOfferActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(DisplayFoodOfferActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(DisplayFoodOfferActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(DisplayFoodOfferActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("seller_id", getSharedPreferences(
                        "LOGIN_DETAIL", MODE_PRIVATE).getString("USER_ID", ""));
                params.put("offer_id", "" + offer_id);
                params.put("category_id", "1");
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }
    public void SetOfferDelete() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "delete_offer.php";
        Log.e("url", url + "");
        final ProgressDialog mProgressDialog = new ProgressDialog(DisplayFoodOfferActivity.this);
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
                                i.putExtra("msg","Success");
                                setResult(41, i);
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
                                Toast.makeText(DisplayFoodOfferActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(DisplayFoodOfferActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(DisplayFoodOfferActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(DisplayFoodOfferActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", getSharedPreferences(
                        "LOGIN_DETAIL", MODE_PRIVATE).getString("USER_ID", ""));
                params.put("offer_id", "" + offer_id);
                params.put("category_id", "1");
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
        final ProgressDialog mProgressDialog = new ProgressDialog(DisplayFoodOfferActivity.this);
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
                                Toast.makeText(DisplayFoodOfferActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(DisplayFoodOfferActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(DisplayFoodOfferActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(DisplayFoodOfferActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", getSharedPreferences(
                        "LOGIN_DETAIL", MODE_PRIVATE).getString("USER_ID", ""));
                params.put("offer_id", "" + offer_id);
                params.put("category_id", "1");
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        i.putExtra("msg","Success");
        setResult(41, i);
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            if(requestCode==31){
                Intent i = new Intent();
                i.putExtra("msg", "Success");
                setResult(41, i);
                finish();
            }  if(requestCode==38){
                Intent i = new Intent();
                i.putExtra("msg", "Success");
                setResult(41, i);
                finish();
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                Intent i = new Intent();
                i.putExtra("msg","Success");
                setResult(41, i);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                Picasso.with(DisplayFoodOfferActivity.this)
                        .load(ImageList.get(position))
                        .into(holder.imageGridview);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return convertView;
        }

    }
}
