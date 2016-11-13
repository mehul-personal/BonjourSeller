package com.analytics.bonjourseller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Transformation;

public class ShareUserSavedLocationFragment extends Fragment implements GetLocationUpdates.LocationUpdates {
    ArrayList<String> location_name_list, latitude_list, longitude_list,
            location_id_list;
    ListView savelocationlist;
    SaveLocationDataAdapter adapter;
    public static ArrayList<String> forward_id, forward_name, checkbox_val,
            forward_phone_no;
    public static Dialog picker, picker1;
    public static String selected_user_id = "";
    ArrayList<String> namelist, numberlist;
    Button shareMobileLocation;
    // GPSTracker gps;
    public static String share_type = "";
    private PendingIntent tracking;
    private int START_DELAY = 5;
    private long UPDATE_INTERVAL = 30000;
    private AlarmManager alarms;
    static String selectedtime = "";
    public static ForwardUserAdapter forwardadapter;
    Button AddNewAddress;
    Typeface mediumFont, boldFont, regularFont, semiboldFont;
    public static ListView itemlist;
    private OnShareUserSaveLocationFragmentInteractionListener mListener;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    static Location currentUpdateLocation;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ShareUserSavedLocationFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ShareUserSavedLocationFragment newInstance(String param1, String param2) {
        ShareUserSavedLocationFragment fragment = new ShareUserSavedLocationFragment();
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnShareUserSaveLocationFragmentInteractionListener) {
            mListener = (OnShareUserSaveLocationFragmentInteractionListener) context;
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

    @Override
    public void handleLocationUpdatesCallback(Location location) {
        currentUpdateLocation = location;
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
    public interface OnShareUserSaveLocationFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_share_saved_location, container, false);

        alarms = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        savelocationlist = (ListView) view.findViewById(R.id.lvSaveLocation);
        shareMobileLocation = (Button) view.findViewById(R.id.btnShareMobileLocation);
        AddNewAddress = (Button) view.findViewById(R.id.btnAddNewAddress);

        GetLocationUpdates locationUpdate = new GetLocationUpdates(getActivity());
        locationUpdate.setLocationUpdatesListener(this);
//		mediumFont = Typeface.createFromAsset(getAssets(), "Lato-Medium.ttf");
//		boldFont = Typeface.createFromAsset(getAssets(), "Lato-Bold.ttf");
//		regularFont = Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf");
//		semiboldFont = Typeface.createFromAsset(getAssets(),
//				"Lato-Semibold.ttf");
//		shareMobileLocation.setTypeface(boldFont);
//		AddNewAddress.setTypeface(boldFont);

//		ActionBar actionBar = getActionBar();
//		actionBar.setBackgroundDrawable(new ColorDrawable(getResources()
//				.getColor(R.color.myPrimaryColor)));
//		actionBar.setCustomView(R.layout.activity_location_header);
//		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
//				| ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
//		actionBar.setDisplayHomeAsUpEnabled(true);
//		actionBar.setDisplayShowHomeEnabled(true);
//		actionBar.setDisplayUseLogoEnabled(false);
//		ImageButton savelocation = (ImageButton) actionBar.getCustomView()
//				.findViewById(R.id.imbSaveLocation);
//
//		savelocation.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent i3 = new Intent(ShareUserSavedLocationFragment.this,
//						SaveLocationActivity.class);
//				i3.putExtra("STATUS", "ADD");
//				startActivityForResult(i3, 1);
//			}
//		});
        AddNewAddress.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AddNewAddress.setBackgroundResource(R.drawable.set_share_location_background);
                shareMobileLocation.setBackgroundResource(R.drawable.set_share_location_border);

                Intent i3 = new Intent(getActivity(), SaveLocationActivity.class);
                i3.putExtra("STATUS", "ADD");
                startActivityForResult(i3, 1);
            }
        });
        shareMobileLocation.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AddNewAddress.setBackgroundResource(R.drawable.set_share_location_border);
                shareMobileLocation.setBackgroundResource(R.drawable.set_share_location_background);

                share_type = "TRACK";
                try {
                    //gps = new GPSTracker(getActivity());
                    //if (gps.canGetLocation()) {
                    Log.e("latlong",
                            "" + currentUpdateLocation.getLatitude() + currentUpdateLocation.getLongitude());

						/*
                         * picker = new Dialog(ShareUserSavedLocationFragment.this);
						 * picker.requestWindowFeature(Window.FEATURE_NO_TITLE);
						 * picker
						 * .setContentView(R.layout.verify_pssword_dialog_item);
						 * 
						 * final EditText edtpassword = (EditText) picker
						 * .findViewById(R.id.edtPassword); Button
						 * submitpassword = (Button) picker
						 * .findViewById(R.id.btnSubmitPassword); submitpassword
						 * .setOnClickListener(new OnClickListener() {
						 * 
						 * @Override public void onClick(View v) { // TODO
						 * Auto-generated method stub
						 * 
						 * SharedPreferences mPrefs = getSharedPreferences(
						 * "LOGIN_DETAIL", MODE_PRIVATE); if
						 * (mPrefs.getString("PASSWORD", "") .equalsIgnoreCase(
						 * edtpassword.getText() .toString())) {
						 * picker.dismiss();
						 */
                    picker1 = new Dialog(getActivity());
                    picker1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    picker1.setContentView(R.layout.list_time_dialog_item);

                    final Spinner time = (Spinner) picker1
                            .findViewById(R.id.spnTime);
                    Button submit = (Button) picker1
                            .findViewById(R.id.btnSubmit);
                    TextView heading = (TextView) picker1
                            .findViewById(R.id.txvLocationVisibilityTime);
                    heading.setTypeface(boldFont);
                    submit.setTypeface(boldFont);
                    submit.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated
                            // method stub

                            selectedtime = time.getSelectedItem()
                                    .toString();
                            picker1.dismiss();
                            userForwardList(currentUpdateLocation.getLatitude() + "",
                                    currentUpdateLocation.getLongitude() + "", "");

                        }
                    });
                    picker1.show();

						/*
                         * } else { Toast.makeText( ShareUserSavedLocationFragment.this,
						 * "Your Password is wrong please try again!",
						 * Toast.LENGTH_LONG).show(); } } });
						 * 
						 * picker.show();
						 *//*} else {
                        Toast.makeText(
								ShareUserSavedLocationFragment.this,
								"Sorry! we can't find your current location \n Please start your GPS!",
								Toast.LENGTH_LONG).show();
					}*/
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(),
                            "Please start your GPS we can't get your location!!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        getSaveLocationData();
        return view;
    }

    private void setRecurringAlarm(Context context) {
        // get a Calendar object with current time
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, START_DELAY);
        Intent intent = new Intent(context, AlarmReceiver.class);
        tracking = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                cal.getTimeInMillis(), UPDATE_INTERVAL, tracking);
    }

    public void stopSendingLocation() {
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        tracking = PendingIntent.getBroadcast(getActivity(), 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        alarms = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarms.cancel(tracking);
        Log.e("stop location send", ">>>Stop tracking()");
    }

    public void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder
                .setMessage(
                        "GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    public void getSaveLocationData() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "get_user_location.php";
        SharedPreferences mPrefs = getActivity().getSharedPreferences("LOGIN_DETAIL", 0);
        final String user_id = mPrefs.getString("USER_ID", "");

        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("get_user_location", response.toString());
                        location_name_list = new ArrayList<String>();
                        latitude_list = new ArrayList<String>();
                        longitude_list = new ArrayList<String>();
                        location_id_list = new ArrayList<String>();
                        try {

                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response
                                    .toString());

                            String msg = object.getString("msg");
                            if (msg.equalsIgnoreCase("Success")) {
                                JSONArray dataArr = object.getJSONArray("data");
                                for (int i = 0; i < dataArr.length(); i++) {
                                    JSONObject dataOb = dataArr
                                            .getJSONObject(i);
                                    location_id_list.add(dataOb
                                            .getString("location_id"));
                                    location_name_list.add(dataOb
                                            .getString("location_name"));
                                    latitude_list.add(dataOb
                                            .getString("latitude"));
                                    longitude_list.add(dataOb
                                            .getString("longitude"));

                                }
                                adapter = new SaveLocationDataAdapter(
                                        location_id_list, location_name_list,
                                        latitude_list, longitude_list);
                                savelocationlist.setAdapter(adapter);
                            } else {
                                Toast.makeText(getActivity(),
                                        "Sorry! we can't find any saved location",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(getActivity(),
                                    "Sorry! we are stuff to fetching data. \n Please try again!",
                                    Toast.LENGTH_SHORT).show();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
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
                VolleyLog.e("get_user_location Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
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
                params.put("userid", user_id);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void deleteLocation(final String locationId) {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "delete_location.php";
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
                        Log.e("get_user_location", response.toString());
                        try {

                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response
                                    .toString());

                            String msg = object.getString("msg");
                            if (msg.equalsIgnoreCase("Success")) {
                                Toast.makeText(getActivity(),
                                        "Your location deleted Successfully",
                                        Toast.LENGTH_SHORT).show();
                                getSaveLocationData();
                            } else {
                                Toast.makeText(getActivity(),
                                        "Sorry! we can't delete your location",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(getActivity(),
                                    "Sorry! we are stuff to deleting data. \n Please try again!",
                                    Toast.LENGTH_SHORT).show();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
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
                VolleyLog.e("get_user_location Error", "Error: "
                        + error.getMessage());
                // hide the progress dialog
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
                SharedPreferences mPrefs = getActivity().getSharedPreferences("LOGIN_DETAIL", 0);
                String user_id = mPrefs.getString("USER_ID", "");
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", user_id);
                params.put("location_id", locationId);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);


    }

    public class SaveLocationDataAdapter extends BaseAdapter {

        LayoutInflater inflater;
        ArrayList<String> loc_id, loc_list, lat_list, long_list;

        int m = 0;

        public SaveLocationDataAdapter(ArrayList<String> location_id,
                                       ArrayList<String> loc_list, ArrayList<String> lat_list,
                                       ArrayList<String> long_list) {
            // TODO Auto-generated constructor stub
            this.loc_id = location_id;
            this.loc_list = loc_list;
            this.lat_list = lat_list;
            this.long_list = long_list;
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        class ViewHolder {
            TextView locationName;
            ImageView share, update, delete, viewLocation;
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

                convertView = inflater.inflate(R.layout.row_saved_location,
                        parent, false);
                holder = new ViewHolder();
                holder.locationName = (TextView) convertView
                        .findViewById(R.id.txvLocationName);
                holder.share = (ImageView) convertView
                        .findViewById(R.id.imvShareLocation);
                holder.update = (ImageView) convertView
                        .findViewById(R.id.imvUpdateLocation);
                holder.delete = (ImageView) convertView
                        .findViewById(R.id.imvDeleteLocation);
                holder.viewLocation = (ImageView) convertView
                        .findViewById(R.id.imvViewLocation);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.locationName.setTypeface(mediumFont);
           /* if (position % 4 == 0) {
                holder.locationName.setBackgroundColor(Color.parseColor("#ffa200"));
                holder.share.setBackgroundColor(Color.parseColor("#4c3000"));
                holder.update.setBackgroundColor(Color.parseColor("#4c3000"));
                holder.delete.setBackgroundColor(Color.parseColor("#4c3000"));
                holder.viewLocation.setBackgroundColor(Color.parseColor("#4c3000"));
            } else if (position % 4 == 1) {
                holder.locationName.setBackgroundColor(Color.parseColor("#8a638f"));
                holder.share.setBackgroundColor(Color.parseColor("#3e2541"));
                holder.update.setBackgroundColor(Color.parseColor("#3e2541"));
                holder.delete.setBackgroundColor(Color.parseColor("#3e2541"));
                holder.viewLocation.setBackgroundColor(Color.parseColor("#3e2541"));
            } else if (position % 4 == 2) {
                holder.locationName.setBackgroundColor(Color.parseColor("#0bc1f0"));
                holder.share.setBackgroundColor(Color.parseColor("#119abd"));
                holder.update.setBackgroundColor(Color.parseColor("#119abd"));
                holder.delete.setBackgroundColor(Color.parseColor("#119abd"));
                holder.viewLocation.setBackgroundColor(Color.parseColor("#119abd"));
            } else if (position % 4 == 3) {
                holder.locationName.setBackgroundColor(Color.parseColor("#00a79d"));
                holder.share.setBackgroundColor(Color.parseColor("#0c736d"));
                holder.update.setBackgroundColor(Color.parseColor("#0c736d"));
                holder.delete.setBackgroundColor(Color.parseColor("#0c736d"));
                holder.viewLocation.setBackgroundColor(Color.parseColor("#0c736d"));
            }*/
            holder.locationName.setText(loc_list.get(position));
            holder.viewLocation.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
//                    Intent i = new Intent();
//                    i.putExtra("LATITUDE", lat_list.get(position));
//                    i.putExtra("LONGITUDE", long_list.get(position));
//                    i.putExtra("LOCATION_NAME", loc_list.get(position));
//                    setResult(109, i);
//                    finish();
                }
            });
            holder.update.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent i = new Intent(getActivity(),
                            SaveLocationActivity.class);
                    i.putExtra("STATUS", "UPDATE");
                    i.putExtra("ID", loc_id.get(position));
                    i.putExtra("NAME", loc_list.get(position));
                    i.putExtra("LATITUDE", lat_list.get(position));
                    i.putExtra("LONGITUDE", long_list.get(position));
                    startActivityForResult(i, 2);
                }
            });
            holder.delete.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    deleteLocation(loc_id.get(position));
                }
            });
            holder.share.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    share_type = "NAVIGATE";
                    selectedtime = "";
                    userForwardList(lat_list.get(position),
                            long_list.get(position), loc_id.get(position));
                }
            });
            return convertView;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return loc_list.size();
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

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // TODO Auto-generated method stub
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 2) {
//            getSaveLocationData();
//        }
//    }

    public class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap
                    .createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }

    public void userForwardList(final String lat, final String log,
                                final String location_id) {

        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "all_bonjour_user.php";
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
                        Log.e("all_bonjour_user", response.toString());
                        forward_id = new ArrayList<String>();
                        forward_name = new ArrayList<String>();

                        forward_phone_no = new ArrayList<String>();
                        try {

                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response
                                    .toString());

                            String msg = object.getString("msg");
                            if (msg.equalsIgnoreCase("Success")) {
                                JSONArray dataArr = object.getJSONArray("data");
                                for (int i = 0; i < dataArr.length(); i++) {
                                    JSONObject dataOb = dataArr
                                            .getJSONObject(i);
                                    forward_name.add(dataOb.getString("name"));
                                    forward_id.add(dataOb.getString("id"));
                                    forward_phone_no.add(dataOb
                                            .getString("phone"));
                                }
                                getAllContacts(lat, log, location_id);
                            } else {
                                Toast.makeText(getActivity(),
                                        "Sorry! we are stuff to fetching data. \n Please try again!",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(getActivity(),
                                    "Sorry! we are stuff to fetching data. \n Please try again!",
                                    Toast.LENGTH_SHORT).show();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
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
                VolleyLog.e("all_bonjour_user Error",
                        "Error: " + error.getMessage());
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
                SharedPreferences mPrefs = getActivity().getSharedPreferences("LOGIN_DETAIL", 0);
                String user_id = mPrefs.getString("USER_ID", "");
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", user_id);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void getAllContacts(final String lat, final String log,
                               final String location_id) {
        namelist = new ArrayList<String>();
        numberlist = new ArrayList<String>();
        final ArrayList<String> filter_id = new ArrayList<String>();
        final ArrayList<String> filter_name = new ArrayList<String>();
        checkbox_val = new ArrayList<String>();
        // Cursor phones =
        // getActivity().getContentResolver().query(Phone.CONTENT_URI, null,
        // null, null, Phone.DISPLAY_NAME+ " ASC");
        Cursor phones = getActivity().getContentResolver().query(Phone.CONTENT_URI, null,
                null, null, "upper(" + Phone.DISPLAY_NAME + ") ASC");
        while (phones.moveToNext()) {
            String name = phones
                    .getString(phones
                            .getColumnIndex(Phone.DISPLAY_NAME));
            String phoneNumberString = phones
                    .getString(phones
                            .getColumnIndex(Phone.NUMBER));
            phoneNumberString = phoneNumberString.replace(" ", "");
            int l = phoneNumberString.length();
            String phone = "";
            if (l > 10) {
                phone = phoneNumberString.substring((l - 10), l);
            } else {
                phone = phoneNumberString;
            }
            Log.e(name, phone);
            namelist.add(name);
            numberlist.add(phone);
        }

        picker = new Dialog(getActivity());
        picker.requestWindowFeature(Window.FEATURE_NO_TITLE);
        picker.setContentView(R.layout.list_select_person_dialog);
        TextView selectPersonTitle = (TextView) picker
                .findViewById(R.id.txvSelectPerson);
        itemlist = (ListView) picker
                .findViewById(R.id.lsvItemDialog);
        ImageView close = (ImageView) picker.findViewById(R.id.imvClose);
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                picker.dismiss();
            }
        });
        TextView forward = (TextView) picker.findViewById(R.id.txvShareLocation);
        TextView share = (TextView) picker.findViewById(R.id.txvShareViaOtherApps);
        final AutoCompleteTextView search_user = (AutoCompleteTextView) picker
                .findViewById(R.id.autoCompleteTextView1);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, forward_name);
        search_user.setAdapter(adapter1);
        forward.setText("Share Location");

        share.setTypeface(boldFont);
        forward.setTypeface(boldFont);
        search_user.setTypeface(regularFont);
        selectPersonTitle.setTypeface(boldFont);

        search_user.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                search_user.setInputType(InputType.TYPE_CLASS_TEXT);
                search_user.onTouchEvent(event); // call native handler
                return true; // consume touch even
            }
        });
        search_user.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                for (int i = 0; i < forward_name.size(); i++) {
                    if (parent.getItemAtPosition(position).toString()
                            .equalsIgnoreCase(forward_name.get(i))) {
                        // selected_user_id = forward_id.get(i);
                        // sellerShareLocation(lat, log);
                        // picker.dismiss();
                        filter_id.add(forward_id.get(i));
                        filter_name.add(forward_name.get(i));
                        checkbox_val.add("false");
                        Log.e("name", parent.getItemAtPosition(position)
                                .toString() + ":" + forward_name.get(i));
                        break;
                    }
                }
                forwardadapter.notifyDataSetChanged();
                // forwardadapter = new ForwardUserAdapter(filter_id,
                // filter_name);
                // itemlist.setAdapter(forwardadapter);
            }
        });

        share.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String shareBody = "Hey! freinds login in a bonjour you find the your friend location";
                Intent sharingIntent = new Intent(
                        Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Here's my location,courtesy of bonjour");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via other apps"));
            }
        });

        for (int j = 0; j < forward_phone_no.size(); j++) {
            for (int i = 0; i < numberlist.size(); i++) {
                if (forward_phone_no.get(j).equalsIgnoreCase(numberlist.get(i))) {
                    filter_id.add(forward_id.get(j));
                    filter_name.add(forward_name.get(j));
                    checkbox_val.add("false");
                    break;
                }
            }
        }
        forwardadapter = new ForwardUserAdapter(filter_id, filter_name);
        itemlist.setAdapter(forwardadapter);
        forward.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                selected_user_id = "";
                int m = 0;
                for (int i = 0; i < checkbox_val.size(); i++) {
                    Log.e("checkbox value", "value:" + checkbox_val.get(i));
                    if (checkbox_val.get(i).equalsIgnoreCase("true")) {
                        selected_user_id = selected_user_id + "|"
                                + filter_id.get(i);
                    } else {
                        m++;
                    }
                }
                Log.e("selected user id", selected_user_id + "");
                if (m != checkbox_val.size()) {
                    sellerShareLocation(lat, log, location_id);
                    picker.dismiss();
                } else {
                    Toast.makeText(getActivity(),
                            "Please select any sender", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
        picker.show();

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

            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        class ViewHolder {
            TextView txvForwardName;
            CheckBox chbForwardUser;

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

                convertView = inflater.inflate(R.layout.row_user_list, parent,
                        false);
                holder = new ViewHolder();
                holder.txvForwardName = (TextView) convertView
                        .findViewById(R.id.txvForwarduserName);
                holder.chbForwardUser = (CheckBox) convertView
                        .findViewById(R.id.chbForwardUser);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.txvForwardName.setTypeface(mediumFont);
            if (checkbox_val.get(position).equalsIgnoreCase("false")) {
                holder.chbForwardUser.setChecked(false);
            } else {
                holder.chbForwardUser.setChecked(true);
            }
            holder.chbForwardUser.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (checkbox_val.get(position).equalsIgnoreCase("false")) {
                        checkbox_val.set(position, "true");
                    } else {
                        checkbox_val.set(position, "false");
                    }

                }
            });
            /*holder.chbForwardUser
                    .setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							//checkbox_val.set(position, isChecked + "");
							int pos = itemlist.getPositionForView(buttonView);
					         System.out.println("Pos ["+pos+"]");
					         if (pos != ListView.INVALID_POSITION) {
//					             Planet p = planetsList.get(pos);        
//					             p.setChecked(isChecked);
					        	 checkbox_val.set(pos, isChecked + "");
					         }
					
						}
					});*/

            holder.txvForwardName.setText(forwardUserName.get(position));
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

    public void sellerShareLocation(final String lat, final String log,
                                    final String location_id) {

        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "share_location.php";
        Log.e("url", url + "");
        SharedPreferences mPrefs = getActivity().getSharedPreferences("LOGIN_DETAIL", 0);
        final String user_id = mPrefs.getString("USER_ID", "");

        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("share_location", response.toString());
                        forward_id = new ArrayList<String>();
                        forward_name = new ArrayList<String>();
                        checkbox_val = new ArrayList<String>();
                        try {

                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());

                            String msg = object.getString("msg");
                            if (msg.equalsIgnoreCase("Success")) {
                                JSONObject dataob = object.getJSONObject("data");
                                picker.dismiss();
                                SharedPreferences mPrefs = getActivity().getSharedPreferences(
                                        "LOGIN_DETAIL", 0);
                                Editor edit = mPrefs.edit();
                                edit.putString("SHARE_TRIP_ID",
                                        dataob.getString("last_share_id"));
                                edit.commit();
                                Toast.makeText(getActivity(),
                                        "Thank you! your location shared successfully",
                                        Toast.LENGTH_SHORT).show();

                                LocationManager locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
                                if (locationManager
                                        .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                    // Toast.makeText(this,
                                    // "GPS is Enabled in your device",
                                    // Toast.LENGTH_SHORT).show();
                                } else {
                                    showGPSDisabledAlertToUser();
                                }

                                setRecurringAlarm(getActivity());
                            } else {
                                Toast.makeText(getActivity(),
                                        "Sorry! we are stuff to fetching data. \n Please try again!",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(getActivity(),
                                    "Sorry! we are stuff to fetching data. \n Please try again!",
                                    Toast.LENGTH_SHORT).show();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
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
                VolleyLog.e("share_location Error",
                        "Error: " + error.getMessage());
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
                params.put("userid", user_id);
                params.put("to_userid", selected_user_id.substring(1, selected_user_id.length()));
                params.put("latitude", lat);
                params.put("longitude", log);
                params.put("comment", "nothing");
                params.put("sharing_type", share_type);
                params.put("visible_time", selectedtime);
                params.put("location_id", location_id);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

}
