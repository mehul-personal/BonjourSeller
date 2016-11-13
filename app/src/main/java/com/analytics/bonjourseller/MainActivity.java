package com.analytics.bonjourseller;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
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
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        EditFragment.OnEditFragmentInteractionListener,
        CustomSearchFragment.OnCustomSearchFragmentInteractionListener, SettingFragment.OnSettingFragmentInteractionListener,
        ShareUserSavedLocationFragment.OnShareUserSaveLocationFragmentInteractionListener,
        MainOfferListFragment.OnOfferListFragmentInteractionListener,PostNewOfferFragment.OnPostOfferFragmentInteractionListener , GetLocationUpdates.LocationUpdates {
    ImageView userPhoto;
    TextView userName;
    static Location currentUpdateLocation;
    public static ArrayList<String> forward_id, forward_name, checkbox_val,
            forward_phone_no;
    ArrayList<String> namelist, numberlist;
    public static Dialog picker, picker1;
   // Typeface mediumFont, boldFont, regularFont, semiboldFont;
   public static ForwardUserAdapter forwardadapter;
    public static String selected_user_id = "";
    public static ListView itemlist;
    private AlarmManager alarms;
    private PendingIntent tracking;
    private int START_DELAY = 5;
    private long UPDATE_INTERVAL = 30000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        navigationView.setItemTextColor(ColorStateList.valueOf(Color.parseColor("#908b8b")));

        View headerLayout = navigationView.getHeaderView(0);
        userPhoto = (ImageView) headerLayout.findViewById(R.id.imvUserPhoto);
        userName = (TextView) headerLayout.findViewById(R.id.txvUserName);
        GetLocationUpdates locationUpdate = new GetLocationUpdates(MainActivity.this);
        locationUpdate.setLocationUpdatesListener(this);
        SharedPreferences mPrefs = getSharedPreferences(
                "LOGIN_DETAIL", MODE_PRIVATE);
        userName.setText(mPrefs.getString("NAME",""));
        if(mPrefs.getString("IMAGE_URL", "").isEmpty()){
            Picasso.with(MainActivity.this)
                    .load(R.drawable.ic_no_image).transform(new CircleTransform())
                    .into(userPhoto);
        }else {
            Picasso.with(MainActivity.this)
                    .load(mPrefs.getString("IMAGE_URL", "")).placeholder(R.drawable.ic_no_image).transform(new CircleTransform())
                    .into(userPhoto);
        }userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

                Fragment editFragment = new EditFragment();
                if (editFragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                    fragmentTransaction.replace(R.id.flMainLayout, editFragment);
                    fragmentTransaction.commit();
                }

            }
        });
        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

                Fragment editFragment = new EditFragment();
                if (editFragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                    fragmentTransaction.replace(R.id.flMainLayout, editFragment);
                    fragmentTransaction.commit();
                }

            }
        });
        selectMenuItem(R.id.nav_home);
    }
    @Override
    public void handleLocationUpdatesCallback(Location location) {
        currentUpdateLocation = location;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        selectMenuItem(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void selectMenuItem(int id) {
        if (id == R.id.nav_home) {
            Fragment offerListFragment = new MainOfferListFragment();
            if (offerListFragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                fragmentTransaction.replace(R.id.flMainLayout, offerListFragment);
                fragmentTransaction.commit();
            }

        } else if (id == R.id.nav_settings) {
            Fragment mapFragment = new SettingFragment();
            if (mapFragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                fragmentTransaction.replace(R.id.flMainLayout, mapFragment);
                fragmentTransaction.commit();
            }
        } else if (id == R.id.nav_post_new_offer) {
            Fragment mapFragment = new PostNewOfferFragment();
            if (mapFragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                fragmentTransaction.replace(R.id.flMainLayout, mapFragment);
                fragmentTransaction.commit();
            }
        } else if (id == R.id.nav_share) {
           /* Fragment mapFragment = new ShareUserSavedLocationFragment();
            if (mapFragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.push_down_right_in, R.anim.push_down_right_out);
                fragmentTransaction.replace(R.id.flMainLayout, mapFragment);
                fragmentTransaction.commit();
            }*/

            try{
                Log.e("latlong",
                        "" + currentUpdateLocation.getLatitude() + currentUpdateLocation.getLongitude());
                userForwardList(currentUpdateLocation.getLatitude() + "",
                        currentUpdateLocation.getLongitude() + "", "");
            }catch (Exception e){
                Toast.makeText(MainActivity.this,
                        "Please start your GPS we can't get your location!!",
                        Toast.LENGTH_SHORT).show();
            }
        }else if(id==R.id.nav_user_zone){
            try {
                PackageManager pm = getPackageManager();
                Intent launchIntent = pm.getLaunchIntentForPackage("com.analytics.bonjourbuyer");
                startActivity(launchIntent);
            }catch(Exception e){
                Toast.makeText(MainActivity.this,"Users Apps not available \nYou can download on play store!",Toast.LENGTH_LONG).show();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.analytics.bonjourbuyer")));

            }
        } else if (id == R.id.nav_sign_out) {
            SharedPreferences sharedPreferences5 = getSharedPreferences(
                    "LOGIN_DETAIL", 0);
            sharedPreferences5.edit().clear().commit();
            Intent intent = new Intent(MainActivity.this,
                    LoginActivity.class);
            startActivity(intent);
            finish();
        }

    }
    public void userForwardList(final String lat, final String log,
                                final String location_id) {

        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "all_bonjour_user.php";
        Log.e("url", url + "");

        final ProgressDialog mProgressDialog = new ProgressDialog(MainActivity.this);
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
                                Toast.makeText(MainActivity.this,
                                        "Sorry! we are stuff to fetching data. \n Please try again!",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this,
                                    "Sorry! we are stuff to fetching data. \n Please try again!",
                                    Toast.LENGTH_SHORT).show();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(MainActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(MainActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences mPrefs = getSharedPreferences("LOGIN_DETAIL", 0);
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
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                null, null, "upper(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") ASC");
        while (phones.moveToNext()) {
            String name = phones
                    .getString(phones
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumberString = phones
                    .getString(phones
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
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

        picker = new Dialog(MainActivity.this);
        picker.requestWindowFeature(Window.FEATURE_NO_TITLE);
        picker.setContentView(R.layout.list_select_person_dialog);
        TextView selectPersonTitle = (TextView) picker
                .findViewById(R.id.txvSelectPerson);
        itemlist = (ListView) picker
                .findViewById(R.id.lsvItemDialog);
        ImageView close = (ImageView) picker.findViewById(R.id.imvClose);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker.dismiss();
            }
        });
        TextView forward = (TextView) picker.findViewById(R.id.txvShareLocation);
        TextView share = (TextView) picker.findViewById(R.id.txvShareViaOtherApps);
        final AutoCompleteTextView search_user = (AutoCompleteTextView) picker
                .findViewById(R.id.autoCompleteTextView1);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1, forward_name);
        search_user.setAdapter(adapter1);
        forward.setText("Share Location");

//        share.setTypeface(boldFont);
//        forward.setTypeface(boldFont);
//        search_user.setTypeface(regularFont);
//        selectPersonTitle.setTypeface(boldFont);

        search_user.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                search_user.setInputType(InputType.TYPE_CLASS_TEXT);
                search_user.onTouchEvent(event); // call native handler
                return true; // consume touch even
            }
        });
        search_user.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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

        share.setOnClickListener(new View.OnClickListener() {

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
        forward.setOnClickListener(new View.OnClickListener() {

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
                    Toast.makeText(MainActivity.this,
                            "Please select any sender", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
        picker.show();

    }
    public void sellerShareLocation(final String lat, final String log,
                                    final String location_id) {

        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "share_location.php";
        Log.e("url", url + "");
        SharedPreferences mPrefs = getSharedPreferences("LOGIN_DETAIL", 0);
        final String user_id = mPrefs.getString("USER_ID", "");

        final ProgressDialog mProgressDialog = new ProgressDialog(MainActivity.this);
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
                                SharedPreferences mPrefs = getSharedPreferences(
                                        "LOGIN_DETAIL", 0);
                                SharedPreferences.Editor edit = mPrefs.edit();
                                edit.putString("SHARE_TRIP_ID",
                                        dataob.getString("last_share_id"));
                                edit.commit();
                                Toast.makeText(MainActivity.this,
                                        "Thank you! your location shared successfully",
                                        Toast.LENGTH_SHORT).show();

                                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                                if (locationManager
                                        .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                    // Toast.makeText(this,
                                    // "GPS is Enabled in your device",
                                    // Toast.LENGTH_SHORT).show();
                                } else {
                                    showGPSDisabledAlertToUser();
                                }

                                setRecurringAlarm(MainActivity.this);
                            } else {
                                Toast.makeText(MainActivity.this,
                                        "Sorry! we are stuff to fetching data. \n Please try again!",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this,
                                    "Sorry! we are stuff to fetching data. \n Please try again!",
                                    Toast.LENGTH_SHORT).show();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(MainActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(MainActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
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
                params.put("sharing_type", "TRACK");
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
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
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        tracking = PendingIntent.getBroadcast(MainActivity.this, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarms.cancel(tracking);
        Log.e("stop location send", ">>>Stop tracking()");
    }
    public void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
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
            //holder.txvForwardName.setTypeface(mediumFont);
            if (checkbox_val.get(position).equalsIgnoreCase("false")) {
                holder.chbForwardUser.setChecked(false);
            } else {
                holder.chbForwardUser.setChecked(true);
            }
            holder.chbForwardUser.setOnClickListener(new View.OnClickListener() {

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
    @Override
    public void onFragmentInteraction(Uri uri) {

    } public class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
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


}
