package com.analytics.bonjourseller;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TrackingMapActivity extends FragmentActivity implements
        OnCameraChangeListener, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    public static ArrayList<String> shareUserID, shareLatitude, shareLongitude,
            shareComment, category_id, shareUserName, shareTripID, sellerid;
    GoogleMap googleMap;
    private LocationManager locationManager;
    private GoogleApiClient mLocationClient = null;
    private LocationRequest mLocationRequest = null;
    private static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND
            * UPDATE_INTERVAL_IN_SECONDS;
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND
            * FASTEST_INTERVAL_IN_SECONDS;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public static double past_latitude = 0.0, past_longitude = 0.0;

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

    Button hide;
    public static String user_id, trip_id;
    int cc = 0;
    public static boolean flage = false;
    public static TimerTask doAsynchronousTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracking_map_dialog);

        mLocationClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        servicesConnected();
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        // Getting a reference to the map

        googleMap = supportMapFragment.getMap();
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        googleMap.setMyLocationEnabled(true);
        // googleMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(
        // 15.7833, 47.8667)));

        googleMap.setOnCameraChangeListener(this);
        hide = (Button) findViewById(R.id.btnHide);
        hide.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        Intent i = getIntent();
        String selected_latitude = i.getStringExtra("LATITUDE");
        String selected_longitude = i.getStringExtra("LONGITUDE");
        user_id = i.getStringExtra("USER_ID");
        trip_id = i.getStringExtra("TRIP_ID");
        takeToLocation(new LatLng(Double.parseDouble(selected_latitude),
                Double.parseDouble(selected_longitude)));

        SQLiteDatabase mdatabase = openOrCreateDatabase("TRACK_DATABASE.db",
                Context.MODE_PRIVATE, null);

        String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS "
                + "TRACK_TABLE"
                + "(trip_id TEXT,latitude TEXT,longitude TEXT,username TEXT,userid TEXT)";
        mdatabase.execSQL(DATABASE_CREATE);

        mdatabase.close();
        setTrackingData(user_id, trip_id);

    }

    private boolean checkDataBase() {
        File PATH = getDatabasePath("TRACK_DATABASE.db");
        if (!PATH.exists()) {
            return false;
        } else {
            return true;
        }
    }

    public void setTrackingData(String track_user_id, String shared_trip_id) {
        boolean chkDb = checkDataBase();
        if (chkDb == true) {

            SQLiteDatabase DB = openOrCreateDatabase("TRACK_DATABASE.db",
                    Context.MODE_PRIVATE, null);
            Cursor c = DB.rawQuery(
                    "SELECT name FROM sqlite_master WHERE type='table'", null);
            cc = 0;
            while (c.moveToNext()) {
                String s = c.getString(0);
                if (s.compareTo("TRACK_TABLE") == 0) {
                    cc++;
                }
            }
            c.close();
            DB.close();
        }

        if (cc == 0 || chkDb == false) {
            sendFrequentData();
        } else {

            // datasource.open();
            SQLiteDatabase db = openOrCreateDatabase("TRACK_DATABASE.db",
                    Context.MODE_PRIVATE, null);
            try {

                Cursor cursor = db.rawQuery(
                        "SELECT * FROM TRACK_TABLE WHERE userid = '"
                                + track_user_id + "' AND trip_id = '"
                                + shared_trip_id + "'", null);

                cursor.moveToFirst();
                Log.e("chat user list size", "count:" + cursor.getCount());

                while (!cursor.isAfterLast()) {
                    Log.e("trip_id", cursor.getString(0));

                    Log.e("latitude", cursor.getString(1));

                    Log.e("longitude", cursor.getString(2));

                    Log.e("username", cursor.getString(3));

                    Log.e("userid", cursor.getString(4));
                    googleMap
                            .addMarker(new MarkerOptions()
                                    .position(
                                            new LatLng(Double
                                                    .parseDouble(cursor
                                                            .getString(1)),
                                                    Double.parseDouble(cursor
                                                            .getString(2))))
                                    .title(cursor.getString(3) + " Location")
                                    .icon(BitmapDescriptorFactory
                                            .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    if (flage == true) {
                        googleMap.addPolyline(new PolylineOptions()
                                .add(new LatLng(past_latitude, past_longitude),

                                        new LatLng(Double.parseDouble(cursor
                                                .getString(1)), Double
                                                .parseDouble(cursor
                                                        .getString(2))))
                                .width(7).color(Color.RED));
                    }
                    flage = true;

                    past_latitude = Double.parseDouble(cursor.getString(1));
                    past_longitude = Double.parseDouble(cursor.getString(2));
                    cursor.moveToNext();
                }
                cursor.close();
                sendFrequentData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCameraChange(CameraPosition arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLocationChanged(Location arg0) {
        // TODO Auto-generated method stub
        // setTrackingLocation(arg0.getLatitude() + "", arg0.getLongitude() +
        // "");
    }

    @Override
    public void onConnected(Bundle arg0) {
        // TODO Auto-generated method stub
        try {
            Location mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mLocationClient);
            // takeToLocation(convertLocationtoLatLang(mLastLocation));

        } catch (Exception e) {
            Log.e("error .current location", e + "");
        }
    }

    public void sendFrequentData() {

        final Handler handler = new Handler();
        Timer timer = new Timer();

        doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {

                            getUserData();

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 50000);

    }

    public void takeToLocation(LatLng toBeLocationLatLang) {
        if (toBeLocationLatLang != null) {
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
                    toBeLocationLatLang, 16);
            googleMap.animateCamera(update);
            // googleMap.clear();
            // googleMap.addMarker(new MarkerOptions()
            // .position(toBeLocationLatLang)
            // .title("My Location")
            // .icon(BitmapDescriptorFactory
            // .fromResource(R.drawable.orange_marker)));
            // getOfferData();
        } else {
            Toast.makeText(this, "Position Unavailable", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private LatLng convertLocationtoLatLang(Location location) {
        LatLng currentLatLang = new LatLng(location.getLatitude(),
                location.getLongitude());

        Log.e("CURRENT LATLONG", currentLatLang.toString());

        return currentLatLang;

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        // TODO Auto-generated method stub

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
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        doAsynchronousTask.cancel();
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
    public void onConnectionFailed(ConnectionResult arg0) {
        // TODO Auto-generated method stub

    }

	/*public void getMySharedLocation() {

		new AsyncTask<Void, Void, String>() {
			ProgressDialog mProgressDialog;

			protected void onPreExecute() {
				mProgressDialog = new ProgressDialog(TrackingMapActivity.this);
				mProgressDialog.setTitle("");
				mProgressDialog.setCanceledOnTouchOutside(false);
				mProgressDialog.setMessage("Please Wait...");
				mProgressDialog.show();
			};

			@SuppressWarnings("deprecation")
			@Override
			protected String doInBackground(Void... params) {
				// TODO Auto-generated method stub

				SharedPreferences mPrefs = getSharedPreferences("LOGIN_DETAIL",
						MODE_PRIVATE);
				String user_id = mPrefs.getString("USER_ID", "");

				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost;
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						1);

				httppost = new HttpPost(ApplicationData.serviceURL
						+ "get_seller_trip_location.php");
				StringBuffer sb = new StringBuffer();
				try {
					// Add your data
					nameValuePairs
							.add(new BasicNameValuePair("userid", user_id));
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

					// Execute HTTP Post Request
					HttpResponse response = httpclient.execute(httppost);
					BufferedReader in = new BufferedReader(
							new InputStreamReader(response.getEntity()
									.getContent()));
					sb = new StringBuffer("");
					String line = "";
					while ((line = in.readLine()) != null) {
						sb.append(line);
					}
					in.close();
					Log.e("get share Data", "" + sb.toString());
					return sb.toString();
				} catch (Exception e) {
					Log.e("problem share data getting", "" + e);
					return "";
				}

			}

			protected void onPostExecute(String result) {
				mProgressDialog.dismiss();
				// googleMap.clear();
				shareUserID = new ArrayList<String>();
				shareLatitude = new ArrayList<String>();
				shareLongitude = new ArrayList<String>();
				shareComment = new ArrayList<String>();
				shareUserName = new ArrayList<String>();
				shareTripID = new ArrayList<String>();

				SQLiteDatabase mdatabase = openOrCreateDatabase(
						"TRACK_DATABASE.db", Context.MODE_PRIVATE, null);
				try {
					JSONObject jsob = new JSONObject(result.toString());
					if (jsob.getString("msg").equalsIgnoreCase("Success")) {
						JSONArray datarray = jsob.getJSONArray("data");
						// Log.e("array length", "" + datarray.length());
						for (int i = 0; i < datarray.length(); i++) {
							JSONObject dataOb = datarray.getJSONObject(i);
							shareUserID.add(dataOb.getString("userid"));
							shareLatitude.add(dataOb.getString("latitude"));
							shareLongitude.add(dataOb.getString("longitude"));
							shareComment.add(dataOb.getString("comments"));
							shareUserName.add(dataOb.getString("username"));
							shareTripID.add(dataOb.getString("share_trip_id"));
							if (trip_id.equalsIgnoreCase(dataOb
									.getString("share_trip_id"))) {
								googleMap
										.addMarker(new MarkerOptions()
												.position(
														new LatLng(
																Double.parseDouble(dataOb
																		.getString("latitude")),
																Double.parseDouble(dataOb
																		.getString("longitude"))))
												.title(dataOb
														.getString("username")
														+ " Location")
												.icon(BitmapDescriptorFactory
														.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

								if (flage == true) {
									googleMap
											.addPolyline(new PolylineOptions()
													.add(new LatLng(
															past_latitude,
															past_longitude),

															new LatLng(
																	Double.parseDouble(dataOb
																			.getString("latitude")),
																	Double.parseDouble(dataOb
																			.getString("longitude"))))
													.width(7).color(Color.RED));
								}
								flage = true;

								past_latitude = Double.parseDouble(dataOb
										.getString("latitude"));
								past_longitude = Double.parseDouble(dataOb
										.getString("longitude"));
								takeToLocation(new LatLng(
										Double.parseDouble(dataOb
												.getString("latitude")),
										Double.parseDouble(dataOb
												.getString("longitude"))));
								mdatabase.beginTransaction();
								try {

									ContentValues values = new ContentValues();
									values.put("trip_id",
											dataOb.getString("share_trip_id"));
									values.put("latitude",
											dataOb.getString("latitude"));
									values.put("longitude",
											dataOb.getString("longitude"));
									values.put("username",
											dataOb.getString("username"));
									values.put("userid",
											dataOb.getString("userid"));

									mdatabase.insert("TRACK_TABLE", null,
											values);

									mdatabase.setTransactionSuccessful();
									Log.e("chat database",
											"TRACK COMMENT INSERTED");
								} catch (Exception e) {
									Log.e("chat database", "TRACK NOT INSERTED");

								} finally {
									mdatabase.endTransaction();

								}
							}
						}
						mdatabase.close();
					}
				} catch (Exception e) {
					Log.e("shar location set error", "" + e);
				}
			}
		}.execute();

	}

	public class ShareLocationAdapter extends BaseAdapter {

		LayoutInflater inflater;
		ArrayList<String> shareUserID, shareLatitude, shareLongitude,
				shareComment, shareUserName, shareTripId;

		int m = 0;

		public ShareLocationAdapter(ArrayList<String> shareUserID,
				ArrayList<String> shareLatitude,
				ArrayList<String> shareLongitude,
				ArrayList<String> shareComment,
				ArrayList<String> shareUserName, ArrayList<String> shareTripID) {
			// TODO Auto-generated constructor stub
			this.shareUserID = shareUserID;
			this.shareLatitude = shareLatitude;
			this.shareLongitude = shareLongitude;
			this.shareComment = shareComment;
			this.shareUserName = shareUserName;
			this.shareTripId = shareTripID;
			inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		class ViewHolder {
			TextView txvShareDesc;

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

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.txvShareDesc.setText(shareUserName.get(position)
					+ " shared location with you");
			holder.txvShareDesc.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
							new LatLng(Double.parseDouble(shareLatitude
									.get(position)), Double
									.parseDouble(shareLongitude.get(position))),
							12);
					googleMap.animateCamera(update);

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
	}*/

    @SuppressWarnings("deprecation")
    public void getUserData() {

        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "get_seller_trip_location.php";
        Log.e("url", url + "");
        final ProgressDialog mProgressDialog = new ProgressDialog(TrackingMapActivity.this);
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
                            shareUserID = new ArrayList<String>();
                            shareLatitude = new ArrayList<String>();
                            shareLongitude = new ArrayList<String>();
                            shareComment = new ArrayList<String>();
                            shareUserName = new ArrayList<String>();
                            shareTripID = new ArrayList<String>();

                            SQLiteDatabase mdatabase = openOrCreateDatabase("TRACK_DATABASE.db", Context.MODE_PRIVATE, null);

                            JSONObject jsob = new JSONObject(response.toString());
                            if (jsob.getString("msg").equalsIgnoreCase("Success")) {
                                JSONArray datarray = jsob.getJSONArray("data");

                                for (int i = 0; i < datarray.length(); i++) {
                                    JSONObject dataOb = datarray.getJSONObject(i);
                                    shareUserID.add(dataOb.getString("userid"));
                                    shareLatitude.add(dataOb.getString("latitude"));
                                    shareLongitude.add(dataOb.getString("longitude"));
                                    shareComment.add(dataOb.getString("comments"));
                                    shareUserName.add(dataOb.getString("username"));
                                    shareTripID.add(dataOb.getString("share_trip_id"));
                                    if (trip_id.equalsIgnoreCase(dataOb.getString("share_trip_id"))) {
                                        googleMap
                                                .addMarker(new MarkerOptions()
                                                        .position(new LatLng(Double.parseDouble(dataOb.getString("latitude")),
                                                                Double.parseDouble(dataOb.getString("longitude"))))
                                                        .title(dataOb.getString("username") + " Location")
                                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

                                        if (flage == true) {
                                            googleMap
                                                    .addPolyline(new PolylineOptions()
                                                            .add(new LatLng(past_latitude, past_longitude),
                                                                    new LatLng(Double.parseDouble(dataOb.getString("latitude")),
                                                                            Double.parseDouble(dataOb.getString("longitude"))))
                                                            .width(7).color(Color.RED));
                                        }
                                        flage = true;

                                        past_latitude = Double.parseDouble(dataOb.getString("latitude"));
                                        past_longitude = Double.parseDouble(dataOb.getString("longitude"));
                                        takeToLocation(new LatLng(Double.parseDouble(dataOb
                                                .getString("latitude")), Double.parseDouble(dataOb.getString("longitude"))));
                                        mdatabase.beginTransaction();
                                        try {
                                            ContentValues values = new ContentValues();
                                            values.put("trip_id", dataOb.getString("share_trip_id"));
                                            values.put("latitude", dataOb.getString("latitude"));
                                            values.put("longitude", dataOb.getString("longitude"));
                                            values.put("username", dataOb.getString("username"));
                                            values.put("userid", dataOb.getString("userid"));
                                            mdatabase.insert("TRACK_TABLE", null, values);
                                            mdatabase.setTransactionSuccessful();
                                            Log.e("chat database", "TRACK COMMENT INSERTED");
                                        } catch (Exception e) {
                                            Log.e("chat database", "TRACK NOT INSERTED");
                                        } finally {
                                            mdatabase.endTransaction();
                                        }
                                    }
                                }
                                mdatabase.close();
                            } else {
                                Toast.makeText(TrackingMapActivity.this,
                                        "Sorry! your offer not Forwarded. \n Please try again!",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception error) {
                            mProgressDialog.dismiss();
                            error.printStackTrace();
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                Toast.makeText(TrackingMapActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(TrackingMapActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(TrackingMapActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(TrackingMapActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences mPrefs = getSharedPreferences("LOGIN_DETAIL",
                        MODE_PRIVATE);
                String user_id = mPrefs.getString("USER_ID", "");
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", "" + user_id);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }
}
