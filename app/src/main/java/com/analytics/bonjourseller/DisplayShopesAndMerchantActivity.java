package com.analytics.bonjourseller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class DisplayShopesAndMerchantActivity extends AppCompatActivity {
    TextView txvDisplayBrandNameValue, txvDisplayAddressValue, txvDisplayDescriptionValue, txvDisplayCategoryValue;
    ExpandableHeightGridView ehgDisplayUploadedImage;
    Button btnRunOffer, btnDisplayOffer, btnEdit, btnStop, btnDelete;
    ImageView imvEdit, imvStop, imvDelete;
    UploagImageListAdapter ImageAdapter;
    static String offer_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_shopes_and_merchant);

        txvDisplayBrandNameValue = (TextView) findViewById(R.id.txvDisplayBrandNameValue);
        txvDisplayAddressValue = (TextView) findViewById(R.id.txvDisplayAddressValue);
        txvDisplayDescriptionValue = (TextView) findViewById(R.id.txvDisplayDescriptionValue);
        txvDisplayCategoryValue = (TextView) findViewById(R.id.txvDisplayCategoryValue);
        ehgDisplayUploadedImage = (ExpandableHeightGridView) findViewById(R.id.ehgDisplayUploadedImage);
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
        final String description = i.getStringExtra("DESCRIPTION");
        final String latitude = i.getStringExtra("LATITUDE");
        final String longitude = i.getStringExtra("LONGITUDE");
        final String category_name = i.getStringExtra("CATEGORY_NAME");
        final String offer_status = i.getStringExtra("OFFER_STATUS");
        final ArrayList<String> ImageList = i.getStringArrayListExtra("IMAGE");
        offer_id = i.getStringExtra("OFFER_ID");

        txvDisplayBrandNameValue.setText(name);
        txvDisplayAddressValue.setText(address);
        txvDisplayDescriptionValue.setText(description);
        txvDisplayCategoryValue.setText(category_name);

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
                Intent i=new Intent(DisplayShopesAndMerchantActivity.this,RunOfferActivity.class);
                i.putExtra("OFFER_ID",offer_id);
                i.putExtra("CATEGORY","MERCHANT");
                startActivityForResult(i,32);
            }
        });
        imvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DisplayShopesAndMerchantActivity.this, PostShopesAndMerchantActivity.class);
                i.putExtra("TYPE","EDIT");
                i.putExtra("NAME", name);
                i.putExtra("ADDRESS", address);
                i.putExtra("DESCRIPTION", description);
                i.putExtra("LATITUDE", latitude);
                i.putExtra("LONGITUDE", longitude);
                i.putExtra("CATEGORY_NAME", category_name);
                i.putExtra("OFFER_STATUS", offer_status);
                i.putExtra("OFFER_ID",offer_id);
                i.putStringArrayListExtra("IMAGE", ImageList);
                startActivityForResult(i, 37);
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DisplayShopesAndMerchantActivity.this, PostShopesAndMerchantActivity.class);
                i.putExtra("TYPE","EDIT");
                i.putExtra("NAME", name);
                i.putExtra("ADDRESS", address);
                i.putExtra("DESCRIPTION", description);
                i.putExtra("LATITUDE", latitude);
                i.putExtra("LONGITUDE", longitude);
                i.putExtra("CATEGORY_NAME", category_name);
                i.putExtra("OFFER_STATUS", offer_status);
                i.putExtra("OFFER_ID",offer_id);
                i.putStringArrayListExtra("IMAGE", ImageList);
                startActivityForResult(i, 37);
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
        ImageAdapter = new UploagImageListAdapter(ImageList);
        ehgDisplayUploadedImage.setAdapter(ImageAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                Intent i = new Intent();
                i.putExtra("msg","Success");
                setResult(42, i);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            if(requestCode==32){
                Intent i = new Intent();
                i.putExtra("msg", "Success");
                setResult(42, i);
                finish();
            }
            if(requestCode==37){
                Intent i = new Intent();
                i.putExtra("msg", "Success");
                setResult(42, i);
                finish();
            }
        }
    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        i.putExtra("msg","Success");
        setResult(42, i);
        finish();
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
                Picasso.with(DisplayShopesAndMerchantActivity.this)
                        .load(ImageList.get(position))
                        .into(holder.imageGridview);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return convertView;
        }

    }

    public void SetOfferTag() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "add_tag_seller.php";
        Log.e("url", url + "");
        final ProgressDialog mProgressDialog = new ProgressDialog(DisplayShopesAndMerchantActivity.this);
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
                                        "Your offer Tagged Successfully",
                                        Toast.LENGTH_LONG).show();
                                btnDisplayOffer.setBackgroundResource(R.drawable.set_add_offer_location_background);
                                btnRunOffer.setBackgroundResource(R.drawable.set_add_offer_location_border);

                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Oops! Your offer not Tagged please try again",
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception error) {
                            mProgressDialog.dismiss();
                            error.printStackTrace();
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                Toast.makeText(DisplayShopesAndMerchantActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(DisplayShopesAndMerchantActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(DisplayShopesAndMerchantActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(DisplayShopesAndMerchantActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("seller_id", getSharedPreferences(
                        "LOGIN_DETAIL", MODE_PRIVATE).getString("USER_ID", ""));
                params.put("offer_id", "" + offer_id);
                params.put("category_id", "3");
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
        final ProgressDialog mProgressDialog = new ProgressDialog(DisplayShopesAndMerchantActivity.this);
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
                                setResult(42, i);
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
                                Toast.makeText(DisplayShopesAndMerchantActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(DisplayShopesAndMerchantActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(DisplayShopesAndMerchantActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(DisplayShopesAndMerchantActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", getSharedPreferences(
                        "LOGIN_DETAIL", MODE_PRIVATE).getString("USER_ID", ""));
                params.put("offer_id", "" + offer_id);
                params.put("category_id", "3");
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
        final ProgressDialog mProgressDialog = new ProgressDialog(DisplayShopesAndMerchantActivity.this);
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
                                Toast.makeText(DisplayShopesAndMerchantActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(DisplayShopesAndMerchantActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(DisplayShopesAndMerchantActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(DisplayShopesAndMerchantActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", getSharedPreferences(
                        "LOGIN_DETAIL", MODE_PRIVATE).getString("USER_ID", ""));
                params.put("offer_id", "" + offer_id);
                params.put("category_id", "3");
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }
}
