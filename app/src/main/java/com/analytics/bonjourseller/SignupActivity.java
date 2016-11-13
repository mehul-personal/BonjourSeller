package com.analytics.bonjourseller;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class SignupActivity extends AppCompatActivity {
    Button signup;
    ImageView close;
    EditText name, address, email, mobile, confirmpassword, password, captcha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        close = (ImageView) findViewById(R.id.imvClose);
        name = (EditText) findViewById(R.id.edtName);
        address = (EditText) findViewById(R.id.edtAddress);
        email = (EditText) findViewById(R.id.edtEmail);
        mobile = (EditText) findViewById(R.id.edtMobile);
        password = (EditText) findViewById(R.id.edtPassword);
        confirmpassword = (EditText) findViewById(R.id.edtConfirmPassword);
        signup = (Button) findViewById(R.id.btnSignUp);
        captcha = (EditText) findViewById(R.id.edtCaptcha);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().isEmpty()) {
                    name.setError("Please fill out space");
                    Toast.makeText(SignupActivity.this,
                            "Sorry! Name must be require", Toast.LENGTH_LONG)
                            .show();
                } else if (address.getText().toString().isEmpty()) {
                    address.setError("Please fill out space");
                    Toast.makeText(SignupActivity.this,
                            "Sorry! Address must be require", Toast.LENGTH_LONG)
                            .show();
                } else if (email.getText().toString().isEmpty()) {
                    email.setError("Please fill out space");
                    Toast.makeText(SignupActivity.this,
                            "Sorry! Email must be require", Toast.LENGTH_LONG)
                            .show();
                } else if (mobile.getText().toString().isEmpty()) {
                    mobile.setError("Please fill out space");
                    Toast.makeText(SignupActivity.this,
                            "Sorry! Mobile must be require", Toast.LENGTH_LONG)
                            .show();
                } else if (password.getText().toString().isEmpty()) {
                    password.setError("Please fill out space");
                    Toast.makeText(SignupActivity.this,
                            "Sorry! Password must be require",
                            Toast.LENGTH_LONG).show();
                } else if (confirmpassword.getText().toString().isEmpty()) {
                    confirmpassword.setError("Please fill out space");
                    Toast.makeText(SignupActivity.this,
                            "Sorry! Confirm password must be require",
                            Toast.LENGTH_LONG).show();
                } else if (captcha.getText().toString().isEmpty()) {
                    captcha.setError("Please fill out space");
                    Toast.makeText(SignupActivity.this,
                            "Sorry! Captcha code must be require",
                            Toast.LENGTH_LONG).show();
                } else if (!isValidEmail(email.getText().toString())) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter valid Email", Toast.LENGTH_LONG)
                            .show();
                } else if (!password.getText().toString()
                        .equalsIgnoreCase(confirmpassword.getText().toString())) {
                    Toast.makeText(SignupActivity.this,
                            "Sorry! Please check your password",
                            Toast.LENGTH_LONG).show();
                } else if (!captcha.getText().toString().equalsIgnoreCase("W68HP")) {
                    Toast.makeText(SignupActivity.this,
                            "Sorry! Please check your Captcha code",
                            Toast.LENGTH_LONG).show();
                } else {
                    UserRegister();
                }
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target)
                && android.util.Patterns.EMAIL_ADDRESS.matcher(target)
                .matches();
    }

    public void UserRegister() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "register.php";
        Log.e("url", url + "");
        final ProgressDialog mProgressDialog = new ProgressDialog(SignupActivity.this);
        mProgressDialog.setTitle("");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.show();
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {
                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());
                            Log.e("2", "2");

                            String msg = object.getString("msg");
                            if (msg.equalsIgnoreCase("Success")) {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Thank you! Your register process completed \n Please Login",
                                        Toast.LENGTH_LONG).show();
                                finish();
                            } else if (msg.equalsIgnoreCase("Fail")) {
                                if (object.getString("error").equalsIgnoreCase("Email or Phone No. already Exists")) {
                                    Toast.makeText(getApplicationContext(),
                                            "You have already registered",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "Oopss! Register failure please try again",
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Oopss! Register failure please try again",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception error) {
                            mProgressDialog.dismiss();
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                Toast.makeText(SignupActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(SignupActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(SignupActivity.this, "Please check your internet connection!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SignupActivity.this, "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("name", name
                        .getText().toString());
                params.put("address", "" + address.getText().toString());
                params.put("phone", "" + mobile.getText().toString());
                params.put("email", "" + email.getText().toString());
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
