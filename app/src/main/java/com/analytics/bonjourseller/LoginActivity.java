package com.analytics.bonjourseller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    ImageView close;
    TextView signup, forgotPasword, txvUser, txvLogin;
    Button login;
    EditText phone, password;
    Typeface ALight, ARegular, ASemi, RLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        close = (ImageView) findViewById(R.id.imvClose);
        signup = (TextView) findViewById(R.id.txvSignUp);
        login = (Button) findViewById(R.id.btnLogin);
        forgotPasword = (TextView) findViewById(R.id.txvForgotPassword);
        txvUser = (TextView) findViewById(R.id.txvUser);
        txvLogin = (TextView) findViewById(R.id.txvLogin);
        phone = (EditText) findViewById(R.id.edtPhone);
        password = (EditText) findViewById(R.id.edtPassword);


        ALight = Typeface.createFromAsset(getAssets(),
                "Aileron-Light.otf");
        ARegular = Typeface.createFromAsset(getAssets(),
                "Aileron-Regular.otf");
        ASemi = Typeface.createFromAsset(getAssets(),
                "Aileron-SemiBold.otf");
        RLight = Typeface.createFromAsset(getAssets(),
                "roboto-light-webfont.ttf");

        txvUser.setTypeface(RLight, Typeface.BOLD);
        txvLogin.setTypeface(RLight, Typeface.BOLD);
        forgotPasword.setTypeface(ALight);
        phone.setTypeface(ALight);
        password.setTypeface(ALight);
        signup.setTypeface(ARegular, Typeface.BOLD);
        login.setTypeface(RLight, Typeface.BOLD);
        SharedPreferences mPrefs = getSharedPreferences(
                "LOGIN_DETAIL", MODE_PRIVATE);
        if (!mPrefs.getString("USER_ID", "").isEmpty()) {
            Intent i = new Intent(LoginActivity.this,
                    MainActivity.class);
            startActivity(i);
            finish();
        }

        forgotPasword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(i);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter phone", Toast.LENGTH_LONG)
                            .show();
                } else if (password.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter password", Toast.LENGTH_LONG)
                            .show();
                } else {
                    signIn();
                }
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });
    }

    public void signIn() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "login.php";
        Log.e("url", url + "");
        final ProgressDialog mProgressDialog = new ProgressDialog(LoginActivity.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("response", "" + response);
                        try {
                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());
                            Log.e("2", "2");

                            String msg = object.getString("msg");
                            if (msg.equalsIgnoreCase("Success")) {
                                JSONObject dataOb= object.getJSONObject("data");
                                String id = dataOb.getString("id");
                                String name = dataOb.getString("name");
                                String address = dataOb.getString("address");
                                String state = dataOb.getString("state");
                                String city = dataOb.getString("city");
                                String phone = dataOb.getString("phone");
                                String email = dataOb.getString("email");

                                SharedPreferences mPrefs = getSharedPreferences(
                                        "LOGIN_DETAIL", MODE_PRIVATE);
                                SharedPreferences.Editor edit = mPrefs.edit();
                                edit.putString("USER_ID", id);
                                edit.putString("NAME", name);
                                edit.putString("ADDRESS", address);
                                edit.putString("STATE", state);
                                edit.putString("CITY", city);
                                edit.putString("PHONE", phone);
                                edit.putString("EMAIL", email);
                                edit.putString("PASSWORD", password.getText().toString());
                                edit.putString("IMAGE_URL", dataOb.getString("image_name"));
                                edit.commit();

                                new GcmRegistrationAsyncTask(LoginActivity.this).execute();
                                Intent i = new Intent(LoginActivity.this,
                                        MainActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Oopss! Login failure please try again",
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception error) {
                            mProgressDialog.dismiss();
                            error.printStackTrace();
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                Toast.makeText(LoginActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(LoginActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("phone", phone
                        .getText().toString());
                params.put("password", "" + password.getText().toString());
                params.put("user_type", "seller");
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }
}




