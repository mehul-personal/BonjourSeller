package com.analytics.bonjourseller;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditFragment.OnEditFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ImageView userPhoto;
    TextView userName;
    EditText editName, editAddress, editEmail, editPhone, editPassword;
    private OnEditFragmentInteractionListener mListener;
    Button update;
    static String selectedImagePath = "";

    public EditFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static EditFragment newInstance(String param1, String param2) {
        EditFragment fragment = new EditFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit, container, false);
        userPhoto = (ImageView) view.findViewById(R.id.ivUserPhoto);
        userName = (TextView) view.findViewById(R.id.txvUserName);
        editName = (EditText) view.findViewById(R.id.edtEditName);
        editAddress = (EditText) view.findViewById(R.id.edtEditAddress);
        editEmail = (EditText) view.findViewById(R.id.edtEditEmailAddress);
        editPhone = (EditText) view.findViewById(R.id.edtEditPhone);
        editPassword = (EditText) view.findViewById(R.id.edtEditPassword);
        update = (Button) view.findViewById(R.id.btnUpdateProfile);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditProfile();
            }
        });

        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });
        editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                userName.setText(editName.getText().toString());
            }
        });
        getUserDetail();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEditFragmentInteractionListener) {
            mListener = (OnEditFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnEditFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void getUserDetail() {
        String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "get_user_details.php";
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

                        try {
                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());
                            Log.e("2", "2");

                            String msg = object.getString("msg");
                            if (msg.equalsIgnoreCase("Success")) {
                                JSONObject dataob = object.getJSONObject("data");
                                dataob.getString("userid");
                                userName.setText(dataob.getString("name"));
                                editName.setText(dataob.getString("name"));
                                editAddress.setText(dataob.getString("address"));
                                if (!dataob.getString("profile_image").isEmpty()) {
                                    Picasso.with(getActivity())
                                            .load(dataob.getString("profile_image"))
                                            .placeholder(R.drawable.ic_user)
                                            .into(userPhoto);
                                }
                                editPhone.setText(dataob.getString("phone"));
                                dataob.getString("city");
                                dataob.getString("password");
                                editEmail.setText(dataob.getString("email"));

                                SharedPreferences mPrefs = getActivity().getSharedPreferences(
                                        "LOGIN_DETAIL", 0);
                                SharedPreferences.Editor edit = mPrefs.edit();
                                edit.putString("NAME", dataob.getString("name"));
                                edit.putString("ADDRESS", dataob.getString("address"));
                                edit.putString("CITY", dataob.getString("city"));
                                edit.putString("PHONE", dataob.getString("phone"));
                                edit.putString("EMAIL", dataob.getString("email"));
                                edit.putString("IMAGE_URL", dataob.getString("profile_image"));
                                edit.commit();
                            } else {
                                Toast.makeText(getActivity(),
                                        "Oopss! We are troubleing to get data",
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception error) {
                            error.printStackTrace();
                            mProgressDialog.dismiss();
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
                SharedPreferences mPrefs = getActivity().getSharedPreferences(
                        "LOGIN_DETAIL", getActivity().MODE_PRIVATE);
                Map<String, String> params = new HashMap<String, String>();
                params.put("seller_id", mPrefs.getString("USER_ID", ""));
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }

    public void setEditProfile() {
        final String name = editName.getText().toString();
        final String Address = editAddress.getText().toString();
        final String Phone = editPhone.getText().toString();
        final String Password = editPassword.getText().toString();
        new AsyncTask<Void, Void, String>() {
            ProgressDialog mProgressDialog;

            @Override
            protected String doInBackground(Void... params) {
                // TODO Auto-generated method stub
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                        "LOGIN_DETAIL", 0);
                String userid = sharedPreferences.getString("USER_ID", "");
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(ApplicationData.serviceURL + "edit_profile.php");

                try {

                    MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                    entity.addPart("userid", new StringBody(userid,
                            Charset.forName("UTF-8")));
                    if (selectedImagePath != null) {
                        if (!selectedImagePath.isEmpty()) {
                            Log.e("image path", "image:" + selectedImagePath);
                            entity.addPart("image_name", new FileBody(new File(
                                    selectedImagePath)));
                        }
                    }
                    if (!name.isEmpty())
                        entity.addPart("name", new StringBody("" + name));
                    if (!Address.isEmpty())
                        entity.addPart("address", new StringBody("" + Address));
                    if (!Phone.isEmpty())
                        entity.addPart("phone", new StringBody("" + Phone));
                    if (!Password.isEmpty())
                        entity.addPart("password", new StringBody("" + Password));

                    httppost.setEntity(entity);
                    HttpResponse response = httpclient.execute(httppost);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(response.getEntity()
                                    .getContent()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";
                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                    }
                    in.close();
                    Log.e("edit profile data", sb.toString());
                    return sb.toString();

                } catch (Exception e) {
                    Log.e("edit profile problem", "" + e);
                    return "";
                }

            }

            @Override
            protected void onPostExecute(String result) {
                // TODO Auto-generated method stub
                super.onPostExecute(result);
                try {
                    mProgressDialog.dismiss();
                    JSONObject object = new JSONObject(result.toString());
                    Log.e("2", "2");

                    String msg = object.getString("msg");
                    if (msg.equalsIgnoreCase("Success")) {
                        Toast.makeText(getActivity(),
                                "Your Profile has been updated successfully!",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(),
                                "Oopss! We are troubleing to get data",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception error) {
                    error.printStackTrace();
                    mProgressDialog.dismiss();
                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        Toast.makeText(getActivity(), "Please check your internet connection!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "Something is wrong Please try again!", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            protected void onPreExecute() {
                // TODO Auto-generated method stub
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setTitle("");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.setMessage("Please Wait...");
                mProgressDialog.show();

            }
        }.execute();

       /* String tag_json_obj = "json_obj_req";

        String url = ApplicationData.serviceURL + "edit_profile.php";
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

                        try {
                            mProgressDialog.dismiss();
                            JSONObject object = new JSONObject(response.toString());
                            Log.e("2", "2");

                            String msg = object.getString("msg");
                            if (msg.equalsIgnoreCase("Success")) {
                                Toast.makeText(getActivity(),
                                        "Oopss! We are troubleing to get data",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(),
                                        "Oopss! We are troubleing to get data",
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception error) {
                            mProgressDialog.dismiss();
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
                SharedPreferences mPrefs = getActivity().getSharedPreferences(
                        "LOGIN_DETAIL", getActivity().MODE_PRIVATE);
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", mPrefs.getString("USER_ID", ""));
                if (!editName.getText().toString().isEmpty())
                    params.put("name", "" + editName.getText().toString());
                if (!editAddress.getText().toString().isEmpty())
                    params.put("address", "" + editAddress.getText().toString());
                if (!editPhone.getText().toString().isEmpty())
                    params.put("phone", "" + editPhone.getText().toString());
                if (!editPassword.getText().toString().isEmpty())
                    params.put("password", "" + editPassword.getText().toString());
                return params;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // Adding request to request queue
        ApplicationData.getInstance().addToRequestQueue(jsonObjReq,
                tag_json_obj);*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            System.out.println("Content Path : " + selectedImage.toString());
            selectedImagePath = getPath(getActivity(), selectedImage);
            Log.e("selected image", selectedImagePath + "");
            if (selectedImage != null) {
                userPhoto
                        .setImageBitmap(getScaledBitmap(selectedImage));
            } else {
                selectedImagePath = "";
                Toast.makeText(getActivity(), "Error getting Image",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(getActivity(), "No Photo Selected",
                    Toast.LENGTH_SHORT).show();
            selectedImagePath = "";
        }
    }

    public Bitmap getCircleBitmap(Bitmap bitmap) {
        Bitmap output;
        try {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                    Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(output);
            final int color = Color.RED;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());
            final RectF rectF = new RectF(rect);
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawOval(rectF, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
            // bitmap.recycle();

            return output;
        } catch (Exception e) {
            output = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_no_image);
            return output;
        }
    }

    private Bitmap getScaledBitmap(Uri uri) {
        Bitmap thumb = null, rotatedBitmap = null;
        try {
            ContentResolver cr = getActivity().getContentResolver();
            InputStream in = cr.openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            // options.inSampleSize=12;
            thumb = Bitmap.createScaledBitmap(
                    BitmapFactory.decodeStream(in, null, options), 100, 100,
                    true);
            // Matrix matrix = new Matrix();
            // matrix.postRotate(90);
            // rotatedBitmap = Bitmap.createBitmap(thumb, 0, 0,
            // thumb.getWidth(), thumb.getHeight(), matrix, true);
        } catch (FileNotFoundException e) {
            // Toast.makeText(sliderContext, "File not found",
            // Toast.LENGTH_SHORT)
            // .show();
        }
        return thumb;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (!isKitKat) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = context.getContentResolver().query(uri,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        } else if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }

                // handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }
}
