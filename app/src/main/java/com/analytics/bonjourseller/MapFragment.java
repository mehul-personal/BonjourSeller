package com.analytics.bonjourseller;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnCameraChangeListener, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap googleMap;
    ExpandableListView offerList;
    ListView messageList;
    ArrayList<String> offerId, offerName, offerDesc, offerDistance, offerInvolvePeople,
            latList, longList, sellerid, offerType, category_id, imageCount;
    HashMap<String, ArrayList<String>> offerImage;
    ImageView imvOffers, imvMessage, imvSellerzone;
    TextView txvOffers, txvMessage, txvSellerzone;
    LinearLayout offerlistLayout;
    GPSTracker gpsTracker;
    private ArrayList<MyMarker> mMyMarkersArray = new ArrayList<MyMarker>();
    private HashMap<Marker, MyMarker> mMarkersHashMap;
    private static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND
            * UPDATE_INTERVAL_IN_SECONDS;
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND
            * FASTEST_INTERVAL_IN_SECONDS;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mLocationClient = null;
    private LocationRequest mLocationRequest = null;
    static LatLng currentLatLang;
    ArrayList<String> shareUserID, shareLatitude, shareLongitude, shareComment, shareUserName, shareTripID, sharing_type;

    private boolean servicesConnected() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getActivity());
        if (ConnectionResult.SUCCESS == resultCode) {
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    resultCode, getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
            if (errorDialog != null) {
                errorDialog.show();
            }
            return false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_maps, null, false);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        offerlistLayout = (LinearLayout) v.findViewById(R.id.llOfferMessage);
        offerList = (ExpandableListView) v.findViewById(R.id.elvOfferList);
        messageList = (ListView) v.findViewById(R.id.lvMessageList);
        imvMessage = (ImageView) v.findViewById(R.id.imvMessages);
        imvOffers = (ImageView) v.findViewById(R.id.imvOffers);
        imvSellerzone = (ImageView) v.findViewById(R.id.imvSellerZone);

        txvMessage = (TextView) v.findViewById(R.id.txvMessages);
        txvOffers = (TextView) v.findViewById(R.id.txvOffers);
        txvSellerzone = (TextView) v.findViewById(R.id.txvSellerZone);
        gpsTracker = new GPSTracker(getActivity());

        mMarkersHashMap = new HashMap<Marker, MyMarker>();

        mLocationClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        servicesConnected();
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        txvOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getOfferData();
            }
        });
        imvOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getOfferData();
            }
        });
        txvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getMessages();
            }
        });
        imvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getMessages();
            }
        });


        offerList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return false;
            }
        });

        // Listview Group expanded listener
        offerList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                takeToLocation(new LatLng(Double.parseDouble(latList.get(groupPosition)), Double.parseDouble(longList.get(groupPosition))));
            }
        });

        offerList.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
            }
        });

        // Listview on child click listener
        offerList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                return false;
            }
        });

        getOfferData();
        return v;

    }

    public void getOfferData() {
        if (gpsTracker != null) {
            if (gpsTracker.getLatitude() != 0 || gpsTracker.getLongitude() != 0) {
                String tag_json_obj = "json_obj_req";

                String url = ApplicationData.serviceURL + "get_run_offer.php";
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
                                Log.e("get_run_offer", "" + response);
                                try {
                                    mProgressDialog.dismiss();

                                    offerlistLayout.setVisibility(View.VISIBLE);
                                    offerList.setVisibility(View.VISIBLE);
                                    messageList.setVisibility(View.GONE);

                                    offerId = new ArrayList<String>();
                                    offerName = new ArrayList<String>();
                                    offerDesc = new ArrayList<String>();
                                    offerDistance = new ArrayList<String>();
                                    offerInvolvePeople = new ArrayList<String>();
                                    latList = new ArrayList<String>();
                                    longList = new ArrayList<String>();
                                    sellerid = new ArrayList<String>();
                                    offerType = new ArrayList<String>();
                                    category_id = new ArrayList<String>();
                                    imageCount = new ArrayList<String>();
                                    offerImage = new HashMap<String, ArrayList<String>>();
                                    mMyMarkersArray = new ArrayList<MyMarker>();

                                    JSONObject object = new JSONObject(response.toString());
                                    Log.e("2", "2");

                                    String msg = object.getString("msg");
                                    if (msg.equalsIgnoreCase("Success")) {
                                        JSONArray datarray = object.getJSONArray("data");
                                        // Log.e("array length", "" + datarray.length());
                                        for (int i = 0; i < datarray.length(); i++) {
                                            JSONObject dataOb = datarray.getJSONObject(i);
                                            offerId.add(dataOb.getString("offer_id"));
                                            offerName.add(dataOb.getString("name"));
                                            try {
                                                offerDesc.add(dataOb.getString("additional_text"));
                                            } catch (Exception e) {
                                                offerDesc.add(" ");
                                            }
                                            try {
                                                latList.add(dataOb.getString("latitude"));
                                                longList.add(dataOb.getString("longitude"));
                                                offerDistance.add(dataOb.getString("distance"));
                                            } catch (Exception e) {
                                                Log.e("latlong not found", "" + e);
                                                latList.add("0.00");
                                                longList.add("0.000");
                                                offerDistance.add("0");
                                            }
                                            sellerid.add(dataOb.getString("userid"));
                                            if (dataOb.getString("no_of_people").isEmpty()) {
                                                offerInvolvePeople.add("0");
                                            } else {
                                                offerInvolvePeople.add(dataOb
                                                        .getString("no_of_people"));
                                            }
                                            offerType.add(dataOb.getString("category_name"));
                                            imageCount.add(dataOb.getJSONArray("image_array")
                                                    .length() + "");
                                            category_id.add(dataOb.getString("category_id"));


                                            ArrayList<String> offerSubimage = new ArrayList<String>();
                                            JSONArray imgarr = dataOb
                                                    .getJSONArray("image_array");

                                            for (int j = 0; j < imgarr.length(); j++) {
                                                offerSubimage.add(imgarr.getString(j));
                                            }

                                            offerImage.put(dataOb.getString("category_id") + dataOb.getString("offer_id") + "",
                                                    offerSubimage);
                                        }

                                        for (int i = 0; i < datarray.length(); i++) {

                                            if (Integer.parseInt(imageCount.get(i)) <= 0) {
                                                mMyMarkersArray.add(new MyMarker(offerName.get(i), offerDesc.get(i),
                                                        offerInvolvePeople.get(i), "", Double.parseDouble(latList.get(i)),
                                                        Double.parseDouble(longList.get(i))));
                                            } else {
                                                int ic = Integer.parseInt(imageCount.get(i));
                                                if (ic <= 0) {
                                                    mMyMarkersArray.add(new MyMarker(offerName.get(i), offerDesc.get(i),
                                                            offerInvolvePeople.get(i), "", Double.parseDouble(latList.get(i)),
                                                            Double.parseDouble(longList.get(i))));
                                                } else {
                                                    mMyMarkersArray
                                                            .add(new MyMarker(offerName.get(i), offerDesc.get(i), offerInvolvePeople.get(i),
                                                                    offerImage.get(category_id.get(i) + offerId.get(i)).get(0),
                                                                    Double.parseDouble(latList.get(i)),
                                                                    Double.parseDouble(longList.get(i))));
                                                }
                                            }
                                        }
                                        Log.e("post data", "1 size:" + mMyMarkersArray.size());
                                        plotMarkers(mMyMarkersArray);
                                        Log.e("post data", "2");
                                        // sellerData();


                                        ExpandableListAdapter adapter = new ExpandableListAdapter(
                                                getActivity(), offerName, offerDesc,
                                                offerDistance, offerInvolvePeople, category_id);
                                        offerList.setAdapter(adapter);

                                    } else {
                                        Toast.makeText(getActivity(),
                                                "Oopss! Login failure please try again",
                                                Toast.LENGTH_LONG).show();
                                    }
                                } catch (Exception error) {
                                    mProgressDialog.dismiss();
                                    error.printStackTrace();
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
                        params.put("latitude", gpsTracker.getLatitude() + "");
                        params.put("longitude", "" + gpsTracker.getLongitude());
                        return params;
                    }
                };
                jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
                // Adding request to request queue
                ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                        tag_json_obj);
            }
        }
    }

    private void plotMarkers(ArrayList<MyMarker> markers) {
        int i = 0;
        if (markers.size() > 0) {
            for (MyMarker myMarker : markers) {

                // Create user marker with custom icon and other options
                MarkerOptions markerOption = new MarkerOptions()
                        .position(new LatLng(myMarker.getmLatitude(), myMarker
                                .getmLongitude()));
                markerOption.snippet(i + "");
                Log.e("id", "id:" + offerId.get(i));
                if (offerType.get(i).equalsIgnoreCase("Food / Restaurant")) {
                    markerOption.icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.ic_marker_restaruent));
                } else if (offerType.get(i).equalsIgnoreCase("Real Estate")) {
                    markerOption.icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.ic_marker_realestate));
                } else if (offerType.get(i).equalsIgnoreCase("Merchant")) {
                    markerOption.icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.ic_marker_merchant));
                } else {
                    markerOption.icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.ic_marker_merchant));
                }
                Marker currentMarker = googleMap.addMarker(markerOption);
                mMarkersHashMap.put(currentMarker, myMarker);

                // googleMap.setInfoWindowAdapter(new
                // MarkerInfoWindowAdapter());
                i++;
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        this.googleMap.setMyLocationEnabled(true);

        this.googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {
                View v = getActivity().getLayoutInflater().inflate(
                        R.layout.marker_info_window_layout, null);
                if (arg0.getTitle() != null) {
                    if (arg0.getTitle().equalsIgnoreCase("My Location")
                            || arg0.getTitle().contains("Location")) {
                        return null;
                    } else {
                        return null;
                    }
                } else {
                    MyMarker myMarker = mMarkersHashMap.get(arg0);
                    // Getting the position from the marker
                    LatLng latLng = arg0.getPosition();

                    TextView tvTitle = (TextView) v.findViewById(R.id.txvPlaceTitle);
                    TextView tvDesc = (TextView) v.findViewById(R.id.txvPlaceDescription);
                    TextView tvNoOfPeople = (TextView) v.findViewById(R.id.txvPlaceNoOfPeople);
                    Button moreOffer = (Button) v.findViewById(R.id.btnMoreInfo);

                    tvTitle.setText(myMarker.gettitle());
                    tvDesc.setText(myMarker.getdescription());
                    tvNoOfPeople.setText("Offer Available into " + myMarker.getNoOfPeople() + " people");
                    return v;
                }
            }
        });
        this.googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker arg0) {
                // TODO Auto-generated method stub
                Log.e("MAP CLICK", "id:" + arg0.getSnippet());
                if (arg0.getSnippet() != null) {
                    int selectedPosition = Integer.parseInt(arg0.getSnippet());
                    Intent i = new Intent(getActivity(),
                            OfferDialogActivity.class);
                    i.putExtra("OFFER_ID", offerId.get(selectedPosition));
                    i.putExtra("OFFER_NAME", offerName.get(selectedPosition));
                    i.putExtra("OFFER_DISTANCE", offerDistance.get(selectedPosition));
                    i.putExtra("OFFER_DESCRIPTION",
                            offerDesc.get(selectedPosition));
                    i.putExtra("INVOLVE_PEOPLE",
                            offerInvolvePeople.get(selectedPosition));
                    i.putExtra("CURRENT_LATITUDE", currentLatLang.latitude + "");
                    i.putExtra("CURRENT_LONGITUDE", currentLatLang.longitude
                            + "");
                    i.putExtra("OFFER_LATITUDE", latList.get(selectedPosition)
                            + "");
                    i.putExtra("CATEGORY_ID", category_id.get(selectedPosition));
                    i.putExtra("OFFER_LONGITUDE",
                            longList.get(selectedPosition) + "");
                    i.putExtra("SELLER_ID", sellerid.get(selectedPosition));
                    i.putStringArrayListExtra("IMAGE",
                            offerImage.get(category_id.get(selectedPosition) + offerId.get(selectedPosition))
                    );
                    startActivity(i);
                }

            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            Location mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mLocationClient);
            takeToLocation(convertLocationtoLatLang(mLastLocation));

        } catch (Exception e) {
            Log.e("error current location", e + "");
        }
    }

    public void takeToLocation(LatLng toBeLocationLatLang) {
        if (toBeLocationLatLang != null) {
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
                    toBeLocationLatLang, 16);
            googleMap.animateCamera(update);

        }
    }

    private LatLng convertLocationtoLatLang(Location location) {
        currentLatLang = new LatLng(location.getLatitude(),
                location.getLongitude());

        Log.e("CURRENT LATLONG", currentLatLang.toString());

        return currentLatLang;

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onStart() {
        super.onStart();
        mLocationClient.connect();
    }

    @Override
    public void onStop() {
        mLocationClient.disconnect();
        super.onStop();
    }

    public void getMessages() {
        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "get_seller_trip_location.php";
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
                        Log.e("forward_offer response", "" + response);
                        try {
                            mProgressDialog.dismiss();
                            offerlistLayout.setVisibility(View.VISIBLE);
                            messageList.setVisibility(View.VISIBLE);
                            offerList.setVisibility(View.GONE);

                            shareUserID = new ArrayList<String>();
                            shareLatitude = new ArrayList<String>();
                            shareLongitude = new ArrayList<String>();
                            shareComment = new ArrayList<String>();
                            shareUserName = new ArrayList<String>();
                            shareTripID = new ArrayList<String>();
                            sharing_type = new ArrayList<String>();

                            JSONObject object = new JSONObject(response.toString());

                            String msg = object.getString("msg");

                            if (msg.equalsIgnoreCase("Success")) {
                                JSONArray datarray = object.getJSONArray("data");
                                // Log.e("array length", "" + datarray.length());
                                for (int i = 0; i < datarray.length(); i++) {
                                    JSONObject dataOb = datarray.getJSONObject(i);
                                    shareUserID.add(dataOb.getString("userid"));
                                    shareLatitude.add(dataOb.getString("latitude"));
                                    shareLongitude.add(dataOb.getString("longitude"));
                                    shareComment.add(dataOb.getString("comments"));
                                    shareUserName.add(dataOb.getString("username"));
                                    shareTripID.add(dataOb.getString("share_trip_id"));
                                    sharing_type.add(dataOb.getString("sharing_type"));
                                }
                                MessageLocationAdapter adapter = new MessageLocationAdapter(
                                        shareUserID, shareLatitude, shareLongitude,
                                        shareComment, shareUserName, shareTripID,
                                        sharing_type);
                                messageList.setAdapter(adapter);
                            } else {
                                Toast.makeText(getActivity(),
                                        "Sorry! we can't get your messages. \n Please try again!",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception error) {
                            mProgressDialog.dismiss();
                            error.printStackTrace();
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
                SharedPreferences mPrefs = getActivity().getSharedPreferences("LOGIN_DETAIL",
                        getActivity().MODE_PRIVATE);
                String user_id = mPrefs.getString("USER_ID", "");
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", "" + user_id);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);


    }

    public class MessageLocationAdapter extends BaseAdapter {

        LayoutInflater inflater;
        ArrayList<String> shareUserID, shareLatitude, shareLongitude,
                shareComment, shareUserName, shareTripId, shareType;

        int m = 0;

        public MessageLocationAdapter(ArrayList<String> shareUserID,
                                      ArrayList<String> shareLatitude,
                                      ArrayList<String> shareLongitude,
                                      ArrayList<String> shareComment,
                                      ArrayList<String> shareUserName, ArrayList<String> shareTripID,
                                      ArrayList<String> shareType) {
            // TODO Auto-generated constructor stub
            this.shareUserID = shareUserID;
            this.shareLatitude = shareLatitude;
            this.shareLongitude = shareLongitude;
            this.shareComment = shareComment;
            this.shareUserName = shareUserName;
            this.shareTripId = shareTripID;
            this.shareType = shareType;
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        class ViewHolder {
            TextView txvShareDesc;
            ImageView messageIcon;
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
                        R.layout.row_message_item, parent, false);
                holder = new ViewHolder();
                holder.txvShareDesc = (TextView) convertView
                        .findViewById(R.id.txvShareLocationDescription);
                holder.messageIcon = (ImageView) convertView.findViewById(R.id.imvMessageIcon);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (shareType.get(position).equalsIgnoreCase("NAVIGATE")) {
                holder.txvShareDesc.setText(shareUserName.get(position)
                        + " shared location with you");
                holder.messageIcon.setImageResource(R.drawable.ic_white_navigate);
            } else {
                holder.txvShareDesc.setText(shareUserName.get(position)
                        + " shared Tracking location with you");
                holder.messageIcon.setImageResource(R.drawable.ic_white_tracking);
            }
            holder.txvShareDesc.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    if (shareType.get(position).equalsIgnoreCase("NAVIGATE")) {
                        Intent intent = new Intent(
                                android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?saddr="
                                        + currentLatLang.latitude + ","
                                        + currentLatLang.longitude + "&daddr="
                                        + shareLatitude.get(position) + ","
                                        + shareLongitude.get(position)));
                        intent.setClassName("com.google.android.apps.maps",
                                "com.google.android.maps.MapsActivity");
                        startActivity(intent);
                    } else {
                        Intent i = new Intent(getActivity(),
                                TrackingMapActivity.class);
                        i.putExtra("LATITUDE", shareLatitude.get(position) + "");
                        i.putExtra("LONGITUDE", shareLongitude.get(position) + "");
                        i.putExtra("USER_ID", shareUserID.get(position) + "");
                        i.putExtra("TRIP_ID", shareTripId.get(position) + "");
                        startActivity(i);
                    }
                }
            });
            return convertView;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return shareUserID.size();
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
}
