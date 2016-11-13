package com.analytics.bonjourseller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends Activity {
    ImageView close;
    TextView backLogin, sendEmail;
    EditText edtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        close = (ImageView) findViewById(R.id.imvClose);
        backLogin = (TextView) findViewById(R.id.txvBackLogin);
        sendEmail = (TextView) findViewById(R.id.txvEmail);
        edtEmail = (EditText) findViewById(R.id.edtEmailAddress);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        backLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendForgotPasswordDetails();
            }
        });
    }

    public void sendForgotPasswordDetails() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "forgot_password.php";
        Log.e("url", url + "");
        final ProgressDialog mProgressDialog = new ProgressDialog(ForgotPasswordActivity.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("forgot_password", "" + response);
                        try {
                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());
                            Log.e("2", "2");

                            String msg = object.getString("msg");
                            if (msg.equalsIgnoreCase("Success")) {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Your reset password link send your email address!",
                                        Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Oopss! Your Reset password process is failure",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception error) {
                            mProgressDialog.dismiss();
                            Log.e("forgot_password", "" + error);
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                Toast.makeText(ForgotPasswordActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ForgotPasswordActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressDialog.dismiss();
                Log.e("forgot_password", "" + error);
                error.getCause();
                error.printStackTrace();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", "" + edtEmail.getText().toString());
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }
}
