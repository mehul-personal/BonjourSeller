package com.analytics.bonjourseller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.gcm.GoogleCloudMessaging;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GcmRegistrationAsyncTask extends AsyncTask<Void, Void, String> {
    // TODO: change to your own sender ID to Google Developers Console project number, as per instructions above
    private static final String SENDER_ID = "504692919064";
    static String regId = "";
    // private static Registration regService = null;
    private GoogleCloudMessaging gcm;
    private Context context;
    String device_id, device_Name, user_id;

    public GcmRegistrationAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        SharedPreferences prefers = context.getSharedPreferences(
                "LOGIN_DETAIL", 0);
        user_id = prefers.getString("USER_ID", "");

        String msg = "";
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(context);
            }
             regId = gcm.register(SENDER_ID);
            msg = "Device registered, registration ID=" + regId;

            // You should send the registration ID to your server over HTTP,
            // so it can use GCM/HTTP or CCS to send messages to your app.
            // The request to your server should be authenticated if your app
            // is using accounts.
            // regService.register(regId).execute();
            device_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            device_Name = android.os.Build.MODEL;
            deviceRegister();
//            HttpClient httpclient = new DefaultHttpClient();
//            HttpPost httppost = new HttpPost("http://journey.coderspreview.com/main_webservices");
//            try {
//                // Add your data
//                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
//                        3);
//
//                nameValuePairs
//                        .add(new BasicNameValuePair(
//                                "action",
//                                "DeviceRegistration"));
//                nameValuePairs
//                        .add(new BasicNameValuePair(
//                                "devicename",
//                                device_Name));
//                nameValuePairs
//                        .add(new BasicNameValuePair(
//                                "deviceid",
//                                device_id));
//                nameValuePairs
//                        .add(new BasicNameValuePair(
//                                "type",
//                                "Android"));
//                nameValuePairs
//                        .add(new BasicNameValuePair(
//                                "token",
//                                regId));
//                nameValuePairs
//                        .add(new BasicNameValuePair(
//                                "userid",
//                                user_id));
//
//                httppost.setEntity(new UrlEncodedFormEntity(
//                        nameValuePairs));
//
//                // Execute HTTP Post
//                // Request
//                HttpResponse response = httpclient
//                        .execute(httppost);
//                BufferedReader in = new BufferedReader(
//                        new InputStreamReader(
//                                response.getEntity()
//                                        .getContent()));
//                StringBuffer sb = new StringBuffer(
//                        "");
//                String line = "";
//                while ((line = in
//                        .readLine()) != null) {
//                    sb.append(line);
//                }
//                in.close();
//                Log.e("register device data", ""
//                        + sb.toString());
//                return sb.toString();
//            } catch (Exception e) {
//                Log.e("error on reg device",
//                        "" + e);
//                return "";
//            }
//
        } catch (IOException ex) {
            ex.printStackTrace();
            msg = "Error: " + ex.getMessage();
        }
        return msg;
    }

    @Override
    protected void onPostExecute(String msg) {
        //  Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        Logger.getLogger("REGISTRATION").log(Level.INFO, msg);
    }

    public void deviceRegister() {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "register_your_mobile_device.php";
        Log.e("url", url + "");
//        final ProgressDialog mProgressDialog = new ProgressDialog(context);
//        mProgressDialog.setTitle("");
//        mProgressDialog.setCanceledOnTouchOutside(false);
//        mProgressDialog.setMessage("Please Wait...");
//        mProgressDialog.show();
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("device register respnse", "" + response);
                        try {
//                            mProgressDialog.dismiss();
                          /*  JSONObject object = new JSONObject(response.toString());
                            String msg = object.getString("msg");
                            if (msg.equalsIgnoreCase("Success")) {
                            } else {
                                Toast.makeText(context, "Sorry! your offer not Forwarded. \n Please try again!",
                                        Toast.LENGTH_SHORT).show();
                            }*/
                        } catch (Exception error) {
//                            mProgressDialog.dismiss();
                            error.printStackTrace();
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                Toast.makeText(context, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
//                mProgressDialog.dismiss();

                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(context, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences mPrefs = context.getSharedPreferences("LOGIN_DETAIL",
                        context.MODE_PRIVATE);
                String user_id = mPrefs.getString("USER_ID", "");
                Map<String, String> params = new HashMap<String, String>();
                params.put("Action", "RegisterDevice");
                params.put("device_name", device_Name);
                params.put("device_type", "Android");
                params.put("device_id", device_id);
                params.put("device_token", regId);
                params.put("user_id", user_id);
                params.put("user_type","seller");
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }
}
