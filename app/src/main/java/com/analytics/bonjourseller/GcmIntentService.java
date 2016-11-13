package com.analytics.bonjourseller;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by techiestown on 17/6/15.
 */
public class GcmIntentService extends IntentService {
    static final String PACKAGE_NAME = "com.analytics.bonjourseller";
    static final String ACTIVITY_NAME = "com.analytics.bonjourseller.SplashScreenActivity";
    static final String CHAT_ACTIVITY_NAME = "com.analytics.bonjourseller.ChatRoomActivity";
    public static int notify_id = 0;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (extras != null && !extras.isEmpty()) {  // has effect of unparcelling Bundle
            // Since we're not using two way messaging, this is all we really to check for
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Logger.getLogger("GCM_RECEIVED").log(Level.INFO, extras.toString());

                // showToast(extras.getString("message"));
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
        generateNotification(getApplicationContext(), extras.getString("message"));
    }

    protected void showToast(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }


    public void generateNotification(Context context, String message) {
        notify_id++;
        storeDataOnDatabase(message);

        try {
            if (message != null || !message.isEmpty() || message.length() != 0) {

                if (message.contains("||")) {
                    displayChatNotification(context, message);
                } else {
                    int icon = R.mipmap.ic_launcher;
                    long when = System.currentTimeMillis();
                    NotificationManager notificationManager = (NotificationManager) context
                            .getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification notification = new Notification(icon, message, when);
                    String title = context.getString(R.string.app_name);
                    Intent notificationIntent = new Intent(Intent.ACTION_MAIN);
                    notificationIntent.setComponent(new ComponentName(PACKAGE_NAME, ACTIVITY_NAME));
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    PendingIntent intent = PendingIntent.getActivity(context, 0,
                            notificationIntent, 0);
                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
                    notification.defaults |= Notification.DEFAULT_SOUND;
                    notification.defaults |= Notification.DEFAULT_VIBRATE;
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                    builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setAutoCancel(true)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setWhen(when)
                            .setContentIntent(PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                                    //.setDeleteIntent(PendingIntent.getBroadcast(context, 0, new Intent(Intent.ACTION_CLEAR_NOTIFICATION), PendingIntent.FLAG_CANCEL_CURRENT))
                            .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                    notificationManager.notify(notify_id, builder.build());
                }
            }
        } catch (Exception e) {
            Log.e("exception notification", "" + e);
        }
    }

    public void displayChatNotification(Context context, String message) {
        ArrayList<String> strmsg = new ArrayList<String>();
        int icon = R.mipmap.ic_launcher;
        long when = System.currentTimeMillis();
        String title = context.getString(R.string.app_name);
        StringTokenizer st = new StringTokenizer(message, "||");
        while (st.hasMoreTokens()) {
            strmsg.add(st.nextToken());
        }
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, strmsg.get(0), when);
        Intent notificationIntent = new Intent(Intent.ACTION_MAIN);
        notificationIntent.setComponent(new ComponentName(
                PACKAGE_NAME, CHAT_ACTIVITY_NAME));
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.putExtra("STATUS", "PUSH");
        try {
            notificationIntent.putExtra("SELLER_ID", "" + strmsg.get(1));
        } catch (Exception e) {
        }
        try {
            notificationIntent.putExtra("OFFER_ID", "" + strmsg.get(2));
        } catch (Exception e) {
        }
        try {
            notificationIntent.putExtra("CATEGORY_ID", "" + strmsg.get(3));
        } catch (Exception e) {
        }
        try {
            notificationIntent.putExtra("BUYER_ID", "" + strmsg.get(4));
        } catch (Exception e) {
        }
        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon))
                .setSmallIcon(icon)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(strmsg.get(0))
                .setWhen(when)
                .setContentIntent(PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                        //.setDeleteIntent(PendingIntent.getBroadcast(context, 0, new Intent(Intent.ACTION_CLEAR_NOTIFICATION), PendingIntent.FLAG_CANCEL_CURRENT))
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        notificationManager.notify(notify_id, builder.build());
    }

    public void storeDataOnDatabase(String message) {

        try {
            if (message != null || !message.isEmpty() || message.length() != 0) {
                ArrayList<String> strmsg = new ArrayList<String>();
                StringTokenizer st = new StringTokenizer(message, "|");
                while (st.hasMoreTokens()) {
                    strmsg.add(st.nextToken());
                }
                String comment = "", seller_id = "", offer_id = "", category_id = "", buyer_id = "";
                try {
                    comment = strmsg.get(0);
                } catch (Exception e) {
                    comment = "";
                }
                try {
                    seller_id = strmsg.get(1);
                } catch (Exception e) {
                    seller_id = "";
                }
                try {
                    offer_id = strmsg.get(2);
                } catch (Exception e) {
                    offer_id = "";
                }
                try {
                    category_id= strmsg.get(3);
                } catch (Exception e) {
                    category_id= "";
                }
                try {
                    buyer_id = strmsg.get(4);
                } catch (Exception e) {
                    buyer_id = "";
                }

                SharedPreferences chat_prefence = getSharedPreferences(
                        "CHAT_PREFERENCE", MODE_PRIVATE);
                SharedPreferences.Editor edit = chat_prefence.edit();

                edit.putString("CHAT_SELLER_ID", "" + seller_id);
                edit.putString("CHAT_OFFER_ID", "" + offer_id);
                edit.putString("CHAT_CATEGORY_ID", "" + category_id);
                edit.putString("CHAT_BUYER_ID", "" + buyer_id);
                edit.commit();

                SQLiteDatabase mdatabase = openOrCreateDatabase(
                        "CHAT_DATABASE.db", Context.MODE_PRIVATE, null);

                String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS "
                        + "CHAT_TABLE"
                        + "(iID INTEGER PRIMARY KEY AUTOINCREMENT,chat_seller_id TEXT,chat_offer_id TEXT,chat_category_id TEXT,chat_buyer_id TEXT,chat_comment TEXT)";

                String DATABASE_COUNT = "CREATE TABLE IF NOT EXISTS "
                        + "CHAT_COUNT"
                        + "(iID INTEGER PRIMARY KEY AUTOINCREMENT,chat_seller_id TEXT,chat_offer_id TEXT,chat_category_id TEXT,chat_buyer_id TEXT,chat_comment TEXT)";

                mdatabase.execSQL(DATABASE_CREATE);
                mdatabase.execSQL(DATABASE_COUNT);
                mdatabase.beginTransaction();
                try {

                    ContentValues values = new ContentValues();
                    values.put("chat_seller_id", seller_id);
                    values.put("chat_offer_id", offer_id);
                    values.put("chat_category_id", category_id);
                    values.put("chat_buyer_id", buyer_id);
                    values.put("chat_comment", comment);

                    mdatabase.insert("CHAT_TABLE", null, values);
                    mdatabase.insert("CHAT_COUNT", null, values);
                    mdatabase.setTransactionSuccessful();
                    Log.e("chat database", "CHAT COMMENT INSERTED");
                } catch (Exception e) {
                    Log.e("chat database", "CHAT NOT INSERTED");

                } finally {
                    mdatabase.endTransaction();
                    mdatabase.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}