package com.analytics.bonjourseller;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.analytics.bonjourseller.infiniteindicator.InfiniteIndicatorLayout;
import com.analytics.bonjourseller.infiniteindicator.slideview.BaseSliderView;
import com.analytics.bonjourseller.infiniteindicator.slideview.DefaultSliderView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OfferDialogActivity extends Activity {
    ImageView ivAccept, ivLike, ivForward, ivNavigate, ivChat, close;
    TextView offerTitle, offerDescription,offerAvailable,offerDistance, txvAccept, txvLike, txvForward, txvNavigate, txvChat;
    public static String o_id = "", o_name = "", o_description = "",
            o_distance = "", o_involve_people = "", current_lat = "",
            current_log = "", offer_lat = "", offer_long = "", seller_id = "", category_id = "";
    public static ArrayList<String> forward_id, forward_name, checkbox_val,
            image_url;
    GPSTracker gps;
    Dialog personDialog;
    InfiniteIndicatorLayout viewPager;
    ArrayList<PageInfo> viewInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_dialog);
        offerTitle = (TextView) findViewById(R.id.txvOfferTitle);
        offerDescription = (TextView) findViewById(R.id.txvOfferDescription);
        offerAvailable=(TextView) findViewById(R.id.txvOfferAvialable);
        offerDistance=(TextView) findViewById(R.id.txvOfferDistance);
        ivAccept = (ImageView) findViewById(R.id.imvAccept);
        ivLike = (ImageView) findViewById(R.id.imvLike);
        ivForward = (ImageView) findViewById(R.id.imvForward);
        ivNavigate = (ImageView) findViewById(R.id.imvNavigate);
        ivChat = (ImageView) findViewById(R.id.imvChat);
        close = (ImageView) findViewById(R.id.imvClose);

        txvAccept = (TextView) findViewById(R.id.txvAccept);
        txvLike = (TextView) findViewById(R.id.txvLike);
        txvForward = (TextView) findViewById(R.id.txvForward);
        txvNavigate = (TextView) findViewById(R.id.txvNavigate);
        txvChat = (TextView) findViewById(R.id.txvChat);
        viewPager = (InfiniteIndicatorLayout) findViewById(R.id.vpHomeSlider);

        Intent i = getIntent();
        category_id = i.getStringExtra("CATEGORY_ID");
        o_id = i.getStringExtra("OFFER_ID");
        o_name = i.getStringExtra("OFFER_NAME");
        o_distance = i.getStringExtra("OFFER_DISTANCE");
        o_description = i.getStringExtra("OFFER_DESCRIPTION");
        o_involve_people = i.getStringExtra("INVOLVE_PEOPLE");
        current_lat = i.getStringExtra("CURRENT_LATITUDE");
        current_log = i.getStringExtra("CURRENT_LONGITUDE");
        offer_lat = i.getStringExtra("OFFER_LATITUDE");
        offer_long = i.getStringExtra("OFFER_LONGITUDE");
        seller_id = i.getStringExtra("SELLER_ID");

        image_url = i.getStringArrayListExtra("IMAGE");
        Log.e("image", "imageurl:" + image_url);
        offerTitle.setText(o_name);
        offerDescription.setText(o_description);
        offerAvailable.setText("Offer available into "+o_involve_people+" people");
offerDistance.setText(o_distance+" KM");

        viewInfos = new ArrayList<PageInfo>();

        for (int j = 0; j < image_url.size(); j++) {
            viewInfos.add(new PageInfo("" + j, image_url.get(j)));
        }

        for (PageInfo name : viewInfos) {
            DefaultSliderView textSliderView = new DefaultSliderView(this);

            try {
                textSliderView.image(name.getUrl()).setScaleType(BaseSliderView.ScaleType.Fit);
                textSliderView.getBundle()
                        .putString("extra", name.getData());
                viewPager.addSlider(textSliderView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        viewPager.setIndicatorPosition(InfiniteIndicatorLayout.IndicatorPosition.Center_Bottom);
        viewPager.startAutoScroll();


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ivAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivAccept.setImageResource(R.drawable.ic_blue_accept);
                ivLike.setImageResource(R.drawable.ic_gray_like);
                ivForward.setImageResource(R.drawable.ic_gray_forward);
                ivNavigate.setImageResource(R.drawable.ic_gray_navigate);
                ivChat.setImageResource(R.drawable.ic_gray_chat);

                txvAccept.setTextColor(Color.parseColor("#005e95"));
                txvLike.setTextColor(Color.parseColor("#8a8787"));
                txvForward.setTextColor(Color.parseColor("#8a8787"));
                txvNavigate.setTextColor(Color.parseColor("#8a8787"));
                txvChat.setTextColor(Color.parseColor("#8a8787"));
                acceptOffer();
            }
        });
        txvAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivAccept.setImageResource(R.drawable.ic_blue_accept);
                ivLike.setImageResource(R.drawable.ic_gray_like);
                ivForward.setImageResource(R.drawable.ic_gray_forward);
                ivNavigate.setImageResource(R.drawable.ic_gray_navigate);
                ivChat.setImageResource(R.drawable.ic_gray_chat);

                txvAccept.setTextColor(Color.parseColor("#005e95"));
                txvLike.setTextColor(Color.parseColor("#8a8787"));
                txvForward.setTextColor(Color.parseColor("#8a8787"));
                txvNavigate.setTextColor(Color.parseColor("#8a8787"));
                txvChat.setTextColor(Color.parseColor("#8a8787"));
                acceptOffer();
            }
        });
        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivAccept.setImageResource(R.drawable.ic_gray_accept);
                ivLike.setImageResource(R.drawable.ic_blue_like);
                ivForward.setImageResource(R.drawable.ic_gray_forward);
                ivNavigate.setImageResource(R.drawable.ic_gray_navigate);
                ivChat.setImageResource(R.drawable.ic_gray_chat);

                txvAccept.setTextColor(Color.parseColor("#8a8787"));
                txvLike.setTextColor(Color.parseColor("#005e95"));
                txvForward.setTextColor(Color.parseColor("#8a8787"));
                txvNavigate.setTextColor(Color.parseColor("#8a8787"));
                txvChat.setTextColor(Color.parseColor("#8a8787"));
                likeOffer();
            }
        });
        txvLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivAccept.setImageResource(R.drawable.ic_gray_accept);
                ivLike.setImageResource(R.drawable.ic_blue_like);
                ivForward.setImageResource(R.drawable.ic_gray_forward);
                ivNavigate.setImageResource(R.drawable.ic_gray_navigate);
                ivChat.setImageResource(R.drawable.ic_gray_chat);

                txvAccept.setTextColor(Color.parseColor("#8a8787"));
                txvLike.setTextColor(Color.parseColor("#005e95"));
                txvForward.setTextColor(Color.parseColor("#8a8787"));
                txvNavigate.setTextColor(Color.parseColor("#8a8787"));
                txvChat.setTextColor(Color.parseColor("#8a8787"));
                likeOffer();
            }
        });
        ivForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivAccept.setImageResource(R.drawable.ic_gray_accept);
                ivLike.setImageResource(R.drawable.ic_gray_like);
                ivForward.setImageResource(R.drawable.ic_blue_forward);
                ivNavigate.setImageResource(R.drawable.ic_gray_navigate);
                ivChat.setImageResource(R.drawable.ic_gray_chat);

                txvAccept.setTextColor(Color.parseColor("#8a8787"));
                txvLike.setTextColor(Color.parseColor("#8a8787"));
                txvForward.setTextColor(Color.parseColor("#005e95"));
                txvNavigate.setTextColor(Color.parseColor("#8a8787"));
                txvChat.setTextColor(Color.parseColor("#8a8787"));
                userForwardList();
            }
        });
        txvForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivAccept.setImageResource(R.drawable.ic_gray_accept);
                ivLike.setImageResource(R.drawable.ic_gray_like);
                ivForward.setImageResource(R.drawable.ic_blue_forward);
                ivNavigate.setImageResource(R.drawable.ic_gray_navigate);
                ivChat.setImageResource(R.drawable.ic_gray_chat);

                txvAccept.setTextColor(Color.parseColor("#8a8787"));
                txvLike.setTextColor(Color.parseColor("#8a8787"));
                txvForward.setTextColor(Color.parseColor("#005e95"));
                txvNavigate.setTextColor(Color.parseColor("#8a8787"));
                txvChat.setTextColor(Color.parseColor("#8a8787"));
                userForwardList();
            }
        });
        ivNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivAccept.setImageResource(R.drawable.ic_gray_accept);
                ivLike.setImageResource(R.drawable.ic_gray_like);
                ivForward.setImageResource(R.drawable.ic_gray_forward);
                ivNavigate.setImageResource(R.drawable.ic_blue_navigate);
                ivChat.setImageResource(R.drawable.ic_gray_chat);

                txvAccept.setTextColor(Color.parseColor("#8a8787"));
                txvLike.setTextColor(Color.parseColor("#8a8787"));
                txvForward.setTextColor(Color.parseColor("#8a8787"));
                txvNavigate.setTextColor(Color.parseColor("#005e95"));
                txvChat.setTextColor(Color.parseColor("#8a8787"));
                try {
                    gps = new GPSTracker(OfferDialogActivity.this);
                    if (gps.canGetLocation()) {
                        Log.e("latlong",
                                "" + gps.getLatitude() + gps.getLongitude());

                        Intent intent = new Intent(
                                android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?saddr="
                                        + gps.getLatitude() + ","
                                        + gps.getLongitude() + "&daddr="
                                        + offer_lat + "," + offer_long));
                        intent.setClassName("com.google.android.apps.maps",
                                "com.google.android.maps.MapsActivity");
                        startActivity(intent);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        txvNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivAccept.setImageResource(R.drawable.ic_gray_accept);
                ivLike.setImageResource(R.drawable.ic_gray_like);
                ivForward.setImageResource(R.drawable.ic_gray_forward);
                ivNavigate.setImageResource(R.drawable.ic_blue_navigate);
                ivChat.setImageResource(R.drawable.ic_gray_chat);

                txvAccept.setTextColor(Color.parseColor("#8a8787"));
                txvLike.setTextColor(Color.parseColor("#8a8787"));
                txvForward.setTextColor(Color.parseColor("#8a8787"));
                txvNavigate.setTextColor(Color.parseColor("#005e95"));
                txvChat.setTextColor(Color.parseColor("#8a8787"));
                try {
                    gps = new GPSTracker(OfferDialogActivity.this);
                    if (gps.canGetLocation()) {
                        Log.e("latlong",
                                "" + gps.getLatitude() + gps.getLongitude());

                        Intent intent = new Intent(
                                android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?saddr="
                                        + gps.getLatitude() + ","
                                        + gps.getLongitude() + "&daddr="
                                        + offer_lat + "," + offer_long));
                        intent.setClassName("com.google.android.apps.maps",
                                "com.google.android.maps.MapsActivity");
                        startActivity(intent);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        ivChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivAccept.setImageResource(R.drawable.ic_gray_accept);
                ivLike.setImageResource(R.drawable.ic_gray_like);
                ivForward.setImageResource(R.drawable.ic_gray_forward);
                ivNavigate.setImageResource(R.drawable.ic_gray_navigate);
                ivChat.setImageResource(R.drawable.ic_blue_chat);

                txvAccept.setTextColor(Color.parseColor("#8a8787"));
                txvLike.setTextColor(Color.parseColor("#8a8787"));
                txvForward.setTextColor(Color.parseColor("#8a8787"));
                txvNavigate.setTextColor(Color.parseColor("#8a8787"));
                txvChat.setTextColor(Color.parseColor("#005e95"));
                SharedPreferences mPrefs = getSharedPreferences(
                        "LOGIN_DETAIL", MODE_PRIVATE);


                Intent i = new Intent(OfferDialogActivity.this,
                        ChatRoomActivity.class);
                i.putExtra("STATUS", "USER");
                i.putExtra("OFFER_ID", o_id);
                i.putExtra("CATEGORY_ID", category_id);
                i.putExtra("USER_ID", mPrefs.getString("USER_ID", ""));
                i.putExtra("SELLER_ID", seller_id);
                startActivity(i);
            }
        });
        txvChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivAccept.setImageResource(R.drawable.ic_gray_accept);
                ivLike.setImageResource(R.drawable.ic_gray_like);
                ivForward.setImageResource(R.drawable.ic_gray_forward);
                ivNavigate.setImageResource(R.drawable.ic_gray_navigate);
                ivChat.setImageResource(R.drawable.ic_blue_chat);

                txvAccept.setTextColor(Color.parseColor("#8a8787"));
                txvLike.setTextColor(Color.parseColor("#8a8787"));
                txvForward.setTextColor(Color.parseColor("#8a8787"));
                txvNavigate.setTextColor(Color.parseColor("#8a8787"));
                txvChat.setTextColor(Color.parseColor("#005e95"));
                SharedPreferences mPrefs = getSharedPreferences(
                        "LOGIN_DETAIL", MODE_PRIVATE);


                Intent i = new Intent(OfferDialogActivity.this,
                        ChatRoomActivity.class);
                i.putExtra("STATUS", "USER");
                i.putExtra("OFFER_ID", o_id);
                i.putExtra("CATEGORY_ID", category_id);
                i.putExtra("USER_ID", mPrefs.getString("USER_ID", ""));
                i.putExtra("SELLER_ID", seller_id);
                startActivity(i);
            }
        });
    }

    public void userForwardList() {

        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "all_bonjour_user.php";
        Log.e("url", url + "");
        final ProgressDialog mProgressDialog = new ProgressDialog(OfferDialogActivity.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("all_bonjour_user res", "" + response);
                        forward_id = new ArrayList<String>();
                        forward_name = new ArrayList<String>();
                        checkbox_val = new ArrayList<String>();
                        try {

                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response
                                    .toString());

                            String msg = object.getString("msg");
                            if (msg.equalsIgnoreCase("Success")) {
                                JSONArray dataArr = object.getJSONArray("data");
                                for (int i = 0; i < dataArr.length(); i++) {
                                    JSONObject dataOb = dataArr.getJSONObject(i);
                                    forward_name.add(dataOb.getString("name"));
                                    forward_id.add(dataOb.getString("id"));
                                    checkbox_val.add("false");
                                }

                                personDialog = new Dialog(OfferDialogActivity.this);
                                personDialog.setContentView(R.layout.dialog_select_person);

                                ListView itemlist = (ListView) personDialog
                                        .findViewById(R.id.lsvSelectPersonList);
                                TextView shareViaOtherApp = (TextView) personDialog
                                        .findViewById(R.id.txvShareViaOtherApp);
                                TextView shareLocation = (TextView) personDialog
                                        .findViewById(R.id.txvShareLocation);
                                shareViaOtherApp.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
                                        String shareBody = "Here is the Bonjour Apps checkout this apps and enjoy best offer for you https://play.google.com/store/apps/details?id=com.grokkt.android.bonjour&hl=en";
                                        Intent sharingIntent = new Intent(
                                                android.content.Intent.ACTION_SEND);
                                        sharingIntent.setType("text/plain");
                                        sharingIntent.putExtra(
                                                android.content.Intent.EXTRA_SUBJECT,
                                                "Checkout the best Apps!");
                                        sharingIntent.putExtra(
                                                android.content.Intent.EXTRA_TEXT,
                                                shareBody);
                                        startActivity(Intent.createChooser(
                                                sharingIntent, "Share link to.."));
                                    }
                                });
                                ForwardUserAdapter adapter = new ForwardUserAdapter(
                                        forward_id, forward_name);
                                itemlist.setAdapter(adapter);
                                shareLocation.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
                                        for (int i = 0; i < checkbox_val.size(); i++) {
                                            Log.e("checkbox value", "value:"
                                                    + checkbox_val.get(i));
                                        }
                                        sendForwardList();
                                    }
                                });
                                personDialog.show();

                            } else {
                                Toast.makeText(
                                        OfferDialogActivity.this,
                                        "Sorry! we are stuff to fetching data. \n Please try again!",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(
                                    OfferDialogActivity.this,
                                    "Sorry! we are stuff to fetching data. \n Please try again!",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();

                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(OfferDialogActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(OfferDialogActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences mPrefs = getSharedPreferences("LOGIN_DETAIL",
                        MODE_PRIVATE);
                String user_id = mPrefs.getString("USER_ID", "");
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", user_id);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);


    }


    public class ForwardUserAdapter extends BaseAdapter {

        LayoutInflater inflater;
        ArrayList<String> forwardUserID, forwardUserName;

        int m = 0;

        public ForwardUserAdapter(ArrayList<String> forwardUserID,
                                  ArrayList<String> forwardUserName) {
            // TODO Auto-generated constructor stub
            this.forwardUserID = forwardUserID;
            this.forwardUserName = forwardUserName;

            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        class ViewHolder {
            TextView txvSelectPersonName;
            CheckBox chbSelectPerson;

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
                        R.layout.row_select_person, parent, false);
                holder = new ViewHolder();
                holder.txvSelectPersonName = (TextView) convertView
                        .findViewById(R.id.txvSelectPersonName);
                holder.chbSelectPerson = (CheckBox) convertView
                        .findViewById(R.id.chbSelectPerson);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.chbSelectPerson
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            // TODO Auto-generated method stub
                            checkbox_val.set(position, isChecked + "");
                        }
                    });

            holder.txvSelectPersonName.setText(forwardUserName.get(position));
            return convertView;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return forwardUserID.size();
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

    public void sendForwardList() {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "forward_offer.php";
        Log.e("url", url + "");
        final ProgressDialog mProgressDialog = new ProgressDialog(OfferDialogActivity.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("forward_offer response", "" + response);
                        try {
                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());

                            String msg = object.getString("msg");
                            if (msg.equalsIgnoreCase("Success")) {
                                Toast.makeText(OfferDialogActivity.this,
                                        "Thank you! Your offer Forwarded successfully",
                                        Toast.LENGTH_SHORT).show();
                                personDialog.dismiss();
                            } else {
                                Toast.makeText(
                                        OfferDialogActivity.this,
                                        "Sorry! your offer not Forwarded. \n Please try again!",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception error) {
                            mProgressDialog.dismiss();
                            error.printStackTrace();
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                Toast.makeText(OfferDialogActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(OfferDialogActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(OfferDialogActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(OfferDialogActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences mPrefs = getSharedPreferences("LOGIN_DETAIL",
                        MODE_PRIVATE);
                String user_id = mPrefs.getString("USER_ID", "");

                int cnt = 0;
                String all_id = "";
                for (int i = 0; i <= checkbox_val.size() - 1; i++) {
                    if (checkbox_val.get(i).equalsIgnoreCase("true")) {
                        if (cnt == 0) {
                            all_id = forward_id.get(i);
                        } else {
                            all_id = all_id + "|" + forward_id.get(i);
                        }
                        cnt++;
                    }
                }
                Log.e("all id", "id:" + all_id);

                Map<String, String> params = new HashMap<String, String>();
                params.put("offer_id", o_id);
                params.put("userid", "" + user_id);
                params.put("to_user_id", "" + all_id);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void acceptOffer() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "accept_offer.php";
        Log.e("url", url + "");
        final ProgressDialog mProgressDialog = new ProgressDialog(OfferDialogActivity.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("accept_offer response", "" + response);
                        try {
                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());

                            String msg = object.getString("msg");
                            if (msg.equalsIgnoreCase("Success")) {
                                Toast.makeText(OfferDialogActivity.this,
                                        "Thank you! Your offer accepted successfully",
                                        Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(
                                        OfferDialogActivity.this,
                                        "Sorry! your offer not accepted. \n Please try again!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception error) {
                            mProgressDialog.dismiss();
                            error.printStackTrace();
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                Toast.makeText(OfferDialogActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(OfferDialogActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(OfferDialogActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(OfferDialogActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences mPrefs = getSharedPreferences("LOGIN_DETAIL",
                        MODE_PRIVATE);
                String user_id = mPrefs.getString("USER_ID", "");
                Map<String, String> params = new HashMap<String, String>();

                params.put("offer_id", o_id);
                params.put("userid", "" + user_id);
                params.put("no_of_people", o_involve_people);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }


    public void likeOffer() {

        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "like_offer.php";
        Log.e("url", url + "");
        final ProgressDialog mProgressDialog = new ProgressDialog(OfferDialogActivity.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("like_offer response", "" + response);
                        try {
                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());

                            String msg = object.getString("msg");
                            if (msg.equalsIgnoreCase("Success")) {
                                Toast.makeText(OfferDialogActivity.this,
                                        "Thank you! offer liked successfully",
                                        Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(OfferDialogActivity.this,
                                        "Sorry! offer not liked. \n Please try again!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception error) {
                            mProgressDialog.dismiss();
                            error.printStackTrace();
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                Toast.makeText(OfferDialogActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(OfferDialogActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(OfferDialogActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(OfferDialogActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences mPrefs = getSharedPreferences("LOGIN_DETAIL",
                        MODE_PRIVATE);
                String user_id = mPrefs.getString("USER_ID", "");
                Map<String, String> params = new HashMap<String, String>();

                params.put("offer_id", o_id);
                params.put("userid", "" + user_id);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

}
