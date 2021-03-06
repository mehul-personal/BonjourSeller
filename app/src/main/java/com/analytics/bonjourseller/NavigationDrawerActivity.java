package com.analytics.bonjourseller;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressLint({"Recycle", "NewApi", "ResourceAsColor"})
public class NavigationDrawerActivity extends AppCompatActivity implements
        OnCameraChangeListener, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnItemClickListener {
    private static final String LOG_TAG = "NavigationDrawerActiviy";
    private GoogleMap googleMap;
    private LocationManager locationManager;
    static String provider = "";
    public static String sdcard_image_url = "";
    boolean mapDefault = false;
    Button markerInfoWindow;
    // public LocationClient client;
    private GoogleApiClient mLocationClient = null;
    public static Location location = null;

    public static Location currentLocation;
    public static LatLng center_poistion, current_poistion;
    String user_type = "";
    private static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND
            * UPDATE_INTERVAL_IN_SECONDS;
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND
            * FASTEST_INTERVAL_IN_SECONDS;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest = null;
    MapView map;
    ImageView marker_sign;
    Intent intent;
    public static AutoCompleteTextView AddressBar;
    public static String addressLine = "";
    String zip;
    String city;
    String state;
    String country;
    String address1;
    List<Address> addresses = null;
    Button submit;
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    private static final String API_KEY = "AIzaSyCp5GFTxD5-KUaXMHxEAAiK04EQ6o0H01s";
    private ArrayList<String> resultList;
    public static String savedLat, savedLog, location_name, location_type,
            location_id;
    ImageButton maptype;
//	Typeface mediumFont, boldFont, regularFont, semiboldFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_main_layout);
        resultList = new ArrayList<String>();
//		ActionBar actionBar = getActionBar();
//		actionBar.setBackgroundDrawable(new ColorDrawable(getResources()
//				.getColor(R.color.myPrimaryColor)));
//		actionBar.setDisplayHomeAsUpEnabled(true);
//		actionBar.setDisplayShowHomeEnabled(true);
//		actionBar.setDisplayUseLogoEnabled(false);

//		mediumFont = Typeface.createFromAsset(getAssets(), "Lato-Medium.ttf");
//		boldFont = Typeface.createFromAsset(getAssets(), "Lato-Bold.ttf");
//		regularFont = Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf");
//		semiboldFont = Typeface.createFromAsset(getAssets(),
//				"Lato-Semibold.ttf");

        mLocationClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        servicesConnected();
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        AddressBar = (AutoCompleteTextView) findViewById(R.id.txvCurrentLocation);
        AddressBar.setAdapter(new GooglePlacesAutocompleteAdapter(this,
                R.layout.list_item_loc));
        AddressBar.setOnItemClickListener(this);
        marker_sign = (ImageView) findViewById(R.id.marker_sign);
        submit = (Button) findViewById(R.id.btnLocationSubmit);

        markerInfoWindow = (Button) findViewById(R.id.btnMarkerInfoWindow);
        maptype = (ImageButton) findViewById(R.id.imbMapSetLocationType);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pickup Location");

//		submit.setTypeface(boldFont);
//		markerInfoWindow.setTypeface(boldFont);
//		AddressBar.setTypeface(regularFont);

		/*Intent i = getIntent();
        location_name = i.getStringExtra("LOCATION_NAME");
		location_type = i.getStringExtra("LOCATION_TYPE");
		if (location_type.equalsIgnoreCase("UPDATE")) {
			savedLat = i.getStringExtra("LATITUDE");
			savedLog = i.getStringExtra("LONGITUDE");
			location_id = i.getStringExtra("LOCATION_ID");
		}*/
        Log.i("msg", "2");
        maptype.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Dialog picker = new Dialog(NavigationDrawerActivity.this);
                picker.requestWindowFeature(Window.FEATURE_NO_TITLE);
                picker.setContentView(R.layout.list_map_type);

                ListView itemlist = (ListView) picker
                        .findViewById(R.id.lsvItemDialog);
                final ArrayList<String> data = new ArrayList<String>();
                final ArrayList<Integer> image = new ArrayList<Integer>();
                data.add("Normal");
                data.add("Satellite");
                data.add("Terrain");
                data.add("Hybrid");
                image.add(R.drawable.ic_map_normal);
                image.add(R.drawable.ic_map_satellite);
                image.add(R.drawable.ic_map_terrien);
                image.add(R.drawable.ic_map_hybrid);
                //
                // ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                // MainActivity.this, android.R.layout.simple_list_item_1,
                // android.R.id.text1, data);
                MapTypeAdapter adapter = new MapTypeAdapter(data, image);
                itemlist.setAdapter(adapter);
                itemlist.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        // TODO Auto-generated method stub

                        if (position == 0) {
                            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        } else if (position == 1) {
                            googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        } else if (position == 2) {
                            googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        } else if (position == 3) {
                            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        }
                        picker.dismiss();
                    }
                });
                picker.show();

            }
        });
        submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (center_poistion == null) {
                    Toast.makeText(getApplicationContext(),
                            "Please Wait Adjust Location", Toast.LENGTH_LONG)
                            .show();
                } else {
                    Intent i = new Intent();
                    i.putExtra("LATITUDE", "" + center_poistion.latitude);
                    i.putExtra("LONGITUDE", "" + center_poistion.longitude);
                    setResult(15,i);
                    finish();
//					if (location_type.equalsIgnoreCase("UPDATE")) {
//						updateLocation(location_id);
//					} else {
//						saveLocation();
//					}
                }
            }
        });

        markerInfoWindow.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    markerInfoWindow.setTextColor(Color.WHITE);
                    // markerInfoWindow.setBackground(getResources().getDrawable(
                    // R.drawable.marker_popup_hover));
                    markerInfoWindow.setPadding(0, 0, 0, 10);

                    // set right side
                    markerInfoWindow.getRight();

                    Log.e("navigation latlng", "" + center_poistion.latitude
                            + ":" + center_poistion.longitude);

                    // if (center_poistion != null || location != null) {
                    // if ((center_poistion.latitude) != 0.0
                    // && (center_poistion.longitude) != 0.0
                    // && (location.getLatitude()) != 0
                    // && (location.getLongitude()) != 0) {
                    // Intent i = new Intent(
                    // NavigationDrawerActivity.this,
                    // PickupFeatureActivity.class);
                    // // center latlong
                    // i.putExtra("CENTER_LATITUDE",
                    // center_poistion.latitude);
                    // i.putExtra("CENTER_LONGITUDE",
                    // center_poistion.longitude);
                    // // current latlong
                    // i.putExtra("CURRENT_LATITUDE",
                    // location.getLatitude());
                    // i.putExtra("CURRENT_LONGITUDE",
                    // location.getLongitude());
                    // i.putExtra("PICKUP_ADDRESS", addressLine);
                    // startActivity(i);
                    // } else {
                    // Toast.makeText(getApplicationContext(),
                    // "Ooops Location Not Available! Please Try Agian",Toast.LENGTH_LONG).show();
                    // }
                    // } else {
                    // Toast.makeText(getApplicationContext(),"Ooops Location Not Available! Please Try Agian",Toast.LENGTH_LONG).show();
                    // }

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    markerInfoWindow.setTextColor(Color.parseColor("#f1c40f"));
                    // markerInfoWindow.setBackground(getResources().getDrawable(
                    // R.drawable.marker_popup));
                    markerInfoWindow.setPadding(0, 0, 0, 10);

                }
                return true;
            }
        });

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        // Getting a reference to the map

        googleMap = supportMapFragment.getMap();
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        googleMap.setMyLocationEnabled(true);
        // googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(
        // 15.7833, 47.8667)));

        googleMap.setOnCameraChangeListener(this);

        // LocationManager service = (LocationManager)
        // getSystemService(LOCATION_SERVICE);
        // boolean enabledGPS = service
        // .isProviderEnabled(LocationManager.GPS_PROVIDER);
        //
        // if (!enabledGPS) {
        // Toast.makeText(this, "GPS signal not found", Toast.LENGTH_LONG)
        // .show();
        // Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        // startActivity(intent);
        // }
        // LatLng center_poistion = googleMap.getCameraPosition().target;
        // lastLocation(center_poistion);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class GetAddressTask extends AsyncTask<Location, Void, String> {
        Context localContext;
        public final AndroidHttpClient ANDROID_HTTP_CLIENT = AndroidHttpClient
                .newInstance(GetAddressTask.class.getName());

        public GetAddressTask(Context context) {
            super();
            localContext = context;
        }

        /**
         * Get a geocoding service instance, pass latitude and longitude to it,
         * format the returned address, and return the address to the UI thread.
         */
        @Override
        protected String doInBackground(Location... params) {
            Geocoder geocoder = new Geocoder(localContext, Locale.getDefault());
            Location location = params[0];
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);
            } catch (IOException exception1) {
                Log.e("error", exception1 + "");
                exception1.printStackTrace();
            } catch (IllegalArgumentException exception2) {
                exception2.printStackTrace();
            }
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String addressText = getString(
                        R.string.address_output_string,
                        address.getMaxAddressLineIndex() > 0 ? address
                                .getAddressLine(0) : "",
                        address.getLocality(),
                        address.getCountryName());
                return addressText;
            } else {
                return fetchCityNameUsingGoogleMap(location.getLatitude(),
                        location.getLongitude());
            }
        }

        private String fetchCityNameUsingGoogleMap(double latitude,
                                                   double longitude) {
            String googleMapUrl = "http://maps.googleapis.com/maps/api/geocode/json?latlng="
                    + latitude + "," + longitude + "&sensor=false";

            try {
                JSONObject googleMapResponse = new JSONObject(
                        ANDROID_HTTP_CLIENT.execute(new HttpGet(googleMapUrl),
                                new BasicResponseHandler()));

                // many nested loops.. not great -> use expression instead
                // loop among all results
                JSONArray results = (JSONArray) googleMapResponse
                        .get("results");
                for (int i = 0; i < results.length(); i++) {
                    // loop among all addresses within this result
                    JSONObject result = results.getJSONObject(i);
                    if (result.has("address_components")) {
                        JSONArray addressComponents = result
                                .getJSONArray("address_components");
                        // loop among all address component to find a 'locality'
                        // or 'sublocality'
                        for (int j = 0; j < addressComponents.length(); j++) {
                            JSONObject addressComponent = addressComponents
                                    .getJSONObject(j);
                            if (result.has("types")) {
                                JSONArray types = addressComponent
                                        .getJSONArray("types");

                                // search for locality and sublocality
                                String cityName = null;

                                for (int k = 0; k < types.length(); k++) {
                                    if ("locality".equals(types.getString(k))
                                            && cityName == null) {
                                        if (addressComponent.has("long_name")) {
                                            cityName = addressComponent
                                                    .getString("long_name");
                                        } else if (addressComponent
                                                .has("short_name")) {
                                            cityName = addressComponent
                                                    .getString("short_name");
                                        }
                                    }
                                    if ("sublocality"
                                            .equals(types.getString(k))) {
                                        if (addressComponent.has("long_name")) {
                                            cityName = addressComponent
                                                    .getString("long_name");
                                        } else if (addressComponent
                                                .has("short_name")) {
                                            cityName = addressComponent
                                                    .getString("short_name");
                                        }
                                    }
                                }
                                if (cityName != null) {
                                    return cityName;
                                }
                            }
                        }
                    }
                }

            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
            return null;
        }

        /**
         * A method that's called once doInBackground() completes. Set the text
         * of the UI element that displays the address. This method runs on the
         * UI thread.
         */
        @Override
        protected void onPostExecute(String address) {

            // Set the address in the UI

            if (address != null) {
                addressLine = address;
                AddressBar.setText(addressLine);
                // resultList.add(addressLine);
            }
        }

        @Override
        protected void finalize() {
            ((AndroidHttpClient) this.ANDROID_HTTP_CLIENT).close();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLocationClient.connect();
    }

    @Override
    protected void onStop() {

        // if (mLocationClient.isConnected()) {
        // mLocationClient.removeLocationUpdates(this);
        // }
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onCameraChange(CameraPosition arg0) {
        // TODO Auto-generated method stub
        // googleMap.animateCamera(
        // CameraUpdateFactory.newCameraPosition(arg0), 1000, null);

        center_poistion = googleMap.getCameraPosition().target;
        Log.e("center location", "" + center_poistion);
        // lastLocation(center_poistion);
        Location location = new Location("Test");
        location.setLatitude(center_poistion.latitude);
        location.setLongitude(center_poistion.longitude);
        location.setTime(new Date().getTime());

        (new GetAddressTask(this)).execute(location);

    }

    public void convertAddress(String address) {
        if (address != null && !address.isEmpty()) {

            Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
            try {
                List<Address> addressList = geocoder.getFromLocationName(
                        address, 1);
                if (addressList != null && addressList.size() > 0) {
                    double lat = addressList.get(0).getLatitude();
                    double lng = addressList.get(0).getLongitude();

                    LatLng TIMES_SQUARE = new LatLng(addressList.get(0)
                            .getLatitude(), addressList.get(0).getLongitude());
                    Log.e("get address lat", String.valueOf(lat));
                    Log.e("get address lon", String.valueOf(lng));

                    takeToLocation(new LatLng(lat, lng));

                    // addLines();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean servicesConnected() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == resultCode) {
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    resultCode, this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            if (errorDialog != null) {
                errorDialog.show();
            }
            return false;
        }
    }

    @Override
    public void finish() {
        // TODO Auto-generated method stub
        super.finish();
    }

    @Override
    public void onLocationChanged(Location arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnected(Bundle arg0) {
        // TODO Auto-generated method stub

        try {
            if (savedLat != null) {
                if (savedLat.isEmpty()) {
                    Location mLastLocation = LocationServices.FusedLocationApi
                            .getLastLocation(mLocationClient);
                    takeToLocation(convertLocationtoLatLang(mLastLocation));
                } else {
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
                            new LatLng(Double.parseDouble(savedLat), Double
                                    .parseDouble(savedLog)), 16);
                    googleMap.animateCamera(update);
                }
            } else {
                Location mLastLocation = LocationServices.FusedLocationApi
                        .getLastLocation(mLocationClient);
                takeToLocation(convertLocationtoLatLang(mLastLocation));
            }
        } catch (Exception e) {
            Log.e("error current location", e + "");
        }

    }

    public void takeToLocation(LatLng toBeLocationLatLang) {
        if (toBeLocationLatLang != null) {
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
                    toBeLocationLatLang, 16);
            googleMap.animateCamera(update);
        } else {
            Toast.makeText(this, "Position Unavailable", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private LatLng convertLocationtoLatLang(Location location) {
        LatLng currentLatLang = new LatLng(location.getLatitude(),
                location.getLongitude());

        Log.e("CURRENT LATLONG", currentLatLang.toString());
        current_poistion = currentLatLang;
        return currentLatLang;

    }

    private void checkforGPSAndPromtOpen() {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        convertAddress(AddressBar.getText().toString());
    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter<String>
            implements Filterable {

        public GooglePlacesAutocompleteAdapter(Context context,
                                               int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint,
                                              FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    public static ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE
                    + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            // sb.append("&components=country:in");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());

            System.out.println("URL: " + url);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {

            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString(
                        "description"));
                System.out
                        .println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString(
                        "description"));

            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    public class MapTypeAdapter extends BaseAdapter {
        ArrayList<String> typeList;
        ArrayList<Integer> imageList;
        LayoutInflater inflater;

        public MapTypeAdapter(ArrayList<String> typeList,
                              ArrayList<Integer> imageList) {
            this.typeList = typeList;
            this.imageList = imageList;
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return typeList.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        class ViewHolder {
            TextView txvMapDetail;
            ImageView mapImage;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder;
            if (convertView == null) {

                convertView = inflater.inflate(R.layout.row_map_type, parent,
                        false);
                holder = new ViewHolder();
                holder.txvMapDetail = (TextView) convertView
                        .findViewById(R.id.txvMapName);
                holder.mapImage = (ImageView) convertView
                        .findViewById(R.id.imvMapImage);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //holder.txvMapDetail.setTypeface(mediumFont);
            holder.txvMapDetail.setText(typeList.get(position));
            holder.mapImage.setImageResource(imageList.get(position));
            return convertView;
        }

    }

}