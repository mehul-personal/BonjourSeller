package com.analytics.bonjourseller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import java.util.HashMap;
import java.util.Map;

public class ChatUserHistoryActivity extends AppCompatActivity {
    ListView lvChatUserHistory;
    String category_id, offer_id, seller_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_user_history);

        lvChatUserHistory = (ListView) findViewById(R.id.lvChatUserHistory);
        Intent i = getIntent();
        i.getStringExtra("STATUS");
        category_id = i.getStringExtra("CATEGORY_ID");
        offer_id = i.getStringExtra("OFFER_ID");
        seller_id = getSharedPreferences("LOGIN_DETAIL", MODE_PRIVATE).getString("USER_ID", "0");

        sellerChatHistory();
    }

    public void sellerChatHistory() {

        String tag_json_obj = "json_obj_req";
        String url = ApplicationData.serviceURL + "seller_chat_with_buyer_details.php";
        Log.e("url", url + "");
        final ProgressDialog mProgressDialog = new ProgressDialog(ChatUserHistoryActivity.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("seller chat with buyer", response.toString());
                        ArrayList<String> buyer_id = new ArrayList<String>();
                        ArrayList<String> buyer_photo = new ArrayList<String>();
                        ArrayList<String> buyer_name = new ArrayList<String>();
                        try {

                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());

                            String msg = object.getString("msg");
                            if (msg.equalsIgnoreCase("Success")) {
                                JSONArray dataArr = object.getJSONArray("data");
                                for (int i = 0; i < dataArr.length(); i++) {
                                    JSONObject dataOb = dataArr.getJSONObject(i);
                                    buyer_id.add(dataOb.getString("buyer_id"));
                                    buyer_name.add(dataOb.getString("name"));
                                    buyer_photo.add(dataOb.getString("buyer_photo"));
                                }
                                UserChatHistoryAdapter adapter = new UserChatHistoryAdapter(buyer_id, buyer_name, buyer_photo);
                                lvChatUserHistory.setAdapter(adapter);
                            } else {
                                Toast.makeText(ChatUserHistoryActivity.this,
                                        "Sorry! we are stuff to fetching data. \n Please try again!",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(ChatUserHistoryActivity.this,
                                    "Sorry! we are stuff to fetching data. \n Please try again!",
                                    Toast.LENGTH_SHORT).show();
                            if (e instanceof TimeoutError || e instanceof NoConnectionError) {
                                Toast.makeText(ChatUserHistoryActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ChatUserHistoryActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                VolleyLog.e("seller chat with buyer Error",
                        "Error: " + error.getMessage());
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(ChatUserHistoryActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ChatUserHistoryActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("category_id", category_id);
                params.put("offer_id", offer_id);
                params.put("seller_id", seller_id);
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public class UserChatHistoryAdapter extends BaseAdapter {

        LayoutInflater inflater;
        ArrayList<String> buyerID, buyerName, buyerPhoto;

        int m = 0;

        public UserChatHistoryAdapter(ArrayList<String> buyerID,
                                      ArrayList<String> buyerName, ArrayList<String> buyerPhoto) {
            // TODO Auto-generated constructor stub
            this.buyerID = buyerID;
            this.buyerName = buyerName;
            this.buyerPhoto = buyerPhoto;
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

                convertView = inflater.inflate(R.layout.row_user_chat_history, parent,
                        false);
                holder = new ViewHolder();
                holder.txvUserName = (TextView) convertView
                        .findViewById(R.id.txvUserName);
                holder.txvUserTime = (TextView) convertView
                        .findViewById(R.id.txvUserTime);
                holder.userPhoto = (ImageView) convertView.findViewById(R.id.imvUserPhoto);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.txvUserName.setText(buyerName.get(position));
            if (!buyerPhoto.get(position).isEmpty()) {
                Picasso.with(ChatUserHistoryActivity.this)
                        .load(buyerPhoto.get(position))
                        .placeholder(R.drawable.ic_user)
                        .transform(new CircleTransform()).into(holder.userPhoto);
            } else {
                Picasso.with(ChatUserHistoryActivity.this)
                        .load(R.drawable.ic_user)
                        .transform(new CircleTransform()).into(holder.userPhoto);
            }

            holder.txvUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callChatRoom(position);
                }
            });
            holder.txvUserTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callChatRoom(position);
                }
            });
            holder.userPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callChatRoom(position);
                }
            });
            return convertView;
        }

        public void callChatRoom(int position) {
            Intent i = new Intent(ChatUserHistoryActivity.this, ChatRoomActivity.class);
            i.putExtra("STATUS", "SELLER");
            i.putExtra("OFFER_ID", offer_id + "");
            i.putExtra("CATEGORY_ID", category_id + "");
            i.putExtra("BUYER_ID", buyerID.get(position) + "");
            i.putExtra("BUYER_NAME",buyerName.get(position)+"");
            startActivity(i);

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return buyerID.size();
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

        class ViewHolder {
            TextView txvUserName, txvUserTime;
            ImageView userPhoto;
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
