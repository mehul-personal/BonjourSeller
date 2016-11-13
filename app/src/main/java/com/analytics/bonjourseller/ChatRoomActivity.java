package com.analytics.bonjourseller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatRoomActivity extends AppCompatActivity implements OnClickListener {
    private static final String TAG_BUYER_ID = "buyer_id";
    private static final String TAG_SELLER_ID = "seller_id";
    private static final String TAG_COMMENT = "comment";
    private static final String TAG_OFFER_ID = "offer_id";
    private static final String TAG_CATEGORY_ID = "category_id";
    private static final String TAG_USER_TYPE = "user_type";
    private static final String TAG_USERNAME = "name";
    private static final String TAG_CREATED_AT = "created_at";
    private static final String TAG_BUYER_PHOTO = "buyer_photo";
    private static final String TAG_SELLER_PHOTO = "seller_photo";
    //JSONParserNew jParser = new JSONParserNew();
    public static String buyer_id = "", seller_id = "", offer_id = "",
            categoryid = "", chat_status = "", ID = "", TO_ID = "";
    EditText editText;
    Button buttonSend;
    String strMsg = "";
    JSONArray contacts = null, contactslaters = null;
    ArrayList<HashMap<String, String>> contactList;
    int index;
    String stringStr = "", indexStr = "";
    Thread t;
    ListView listView1_comment;
    CommnetAdapter adapter;
    private ProgressDialog progressDialog;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                    listView1_comment = (ListView) findViewById(R.id.listView1);
                    listView1_comment.requestFocus();

                    adapter = new CommnetAdapter(ChatRoomActivity.this,
                            R.layout.list_chatroom, contactList);
                    listView1_comment.setAdapter(adapter);

                    adapter.notifyDataSetChanged();
                    listView1_comment.invalidate();

                    if (contactList.size() > 0) {
                        listView1_comment.setSelection(contactList.size() - 1);
                    }

                    break;

                case 1:
                    progressDialog.dismiss();
//                    Toast.makeText(ChatRoomActivity.this, "No Chat Here..",
//                            Toast.LENGTH_SHORT).show();
                    break;

                case 2:
                    editText.setText("");
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }


                    break;

                default:
                    break;
            }
        }
    };
    private SharedPreferences preferences;
    private Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);
        // ActionBar actionBar = getActionBar();
//		actionBar.setBackgroundDrawable(new ColorDrawable(getResources()
//				.getColor(R.color.blue_header)));
        preferences = getSharedPreferences("CHAT_COMMENT_PREFS", 0);
        editor = preferences.edit();

        editText = (EditText) findViewById(R.id.EditText_Comment);

        buttonSend = (Button) findViewById(R.id.Button_Send);
        buttonSend.setOnClickListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.app_name));

        Intent i = getIntent();
        chat_status = i.getStringExtra("STATUS");
        seller_id = getSharedPreferences("LOGIN_DETAIL", MODE_PRIVATE).getString("USER_ID", "0");
        if (chat_status.equalsIgnoreCase("SELLER")) {
                        offer_id = i.getStringExtra("OFFER_ID");
            categoryid = i.getStringExtra("CATEGORY_ID");
            buyer_id = i.getStringExtra("BUYER_ID");
            getSupportActionBar().setTitle(i.getStringExtra("BUYER_NAME"));
        } else if (chat_status.equalsIgnoreCase("PUSH")) {
            /** This comes from GCMIntentService **/
            seller_id=i.getStringExtra("SELLER_ID");
            buyer_id = i.getStringExtra("BUYER_ID");
            offer_id = i.getStringExtra("OFFER_ID");
            categoryid=i.getStringExtra("CATEGORY_ID");
            SQLiteDatabase mdatabase = openOrCreateDatabase("CHAT_DATABASE.db",
                    Context.MODE_PRIVATE, null);
            mdatabase
                    .delete("CHAT_COUNT",
                            "chat_seller_id=? AND chat_buyer_id=? AND chat_offer_id=? AND chat_category_id=?",
                            new String[]{seller_id, buyer_id, offer_id,
                                    categoryid});
        } /*else if (chat_status.equalsIgnoreCase("MULTI_USER_CHAT")) {
            *//** This comes from ChatUserList **//*
            buyer_id = i.getStringExtra("USER_ID");
            offer_id = i.getStringExtra("OFFER_ID");
            String chat_id = i.getStringExtra("CHAT_ID");

            editor.putString("chat_id_key", i.getStringExtra("CHAT_ID") + "");
            editor.commit();

            SQLiteDatabase mdatabase = openOrCreateDatabase("CHAT_DATABASE.db",
                    Context.MODE_PRIVATE, null);
            mdatabase
                    .delete("CHAT_COUNT",
                            "chat_seller_id=? AND chat_user_id=? AND chat_offer_id=? AND chat_id=?",
                            new String[]{seller_id, buyer_id, offer_id,
                                    chat_id});

        }*/

        // From Getting Data
        progressDialog = ProgressDialog.show(ChatRoomActivity.this, "",
                "Please wait...");
        new Thread(new Runnable() {
            public void run() {
                getCommnetData();
            }
        }).start();

        startThreadInBackground();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View arg0) {
        if (arg0 == buttonSend) {
            if (editText.getText().toString().equalsIgnoreCase("")) {
                Toast.makeText(ChatRoomActivity.this, "Please enter comment.",
                        Toast.LENGTH_SHORT).show();
            } else {
                progressDialog = ProgressDialog.show(ChatRoomActivity.this, "",
                        "Please wait...");
                new Thread(new Runnable() {
                    public void run() {
                        insertCommnetdata(editText.getText().toString());
                    }
                }).start();
            }
        }
    }

    public void getCommnetData() {

        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "user_chat_details_offer_wise.php";
        Log.e("url", url + "");
//        final ProgressDialog mProgressDialog = new ProgressDialog(ChatRoomActivity.this);
//        mProgressDialog.setTitle("");
//        mProgressDialog.setCanceledOnTouchOutside(false);
//        mProgressDialog.setMessage("Please Wait...");
//        mProgressDialog.show();
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("user_chat_det_off_wise", "" + response);
                        try {
                            progressDialog.dismiss();


                            JSONObject jsonObj = new JSONObject(response.toString());
                            if (jsonObj.getString("msg").equalsIgnoreCase("Success")) {
                                contactList = new ArrayList<HashMap<String, String>>();
                                // Getting JSON Array node
                                contacts = jsonObj.getJSONArray("data");
                                if (contacts.length() > 0) {
                                    for (int i = 0; i < contacts.length(); i++) {
                                        JSONObject c = contacts.getJSONObject(i);

                                        HashMap<String, String> map = new HashMap<String, String>();
                                        map.put(TAG_USER_TYPE, c.getString(TAG_USER_TYPE));
                                        map.put(TAG_COMMENT, c.getString(TAG_COMMENT));
                                        map.put(TAG_USERNAME, c.getString(TAG_USERNAME));
                                        map.put(TAG_OFFER_ID, c.getString(TAG_OFFER_ID));
                                        map.put(TAG_CATEGORY_ID, c.getString(TAG_CATEGORY_ID));
                                        map.put(TAG_CREATED_AT, c.getString(TAG_CREATED_AT));
                                        if (c.getString(TAG_USER_TYPE).equalsIgnoreCase("buyer")) {
                                            map.put("ID", c.getString(TAG_BUYER_ID));
                                            map.put("PHOTO", c.getString(TAG_BUYER_PHOTO));
                                        } else {
                                            map.put("ID", c.getString(TAG_SELLER_ID));
                                            map.put("PHOTO", c.getString(TAG_SELLER_PHOTO));
                                        }

                                        contactList.add(map);
                                    }

                                    mHandler.sendEmptyMessage(0);
                                } else {
                                    mHandler.sendEmptyMessage(1);
                                }

                            } else {
                                mHandler.sendEmptyMessage(1);
                            }

                        } catch (Exception error) {
                            progressDialog.dismiss();
                            error.printStackTrace();
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                Toast.makeText(ChatRoomActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ChatRoomActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(ChatRoomActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ChatRoomActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("category_id", categoryid);
                params.put("offer_id", offer_id);
                params.put("seller_id", seller_id);
                params.put("buyer_id", buyer_id);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);


    }

    // Refersh Evevry 10 second Chat API
    public void startThreadInBackground() {
        t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(10000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                new Thread(new Runnable() {
                                    public void run() {
                                        getCommnetData();
                                    }
                                }).start();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();
    }

    public void insertCommnetdata(final String commnet) {
        final String tag_json_obj = "json_obj_req";
        String url;
        url = ApplicationData.serviceURL + "buyer_seller_send_messages.php";
        Log.e("url", url + "");
        final StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("insert comment response", "" + response);
                        try {
                            progressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());
                            String msg = object.getString("msg");

                            if (msg.equalsIgnoreCase("Success")) {

                                getCommnetData();
                            } else {
                                Toast.makeText(ChatRoomActivity.this,
                                        "Sorry! we can't get your messages. \n Please try again!",
                                        Toast.LENGTH_SHORT).show();
                            }
                            mHandler.sendEmptyMessage(2);
                        } catch (Exception error) {
                            progressDialog.dismiss();
                            mHandler.sendEmptyMessage(2);
                            error.printStackTrace();
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                Toast.makeText(ChatRoomActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ChatRoomActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(ChatRoomActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ChatRoomActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("user_type", "seller");
                params.put("seller_id", seller_id);
                params.put("offer_id", offer_id);
                params.put("category_id", categoryid);
                params.put("push_message_buyer_id", buyer_id);
                params.put("comment", commnet);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            t.interrupt();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    class CommnetAdapter extends ArrayAdapter<HashMap<String, String>> {
        public CommnetAdapter(Context context, int textViewResourceId,
                              ArrayList<HashMap<String, String>> contactList) {
            super(context, textViewResourceId, contactList);
        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_chatroom, null);

            // / Sender
            LinearLayout laySender = (LinearLayout) rowView
                    .findViewById(R.id.LinearSender);
            LinearLayout layReceiver = (LinearLayout) rowView
                    .findViewById(R.id.LinearReceiver);

            ImageView senderPhoto = (ImageView) rowView
                    .findViewById(R.id.imvSenderPhoto);
            ImageView recevierPhoto = (ImageView) rowView
                    .findViewById(R.id.imvRecevierPhoto);


            if (contactList.size() > 0) {
                TextView senderCommnet = (TextView) rowView
                        .findViewById(R.id.Textview_sender_comment);
                senderCommnet.setText("" + contactList.get(position).get(TAG_COMMENT));
                // Receiver
                TextView receiveCommnet = (TextView) rowView
                        .findViewById(R.id.Textview_receiver_comment);
                receiveCommnet.setText("" + contactList.get(position).get(TAG_COMMENT));
                if (contactList.get(position).get(TAG_USER_TYPE).equalsIgnoreCase("seller")) {
                    if (contactList.get(position).get("PHOTO").isEmpty()) {
                        Picasso.with(ChatRoomActivity.this)
                                .load(R.drawable.ic_user)
                                .transform(new CircleTransform()).into(senderPhoto);

                    } else {
                        Picasso.with(ChatRoomActivity.this)
                                .load(contactList.get(position).get("PHOTO"))
                                .placeholder(R.drawable.ic_user)
                                .transform(new CircleTransform()).into(senderPhoto);
                    }
                    laySender.setVisibility(View.VISIBLE);
                } else {
                    if (contactList.get(position).get("PHOTO").isEmpty()) {
                        Picasso.with(ChatRoomActivity.this)
                                .load(R.drawable.ic_user)
                                .transform(new CircleTransform()).into(recevierPhoto);

                    } else {
                        Picasso.with(ChatRoomActivity.this)
                                .load(contactList.get(position).get("PHOTO"))
                                .placeholder(R.drawable.ic_user)
                                .transform(new CircleTransform()).into(recevierPhoto);
                    }
                    layReceiver.setVisibility(View.VISIBLE);
                }

                editText.requestFocus();
            }
            return rowView;
        }
    }

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

}
